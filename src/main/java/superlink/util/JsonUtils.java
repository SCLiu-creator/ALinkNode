package superlink.util;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {

    public static String getJsonString(Object o){
        return JackJson.toJson(o);
    }
//    public static String getJson(Object o){
//        return JackJson.toJson(o);
//    }

    public static Object JsontoObject(String jsonString,Class cla){
        return JackJson.toObject(jsonString,cla);
    }
}
