package superlink.udpbind.client.recives.data.blockBuffer;

import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.ByteBufer;
import superlink.udpbind.client.recives.ByteQueLink;
import superlink.udpbind.client.recives.Senders;
import superlink.util.Utils;

import java.net.DatagramPacket;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

//不使用
public class DataStream implements ByteBufer {

    public static Map<String,DataStream> dataStreamMap=new HashMap<>();

    ByteBufer blockingQueueSend;
    ByteBufer blockingQueueRev;
    public byte[][] arraysend =new byte[256][];
    public byte[][] arraysendA =new byte[256][];
    public byte[][] arrayrev =new byte[256][];
    public byte[][] arrayrevA =new byte[256][];
    public int possend=0;
    public int posrev=0;
    public volatile AtomicInteger cheaksend =new AtomicInteger(0);
    public volatile AtomicInteger cheakrev =new AtomicInteger(0);
    int times=1;
    short id;
    Senders senders;
    UserContext userContext;
    public DataStream(String username,short id) throws Exception {
        arraysendA =new byte[256][];
        arrayrevA =new byte[256][];
        blockingQueueSend=new ByteQueLink();
        blockingQueueRev=new ByteQueLink();
        this.id=id;
        senders=new Senders();
        senders.Init(this.id,username);
        userContext= UDPclient.mainDataQueue.getUserContext(username);
        userContext.setQueue(id,this);
        arraysend=arraysendA;
        arrayrev=arrayrevA;
        new re().start();
    }
    public void bulid(){
        senders.sendSym(("cl"+id).getBytes());
    }
    boolean over=false;
    int alsr=0;

    volatile Thread sendThread;
    byte[] sendSy=new byte[]{1,0,0};
    public synchronized void send(byte[] bytes){
        if (possend<256){
            sendSy[2]=(byte) (possend-128);
            arraysend[possend]= Utils.byteMerger(sendSy,bytes);
            cheaksend.getAndIncrement();
        }else {
            while (cheaksend.get()!=0){

                try {
                    sendThread=Thread.currentThread();
                    Thread.sleep(times*100);
                } catch (InterruptedException e) {
                    System.out.println("DataStream sends interrput");
                    if (over){return;}
                    break;
                }finally {
                    sendThread=null;
                }
                if (times<=32){
                    times=times*2;
                } else {
                    alsr++;
                    senders.send0(new byte[]{1,-128});}
            }
            times=1;
            possend=0;
            sendSy[2]=(byte) (possend-128);
            arraysendA[possend]= Utils.byteMerger(sendSy,bytes);
        }
        senders.send(arraysendA[possend]);
        possend++;
    }


    byte[] bytesReBuffer;
    byte[] reSend=new byte[]{0,0,0};
    int timer=1;
    volatile Thread reciveThread;
    public synchronized byte[] recive0(){
        bytesReBuffer= arrayrevA[posrev];
        while (true){
            if (bytesReBuffer==null){
                reSend[1]=-1;
                reSend[2]= (byte) (posrev-128);
                senders.send0(reSend);

                try {
                    reciveThread=Thread.currentThread();
                    Thread.sleep(60*timer*timer);
                } catch (InterruptedException e) {
//                    e.printStackTrace();
                    System.out.println("DataStream recive interrput");
                    if (over){
                        return new byte[3];
                    }

                }finally {
                    reciveThread=null;
                }

                if (timer<=16){
                    timer=timer+1;
                }else {
                    alsr++;
                    reSend[1]=-128;
                    senders.send0(reSend);
                }

            }else {
                timer=1;
                break;
            }
            bytesReBuffer= arrayrevA[posrev];
        }
        reSend[1]=1;
        reSend[2]= (byte) (posrev-128);
        senders.send0(reSend);
        if (bytesReBuffer[1]==127){
            return new byte[3];
        }
        arrayrevA[posrev]=null;
        posrev++;
        if (posrev>=256){
            posrev=0;
            reSend[1]=127;
            senders.send0(reSend);
        }
        if (bytesReBuffer.length==3){
            System.out.println("tttt");
        }
        return bytesReBuffer;
    }
    public byte[] recive(){ byte[] bytes=recive0();return Arrays.copyOfRange(bytes,3,bytes.length);}

    //1,
    Thread reThread;
    boolean aBoolean=true;
    int al=0;

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
                bytes1 = blockingQueueRev.poll();
                if (bytes1 != null) {
                    if (bytes1[0] == 0) {
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
                            sendThread.interrupt();
                        }

                    } else {

                    }
                    time = 1;
                }
                bytes2 = blockingQueueSend.poll();
                if (bytes2 != null) {
                    if (bytes2[0] == 1) {
                        if (bytes2[1] == -128) {
                            senders.send0(new byte[]{1,127});
                        } else if (bytes2[1] == 127) {
                            al++;

                    } else {

                    }
                }
                time = 1;
            }
                alsr=0;
                al=0;
            if (bytes1 == null && bytes2 == null) {
                if (time < 1000) {
                    time=time*(time+1);
                }else {
                    if (alsr>7&&al==0){
                        over=true;
                        if (sendThread!=null){sendThread.interrupt();}
                        if (reciveThread!=null){reciveThread.interrupt();}
                    }else {

                        alsr=0;
                        al=0;
                    }

                }
            }
                Thread.sleep(1 * time);
            } catch (Exception e) {
//                System.out.println("DataStreamReThread Interrupt");
//                e.printStackTrace();
            }
        }

        }
    }




    public List list=new  ArrayList(1000);
    public void add(DatagramPacket packet) {
        throw new IllegalStateException("UnImplement");
    }

    @Override
    public boolean add(byte[] o) {
        byte[] bytes =  o;
        if (bytes[0] == 0) {
            blockingQueueRev.add(bytes);
            reThread.interrupt();
//            blockingQueueSend.add(bytes);
//            if (sendThread!=null){sendThread.interrupt();}
        } else {
            if (bytes[1] == 0) {

                arrayrevA[bytes[2] + 128] = bytes;
                if (reciveThread != null) {
                    reciveThread.interrupt();
                }
            } else {
                blockingQueueSend.add(bytes);
                reThread.interrupt();
            }
        }

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
    public void clear() {

    }


    @Override
    public int size() {
        return 0;
    }

    @Override
    public void finalize(){
        aBoolean=false;
        userContext.deltask(id);
        dataStreamMap.remove(userContext.userName);
//        reThread.destroy();

    }
    @Override
    public int hashCode(){
        return userContext.getBothId()+id;
    }

}
