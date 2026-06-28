package superlink.udpbind.client.recives.datalen;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.annotation.JSONField;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.ByteBufer;
import superlink.udpbind.client.recives.Senders;

import superlink.udpbind.client.recives.datalen.autoSend.dataBI;
import superlink.udpbind.client.recives.datalen.autoSend.dataByte;
import superlink.udpbind.client.recives.datalen.autoSend.dataInteger;
import superlink.udpbind.client.recives.datalen.autoSend.synDataByte;
import superlink.udpbind.usedata.DataRequest;
import superlink.util.Utils;
import superlink.util.asynhandle.AsynHandler;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class AutoData extends DataLength {
    @JSONField(serialize = false)
    public Senders senders;
    @JSONField(serialize = false)
    public ByteBufer blockingQueue;
    @JSONField(serialize = false)
    public byte[] rev;
    @JSONField(serialize = false)
    public static ConcurrentHashMap<AutoData,AutoData> DataMap =new ConcurrentHashMap<>();

    public AutoData(String username){
        System.out.println("revor");
        this.userContext = UDPclient.mainDataQueue.getUserContext(username);
        this.id = userContext.newQueue(256);
        blockingQueue = userContext.getDataQue(this.id);
        this.senders = new Senders().Init(this.id, username);
        userContext.getTask((short) id).task=this;
    }


    public AutoData(String username, short id) {
        System.out.println("sendor");
        this.userContext = UDPclient.mainDataQueue.getUserContext(username);
        this.id = id;
        blockingQueue = userContext.getDataQue(this.id);
        this.senders = new Senders().Init(this.id, username);
        userContext.getTask((short) id).task=this;
    }

    public byte[] dt;
    public Object reqFile(String remoteFilename) {
        DataRequest sdr = new DataRequest();
        sdr.requestname = UDPclient.userlocal.username;
        sdr.filename = remoteFilename;
        sdr.id = id;
        sdr.pl = pagelen;
        this.sdr=sdr;
        DataMap.put(this,this);
        dt= ("AD" + JSON.toJSONString(sdr)).getBytes();
        senders.sendSym(dt);
        Long time = System.currentTimeMillis();
        byte[] star = null;
        int i = 0;
        DataRequest dataRequest;
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
                if (re.equals("DA")){
                    return null;
                }else {
                    dataRequest=JSON.parseObject(re.substring(2), DataRequest.class);
                    break;
                }
            } catch (Exception e) {
                System.out.println("Auto Timeout");
            }
            senders.sendSym(dt);
            i++;
        }
        send=Utils.byteMerger("DA".getBytes(),Utils.shortToByteArray( id));
        Long t2 = System.currentTimeMillis();
        time = t2 - time;

        if ("DB".equals(re.substring(0,2))){
            rev=new dataByte(this,pagelen).reqFile(dataRequest,time);
            ByteBuffer buffer=ByteBuffer.wrap(rev);
            return buffer;//new BufferedInputStream(new FileInputStream(rev));
        }else if ("DI".equals(re.substring(0,2))){
            rev=new dataInteger(this,pagelen).reqFile(dataRequest,time);
            ByteBuffer buffer=ByteBuffer.wrap(rev);
            return buffer;//new ByteArrayInputStream(rev);
        }else if ("BI".equals(re.substring(0,2))){
            File file=new dataBI(this,pagelen).reqFile(dataRequest,time);
            return file;//new ByteArrayInputStream(FileInputStream);
        }
        return null;
    }

    public long timeRecord=System.currentTimeMillis();
    public long timeWait=30;

    public boolean reqFileSyn(String remoteFilename, AsynHandler... handlers) {
        DataRequest sdr = new DataRequest();
        sdr.requestname = UDPclient.userlocal.username;
        sdr.filename = remoteFilename;
        sdr.id = id;
        sdr.pl = pagelen;
        this.sdr=sdr;
        DataMap.put(this,this);
        dt= ("AD" + JSON.toJSONString(sdr)).getBytes();
        senders.sendSym(dt);
        long time = System.currentTimeMillis();
        byte[] star;
        int i = 0;
        DataRequest dataRequest;
        String re;
        send=dt;
//        senders.sendSym(dt);

        if (i > 5) {
            return false;
        }

        star = blockingQueue.poll();
        i++;
        if (star==null){
            senders.sendSym(dt);
            long t=System.currentTimeMillis();
            timeWait = t-timeRecord;
            timeRecord=t;
            return true;
        }
        re = new String(star);
        if (re.equals("DA")){
            long t=System.currentTimeMillis();
            timeWait = t-timeRecord;
            timeRecord=t;
            return true;
        }else {
            dataRequest=JSON.parseObject(re.substring(2), DataRequest.class);
            send=Utils.byteMerger("DA".getBytes(),Utils.shortToByteArray(id));
            DataMap.remove(this);
            Long t2 = System.currentTimeMillis();
            time = t2 - time;
            Object obj = null;
            if ("DB".equals(re.substring(0,2))){
                new synDataByte(this,pagelen).addWork(handlers).synReqFile(dataRequest,time);
                ByteBuffer buffer=ByteBuffer.wrap(rev);
                obj=buffer;
            }else if ("DI".equals(re.substring(0,2))){
                rev=new dataInteger(this,pagelen).reqFile(dataRequest,time);
                ByteBuffer buffer=ByteBuffer.wrap(rev);
                obj=buffer;
            }else if ("BI".equals(re.substring(0,2))){
                File file=new dataBI(this,pagelen).reqFile(dataRequest,time);
                obj=file;
            }
            return false;
        }

    }

    public byte[] send=null;
    public AutoData sends(DataRequest sdr){
        this.sdr=sdr;
        AutoData autoData=DataMap.get(this);
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
            senders.send("DA".getBytes());
        }else {
            senders.send(send);
        }

    }


    long I=Integer.MAX_VALUE/2;//1446

    @Override
    public void run() {
        sdr.filename= Utils.pathPrase(sdr.filename).path;
        File file=new File(sdr.filename);
        pagelen=sdr.pl;
        long B=(pagelen-1)*256;
        if (file.exists()){
            try {
                if (file.length()<B){
                    dataByte dataByte= new dataByte(this,pagelen);
                    dataByte.sends(sdr);
                }else if (file.length()<I){
                    dataInteger dataInteger=new dataInteger(this,pagelen);
                    dataInteger.sends(sdr);
                    //new dataInteger(this).sends(sdr);
                }else{
                    dataBI dataBI=new dataBI(this,pagelen);
                    dataBI.sends(sdr);
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                clear();
            }
        }
    }

    @Override
    public int hashCode(){
        return userContext.getUserId()&id;
    }
    @Override
    public boolean equals(Object o){
        return this.hashCode()==o.hashCode()?true:false;
    }
    @Override
    public String toString(){
        if (sdr!=null){
            return userContext.userName+" + "+id+" : "+JSON.toJSONString(sdr);
        }
        return JSON.toJSONString(userContext.userName+" + "+id);
    }

    @Override
    public int getprogress(){
        if (data instanceof byte[][]){
            byte[][] bs= (byte[][]) data;
            int i=0;
            for (byte[] b:bs){
                if (b!=null){
                    i++;
                }
            }
            return (int)Math.floor(i*100/sdr.page);
        }
        if (data instanceof File){
            File file= (File) data;
            return (int)Math.floor(file.length()*100/sdr.page*1445);

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

