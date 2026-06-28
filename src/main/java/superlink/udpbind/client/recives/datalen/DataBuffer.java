package superlink.udpbind.client.recives.datalen;

import com.alibaba.fastjson2.JSON;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.ByteBufer;
import superlink.udpbind.client.recives.Senders;
import superlink.udpbind.usedata.DataRequest;
import superlink.util.Utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
//废弃
public class DataBuffer extends DataLength {
    Senders senders;
    ByteBufer blockingQueue;

    public DataBuffer(String username){
        System.out.println("revor");
        this.userContext= UDPclient.mainDataQueue.getUserContext(username);
        this.id=userContext.newQueue();
        blockingQueue=userContext.getDataQue(this.id);
        this.senders=new Senders();
        senders.Init(this.id,username);
        userContext.getTask((short) id).task=this;
    }
    public DataBuffer(String username, int id){
        System.out.println("sendor");
        this.userContext=UDPclient.mainDataQueue.getUserContext(username);
        this.id= (short) id;
        blockingQueue=userContext.getDataQue(this.id);
        this.senders=new Senders();
        senders.Init(this.id,username);
        userContext.getTask((short) id).task=this;
    }

    public static Map<String,byte[]> byteHashBuffer =new HashMap();


    public byte[] reqFile(String byteName){
        DataRequest sdr=new DataRequest();
        sdr.requestname= UDPclient.userlocal.username;
        sdr.filename=byteName;
        sdr.id=id;

        byte[] dt=("Db"+ JSON.toJSONString(sdr)).getBytes();
        long time=System.currentTimeMillis();
        senders.sendSym(dt);
        byte[] star=null;
        int i=0;
        DataRequest dataRequest;
        while (true){
            if (i>=3){
                return null;
            }
            try {
                star=blockingQueue.poll(2, TimeUnit.SECONDS);
                dataRequest=JSON.parseObject(star,DataRequest.class);
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            senders.sendSym(dt);
            i++;
        }
        long t2=System.currentTimeMillis();
        time=t2-time;
        i=0;
        int l;
        byte[][] bytess=null;
        if (dataRequest.page > 0){
            int page=dataRequest.page;//int page=dataRequest.page-Integer.MAX_VALUE;
            bytess=new byte[page][];
            Integer pos=0;//Integer pos=Integer.MIN_VALUE;
            while (pos<page){
                byte[] recive=null;
                try {
                    recive=blockingQueue.poll(time,TimeUnit.MILLISECONDS);
                    pos++;
                } catch (InterruptedException e) {
                    i++;
                    e.printStackTrace();
                }
                System.out.println("reving:"+pos);
                int r= Utils.byteArrayToInt(Utils.subByte(recive,0,4));
                bytess[r]= Utils.subByte(recive,4,recive.length-1);
            }

            while (true){
                i=0;
                System.out.println("revcheak");
                for (byte[] b:bytess){
                    if (b!=null){
                        i++;
                    }else {
                        senders.send(Utils.intToByteArray(i));
                    }
                }
//                i++;
                if (i==dataRequest.page){
                    break;
                }
                try {
                    Thread.sleep(time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                byte[] bytes;
                while (true){
                    bytes=blockingQueue.poll();
                    if (bytes==null){
                        break;
                    }
                    bytess[Utils.byteArrayToInt(Utils.subByte(bytes,0,4))]= Utils.subByte(bytes,4,bytes.length-1);
                }
            }
            senders.send("OK".getBytes());
            senders.send("OK".getBytes());
        }else {
            return null;
        }
        l=bytess[dataRequest.page-1].length;

        l=((dataRequest.page-1)*1446)+l;
        byte[] rev=new byte[l];
        i=0;
        for (byte[] bytes:bytess){
            System.arraycopy(bytes,0,rev,i*1446,bytes.length);
            i++;
        }
        return rev;
    }

    public void sends(DataRequest sdr){
        this.sdr=sdr;
        setThreadPool.reExecute(this);

    }

    @Override
    public void run() {
//        File file=new File(sdr.filename);
        byte[] bytes0= byteHashBuffer.get(sdr.filename);
        if (bytes0!=null){
            if (bytes0.length<Integer.MAX_VALUE){
                sdr.page= Math.toIntExact(bytes0.length / 1446);
                if ((bytes0.length %1446)!=0){sdr.page+=1;}
            }
        }
        senders.send(JSON.toJSONBytes(sdr));
        byte[] bytes=new byte[1446];
        int p=0;
        int len=0;
        byte[][] bytess=new byte[sdr.page][];
        int ii=0;
        try {
            while (true){
                bytes=Utils.subByte(bytes0,ii*1446,1446);
                System.out.println("send");
                bytess[p]=bytes;
                senders.send(Utils.byteMerger(Utils.intToByteArray(p),bytes));
                if (len<1446){
                    break;
                }
                p++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] re=null;
        String s = "";
        while (true){
            try {
                System.out.println("sending");
                re=blockingQueue.poll(2,TimeUnit.SECONDS);
                s=new String(re);
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }

            if ("OK".equals(s)){
                break;
            }else{
                p=Utils.byteArrayToInt(Utils.subByte(re,0,4))*1446;
                bytes=bytess[p];
                senders.send(Utils.byteMerger(Utils.intToByteArray(p),bytes));
            }
        }
    }

    @Override
    public int hashCode(){
        return sdr.hashCode();
    }
    @Override
    public boolean equals(Object o){
        return this.hashCode()==o.hashCode()?true:false;
    }

    @Override
    public void finalize(){
        System.out.println("userContext.deltask(id): "+id);
        userContext.deltask(id);
    }
    public int getprogress(){
        if (data instanceof byte[][]){
            byte[][] bs= (byte[][]) data;
            return (int)Math.floor(bs.length*100/sdr.page*1442);
        }
        if (data instanceof File){
            File file= (File) data;
            return (int)Math.floor(file.length()*100/sdr.page*1445);

        }
        return 0;
    }

    public int clear() {
        System.out.println("userContext.deltask(id): " + id);
        userContext.deltask(id);
        return id;
//        new Exception("追踪抛出").printStackTrace();
    }

}

