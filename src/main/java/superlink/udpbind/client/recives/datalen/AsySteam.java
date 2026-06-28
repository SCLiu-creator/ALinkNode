package superlink.udpbind.client.recives.datalen;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.annotation.JSONField;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.*;
import superlink.udpbind.client.recives.datalen.dataAsy.*;
import superlink.udpbind.client.recives.datalen.dataCache.BufferDataCon;
import superlink.udpbind.usedata.BufferRequest;
import superlink.util.Utils;

import java.io.*;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.CRC32;


public class AsySteam extends DataLength {
    @JSONField(serialize = false)
    public Senders senders;
    @JSONField(serialize = false)
    public ByteBufer blockingQueue;
    @JSONField(serialize = false)
    public InputStream rev;
    @JSONField(serialize = false)
    public static ConcurrentHashMap<AsySteam, AsySteam> DataMap = new ConcurrentHashMap<>();

    public int readyTimes=0;

    AsySteam asySteam = this;

    public static Executor executor = Executors.newSingleThreadExecutor();

    CRC32 crc32 = new CRC32();

    public static AsySteam getSteam(String username, short id) {
        AsySteam steam = new AsySteam(username, id);
        steam = AsySteam.getSteam(steam);
        return steam;
    }
    public static AsySteam getSteam(AsySteam asySteam) {
        AsySteam autoData = DataMap.get(asySteam);
        if (autoData == null) {
            DataMap.put(asySteam, asySteam);
            return asySteam;
        } else {
            return autoData;
        }
    }
    public AsySteam(String username) {
        System.out.println("revor");
        this.userContext = UDPclient.mainDataQueue.getUserContext(username);
        this.id = userContext.newQueue(256);
        blockingQueue = userContext.getDataQue(this.id);
        this.senders = new Senders().Init(this.id, username);
        setPagelen(DataLenMange.getLen(username));
    }

    public AsySteam(String username, short id) {
        System.out.println("sendor");
        this.userContext = UDPclient.mainDataQueue.getUserContext(username);
        this.id = id;
        blockingQueue = userContext.getDataQue(this.id);
        this.senders = new Senders().Init(this.id, username);
        setPagelen(DataLenMange.getLen(username));
        userContext.getTask(id).task=this;
    }

    LinkedBlockingQueue<CallPoll> callPolls = new LinkedBlockingQueue<>();

