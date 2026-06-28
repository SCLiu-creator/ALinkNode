package superlink.udpbind.client.recives.datalen.dataAsy;

import superlink.udpbind.client.recives.DataReCallBuffer;
import superlink.udpbind.client.recives.Senders;
import superlink.util.Utils;

import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.zip.CRC32;

public class SendCall implements DataReCallBuffer.call {
    public long time = System.currentTimeMillis();
    public int len;
    public int allPage;
    public volatile int posW;
    public volatile int posR;
    int sp;
    public DataReader reader;
    Senders senders;
    public byte[][] bytess;
    public int[] bytel;
    CRC32 crc32 = new CRC32();
    boolean s;
    volatile boolean read = false;
    public LinkedBlockingQueue<byte[]> queue = new LinkedBlockingQueue<>();

    public final int max;

    public SendCall(int fp, int bl) {
        bytess = new byte[bl][];
        bytel = new int[bl];

        sp=bl/2;

        for (int i = 0; i < bytess.length; i++) {
            bytess[i] = new byte[fp + 14];
        }
        Arrays.fill(bytel, -1);
//        max=256*256*bl;
        max=4*bl;
    }

    public void setSenders(Senders senders) {
        this.senders = senders;
    }

    public void add(byte[] bytes, int pos, int len) {
        s = true;
        if (len == 4) {
            int index = Utils.byteArrayToInt(Utils.subByte(bytes, pos, 4));
            if (isBetween(index)) {
                queue.add(Arrays.copyOfRange(bytes, pos, len + pos));
            } else {
                return;
            }
        } else {
            if (len == 7) {//todo
                String s = new String(bytes, pos + 4, 3);
                if (s.equals("DEL")) {
                    int index = Utils.byteArrayToInt(Utils.subByte(bytes, pos, 4));

                    for (; isBetween(index); ) {
                        int i = posR % bytess.length;
                        bytel[i] = -1;
                        incrementPosR();
                        System.out.println("del: "+posR);
                    }
                }
                return;
            }
            queue.add(Arrays.copyOfRange(bytes, pos, len + pos));
        }
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
            return index > posW;
        } else {
            // 溢出情况：pos2 归零，pos1 未归零
            return index > posW && index < posR;
        }
    }

    public boolean isIndexInFixedRange(int index) {
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
    public boolean isIndexBeyondFixedRange( int index) {
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
            // 无溢出情况：检查 posW 是否在 [posR, endPos) 区间内
            return posW >= posR && posW < endPos;
        } else {
            // 有溢出情况：区间分成两部分 [posR, max) 和 [0, endPos % max)
            // posW 必须在其中任意一个区间内
            int wrappedEnd = endPos - max;
            return (posW >= posR && posW < max) ||  // 上半部分
                    (posW >= 0 && posW < wrappedEnd); // 下半部分
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

    public int readTimes = 0;

    public void rd() {
        int p = readTimes % bytess.length;
        byte[] bytes = bytess[p];
    }

    public boolean ava=false;
    public void read() {
        read = true;
        try {
            for (; ispos(); ) {
//                if(posW==126){
//                    System.out.println("ps");
//                }
                int index = posW % bytel.length;
                if (bytel[index] == -1) {
                    int len = reader.read(bytess[index], posW);
                    if(len>=0){
                        ava=true;
                    }
                    if(len==-1 ){
                        if(ava){
                            //设置0长度数据
                            System.out.println("close 设置0长度数据");
                            len=0;
                            ava=false;
                        }else {
                            return;
                        }
                    }
                    bytel[index] = len;
                    senders.send0(bytess[index], 0, len + 14);
                    incrementPosW();
                }else {
                    return;
                }
            }

        } finally {
            read = false;
        }
    }

    public void readAsy() {
        read = true;
        try {
            for (; ispos(); ) {
//                if(posW==126){
//                    System.out.println("ps");
//                }
                int index = posW % bytel.length;
                if (bytel[index] == -1) {
                    int len = reader.read(bytess[index], posW);
                    if(len==-1 ){
                        return;
                    }
                    bytel[index] = len;
                    senders.send0(bytess[index], 0, len + 14);
                    incrementPosW();
                }else {
                    return;
                }
            }

        } finally {
            read = false;
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
}
