package superlink.udpbind.client.recives;

import superlink.util.Utils;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ServerQueue implements ByteBufer {
    Node[] items;

    int takeIndex;

    int putIndex;

    int count;

    ReentrantLock lock;

    private Condition notEmpty;

    private Condition notFull;

    public Node reNode;

    public class Node{
        public InetAddress inetAddress;
        public int port;
        public byte[] bytes;
        public int len;
        public Node(int len){
            bytes=new byte[len];
        }
        public Node(){
            bytes=new byte[65536];
        }
        public void clear(){
            inetAddress=null;
            port=0;
        }
        public void write(Node node){
            System.arraycopy(bytes,0,node.bytes,0,len);
            node.inetAddress=inetAddress;
            node.port=port;
            node.len=len;
            clear();
        }

        public void read(byte[] bytes,int len){
            System.arraycopy(bytes,0,this.bytes,0,len);
            clear();
        }
        @Override
        public String toString() {
            String st="null";
            try {
                st=new String(bytes);
            }catch (Exception e){
                e.printStackTrace();
            }

            return "ServerOrg{" +
                    "inetAddress=" + inetAddress +
                    ", port=" + port +
                    ", bytes=" + Arrays.toString(Utils.subByte(bytes,0,len)) +
                    ",byteString="+st+
                    '}';
        }
    }

    private static void checkNotNull(Object v) {
        if (v == null)
            throw new NullPointerException();
    }

    /**
     * Inserts element at current put position, advances, and signals.
     * Call only when holding lock.
     */
    private Node enqueue(byte[] x) {
        Node[] items = this.items;
        Node node=items[putIndex];
        node.bytes= x;
        if (++putIndex == items.length)
            putIndex = 0;
        count++;
        notEmpty.signal();
        return node;
    }
    private Node enqueue(byte[] x,int len) {
        Node[] items = this.items;
        Node node=items[putIndex];
        node.read(x,len);
        if (++putIndex == items.length)
            putIndex = 0;
        count++;
        notEmpty.signal();
        return node;
    }

    /**
     * Extracts element at current take position, advances, and signals.
     * Call only when holding lock.
     */
    private Node dequeue(Node buffer) {
        Node[] items = this.items;
        Node x = items[takeIndex];
        x.write(buffer);
        if (++takeIndex == items.length)
            takeIndex = 0;
        count--;
        notFull.signal();
        return x;
    }


    public ServerQueue(int capacity) {
        this(capacity, false);
    }

    public ServerQueue(int capacity, boolean fair) {
        if (capacity <= 0)
            throw new IllegalArgumentException();
        this.items = new Node[capacity];
        for (int i = 0; i < this.items.length; i++) {
            this.items[i]=new Node();
        }
        reNode=new Node();

        lock = new ReentrantLock(fair);
        notEmpty = lock.newCondition();
        notFull = lock.newCondition();
    }

    public void add(DatagramPacket packet) {
        byte[] bytes=Arrays.copyOfRange(packet.getData(), 6, packet.getLength());
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            if (count == items.length){
                //todo
            } else {
                Node node=enqueue(bytes,packet.getLength()-6);
                node.inetAddress=packet.getAddress();
                node.port=packet.getPort();
                node.len=packet.getLength()-6;
            }
        } finally {
            lock.unlock();
        }
    }


    public boolean add(byte[] e) {
        if (offer(e))
            return true;
        else
            throw new IllegalStateException("Queue full");
    }


    public boolean offer(byte[] e) {
        throw new IllegalStateException("Queue full");
    }

    public void put(byte[] e) throws InterruptedException {
        checkNotNull(e);
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            while (count == items.length)
                notFull.await();
            enqueue(e);
        } finally {
            lock.unlock();
        }
    }


    public boolean offer(byte[] e, long timeout, TimeUnit unit)
            throws InterruptedException {
        checkNotNull(e);
        long nanos = unit.toNanos(timeout);
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            while (count == items.length) {
                if (nanos <= 0)
                    return false;
                nanos = notFull.awaitNanos(nanos);
            }
            enqueue(e);
            return true;
        } finally {
            lock.unlock();
        }
    }

    public byte[] poll() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        Node node=new Node();
        node.bytes=new byte[65536];
        try {
            return (count == 0) ? null : dequeue(node).bytes;
        } finally {
            lock.unlock();
        }
    }
    public Node pollNode(Node buffer) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return (count == 0) ? null : dequeue(buffer);
        } finally {
            lock.unlock();
        }
    }

    public byte[] take() throws InterruptedException {
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        Node node=new Node();
        node.bytes=new byte[65536];
        try {
            while (count == 0)
                notEmpty.await();
            return dequeue(node).bytes;
        } finally {
            lock.unlock();
        }
    }

    public byte[] poll(long timeout, TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        Node node=new Node();
        node.bytes=new byte[65536];
        try {
            while (count == 0) {
                if (nanos <= 0)
                    return null;
                nanos = notEmpty.awaitNanos(nanos);
            }
            return dequeue(node).bytes;
        } finally {
            lock.unlock();
        }
    }


    public int size() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return count;
        } finally {
            lock.unlock();
        }
    }


    public boolean remove(Object o) {
        if (o == null) return false;
        final Object[] items = this.items;
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            if (count > 0) {
                final int putIndex = this.putIndex;
                int i = takeIndex;
                do {
                    if (o.equals(items[i])) {
                        return true;
                    }
                    if (++i == items.length)
                        i = 0;
                } while (i != putIndex);
            }
            return false;
        } finally {
            lock.unlock();
        }
    }


    public String toString() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            int k = count;
            if (k == 0)
                return "[]";

            final Object[] items = this.items;
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            for (int i = takeIndex; ; ) {
                Object e = items[i];
                sb.append(e == this ? "(this Collection)" : e);
                if (--k == 0)
                    return sb.append(']').toString();
                sb.append(',').append(' ');
                if (++i == items.length)
                    i = 0;
            }
        } finally {
            lock.unlock();
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


}
