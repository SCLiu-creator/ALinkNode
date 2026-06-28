package superlink.udpbind.client.recives.data.datastream;

import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.ByteBufer;
import superlink.udpbind.client.recives.ByteQueue;
import superlink.udpbind.client.recives.DataLenMange;
import superlink.udpbind.client.recives.Senders;
import superlink.util.Utils;

import java.net.DatagramPacket;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class DataStreamAB implements ByteBufer {
    //[接受端还是发送端，0数据，1发送send，-1接受rec],[缓冲区选择1，-1]，[类型为数据还是指令0，1]，[内容]
    public static Map<String, DataStreamAB> dataStreamMap = new HashMap<>();
    public int pageLen;
    ByteBufer blockingQueueSend;
    ByteBufer blockingQueueRev;

    short id;
    public Senders senders;
    UserContext userContext;

    public DataStreamAB(String username, short id) {
        blockingQueueSend = new ByteQueue(256);
        blockingQueueRev = new ByteQueue(256);
        this.id = id;
        senders = new Senders();
        senders.Init(this.id, username);
        userContext = UDPclient.mainDataQueue.getUserContext(username);
        new DataStreamAB.re().start();
        pageLen= DataLenMange.getLen( username)-3;
        dataStreamMap.put(username+":"+id,this);
    }

    public static DataStreamAB dealGetDataStreamAB(String username, short id){
        DataStreamAB queueStream=DataStreamAB.dataStreamMap.get(username);
        if (queueStream==null){
            try {
                queueStream=new DataStreamAB(username,id,true);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            DataStreamAB.dataStreamMap.put(username+":"+id,queueStream);
        }
        return queueStream;
    }
    public DataStreamAB(String username, short id,boolean b) {
        blockingQueueSend = new ByteQueue(256);
        blockingQueueRev = new ByteQueue(256);
        this.id = id;
        senders = new Senders();
        senders.Init(this.id, username);
        userContext = UDPclient.mainDataQueue.getUserContext(username);
        userContext.setQueue(id, this);
        new DataStreamAB.re().start();
        pageLen= DataLenMange.getLen( username)-3;
        dataStreamMap.put(username+":"+id,this);
    }

    public boolean build() {
        byte[] bytes=null;
        for (int i = 0; i <6&&bytes==null ; i++) {
            try {
                senders.sendSym(("DC" + id).getBytes());
                bytes=  userContext.getDataQue(this.id).poll(5000,TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (bytes!=null&&bytes.length!=0){
            dataStreamMap.put(userContext.userName,this);
            userContext.setQueue(id, this);
            return true;
        }else {
            return false;
        }
    }


    boolean over = false;
    int alsr = 0;

    public volatile Thread thread;

    DataWCon sendSyA = new DataWCon((byte) -1);
    DataWCon sendSyB = new DataWCon((byte) 1);
    DataWCon sendSy = sendSyA;
    byte recordR=-1;
    int recordRpos = 0;
    int recordRv = 0;
    public volatile boolean reState=true;
    public ReentrantLock reentrantLock = new ReentrantLock();
    public Condition conditionW = reentrantLock.newCondition();
    public Condition conditionR = reentrantLock.newCondition();
    public  DataWCon getWrit(){
        synchronized (sendSy){
            if(sendSy.isFull()){
                if(sendSy==sendSyA){
                    if(sendSyB.isFull()){
                        return null;
                    }else {
                        sendSy=sendSyB;
                    }
                }else {
                    if(sendSyA.isFull()){
                        return null;
                    }else {
                        sendSy=sendSyA;
                    }
                }
                return sendSy;
            }else {
                return sendSy;
            }
        }
    }
    public DataWCon getWrit(byte b){
        if(b==-1){
            return sendSyA;
        }else {
            return sendSyB;
        }
    }
    public void write(byte[] bytes) {
        byte[] redata = null;
        while (redata!=bytes){
            if (bytes.length>pageLen){
                redata=Utils.subByte(bytes,0,pageLen);
                bytes = Utils.subByte(bytes,pageLen,bytes.length);
            }else {
                redata = bytes;
            }
            DataWCon dataWCon = getWrit();

            while (dataWCon==null){
                String threadName = Thread.currentThread().getName();
                try {
                    DataWCon bf = getWrit(recordR);
                    if(bf.view==recordRv){
                        byte[] bs = bf.get(recordRpos);
                        senders.send(new byte[]{0,recordR,bf.view, (byte) (recordRpos-128)},bs);
//                        if (bs!=null){
//
//                        }
                    }
                }  catch (Exception e) {
                    e.printStackTrace();
                }
                reentrantLock.lock();
                try {
                    Thread.currentThread().setName("wait St W");
                    reThread.interrupt();
                    conditionW.await(10,TimeUnit.SECONDS);
//                    if(recordR==sendSy.aByte&&recordRv==sendSy.view){
//                        if(sendSy.w==sendSy.array.length){
//                            System.out.println("reSetW out: ");
//                            getWrit((byte) (-sendSy.aByte)).reSet();
//                        }
//                    }
//                    DataWCon db= getWrit(recordR);
//                    if(recordR!=db.aByte&&){
//                        System.out.println("reSetW out: ");
//                        getWrit((byte) (-sendSy.aByte)).reSet();
//                    }
//                        Thread.sleep(10000);
                } catch (IllegalMonitorStateException e) {
                    System.out.println("DataStream wait IllegalMonitorStateException");
                    if (reState){ break; }
                } catch (InterruptedException e) {
                    System.out.println("DataStream wait interrput");
                    if (reState){ break; }
                } catch (Exception e) {
                    System.out.println("DataStream Exce");
                    e.printStackTrace();
                    if (reState){ break; }
                }finally {
                    reentrantLock.unlock();
                    Thread.currentThread().setName(threadName);
                    dataWCon = getWrit();
                }
            }
            System.out.println("data Pos :"+dataWCon.aByte+" : "+dataWCon.w);
            System.out.println("dataWL :"+redata.length);
            dataWCon.write(senders,redata);
        }
    }
    public void send(byte[] bytes){
        write(bytes);
    }

    DataRCon recSyA = new DataRCon((byte) -1);
    DataRCon recSyB = new DataRCon((byte) 1);
    DataRCon recSy = recSyA;

    byte recordS = -1;
    int recordSpos = 0;
    int recordSv = 0;

    public DataRCon getRead(){
        synchronized (recSy){
            if(recSy.isEmpty()){
                if(recSy==recSyA){
                    if(recSyA.isOver()){
                        if (recSyB.isOver()){
                            return null;
                        }else {
                            recSy=recSyB;
                            if(recSyA.r==recSyA.w&&recSyA.r==recSyA.array.length){
                                recSyA.reSet();
                            }
                        }
                    }
                    if(recSy.isEmpty()){
                        return null;
                    }
                    return recSy;
                }else {
                    if(recSyB.isOver()){
                        if (recSyA.isOver()){
                            return null;
                        }else {
                            recSy=recSyA;
                            if(recSyB.r==recSyB.w&&recSyB.r==recSyB.array.length){
                                recSyB.reSet();
                            }
                        }
                    }
                    if(recSy.isEmpty()){
                        return null;
                    }
                    return recSy;
                }
            }else {
                return recSy;
            }
        }
    }

    public byte[] read0() {
        byte[] bytes=null;
        DataRCon rCon=null;
        while (bytes==null){
            rCon=getRead();
            while (rCon==null){
                String threadName = Thread.currentThread().getName();
                reentrantLock.lock();
                try {
                    senders.send(new byte[]{1,recSy.aByte,1, (byte) (recSy.w-128),recSy.view});
                    Thread.currentThread().setName("wait St W");
                    reThread.interrupt();
                    empyt=false;
                    conditionR.await(7,TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    System.out.println("DataStream sends interrput");
                    if (reState){ break; }
                } finally {
                    reentrantLock.unlock();
                    Thread.currentThread().setName(threadName);
                    rCon = getRead();
                }
            }
            bytes=rCon.get();
        }
        System.out.println("data Pos :"+rCon.aByte+" : "+bytes[3] +" :"+rCon.view);
        return Utils.subByte(bytes,4,bytes.length);
    }

    public byte[] read0(long time) {
        byte[] bytes=null;
        DataRCon rCon=null;
        long time1= System.currentTimeMillis();
        while (bytes==null){
            rCon=getRead();
            while (rCon==null){
                if(System.currentTimeMillis()-time1>time){
                    return null;
                }
                String threadName = Thread.currentThread().getName();
                reentrantLock.lock();
                try {
                    senders.send(new byte[]{1,recSy.aByte,1, (byte) (recSy.w-128),recSy.view});
                    Thread.currentThread().setName("wait St W");
                    reThread.interrupt();
                    empyt=false;
                    conditionR.await(time,TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    System.out.println("DataStream sends interrput");
                    if (reState){ break; }
                } finally {
                    reentrantLock.unlock();
                    Thread.currentThread().setName(threadName);
                    rCon = getRead();
                }
            }
            bytes=rCon.get();
        }
        System.out.println("data Pos :"+rCon.aByte+" : "+bytes[3] +" :"+rCon.view);
        return Utils.subByte(bytes,4,bytes.length);
    }

    public byte[] recive() {
        byte[] bytes = read0();
        return bytes;
    }
    public byte[] recive(long l) {
        byte[] bytes = read0(l);
        return bytes;
    }
    //1,
    Thread reThread;
    boolean aBoolean = true;

    boolean empyt = true;
    int al = 0;

    public class re extends Thread {
        @Override
        public void run() {
            reThread = Thread.currentThread();
            reThread.setName("DataStream re");
            byte[] bytes1 = null;
            byte[] bytes2 = null;
            int time = 0;
            alsr = 0;
            al = 0;
            boolean frist = true;
            while (aBoolean) {
                try {
                    DataRCon rCon =null;
                    bytes1 = blockingQueueRev.poll();//0

                    if(bytes1!=null){

                        if (bytes1[1]== -1){
                            rCon=recSyA;
                        }else {
                            rCon=recSyB;
                        }
                        if (bytes1[2]==0){
                            //判断，发送端咨询缓冲区，送端处理响应
                            recordR = bytes1[1] ;
                            recordRpos = bytes1[3]+128;
                            recordRv = bytes1[4];
                        }else if(bytes1[2]==1) {
                            //判断，发送端无可读信息,接受端置空
                            int w = bytes1[3]+128;
                            if (rCon.w==w){
                                empyt=true;
                            }
                        }else if(bytes1[2]==-1) {
                            //判断，发送端咨询缓冲区，接受端返回
                            recordS = bytes1[1];
                            recordSpos = bytes1[3]+128;
                            recordSv= bytes1[4];
                            senders.send(new byte[]{-1,recSy.aByte,0, (byte) (recSy.w-128),recSy.view});
                        }
                    }

                    bytes2 = blockingQueueSend.poll();
                    if(bytes2!=null){
                        DataWCon wCon =null;
                        if (bytes2[1]== -1){
                            wCon=sendSyA;
                        }else {
                            wCon=sendSyB;
                        }
                        if (bytes2[2]==0){
                            //重置pos
                            if(bytes2[4]==wCon.view){
                                int pos=bytes2[3]+128;
                                wCon.setNull(pos);
                                if(wCon.w==wCon.r){
                                    senders.send(new byte[]{-1, wCon.aByte,1, (byte) (wCon.w-128)});
                                }
                                reentrantLock.lock();
                                try {
                                    conditionR.signalAll(); // 唤醒所有等待线程
                                } finally {
                                    reentrantLock.unlock();
                                }
                            }
                        }else if(bytes2[2]==1){
                            byte[] redata=wCon.get(bytes2[3]+128);
                            if (redata!=null){
                                senders.send(new byte[]{0, wCon.aByte,wCon.view, bytes2[3]},redata);
                            }

//                            conditionR.signalAll();
                            //todo
                        }
                    }

                    if (bytes1 == null && bytes2 == null) {
                        if(frist){
                            frist=false;
                            if (time < 1000) {
                                time = (time + 1) * (time + 1);
                            } else {
                                if (al==0){
                                    if (alsr > 7 ) {
                                        over = true;
                                        if (thread != null) {
                                            thread.interrupt();
                                        }
                                    }
                                }else {
                                    alsr = 0;
                                    al = 0;
                                }

                            }
                        }
                    }else {
                        if(frist) {
                            frist=false;
                            time = (time/2)+2;
                            alsr = 0;
                            al = 0;
                        }
                        continue;
                    }

                    if (!empyt){
                        if (recSy.w<recSy.array.length){
                            senders.send(new byte[]{1,recSy.aByte,1, (byte) (recSy.w-128)});
                        }

                        if (recSy.w>0){
                            //置空信号
                            senders.send(new byte[]{1,recSy.aByte,0, (byte) (recSy.w-1-128),recSy.view});
                        }
                    }


                    if (sendSy.r!=sendSy.w){
                        byte[] sdata ;
                        if(sendSy.w>0){
                            sdata = sendSy.array[sendSy.w-1];
                            if(sdata!=null){
                                senders.send(new byte[]{0,sendSy.aByte,sendSy.view, (byte) (sendSy.w-1-128)},sdata);
                            }
                            sdata = sendSy.array[sendSy.r];
                            if(sdata!=null){
                                senders.send(new byte[]{0,sendSy.aByte,sendSy.view, (byte) (sendSy.r-128)},sdata);
                            }

                            //询问缓冲区
                            senders.send(new byte[]{-1,sendSy.aByte,-1, (byte) (sendSy.w-1-128), sendSy.view});
                        }
                    }

                    if (sendSyA.isClear()&&sendSyA!=sendSy){
                        sendSyA.reSet() ;
                        reentrantLock.lock();
                        conditionW.signalAll();
                        reentrantLock.unlock();
                    }
                    if (sendSyB.isClear()&&sendSyB!=sendSy){
                        sendSyB.reSet() ;
                        reentrantLock.lock();
                        conditionW.signalAll();
                        reentrantLock.unlock();
                    }


                    if(recordRv>sendSyA.view||recordRv+2>sendSyA.view+2){
                        if(sendSyB.view==sendSyA.view){
                            if(sendSyA!=sendSy){
                                if(sendSyA.w==sendSyA.array.length){
                                    sendSyA.reSet(true);
                                }
                            }
                        }else {
                            if(sendSyB!=sendSy){
                                if(sendSyB.w==sendSyB.array.length){
                                    sendSyB.reSet(true);
                                }
                            }
                        }
                    }
                    if(recordRv>sendSyB.view||recordRv+2>sendSyB.view+2){
                        if(recordR==sendSyA.aByte&&sendSyB!=sendSy){
                            sendSyB.reSet(true);
                        }
                    }
//                    if (recSyA.isOver()){
//                        recSyA.reSet() ;
//                    }
//                    if (recSyB.isOver()){
//                        recSyB.reSet() ;
//                    }

                    Thread.sleep(10 * time);
                    frist=true;
                } catch (InterruptedException e) {
//                    System.out.println("DataStreamReThread Interrupt");
                } catch (IllegalAccessError e) {
                    System.out.println("DataStreamReThread Ill");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean add(byte[] o) {
        try {
            if (o[0]==0){
                DataRCon rCon =null;
                if (o[1]== -1){
                    rCon=recSyA;
                }else {
                    rCon=recSyB;
                }
                synchronized (rCon){
                    if (o[2]==rCon.view){
                        rCon.add(o, o[3]+128);
//                senders.send(new byte[]{1,o[0],1, (byte) (rCon.w-128)});
                        reentrantLock.lock();  // ✅ 获取锁
                        try {
                            conditionR.signalAll(); // ✅ 安全唤醒
                        } finally {
                            reentrantLock.unlock(); // ✅ 释放锁
                        }
                    }else {
                        //todo
                    }
                }
                return false;
            }
            if (o[0]==-1){//1则进入rev;内容为发送的数据
                blockingQueueRev.add(o);
                reThread.interrupt();
            }else {
                blockingQueueSend.add(o);
                reThread.interrupt();
            }
            return false;
        }catch (Exception e){
            if (o==null || o.length==0){
                userContext.deltask(id);
                aBoolean=false;
            }
            return false;
        }

    }
    public void add(DatagramPacket packet) {
        add(Arrays.copyOfRange(packet.getData(), 6, packet.getLength()));
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
        senders.send0(new byte[0]);
        int i=5;
        while (i>=0){
            synchronized (this){
                try {
                    wait(1000);
                    senders.send0(new byte[0]);
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

    public class DataWCon{
        public DataWCon(byte b){
            aByte=b;
        }
        public byte[][] array = new byte[255][];
        byte aByte;
        public volatile int w=0;
        public volatile int r=0;

        public byte view=0;
        public boolean isFull(){
            return w==array.length;
        }
        public boolean isClear(){
            return r==w&&w==array.length;
        }

        public synchronized boolean reSet(){
            if (r!=w)return false;
            r=0;
            w=0;
            Arrays.fill(array, null);
            System.out.println("reSetW: "+aByte+" "+ view);
            view++;
            return true;
        }
        public synchronized boolean reSet(boolean b){
            r=0;
            w=0;
            Arrays.fill(array, null);
            System.out.println("reSetWb: "+aByte+" "+ view);
            view++;
            return true;
        }

        public synchronized void write(Senders senders,byte[] bytes){
            byte[] data = Utils.byteMerger(new byte[]{0,aByte,view, (byte) (w-128)},bytes);
            write(bytes);
            senders.send(data);
        }

        public void write(byte[] bytes){
            array[w]=bytes;
            w++;
        }

        public synchronized byte[] get(int pos){
            return array[pos];
        }

        public synchronized boolean setNull(int pos){
            if(pos>w)return false;
            if(pos>=r){
                for (int p=r;p<=pos;p++,r=p){
                    array[p]=null;
//                    r=p+1;
                }
            }
            return true;
        }
    }

    public class DataRCon{
        public DataRCon(byte b){
            aByte=b;
        }
        public byte[][] array = new byte[255][];
        byte aByte;
        public volatile int w=0;
        public volatile int r=0;

        public byte view=0;

        public boolean isEmpty(){
            return w==r;
        }

        public boolean isOver(){
            return r==w&&w==array.length;
        }

        public synchronized boolean reSet(){
            if (r!=w)return false;
            r=0;
            w=0;
            Arrays.fill(array, null);
            System.out.println("reSetR: "+aByte+" "+ view);
            view++;
            return true;
        }


        public synchronized void add(byte[] bytes,int pos){
            if(pos>=r){
                array[pos]=bytes;
            }
            while (true) {
                if (w >= array.length) break;
                if (array[w] ==null) break;
                w++;
            }
        }

        public synchronized byte[] get(){
            byte[] bytes = array[r];
//            array[r]=null;
            if (bytes!=null){
                senders.send(new byte[]{1,aByte,0, (byte) (r-128),view});
                r++;
                return bytes;
            }
            return null;
        }
    }
}

