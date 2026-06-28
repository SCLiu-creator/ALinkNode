package superlink.udpbind.client.recives.data.stream1;

import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.ByteBufer;
import superlink.udpbind.client.recives.Senders;
import superlink.util.Utils;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

//不使用
public class QueueStreamSt<E> implements BlockingQueue<E> {
    public static Map<String,QueueStreamSt> map=new HashMap<>();

    public static int defsize=12;

    private final UserContext userContext;
    private final short id;
    private final Senders senders;
    ArrayList list;
    byte[][] bytes;
    int cap;
    public int pos=0;
    public int posl=0;
    ByteBufer writeCheakBlockingQueue;
    public QueueStreamSt(String username,int cap) throws Exception {
        this.cap=cap;
        this.list=new ArrayList(cap);
        bytes=new byte[cap][];
        System.out.println("revor");
        this.userContext = UDPclient.mainDataQueue.getUserContext(username);
        this.id = userContext.newQueue((ByteBufer) this);
        this.senders = new Senders().Init(this.id, username);
    }

    public QueueStreamSt(String username,int cap, int id) throws Exception {
        this.cap=cap;
        this.list=new ArrayList(cap);
        bytes=new byte[cap][];
        System.out.println("sendor");
        this.userContext = UDPclient.mainDataQueue.getUserContext(username);
        this.id = (short) id;
        writeCheakBlockingQueue =userContext.getDataQue(this.id);
        this.senders = new Senders().Init(this.id, username);
        this.re();
    }

    public void build(){
        String s="Qt"+id;
        this.senders.sendSym(s.getBytes());
//        this.senders.sendSym(s.getBytes());
        this.thread=Thread.currentThread();
    }
    public void build1(){
        String s="QT"+id;
        this.senders.sendSym(s.getBytes());
//        this.senders.sendSym(s.getBytes());
        this.thread=Thread.currentThread();
    }
    public void build2(){
        String s="TQ"+id;
        this.senders.sendSym(s.getBytes());
//        this.senders.sendSym(s.getBytes());
        this.thread=Thread.currentThread();
    }

    ReentrantReadWriteLock.ReadLock readLock=new ReentrantReadWriteLock().readLock();

    @Override
    public boolean add(E o) {
        int s=((byte[]) o)[1]+128;
//        if (s<pos){
//            return true;
//        }
//        if (pos<posl){
//            if (s<posl){
//                bytes[s]= (byte[]) o;
//                pos=s;
//            }
//            return true;
//        }else {
//            if (s>=posl){
//                bytes[s]= (byte[]) o;
//                pos=s;
//            }
//        }
//        pos=s;
        if (bytes[s]==null){
            bytes[s]= (byte[]) o;
        }
        this.thread.interrupt();

        return true;
    }

    byte[] bytesr;
    byte[] sendbytes=new byte[2];
    public byte[] read(){
        bytesr=bytes[posl];
        if (bytesr==null){
            sendbytes[0]=0;
            sendbytes[1]= (byte)(posl-128) ;
            senders.send(sendbytes);
        }else {
            bytes[posl]=null;
            sendbytes[0]=-128;
            sendbytes[1]= (byte)(posl-128) ;
            senders.send(sendbytes);
        }
        pos++;
        posl++;
        if (posl==cap){
            posl=0;
            pos++;
        }
        return Arrays.copyOfRange(bytesr,2,bytesr.length);
    }
    long timeout=3000;
    int times=1;
    int s=1;
    public byte[] synread(){
        while (true){
            bytesr=bytes[posl];

            if (bytesr==null){
                sendbytes[0]=0;
                sendbytes[1]= (byte)(posl-128) ;
                senders.send(sendbytes);
                try {
                    if (s%3==0){
                        times++;
                    }
                    synchronized (this){
                        this.wait(timeout*times);
                    }
                    s++;

                } catch (Exception e) {
                    System.out.println("synread ng");
//                    Thread.interrupt();
                }
                continue;
            }else {
                times=1;
                s=1;
                //System.out.println("   " +"   "+bytesr[0]+ "    "+byteArrayToInt1(bytesr));
                if (bytesr[0]==126){
                    bytes[posl]=null;
                    sendbytes[0]=126;
                    sendbytes[1]= 0 ;
                    senders.send(sendbytes);
                    pos=0;
                    posl=0;
                    return new byte[2];
                }
                bytes[posl]=null;
                sendbytes[0]=-128;
                sendbytes[1]= (byte)(posl-128) ;
                senders.send(sendbytes);
            }
            pos++;
            posl++;
            if (posl==cap){
                posl=0;
                pos=0;
                bytes[posl]=null;
                sendbytes[0]=127;
                sendbytes[1]= (byte)(posl-128) ;
                senders.send(sendbytes);
            }
            return bytesr;
        }


    }

    ReentrantLock lock=new ReentrantLock();
    Condition condition=lock.newCondition();

    public void reset(){
        writebytes[0]=0;writebytes[1]=-128;
        posl=0;
        pos=0;
        bytes=new byte[cap][];
        state=true;
        //thread=Thread.currentThread();
    }

    byte[] writebytes=new byte[2];

