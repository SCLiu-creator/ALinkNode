package superlink.linkServer;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ParameterDeserializer {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 根据方法参数类型将字符串数组反序列化为参数对象数组
     * @param method 目标方法
     * @param paramStrings 参数字符串数组
     * @return 反序列化后的参数对象数组
     * @throws Exception 如果反序列化失败
     */
    public static Object[] deserializeParameters(Method method, String[] paramStrings) throws Exception {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Type[] genericParameterTypes = method.getGenericParameterTypes();

        if (parameterTypes.length != paramStrings.length) {
            throw new IllegalArgumentException("参数数量不匹配");
        }

        Object[] params = new Object[parameterTypes.length];

        for (int i = 0; i < parameterTypes.length; i++) {
            params[i] = deserializeString(paramStrings[i], parameterTypes[i], genericParameterTypes[i]);
        }

        return params;
    }

    public static Object[] deserializeParameters(Class<?>[] parameterTypes, Type[] genericParameterTypes,String[] paramStrings) throws Exception {
        if (parameterTypes.length != paramStrings.length) {
            throw new IllegalArgumentException("参数数量不匹配");
        }

        Object[] params = new Object[parameterTypes.length];

        for (int i = 0; i < parameterTypes.length; i++) {
            params[i] = deserializeString(paramStrings[i], parameterTypes[i], genericParameterTypes[i]);
        }

        return params;
    }

    /**
     * 将单个字符串反序列化为指定类型的对象
     * @param value 字符串值
     * @param paramType 参数类型
     * @param genericType 泛型类型信息
     * @return 反序列化后的对象
     * @throws Exception 如果反序列化失败
     */
    private static Object deserializeString(String value, Class<?> paramType, Type genericType) throws Exception {
        if (value == null || value.equals("null")) {
            return null;
        }

        // 处理基本类型和包装类型
        if (paramType == String.class) {
            return value;
        } else if (paramType == int.class || paramType == Integer.class) {
            return Integer.parseInt(value);
        } else if (paramType == long.class || paramType == Long.class) {
            return Long.parseLong(value);
        } else if (paramType == double.class || paramType == Double.class) {
            return Double.parseDouble(value);
        } else if (paramType == float.class || paramType == Float.class) {
            return Float.parseFloat(value);
        } else if (paramType == boolean.class || paramType == Boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (paramType == short.class || paramType == Short.class) {
            return Short.parseShort(value);
        } else if (paramType == byte.class || paramType == Byte.class) {
            return Byte.parseByte(value);
        } else if (paramType == char.class || paramType == Character.class) {
            if (value.length() != 1) {
                throw new IllegalArgumentException("字符参数长度必须为1");
            }
            return value.charAt(0);
        }
        // 处理数组类型
        else if (paramType.isArray()) {
            return deserializeArray(value, paramType.getComponentType());
        }
        // 处理集合类型
        else if (Collection.class.isAssignableFrom(paramType)) {
            return deserializeCollection(value, paramType, genericType);
        }
        // 处理Map类型
        else if (Map.class.isAssignableFrom(paramType)) {
            return deserializeMap(value, paramType, genericType);
        }
        // 处理其他对象类型（包括自定义类型）
        else {
            return objectMapper.readValue(value, paramType);
        }
    }

    /**
     * 反序列化数组
     */
    private static Object deserializeArray(String value, Class<?> componentType) throws Exception {
        // 简单处理：假设输入是JSON数组格式
        if (value.startsWith("[") && value.endsWith("]")) {
            // 使用Jackson处理复杂数组
            return objectMapper.readValue(value, objectMapper.getTypeFactory().constructArrayType(componentType));
        }

        // 简单处理：逗号分隔的值
        String[] parts = value.split(",");
        Object array = Array.newInstance(componentType, parts.length);

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i].trim();
            Array.set(array, i, deserializeString(part, componentType, componentType));
        }

        return array;
    }

    /**
     * 反序列化集合
     */
    private static Collection<?> deserializeCollection(String value, Class<?> collectionType, Type genericType) throws Exception {
        // 处理泛型类型信息
        if (genericType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericType;
            Type[] actualTypeArguments = pt.getActualTypeArguments();
            if (actualTypeArguments.length > 0) {
                Type elementType = actualTypeArguments[0];

                // 简单处理：假设输入是JSON数组格式
                if (value.startsWith("[") && value.endsWith("]")) {
                    JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionType,
                            objectMapper.getTypeFactory().constructType(elementType));
                    return objectMapper.readValue(value, javaType);
                }

                // 简单处理：逗号分隔的值
                String[] parts = value.split(",");
                Collection<Object> collection = createCollectionInstance(collectionType);

                for (String part : parts) {
                    part = part.trim();
                    collection.add(deserializeString(part, getTypeFromType(elementType), elementType));
                }

                return collection;
            }
        }

        // 如果没有泛型信息，默认作为字符串集合处理
        if (value.startsWith("[") && value.endsWith("]")) {
            return (Collection<?>) objectMapper.readValue(value, collectionType);
        }

        Collection<Object> collection = createCollectionInstance(collectionType);
        Collections.addAll(collection, value.split(","));
        return collection;
    }

    /**
     * 反序列化Map
     */
    private static Map<?, ?> deserializeMap(String value, Class<?> mapType, Type genericType) throws Exception {
        // 处理泛型类型信息
        if (genericType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericType;
            Type[] actualTypeArguments = pt.getActualTypeArguments();
            if (actualTypeArguments.length == 2) {
                Type keyType = actualTypeArguments[0];
                Type valueType = actualTypeArguments[1];

                // 简单处理：假设输入是JSON对象格式
                if (value.startsWith("{") && value.endsWith("}")) {
                    JavaType javaType = objectMapper.getTypeFactory().constructParametricType(mapType,
                            objectMapper.getTypeFactory().constructType(keyType),
                            objectMapper.getTypeFactory().constructType(valueType));
                    return objectMapper.readValue(value, javaType);
                }

                // 简单处理：key=value,key=value格式
                Map<Object, Object> map = createMapInstance(mapType);
                String[] pairs = value.split(",");

                for (String pair : pairs) {
                    String[] kv = pair.split("=");
                    if (kv.length == 2) {
                        String keyStr = kv[0].trim();
                        String valueStr = kv[1].trim();
                        Object key = deserializeString(keyStr, getTypeFromType(keyType), keyType);
                        Object val = deserializeString(valueStr, getTypeFromType(valueType), valueType);
                        map.put(key, val);
                    }
                }

                return map;
            }
        }

        // 如果没有泛型信息，默认作为字符串Map处理
        if (value.startsWith("{") && value.endsWith("}")) {
            return (Map<?, ?>) objectMapper.readValue(value, mapType);
        }

        // 简单处理：key=value,key=value格式
        Map<Object, Object> map = createMapInstance(mapType);
        String[] pairs = value.split(",");

        for (String pair : pairs) {
            String[] kv = pair.split("=");
            if (kv.length == 2) {
                map.put(kv[0].trim(), kv[1].trim());
            }
        }

        return map;
    }

    /**
     * 创建集合实例
     */
    private static Collection<Object> createCollectionInstance(Class<?> collectionType) {
        if (collectionType.isInterface()) {
            if (List.class.isAssignableFrom(collectionType)) {
                return new ArrayList<>();
            } else if (Set.class.isAssignableFrom(collectionType)) {
                return new HashSet<>();
            } else if (Queue.class.isAssignableFrom(collectionType)) {
                return new LinkedList<>();
            } else {
                return new ArrayList<>(); // 默认返回ArrayList
            }
        }

        try {
            return (Collection<Object>) collectionType.newInstance();
        } catch (Exception e) {
            return new ArrayList<>(); // 默认返回ArrayList
        }
    }

    /**
     * 创建Map实例
     */
    private static Map<Object, Object> createMapInstance(Class<?> mapType) {
        if (mapType.isInterface()) {
            if (SortedMap.class.isAssignableFrom(mapType)) {
                return new TreeMap<>();
            } else if (ConcurrentMap.class.isAssignableFrom(mapType)) {
                return new ConcurrentHashMap<>();
            } else {
                return new HashMap<>(); // 默认返回HashMap
            }
        }

        try {
            return (Map<Object, Object>) mapType.newInstance();
        } catch (Exception e) {
            return new HashMap<>(); // 默认返回HashMap
        }
    }

    /**
     * 从Type获取Class对象
     */
    private static Class<?> getTypeFromType(Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            return getTypeFromType(((ParameterizedType) type).getRawType());
        } else if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) type).getGenericComponentType();
            Class<?> componentClass = getTypeFromType(componentType);
            return Array.newInstance(componentClass, 0).getClass();
        } else if (type instanceof TypeVariable) {
            return Object.class; // 类型变量，默认返回Object
        } else if (type instanceof WildcardType) {
            Type[] upperBounds = ((WildcardType) type).getUpperBounds();
            return upperBounds.length > 0 ? getTypeFromType(upperBounds[0]) : Object.class;
        }
        return Object.class;
    }
}
