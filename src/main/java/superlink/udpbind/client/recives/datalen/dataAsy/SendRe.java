package superlink.udpbind.client.recives.datalen.dataAsy;

import superlink.udpbind.client.recives.DataReCallBuffer;
import superlink.udpbind.client.recives.Senders;
import superlink.util.Utils;

import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.zip.CRC32;

public class SendRe implements DataReCallBuffer.call {
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
    public LinkedBlockingQueue<byte[]> queue = new LinkedBlockingQueue();

    public SendRe(int size, int fp, int bl) {
        len = size;
        allPage = size / fp;
        int s = size % fp;
        if (s != 0) {
            allPage = allPage + 1;
        }
        if (allPage < bl) {
            bytess = new byte[allPage][];
            bytel = new int[allPage];
        } else {
            bytess = new byte[bl][];
            bytel = new int[bl];
        }
        sp=bl-2;

        for (int i = 0; i < bytess.length; i++) {
            bytess[i] = new byte[fp + 14];
        }
    }

    public void setSenders(Senders senders) {
        this.senders = senders;
    }

    public void add(byte[] bytes, int pos, int len) {
        s = true;
        if (len == 4) {
            int index = Utils.byteArrayToInt(Utils.subByte(bytes, pos, 4));
            if (index < posR || index > posW) {
                return;
            } else {
                queue.add(Arrays.copyOfRange(bytes, pos, len + pos));
            }
        } else {
            if (len == 7) {//todo
                String s = new String(bytes, pos + 4, 3);
                if (s.equals("DEL")) {
                    int index = Utils.byteArrayToInt(Utils.subByte(bytes, pos, 4));
                    for (; posR <= index; posR++) {
                        int i = posR % bytess.length;
                        bytel[i] = 0;
                    }
                }
                return;
            }
            queue.add(Arrays.copyOfRange(bytes, pos, len + pos));
        }
    }

    int readTimes = 0;

    public void rd() {
        int p = readTimes % bytess.length;
        byte[] bytes = bytess[p];
    }

    public void read() {
        read = true;
        try {
            if (posW - posR < sp || posW < allPage) {
                for (; (posW - posR) < sp && posW < allPage; posW=posW+1) {
                    int index = posW % bytel.length;
                    if (bytel[index] == 0) {
                        int len = reader.read(bytess[index], posW);
                        bytel[index] = len;
                        senders.send0(bytess[index], 0, len + 14);
                    }
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
