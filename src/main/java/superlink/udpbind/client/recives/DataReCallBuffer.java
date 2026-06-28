package superlink.udpbind.client.recives;

import superlink.udpbind.client.recives.datalen.AutoAsyBuffer;
import superlink.udpbind.client.recives.datalen.dataAsy.ReCallCon;

import java.net.DatagramPacket;
import java.util.concurrent.TimeUnit;

public class DataReCallBuffer implements ByteBufer {

    byte[][] items;

    public call reCallCon;

    public DataReCallBuffer() {
    }
    public DataReCallBuffer setCall(ReCallCon callCon){
        this.reCallCon=callCon;
        return this;
    }

    public void add(DatagramPacket packet) {
        offer(packet.getData(), 6, packet.getLength()-6);
    }


    public boolean add(byte[] e) {
        if (offer(e,0,e.length))
            return true;
        else
            System.out.println("Queue full");
            return false;
//            throw new IllegalStateException("Queue full");
    }


    public boolean offer(byte[] e,int pos,int len) {
        try {
          reCallCon.add(e,pos,len);
        } finally {
            return true;
        }

    }

    public byte[] poll() {
        return null;
    }

    public byte[] take() throws InterruptedException {
        return null;
    }

    public byte[] poll(long timeout, TimeUnit unit) throws InterruptedException {
        return null;
    }


    public int size() {
        return 0;
    }

    public static interface call{
        void add(byte[] bytes, int pos, int len);
    };


    public String toString() {
        try {
            return reCallCon.toString();
        } finally {

        }
    }

    /**
     * Atomically removes all of the elements from this queue.
     * The queue will be empty after this call returns.
     */
    public void clear() {
        try {

        } finally {

        }
    }

}