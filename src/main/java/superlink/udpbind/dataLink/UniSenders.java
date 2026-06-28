package superlink.udpbind.dataLink;

import superlink.udpbind.dataqueue.DataQueue;
import superlink.udpbind.handle.Handler;
import superlink.util.Utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

public class UniSenders {
    DatagramSocket Socket;
    DataQueue dataQueue;
    byte[] prex;
    InetAddress inetAddress;
    int port;
    BlockingQueue<byte[]> blockingQueue;
    DatagramPacket datagramPacket;
    UdpData udpData;
    public UniSenders(String username){
        this.udpData=Handler.UdpMap.get(username);
        this.dataQueue= Handler.UdpMap.get(username).dataQueue;
        this.inetAddress=udpData.userRequest.toaddress;
        this.port=udpData.dataport;
        this.Socket= udpData.dataSocket;
    }
    public UniSenders Init(int id){
        prex= new byte[]{(byte) id};
        blockingQueue=dataQueue.quemap.get(id);
        System.out.println("uniSenders   " +" id: "+id);
        datagramPacket=new DatagramPacket(new byte[0],0,inetAddress,port);
        return this;
    }

    public void sendSym(byte[] data){
        byte[] pre= new byte[1];
        byte[] send=Utils.byteMerger(pre,data);
        synchronized (datagramPacket){
            datagramPacket.setData(send);
            //DatagramPacket datagramPacket=new DatagramPacket(send,send.length,inetAddress,port);
            try {
                this.Socket.send(datagramPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void send(byte[] data){
        byte[] send=Utils.byteMerger(prex,data);
            datagramPacket.setData(send);
            try {
                this.Socket.send(datagramPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
    public synchronized boolean send(byte[] data,int offset,int length){
        byte[] send=Utils.byteMerger(prex,data);
        //DatagramPacket datagramPacket=new DatagramPacket(send,send.length,inetAddress,port);
        datagramPacket.setData(send,offset,length);
            try {
                this.Socket.send(datagramPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        return true;
    }

    public synchronized boolean send1(byte[] data,int offset,int length){
        data[0]=prex[0];
        //DatagramPacket datagramPacket=new DatagramPacket(send,send.length,inetAddress,port);
        datagramPacket.setData(data,offset,length);
        try {
            this.Socket.send(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    ReentrantLock reentrantLock;
    public void Revices(){
        while (true){
            reentrantLock.lock();
            blockingQueue.poll();


            try {
                reentrantLock.newCondition().await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            reentrantLock.unlock();
        }

    }
}
