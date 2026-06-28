package superlink.udpbind.client.recives.datalen.dataAsy;

import superlink.udpbind.client.recives.ByteBufer;
import superlink.udpbind.client.recives.DataReCallBuffer;
import superlink.util.Utils;

import java.util.Arrays;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.CRC32;

public class ReCallCon implements DataReCallBuffer.call {
    public long time = System.currentTimeMillis();
    public int allPage;
    public volatile int posW;
    public volatile int posR;
    public DataWriter writer;
    public byte[][] bytess;
    public volatile int[] bytel;
    CRC32 crc32 = new CRC32();
    boolean s;
//    volatile boolean read = false;
    AtomicBoolean read = new AtomicBoolean(false);
    public ByteBufer queue;
    public AtomicInteger atomicInteger=new AtomicInteger(0);

    public ReCallCon(int size, int fp, int bl) {
//        atomicInteger.getAndIncrement()
        allPage = size / fp;
        int l = size % fp;
        if (l != 0) {
            allPage = allPage + 1;
        }
        if (allPage < bl) {
            bytess = new byte[allPage][];
            bytel = new int[allPage];
        } else {
            bytess = new byte[bl][];
            bytel = new int[bl];
        }
        for (int i = 0; i < bytess.length; i++) {
            bytess[i] = new byte[fp];
        }
    }

    public void add(byte[] bytes, int pos, int len) {
        if (len < 4) {
            queue.add(Arrays.copyOfRange(bytes, pos, len + pos));
            return;
        }
        s = true;
        int index = Utils.byteArrayToInt(bytes, pos);
        int i = index % bytess.length;
        if (bytel[i] == 0) {
            crc32.reset();
            crc32.update(bytes, pos, len - 4);
            int c = (int) crc32.getValue();
            int c0 = Utils.byteArrayToInt(bytes, pos + len - 4);
            if (c != c0) {
                return;
            }

            try {
                if (read.compareAndSet(false,true)) {
                    if(index == posR){
                        bytel[i] = len - 4 - 4;
                        writer.add(bytes, pos + 4, len - 4 - 4);
//                        System.out.println("readed add");
                        posR++;
                        bytel[i] = 0;
                        if (posW < posR) {
                            posW=posR;
                        }
                    }else {
                        if(index<posR)return;
                        if(index-posR>bytess.length-1)return;
                        if(bytel[i]>0)return;
                        System.arraycopy(bytes, pos + 4, bytess[i], 0, len - 4 - 4);
                        bytel[i] = len - 4 - 4;
                        if (posW <= index) {
                            posW = index+1;
                        }
                    }
//                    read=true;
//                    synchronized (this){

//                    read=false;
//                    }
                }else {
                    System.out.println("cannt get lock");
                }
            }catch (Exception e){
                e.printStackTrace();

            }finally {
                read.set(false);
            }

        }
    }

    public void read() {
//        if(read){
//            return;
//        }

        try {
            if (read.compareAndSet(false, true)) {
//            synchronized (this){
                for (int p = posR; p < posW; p++) {
                    int index = posR % bytel.length;
                    if (bytel[index] > 0) {
                        writer.add(bytess[index], 0, bytel[index]);
                        bytel[index] = 0;
                        posR++;
                    } else {
                        return;
                    }
                }
            }
//            if(posW==posR){
//                int index = posR % bytel.length;
//                bytel[index] = 0;
//            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            read.set(false);
        }

    }

    public boolean gets() {
        read();
        try {
            return s;
        } finally {
            s = false;
        }
    }

    public void readQue(ByteBufer queue){
        byte[] bytes;
        while (queue.size()>0){
            bytes=queue.poll();
            add(bytes,0,bytes.length);
        }
    }
}