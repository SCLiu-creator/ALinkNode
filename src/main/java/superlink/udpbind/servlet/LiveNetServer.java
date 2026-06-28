//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package superlink.udpbind.servlet;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.recives.Senders;
import superlink.udpbind.client.udplink.ReCallBind;
import superlink.udpbind.handle.Handler;

public class LiveNetServer extends ReCallBind {
    public static boolean liveServer = false;
    boolean b = true;

    public LiveNetServer(String s, InetAddress inetAddress,int port) {
        super(inetAddress, port,new byte[]{76,76}, s);
        Handler.DispectMap.put("LiveNetServer", this);
        liveServer = true;
        ReCallBind.traversalMap.put(s, this);
    }

    @Override
    public Object call() {
        if (this.b) {
            Senders.Sends(inetAddress,port,data);
        }
        this.b = !this.b;
        return null;
    }
}
