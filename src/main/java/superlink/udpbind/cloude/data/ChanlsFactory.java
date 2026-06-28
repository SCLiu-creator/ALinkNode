package superlink.udpbind.cloude.data;

import com.alibaba.fastjson2.JSON;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.ByteBufer;
import superlink.util.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ChanlsFactory {
    public static Map<String, CloudeChanel> ChanlsMap=new HashMap<>();

    public static CloudeChanel getCL(String name) throws Exception {
        UserContext userContext= UDPclient.mainDataQueue.getUserContext(name);
        if (ChanlsMap.get(name)!=null){
            return ChanlsMap.get(name);
        }else {
            ID i=new ID();
            i.my=userContext.newQueue();
            i.my1=userContext.newQueue();
            String s="CF"+ JSON.toJSONString(i);
            ByteBufer blockingQueue=userContext.getQueue((short)i.my);
            int j=0;
            byte[] bytes=null;
            while (true){
                if (j>4){
                        //todo 信道创建失败
                    throw new Exception();
//                    return null;
                }
                Utils.dealsSend(name,s.getBytes());
                try {
                    bytes=blockingQueue.poll(2, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (bytes==null){
                    continue;
                }else {
                    break;
                }
            }
            i=JSON.parseObject(bytes,ChanlsFactory.ID.class);
            CloudeChanel cloudeChanel=new CloudeChanel();
            cloudeChanel.build(userContext,i);
            ChanlsMap.put(name,cloudeChanel);
            return cloudeChanel;
        }
    }

    public static class ID{
        public int my;
        public int you;
        public int my1;
        public int you1;
        public ID change(){
            int c=my;
            my=you;
            you=c;
            return this;
        }
        public ID change1(){
            int c=my1;
            my1=you1;
            you1=c;
            return this;
        }
    }
}