    AtomicReference<BufferRequest> dataRequest = new AtomicReference<>();
    CallPoll callPoll = new CallPoll() {
        int i = 0;
        boolean b = false;
        long time1 = System.currentTimeMillis();
        long tw;

        @Override
        public Long runTime() {
            try {
                byte[] star = blockingQueue.poll();
                if (star == null) {
                    if (i > 5) {
                        return null;
                    }
                    tw = System.currentTimeMillis() - time1;
                    time1 = System.currentTimeMillis();
                    tw = 3500L - tw;
                    if (tw > 0) {
                        return tw;
                    } else {
                        i++;
                        System.out.println("Auto Timeout");
                        senders.sendSym(send);
                        return 3500L;
                    }
                } else {
                    String re = new String(star);
                    if (re.equals("bA")) {
                        return null;
                    }
                    if (re.equals("WA")) {
                        time1 = System.currentTimeMillis();
                        i = 0;
                        return -1L;
                    }
                    return null;
                }
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public boolean isOver() {
            return b;
        }
    };
    CallRead callRead;

    //使用b
    public CallRead reqData(String remoteFilename) {
        long t1 = System.currentTimeMillis();
        Long time;
        if(remoteFilename!=null){
            BufferRequest sdr = new BufferRequest();
            sdr.name = UDPclient.userlocal.username;
            sdr.id = id;
            sdr.pl = pagelen;
            sdr.bufname = ":datafl&:" + remoteFilename;
            this.bdr = sdr;
            DataMap.put(this, this);
            send = ("As" + JSON.toJSONString(sdr)).getBytes();
            senders.sendSym(send);
            while (true) {
                time = callPoll.runTime();
                if (time == null) return null;
                if (time > 0) {
                    try {
                        Thread.sleep(time / 100);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                } else {
                    break;
                }
            }
            long t2 = System.currentTimeMillis();
            time = t2 - t1;
        }else {
            time = 3000L;
        }

        send = ("WA" + JSON.toJSONString(bdr)).getBytes();
//        send = Utils.byteMerger("bA".getBytes(), Utils.shortToByteArray((short) id));


        callCon = new CallCon(pagelen, 128);

        DataReCallBuffer bufer = new DataReCallBuffer();
        bufer.reCallCon = callCon;
        callCon.queue = new ByteQueue(128);
        callCon.readQue(blockingQueue);
        userContext.setQueue((short) id, bufer);

        callRead = req(time, bufer, this);

        return callRead;//new ByteArrayInputStream(rev);
    }

    public Object ro;
    public int readLen;
    Long time;

    public CallRead req(long timeLong, DataReCallBuffer bufer, AsySteam se) {
        time = timeLong;
        callPoll = new CallPoll() {
            long time0 = System.currentTimeMillis();
            long reqTime = System.currentTimeMillis();
            long time = timeLong;
            boolean iso = false;
            int t = 0;
            byte[] bytes;
            byte[] b;
            public int pos;

            @Override
            public Long runTime() {
                long waitTime = System.currentTimeMillis() - time0;
                long waitReq = System.currentTimeMillis() - reqTime;
                if (callCon.gets() ) {
                    t = 0;
                    time0 = System.currentTimeMillis();
                    if(pos != callCon.posR) {
                        pos = callCon.posR;
                        b = Utils.byteMerger(Utils.intToByteArray(callCon.getPosRBatis(-1)), "DEL".getBytes());
                        senders.send(b);
                        return time / 2;
                    }
                } else {
                    if (waitReq < time) {
                        if (readLen > 0) {
                            time0 = System.currentTimeMillis();
                            b = Utils.byteMerger(Utils.intToByteArray(callCon.getPosRBatis(-1)), "DEL".getBytes());
                            senders.send(b);
                            t = 0;
                        }
                    } else {
                        if (waitTime < time) {
                            if (blockingQueue.size() > 0) {
                                bytes = blockingQueue.poll();
                                String s = new String(bytes);
                                System.out.println("poll  " + s);
                                if (s.equals("WA")) {
                                    t = 0;
                                    time = timeLong * 2;
                                    time0 = System.currentTimeMillis();
                                    reqTime = System.currentTimeMillis();
                                } else if ("As".equals(s)) {
                                    if (readyTimes>0){
                                        readyTimes--;
                                        return time;
                                    }
                                    CallExecutor.remove(asySteam);
                                }
                                //todo
                            }
//                            waitTime = System.currentTimeMillis() - time0;
                            if (waitTime < timeLong / 2) {
                                time = waitTime(t, waitTime, timeLong);
                                time0 = System.currentTimeMillis();
                                senders.send(b);
                                senders.send(send);
                            }
                            if (waitTime < time) {
                                return -1L;
                            }
                        } else {
                            if (t >= 4) {
                                UserContext userC = UDPclient.mainDataQueue.contrainUser(userContext.userName);
                                if (userC == null) {
                                    iso = true;
                                    CallExecutor.remove(asySteam);
                                } else {
                                    if (userC != userContext) {
                                        userContext = userC;
                                        senders.InitInit(id, userContext);
                                        userContext.setQueue((short) id, bufer);
                                    }
                                    if (t > 13 && time>1000) {
                                        iso = true;
                                        CallExecutor.remove(asySteam);
                                    }
                                    if (t % 3 == 0) {
                                        ByteBufer ub = userC.getQueue(id);
                                        if (ub != blockingQueue && ub != bufer) {
                                            iso = true;
                                            CallExecutor.remove(asySteam);
                                        }
                                    }
                                    senders.sendSym(Utils.byteMerger(("sA").getBytes(), Utils.intToByteArray(id)));
                                }
                            }
                            t++;
                            time = waitTime(t, time, timeLong);
                            time0 = System.currentTimeMillis();
                        }
                        reqTime = System.currentTimeMillis();

                        int p = callCon.getPosRBatis(-1);
                        b = Utils.byteMerger(Utils.intToByteArray(p), "DEL".getBytes());
                        senders.send(b);
//                            senders.send(Utils.intToByteArray(k));
//                    pos= callCon.posR;
                        int k;
                        for (k = callCon.posR; callCon.isBetween(k); k++) {
                            if (k >= callCon.max) k = 0;
                            if (callCon.bytel[k % callCon.bytel.length] == -1) {
                                senders.send(Utils.intToByteArray(k));
                                System.out.println("recCheak  " + k);
                                send = Utils.intToByteArray(k);
                            }
                        }
                        if (callCon.isInFixedRange(k) && callCon.bytel[k % callCon.bytel.length] == -1) {
                            senders.send(Utils.intToByteArray(k));
                        }
                    }
                    return -1L;
                }
                return -1L;
            }

            //
            @Override
            public boolean isOver() {
                return false;
            }
        };

        callRead = new CallRead() {
            long time0 = System.currentTimeMillis();
            long reqTime = System.currentTimeMillis();
            long time = timeLong;
            int t = 0;
            byte[] bytes;
            byte[] b;
            boolean iso = false;
            int pos;
            int k;
            boolean r = true;

            @Override
            public int runTime(byte[] byteBuffer) throws Exception {
                long waitTime = System.currentTimeMillis() - time0;
                long waitReq = System.currentTimeMillis() - reqTime;
                readLen = callCon.read(byteBuffer);
                if (readLen >= 0) {
//                    System.out.println("read: "+callCon.ts);
                    b = Utils.byteMerger(Utils.intToByteArray(callCon.getPosRBatis(-1)), "DEL".getBytes());
                    senders.send(b);
                    t = 0;
                    if (callCon.isLessThanPosR(pos)) {
                        pos = callCon.posR;
                        if (time0 != reqTime) {
                            time = waitTime(t, time, timeLong);
                        }
                    }
                    time0 = System.currentTimeMillis();
                    if (readLen == 0) {
                        System.out.println("thiis");
                    }
                    return readLen;
                }
                if (waitReq < time) {
                    time0 = System.currentTimeMillis();
                    if (readLen > 0) {
                        b = Utils.byteMerger(Utils.intToByteArray(callCon.getPosRBatis(-1)), "DEL".getBytes());
                        senders.send(b);
                        t = 0;
                    }
                } else {
                    if (waitTime < time) {
                        if (blockingQueue.size() > 0) {
                            bytes = blockingQueue.poll();
                            System.out.println("poll  " + new String(bytes));
                            if (new String(bytes).equals("WA")) {
                                t = 0;
                                time = timeLong * 2;
                            }
                            //todo
                        }
                        waitTime = System.currentTimeMillis() - time0;
                        if (waitTime < time) {
                            return -1;

                        }
                        if (waitTime < timeLong / 2) {
                            time = waitTime(t, waitTime, timeLong);
                            time0 = System.currentTimeMillis();
                            senders.send(b);
                            senders.send(send);
                        }
                    }
                    reqTime = System.currentTimeMillis();
                    t++;
                    int p = callCon.getPosRBatis(-1);
                    b = Utils.byteMerger(Utils.intToByteArray(p), "DEL".getBytes());
                    senders.send(b);
//                            senders.send(Utils.intToByteArray(k));
//                    pos= callCon.posR;
                    for (k = callCon.posR; callCon.isBetween(k); k++) {
                        if (k >= callCon.max) k = 0;
                        if (callCon.bytel[k % callCon.bytel.length] == -1) {
                            senders.send(Utils.intToByteArray(k));
                            System.out.println("recCheak  " + k);
                            send = Utils.intToByteArray(k);
                        }
                    }
                    if (callCon.isInFixedRange(k) && callCon.bytel[k % callCon.bytel.length] == -1) {
                        senders.send(Utils.intToByteArray(k));
                    }
                }
                if (t >= 4) {
                    UserContext userC = UDPclient.mainDataQueue.contrainUser(userContext.userName);
                    if (userC == null) {
                        iso = true;
                        throw new Exception("link break");
                    } else {
                        if (userC != userContext) {
                            userContext = userC;
                            senders.InitInit(id, userContext);
                            userContext.setQueue( id, bufer);
                        }
                        if (t > 13&&time>1500) {
                            iso = true;
                            throw new Exception("link break");
                        }
                        if (t % 3 == 0) {
                            ByteBufer ub = userC.getQueue(id);
                            if (ub != blockingQueue && ub != bufer) {
                                iso = true;
                                throw new Exception("link break");
                            }
                            senders.sendSym(Utils.byteMerger(("bA").getBytes(), Utils.intToByteArray(id)));
                        }
                    }
                }
                time = waitTime(t, time, timeLong);
                time0 = System.currentTimeMillis();

                return -1;
            }

            @Override
            public boolean isOver() {
//                senders.send("OK".getBytes());
//                senders.send("OK".getBytes());
//                System.out.println("over");
//                se.finalize(se);
                return iso;
            }
        };
        CallExecutor.add(asySteam, callPoll);
        return callRead;
    }


    public byte[] getbytes() {
        try {
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024 * 1024 * 10);
            byte[] bytebuf = new byte[1024];
            int len;
            while (true) {
                len = callRead.runTime(bytebuf);
                if (len > 0) {
                    try {
                        byteBuffer.put(bytebuf, 0, len);
                    } catch (Exception i) {
                        i.printStackTrace();
                    }
                } else {
                    if (len == -1) {
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException inr) {
                            inr.printStackTrace();
                        }
                        if(CallExecutor.unContain(asySteam)){
                            break;
                        }
                        continue;
                    }
                    if (len == 0) {
                        System.out.println("break");
                        byteBuffer.flip();
                        byte[] data = new byte[byteBuffer.remaining()];
                        byteBuffer.get(data);
                        return data;
                    }
                }
                if (callRead.isOver()) {
                    senders.send("OK".getBytes());
                    senders.send("OK".getBytes());
                    System.out.println("over");
//                    this.finalize(this);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            senders.send("OK".getBytes());
        }
        return null;
    }


    public int read(byte[] bytebuf) {
        try {
            int len;
            while (true) {
                len = callCon.read(bytebuf);
                if (len > 0) {
                    return len;
                } else {
                    if (len == -1) {
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException inr) {
                            inr.printStackTrace();
                            return 0;
                        }
                        continue;
                    }
                    if (len == 0) {
                        return -1;
                    }
                }
                if (callRead.isOver()) {
                    senders.send("OK".getBytes());
                    senders.send("OK".getBytes());
                    System.out.println("over");
//                    this.finalize(this);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            senders.send("OK".getBytes());
        }
        return -1;
    }

    public byte[] gettest() {
        try {
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024 * 1024 * 10);
            byte[] bytebuf = new byte[1024];
            int len;
            while (true) {
                len = callCon.read(bytebuf);
                if (len > 0) {
                    try {
                        byteBuffer.put(bytebuf, 0, len);
                    } catch (Exception i) {
                        i.printStackTrace();
                    }
                } else {
                    if (len == -1) {
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException inr) {
                            inr.printStackTrace();
                        }
                        if(CallExecutor.unContain(asySteam)){
                            break;
                        }
                        continue;
                    }
                    if (len == 0) {
                        System.out.println("break");
                        byteBuffer.flip();
                        byte[] data = new byte[byteBuffer.remaining()];
                        byteBuffer.get(data);
                        return data;
                    }
                }
                if (callRead.isOver()) {
                    senders.send("OK".getBytes());
                    senders.send("OK".getBytes());
                    System.out.println("over");
//                    this.finalize(this);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            senders.send("OK".getBytes());
        }
        return null;
    }

    public byte[] reqDataAsy(String remoteFilename) {
        return null;

    }

    public volatile byte[] send = null;



    public void execute(boolean b) {
        DataMap.put(asySteam, asySteam);
        if (b) {
            setThreadPool.reExecute(this);
        } else {
            run();
        }
    }

    public void aSend() {
        if (send == null) {
            senders.send("bA".getBytes());
        } else {
            senders.send(send);
        }
    }

    Thread sthread;

    //使用bA
    @Override
    public void run() {
//        if(sthread==null){
//            sthread=Thread.currentThread();
//        }else {
//            return;
//        }
        send = ("WA").getBytes();
//        senders.sendSym(send);
//        senders.sendSym(send);
        senders.send(send);
        InputStream data = praseStream(bdr);
//        send=Utils.byteMerger(("bA").getBytes(),Utils.intToByteArray(id));
        if (data == null) {
            send = Utils.byteMerger(("WA").getBytes(), Utils.intToByteArray(id));
//            senders.sendSym(send);
            senders.send(send);
            return;
        }

        SendCall sendRe = new SendCall(pagelen, 128);
        sendRe.setSenders(senders);
        InputStream fileInputStream = data;
        reader = new DataReader() {
            public int read(byte[] buf, byte[] sendhead, byte[] prex, byte[] cc, byte[] dataorg, int poss, int len) {
                int s = 0;
                System.arraycopy(sendhead, 0, buf, s, sendhead.length);
                s = s + sendhead.length;
                System.arraycopy(prex, 0, buf, s, prex.length);
                s = s + prex.length;
                System.arraycopy(dataorg, poss, buf, s, len);
                s = s + len;
                System.arraycopy(cc, 0, buf, s, cc.length);
                return len;
            }

            //            public int ts=0;
            final byte[] data = new byte[pagelen];
            boolean readStop = false;

            public int read(byte[] buf, int readTimes) {
                byte[] pre = Utils.intToByteArray(readTimes);
                crc32.reset();
                crc32.update(pre, 0, 4);
                int len = -1;
                try {
                    len = fileInputStream.read(data);
//                    ts=ts+len;
//                    System.out.println("read: "+ts+" and :"+len);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (len == -1) {
                    if (readStop) {
                        return -1;
                    } else {
                        System.out.println("close FileStream");
                        readStop = true;
                        len = 0;
                        crc32.update(data, 0, len);
                        byte[] cc = Utils.intToByteArray((int) crc32.getValue());
                        read(buf, senders.getPrex(), pre, cc, data, 0, len);
                        return -1;
                    }
                }
                crc32.update(data, 0, len);
                byte[] cc = Utils.intToByteArray((int) crc32.getValue());
                return read(buf, senders.getPrex(), pre, cc, data, 0, len);
            }
        };
        DataReCallBuffer bufer = new DataReCallBuffer();
        sendRe.reader = reader;
        bufer.reCallCon = sendRe;
        userContext.setQueue((short) id, bufer);
        send = ("DI" + JSON.toJSONString(bdr)).getBytes();
        senders.send(send);
        byte[] cheak = Utils.byteMerger(("bA").getBytes(), Utils.intToByteArray(id));
        try {
            sends(sendRe, cheak);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            clear();
        }
    }

    public BlockingStack getSend() {
        send = ("WA").getBytes();
        senders.send(send);
        senders.send(send);

        SendCall sendRe = new SendCall(pagelen, 128);
        sendRe.setSenders(senders);
        BlockingStack blockingStack = new BlockingStack();
        reader = new DataReader() {
            public int read(byte[] buf, byte[] sendhead, byte[] prex, byte[] cc, byte[] dataorg, int poss, int len) {
                int s = 0;
                System.arraycopy(sendhead, 0, buf, s, sendhead.length);
                s = s + sendhead.length;
                System.arraycopy(prex, 0, buf, s, prex.length);
                s = s + prex.length;
                System.arraycopy(dataorg, poss, buf, s, len);
                s = s + len;
                System.arraycopy(cc, 0, buf, s, cc.length);
                return len;
            }

            final byte[] data = new byte[pagelen];
            public boolean readStop = true;

            public int read(byte[] buf, int readPow) {
                byte[] pre = Utils.intToByteArray(readPow);
                crc32.reset();
                crc32.update(pre, 0, 4);
                int len = -1;
                try {
                    len = blockingStack.read(data);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (len == -1) {
                    System.out.println("close FileStream");
                    return -1;
                }
//                if(len==0){
//                    System.out.println("FileStream0");
//                    if(readStop){
//                        return -1;
//                    }else {
//                        readStop=true;
//                        len=0;
//                        crc32.update(data, 0, len);
//                        byte[] cc = Utils.intToByteArray((int) crc32.getValue());
//                        read(buf, senders.getPrex(), pre, cc, data, 0, len);
//                        return -1;
//                    }
//                }
                readStop = false;
                crc32.update(data, 0, len);
                byte[] cc = Utils.intToByteArray((int) crc32.getValue());
                return read(buf, senders.getPrex(), pre, cc, data, 0, len);
            }
        };
        DataReCallBuffer bufer = new DataReCallBuffer();
        sendRe.reader = reader;
        bufer.reCallCon = sendRe;
        userContext.setQueue((short) id, bufer);
        byte[] send = ("DI" + JSON.toJSONString(bdr)).getBytes();
        senders.send(send);
        byte[] cheak = Utils.byteMerger(("sA").getBytes(), Utils.intToByteArray(id));
//        try {
        AsySteam steam = this;
        sendAsy(sendRe, cheak);

        this.blockingStack = blockingStack;
        return blockingStack;
    }

    BlockingStack blockingStack;

    public BlockingStack getWrite() {
        if (blockingStack == null) {
            getSend();
            if (sthread == null) {
                CallExecutor.add(this, callPoll);
                sthread = Thread.currentThread();
            } else {
                return this.blockingStack;
            }
        }
        return blockingStack;
    }

    public void testSend() {
        if (sthread == null) {
            sthread = Thread.currentThread();
        } else {
            return ;
        }
        BlockingStack blockingStack = getSend();
        CallExecutor.add(this, callPoll);
        send = ("WA").getBytes();
//        senders.sendSym(send);
//        senders.sendSym(send);
        senders.send(send);
        InputStream inputStream = praseStream(bdr);
//        send=Utils.byteMerger(("bA").getBytes(),Utils.intToByteArray(id));
        int len = -1;
//        try {
//            Thread.sleep(120 * 1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        while (true) {
            byte[] data = new byte[1024];
            try {
                len = inputStream.read(data);
                if (len >= 0) {
                    blockingStack.write(data);
                }
//                    ts=ts+len;
//                    System.out.println("read: "+ts+" and :"+len);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (len == -1) {
                try {
                    blockingStack.write(new byte[0]);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }

                break;
            }
        }
    }

    public void writeFile(File file) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        send=Utils.byteMerger(("bA").getBytes(),Utils.intToByteArray(id));
        int len = -1;
        while (true) {
            byte[] data = new byte[1024];
            try {
                len = inputStream.read(data);
                if (len >= 0) {
                    blockingStack.write(data);
                }
//                    ts=ts+len;
//                    System.out.println("read: "+ts+" and :"+len);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (len == -1) {
                try {
                    blockingStack.write(new byte[0]);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
                break;
            }
        }
    }

    public void writeInstream(InputStream inputStream) {
        int len = -1;
        while (true) {
            byte[] data = new byte[1024];
            try {
                len = inputStream.read(data);
                if (len >= 0) {
                    blockingStack.write(data);
                }
//                    ts=ts+len;
//                    System.out.println("read: "+ts+" and :"+len);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (len == -1) {
                try {
                    blockingStack.write(new byte[0]);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
                break;
            }
        }
    }

    public void writeBytes(byte[] bs) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bs);
        writeBuffer(byteBuffer);
    }

    public void writeBuffer(ByteBuffer byteBuffer) {
        while (byteBuffer.remaining() > 0) {
            byte[] data = new byte[1024];
            try {
                int toRead = Math.min(byteBuffer.remaining(), data.length);
                byteBuffer.get(data, 0, toRead);
                blockingStack.write(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            blockingStack.write(new byte[0]);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
    }

    private CallCon callCon;

    public boolean state = true;

    DataReader reader;

    public void sends(SendCall sendRe, byte[] cheak) {
        if (sthread == null) {
            sthread = Thread.currentThread();
        } else {
            return;
        }
        int p;
        byte[] re = null;
        String s = "";
        int j = 0;

        while (state) {
            try {
                sendRe.read();
                re = sendRe.queue.poll(3, TimeUnit.SECONDS);
                if (re.length != 4) {
                    s = new String(re, 0, 2);
                    if ("WA".equals(s)) {
                        j = 0;
                        continue;
                    }
                    if ("OK".equals(s) || "As".equals(s)) {
                        state = false;
                        if (readyTimes>0){
                            readyTimes--;
                            continue;
                        }
                        break;
                    }
                    if ("sA".equals(s) && re.length == 2) {
                        return;
                    }
                    j = 0;
                    continue;
                }
                j = 0;
            } catch (Exception e) {
                senders.sendSym(cheak);
                System.out.println("send(send)   " + j);
                if (re == null) {
                    if (j > 3) {
                        UserContext userContext = UDPclient.mainDataQueue.contrainUser(this.userContext.userName);
                        if (userContext == null) {
                            break;
                        } else {
                            if (userContext.getQueue(id) == null) {
                                break;
                            }
                            if (j % 3 == 0) {
                                if (this.userContext != userContext) {
                                    this.senders.InitInit(this.id, userContext);
                                }
                            }
                            if (j > 12) {
                                state = false;
//                                break;
                            }
                        }
                    }
                    j++;
                    int index = sendRe.posR % sendRe.bytel.length;
                    send = sendRe.bytess[index];
                    senders.send0(send, 0, sendRe.bytel[index] + 14);
                    System.out.println("sendpr+ " + sendRe.posR);
                    index = (sendRe.getPosWBatis(-1)) % sendRe.bytel.length;
                    send = sendRe.bytess[index];
                    senders.send0(send, 0, sendRe.bytel[index] + 14);
                    System.out.println("sendpw+ " + sendRe.posW);
                }
                continue;
            }
            try {
                p = Utils.byteArrayToInt(re);
                if (sendRe.isBetween(p)) {
                    int index = p % sendRe.bytel.length;
                    send = sendRe.bytess[index];
                    senders.send0(send, 0, sendRe.bytel[index] + 14);
                    System.out.println("send+ " + p);
                } else {
                    int index = sendRe.posR % sendRe.bytel.length;
                    send = sendRe.bytess[index];
                    senders.send0(send, 0, sendRe.bytel[index] + 14);
                    System.out.println("send+ " + p);
                }
                System.out.println("sendsCheak");
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("exc re " + Arrays.toString(re));
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
//            System.out.println("sending "+Utils.byteArrayToInt(pre));
            j = 0;
        }
        System.out.println("over");
        userContext.deltask(id);
    }

    public void sendAsy(SendCall sendRe, byte[] cheak) {
        callPoll = new CallPoll() {
            long time0 = System.currentTimeMillis();
            long reqTime = System.currentTimeMillis();
            long time = 3000;
            int t = 0;
            byte[] bytes;
            byte[] b;
            boolean iso = false;
            int pos;
            int k;
            boolean r = true;

            int j = 0;

            @Override
            public Long runTime() {
                try {
                    long waitTime = System.currentTimeMillis() - time0;
                    long waitReq = System.currentTimeMillis() - reqTime;
                    sendRe.readAsy();
                    byte[] re = sendRe.queue.poll();
                    if (re == null) {
                        if (waitReq > time) {
                            time=time+time;
                            senders.sendSym(cheak);
                            reqTime = System.currentTimeMillis();
                            System.out.println("send(send)   " + j);
                        }
                        if (waitTime > 3000) {
                            time0 = System.currentTimeMillis();
                            if (j > 3) {
                                UserContext uc = UDPclient.mainDataQueue.contrainUser(userContext.userName);
                                if (uc == null) {
                                    CallExecutor.remove(asySteam);
                                    return -1L;
                                } else {
                                    if (userContext.getQueue(id) == null) {
                                        CallExecutor.remove(asySteam);
                                        return -1L;
                                    }
                                    if (j % 3 == 0) {
                                        if (userContext != uc) {
                                            senders.InitInit(id, uc);
                                        }
                                    }
                                    if (j > 13) {
                                        state = false;
                                        CallExecutor.remove(asySteam);
                                        return -1L;
//                                break;
                                    }
                                }
                            }
                            j++;
                            int index = sendRe.posR % sendRe.bytel.length;
                            send = sendRe.bytess[index];
                            senders.send0(send, 0, sendRe.bytel[index] + 14);
                            System.out.println("sendpr+ " + sendRe.posR);
                            index = (sendRe.getPosWBatis(-1)) % sendRe.bytel.length;
                            send = sendRe.bytess[index];
                            senders.send0(send, 0, sendRe.bytel[index] + 14);
                            System.out.println("sendpw+ " + sendRe.posW);

                            System.out.println("sendsCheak");

                        }
                    } else {
                        time=3000;
                        if (re.length != 4) {
                            String s = new String(re, 0, 2);
                            if ("WA".equals(s)) {
                                j = 0;
                                return time;
                            }
                            if ("OK".equals(s) || "As".equals(s)) {
                                state = false;
                                if (readyTimes>0){
                                    readyTimes--;
                                    return time;
                                }
                                CallExecutor.remove(asySteam);
                                return -1L;
                            }
                            if ("sA".equals(s) && re.length == 2) {
                                return time;
                            }
                            j = 0;
                            return time;
                        }
                        j = 0;
                        try {
                            int p = Utils.byteArrayToInt(re);
                            if (sendRe.isBetween(p)) {
                                int index = p % sendRe.bytel.length;
                                send = sendRe.bytess[index];
                                senders.send0(send, 0, sendRe.bytel[index] + 14);
                                System.out.println("send+ " + p);
                            } else {
                                int index = sendRe.posR % sendRe.bytel.length;
                                int len = sendRe.bytel[index];
                                if (len < 0) {
                                    send = sendRe.bytess[index];
                                    senders.send("WA".getBytes());
                                    System.out.println("send+ WA");
                                } else {
                                    send = sendRe.bytess[index];
                                    senders.send0(send, 0, sendRe.bytel[index] + 14);
                                    System.out.println("send+ " + p);
                                }

                            }

                        } catch (ArrayIndexOutOfBoundsException e) {
                            System.out.println("AIO re " + Arrays.toString(re));
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public boolean isOver() {
                return false;
            }
        };
    }


    //":cloudefile&:"
    public InputStream praseStream(BufferRequest req) {
        String[] strings = null;
        String bufName = req.bufname;
        try {
            bufName = URLDecoder.decode(bufName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] bytes = new byte[0];

        String s = bufName.substring(0, 1);
        int i = bufName.indexOf("&:");
        if (":".equals(s) && i != -1) {
            s = bufName.substring(1, i);
            strings = bufName.substring(1).split("&:", 2);
            switch (s) {
                case "wait": {
                    bytes = BufferDataCon.getData(bufName);
                    if (bytes == null) {
//                        waitMap.put(req,this);
                    }
                    break;
                }
                case "data": {
                    break;
                }
                case "datafl": {
                    File file = new File(strings[1]);
                    try {
                        FileInputStream inputStream = new FileInputStream(file);
                        return inputStream;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                default: {
                    if (rev != null) return rev;
                }
            }
        }
        return null;
    }

    @Override
    public int hashCode() {
        return userContext.userName.hashCode() & id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        AsySteam a = (AsySteam) o;
        return this.userContext.getBothId() == a.userContext.getBothId() && this.id == a.id;
    }

    @Override
    public String toString() {
        if (bdr != null) {
            return userContext.userName + " + " + id + " : " + JSON.toJSONString(bdr);
        }
        return JSON.toJSONString(userContext.userName + " + " + id);
    }

    public int getprogress() {
        return 0;
    }

    //    @Override
//    public void finalize(){
//        System.out.println("userContext.deltask(id): "+id);
//        userContext.deltask(id);
////        new Exception("追踪抛出").printStackTrace();
//    }
    public int clear() {
        System.out.println("userContext.deltask(id): " + id);
        userContext.deltask(id);
        DataMap.remove(this);
        state=false;
        if(sthread==null || sthread==Thread.currentThread()){
        }else {
            sthread.interrupt();
        }
//        CallExecutor.remove(this);
        return id;

//        new Exception("追踪抛出").printStackTrace();
    }

    public static long waitTime(int t, long time, long timeo) {
        if (t == 0) {
            time = time / 2 + 2;
        } else {
            if (time < timeo) {
                time = (time + 2) * 2;
            }
            long ti = safeMultiply(time, toTime(t));
            if (ti > time) {
//                if (ti >= timeo*10) {
//                    ti = timeo*10;
//
//                }
                return ti;
            }
        }
        return time;
    }

    //    public static boolean cheakByte(byte[] bytes,)
    public static long toTime(int j) {
        double v = 1;
        if (j > 7) {
            j = 8;
        }
        for (; j > 0; j--) {
            v = v + (v * Math.abs(j - 3));
        }
        v = Math.abs(v);
        return (long) v;
    }

    public static long safeMultiply(long a, long b) {
        // 使用 Math.multiplyExact 检测溢出，但捕获异常
        try {
            return a + b;
        } catch (ArithmeticException e) {
            return a;
        }
    }

    public static void main(String[] args) {
        byte[] bytes = new byte[65535];
        Random random = new Random();
        random.nextBytes(bytes);
        for (int i = 0; i < bytes.length; i++) {

        }
        CRC32 crc32 = new CRC32();
        crc32.update(bytes);
        crc32.getValue();
    }

}
