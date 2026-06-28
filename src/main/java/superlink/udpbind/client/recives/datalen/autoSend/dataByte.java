package superlink.udpbind.client.recives.datalen.autoSend;

import com.alibaba.fastjson2.JSON;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.ByteBufer;
import superlink.udpbind.client.recives.ByteQueFactory;
import superlink.udpbind.client.recives.Senders;
import superlink.udpbind.client.recives.datalen.AutoData;
import superlink.udpbind.usedata.DataRequest;
import superlink.util.Utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class dataByte {
    public UserContext userContext;
    public short id;
    Senders senders;
    ByteBufer blockingQueue;
    DataRequest sdr;
    public int fp = 1449;
    AutoData dataAuto;

    public dataByte(AutoData dataAuto, int max) {
        System.out.println("revor");
        this.userContext = dataAuto.userContext;
        this.id = dataAuto.id;
        blockingQueue = dataAuto.blockingQueue;
        this.senders = dataAuto.senders;
        this.dataAuto = dataAuto;
        fp = max - 1;
    }

    byte[] rev;

    public byte[] reqFile(DataRequest dataRequest, long timeLong) {
        int time = (int) timeLong;
        int i = 0;
        int l;
        byte[][] bytess = null;
        byte[] cheak = Utils.byteMerger(("DA").getBytes(), Utils.shortToByteArray((short) id));
        if (dataRequest.page > 0) {
            bytess = new byte[dataRequest.page][];
            dataAuto.data = bytess;
            dataAuto.sdr.page = dataRequest.page;
            int pos = 0;
            int j = 0;
            long l1 = 10;
            byte[] bytes;
            byte[] bytec = "AD".getBytes();
            while (true) {
                while (true) {
                    bytes = blockingQueue.poll();
                    if (bytes == null) {
                        try {
                            bytes = blockingQueue.poll(l1 / 2, TimeUnit.MILLISECONDS);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (bytes == null) {
                        break;
                    } else {
                        if (bytes.length > 5) {
                            l = (int) Utils.calculateChecksum(bytes, 0, bytes.length - 4);
                            if (l == Utils.byteArrayToInt(bytes, bytes.length - 4)) {
                                bytess[bytes[0] + 128] = Utils.subByte(bytes, 1, bytes.length - 5);
                            }
                        } else {
                            if (bytes.length == 2 && Utils.equals(bytes, bytec)) {
                                return null;
                            }
                        }
                        j = 0;
                    }
                }
                if (bytes == null) {
                    time = time * 2;
                    if (time < 0) {
                        time = -time;
                    }
                } else {
                    if (time > 4) {
                        time = time / 2;
                    }
                }
                if (j % 4 == 0 && j != 0) {
                    if (this.userContext.cheak()) {
                        break;
                    } else {
                        senders.sendSym(cheak);
                        if (userContext.getQueue(id) != this.blockingQueue) {
                            break;
                        } else {
                            this.userContext = UDPclient.mainDataQueue.getUserContext(userContext.getUserId());
                            this.senders.InitInit(this.id, userContext);
                        }
                    }
                    if (j > 14) {
                        return null;
                    }
                }
                j++;
                i = 0;
                pos = Byte.MIN_VALUE;
                System.out.println("revcheak");
                for (byte[] b : bytess) {
                    if (b != null) {
                        i++;
                    } else {
                        senders.send(new byte[]{(byte) pos});
                    }
                    pos++;
                }
                if (i == dataRequest.page) {
                    try {
                        l = bytess[dataRequest.page - 1].length;
                        l = ((dataRequest.page - 1) * fp) + l;
                        rev = new byte[l];
                        i = 0;
                        for (byte[] bytes1 : bytess) {
                            System.arraycopy(bytes1, 0, rev, i * fp, bytes1.length);
                            i++;
                        }
                        break;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                try {
                    l1 = (long) ((long) Math.log(safeMultiply(time, time)) * Math.log(258 - i) * 16);
                } catch (Exception e) {
                    System.out.println("value of byteTtime: " + time);
                    time = (int) timeLong;
                }
            }
            senders.send("OK".getBytes());
            senders.send("OK".getBytes());
        } else {
            return new byte[0];
        }
        return rev;
    }

    public void sends(DataRequest sdr) {
        this.sdr = sdr;
        File file = new File(sdr.filename);
        if (file.exists()) {
            sdr.page = Math.toIntExact(file.length() / fp);
            if ((file.length() % fp) != 0) {
                sdr.page += 1;
            }
        } else {
            sdr.page = 0;
            String send = "DB" + JSON.toJSONString(sdr);
            senders.send(send.getBytes());
            senders.send(send.getBytes());
            return;
        }
        byte[] send = ("DB" + JSON.toJSONString(sdr)).getBytes();
        senders.send(send);
        sdr.requestname = UDPclient.userlocal.username;
        sdr.filename = null;
        byte[] cheak = Utils.byteMerger(("DA").getBytes(), Utils.shortToByteArray(id));
        dataAuto.send = cheak;
        System.out.println(sdr.page);

        byte[] bytes = new byte[fp];
        int p = 0;
        int len = 0;
        RandomAccessFile randomFile = null;
        BufferedInputStream buffer = null;
        try {
            randomFile = new RandomAccessFile(file, "rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        //buffer.mark(1449);
        byte[][] cache = new byte[sdr.page][];
        byte[] re = null;
        String s = "";
        bytes = new byte[fp];
        int t = 1;
        long l;
        boolean st = false;
        for (int i = 0; i < sdr.page; i++) {
            try {
                randomFile.seek(i * fp);
                len = randomFile.read(bytes);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (len != fp) {
                bytes = Utils.subByte(bytes, 0, len);
            }
            send = Utils.byteMerger(new byte[]{(byte) (i - 128)}, bytes);
            l = Utils.calculateChecksum(send, 0, send.length);
            send = Utils.byteMerger(send, Utils.intToByteArray((int) l));
            cache[i] = send;
            senders.send(send);
        }

        while (true) {
            try {
                re = blockingQueue.poll(2, TimeUnit.SECONDS);
                if (re.length == 1) {
                    p = (re[0] + 128);
                    senders.send(cache[p]);
                } else {
                    s = new String(re);
                    if ("OK".equals(s)) {
                        break;
                    }
                    if ("AD".equals(s)) {
                        return;
                    }
                }
                t = 0;
            } catch (InterruptedException | NullPointerException inexce) {
                if (this.userContext.cheak()) {
                    break;
                } else {
                    if (t / 2 > 0) {
                        senders.sendSym(cheak);
                    }
                    if (t % 4 == 0) {
                        this.senders.InitInit(this.id, userContext);
                    }
                }
                senders.send(send);
                System.out.println("dataByte timeOut or Null");
                if (t > 6) {
                    break;
                }
                t++;
            } catch (Exception e) {
                System.out.println("dataByte Excep: " + new String(bytes));
            }
        }
        System.out.println("over");
    }


    public long safeMultiply(long a, long b) {
        try {
            return Math.multiplyExact(a, b);
        } catch (ArithmeticException e) {
            return Long.MAX_VALUE;
        }
    }

    @Override
    public int hashCode() {
        return sdr.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return this.hashCode() == o.hashCode() ? true : false;
    }

}
