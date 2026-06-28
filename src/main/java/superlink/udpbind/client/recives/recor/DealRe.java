package superlink.udpbind.client.recives.recor;

import com.alibaba.fastjson2.JSON;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.Senders;
import superlink.udpbind.client.recives.datalen.AutoBuffer;
import superlink.udpbind.client.recives.datalen.dataCache.BufferDataCon;
import superlink.udpbind.remote.invoking.InvokeTemplate;
import superlink.udpbind.remote.invoking.RemoteWorkContrains;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import static superlink.udpbind.client.UDPclient.mainDataQueue;

public class DealRe implements DealsRun {

    Deals deals;
    public List <Callable> list=new ArrayList(0);
    public DealRe(Deals deals){
        this.deals=deals;
    }


    @Override
    public boolean run(byte[] bytes) {
        if (bytes.length<8){
            return true;
        }
        if ((bytes[6]&bytes[7])==0){//回传
            switch (bytes[8]) {
                case 1:{
                    UserContext userContext = mainDataQueue.getUserContext(deals.username);;
                    break;
                }
                case 2: {

                    break;
                }
                default:{
                    break;
                }
            }
        }else {
            switch (bytes[8]) {
                case 1: {
                    UserContext userContext=mainDataQueue.getUserContext(deals.username);
                    byte[] bytes1= Arrays.copyOfRange(bytes,0,bytes.length);
                    bytes1[1]=0;
                    Senders.Sends(userContext.getBothId(),0,userContext.inetAddress,userContext.port,bytes1);
                    RemoteWorkContrains.threadPool.execute(()->{
                        AutoBuffer autoBuffer=new AutoBuffer(deals.username);
                        String hash=new String(bytes,3,bytes.length-3);
                        Object o=autoBuffer.reqData(hash);
                        byte[] bytesdata=BufferDataCon.toData(o);
                        autoBuffer.clear();
                        InvokeTemplate invokeTemplate= JSON.parseObject(new String(bytesdata),InvokeTemplate.class);

                        Object obj=invokeTemplate.RI(invokeTemplate.objects);
                        BufferDataCon.setData(hash,JSON.toJSONString(obj).getBytes(),1);
                    });
                    return false;
                } default:{
                    for (Callable call:list){
                        try {
                            call.call();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        }

       return false;
    }

    public synchronized void addCall(Callable callable){
        list.add(callable);
    }
    public synchronized void delCall(Callable callable){
        list.remove(callable);
    }
    public synchronized void clearCall(){
        list.clear();
    }

//    @FunctionalInterface
//    public interface reCall{
//
//        public void run();
//
//
//
//    }
}
