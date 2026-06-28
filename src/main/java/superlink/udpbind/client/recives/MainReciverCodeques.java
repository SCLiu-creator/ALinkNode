package superlink.udpbind.client.recives;

import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainReciverCodeques implements Runnable {

    public DatagramSocket datagramSocket;
    public Integer userid;
    public Short id;
    public String requestname;

    public byte[] code;
    public static Map<String,byte[]> stringMap=new HashMap<>();
    public boolean run=true;
    Thread thread;
    public MainReciverCodeques(DatagramSocket datagramSocket, String requestname, MainDataQueue dataQueue) {
        this.datagramSocket = datagramSocket;
        this.requestname = requestname;
    }


    @Override
    public void run() {
        if (thread==null){
            thread=Thread.currentThread();
        }else {
            Thread.currentThread().setName("priorityThread");
            return;
        }
        Thread.currentThread().setName("MainReacterThread");
        Thread.currentThread().setPriority(8);
        Map<Short, ByteBufer> map=null;
        byte[] bytes=new byte[65507];
        byte[] zero=new byte[6];
        ByteBuffer buffer= ByteBuffer.allocateDirect(65507);
        UserContext userContext;
        DatagramPacket packet = new DatagramPacket( buffer.array(), 65507);
        DatagramPacket packetdec = new DatagramPacket( new byte[6], 0);
        while (run) {
            try {
                datagramSocket.receive(packet);
            } catch (IOException e) {
                System.out.println("主连接连接超时");
                break;
            }

            try {
                byte[] bytes1=stringMap.get(packet.getAddress()+":"+packet.getPort());
                if (bytes1!=null){
                    for (int i = 0; i < bytes1.length; i++) {
                        bytes[i]= (byte) (bytes1[i]^bytes[i]);
                    }
                }
                //System.out.println("RECV: " +packet.getAddress().toString()+":"+ packet.getPort());
                userid =buffer.getInt();
                id = buffer.getShort();
                userContext = UDPclient.mainDataQueue.getQueUser(userid);
                byte[] buf= new byte[packet.getLength()-6];
                buffer.get(buf,0,buf.length);
                packetdec.setData(buf);
                packetdec.setAddress(packet.getAddress());
                packetdec.setPort(packet.getPort());
                userContext.map.get(id).add(packetdec);
                buffer.clear();
            } catch (Exception e) {
                System.out.println("捕获错误userid: "+ userid+"   id: "+id);
                e.printStackTrace();
                if (bytes!=null){
                    if (bytes.length<20){
                        System.out.println(new String(bytes));
                        System.out.println(Arrays.toString(bytes));
                    }else {
                        try {
                            System.out.println(new String(bytes,0,20,"utf-8")+"      byte length:  "+packet.getLength());
                            System.out.println(Arrays.toString(Arrays.copyOfRange(bytes,0,20)));
                        } catch (UnsupportedEncodingException unsupportedEncodingException) {
                            unsupportedEncodingException.printStackTrace();
                        }
                    }

                }
            }
        }

    }
}
