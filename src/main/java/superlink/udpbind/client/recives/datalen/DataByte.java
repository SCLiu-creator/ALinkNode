package superlink.udpbind.client.recives.datalen;

import com.alibaba.fastjson2.JSON;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.ByteBufer;
import superlink.udpbind.client.recives.Senders;
import superlink.udpbind.usedata.DataRequest;
import superlink.util.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
//废弃
public class DataByte extends DataLength {
    Senders senders;
    ByteBufer blockingQueue;

    public DataByte(String username){
        System.out.println("revor");
        this.userContext=UDPclient.mainDataQueue.getUserContext(username);
        this.id=userContext.newQueue();
        blockingQueue=userContext.getDataQue(this.id);
        this.senders=new Senders();
        senders.Init(this.id,username);
        userContext.getTask((short) id).task=this;
    }
    public DataByte(String username,int id){
        System.out.println("sendor");
        this.userContext=UDPclient.mainDataQueue.getUserContext(username);
        this.id= (short) id;
        blockingQueue=userContext.getDataQue(this.id);
        this.senders=new Senders();
        senders.Init(this.id,username);
        userContext.getTask((short) id).task=this;
    }
    public byte[] reqFile(String remoteFilename){
        DataRequest sdr=new DataRequest();
        sdr.requestname= UDPclient.userlocal.username;
        sdr.filename=remoteFilename;
        sdr.id=id;

        byte[] dt=("DB"+ JSON.toJSONString(sdr)).getBytes();
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
        if (dataRequest.page >= 0){
            int page=dataRequest.page-128;
            bytess=new byte[dataRequest.page][];
            byte pos=Byte.MIN_VALUE;
            while (pos<page){
                byte[] recive=null;
                try {
                    recive=blockingQueue.poll(time,TimeUnit.MILLISECONDS);
                    pos++;
                } catch (InterruptedException e) {
                    i++;
                    e.printStackTrace();
                }
                System.out.println("reving");
                int r=recive[0]+128;
                bytess[r]= Utils.subByte(recive,1,recive.length-1);
            }

            while (true){
                i=0;
                pos=0;
                System.out.println("revcheak");
                for (byte[] b:bytess){
                    if (b!=null){
                        i++;
                    }else {
                        senders.send(new byte[]{(byte) (pos-128),0});
                    }
                    pos++;
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
                byte[] bytes=null;
                while (true){
                    bytes=blockingQueue.poll();
                    if (bytes==null){
                        break;
                    }
                    bytess[bytes[0]+128]= Utils.subByte(bytes,1,bytes.length-1);
                }
            }
            senders.send("OK".getBytes());
            senders.send("OK".getBytes());
        }
        l=bytess[dataRequest.page-1].length;
        l=((dataRequest.page-1)*1449)+l;
        byte[] rev=new byte[l];
        i=0;
        for (byte[] bytes:bytess){
            System.arraycopy(bytes,0,rev,i*1449,bytes.length);
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
        File file=new File(sdr.filename);
        if (file.exists()){
            sdr.page= Math.toIntExact(file.length() / 1449);
            if ((file.length() %1449)!=0){sdr.page+=1;}
            }
        senders.send(JSON.toJSONBytes(sdr));
        byte[] bytes=new byte[1449];
        int p=0;
        int len=0;
        FileInputStream inputStream = null;
        try {
            inputStream=new FileInputStream(file);
            while ((len=inputStream.read(bytes))!= -1){
                if (len!=1449){
                    bytes=Utils.subByte(bytes,0,len);
                }
                System.out.println("send");
                senders.send(Utils.byteMerger(new byte[]{(byte) (p-128)},bytes));
                p++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] re=null;
        String s = "";
        bytes=new byte[1449];
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
                p=(re[0]+128)*1449;
                try {
                    inputStream.skip(p);
                    len=inputStream.read(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (len!=1449){
                    bytes=Utils.subByte(bytes,0,len);
                }
                senders.send(Utils.byteMerger(new byte[]{(byte) (p-128)},bytes));
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
    public int clear(){
        System.out.println("userContext.deltask(id): "+id);
        userContext.deltask(id);
        return id;
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

}
