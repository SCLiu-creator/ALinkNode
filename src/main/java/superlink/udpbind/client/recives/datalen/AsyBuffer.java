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
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.CRC32;


public class AsyBuffer extends DataLength {
    @JSONField(serialize = false)
    public Senders senders;
    @JSONField(serialize = false)
    public ByteBufer blockingQueue;
    @JSONField(serialize = false)
    public byte[] rev;
    @JSONField(serialize = false)
    public static ConcurrentHashMap<AsyBuffer, AsyBuffer> DataMap = new ConcurrentHashMap<>();

    CRC32 crc32 = new CRC32();

    public AsyBuffer(String username) {
        System.out.println("revor");
        this.userContext = UDPclient.mainDataQueue.getUserContext(username);
        this.id = userContext.newQueue(256);
        blockingQueue = userContext.getDataQue(this.id);
        this.senders = new Senders().Init(this.id, username);
        setPagelen(DataLenMange.getLen(username));
        userContext.getTask((short) id).task=this;
    }

    public AsyBuffer(String username, int id) {
        System.out.println("sendor");
        this.userContext = UDPclient.mainDataQueue.getUserContext(username);
        this.id = (short)id;
        blockingQueue = userContext.getDataQue(this.id);
        this.senders = new Senders().Init(this.id, username);
        userContext.getTask((short) id).task=this;
    }

    LinkedBlockingQueue<CallPoll> callPolls=new LinkedBlockingQueue<>();

