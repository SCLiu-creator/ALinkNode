package superlink.udpbind.client.recives.datalen;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.annotation.JSONField;
import superlink.filemanage.xmltool.XmlCreate;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.ByteBufer;
import superlink.udpbind.client.recives.DataLenMange;
import superlink.udpbind.client.recives.DataReCallBuffer;
import superlink.udpbind.client.recives.Senders;
import superlink.udpbind.client.recives.datalen.dataAsy.*;
import superlink.udpbind.usedata.BufferRequest;
import superlink.util.Utils;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.CRC32;

import static superlink.udpbind.client.recives.datalen.AsyBuffer.waitTime;

public class AutoAsyFile extends DataLength {
    @JSONField(serialize = false)
    public Senders senders;
    @JSONField(serialize = false)
    public ByteBufer blockingQueue;
    @JSONField(serialize = false)
    public byte[] rev;
    @JSONField(serialize = false)
    public static ConcurrentHashMap<AutoAsyFile, AutoAsyFile> DataMap = new ConcurrentHashMap<>();

    CRC32 crc32 = new CRC32();

    Callable callable;

    public Callable setCallable(Callable call) {
        this.callable=call;
        return callable;
    }

    public AutoAsyFile(String username) {
        System.out.println("revor");
        this.userContext = UDPclient.mainDataQueue.getUserContext(username);
        this.id = userContext.newQueue(256);
        blockingQueue = userContext.getDataQue(this.id);
        this.senders = new Senders().Init(this.id, username);
        setPagelen(DataLenMange.getLen(username));
        userContext.getTask((short) id).task=this;
    }

    public AutoAsyFile(String username, int id) {
        System.out.println("sendor");
        this.userContext = UDPclient.mainDataQueue.getUserContext(username);
        this.id = (short) id;
        blockingQueue = userContext.getDataQue(this.id);
        this.senders = new Senders().Init(this.id, username);
        userContext.getTask((short) id).task=this;
    }

