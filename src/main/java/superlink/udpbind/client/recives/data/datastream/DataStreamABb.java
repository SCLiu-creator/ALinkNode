package superlink.udpbind.client.recives.data.datastream;

import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.ByteBufer;
import superlink.udpbind.client.recives.ByteQueue;
import superlink.udpbind.client.recives.DataLenMange;
import superlink.udpbind.client.recives.Senders;
import superlink.util.Utils;
import superlink.util.datastack.DataConList;

import java.net.DatagramPacket;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

//不使用
public class DataStreamABb implements ByteBufer {

    public static Map<String, DataStreamABb> dataStreamMap = new HashMap<>();

    ByteBufer blockingQueueSend;
    ByteBufer blockingQueueRev;
    public int possend = 0;
    public int posrev = 0;
    public volatile AtomicInteger cheaksend = new AtomicInteger(0);
    public volatile AtomicInteger cheakrev = new AtomicInteger(0);
    int times = 1;
    short id;
    public Senders senders;
    UserContext userContext;
    public static DataStreamABb dealGetDataStreamAB(String username, short id){
        DataStreamABb queueStream=DataStreamABb.dataStreamMap.get(username);
        if (queueStream==null){
            try {
                queueStream=new DataStreamABb(username,id,true);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            DataStreamABb.dataStreamMap.put(username,queueStream);
        }
        return queueStream;
    }

    public DataStreamABb(String username, short id) throws Exception {
        blockingQueueSend = new ByteQueue(256);
        blockingQueueRev = new ByteQueue(256);
        this.id = id;
        senders = new Senders();
        senders.Init(this.id, username);
        userContext = UDPclient.mainDataQueue.getUserContext(username);
        arraysend = arraysendA;
        arrayrev = arrayrevA;
        new DataStreamABb.re().start();
    }
    public DataStreamABb(String username, short id,boolean b) throws Exception {
        blockingQueueSend = new ByteQueue(256);
        blockingQueueRev = new ByteQueue(256);
        this.id = id;
        senders = new Senders();
        senders.Init(this.id, username);
        userContext = UDPclient.mainDataQueue.getUserContext(username);
        userContext.setQueue(id, this);
        arraysend = arraysendA;
        arrayrev = arrayrevA;
        dataStreamMap.put(username,this);
        new DataStreamABb.re().start();
    }

    public boolean build() {
        byte[] bytes=null;
        senders.sendSym(("cl" + id).getBytes());
        for (int i = 0; i <4&&bytes==null ; i++) {
            try {
                bytes=  userContext.getDataQue(this.id).poll(2300,TimeUnit.MICROSECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (bytes!=null){
            dataStreamMap.put(userContext.userName,this);
            userContext.setQueue(id, this);
            return true;
        }else {
            return false;
        }
    }
    public void build0() {
        senders.sendSym(("cl" + id).getBytes());
    }
    boolean over = false;
    int alsr = 0;

    public volatile Thread sendThread;
    public volatile byte[][] arraysend = new byte[256][];
    public byte[][] arraysendA = new byte[256][];
    public byte[][] arraysendB = new byte[256][];
    byte[] sendSyA = new byte[]{(byte) 0b11110001, 0, 0};
    byte[] sendSyB = new byte[]{0b00000001, 0, 0};
    byte[] sendSy = sendSyA;
    volatile boolean seAB=true;
    public volatile boolean reState=true;
    public int send(byte[] bytes) {
        if (possend < 256) {
            sendSy[2] = (byte) (possend - 128);
            arraysend[possend] = Utils.byteMerger(sendSy, bytes);
            cheaksend.getAndIncrement();
        } else {
            while (true){
                if (seAB){
                    if(sendSy==sendSyA){
                        while (cheaksend.get() != 0) {
                            try {
                                sendThread = Thread.currentThread();
                                senders.send0(new byte[]{0b00000000, 2});
                                Thread.sleep(times * 100);
                            } catch (InterruptedException e) {
                                System.out.println("DataStream sends interrput");
                                if (reState){ break; }
                                if (over) { return 0; }
//                                break;
                            } finally {
                                sendThread = null;
                            }
                            if (times <= 32) {
                                times = times * 2;
                            } else {
                                alsr++;
                                senders.send0(new byte[]{0b00000001, -128});
                            }
                        }
                        sendSy=sendSyB;
                        arraysend=arraysendB;
                        possend=0;
                        seAB=false;
                        break;
                    }else {
                        if (reState!=seAB){
                            sendSy=sendSyB;
                            arraysend=arraysendB;
                            possend=0;
                            seAB=false;
                            break;
                        }else {
                            continue;
                        }//                    sendSy=sendSyA;
                    }
                }else {
                    if(sendSy==sendSyB){
                        while (cheaksend.get() != 0) {
                            try {
                                sendThread = Thread.currentThread();
                                senders.send0(new byte[]{(byte) 0b11110000, 2});//询问rec另一个缓冲区是否读完
                                Thread.sleep(times * 100);
                            } catch (InterruptedException e) {
                                System.out.println("DataStream sends interrput");
                                if (!reState){ break; }
                                if (over) { return 0; }
//                                break;
                            } finally {
                                sendThread = null;
                            }
                            if (times <= 32) {
                                times = times * 2;
                            } else {
                                alsr++;
                                senders.send0(new byte[]{(byte) 0b11110001, -128});
                            }
                        }
                        synchronized (arrayrev){
                            seAB=true;
                            sendSy=sendSyA;
                            possend=0;
                            arraysend=arraysendA;
                        }

                        break;
                    }else {
                        if (reState==seAB){
                            synchronized (arrayrev){
                                sendSy=sendSyA;
                                arraysend=arraysendA;
                                possend=0;
                                seAB=true;
                            }
                            break;
                        }else {
                            continue;
                        }
//                    sendSy=sendSyA;
                    }
                }
            }

            times = 1;
            possend = 0;
            sendSy[2] = (byte) (possend - 128);
            arraysend[possend] = Utils.byteMerger(sendSy, bytes);
        }
        senders.send(arraysend[possend]);
        possend++;
        return bytes.length;
    }


    byte[] bytesReBuffer;
    public volatile byte[][] arrayrev = new byte[256][];
    public byte[][] arrayrevA = new byte[256][];
    public byte[][] arrayrevB = new byte[256][];
    byte[] reSendA = new byte[]{(byte) 0b11110000, 0, 0};
    byte[] reSendB = new byte[]{0b00000000, 0, 0};
    byte[] reSend = reSendA;
    int timer = 1;
    public volatile boolean reAB=true;
    public volatile Thread reciveThread;

    public byte[] recive0(long time) {
        boolean b=true;
        int time1=0;
        bytesReBuffer = arrayrev[posrev];
        while (b) {
            if (bytesReBuffer == null) {
                if (time1==4){
                    return new byte[3];
                }
                reSend[1] = -1;
                reSend[2] = (byte) (posrev - 128);
                senders.send0(reSend);
                try {
                    reciveThread = Thread.currentThread();
                    Thread.sleep(time/4);
                    time1++;
                } catch (InterruptedException e) {
                    System.out.println("DataStream recive interrput");
                } finally {
                    reciveThread = null;
                }
            } else {
                break;
            }
            bytesReBuffer = arrayrev[posrev];
        }
        reSend[1] = 1;
        reSend[2] = (byte) (posrev - 128);
        senders.send0(reSend);
        if (bytesReBuffer[1] == 127) {
            return new byte[3];
        }
        arrayrev[posrev] = null;
        posrev++;
        if (posrev >= 256) {
            arrayrev[255]=new byte[0];
            synchronized (this){
                if (reAB){
                    arrayrev=arrayrevB;
                    arrayrevB[255]=null;
                    reAB=false;
                    reSend=reSendB;
                }else {
                    arrayrev=arrayrevA;
                    arrayrevA[255]=null;
                    reAB=true;
                    reSend=reSendA;
                }
                posrev = 0;
            }
            reSend[1] = 127;
            senders.send0(reSend);
        }
        if (bytesReBuffer.length == 3) {
            System.out.println("tttt");
        }
        return bytesReBuffer;
    }
    public byte[] recive(long time) {
        byte[] bytes = recive0(time);
        return Arrays.copyOfRange(bytes, 3, bytes.length);
    }

    public byte[] recive0() {
        bytesReBuffer = arrayrev[posrev];
        while (true) {
            if (bytesReBuffer == null) {
                reSend[1] = -1;
                reSend[2] = (byte) (posrev - 128);
                senders.send0(reSend);

                try {
                    reciveThread = Thread.currentThread();
                    Thread.sleep(10 * timer * timer);
                } catch (InterruptedException e) {
//                    e.printStackTrace();
                    System.out.println("DataStream recive interrput");
                    if (over) {
                        return new byte[3];
                    }

                } finally {
                    reciveThread = null;
                }

                if (timer <= 16) {
                    timer = timer + 1;
                } else {
                    alsr++;
                    reSend[1] = -128;
                    senders.send0(reSend);
                }

            } else {
                timer = 1;
                break;
            }
            bytesReBuffer = arrayrev[posrev];
        }
        reSend[1] = 1;
        reSend[2] = (byte) (posrev - 128);
        senders.send0(reSend);
        if (bytesReBuffer[1] == 127) {
            return new byte[3];
        }
        arrayrev[posrev] = null;
        posrev++;
        if (posrev >= 256) {
            arrayrev[255]=new byte[0];
            synchronized (arrayrev){
                if (reAB){
                    arrayrev=arrayrevB;
                    arrayrevB[255]=null;
                    reAB=false;
                    reSend=reSendB;
                }else {
                    arrayrev=arrayrevA;
                    arrayrevA[255]=null;
                    reAB=true;
                    reSend=reSendA;
                }
                posrev = 0;
            }

            reSend[1] = 127;
            senders.send0(reSend);
        }
        if (bytesReBuffer.length == 3) {
            System.out.println("tttt");
        }
        return bytesReBuffer;
    }

    public byte[] recive() {
        byte[] bytes = recive0();
        return Arrays.copyOfRange(bytes, 3, bytes.length);
    }

    public synchronized boolean reMeLast(byte[] bytes) {
        if (posrev != 0) {
            posrev--;
            arrayrev[posrev]=bytes;
            return true;
        }
        return false;
    }

    //1,
    Thread reThread;
    boolean aBoolean = true;
    int al = 0;

    public class re extends Thread {
        @Override
        public void run() {
            reThread = Thread.currentThread();
            reThread.setName("DataStream re");
            byte[] bytes1 = null;
            byte[] bytes2 = null;
            int time = 0;
            byte[] resend = new byte[3];
            byte[] alivebytes = new byte[]{};
            while (aBoolean) {
                try {
                    bytes1 = blockingQueueRev.poll();//0
                    if (bytes1 != null) {
                        if ((bytes1[0]&0b11110000)==0b11110000) {//是不是A缓冲区
                            if (bytes1[1] == -1) {
                                byte[] bytes0 = arraysendA[bytes1[2] + 128];
//                            bytes0[0] = 1;
                                bytes0[1] = 0;
//                            bytes1[2]=bytes[2];
                                senders.send0(bytes0);
                            } else if (bytes1[1] == 1) {
                                arraysendA[bytes1[2] + 128] = null;
                            } else if (bytes1[1] == 127) {
                                blockingQueueRev.clear();
                                cheaksend.set(0);
                                reState=false;
                                sendThread.interrupt();
                            }else if (bytes1[1] == 2) {
                                if (reAB){//正在读A缓冲区
                                    if(arrayrevB[255]!=null){//是否已经标记缓冲区读完
                                        if (arrayrevB[255].length==0){
                                            senders.send0(new byte[]{0b00000001,2});
                                        }

                                    }else {
                                        senders.send0(new byte[]{0b00000001,2});
                                    }
                                }else {
                                    if(arrayrevA[255]!=null){//是否已经标记缓冲区读完
                                        if (arrayrevA[255].length==0){
                                            senders.send0(new byte[]{(byte) 0b11110001,2});
                                        }
                                    }else {
                                        senders.send0(new byte[]{(byte) 0b11110001,2});
                                    }
                                }
                            }

                        } else {
                            if (bytes1[1] == -1) {
                                byte[] bytes0 = arraysendB[bytes1[2] + 128];
//                            bytes0[0] = 1;
                                bytes0[1] = 0;
//                            bytes1[2]=bytes[2];
                                senders.send0(bytes0);
                            } else if (bytes1[1] == 1) {
                                arraysendB[bytes1[2] + 128] = null;
                            } else if (bytes1[1] == 127) {
                                blockingQueueRev.clear();
                                cheaksend.set(0);
                                reState=true;
                                sendThread.interrupt();
                            }else if (bytes1[1] == 2) {
                                if (reAB){//正在读A缓冲区
                                    if(arrayrevB[255]!=null){//是否已经标记缓冲区读完
                                        if (arrayrevB[255].length==0){
                                            senders.send0(new byte[]{0b00000001,2});
                                        }

                                    }else {
                                        senders.send0(new byte[]{0b00000001,2});
                                    }
                                }else {
                                    if(arrayrevA[255]!=null){//是否已经标记缓冲区读完
                                        if (arrayrevA[255].length==0){
                                            senders.send0(new byte[]{(byte) 0b11110001,2});
                                        }
                                    }else {
                                        senders.send0(new byte[]{(byte) 0b11110001,2});
                                    }
                                }
                            }
                        }
                        time = 1;
                    }
                    bytes2 = blockingQueueSend.poll();
                    if (bytes2 != null) {
                        if (bytes2[0] == 1) {
                            if (bytes2[1] == -128) {
                                senders.send0(new byte[]{1, 127});
                            } else if (bytes2[1] == 2) {
                                if ((bytes2[0]&0b11110000)==0b11110000){//检查回答中是不是A缓冲区已读完
//                                    seAB=false;//使正在读的标识变成B缓冲区
                                    reState=false;
                                    cheaksend.set(0);//使发送区跳出循环
                                    sendThread.interrupt();
                                }else {
                                    reState=true;
                                }
//                                blockingQueueRev.clear();
                            } else if (bytes2[1] == 127) {
                                al++;
                            } else if (bytes2[1]==88){//close
                                over = true;
                                if (sendThread != null) {
                                    sendThread.interrupt();
                                }
                                if (reciveThread != null) {
                                    reciveThread.interrupt();
                                }
                                senders.send0(new byte[]{1,89});
                            }else if (bytes2[1]==89){//close
                                synchronized (this){
                                    this.notifyAll();
                                }
                            }
                        }
                        time = 1;
                    }
                    alsr = 0;
                    al = 0;
                    if (bytes1 == null && bytes2 == null) {
                        if (time < 1000) {
                            time = (time + 1) * (time + 1);
                        } else {
                            if (al==0){
                                if (alsr > 7 ) {
                                    over = true;
                                    if (sendThread != null) {
                                        sendThread.interrupt();
                                    }
                                    if (reciveThread != null) {
                                        reciveThread.interrupt();
                                    }
                                }
                            }else {
                                alsr = 0;
                                al = 0;
                            }

                        }
                    }
                    Thread.sleep(10 * time);
                } catch (Exception e) {
//                System.out.println("DataStreamReThread Interrupt");
//                e.printStackTrace();
                }
            }

        }
    }


    public List list = new ArrayList(1000);

    public void add(DatagramPacket packet) {
        add(Arrays.copyOfRange(packet.getData(), 6, packet.getLength()));
    }

    @Override
    public boolean add(byte[] o) {
        byte[] bytes=o;
        if ((bytes[0]&0b00000001)==0b00000000){//0则进入rev
            blockingQueueRev.add(bytes);
            reThread.interrupt();
//            blockingQueueSend.add(bytes);
//            if (sendThread!=null){sendThread.interrupt();}
        }else {
            if (bytes[1]==0){
                if ((bytes[0]&0b11110000)==0b11110000){
                    arrayrevA[bytes[2]+128]=bytes;
                }else {
                    arrayrevB[bytes[2]+128]=bytes;
                }
                if (reciveThread!=null){reciveThread.interrupt();}
            }else {
                blockingQueueSend.add(bytes);
                reThread.interrupt();
            }
        }

        return false;
    }

    @Override
    public byte[] poll() {
        return null;
    }

    @Override
    public byte[] take() throws InterruptedException {
        return null;
    }

    @Override
    public byte[] poll(long timeout, TimeUnit unit) throws InterruptedException {
        return null;
    }


    @Override
    public void clear() {

    }

    @Override
    public int size() {
        return 0;
    }

    public void close(){
        senders.send0(new byte[]{1,88});
        int i=5;
        while (i>=0){
            synchronized (this){
                try {
                    wait(1000);
                    senders.send0(new byte[]{1,88});
                } catch (InterruptedException e) {
                    System.out.println("close ok");
                    break;
                }
                i--;
            }
        }

    }

    @Override
    public void finalize() {
        aBoolean = false;
        userContext.deltask(id);
        dataStreamMap.remove(userContext.userName);
//        reThread.destroy();

    }

    @Override
    public int hashCode() {
        return userContext.getBothId() + id;
    }

}

