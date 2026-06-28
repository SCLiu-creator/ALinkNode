package superlink.udpbind.servlet;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.ByteBufer;
import superlink.udpbind.usedata.User;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class UdpReturnServer implements Runnable, ByteBufer {

    public static final Logger logger = LoggerFactory.getLogger(UdpReturnServer.class);

    public UserContext userContext;


    @Override
    public void run() {
        byte[] bytes=new byte[65507];
        DatagramPacket packet = new DatagramPacket(bytes, 65507);
        int i=0;
        while (true){

        }

    }


    public static void main(String[] args)throws Exception {
        InetAddress inetAddress1=InetAddress.getByName("222.34.43.43");
        String s1=inetAddress1.getHostAddress();
        String s2=inetAddress1.getHostName();
        String s3=inetAddress1.getCanonicalHostName();
        InetAddress inetAddress=InetAddress.getByName(s1);
        if (inetAddress.equals(inetAddress1)){
            return;
        }
    }


    @Override
    public void add(DatagramPacket packet) {

    }

    @Override
    public boolean add(byte[] e) {
        return false;
    }

    @Override
    public byte[] poll() {
        return new byte[0];
    }

    @Override
    public byte[] take() throws InterruptedException {
        return new byte[0];
    }

    @Override
    public byte[] poll(long timeout, TimeUnit unit) throws InterruptedException {
        return new byte[0];
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public void clear() {

    }

    @Override
    public boolean equals(Object obj) {
        return obj.hashCode()==hashCode();
    }

    @Override
    public int hashCode() {
        return userContext.userName.hashCode();
    }
}
