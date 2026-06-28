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
import superlink.udpbind.client.recives.Senders;

import superlink.udpbind.client.recives.datalen.autobuffer.dataByte;
import superlink.udpbind.client.recives.datalen.autobuffer.dataInteger;
import superlink.udpbind.client.recives.datalen.dataCache.BufferDataCon;
import superlink.udpbind.cloude.show.ShowBin;
import superlink.udpbind.cloude.show.UserShowContainer;
import superlink.udpbind.usedata.BufferRequest;
import superlink.util.Utils;

import java.io.*;
import java.net.URLDecoder;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class AutoBuffer extends DataLength {
    @JSONField(serialize = false)
    public Senders senders;
    @JSONField(serialize = false)
    public ByteBufer blockingQueue;
    @JSONField(serialize = false)
    public byte[] rev;
    @JSONField(serialize = false)
    public static ConcurrentHashMap<AutoBuffer,AutoBuffer> DataMap =new ConcurrentHashMap<>();


    public AutoBuffer(String username){
        System.out.println("revor");
        this.userContext = UDPclient.mainDataQueue.getUserContext(username);
        this.id = userContext.newQueue(256);
        blockingQueue = userContext.getDataQue(this.id);
        this.senders = new Senders().Init(this.id, username);
        setPagelen(DataLenMange.getLen( username));
        userContext.getTask((short) id).task=this;
    }


    public AutoBuffer(String username, int id) {
        System.out.println("sendor");
        this.userContext = UDPclient.mainDataQueue.getUserContext(username);
        this.id = (short) id;
        blockingQueue = userContext.getDataQue(this.id);
        this.senders = new Senders().Init(this.id, username);
        userContext.getTask((short) id).task=this;
    }


    public static AutoBuffer setRemoteBuf(String username,byte[] buf,String bufName){
        UserContext userContext = UDPclient.getUser(username);
        BufferRequest sdr = new BufferRequest();
        sdr.name = username;
        sdr.bufname = bufName;
        sdr.id = userContext.newQueue();
        AutoBuffer autoBuffer = new AutoBuffer(username,sdr.id);
        autoBuffer.rev = buf;
        userContext.stableSend(("ab"+JSON.toJSONString(sdr)).getBytes());
        DataMap.put(autoBuffer,autoBuffer);
        return autoBuffer;
    }

//使用AB
    public byte[] reqData(String remoteFilename) {
        BufferRequest sdr = new BufferRequest();
        sdr.name = UDPclient.userlocal.username;
        sdr.bufname = remoteFilename;
        sdr.id = id;
        sdr.pl=pagelen;
        this.bdr =sdr;
        DataMap.put(this,this);
        byte[] dt = ("AB" + JSON.toJSONString(sdr)).getBytes();
        senders.sendSym(dt);
        Long time = System.currentTimeMillis();
        byte[] star = null;
        int i = 0;
        String re;
        send=dt;
//        senders.sendSym(dt);
        while (true) {
            if (i > 5) {
                return null;
            }
            try {
                star = blockingQueue.poll(5, TimeUnit.SECONDS);
                re = new String(star);
                if (re.equals("BA")){
                    return null;
                }else {
                    if (re.equals("WA")){
                        i=0;
                        continue;
                    }
                    bdr=JSON.parseObject(re.substring(2), BufferRequest.class);
                    break;
                }
            } catch (Exception e) {
                System.out.println("Auto Timeout");
            }
            senders.sendSym(dt);
            i++;
        }
        send= Utils.byteMerger("BA".getBytes(),Utils.shortToByteArray((short) id));
        Long t2 = System.currentTimeMillis();
        time = t2 - time;

        if ("DB".equals(re.substring(0,2))){
            rev=new dataByte(this,pagelen).reqFile(bdr,time);
            return rev;//new BufferedInputStream(new FileInputStream(rev));
        }else if ("DI".equals(re.substring(0,2))){
            rev=new dataInteger(this,pagelen).reqFile(bdr,time);
            return rev;//new ByteArrayInputStream(rev);
        }
        clear();
        return null;
    }
    public byte[] reqData(String remoteFilename, int start, int len) {
        BufferRequest sdr = new BufferRequest();
        sdr.name = UDPclient.userlocal.username;
        sdr.bufname = remoteFilename;
        sdr.id = id;
        sdr.page=start;
        sdr.l=len;
        sdr.pl=pagelen;
        this.bdr =sdr;
        DataMap.put(this,this);
        byte[] dt = ("AB" + JSON.toJSONString(sdr)).getBytes();
        senders.sendSym(dt);
        Long time = System.currentTimeMillis();
        byte[] star = null;
        int i = 0;
        BufferRequest dataRequest;
        String re;
        send=dt;
//        senders.sendSym(dt);
        while (true) {
            if (i > 5) {
                return null;
            }
            try {
                star = blockingQueue.poll(3, TimeUnit.SECONDS);
                re = new String(star);
                if (re.equals("BA")){
                    return null;
                }else {
                    if (re.equals("WA")){
                        i=0;
                        continue;
                    }
                    bdr=JSON.parseObject(re.substring(2), BufferRequest.class);
                    break;
                }
            } catch (Exception e) {
                System.out.println("Auto Timeout");
            }
            senders.sendSym(dt);
            i++;
        }
        send= Utils.byteMerger("BA".getBytes(),Utils.intToByteArray((id)));
        Long t2 = System.currentTimeMillis();
        time = t2 - time;

        if ("DB".equals(re.substring(0,2))){
            rev=new dataByte(this,pagelen).reqFile(bdr,time);
            return rev;//new BufferedInputStream(new FileInputStream(rev));
        }else if ("DI".equals(re.substring(0,2))){
            rev=new dataInteger(this,pagelen).reqFile(bdr,time);
            return rev;//new ByteArrayInputStream(rev);
        }
        clear();
        return null;
    }

    public volatile byte[] send=null;
    public AutoBuffer getBuf(BufferRequest sdr){
        this.bdr =sdr;
        AutoBuffer autoData=DataMap.get(this);
        if (autoData==null){
            DataMap.put(this,this);
            return this;
        }else {
            return autoData;
        }
    }
    public void execute(boolean b){
        if (b){
            setThreadPool.reExecute(this);
        }else {
            run();
        }
    }
    public void aSend(){
        if (send==null){
            senders.send("BA".getBytes());
        }else {
            senders.send(send);
        }
    }


    public static long  I=Integer.MAX_VALUE/2;//1446
    //使用BA
    @Override
    public void run() {
        send=("WA").getBytes();
//        senders.sendSym(send);
        senders.send(send);
        byte[] bytes = praseBuffer(bdr);
        send=Utils.byteMerger(("BA").getBytes(),Utils.shortToByteArray(id));
        pagelen= bdr.pl;
        if (bytes==null){
            send=Utils.byteMerger(("WA").getBytes(),Utils.shortToByteArray(id));
//            senders.sendSym(send);
            senders.send(send);
            return;
        }
        long B=(pagelen-1)*256;
        try {
            if (bytes.length<B){
                dataByte dataByte= new dataByte(this,pagelen);
                dataByte.sends(bdr,bytes);
            }else if (bytes.length<I){
                dataInteger dataInteger=new dataInteger(this,pagelen);
                dataInteger.sends(bdr,bytes);
                //new dataInteger(this).sends(sdr);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            clear();
        }

    }

    public void process(byte[] bytes) {
        long B=(pagelen-1)*256;
        try {
            if (bytes.length<B){
                dataByte dataByte= new dataByte(this,pagelen);
                dataByte.sends(bdr,bytes);
            }else if (bytes.length<I){
                dataInteger dataInteger=new dataInteger(this,pagelen);
                dataInteger.sends(bdr,bytes);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            clear();
        }
    }

    public static ConcurrentHashMap waitMap=new ConcurrentHashMap();

    //":cloudefile&:"
    public byte[] praseBuffer(BufferRequest req){
        String[] strings=null;
        String bufName=req.bufname;
        try {
            bufName= URLDecoder.decode(bufName,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] bytes = new byte[0];

        String s=bufName.substring(0,1);
        int i=bufName.indexOf("&:");
        if (":".equals(s) && i!=-1){
            s=bufName.substring(1,i);
            strings=bufName.substring(1).split("&:",2);
            switch (s) {
                case "wait":{
                    bytes= BufferDataCon.getData(bufName);
                    if (bytes==null){
//                        waitMap.put(req,this);
                    }
                    break;}
                case "data":{
                    break;}
                case "xmlfile":{
                    ShowBin showBin=UserShowContainer.getLocalShowBin();
                    Document document=DocumentFactory.getInstance().createDocument();
                    Element element=document.addElement("p");
                    List<String> lists=showBin.get();
                    String po=null;
                    for (String s1:lists){
                        if (strings[1].contains(s1)){
                            if(po==null){
                                po=s1;
                            }
                        }
                    }
                    if (po==null){
                        bytes=new byte[0];
                        break;
                    }
                    UserShowContainer.writeXml(element,new File(strings[1]),2);
                    ByteArrayOutputStream bao=new ByteArrayOutputStream();
                    try {
                        XMLWriter xmlWriter = new XMLWriter(bao);
                        xmlWriter.write(document);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    bytes=bao.toByteArray();
//                    String string = new String( bytes);
//                    System.out.println(string);
//                    document=XmlParser.byetToDocument(bytes);
//                    Element er=document.getRootElement();
                    break;}
                case "datafl":{
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
                case "chats":{
                    File file=new File(XmlCreate.userChat+"bin.xml");
                    try {
                        long length = file.length();
                        FileInputStream inputStream=new FileInputStream(file);
                        bytes=new byte[(int) length];
                        inputStream.read(bytes);
                        inputStream.close();
                    } catch (Exception e) {
                        bytes=new byte[0];
                        e.printStackTrace();
                    }
                    break;
                }case "chat":{
                    File file=new File(XmlCreate.userChat+"bin.xml");
                    try {
                        long length = file.length();
                        FileInputStream inputStream=new FileInputStream(file);
                        bytes=new byte[(int) length];
                        inputStream.read(bytes);
                        inputStream.close();
                    } catch (Exception e) {
                        bytes=new byte[0];
                        e.printStackTrace();
                    }
                    break;
                }default:{
                    if (rev!= null) return rev;
                }
            }
        }
        return bytes;
    }

    @Override
    public int hashCode(){
        return userContext.getUserId()&id;
    }
    @Override
    public boolean equals(Object o){
        if (o==null)return false;
        return this.hashCode()==o.hashCode()?true:false;
    }
    @Override
    public String toString(){
        if (bdr !=null){
            return userContext.userName+" + "+id+" : "+JSON.toJSONString(bdr);
        }
        return JSON.toJSONString(userContext.userName+" + "+id);
    }
    public int getprogress(){
        if (data instanceof byte[][]){
            byte[][] bs= (byte[][]) data;
            return (int)Math.floor(bs.length*100/ bdr.page*1442);
        }
        if (data instanceof File){
            File file= (File) data;
            return (int)Math.floor(file.length()*100/ bdr.page*1445);

        }
        return 0;
    }
    //    @Override
//    public void finalize(){
//        System.out.println("userContext.deltask(id): "+id);
//        userContext.deltask(id);
////        new Exception("追踪抛出").printStackTrace();
//    }
    public int clear(){
        System.out.println("userContext.deltask(id): "+id);
        userContext.deltask(id);
        DataMap.remove(this);
        return id;
//        new Exception("追踪抛出").printStackTrace();
    }
    
}
