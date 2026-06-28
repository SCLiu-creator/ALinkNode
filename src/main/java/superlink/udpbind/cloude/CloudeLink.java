package superlink.udpbind.cloude;

import com.alibaba.fastjson2.JSON;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.Senders;
import superlink.udpbind.dataLink.UdpData;
import superlink.udpbind.dataqueue.DataQueue;
import superlink.udpbind.dataqueue.ReciveQueueFactory;
import superlink.udpbind.usedata.DataRequest;
import superlink.util.Utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class CloudeLink {
    UserContext userContext=null;
    int id=0;
    BlockingQueue<byte[]> blockingQueue=null;
    UdpData udpData;
    public static Map<String,CloudeLink> linkMap;
    public CloudeLink(String username) throws Exception {
        this.userContext= UDPclient.mainDataQueue.getUserContext(username);
        linkMap.put(username,this);
    }
    public boolean createlink(){
        id=userContext.newQueue();
        byte[] bytes= Utils.getUseridByte(userContext.getBothId(), (short) 0);
        DataRequest dataRequest=new DataRequest();
        dataRequest.id=id;
        dataRequest.requestname=UDPclient.userlocal.username;
        String s="CL"+ JSON.toJSONString(dataRequest);
        bytes=Utils.byteMerger(bytes,s.getBytes());

        Senders.Sends(userContext.inetAddress,userContext.port,bytes);
        int i=0;
        while (i>2){
            String re="";
            try {
                byte[] b=blockingQueue.poll(5, TimeUnit.SECONDS);
                re=new String(b);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (re.equals("LC")){
                return true;
            }else{
                i++;
                Senders.Sends(userContext.inetAddress,userContext.port,bytes);
            }
        }
        return false;

    }

    public boolean createlinks(){
        DataQueue dataQueue= ReciveQueueFactory.ReciveData.get(userContext.userName);
        int i=dataQueue.newId();
        byte[] bytes=("CL"+i).getBytes();
        bytes=Utils.byteMerger(new byte[]{0},bytes);
        DatagramPacket datagramPacket=new DatagramPacket(bytes,bytes.length,userContext.inetAddress,userContext.port);
        try {
            dataQueue.udpData.dataSocket.send(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int ii=0;
        while (ii>2){
            String re="";
            try {
                byte[] b=blockingQueue.poll(5, TimeUnit.SECONDS);
                re=new String(b);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (re.equals("LC")){
                linkMap.put(userContext.userName,this);
                return true;
            }else{
                ii++;
                try {
                    dataQueue.udpData.dataSocket.send(datagramPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;

    }

}
