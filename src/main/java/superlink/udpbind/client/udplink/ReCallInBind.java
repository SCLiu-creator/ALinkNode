package superlink.udpbind.client.udplink;

import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.recives.Senders;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Objects;

public class ReCallInBind extends ReCallBind{

    public int time=0;

    public static ReCallBind ReCallBindFactory(InetAddress inetAddress, int port,byte[] data,String usernaem){
        ReCallBind reCallBind= traversalMap.get(usernaem);
        if (reCallBind==null){
            reCallBind=new ReCallInBind(inetAddress,port,data,usernaem);
            traversalMap.put(usernaem,reCallBind);
        }
        return reCallBind;
    }

    public ReCallInBind(InetAddress inetAddress, int port, byte[] data, String usernaem){
        super(inetAddress,port,data,usernaem);
        this.usernaem=usernaem;
    }

//    @Override
    public Object call() throws Exception {
        if (this.time >= 8) {
            traversalMap.remove(this.usernaem);
        }

        if (this.time % 2 == 0) {
            Senders.Sends(inetAddress,port,data);
        }

        ++this.time;
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
