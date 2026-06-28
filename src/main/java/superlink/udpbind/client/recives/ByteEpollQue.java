package superlink.udpbind.client.recives;

import superlink.udpbind.client.recives.datalen.DataSyn;
import superlink.udpbind.client.recives.datalen.DsCon;

import java.net.DatagramPacket;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ByteEpollQue implements ByteBufer {
    byte[][] items;

    int takeIndex;

    int putIndex;

    volatile int count;

    DataSyn dataSyn;

    Thread thread;


    private synchronized void enqueue(byte[] x) {
        byte[][] items = this.items;
        items[putIndex] = x;
        if (++putIndex == items.length)
            putIndex = 0;
        count++;
    }

    private synchronized byte[] dequeue() {
        byte[][] items = this.items;
        byte[] x = items[takeIndex];
        items[takeIndex] = null;
        if (++takeIndex == items.length)
            takeIndex = 0;
        count--;
        return x;
    }

    public ByteEpollQue(int capacity,Thread thread,DataSyn dataSyn) {
        this(capacity,dataSyn);
        this.thread=thread;
    }

    public ByteEpollQue(int capacity,DataSyn dataSyn) {
        this.dataSyn=dataSyn;
        if (capacity <= 0)
            throw new IllegalArgumentException();
        this.items = new byte[capacity][];
    }

    @Override
    public void add(DatagramPacket packet) {
        offer(Arrays.copyOfRange(packet.getData(), 6, packet.getLength()));

        if ((System.currentTimeMillis()-DsCon.sleepTime)>3 || DsCon.dataMapBuf.size()>800){
            thread.interrupt();
        }else {
//            if(thread.getState().equals(Thread.State.RUNNABLE)){
//                return;
//            }
            DsCon.dataMapBuf.add(dataSyn);
        }

    }

    @Override
    public boolean add(byte[] e) {
        if (offer(e)){
            return true;
        } else{
            throw new IllegalStateException("Queue full");
        }
    }


    public boolean offer(byte[] e) {
        if (count == items.length)
            return false;
        else {
            enqueue(e);
            return true;
        }
    }


    public boolean offer(byte[] e, long timeout, TimeUnit unit) throws InterruptedException {
        throw new IllegalStateException("Queue offer");
    }

    @Override
    public byte[] poll() {
        return (count == 0) ? null : dequeue();
    }

    @Override
    public byte[] take() throws InterruptedException {
        throw new IllegalStateException("Queue take");
    }

    @Override
    public byte[] poll(long timeout, TimeUnit unit) throws InterruptedException {
        throw new IllegalStateException("Queue poll");
    }

    @Override
    public int size() {
        return count;
    }


    @Override
    public String toString() {
        try {
            int k = count;
            if (k == 0)
                return "[]";

            final Object[] items = this.items;
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            for (Object e :items) {
                sb.append(e == null ? " " : e);
                if (--k == 0){
                    return sb.append(']').toString();
                }
                sb.append(',').append(' ');
            }
            return "[]";
        } catch (Exception e){
            return "Exception";
        }finally {

        }
    }

    @Override
    public void clear() {
        throw new IllegalStateException("Queue clear");
    }

}