    CallPoll callPoll=new CallPoll() {
        int i = 0;
        boolean b=false;
        long time1=System.currentTimeMillis();
        long tw;
        @Override
        public Long runTime() {
            try {
                byte[] star = blockingQueue.poll();
                if(star==null){
                    if (i > 5) {
                        return null;
                    }
                    tw=System.currentTimeMillis()-time1;
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
                    if (re.equals("bA")) {
                        return null;
//                        return -1L;
                    }
                    if (re.equals("WA")) {
                        time1=System.currentTimeMillis();
                        i = 0;
                        return 3500L;
                    }
                    bdr=JSON.parseObject(re.substring(2), BufferRequest.class);
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
    public byte[] reqData(String remoteFilename) throws FileNotFoundException {
        BufferRequest bufferRequest = new BufferRequest();
        bufferRequest.name = UDPclient.userlocal.username;
        bufferRequest.bufname = remoteFilename;
        bufferRequest.id = id;
        bufferRequest.pl = pagelen;
        this.bdr = bufferRequest;
        DataMap.put(this, this);
        send = ("Ab" + JSON.toJSONString(bdr)).getBytes();
        senders.sendSym(send);

        return reqData();
    }


    public byte[] reqData(String remoteFilename, int start, int len) throws FileNotFoundException {
        BufferRequest bufferRequest = new BufferRequest();
        bufferRequest.name = UDPclient.userlocal.username;
        bufferRequest.bufname = remoteFilename;
        bufferRequest.id = id;
        bufferRequest.page = start;
        bufferRequest.l = len;
        bufferRequest.pl = pagelen;
        this.bdr = bufferRequest;
        DataMap.put(this, this);
        send = ("Ab" + JSON.toJSONString(bdr)).getBytes();
        senders.sendSym(send);

        return reqData();//new ByteArrayInputStream(rev);
    }

    public byte[] reqData() throws FileNotFoundException {
        long t1 = System.currentTimeMillis();
        Long time;
        while (true){
            time = callPoll.runTime();
            if(time==null)return null;
            if(time>0){
                if(time>10){
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }else {
                    try {
                        Thread.sleep(time);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }
            }else {
                break;
            }
        }

//        send = Utils.byteMerger("bA".getBytes(), Utils.shortToByteArray((short) id));
        long t2 = System.currentTimeMillis();
        time = t2 - t1;

        if (bdr.page <= 0) {
            return new byte[0];
        } else {
            byte[] bbyte = null;
            reCallCon = new ReCallCon((int) bdr.l, pagelen, 128);
            if (bdr.l > 1024 * 1024 * 10) {
                File bfile = new File(XmlCreate.userCache + UUID.randomUUID());
                OutputStream outputStream = new FileOutputStream(bfile);
                reCallCon.writer = new DataWriter() {
                    volatile int len = 0;

                    @Override
                    public int getLen() {
                        return len;
                    }

                    @Override
                    public boolean getState() {
                        if (len == bdr.l) {
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
//                        if(len+pos>bdrbdr.l){
//                            len= (int) (bdr.l-pos);
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
                bbyte = new byte[(int) bdr.l];
                byte[] finalBbyte = bbyte;
                reCallCon.writer = new DataWriter() {
                    int len = 0;

                    @Override
                    public int getLen() {
                        return len;
                    }

                    @Override
                    public boolean getState() {
//                        System.out.println("len "+len);
                        if (len == bdr.l) {
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
            reCallCon.readQue(blockingQueue);
            userContext.setQueue((short) id, bufer);

            callPoll= req(time,bufer,this);
            try {
                while (true){
                    time = callPoll.runTime();
                    if(time==null)return null;
                    if(time>0){
                        if(time>10){
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException interruptedException) {
                                interruptedException.printStackTrace();
                            }
                        }else {
                            try {
                                Thread.sleep(time);
                            } catch (InterruptedException interruptedException) {
                                interruptedException.printStackTrace();
                            }
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
                senders.send("OK".getBytes());
            }
        }

        if(ro instanceof File){
            try {
                ro = Files.readAllBytes(((File)ro).toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return (byte[]) ro;//new ByteArrayInputStream(rev);
    }

    public Object ro;
    public CallPoll req(long timeLong,DataReCallBuffer bufer,AsyBuffer se) throws FileNotFoundException {
        System.out.println("page:  " + bdr.page);
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
            int tt=126;
            @Override
            public Long runTime() {
//                if(reCallCon.gets()){
//                    b = Utils.byteMerger(Utils.intToByteArray(reCallCon.posR - 1), "DEL".getBytes());
//                    senders.send(b);
//                    t=0;
//                }
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
                    System.out.println("readed "+reCallCon.posR);
                    if(reCallCon.posR==0){
                        System.out.println("readeb "+reCallCon.bytel[0]);
                    }
                    System.out.println("readew "+reCallCon.posW);
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
                }
                if (blockingQueue.size() > 0) {
                    bytes = blockingQueue.poll();
                    System.out.println("poll  "+new String(bytes));
                    if(new String(bytes).equals("WA")){
                        t=0;
                        time=timeLong*2;
                        return time;
                    }
                    //todo
                }

//                    waitTime = System.currentTimeMillis() - time0;
                if(waitTime<time){
                    try {
                        waitTime = time-waitTime;
                        return waitTime;
                    } catch (Exception interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }
                if(waitTime<userContext.getTime()){
                    time =waitTime(t,waitTime,timeLong);
//                    time0=System.currentTimeMillis();
//                            senders.send(b);
                    senders.send(send);
//                        if(reCallCon.gets()){
//                            System.out.println("readed");
//                            b = Utils.byteMerger(Utils.intToByteArray(reCallCon.posR - 1), "DEL".getBytes());
//                            senders.send(b);
//                            t=0;
//                        }
                    return time;
                }
//                        return time-waitTime;




                b = Utils.byteMerger(Utils.intToByteArray(reCallCon.posR - 1), "DEL".getBytes());
                senders.send(b);
//                            senders.send(Utils.intToByteArray(k));
                pos=reCallCon.posR;
//                    if (waitTime>userContext.delayTime && tt>0){
                    for (k = reCallCon.posR; k < reCallCon.posW && k < reCallCon.allPage; k++) {
                        if (reCallCon.bytel[k % reCallCon.bytel.length] == 0) {
                            senders.send(Utils.intToByteArray(k));
                            System.out.println("recCheak  "+k);
                            send = Utils.intToByteArray(k);
//                                t-=t;
                        }
                    }
                    if(reCallCon.posR + 126 <reCallCon.allPage){
                        senders.send(Utils.intToByteArray(reCallCon.posR + 125));
//                            t-=t;
                    }else {
                        senders.send(Utils.intToByteArray(reCallCon.allPage-1));
//                            t-=t;
                    }
//                    }
//                    for (k = reCallCon.posR; k < reCallCon.posR + 126 && k < reCallCon.allPage; k++) {


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
                        if (t > 13 && time>1000) {
                            iso=true;
                            return null;
                        }
                        if (t % 3 == 0) {
                            ByteBufer ub = userC.getQueue(id);
                            if (ub != blockingQueue && ub != bufer) {
                                iso=true;
                                return null;
                            }
                            senders.sendSym(Utils.byteMerger(("bA").getBytes(), Utils.intToByteArray(id)));
                        }
                    }
                }
                time=waitTime(t,time,timeLong);
                time0=System.currentTimeMillis();
                reqTime = time0;
                tt=126;
//                if(reCallCon.gets()){
//                    b = Utils.byteMerger(Utils.intToByteArray(reCallCon.posR - 1), "DEL".getBytes());
//                    senders.send(b);
//                    t=0;
//                }
                return time;
            }

            @Override
            public boolean isOver() {
                return  iso;
            }
        };
        return callPoll;
    }


    public byte[] reqDataAsy(String remoteFilename) {
        return null;

    }

    public volatile byte[] send = null;

    public AsyBuffer getBuf(BufferRequest bdr) {
        this.bdr = bdr;
        AsyBuffer autoData = DataMap.get(this);
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
        byte[] data = praseBuffer(bdr);
//        send=Utils.byteMerger(("bA").getBytes(),Utils.intToByteArray(id));
        pagelen = bdr.pl;
        if (data == null) {
            clear();
            send=Utils.byteMerger(("bA").getBytes(),Utils.intToByteArray(id));
//            send = Utils.byteMerger(("WA").getBytes(), Utils.intToByteArray(id));
//            senders.sendSym(send);
            senders.send(send);
            return;
        }
        bdr.page = Math.toIntExact(data.length / pagelen);
        bdr.l = data.length;
        if ((data.length % pagelen) != 0) {
            bdr.page += 1;
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


    private ReCallCon reCallCon;

    public boolean state = true;

    DataReader reader;

    public void sends(SendRe sendRe,byte[] cheak) {
        int p ;
        byte[] re = null;
        String s = "";
        int j = 0;
        long waitTime = userContext.getTime();
        while (state) {
            try {
                sendRe.read();
                re = sendRe.queue.poll(waitTime, TimeUnit.MILLISECONDS);
                if (re.length != 4) {
                    if(re.length !=7){
                        s = new String(re, 0, 2);
                        if ("OK".equals(s) || "bA".equals(s)) {
                            state = false;
                            break;
                        }
                    }else {
                        p = Utils.byteArrayToInt(re);
                        sendRe.posR=p;
                        if(sendRe.posW-1>sendRe.posR){
                            int index = (sendRe.posW-1) % sendRe.bytel.length;
                            send = sendRe.bytess[index];
                            senders.send0(send, 0, sendRe.bytel[index] + 14);
                            System.out.println("send+ " + p);
                        }
                        if(sendRe.posR==sendRe.posW){
                            break;
                        }
                    }
//                    if ("".equals(s) && re.length == 2) {
//                        return;
//                    }
                    j = 0;
                    continue;
                }
                j = 0;
                waitTime = userContext.getTime();
            } catch (Exception e) {
                waitTime = waitTime*2;
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
                            if (j > 13 && waitTime>1000) {
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
                    if(sendRe.posW-1==sendRe.posR)continue;
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
//                    int index = sendRe.posR % sendRe.bytel.length;
//                    send = sendRe.bytess[index];
//                    senders.send0(send, 0, sendRe.bytel[index] + 14);
//                    System.out.println("send+ " + p);
                    if(sendRe.posW-1>sendRe.posR){
                        int index = (sendRe.posW-1) % sendRe.bytel.length;
                        send = sendRe.bytess[index];
                        senders.send0(send, 0, sendRe.bytel[index] + 14);
                        System.out.println("send+ " + p);
                    }
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

    public static ConcurrentHashMap waitMap = new ConcurrentHashMap();

    //":cloudefile&:"
//     path.insert(0,":xmlfile&:");
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

                    break;
                }
                case "data": {
                    bytes = BufferDataCon.getData(bufName);
                    if (bytes == null) {
//                        waitMap.put(req,this);
                    }
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
                    File file=new File(strings[1]);
                    bdr.al=file.length();
                    try {
                        FileInputStream inputStream=new FileInputStream(file);
                        if((bdr.al - req.page)<req.l) {
                            req.l = bdr.al - req.page;
                        }
                        bytes=new byte[(int) req.l];
                        inputStream.skip(req.page);
                        inputStream.read(bytes);
                        inputStream.close();
                    } catch (Exception e) {
                        bytes=new byte[0];
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
        return (int) Math.floor((float) reCallCon.writer.getLen() * 100 / bdr.l);
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
        return id;
//        new Exception("追踪抛出").printStackTrace();
    }

    public static long waitTime(int t,long time,long timeo) {
        if (t == 0) {
            time = time / 2 + 2;
        } else {
            if (time < timeo) {
                time = (time+ 2) * 2 ;
            }
            long ti=safeMultiply(time,toTime(t));
            if(ti>time){
                if (ti >= timeo*10) {
                    ti = timeo*10;
                    return ti;
                }
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
