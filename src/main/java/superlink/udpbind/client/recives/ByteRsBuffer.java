package superlink.udpbind.client.recives;

import com.sun.jmx.remote.internal.ArrayQueue;
import superlink.udpbind.client.UserContext;
import superlink.util.Utils;
import superlink.util.datastack.ArrayQue;
import superlink.util.datastack.LinkQue;

import java.net.DatagramPacket;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

//发送缓冲区,5
public class ByteRsBuffer implements ByteBufer {
    byte[][] items;

    UserContext userContext;
    ArrayQue<Short> arrayQue=new ArrayQue<Short>(64);

    int takeIndex=0;

    int putIndex=0;

    int count=0;

    LinkQue<byte[]> linkedList=new LinkQue();

    short capacity= 64;//Short.MAX_VALUE;

    public ReentrantLock lock;
    public Condition notEmpty;
    public Condition notFull;


    private void enqueue(byte[] x) {
        byte[][] items = this.items;
        int p=putIndex%items.length;
        items[p] = x;
        send((short) putIndex,x);
        if (++putIndex == capacity)
            putIndex = 0;
        count++;
        notEmpty.signal();
    }

    public void send(short s,byte[] bytes){
        bytes=Utils.byteMerger(
                Utils.intToByteArray(userContext.getBothId()),
                Utils.shortToByteArray((short) 7),
                Utils.shortToByteArray(s),
                bytes);
        Senders.Sends(userContext.inetAddress,userContext.port,bytes);
    }

    private byte[] dequeue() {
        byte[][] items = this.items;
        int pos=takeIndex%items.length;
        byte[] x = items[pos];
        items[pos] = null;
        if (++takeIndex == capacity)
            takeIndex = 0;
        count--;
        notFull.signal();
        return x;
    }

    public ByteRsBuffer(int ca) {
//        if(ca%16!=0){
//            ca=128;
//        }
        this.items = new byte[16][];
        lock = new ReentrantLock(false);
        notEmpty = lock.newCondition();
        notFull = lock.newCondition();
    }
    public ByteRsBuffer setUser(UserContext user){
        userContext=user;
        return this;
    }

    public void add(DatagramPacket packet) {
        byte[] bytes = packet.getData();
        byte b= (byte) (bytes[8]&bytes[9]);
        short s= Utils.byteArrayToshort(bytes,6);
        if (b==0){
            in(s);
        }else {
            arrayQue.adde(s);
        }
    }

    public boolean wit(byte[] bytes){
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            if (count >= (items.length-1)) {
                linkedList.add(bytes);
                return false;
            }else {
                enqueue(bytes);
                return true;
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean in(short i){
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            if(i==putIndex){
                return false;
            }
            //if(takeIndex>putIndex&&putIndex<items.length)
            if (i>putIndex && putIndex<takeIndex) {
                if(i>=takeIndex){
                    int start = takeIndex;
                    for (; i >=start && count>0 ; start++) {
                        dequeue();
                    }
                    return true;
                }
                return false;
            }else {
                int end=i;
                int start = takeIndex;
                if (putIndex<takeIndex){
                    end=i+capacity;
                }
                for (; end >=start && count>0 ; start++) {
                    dequeue();
                }
                return true;
            }
        } finally {
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

    int record = 0;
    int recordTimes = 0;
    //"rr"
    byte[] rr=new byte[]{'r','r'};
    public byte[] poll() {
//        System.out.println("rs :take:"+takeIndex+"  put:"+putIndex+"  count:"+count+"  size:"+size());
        final ReentrantLock lock = this.lock;
        if (lock.isLocked()){
            System.out.println(lock);
            System.out.println("waitQue: "+lock.getQueueLength());
            return null;
        }
        lock.lock();
        try {
            while (count != items.length&&linkedList.size()!=0) {
                byte[] bytes= linkedList.poll();
                enqueue(bytes);
            }
            if (count!=0 && arrayQue.size()==0){
//                int pos=putIndex-1;
//                if(pos<0){
//                    pos=pos+capacity;
//                }
                int p=takeIndex%items.length;
                if(items[p]!=null){
                    send((short) takeIndex,items[p]);
                }
//                p=putIndex%items.length;
//                if(items[p]!=null){
//                    send((short) putIndex,items[p]);
//                }
//                if(count>0){
//                    record = takeIndex;
//                }
            }

            if (record == takeIndex){
                recordTimes+=1;
            }else {
                recordTimes = 0;
            }

            while (arrayQue.size()!=0){
                short i= arrayQue.poll();
                if(putIndex<takeIndex){
                    if(i<putIndex || i>=takeIndex){
                        int p=i%items.length;
                        send(i,items[p]);
                    }else {
                        if(count>0){
                            record = takeIndex;
                        }else {
                            record = -1;
                        }
//                        send(i,new byte[0]);
                    }
                }else {
                    if(i<putIndex && i>=takeIndex){
                        int p=i%items.length;
                        send(i,items[p]);
                    }else {
                        if(count>0){
                            record = takeIndex;
                        }else {
                            record = -1;
                        }
//                        send(i,new byte[0]);
                    }
                }
            }

            if (recordTimes>=8&&recordTimes%8==0){
                byte[] bytes = rr;
                bytes=Utils.byteMerger(bytes,Utils.intToByteArray(takeIndex));
                userContext.send((short) 0,bytes);
            }
        } finally {
            lock.unlock();
        }

        return null;
    }

    public boolean add(byte[] e) {
        return wit(e);
    }

    public int size() {
        return count+linkedList.size();
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

    /**
     * Atomically removes all of the elements from this queue.
     * The queue will be empty after this call returns.
     */
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

    public ArrayList<byte[]> getAll(){
        final ReentrantLock lock = this.lock;
        lock.lock();
        ArrayList<byte[]> arrayList=new ArrayList();
        try {
            final int putIndex = this.putIndex;
            int i = takeIndex;
            do {
                arrayList.add(items[i]) ;
                if (++i == items.length)
                    i = 0;
            } while (i != putIndex);
            while (linkedList.size()>0){
                arrayList.add(linkedList.poll());
            }
            return arrayList;
        } finally {
            lock.unlock();
        }
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
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("finalize :rs :take:"+takeIndex+"  put:"+putIndex +"  count: "+count);
    }
}