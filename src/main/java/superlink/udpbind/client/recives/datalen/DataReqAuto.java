package superlink.udpbind.client.recives.datalen;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.annotation.JSONField;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.recives.ByteBufer;
import superlink.udpbind.client.recives.DataLenMange;
import superlink.udpbind.client.recives.Senders;
import superlink.udpbind.client.recives.datalen.dataReq.dataByte;
import superlink.udpbind.client.recives.datalen.dataReq.dataBI;
import superlink.udpbind.client.recives.datalen.dataReq.dataInteger;
import superlink.udpbind.usedata.DataRequest;
import superlink.util.SHAutils;
import superlink.util.Utils;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


//old
public class DataReqAuto extends DataLength {
    @JSONField(serialize = false)
    public Senders senders;
    @JSONField(serialize = false)
    public ByteBufer blockingQueue;
    @JSONField(serialize = false)
    public byte[] rev;
    @JSONField(serialize = false)
    public static WeakHashMap weakHashMap=new WeakHashMap();
    @JSONField(serialize = false)
    public static ConcurrentHashMap<DataReqAuto,String> hashMap=new ConcurrentHashMap<>();


    public DataReqAuto(String username) {
        System.out.println("revor");
        this.userContext = UDPclient.mainDataQueue.getUserContext(username);
        this.id = userContext.newQueue();
        blockingQueue = userContext.getDataQue(this.id);
        this.senders = new Senders().Init(this.id, username);
        setPagelen(DataLenMange.getLen( username));
        userContext.getTask((short) id).task=this;
    }

    public DataReqAuto(String username, int id) {
        System.out.println("sendor");
        this.userContext = UDPclient.mainDataQueue.getUserContext(username);
        this.id = (short) id;
        blockingQueue = userContext.getDataQue(this.id);
        this.senders = new Senders().Init(this.id, username);
        userContext.getTask((short) id).task=this;
    }

    public int cj=0;
    public Object reqFile(String remoteFilename) {
        DataRequest dataRequest = new superlink.udpbind.usedata.DataRequest();
        dataRequest.requestname = UDPclient.userlocal.username;
        dataRequest.filename = remoteFilename;
        dataRequest.id = id;
        dataRequest.pl = pagelen;
        this.sdr=dataRequest;
        byte[] dt = ("DR" + JSON.toJSONString(sdr)).getBytes();
        Long time = System.currentTimeMillis();
        senders.sendSym(dt);
        byte[] star = null;
        int i = 0;
        String re;
        senders.sendSym(dt);
//        senders.sendSym(dt);
        while (true) {
            if (i > 3) {
                return null;
            }
            try {
                star = blockingQueue.poll(3, TimeUnit.SECONDS);
                re = new String(star);
                break;
            } catch (Exception e) {
                System.out.println("Auto Timeout");
            }
            senders.sendSym(dt);
            i++;
        }
        Long t2 = System.currentTimeMillis();
        time = t2 - time;
        sdr=JSON.parseObject(re.substring(2), DataRequest.class);

        if(hashMap.contains(this)){
            return null;
        }else {
            hashMap.put(this,JSON.toJSONString(sdr));
        }
        if ("DB".equals(re.substring(0,2))){
            rev=new dataByte(this).reqFile(time);
            ByteBuffer buffer=ByteBuffer.wrap(rev);
            return buffer;//new BufferedInputStream(new FileInputStream(rev));
        }else if ("DI".equals(re.substring(0,2))){
            rev=new dataInteger(this).reqFile(time);
            ByteBuffer buffer=ByteBuffer.wrap(rev);
           return buffer;//new ByteArrayInputStream(rev);
        }else if ("BI".equals(re.substring(0,2))){
            File file=new dataBI(this).reqFile(time);
            return file;//new ByteArrayInputStream(FileInputStream);
        }
        return null;
    }

    public static boolean writdata(String absoult ,Object data){
        File file=new File(absoult);
        if (!file.exists()){//file..mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                return false;
            }
        }
        if (data instanceof ByteBuffer){
            try (FileOutputStream fileOutputStream=new FileOutputStream(file)){
                fileOutputStream.write(((ByteBuffer) data).array());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        if (data instanceof File){
            FileOutputStream fileOutputStream;
            FileInputStream fileFrom;
            try {
                fileOutputStream=new FileOutputStream(file);
                fileFrom=new FileInputStream((File)data);
                fileFrom.getChannel().transferTo(0,fileFrom.getChannel().size(),fileOutputStream.getChannel());
                fileOutputStream.close();
                fileFrom.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                ((File) data).delete();
            }
        }
        return false;
    }


    public static String getHash(Object data){

        if (data instanceof ByteBuffer){
            ByteBuffer byteBuffer=(ByteBuffer)data;
            return SHAutils.getShaFromByte(byteBuffer.array(),SHAutils.MD_5,false);
        }
        if (data instanceof File){
            try {
                File file=(File) data;
                return SHAutils.getShaFromFile(file.getAbsolutePath(),SHAutils.MD_5,false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return SHAutils.getShaFromFile(data.toString(),SHAutils.MD_5,false);
    }
//    ByteBuf byteBuf= Unpooled.buffer();
    //            try {
//                UDPclient.socket.getChannel().write(new ByteBuffer[0]);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
    public void sends(superlink.udpbind.usedata.DataRequest sdr){
        this.sdr=sdr;
//        weakHashMap.put(this,JSON.toJSONString(sdr));
        setThreadPool.reExecute(this);
//        run();
    }

    long B=1449*256;
    long I=Integer.MAX_VALUE/2;//1446

    @Override
    public void run() {
        if (hashMap.contains(this)){
            return;
        }else {
            hashMap.put(this,JSON.toJSONString(sdr));
        }
        sdr.filename= Utils.pathPrase(sdr.filename).path;
        pagelen=sdr.pl;
        File file=new File(sdr.filename);
        if (file.exists()){
            if (file.length()<B){
                dataByte dataByte= new dataByte(this);
                dataByte.sends();
            }else if (file.length()<I){
                dataInteger dataInteger=new dataInteger(this);
                dataInteger.sends();
            }else{
                dataBI dataBI=new dataBI(this);
                dataBI.sends();
            }
        }else {
            dataByte dataByte= new dataByte(this);
            dataByte.sends();
        }

        clear();
    }

    @Override
    public int hashCode(){
        if (sdr==null){
            return userContext.getUserId()&id;
        }
        return sdr.hashCode();
    }
    @Override
    public boolean equals(Object o){
        return this.hashCode()==o.hashCode()?true:false;
    }

    public int getprogress(){
        if(sdr==null)return -1;
        if (data instanceof byte[][]){
            byte[][] bs= (byte[][]) data;
            if(sdr.page==0)return -1;
            return (int)Math.floor(bs.length*100/sdr.page*1442);
        }
        if (data instanceof File){
            File file= (File) data;
            if(sdr.page==0)return -1;
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
        return id;
//        new Exception("追踪抛出").printStackTrace();
    }

}