    public void synWrite(byte[] bytes){
        if (this.bytes[posl]!=null){
            if (pos>=p1){
                lock.lock();
                try {
                    condition.await();
                    System.out.println("voerRiite");
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    lock.unlock();
                }
            }

           // re();
        }
        pos=posl;
//        writebytes=intToByteArray1(writebytes,pos);
        writebytes[0]= 0;
        writebytes[1]= (byte) (pos-128);
        byte[] send=Utils.byteMerger(writebytes,bytes);
        this.bytes[posl]=send;
        senders.send(send);
        posl++;
        pos++;
        if (posl==cap){
            posl=0;
        }
    }

    boolean state=true;
    public void over(){
        writebytes[1]= (byte) pos;
        if (this.bytes[posl]!=null){
         //   re1();
            lock.lock();
            try {
                condition.await();
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                lock.unlock();
            }
        }
        state=false;
        writebytes[0]=126;
        writebytes[1]=(byte) (pos-128);
        this.bytes[posl]=writebytes;
        senders.send(writebytes);
        posl=0;

    }

    boolean re=true;
    int p1=0;
    public Thread thread;
    public Thread rcthread;
    public void re(){
        if (this.rcthread!=null){
            return;
        }
        this.rcthread= new Thread(()->{
            Thread.currentThread().setName("queueSteamReCheak");
            QueueStreamSt QueueStreamSt=this;
            int i=0;
            while (re){

                try {
                    byte[] re= (byte[]) writeCheakBlockingQueue.take();
//                    if (re==null){
//                        break;
//                    }
//                    System.out.println("   "+i++ +"   "+re[0]+ "    "+re[1]);
                    if(re[0]==0){
//                        if (lock.getQueueLength()>0){
//
//                        }
                        try {
                            byte[] send=bytes[re[1]+128];
                            if (send==null){
                                if(state==false){
                                    re[0]=126;
                                    re[1]=(byte) (pos-128);
                                    senders.send(re);
                                }
                                continue;
                            }
                            senders.send(send);
                        }catch (Exception e0){
                            System.out.println("(QueueStreamSt)  re"+re.length);
                            System.out.println("(QueueStreamSt)  "+Arrays.toString(re));
                        }


                    }else if (re[0]==-128){
                        synchronized (QueueStreamSt){
                            bytes[re[1]+128]=null;
                            p1=re[1]+128;

                        }
                    }else if (re[0]==127){
                        synchronized (QueueStreamSt){
//                            bytes=new byte[cap][];
//                            pos=0;
//                            posl=0;
                            lock.lock();
                            try {
                                condition.signalAll();
                            }catch (Exception e){
                                e.printStackTrace();
                            }finally {
                                lock.unlock();
                            }

                        }
                    }
                    else if(re[0]==126){
                        break;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        );
        rcthread.start();
    }
    public void re1(){
//        this.thread=
//                new Thread(()->{
//            Thread.currentThread().setName("queueSteamReCheak");
        QueueStreamSt QueueStreamSt=this;
        int i=0;
        while (re){

            try {
                byte[] re= (byte[]) writeCheakBlockingQueue.take();

                System.out.println("   "+i++ +"   "+re[0]+ "    "+re[1]);
                if(re[0]==0){
                    if (lock.getQueueLength()>0){

                    }
                    byte[] send=bytes[re[1]+128];
                    if (send==null){
                        continue;
                    }
                    senders.send(send);
                }else if (re[0]==-128){
                    synchronized (QueueStreamSt){
                        bytes[re[1]+128]=null;
                    }
                }
                else if(re[0]==126){
                        break;
                    }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }










    @Override
    public boolean offer(E o) {
        return false;
    }

    @Override
    public E remove() {
        return null;
    }

    @Override
    public E poll() {
        return null;
    }

    @Override
    public E element() {
        return null;
    }

    @Override
    public E peek() {
        return null;
    }

    @Override
    public void put(E o) throws InterruptedException {

    }

    @Override
    public boolean offer(E o, long timeout, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public E take() throws InterruptedException {
        return null;
    }

    @Override
    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        return null;
    }

    @Override
    public int remainingCapacity() {
        return 0;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean addAll(Collection c) {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public boolean retainAll(Collection c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection c) {
        return false;
    }

    @Override
    public boolean containsAll(Collection c) {
        return false;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @Override
    public Iterator iterator() {
        return null;
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public Object[] toArray(Object[] a) {
        return new Object[0];
    }

    @Override
    public int drainTo(Collection c) {
        return 0;
    }

    @Override
    public int drainTo(Collection c, int maxElements) {
        return 0;
    }

    public static int byteArrayToInt1(byte[] bytes) {
        int value = 0;
        value += (bytes[1] & 0xFF) << 24;
        value += (bytes[2] & 0xFF) << 16;
        value += (bytes[3] & 0xFF) << 8;
        value += (bytes[4] & 0xFF) ;
        return value;
    }
    public static byte[] intToByteArray1(byte[] bytes,int i) {

        bytes[1] = (byte) ((i >> 24) & 0xFF);
        bytes[2] = (byte) ((i >> 16) & 0xFF);
        bytes[3] = (byte) ((i >> 8) & 0xFF);
        bytes[4] = (byte) (i & 0xFF);
        return bytes;
    }

    @Override
    public int hashCode(){
        return userContext.getBothId()+id;
    }

}
