package superlink.udpbind.servlet;

import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.recives.Senders;
import superlink.udpbind.client.udplink.ReCallBind;
import superlink.udpbind.handle.Handler;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class LiveReturnServer extends ReCallBind {
    public static InetAddress inetAddress;
    public static int port;
    static {
        try {
            inetAddress = InetAddress.getByName("122.51.51.35");//"122.51.51.35""127.0.0.1"
            port=8090;
//            throw new UnknownHostException();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public static boolean liveServer = false;
    boolean b = true;
    int i=0;
    public LiveReturnServer( String s,InetAddress inetAddress,int port) {
        super(UDPclient.serverip, UDPclient.serverport,new byte[0], s);
        Handler.DispectMap.put("LiveNetServer", this);
        liveServer = true;
        ReCallBind.traversalMap.put(s, this);
    }

    public Object call() {
        if (this.b) {
            Senders.Sends(inetAddress,port,data);
        }
        this.b = !this.b;
        return null;
    }
}