    //使用Ad
    AtomicReference<BufferRequest> dataRequest=new AtomicReference<>();
    CallPoll callPoll=new CallPoll() {
        int i = 0;
        boolean b=false;
        long time1=System.currentTimeMillis();
        @Override
        public Long runTime() {
            try {
                byte[] star = blockingQueue.poll();
                if(star==null){
                    if (i > 5) {
                        return null;
                    }
                    long tw=System.currentTimeMillis()-time1;
                    time1=System.currentTimeMillis();
                    tw=3500L-tw;
                    if(tw>0){
                        return tw;
                    }else {
                        i++;
                        System.out.println("Auto Timeout");
                        senders.sendSym(send);
                        return 3500L;
                    }

                }else {
                    String re = new String(star);
                    if (re.equals("dA")) {
                        return null;
                    }
                    if (re.equals("WA")) {
                        i = 0;
                        time1=System.currentTimeMillis();
                        return 5000L;
                    }
                    dataRequest.set(JSON.parseObject(re.substring(2), BufferRequest.class));
                    return -1L;
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
    //使用b
    public Object reqFile(String remoteFilename) throws FileNotFoundException {
        BufferRequest sdr = new BufferRequest();
        sdr.name = UDPclient.userlocal.username;
        sdr.bufname = remoteFilename;
        sdr.id = id;
        sdr.pl = pagelen;
        this.bdr = sdr;
        DataMap.put(this, this);
        return reqFile(sdr);
    }
    public Object reqFile(BufferRequest sdr) throws FileNotFoundException {
        DataMap.put(this, this);
        send = ("Ad" + JSON.toJSONString(sdr)).getBytes();
        senders.sendSym(send);
        long t1=System.currentTimeMillis();
        Long time;

        while (true){
            time = callPoll.runTime();
            if(time==null)return null;
            if(time>0){
                try {
                    Thread.sleep(1);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }else {
                break;
            }
        }
//        send = Utils.byteMerger("dA".getBytes(), Utils.shortToByteArray((short) id));
        long t2 = System.currentTimeMillis();
        time = t2 - t1;

        BufferRequest dataRequest=this.dataRequest.get();
        this.bdr =dataRequest;
        if (dataRequest.page <= 0) {
            return new byte[0];
        } else {
            byte[] bbyte = new byte[0];;
            reCallCon = new ReCallCon((int) dataRequest.l, pagelen, 128);
            if (dataRequest.l > 1024 * 1024 * 10) {
                File bfile = new File(XmlCreate.userCache + UUID.randomUUID());
                OutputStream outputStream = new FileOutputStream(bfile);
                BufferRequest finalDataRequest = dataRequest;
                reCallCon.writer = new DataWriter() {
                    int len = 0;

                    @Override
                    public int getLen() {
                        return len;
                    }

                    @Override
                    public boolean getState() {
                        if (len == finalDataRequest.l) {
                            ro=bfile;
                            try {
                                outputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return true;
                        }
                        return false;
                    }

                    @Override
                    public void add(byte[] bytes, int pos, int length) {
//                        if(len+pos>sdr.l){
//                            len= (int) (sdr.l-pos);
//                        }
                        try {
                            outputStream.write(bytes, pos, length);
                            len = len + length;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                };
            } else {
                bbyte = new byte[(int) dataRequest.l];
                byte[] finalBbyte = bbyte;
                BufferRequest finalDataRequest = dataRequest;
                reCallCon.writer = new DataWriter() {
                    int len = 0;

                    @Override
                    public int getLen() {
                        return len;
                    }

                    @Override
                    public boolean getState() {
                        if (len == finalDataRequest.l) {
                            ro=finalBbyte;
                            rev=finalBbyte;
                            return true;
                        }
                        return false;
                    }

                    @Override
                    public void add(byte[] bytes, int pos, int length) {
                        try {
                            System.arraycopy(bytes, pos, finalBbyte, len, length);
                            len = len + length;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
            }
            DataReCallBuffer bufer = new DataReCallBuffer();
            bufer.reCallCon = reCallCon;
            reCallCon.queue = blockingQueue;
            reCallCon.readQue(blockingQueue);
            userContext.setQueue((short) id, bufer);

            callPoll = req(dataRequest, time,bufer,this);
            try {
                while (true){
                    time = callPoll.runTime();
                    if(time==null)return null;
                    if(time>0){
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }
                    }else {
                        System.out.println("break");
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                senders.send("OK".getBytes());
            }

        }
        if(ro instanceof byte[]){
            rev= (byte[]) ro;
        }
        if(ro instanceof File){
            try {
                rev= Files.readAllBytes(((File)ro).toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return rev;//new ByteArrayInputStream(rev);
    }

    public Object reqData(String remoteFilename, int start, int len) throws FileNotFoundException {
        BufferRequest sdr = new BufferRequest();
        sdr.name = UDPclient.userlocal.username;
        sdr.bufname = remoteFilename;
        sdr.id = id;
        sdr.page = start;
        sdr.l = len;
        sdr.pl = pagelen;
        this.bdr = sdr;
        DataMap.put(this, this);
        return reqFile(sdr);
    }

    public Object ro;
    public CallPoll req(BufferRequest dataRequest, long timeLong,DataReCallBuffer bufer,AutoAsyFile se) throws FileNotFoundException {
        System.out.println("page:  " + dataRequest.page);
        callPoll=new CallPoll() {
            long time0 = System.currentTimeMillis();
            long reqTime=System.currentTimeMillis();
            long time=timeLong;
            int t=0;
            byte[] bytes;
            byte[] b;
            boolean iso=false;
            int pos;
            int k;
            boolean r=true;
            @Override
            public Long runTime() {
                long waitTime = System.currentTimeMillis() - time0;
                long waitReq = System.currentTimeMillis() - reqTime;
                if (reCallCon.writer.getState()) {
                    iso=true;
                    senders.send("OK".getBytes());
                    senders.send("OK".getBytes());
                    System.out.println("over");
                    se.clear();
                    return -1L;
                }

                if(reCallCon.gets()){
                    b = Utils.byteMerger(Utils.intToByteArray(reCallCon.posR - 1), "DEL".getBytes());
                    senders.send(b);
                    t=0;
                }
                if (waitReq<time) {
                    if (pos<reCallCon.posR) {
                        pos=reCallCon.posR;
                        if(time0!=reqTime){
                            time=waitTime(t,time,timeLong);
                        }
                    }
                    time0=System.currentTimeMillis();
                    return time;
                } else {
                    if (waitTime < time) {
                        if (blockingQueue.size() > 0) {
                            bytes = blockingQueue.poll();
                            System.out.println("poll  "+new String(bytes));
                            if(new String(bytes).equals("WA")){
                                t=0;
                                time=timeLong*2;
                            }
                            //todo
                        }
                        waitTime = System.currentTimeMillis() - time0;
                        if(waitTime<time){
                            try {
                                waitTime = time-waitTime;
                                return waitTime;
                            } catch (Exception interruptedException) {
                                interruptedException.printStackTrace();
                            }
                        }
                        if(waitTime<timeLong){
                            time =waitTime(t,waitTime,timeLong);
                            time0=System.currentTimeMillis();
                            senders.send(b);
                            senders.send(send);
                            return time;
                        }
                        return time-waitTime;
                    }

                    t++;

                    b = Utils.byteMerger(Utils.intToByteArray(reCallCon.posR - 1), "DEL".getBytes());
                    senders.send(b);
//                            senders.send(Utils.intToByteArray(k));
                    pos=reCallCon.posR;
                    for (k = reCallCon.posR; k < reCallCon.posR + 126 && k < reCallCon.allPage; k++) {
                        if (reCallCon.bytel[k % reCallCon.bytel.length] == 0) {
                            senders.send(Utils.intToByteArray(k));
                            System.out.println("recCheak  "+k);
                            send = Utils.intToByteArray(k);
                        }
                    }
                    reqTime = System.currentTimeMillis();
                }
                if (t >= 4) {
                    UserContext userC = UDPclient.mainDataQueue.contrainUser(userContext.userName);
                    if (userC == null) {
                        iso=true;
                        return null;
                    } else {
                        if(userC!=userContext){
                            userContext=userC;
                            senders.InitInit(id,userContext);
                            userContext.setQueue((short)id,bufer);
                        }
                        if (t > 11 && time>1500) {
                            iso=true;
                            return null;
                        }
                        if (t % 3 == 0) {
                            ByteBufer ub = userC.getQueue(id);
                            if (ub != blockingQueue && ub != bufer) {
                                iso=true;
                                return null;
                            }
                            senders.sendSym(Utils.byteMerger(("dA").getBytes(), Utils.intToByteArray(id)));
                        }
                    }
                }
                time=waitTime(t,time,timeLong);
                time0=System.currentTimeMillis();

                return time;
            }

            @Override
            public boolean isOver() {
                return  iso;
            }
        };
        return callPoll;
    }



    public volatile byte[] send = null;

    public AutoAsyFile getBuf(BufferRequest sdr) {
        this.bdr = sdr;
        AutoAsyFile autoData = DataMap.get(this);
        if (autoData == null) {
            DataMap.put(this, this);
            return this;
        } else {
            return autoData;
        }
    }

    public void execute(boolean b) {
        if (b) {
            setThreadPool.reExecute(this);
        } else {
            run();
        }
    }

    public void aSend() {
        if (send == null) {
            senders.send("dA".getBytes());
        } else {
            senders.send(send);
        }
    }

    //使用dA
    @Override
    public void run() {
        try {
            send = ("WA").getBytes();
//        senders.sendSym(send);
            senders.send(send);
            bdr.bufname= Utils.pathPrase(bdr.bufname).path;
            File file=new File(bdr.bufname);
            bdr.l=file.length();
            pagelen= bdr.pl;
//        send=Utils.byteMerger(("dA").getBytes(),Utils.intToByteArray(id));
            pagelen = bdr.pl;
            if (bdr.bufname == null) {
                send=Utils.byteMerger(("dA").getBytes(),Utils.intToByteArray(id));
//            send = Utils.byteMerger(("WA").getBytes(), Utils.intToByteArray(id));
//            senders.sendSym(send);
                senders.send(send);
                return;
            }
            sends(bdr, file);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            clear();
        }
    }

    private ReCallCon reCallCon;

    public boolean state = true;

    DataReader reader;


    public void sends(BufferRequest sdr, File file) throws FileNotFoundException {
        this.bdr = sdr;
        int start = sdr.page;
        int slen = (int) sdr.l;
        sdr.page = Math.toIntExact(file.length() / pagelen);
        if(file.exists()){
            sdr.l = file.length();
            if ((file.length() % pagelen) != 0) {
                sdr.page += 1;
            }
        }else {
            sdr.l=0L;
            sdr.page=0;
            senders.ssendSymRe(("DI" + JSON.toJSONString(sdr)).getBytes(), (short) id);
            send=Utils.byteMerger(("dA").getBytes(),Utils.intToByteArray(id));
//            send = Utils.byteMerger(("WA").getBytes(), Utils.intToByteArray(id));
//            senders.sendSym(send);
            senders.send(send);
            return;
        }

        InputStream fileInputStream=new FileInputStream(file);
        SendRe sendRe=null;
        if(start!=-1){
            try{
                byte[] b = new byte[slen];
                fileInputStream.skip(start);
                fileInputStream.read(b);
                fileInputStream=new ByteArrayInputStream(b);
            }catch (Exception e){
                sdr.l=0L;
                sdr.page=0;
                senders.ssendSymRe(("DI" + JSON.toJSONString(sdr)).getBytes(), (short) id);
                return;
            }
            sendRe = new SendRe(slen, pagelen, 128);
        }else {
            sendRe = new SendRe((int) file.length(), pagelen, 128);
        }

        sendRe.setSenders(senders);
        InputStream finalFileInputStream = fileInputStream;
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

            final byte[] data=new byte[pagelen];
            public int read(byte[] buf, int readTimes) {
                byte[] pre = Utils.intToByteArray(readTimes);
                crc32.reset();
                crc32.update(pre, 0, 4);
                int len = 0;
                try {
                    len = finalFileInputStream.read(data);
                } catch (IOException e) {
                    e.printStackTrace();
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
        byte[] send = ("DI" + JSON.toJSONString(sdr)).getBytes();
        senders.send(send);
        byte[] cheak = Utils.byteMerger(("dA").getBytes(), Utils.intToByteArray(id));
        int p = 0;
        byte[] re = null;
        String s = "";
        int j = 0;

        while (state) {
            try {
                sendRe.read();
                re = sendRe.queue.poll(3, TimeUnit.SECONDS);
                if (re.length != 4) {
                    s = new String(re, 0, 2);
                    if ("OK".equals(s) || "Ad".equals(s)) {
                        state = false;
                        break;
                    }
//                    if (  re[0]=='B'&&re[1]=='A' && re.length==2){
                    if ("dA".equals(s) && re.length == 2) {
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
                            }
                        }
                    }
                    j++;
                    int index = sendRe.posR % sendRe.bytel.length;
                    send = sendRe.bytess[index];
                    senders.send0(send, 0, sendRe.bytel[index] + 14);
                    System.out.println("sendpr+ " + sendRe.posR);
                    index = (sendRe.posW-1) % sendRe.bytel.length;
                    send = sendRe.bytess[index];
                    senders.send0(send, 0, sendRe.bytel[index] + 14);
                    System.out.println("sendpw+ " + sendRe.posW);
                }
                continue;
            }
            try {
                p = Utils.byteArrayToInt(re);
                if (sendRe.posR <= p && p < sendRe.posW) {
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


    @Override
    public int hashCode() {
        return userContext.getUserId() & id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        return this.hashCode() == o.hashCode();
    }

    @Override
    public String toString() {
        if (bdr != null) {
            return userContext.userName + " + " + id + " : " + JSON.toJSONString(bdr);
        }
        return JSON.toJSONString(userContext.userName + " + " + id);
    }

    public int getprogress() {
        if (reCallCon == null || reCallCon.writer == null) return 0;
        return (int) Math.floor((float) reCallCon.writer.getLen()*100 / bdr.l);
    }

    public int clear() {
        System.out.println("userContext.deltask(id): " + id);
        userContext.deltask(id);
        DataMap.remove(this);
        state=false;
        return id;
//        new Exception("追踪抛出").printStackTrace();
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
