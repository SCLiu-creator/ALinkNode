package superlink.udpbind.client.recives.datalen;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.annotation.JSONField;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;
import superlink.filemanage.xmltool.XmlCreate;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.ByteBufer;
import superlink.udpbind.client.recives.DataLenMange;
import superlink.udpbind.client.recives.DataReCallBuffer;
import superlink.udpbind.client.recives.Senders;
import superlink.udpbind.client.recives.datalen.dataAsy.*;
import superlink.udpbind.client.recives.datalen.dataCache.BufferDataCon;
import superlink.udpbind.cloude.show.ShowBin;
import superlink.udpbind.cloude.show.UserShowContainer;
import superlink.udpbind.usedata.BufferRequest;
import superlink.util.Utils;

import java.io.*;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.zip.CRC32;


public class AutoAsyBuffer extends DataLength {
    @JSONField(serialize = false)
    public Senders senders;
    @JSONField(serialize = false)
    public ByteBufer blockingQueue;
    @JSONField(serialize = false)
    public byte[] rev;
    @JSONField(serialize = false)
    public static ConcurrentHashMap<AutoAsyBuffer, AutoAsyBuffer> DataMap = new ConcurrentHashMap<>();

    CRC32 crc32 = new CRC32();

    public AutoAsyBuffer(String username) {
        System.out.println("revor");
        this.userContext = UDPclient.mainDataQueue.getUserContext(username);
        this.id = userContext.newQueue(256);
        blockingQueue = userContext.getDataQue(this.id);
        this.senders = new Senders().Init(this.id, username);
        setPagelen(DataLenMange.getLen(username));
        userContext.getTask((short) id).task=this;
    }

    public AutoAsyBuffer(String username, int id) {
        System.out.println("sendor");
        this.userContext = UDPclient.mainDataQueue.getUserContext(username);
        this.id = (short) id;
        blockingQueue = userContext.getDataQue(this.id);
        this.senders = new Senders().Init(this.id, username);
        userContext.getTask((short) id).task=this;
    }

    //使用b
    public byte[] reqData(String remoteFilename) throws FileNotFoundException {
        BufferRequest sdr = new BufferRequest();
        sdr.name = UDPclient.userlocal.username;
        sdr.bufname = remoteFilename;
        sdr.id = id;
        sdr.pl = pagelen;
        this.bdr = sdr;
        DataMap.put(this, this);
        byte[] dt = ("Ab" + JSON.toJSONString(sdr)).getBytes();
        senders.sendSym(dt);
        long time = System.currentTimeMillis();
        int i = 0;
        BufferRequest dataRequest=null;
        send = dt;
        while (true) {
            if (i > 5) {
                return null;
            }
            try {
                byte[] star = blockingQueue.poll(5, TimeUnit.SECONDS);
                String re = new String(star);
                if (re.equals("bA")) {
                    return null;
                } else {
                    if (re.equals("WA")) {
                        i = 0;
                        continue;
                    }
                    dataRequest = JSON.parseObject(re.substring(2), BufferRequest.class);
                    break;
                }
            } catch (Exception e) {
                System.out.println("Auto Timeout");
            }
            senders.sendSym(dt);
            i++;
        }
        send = Utils.byteMerger("bA".getBytes(), Utils.shortToByteArray((short) id));
        long t2 = System.currentTimeMillis();
        time = t2 - time;

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
            userContext.setQueue((short) id, bufer);

            rev = (byte[]) req(dataRequest, time,bufer);
        }

        senders.send("OK".getBytes());

