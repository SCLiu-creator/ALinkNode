package superlink.udpbind.client.recives.datalen.dataAsy;

import superlink.udpbind.client.recives.ByteBufer;
import superlink.udpbind.client.recives.DataReCallBuffer;
import superlink.util.Utils;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.CRC32;

public class CallCon implements DataReCallBuffer.call {
    public long time = System.currentTimeMillis();
    public volatile int posW;
    public volatile int posR;
    public byte[][] bytess;
    public volatile int[] bytel;
    CRC32 crc32 = new CRC32();
    boolean s;
    public final int max;
//    volatile boolean read = false;
    AtomicBoolean read = new AtomicBoolean(false);
    public ByteBufer queue;
    public AtomicInteger atomicInteger=new AtomicInteger(0);
    int sp;
    public CallCon(int fp, int bl) {
//        atomicInteger.getAndIncrement()
        bytess = new byte[bl][];
        bytel = new int[bl];
        for (int i = 0; i < bytess.length; i++) {
            bytess[i] = new byte[fp];
        }
        sp=bl/2;
        Arrays.fill(bytel, -1);
//        max=256*256*bl;
        max=4*bl;
    }

    public void add(byte[] bytes, int pos, int len) {
        s = true;
        if (len < 4) {
            queue.add(Arrays.copyOfRange(bytes, pos, len + pos));
            return;
        }
        int index = Utils.byteArrayToInt(bytes, pos);
        int i = index % bytess.length;
        if (bytel[i] == -1) {
            crc32.reset();
            crc32.update(bytes, pos, len - 4);
            int c = (int) crc32.getValue();
            int c0 = Utils.byteArrayToInt(bytes, pos + len - 4);
            if (c != c0) {
                return;
            }
            if(posW==126){
                System.out.println("ps");
            }
            if(isInFixedRange(index) && read.compareAndSet(false, true)){
                try{
                    if(bytel[i]>=0)return;
                    System.arraycopy(bytes, pos + 4, bytess[i], 0, len - 4 - 4);
                    bytel[i] = len - 4 - 4;
                    if (isGreaterThanPosW(index)) {
                        posW = index;
                        incrementPosW();
                    }
                } finally {
                    read.set(false);
                }
            };
//            if(isBeyondRange(index))return;

        }
    }
//    public int ts=0;//td
    private int start=0;
    public int read(byte[] bytes) {
        int p0 = 0;
        boolean ready=false;
        try {
            if (read.compareAndSet(false, true)) {
                int p = posR;
                while ( isBetween(p)  && p!= bytes.length) {
//                    if(p==bytel.length)p=0;
                    int index = posR % bytel.length;
//                    if (bytel[index] -start>= 0) {
                    if(bytel[index] == 0){
                        if(ready){return p0;}
                        else {
                            bytel[index] = -1;
                            start=0;
                            incrementPosR();
                            return 0;
                        }
                    }
                    if (bytel[index] < 0) {
                        if(ready)return p0;
                        else return -1;
                    }
//                            writer.add(bytess[index], 0, bytel[index]);
                    if(bytel[index]-start>bytes.length-p0){
                        System.arraycopy(bytess[index],start,bytes,p0,bytes.length-p0);
                        start=start+bytes.length-p0;
//                            ts=ts+(bytes.length-p0);
//                            bytel[index] = bytel[index]-start;
                        return bytes.length;
                    }else {
                        System.arraycopy(bytess[index],start,bytes,p0,bytel[index]-start);
//                            ts=ts+(bytel[index]-start);
                        p0=p0+(bytel[index]-start);
                        bytel[index] = -1;
                        start=0;
                        incrementPosR();
                        p++;
                        ready=true;
                    }

                }
                if(ready)return p0;
                else return -1;
//                return -1;
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
        return -1;
    }

    public boolean isBetween( int index) {
        if(posR==posW)return false;
        if (posR < posW) {
            // 正常情况：pos2 >= pos1，且未发生溢出
            return index >= posR && index < posW;
        } else {
            // 溢出情况：pos2 归零，而 pos1 未归零
            return index >= posR || index < posW;
        }
    }

    public boolean isLessThanPosR(int index) {
        if (posR <= posW) {
            // 正常情况：pos2 >= pos1，未溢出
            return index < posR;
        } else {
            // 溢出情况：pos1 归零，pos2 未归零
            return index < posR || index > posW;
        }
    }

    public boolean isGreaterThanPosW(int index) {
        if (posR <= posW) {
            // 正常情况：pos2 >= pos1，未溢出
            return index >= posW;
        } else {
            // 溢出情况：pos2 归零，pos1 未归零
            return index >= posW && index < posR;
        }
    }

    public boolean isInFixedRange(int index) {
        int endPos = posR + sp;
        if (endPos < max) {
            // 无溢出：区间是 [pos1, pos1 + FIXED_OFFSET - 1]
            return index >= posR && index < endPos;
        } else {
            // 有溢出：区间是 [pos1, MAX_VALUE - 1] 或 [0, (endPos % MAX_VALUE)]
            return (index >= posR && index < max) || (index >= 0 && index < (endPos % max));
        }
    }
//    distance = (pos2 - pos1 + bufferSize) % bufferSize。
    public boolean isBeyondRange(int index) {
        int endPos = posR + sp;
        if (endPos < max) {
            // 无溢出：直接判断 index >= endPos
            return index >= endPos;
        } else {
            // 有溢出：index 必须在 [endPos % MAX_VALUE, pos1) 之间
            int wrappedEnd = endPos % max;
            return index >= wrappedEnd && index < posR;
        }
    }

    public boolean ispos() {
        int endPos = posR + sp;
        if (endPos < max) {
            // 无溢出：直接判断
            return posR <= posW;
        } else {
            // 有溢出：index 必须在 [endPos % MAX_VALUE, pos1) 之间
            int wrappedEnd = endPos % max;
            return posW >= wrappedEnd && posW < posR;
        }
    }
    public int incrementPosR() {
        posR++;
        if (posR >= max) {
            posR = 0;
        }
        return posR;
    }
    public int incrementPosW() {
        posW++;
        if (posW >= max) {
            posW = 0;
        }
        return posW;
    }

    public int getPosWBatis(int b){
        int p =posW+b;
//        if(p>max)return p-max;
//        else {
//            if(p<0)return max+p;
//            return p;
//        }
        if (p >= max) return p - max;
        if (p < 0) return p + max;
        return p;
//        return (p % max + max) % max;
    }
    public int getPosRBatis(int b){
        int p =posR+b;
//        p & (max - 1)
        return p % max;
//        return (p % max + max) % max;
    }
    public boolean gets() {
        try {
//            if()
            return s;
        } finally {
//            s = false;
        }
    }
    public boolean getState() {
        try {
                return s;
        } finally {
//            s = false;
        }
    }

    public void readQue(ByteBufer queue){
        byte[] bytes;
        while (queue.size()>0){
            bytes=queue.poll();
            if(bytes[0]==87 && bytes[1]==65)continue;//"WA"
            add(bytes,0,bytes.length);
        }
    }
}