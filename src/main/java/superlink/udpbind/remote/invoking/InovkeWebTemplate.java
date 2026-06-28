package superlink.udpbind.remote.invoking;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import superlink.filemanage.classprocess.AutoScan;
import superlink.httpserver.servelt.ProcessMapL;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.datalen.AutoBuffer;
import superlink.udpbind.client.recives.datalen.dataCache.BufferDataCon;
import superlink.util.JackJson;
import superlink.util.JsonUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;

public class InovkeWebTemplate {

    public int sort=0;

    public String webUrl;

    public Object[] objects;

    private int pa=0;

    public void setObjects(Object[]... objects) {
        String para= JsonUtils.getJsonString(objects);
        byte[] bytes=para.getBytes();
        if (bytes.length>1000){
            if (bytes.length> AutoBuffer.I/2){
                pa=2;
                //todo
            }else {
                pa=1;
                UUID uuid=UUID.randomUUID();
                BufferDataCon.setData(uuid.toString(),bytes);
                this.objects=new Object[]{uuid.toString()};
            }
        }else {
            this.objects = objects;
        }
    }

    public Object RI(UserContext userContext){
        Object obj;
        if (pa==1){
            AutoBuffer autoBuffer=new AutoBuffer(userContext.userName);
            byte[] bytes=autoBuffer.reqData((String) objects[0]);
            this.objects = (Object[]) JackJson.toObject(new String(bytes),Object[].class);
        }
        ProcessMapL.Nettybean nettybean=null;

        nettybean=ProcessMapL.map.get(webUrl);
        if (nettybean==null){
            return null;
        }
        obj=  nettybean.rre(this.objects);
        return obj;
    }

    public static class controllerTemp{
        public String uri;

    }
    public static class reAssemableTemp{
        public String beanName;
        public String method;
    }

    public static void main(String[] args) {
        String jsonString = "{\"objects\":[{\"type\": \"dog\", \"name\": \"Rex\", \"breed\": \"German Shepherd\"}, {\"type\": \"person\", \"name\": \"Alice\", \"age\": 30}]}";
        InvokeTemplate invokeTemplate= JSON.parseObject(jsonString,InvokeTemplate.class);

    }
}
