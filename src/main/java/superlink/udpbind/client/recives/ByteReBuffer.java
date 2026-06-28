package superlink.udpbind.client.recives;

import superlink.udpbind.client.UserContext;
import superlink.util.Utils;
import superlink.util.datastack.LinkQue;

import java.net.DatagramPacket;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

//接收缓冲区,7
public class ByteReBuffer implements ByteBufer {
    byte[][] items;

    UserContext userContext;

    int takeIndex;

    int putIndex;

    int count;

    LinkQue<node> linkedList=new LinkQue();

    short capacity= 64;//Short.MAX_VALUE;

    public ReentrantLock lock;
    public Condition notEmpty;
    public Condition notFull;

    public void send(short s){
        byte[] bytes=Utils.byteMerger(
                Utils.intToByteArray(userContext.getBothId()),
                Utils.shortToByteArray((short) 5),
                Utils.shortToByteArray(s),
                new byte[]{0,0});
        Senders.Sends(userContext.inetAddress,userContext.port,bytes);
    }
    //00
    public void reqdata(){
        System.out.println("reqdata: "+takeIndex);
        byte[] bytes=Utils.byteMerger(
                Utils.intToByteArray(userContext.getBothId()),
                Utils.shortToByteArray((short) 5),
                Utils.shortToByteArray((short) takeIndex),
                new byte[]{111,111});
        Senders.Sends(userContext.inetAddress,userContext.port,bytes);
    }

    private void enqueue(byte[] x) {
        byte[][] items = this.items;
        int pos=putIndex%items.length;
        items[pos] = x;
        if (++putIndex == capacity)
            putIndex = 0;
        count++;
        notEmpty.signal();
    }
    private void enqueue(int i,byte[] x) {
        byte[][] items = this.items;
        int pos=i%items.length;
        if(items[pos]!=null){
            return;
        }
        items[pos] = x;
        if (++putIndex == capacity)
            putIndex = 0;
        count++;
        notEmpty.signal();
    }

    private byte[] dequeue() {
        byte[][] items = this.items;
        int tx=takeIndex;
        int p=tx%items.length;
        byte[] x = items[p];
        if (x==null){
            return null;
        }
        items[p] = null;
        send((short) tx);
        if (++takeIndex == capacity)
            takeIndex = 0;
        count--;
        notFull.signal();
        return x;
    }


    public ByteReBuffer(int ca) {
//        if(ca%16!=0){
//            ca=128;
//        }
        this.items = new byte[16][];
        lock = new ReentrantLock(false);
        notEmpty = lock.newCondition();
        notFull = lock.newCondition();
    }
    public ByteReBuffer setUser(UserContext user){
        userContext=user;
        return this;
    }


    public void add(DatagramPacket packet) {
        byte[] bytes=packet.getData();
        in(Utils.byteArrayToshort(bytes,6),
                Arrays.copyOfRange(bytes, 8, packet.getLength()));
    }

