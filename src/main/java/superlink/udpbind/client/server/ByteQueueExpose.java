package superlink.udpbind.client.server;

import superlink.udpbind.client.recives.ByteBufer;

import java.net.DatagramPacket;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class ByteQueueExpose  implements ByteBufer {
    public byte[][] items;

    int takeIndex;

    int putIndex;

    int count;
    Lock lock;

    private ConDit notEmpty;

    private ConDit notFull;
    public class Lock{
        public void lock(){

        }
        public void unlock(){

        }
        public long awaitNanos(long t){
            return t;
        }
    }

    public class ConDit{
        public void signal(){

        }
        public void await(){

        }
        public long awaitNanos(long t){
            return t;
        }
    }

    /**
     * Inserts element at current put position, advances, and signals.
     * Call only when holding lock.
     */
    private void enqueue(byte[] x) {
        byte[][] items = this.items;
        items[putIndex] = x;
        if (++putIndex == items.length)
            putIndex = 0;
        count++;
        notEmpty.signal();
    }

    /**
     * Extracts element at current take position, advances, and signals.
     * Call only when holding lock.
     */
    private byte[] dequeue() {
        byte[][] items = this.items;
        byte[] x = items[takeIndex];
        items[takeIndex] = null;
        if (++takeIndex == items.length)
            takeIndex = 0;
        count--;
        notFull.signal();
        return x;
    }


    public ByteQueueExpose(int capacity) {
        this(capacity, false);
    }

    public ByteQueueExpose(int capacity, boolean fair) {
        if (capacity <= 0)
            throw new IllegalArgumentException();
        this.items = new byte[capacity][];
    }

    public void add(DatagramPacket packet) {
        offer(Arrays.copyOfRange(packet.getData(), 6, packet.getLength()));
    }


    public boolean add(byte[] e) {
        if (offer(e))
            return true;
        else
            throw new IllegalStateException("Queue full");
    }


    public boolean offer(byte[] e) {
        lock.lock();
        try {
            if (count == items.length)
                return false;
            else {
                enqueue(e);
                return true;
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean offer(byte[] e, long timeout, TimeUnit unit)
            throws InterruptedException {
        return false;
    }

    public byte[] poll() {
        lock.lock();
        try {
            return (count == 0) ? null : dequeue();
        } finally {
            lock.unlock();
        }
    }

    public byte[] take() throws InterruptedException {
        return null;
    }

    public byte[] poll(long timeout, TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        lock.lock();
        try {
            while (count == 0) {
                if (nanos <= 0)
                    return null;
                nanos = notEmpty.awaitNanos(nanos);
            }
            return dequeue();
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        try {
            return count;
        } finally {

        }
    }

    public String toString() {
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

            }
        } finally {
            lock.unlock();
        }
    }

}