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
import superlink.util.asynhandle.AsynHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class synDataBytes implements Callable<Void> {
    public UserContext userContext;
    public short id;
    Senders senders;
    ByteBufer blockingQueue;
    public int fp = 1449;
    AutoData dataAuto;
    Object object;
    AsynHandler[] callables;
    RandomAccessFile randomFile = null;
    byte[][] cache = null;

    public synDataBytes(AutoData dataAuto, int max,DataRequest sdr) {
        System.out.println("revor");
        this.userContext = dataAuto.userContext;
        this.id = dataAuto.id;
        blockingQueue = dataAuto.blockingQueue;
        this.senders = dataAuto.senders;
        this.dataAuto = dataAuto;
        fp = max - 1;
        bytess = new byte[dataRequest.page][];
        cheak = Utils.byteMerger(("DA").getBytes(), Utils.shortToByteArray((short) id));

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


        try {
            randomFile = new RandomAccessFile(file, "rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        cache = new byte[sdr.page][];

    }
    public void synReqFile(DataRequest dataRequest, long timeLong) {
        this.dataRequest = dataRequest;
        this.timeLong=timeLong;
        if (dataRequest.page > 0) {
            dataAuto.data = bytess;
            dataAuto.sdr.page = dataRequest.page;
            userContext = dataAuto.userContext;
            object=rev;
//            cheak=dataAuto.
        }
        ByteQueFactory.alist.add(this);
    }

    public synDataBytes addWork(AsynHandler... callables){
        this.callables=callables;
        return this;
    }


    byte[][] bytess = null;
    long timeLong;

    byte[] cheak;
    int i = 0;
    DataRequest dataRequest;
    int j = 0;
    byte[] rev;
    long l1 = 10;


    @Override
    public Void call() {
        byte[] bytes = new byte[fp];
        int p = 0;
        int len = 0;
        byte[] send = null;

        //buffer.mark(1449);

        byte[] re = null;
        String s = "";
        bytes = new byte[fp];
        int t = 1;
        long l;
        boolean st = false;
        for (int i = 0; i < cache.length; i++) {
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
                        return null;
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
        return null;
    }


}