    public boolean in(short i,byte[] bytes){
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return in0(i,bytes);
        } finally {
            lock.unlock();
        }
    }
    public boolean in0(short i,byte[] bytes){
        if ((putIndex-i)>items.length ||(i-putIndex)>items.length){
            if((i<takeIndex&&i>takeIndex-items.length)||(i>takeIndex&&(capacity-i+takeIndex)<items.length)){
                linkedList.add(new node(i,bytes));
            }

//                    if (count==0&&linkedList.size()==0){
//                        if(putIndex==0&&takeIndex==0&&i<capacity&&i>=0){
//                            putIndex=i;
//                            takeIndex=i;
//                        }
//                    }else {
//                        return false;
//                    }
            return false;
        }

        if(i<takeIndex){
            if(i>takeIndex-items.length){
                linkedList.add(new node(i,bytes));
            }
            if (takeIndex>=(capacity-items.length)){
                if((i+capacity-takeIndex)>=items.length){
                    return false;
                }
            }else {
                return false;
            }
            if(i>=items.length-(capacity-takeIndex)){
                return false;
            }
        }else {
            if((capacity-i+takeIndex)<items.length){
                linkedList.add(new node(i,bytes));
            }
            if(i-takeIndex>=items.length){
                return false;
            }
        }
        if (items[i%items.length]!=null){
            return false;
        }
        if (count == (items.length)) {
//            linkedList.add(new node(i,bytes));
            return false;
        }else {
            enqueue(i,bytes);
            return true;
        }
    }

    public byte[] poll() {
//        System.out.println("RE:take:"+takeIndex+"  put:"+putIndex+"  count:"+count);
        final ReentrantLock lock = this.lock;
        if (lock.isLocked()){
            System.out.println(lock);
            System.out.println("waitQue  :"+lock.getQueueLength());
            return null;
        }
        lock.lock();
        try {
            while (count<items.length&linkedList.size()>0){
                node nd=linkedList.poll();
                send((short) nd.pos);
//                if(nd!=null){
//                    in0((short) nd.pos,nd.bytes);
//                }
            }
            return (count == 0) ? null : dequeue();
        } finally {
            lock.unlock();
        }
    }
    public class node{
        public node(int pos,byte[] bytes){
            this.pos=pos;
            this.bytes=bytes;
        }
        int pos;
        byte[] bytes;
    }

    long waitTime=System.currentTimeMillis();
    int record=-1;
    int frequncy=0;
    public boolean cheak(long time){
        long l1= System.currentTimeMillis();
        if(record==takeIndex){
            frequncy++;
        }else {
            record=takeIndex;
            frequncy=0;
            waitTime = l1;
        }
        if(frequncy>7){
            frequncy=0;
            if((l1-waitTime)>time){
                waitTime = l1;
                return true;
            }
        }
        return false;
    }

    public void reSet(int pos){
        final ReentrantLock lock = this.lock;
        if (lock.isLocked()){
            System.out.println(lock);
            System.out.println("waitReSet  :"+lock.getQueueLength());
        }
        lock.lock();
        try {
            if(isIn(pos)){
                return;
            }

            if(pos==takeIndex){
                return;
            }

            if(takeIndex == putIndex){
                putIndex = pos;
                takeIndex = putIndex;
            }else {
//                if (takeIndex<pos){
                    putIndex = pos;
                    takeIndex = putIndex;
                    count=0;
//                }
            }
            Arrays.fill(items, null);
            count=0;
        } finally {
            record=-1;
            lock.unlock();
        }
    }

    public boolean isIn(int pos){
        if(takeIndex==putIndex){
            if(count==0){
                return false;
            }else {
                return true;
            }
        }
        if(takeIndex<putIndex){
            if(pos>=takeIndex&&pos<putIndex){
                return true;
            }else {
                return false;
            }
        }else {
            if(pos<putIndex || pos>=takeIndex){
                return true;
            }else {
                return false;
            }
        }
    }

    public boolean add(byte[] e) {
        throw new IllegalStateException("Queue full");
    }

    public boolean offer(byte[] e) {
        throw new IllegalStateException("Queue full");
    }

    public boolean offer(byte[] e, long timeout, TimeUnit unit)
            throws InterruptedException {
        throw new IllegalStateException("Queue full");
    }
    public byte[] take() throws InterruptedException {
        throw new IllegalStateException("Queue full");
    }

    public byte[] poll(long timeout, TimeUnit unit) throws InterruptedException {
        throw new IllegalStateException("Queue full");
    }

    public int size() {
        return count;
    }


    public String toString() {
        int k = count;
        if (k == 0)
            return "[]";
        final Object[] items = this.items;
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = takeIndex; ; ) {
            Object e = items[i];
            if(e==null){
                e="None";
            }
            sb.append(e);
            if (--k == 0)
                return sb.append(']').toString();
            sb.append(',').append(' ');
            if (++i == items.length)
                i = 0;
        }
    }


    public void clear() {
        final Object[] items = this.items;
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            int k = count;
            if (k > 0) {
                final int putIndex = this.putIndex;
                int i = takeIndex;
                do {
                    items[i] = null;
                    if (++i == items.length)
                        i = 0;
                } while (i != putIndex);
                takeIndex = putIndex;
                count = 0;
                for (; k > 0 && lock.hasWaiters(notFull); k--)
                    notFull.signal();
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("finalize :re :take:"+takeIndex+"  put:"+putIndex +"  count: "+count);
    }
}