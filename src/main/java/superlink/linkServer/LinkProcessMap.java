package superlink.linkServer;

import com.alibaba.fastjson2.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
//import com.fasterxml.jackson.databind.ObjectMapper;
import superlink.httpserver.ProcessMap;
import superlink.httpserver.servelt.action.GetParm;
import superlink.httpserver.servelt.action.WebPath;
import superlink.udpbind.usedata.User;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.net.URLDecoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinkProcessMap {
    public static Map<String, Map<String, LinkProcessMap.Linkbean>> mapMap=new HashMap<>();
    public static Map<String, Linkbean> map = new HashMap<>();
    public static List<Linkbean> list = new ArrayList<>();
//    public static List<NettyIntercepter> interceptorList=new ArrayList<>();
//    public static Map<String, NettyDeal> dealMap=new HashMap<>();

    public static class Linkbean<V> {
        Object action;
        Method method;
        Type[] typess;
        Class<?>[] classes;
        Type returnType;
        public String murl;

        @Override
        public String toString() {
            return "{" +
                    "method:" + method +
                    ", classes:" + Arrays.toString(classes) +
                    ", murl:'" + murl + '\'' +
                    '}';
        }

        public Object getAction() {
            return action;
        }

        public Linkbean setAction(Object action) {
            this.action = action;
            return this;
        }

        public Method getMethod() {
            return method;
        }

        public Linkbean setMethod(Method method) {
            this.method = method;
            classes = method.getParameterTypes();
            typess = method.getGenericParameterTypes();
            return this;
        }

        public Class[] getClasses() {
            return classes;
        }
        public Type[] getTypess() {
            return typess;
        }

        public Type getReturnType() {
            return returnType;
        }

        public Linkbean setReturnType(Type returnType) {
            this.returnType = returnType;
            return this;
        }

        public Linkbean setmurl(String murl) {
            this.murl = murl;
            return this;
        }

        public V re(Object... objects) {
            try {
                return (V) method.invoke(action, objects);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } catch (Error e) {
                e.printStackTrace();
            }
            return null;
        }

        public V rre(Object... objects) {
            Object[] objectss = new Object[classes.length];
            int i = 0;
            for (Type c : classes) {
                String s = (String) objects[i];
                if (c instanceof ParameterizedType) {
                    objectss[i] = prasePram(s, c);
                } else {
                    objectss[i] = praseValue(s, c);
                }
                i++;
            }
            try {
                return (V) method.invoke(action, objectss);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } catch (Error e) {
                e.printStackTrace();
            }
            return null;
        }
        public V ire(Object... objects) {
            Object[] objectss = new Object[classes.length];
            int i = 0;
            for (Type c : classes) {
                if (objects[i].getClass().equals(String.class)){
                    String s = (String) objects[i];
                    if (c instanceof ParameterizedType) {
                        objectss[i] = prasePram(s, c);
                    } else {
                        objectss[i] = praseValue(s, c);
                    }
                }else {
                    objectss[i] = objects[i];
                }
                i++;
            }
            try {
                return (V) method.invoke(action, objectss);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } catch (Error e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    public static class LinkIntercepter<V> {
        Object intercepter;
        Method method;
        Type[] classes;
        Type returnclass;

        public Object getIntercepter() {
            return intercepter;
        }

        public LinkProcessMap.LinkIntercepter setIntercepter(Object intercepter) {
            this.intercepter = intercepter;
            return this;
        }

        public Method getMethod() {
            return method;
        }

        public LinkProcessMap.LinkIntercepter setMethod(Method method) {
            this.method = method;
            classes=method.getGenericParameterTypes();
            return this;
        }

        public Type getReturnclass() {
            return returnclass;
        }

        public LinkProcessMap.LinkIntercepter setReturnclass(Type returnclass) {
            this.returnclass = returnclass;
            return this;
        }

        public Object re(ChannelHandlerContext ctx, FullHttpRequest msg) {
            Object o=null;
            try {
                o=method.invoke(intercepter, msg);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            return o;
        }
    }

    public static Map<Type, TypeReference> referenceMap = new HashMap<>();

    static {
        //referenceMap.put((Map<String, List<User>).getType(),new TypeReference<Map<String, List<User>>>() {});
    }

    public static Object prasePram(String s, Type type) {
        Object obj = null;
        Type[] types = ((ParameterizedType) type).getActualTypeArguments();
        if (types.length == 1) {
            //List list=JSON.parseArray(s, types);
            List list = (List) JSON.parse(s);
            if (types[0] instanceof ParameterizedType) {
                Type[] types1 = ((ParameterizedType) types[0]).getActualTypeArguments();
                if (types1.length == 1) {
                    List list1 = new ArrayList();
                    list.forEach(l -> {
                        list1.add(JSON.parseArray(((JSONObject) l).toJSONString(), types1));
                    });
                    list = list1;
                } else if (types1.length == 2) {
                    List list1 = new ArrayList();
                    list.forEach(l -> {
                        Map map = (Map) JSON.parse(((JSONObject) l).toJSONString());
                        map.forEach((k, v) -> {
                            Class ct = v.getClass();
                            Constructor[] constructors = ct.getConstructors();
                            if (v instanceof JSONArray) {
                                JSONArray jsonArray = (JSONArray) v;
                                Object tb = JSON.parseObject(jsonArray.toJSONString(), types1[1]);
                                map.put(k, tb);
                            } else if (v instanceof JSONObject) {
                                JSONObject jsonObject = (JSONObject) v;
                                Object tb = JSON.parseObject(jsonObject.toJSONString(), types1[1]);
                                map.put(k, tb);
                            }
                        });
                        list1.add(map);
                    });
                    list = list1;
                } else {
                    if (list.get(0) instanceof JSONObject) {
                        List list1 = new ArrayList();
                        list.forEach(l -> {
                            list1.add(JSON.parseObject(((JSONObject) l).toJSONString(), types[0]));
                        });
                        list = list1;
                    }
                }
            }
            obj = list;
        } else if (types.length == 2) {
            Map map = JSON.parseObject(s, new TypeReference<Map<Object, Object>>() {});
            if (types[1] instanceof ParameterizedType) {
                Type[] types1 = ((ParameterizedType) types[1]).getActualTypeArguments();
                if (types1.length == 1) {
                    Iterator iterator = map.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<Object, Object> entry = (Map.Entry<Object, Object>) iterator.next();
                        Object key = entry.getKey();
                        Object value = entry.getValue();
                        List list1 = new ArrayList();
                        try {
                            if (value instanceof String) {
                                list1 = JSON.parseArray((String) value, types1);
                            } else {
                                list1.add(JSON.parseArray(((JSONObject) value).toJSONString(), types1));
                            }
                            map.put(key, list1);
                        } catch (Exception e) {
                            e.getMessage();
                        }

                    }

                } else if (types1.length == 2) {
                    Iterator iterator = map.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<Object, Object> entry = (Map.Entry<Object, Object>) iterator.next();
                        Object key = entry.getKey();
                        Object value = entry.getValue();
                        Map map2 = (Map) JSON.parse(((JSONObject) value).toJSONString());
                        map.forEach((k, v) -> {
                            if (v instanceof JSONArray) {
                                JSONArray jsonArray = (JSONArray) v;
                                Object tb = JSON.parseObject(jsonArray.toJSONString(), types1[1]);
                                map2.put(k, tb);
                            } else if (v instanceof JSONObject) {
                                JSONObject jsonObject = (JSONObject) v;
                                Object tb = JSON.parseObject(jsonObject.toJSONString(), types1[1]);
                                map2.put(k, tb);
                            }
                        });
                        map.put(key, map2);

                    }
                    ;
                }
            }
            map.forEach((k, v) -> {
                Type type1 = types[1];
                if (!(type1.equals(String.class))) {
                    if (v instanceof JSONObject) {
                        JSONObject jsonObject = (JSONObject) v;
                        Object tb = JSON.parseObject(jsonObject.toJSONString(), types[1]);
                        map.put(k, tb);
                    } else if (v instanceof JSONArray) {
                        JSONArray jsonArray = (JSONArray) v;
                        Object tb = JSON.parseObject(jsonArray.toJSONString(), types[1]);
                        map.put(k, tb);
                    } else if (v instanceof String) {
                        try {//todo
                            Object parsed = JSON.parse((String) v);
                            if (parsed instanceof JSONObject || parsed instanceof JSONArray) {
                                map.put(k, parsed);
                            }
                        } catch (JSONException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    if (k instanceof JSONObject) {

                    }
                }


            });
            obj = map;
        } else if (types.length == 3) {
            Map map = (Map) JSON.parse(s);
            map.forEach((k, v) -> {
                JSONObject object = (JSONObject) v;
                Object tb = JSON.parseObject(object.toJSONString(), types[1]);
                map.put(k, tb);
            });
            obj = map;
        }
        return obj;

    }

    public static Object praseValue(String s, Type type) {
        Object obj = null;
        Class a = null;
        try {
            a = Class.forName(type.getTypeName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            Class<?>[] classes = a.getInterfaces();
            for (Class cla : classes) {
                if (cla.equals(Comparable.class)) {
                    if (a.equals(String.class)) {
                        obj = s;
                    } else {
                        if ("".equals(s)) {
                            s = "0";
                        }
                        Method method = a.getMethod("valueOf", String.class);
                        Object o = method.invoke(null, s);
                        obj = o;
                    }
                    break;
                }
            }
            if (obj == null) {
                Object o = JSON.parseObject(s, a);
                obj = o;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;

    }

    public <T> T praseGetMap(Object o, Type type) {
        Map<String, List<User>> map = JSON.parseObject((String) o, new TypeReference<Map<String, List<User>>>() {
        });
        // 打印结果以验证
        for (Map.Entry<String, List<User>> entry : map.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
            for (User user : entry.getValue()) {
                System.out.println(user);
            }
        }
        return (T) map;
    }

    public <T> T prase(String o, Class type) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?>[] classes = type.getInterfaces();
        Object v = null;
        for (Class cla : classes) {
            if (cla.equals(Comparable.class)) {
                if (o.equals(String.class)) {
                    v = o;
                } else {
                    if ("".equals(o)) {
                        v = "0";
                    }
                    Method method = type.getMethod("valueOf", String.class);
                    Object obj = method.invoke(null, o);
                    v = obj;
//                            objectss[i]=Integer.valueOf(s);
                }
                break;
            }
        }
        return (T) v;
    }

    /**
     * 将模板字符串（如 "a{val}c{val2}f"）转换成正则表达式。
     *
     * @param template 模板字符串
     * @return 转换后的正则表达式字符串
     */
    public static String convertToRegex(String template) {
        // 将模板中的 "{val}" 和类似的表达式替换为 "(.+?)"
        // 注意：这里假设模板中的 "{...}" 仅用于表示需要匹配的部分，并且没有嵌套或特殊字符需要转义
        String regex = template.replaceAll("\\{[^}]*\\}", "(.+?)");
        return regex;
    }

    public static List<String> matcher(String text, String template) {
        // 将模板中的 "{val}" 和类似的表达式替换为 "(.+?)"
        // 注意：这里假设模板中的 "{...}" 仅用于表示需要匹配的部分，并且没有嵌套或特殊字符需要转义
//        String text = "abcdef";
        Pattern pattern = Pattern.compile(template);
        Matcher matcher = pattern.matcher(text);
        List list = new ArrayList();
        if (matcher.find()) {
            String val = null;
            int i = 1;
            try {
                do {
                    val = matcher.group(i);
                    i++;
                    list.add(val);
                } while (val != null);

            } catch (Exception e) {
                e.getMessage();
            }
//            String val2 = matcher.group(2); // "de"
//            System.out.println("val: " + val + ", val2: " + val2);
        }
        return list;
    }

}