package superlink.udpbind.client.recives;

import superlink.udpbind.client.UDPclient;

import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class RepeateBufer implements ByteBufer {

    RepeateBufer(){
        DatagramChannel channel=UDPclient.socket.getChannel();

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

    public static void main(String[] args) throws Exception {
        DatagramSocket socket=new DatagramSocket(8080);
        DatagramChannel channel = DatagramChannel.open();
        channel.socket().bind(new InetSocketAddress(9999));
        SocketAddress socketAddress= new InetSocketAddress("127.0.0.1",UDPclient.serverport);
        byte[] bytes=new byte[1470];
        DatagramPacket datagramPacket=new DatagramPacket(bytes,0,1470,InetAddress.getByName("127.0.0.1"),UDPclient.serverport);
        long t=System.currentTimeMillis();
        System.out.println(System.currentTimeMillis());
        for (int i = 0; i < 1000*100; i++) {
            System.arraycopy(bytes,0,bytes,0,bytes.length);
            datagramPacket.setData(bytes);
            socket.send(datagramPacket);
        }
        //671
//        ByteBuffer byteBuffer=ByteBuffer.wrap(bytes);
//        byteBuffer.put(bytes);
//        byteBuffer.flip();
//        for (int i = 0; i < 1000*100; i++) {
//            byteBuffer.clear();
////            System.arraycopy(bytes,0,bytes,0,bytes.length);
//            byteBuffer.put(bytes,0,bytes.length);
//            byteBuffer.flip();
//            channel.send(byteBuffer,socketAddress);
//        }
        //681
        System.out.println(System.currentTimeMillis()-t);
    }
}
