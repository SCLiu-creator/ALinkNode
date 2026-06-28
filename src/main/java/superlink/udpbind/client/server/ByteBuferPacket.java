package superlink.udpbind.client.server;

import superlink.udpbind.client.recives.ByteBufer;
import superlink.util.Utils;

import java.net.DatagramPacket;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static superlink.udpbind.client.server.ServerCon.runRead;

public class ByteBuferPacket implements ByteBufer {
    public byte[][] items;

    int fp=1024;
    int takeIndex;

    int putIndex;

    int read=0;

    int count;
    Lock lock=new Lock();

    public class Lock{
        public void lock(){

        }
        public void unlock(){

        }
    }

    public ByteBuferPacket(int capacity) {
        int len=capacity/fp;
        int ba=capacity%fp;
        if (ba!=0){
            len=len+1;
        }
        this.items = new byte[len][];
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
        int pos=Utils.byteArrayToInt(e);
        items[pos]=e;
        lock.unlock();
        count++;
        runRead();
        return true;
    }

    public boolean offer(byte[] e, long timeout, TimeUnit unit)
            throws InterruptedException {
        return false;
    }

    public byte[] poll() {
        byte[] bytes=items[read];
        read++;
        return bytes;
    }

    public byte[] take() throws InterruptedException {
        throw new InterruptedException();
    }

    public byte[] poll(long timeout, TimeUnit unit) throws InterruptedException {
        return null;
    }

    public int size() {
        try {
            return count;
        } finally {

        }
    }

    public byte[] getData(){
        int length = 0;
        int len = 0;
        for (byte[] bytes:items){
            length=length+bytes.length-4;
        }
        byte[] send=new byte[length];
        for (byte[] bytes:items){
            System.arraycopy(bytes,4,send,len,bytes.length-4);
            len=len+bytes.length;
        }
        return send;
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