        System.out.println(i);
        System.out.println("over");
        clear();
        return rev;//new ByteArrayInputStream(rev);
    }

    public byte[] reqData(String remoteFilename, int start, int len) {
        BufferRequest sdr = new BufferRequest();
        sdr.name = UDPclient.userlocal.username;
        sdr.bufname = remoteFilename;
        sdr.id = id;
        sdr.page = start;
        sdr.l = len;
        sdr.pl = pagelen;
        this.bdr = sdr;
        DataMap.put(this, this);
        byte[] dt = ("Ab" + JSON.toJSONString(sdr)).getBytes();
        senders.sendSym(dt);
        Long time = System.currentTimeMillis();
        byte[] star = null;
        int i = 0;
        return null;
    }

    public byte[] reqDataAsy(String remoteFilename) {
        return null;

    }

    public volatile byte[] send = null;

    public AutoAsyBuffer getBuf(BufferRequest sdr) {
        this.bdr = sdr;
        AutoAsyBuffer autoData = DataMap.get(this);
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
            senders.send("bA".getBytes());
        } else {
            senders.send(send);
        }
    }

    //使用bA
    @Override
    public void run() {
        send = ("WA").getBytes();
//        senders.sendSym(send);
//        senders.sendSym(send);
        senders.send(send);
        byte[] bytes = praseBuffer(bdr);
//        send=Utils.byteMerger(("bA").getBytes(),Utils.intToByteArray(id));
        pagelen = bdr.pl;
        if (bytes == null) {
            send = Utils.byteMerger(("WA").getBytes(), Utils.intToByteArray(id));
//            senders.sendSym(send);
            senders.send(send);
            return;
        }
        try {
            sends(bdr, bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            clear();
        }
    }


    private ReCallCon reCallCon;

    public boolean state = true;

    DataReader reader;

    CallPoll callPoll;
    public Object ro;
    public Object req(BufferRequest dataRequest, long timeLong,DataReCallBuffer bufer) throws FileNotFoundException {
        long time = timeLong;
        int i = 0;
        byte[] cheak = Utils.byteMerger(("bA").getBytes(), Utils.intToByteArray(id));
        int t = 0;
        System.out.println("page:  " + dataRequest.page);
        byte[] bytes = null;
        long time0 = System.currentTimeMillis();
        try {
            callPoll=new CallPoll() {
                long time0 = System.currentTimeMillis();
                long time=timeLong;
                int t=0;
                byte[] bytes;
                byte[] b;
                @Override
                public Long runTime() {
                    if (reCallCon.gets()) {
                        t=0;
                        b = Utils.byteMerger(Utils.intToByteArray(reCallCon.posR - 1), "DEL".getBytes());
                        senders.send(b);
                        time0=System.currentTimeMillis();
                        return time/2;
                    } else {
                        long waitTime = System.currentTimeMillis() - time0;
                        if (waitTime < timeLong) {
                            if (blockingQueue.size() > 0) {
                                bytes = blockingQueue.poll();
                                System.out.println("poll  "+new String(bytes));
                                //todo
                                return time;
                            }
                            waitTime = System.currentTimeMillis() - time0;
                            if(waitTime<timeLong){
                                try {
                                    return time-waitTime;

                                } catch (Exception interruptedException) {
                                    interruptedException.printStackTrace();
                                }
                            }
                        }

                        t++;
                        b = Utils.byteMerger(Utils.intToByteArray(reCallCon.posR - 1), "DEL".getBytes());
                        senders.send(b);
//                            senders.send(Utils.intToByteArray(k));
                        for (int k = reCallCon.posR; k < reCallCon.posR + 128 && k < reCallCon.allPage; k++) {
                            if (reCallCon.bytel[k % reCallCon.bytel.length] == 0) {
                                senders.send(Utils.intToByteArray(k));
                                System.out.println("recCheak  "+k);
                                send = Utils.intToByteArray(k);
                            }
                        }
                        if (reCallCon.writer.getState()) {
                            return -1L;
                        }
                    }
                    if (t >= 4) {
                        UserContext userC = UDPclient.mainDataQueue.contrainUser(userContext.userName);
                        if (userC == null) {
                            return null;
                        } else {
                            if(userC!=userContext){
                                userContext=userC;
                                senders.InitInit(id,userContext);
                                userContext.setQueue((short)id,bufer);
                            }
                            if (t > 11 && time>1500) {
                                return null;
                            }
                            if (t % 3 == 0) {
                                ByteBufer ub = userC.getQueue(id);
                                if (ub != blockingQueue && ub != bufer) {
                                    return null;
                                }
                                senders.sendSym(cheak);
                            }
                        }
                    }
                    time=waitTime(t,time,timeLong);
                    t++;
                    return time;
                }

                @Override
                public boolean isOver() {
                    return false;
                }
            };

            while (true) {
                if (reCallCon.gets()) {
                    t = 0;
                    continue;
                } else {
                    long waitTime = System.currentTimeMillis() - time0;
                    if (waitTime < timeLong) {
                        if (blockingQueue.size() > 0) {
                            bytes = blockingQueue.poll();
                            System.out.println("poll  "+new String(bytes));
                            //todo
                            continue;
                        }
                        waitTime = System.currentTimeMillis() - time0;
                        if(waitTime<timeLong){
                            try {
                                Thread.sleep(timeLong - waitTime);
                                time0=System.currentTimeMillis();
                            } catch (Exception interruptedException) {
                                interruptedException.printStackTrace();
                            }
                        }
                    }

                    t++;
                    byte[] b;
                    b = Utils.byteMerger(Utils.intToByteArray(reCallCon.posR - 1), "DEL".getBytes());
                    senders.send(b);
//                            senders.send(Utils.intToByteArray(k));
                    for (int k = reCallCon.posR; k < reCallCon.posR + 128 && k < reCallCon.allPage; k++) {
                        if (reCallCon.bytel[k % reCallCon.bytel.length] == 0) {
                            senders.send(Utils.intToByteArray(k));
                            System.out.println("recCheak  "+k);
                            send = Utils.intToByteArray(k);
                        }
                    }
                    if (reCallCon.writer.getState()) {
                        break;
                    }
                }


                if (t >= 4) {
                    UserContext userContext = UDPclient.mainDataQueue.contrainUser(this.userContext.userName);
                    if (userContext == null) {
                        return null;
                    } else {
//                            if (j > 133) {
//                                break;
//                            }
                        if (t % 3 == 0) {
                            ByteBufer ub = userContext.getQueue(id);
                            if (ub != this.blockingQueue && ub != bufer) {
                                break;
                            }
                            senders.sendSym(cheak);
//                            else {
//                                this.userContext=userContext;
//                                this.senders.InitInit(this.id,userContext);
//                            }
                        }
                    }
                }

                time=waitTime(t,time,timeLong);
                t++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            senders.send("OK".getBytes());
        }

        return ro;
    }


    public void sends(BufferRequest sdr, byte[] data) {
        this.bdr = sdr;
        sdr.page = Math.toIntExact(data.length / pagelen);
        sdr.l = data.length;
        if ((data.length % pagelen) != 0) {
            sdr.page += 1;
        }
        SendRe sendRe = new SendRe(data.length, pagelen, 128);
        sendRe.setSenders(senders);
        reader = new DataReader() {
            volatile int readPos = 0;

            public int read(byte[] buf, byte[]... bytess) {//feiqi
                int s = 0;
                for (byte[] bytes : bytess) {
                    System.arraycopy(bytes, 0, buf, s, bytes.length);
                    s = s + bytes.length;
                }
                return s;
            }

            public int read(byte[] buf, byte[] sendhead, byte[] prex, byte[] cc, byte[] dataorg, int poss, int len) {
                int s = 0;
                System.arraycopy(sendhead, 0, buf, s, sendhead.length);
                s = s + sendhead.length;

                System.arraycopy(prex, 0, buf, s, prex.length);
                s = s + prex.length;

                System.arraycopy(dataorg, poss, buf, s, len);
                readPos = readPos + len;
                s = s + len;

                System.arraycopy(cc, 0, buf, s, cc.length);
//                s=s+cc.length;
                return len;
            }

            public int read(byte[] buf, int readTimes) {
                byte[] pre = Utils.intToByteArray(readTimes);
                crc32.reset();
                int len = pagelen;
                if (readPos + pagelen > data.length) {
                    len = data.length - readPos;
                }
                crc32.update(pre, 0, 4);
                crc32.update(data, readPos, len);
                byte[] cc = Utils.intToByteArray((int) crc32.getValue());
                return read(buf, senders.getPrex(), pre, cc, data, readPos, len);
            }
        };
        DataReCallBuffer bufer = new DataReCallBuffer();
        sendRe.reader = reader;
        bufer.reCallCon = sendRe;
        userContext.setQueue((short) id, bufer);
        byte[] send = ("DI" + JSON.toJSONString(sdr)).getBytes();
        senders.send(send);
        byte[] cheak = Utils.byteMerger(("bA").getBytes(), Utils.intToByteArray(id));
        byte[] bytes = new byte[pagelen];
        int p = 0;
        byte[] re = null;
        String s = "";
        int j = 0;

        try {
            Thread.sleep(10);
        } catch (InterruptedException ignored) {
        }

        while (state) {
            try {
                sendRe.read();
                re = sendRe.queue.poll(3, TimeUnit.SECONDS);
                if (re.length != 4) {
                    s = new String(re, 0, 2);
                    if ("OK".equals(s) || "b".equals(s)) {
                        state = false;
                        break;
                    }
//                    if (  re[0]=='B'&&re[1]=='A' && re.length==2){
                    if ("bA".equals(s) && re.length == 2) {
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
//                                state = false;
//                                break;
                            }
                        }
                    }
                    j++;
//                    int index = sendRe.posR % sendRe.bytel.length;
//                    send = sendRe.bytess[index];
//                    senders.send0(send, 0, sendRe.bytel[index] + 14);
//                    System.out.println("sendpr+ " + sendRe.posR);
//                    index = (sendRe.posW-1) % sendRe.bytel.length;
//                    send = sendRe.bytess[index];
//                    senders.send0(send, 0, sendRe.bytel[index] + 14);
//                    System.out.println("sendpw+ " + sendRe.posW);
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
//        byte[] bytesss=cache[cache.length-1];
//        l=bytesss.length;
//        l=((cache.length-1)*fp)+l;
//        rev=new byte[(int) l];
//        int i=0;
//        for (byte[] bt:cache){
//            System.arraycopy(bt,0,rev,i*fp,bt.length);
//            i++;
//        }
//        String string=new String(rev);
//        System.out.println(string);
        System.out.println("over");
        userContext.deltask(id);
    }

    public static ConcurrentHashMap waitMap = new ConcurrentHashMap();

    //":cloudefile&:"
    public byte[] praseBuffer(BufferRequest req) {
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
                case "xmlfile": {
                    ShowBin showBin = UserShowContainer.getLocalShowBin();
                    Document document = DocumentFactory.getInstance().createDocument();
                    Element element = document.addElement("p");
                    List<String> lists = showBin.get();
                    String po = null;
                    for (String s1 : lists) {
                        if (strings[1].contains(s1)) {
                            if (po == null) {
                                po = s1;
                            }
                        }
                    }
                    if (po == null) {
                        bytes = new byte[0];
                        break;
                    }
                    UserShowContainer.writeXml(element, new File(strings[1]), 2);
                    ByteArrayOutputStream bao = new ByteArrayOutputStream();
                    try {
                        XMLWriter xmlWriter = new XMLWriter(bao);
                        xmlWriter.write(document);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    bytes = bao.toByteArray();
//                    String string = new String( bytes);
//                    System.out.println(string);
//                    document=XmlParser.byetToDocument(bytes);
//                    Element er=document.getRootElement();
                    break;
                }
                case "datafl": {
                    File file = new File(strings[1]);
                    try {
                        FileInputStream inputStream = new FileInputStream(file);
                        bytes = new byte[(int) req.l];
                        inputStream.skip(req.page);
                        inputStream.read(bytes);
                        inputStream.close();
                    } catch (Exception e) {
                        bytes = new byte[0];
                        e.printStackTrace();
                    }
                    break;
                }
                case "chats": {
                    File file = new File(XmlCreate.userChat + "bin.xml");
                    try {
                        long length = file.length();
                        FileInputStream inputStream = new FileInputStream(file);
                        bytes = new byte[(int) length];
                        inputStream.read(bytes);
                        inputStream.close();
                    } catch (Exception e) {
                        bytes = new byte[0];
                        e.printStackTrace();
                    }
                    break;
                }
                case "chat": {
                    File file = new File(XmlCreate.userChat + "bin.xml");
                    try {
                        long length = file.length();
                        FileInputStream inputStream = new FileInputStream(file);
                        bytes = new byte[(int) length];
                        inputStream.read(bytes);
                        inputStream.close();
                    } catch (Exception e) {
                        bytes = new byte[0];
                        e.printStackTrace();
                    }
                    break;
                }
                default: {
                    if (rev != null) return rev;
                }
            }
        }
        return bytes;
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
        return (int) Math.floor((float) reCallCon.writer.getLen() * 100 / bdr.l * 1442);
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
        return id;
//        new Exception("追踪抛出").printStackTrace();
    }

    public static long waitTime(int t,long time,long timeo) {
        if (t == 0) {
            if (time < 1024) {
                time = time * 2 + 2;
            }
            if (time >= timeo*8) {
                time = timeo;
            }
        } else {
            time = time / 2+2;
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

    public long safeMultiply(long a, long b) {
        // 使用 Math.multiplyExact 检测溢出，但捕获异常
        try {
            return a + b;
        } catch (ArithmeticException e) {
            return Long.MAX_VALUE;
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
