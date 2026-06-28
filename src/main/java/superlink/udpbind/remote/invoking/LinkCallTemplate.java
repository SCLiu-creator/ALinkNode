package superlink.udpbind.remote.invoking;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import superlink.linkServer.LinkProcessMap;
import superlink.linkServer.ParameterDeserializer;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.datalen.AsySteam;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class LinkCallTemplate {

    public String startUser;

    public String toUser;

    public int orginId;

    public int targetId;

    public LinkCallTemplate(String startUser,String toUser){
        this.startUser=startUser;
        this.toUser = toUser;
        UserContext userContext = UDPclient.getUser(toUser);
        this.orginId =userContext.newQueue();
        this.targetId =userContext.newQueue();
    }

    public LinkCallTemplate(String startUser, String toUser,int readerId){
        this.startUser=startUser;
        this.toUser = toUser;
        UserContext userContext = UDPclient.getUser(toUser);
        this.orginId =userContext.newQueue(readerId);
        this.targetId =userContext.newQueue();
    }
    public LinkCallTemplate(String startUser, int readerId, int writerId){
        this.startUser=startUser;
        this.orginId =readerId;
        this.targetId =writerId;
    }

    public int sort=0;
    
    public String uri;

    public Object data;

    public String para="";
    public ArrayList dataList;
    
    public Object[] objects;

    public Object RI(Object... para){
        Object obj = null;

        LinkProcessMap.Linkbean linkbean=LinkProcessMap.map.get(uri);

        JSONObject[] jsonObjects= (JSONObject[]) para;
        Object[] objects=new Object[jsonObjects.length];
        Type[] classes=linkbean.getClasses();
        for (int i = 0; i < jsonObjects.length; i++) {
            try {
                objects[i]=JSON.parseObject(jsonObjects[i].toJSONString(),classes[i]);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        obj=  linkbean.re(para);

        return obj;
    }

    public Object RIp(String s){
        Object obj = null;

        LinkProcessMap.Linkbean linkbean=LinkProcessMap.map.get(uri);

        Type[] types=linkbean.getTypess();
        Class[] classes=linkbean.getClasses();
        Object[] objects= new Object[0];
        try {
            objects = ParameterDeserializer.deserializeParameters(classes,types,new String[]{s} );
        } catch (Exception e) {
            e.printStackTrace();
        }

        obj=  linkbean.re(objects);

        return obj;
    }



    public byte[] req(String uri){
        this.uri=uri;
        UserContext userContext=UDPclient.getUser(toUser);
        userContext.stableSend(("lc"+JSON.toJSONString(this)).getBytes());
        if (para.length()>500|| data!=null){
            AsySteam asySteam = new AsySteam(toUser, (short) targetId);
            asySteam.readyTimes=20;
            asySteam.getWrite();
            if(para.length()>500){
                byte[] bytes =para.getBytes();
                asySteam.writeBytes(bytes);
            }
            if(data!=null){
                if(data instanceof File){
                    asySteam.writeFile((File) data);
                }else if(data instanceof InputStream){
                    asySteam.writeInstream((InputStream) data);
                }else if(data instanceof byte[]){
                    asySteam.writeBytes((byte[]) data);
                }
            }
            asySteam.clear();
        }
        AsySteam asySteamr =AsySteam.getSteam(toUser, (short) orginId);
        asySteamr.reqData(null);
        ByteBuffer byteBuffer=ByteBuffer.allocate(1024*1024*10);
        byte[] bytes=new byte[1024];
        int len;
        while (true) {
            len = asySteamr.read(bytes);
            if (len > 0) {
                try {
                    byteBuffer.put(bytes, 0, len);
                } catch (Exception i) {
                    i.printStackTrace();
                }
            }
            if (len == 0||len == -1) {
                break;
            }
        }
        asySteamr.clear();
        byteBuffer.flip();
        byte[] data = new byte[byteBuffer.remaining()];
        byteBuffer.get(data);
        return data;

    }
    public static void main(String[] args) {
        String jsonString = "{\"objects\":[{\"type\": \"dog\", \"name\": \"Rex\", \"breed\": \"German Shepherd\"}, {\"type\": \"person\", \"name\": \"Alice\", \"age\": 30}]}";
        LinkCallTemplate invokeTemplate= JSON.parseObject(jsonString, LinkCallTemplate.class);

    }
}
