package superlink.util.datastack;

import com.alibaba.fastjson2.JSON;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Data {
    public static Map<String,byte[]> userpages=new ConcurrentHashMap<>();
    public static Map<String,Map<String,String>> cloudpages=new ConcurrentHashMap<>();

    public static String getCloudpage(String username){
        Map<String,String> map=cloudpages.get(username);
        return JSON.toJSONString(map);
    }


}
