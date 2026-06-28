package superlink.util;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.StringWriter;
import java.util.Map;

public class JackJson {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    public static String toJson(Object obj){
        try {
            String jsonString = objectMapper.writeValueAsString(obj);
            return jsonString;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    private static final JsonFactory jsonFactory = new JsonFactory();

    public static String toJsonfast(Object obj) {
        try (StringWriter writer = new StringWriter();
             JsonGenerator generator = jsonFactory.createGenerator(writer)) {
            // 手动构建 JSON（假设 obj 是 Map）
            generator.writeStartObject();
            if (obj instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) obj;
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    generator.writeFieldName(entry.getKey().toString());
                    generator.writeObject(entry.getValue());
                }
            }
            generator.writeEndObject();
            return writer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    public static Object toObject(String jsonString,Class cla){
        ObjectMapper objectMapper = new ObjectMapper();
        Object obj = null;
        try {
             obj= objectMapper.readValue(jsonString, cla);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }
    public static JsonNode toObject(String jsonString){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode obj=null;
        try {
            obj = objectMapper.readTree(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }
    public static Object toNode(String jsonString){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode obj = objectMapper.readTree(jsonString);
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static Map toMap(String jsonString){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map obj = objectMapper.readValue(jsonString,new TypeReference<Map<String, Object>>() {});
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        byte[] bytes="".getBytes();
        String s=toJson("");
        Map m=toMap(s);
        System.out.println(bytes);
    }

}
