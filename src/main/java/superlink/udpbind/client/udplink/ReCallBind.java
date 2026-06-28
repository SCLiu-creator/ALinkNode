package superlink.udpbind.client.udplink;

import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.MainDataQueue;
import superlink.udpbind.client.recives.Senders;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

public class ReCallBind implements Callable {

    public static ConcurrentHashMap<String,ReCallBind> traversalMap =new ConcurrentHashMap<>();
    String usernaem;
    public int time=0;
    public InetAddress inetAddress;
    public int port;
    public byte[] data;

    public static ReCallBind ReCallBindFactory(InetAddress inetAddress, int port,byte[] data, String usernaem){
        ReCallBind reCallBind=new ReCallBind(inetAddress,port,data,usernaem);
        traversalMap.put(usernaem,reCallBind);
        return reCallBind;
    }
//    public static ReCallBind ReCallBindFactory(DatagramPacket datagramPacket,String usernaem){
//        ReCallBind reCallBind=new ReCallBind(datagramPacket,usernaem);
//        traversalMap.put(usernaem,reCallBind);
//        return reCallBind;
//    }

    public  ReCallBind(InetAddress inetAddress, int port,byte[] data,String usernaem){
        this.data=data;
        this.inetAddress=inetAddress;
        this.port=port;
        this.usernaem=usernaem;
    }

    @Override
    public Object call() throws Exception {
        if (time>=7){
            traversalMap.remove(this.usernaem);
            UDPclient.mainDataQueue.removeUserBuf(this.usernaem);
        }
        time++;

        Senders.Sends(inetAddress,port,data);
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReCallBind that = (ReCallBind) o;
        return Objects.equals(usernaem, that.usernaem);
    }

    @Override
    public int hashCode() {
//        Objects.hash(usernaem, time, datagramPacket);
        return usernaem.hashCode();
    }
}
