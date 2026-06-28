package superlink.udpbind.dataqueue;

import superlink.udpbind.dataLink.UdpData;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;




public class Reciverques implements Runnable{
    public DatagramSocket datagramSocket;
    public Integer id;
    public String requestname;
    public DataQueue dataQueue;
    public UdpData udpData;

    public Reciverques(UdpData udpData, String requestname, DataQueue dataQueue){
        this.udpData=udpData;
        this.datagramSocket=udpData.dataSocket;
        this.requestname=requestname;
        this.dataQueue=dataQueue;
    }

    @Override
    public void run() {
        DatagramPacket packet=new DatagramPacket(new byte[65507],65507);
        while (udpData.state){

            try {
                datagramSocket.receive(packet);

                System.out.println("RECVS:"+new String(packet.getData())+"    "+packet.getPort());
                BlockingQueue<byte[]> queue=dataQueue.quemap.get(packet.getData()[0]);
                queue.add(Arrays.copyOfRange(packet.getData(),1,packet.getLength()));
            }catch (Exception e){
                System.out.println("RECVS:"+packet.getData()+"    "+packet.getPort());
                e.printStackTrace();
            }

        }

    }

}