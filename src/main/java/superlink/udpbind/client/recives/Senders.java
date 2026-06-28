package superlink.udpbind.client.recives;

import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.util.Utils;
import superlink.util.asynhandle.AsynHandler;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class Senders {
    DatagramSocket socket;
    UserContext userContext;
    byte[] prex;
    InetAddress inetAddress;
    int port;
    ByteBufer blockingQueue;
    DatagramPacket datagramPacket;
    byte[] bytes;
    public static AsynHandler cheak;
    public Senders(){
        this.socket = UDPclient.socket;
    }
    public Senders Init(short id,String username){
        try {
            this.userContext=UDPclient.mainDataQueue.getUserContext(username);
        } catch (Exception e) {
            return null;
        }
        this.inetAddress=userContext.inetAddress;
        this.port=userContext.port;
        int bothId=userContext.getBothId();
        prex= Utils.getUseridByte(bothId, id);
        blockingQueue=userContext.getDataQue(id);
        System.out.println("Senders   " + "bothId: "+userContext.getBothId()+"  userid: "+userContext.getUserId()+" id: "+id);
        datagramPacket=new DatagramPacket(new byte[0],0,inetAddress,port);
        bytes=Utils.byteMerger(prex,new byte[65501]);
        return this;
    }

    public synchronized Senders InitInit(short id,UserContext userContext){
        this.userContext=userContext;
        this.inetAddress=userContext.inetAddress;
        this.port=userContext.port;
        int bothId=userContext.getBothId();
        prex= Utils.getUseridByte(bothId, id);
        blockingQueue=userContext.getDataQue(id);
        System.out.println("Senders   " + "bothId: "+userContext.getBothId()+"  userid: "+userContext.getUserId()+" id: "+id);
        datagramPacket=new DatagramPacket(new byte[0],0,inetAddress,port);
        bytes=Utils.byteMerger(prex,new byte[65501]);
        return this;
    }

    public byte[] getPrex(){
        return prex;
    }

    public void sendSym(byte[] data){
        byte[] pre= Utils.getUseridByte(userContext.getBothId(), (short) 0);
        byte[] send=Utils.byteMerger(pre,data);
        synchronized (datagramPacket){
            datagramPacket.setData(send);
            //DatagramPacket datagramPacket=new DatagramPacket(send,send.length,inetAddress,port);
            send0(datagramPacket);
        }
    }
    public void ssendSym(byte[] data){
        byte[] pre= Utils.getUseridByte(userContext.getBothId(), (short) 5);
        byte[] send=Utils.byteMerger(pre,data);
        synchronized (datagramPacket){
            datagramPacket.setData(send);
            //DatagramPacket datagramPacket=new DatagramPacket(send,send.length,inetAddress,port);
            send0(datagramPacket);
        }
    }
    public final static byte[] SR ="SR".getBytes();
    public void ssendSymRe(byte[] data,short id){
        byte[] pre= Utils.getUseridByte(userContext.getBothId(), (short) 5);
        byte[] bid = Utils.shortToByteArray(id);
        byte[] send=Utils.byteMerger(pre,SR,bid,data);
        ByteRsBuffer byteRsBuffer= (ByteRsBuffer) userContext.getQueue((short) 5);
        byteRsBuffer.add(send);
//        synchronized (datagramPacket){
//            datagramPacket.setData(send);
//            //DatagramPacket datagramPacket=new DatagramPacket(send,send.length,inetAddress,port);
//            send0(datagramPacket);
//        }
    }
    public void send(byte[] data){
        byte[] send=Utils.byteMerger(prex,data);
        datagramPacket.setData(send,0,send.length);
        send0(datagramPacket);
    }

    public void send(byte[] data,int length){
        datagramPacket.setData(data,0,length);
        send0(datagramPacket);
    }

//    public void send(byte[] data,int start,int length){
//        System.arraycopy(bytes,6,data,);
//        datagramPacket.setData(data,0,length);
//        try {
//            this.socket.send(datagramPacket);
//        } catch (IOException e) {
//            e.printStackTrace();
//            this.socket =UDPclient.socket;
//        }
//    }
    public void send(byte[]... bytess){
        int length = 0;
        int len = 6;
        for (byte[] bytes:bytess){
            length=length+bytes.length;
        }
        byte[] send=new byte[length+6];
        for (byte[] bytes:bytess){
            System.arraycopy(bytes,0,send,len,bytes.length);
            len=len+bytes.length;
        }
        send[0]=prex[0];
        send[1]=prex[1];
        send[2]=prex[2];
        send[3]=prex[3];
        send[4]=prex[4];
        send[5]=prex[5];
        datagramPacket.setData(send,0,send.length);
        send0(datagramPacket);
    }
    public void sendsyn(byte[]... bytess){
        int length = 0;
        int len = 6;
        for (byte[] bytes:bytess){
            length=length+bytes.length;
        }
        for (byte[] bytes:bytess){
            System.arraycopy(bytes,0,this.bytes,len,bytes.length);
            len=len+bytes.length;
        }
        datagramPacket.setData(this.bytes,0,length+6);
        send0(datagramPacket);
    }

    public void send0(byte[] data){
//        byte[] send=Utils.byteMerger(prex,data);
        System.arraycopy(data,0,bytes,6,data.length);
        //DatagramPacket datagramPacket=new DatagramPacket(send,send.length,inetAddress,port);
        // synchronized (datagramPacket){
        datagramPacket.setData(bytes,0,data.length+6);
        send0(datagramPacket);
    }
    public void send0(byte[] data,int pos,int len){
//        byte[] send=Utils.byteMerger(prex,data);
        System.arraycopy(data,pos,bytes,0,len);
        //DatagramPacket datagramPacket=new DatagramPacket(send,send.length,inetAddress,port);
        // synchronized (datagramPacket){
        datagramPacket.setData(bytes,0,pos+len);
        send0(datagramPacket);
    }
    public void send0(byte[] pre,byte[] data){
//        byte[] send=Utils.byteMerger(prex,data);
        System.arraycopy(pre,0,bytes,6,pre.length);
        System.arraycopy(data,0,bytes,6+pre.length,data.length);
        //DatagramPacket datagramPacket=new DatagramPacket(send,send.length,inetAddress,port);
        // synchronized (datagramPacket){
        datagramPacket.setData(bytes,0,data.length+pre.length+6);
        send0(datagramPacket);
        //}
    }

    public void send1(byte[] data){
        datagramPacket.setData(data);
        send0(datagramPacket);
    }

    public void send0(DatagramPacket datagramPacket){
        if (cheak!=null){
            try {
                cheak.call(datagramPacket);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        try {
            this.socket.send(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
            this.socket =UDPclient.socket;
        }
    }
//    public boolean send(byte[] data){
//        byte[] send=Utils.byteMerger(prex,data);
//        //DatagramPacket datagramPacket=new DatagramPacket(send,send.length,inetAddress,port);
//            datagramPacket.setData(send);
//            try {
//                this.Socket.send(datagramPacket);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        return true;
//    }



    public static void Sends(String u,int id,byte[] bytes){
        UserContext userContext=UDPclient.mainDataQueue.getUserContext(u);
        if (userContext!=null){
            byte[] bytes1=Utils.getUseridByte(userContext.getBothId(), (short) id);
            bytes1=Utils.byteMerger(bytes1,bytes);
            Sends(bytes1,userContext.inetAddress,userContext.port);
        }
    }
    public static void Sends(int bid,int id,InetAddress inetAddress,int port,byte[] bytes){
        byte[] bytes1=Utils.getUseridByte(bid, (short) id);
        bytes1=Utils.byteMerger(bytes1,bytes);
        Sends(bytes1,inetAddress,port);
    }

    protected static void Sends(byte[] bytes,InetAddress inetAddress,int port){
        if(bytes.length==12){
            System.out.println(Arrays.toString(bytes));
        }
        DatagramPacket datagramPacket=new DatagramPacket(bytes,bytes.length,inetAddress,port);
        Sends0(datagramPacket);
    }

    public static void ServerSends(byte[] bytes){
        DatagramPacket datagramPacket=new DatagramPacket(bytes,bytes.length,UDPclient.getServerip(),UDPclient.getSport());
//        System.out.println(UDPclient.socket.getInetAddress());
        Sends0(datagramPacket);
    }
    public static void Sends(InetAddress inetAddress,int port,byte[] bytes){
        DatagramPacket datagramPacket=new DatagramPacket(bytes,bytes.length,inetAddress,port);
        Sends0(datagramPacket);
    }
    static byte[] prex0 = new byte[6];
    public static void SendMain(InetAddress inetAddress,int port,byte[] bytes){
        bytes = Utils.byteMerger(prex0,bytes);
        DatagramPacket datagramPacket=new DatagramPacket(bytes,bytes.length,inetAddress,port);
        Sends0(datagramPacket);
    }
    public static void Sends2(InetAddress inetAddress,int port,byte[] bytes){
        DatagramPacket datagramPacket=new DatagramPacket(bytes,bytes.length,inetAddress,port);
        Sends0(datagramPacket);
        Sends0(datagramPacket);
    }

    public static void Sends0(DatagramPacket datagramPacket){
        if (cheak!=null){
            try {
                cheak.call(datagramPacket);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        try {
            UDPclient.socket.send(datagramPacket);
        } catch (Exception e) {
            e.printStackTrace();
//            UDPclient.initSocket();
//            try {
//                UDPclient.socket.send(datagramPacket);
//            } catch (IOException ioException) {
//                ioException.printStackTrace();
//            }
        }
    }

    public void Sends(File file){

    }
    ReentrantLock reentrantLock;
    public void Revices(){
        while (true){
            reentrantLock.lock();
            byte[] bytes=blockingQueue.poll();

            try {
                reentrantLock.newCondition().await(10, TimeUnit.MICROSECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                reentrantLock.newCondition().signalAll();
            }
            reentrantLock.unlock();
        }
    }

    public static void main(String[] args) {
        DatagramPacket datagramPacket=new DatagramPacket("SE".getBytes(),2,UDPclient.getServerip(),UDPclient.getSport());
        try {
            new DatagramSocket().send(datagramPacket);
            new DatagramSocket().send(datagramPacket);
            new DatagramSocket().send(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
