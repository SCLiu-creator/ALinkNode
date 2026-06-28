package superlink.udpbind.client.recives.datalen.dataReq;

import com.alibaba.fastjson2.JSON;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.ByteBufer;
import superlink.udpbind.client.recives.Senders;
import superlink.udpbind.client.recives.datalen.DataSmall;
import superlink.udpbind.usedata.DataRequest;
import superlink.util.Utils;

import java.io.*;
import java.util.concurrent.TimeUnit;

public class dataBytes {
    public UserContext userContext;
    public int id;
    Senders senders;
    ByteBufer blockingQueue;
    DataRequest sdr;

    public dataBytes(DataSmall dataAuto){
        System.out.println("revor");
        this.userContext=dataAuto.userContext;
        this.id=dataAuto.id;
        blockingQueue=dataAuto.blockingQueue;
        this.senders=dataAuto.senders;
    }

    public byte[] reqFile(DataRequest dataRequest,long time){
        int i=0;
        int l;
        byte[][] bytess=null;
        byte bpage= (byte) (dataRequest.page-128);
        if (dataRequest.page >= 0){
            int j=0;
            bytess=new byte[dataRequest.page][];
            byte pos=Byte.MIN_VALUE;
            byte[] bp=new byte[1];
            long l1= (long) Math.log(time*dataRequest.page)*50;
            byte[] bytes=null;
            while (pos<bpage){
                try {
                    bytes=blockingQueue.poll(l1,TimeUnit.MILLISECONDS);
                    bytess[bytes[0]+128]=Utils.subByte(bytes,1,bytes.length-1);
                } catch (Exception e) { }
                pos++;
            }
            while (true){
                i=0;
                pos=Byte.MIN_VALUE;
                System.out.println("revcheak");
                for (byte[] b:bytess){
                    if (b!=null){
                        i++;
                    }else {
                        bp[0]= pos;
                        senders.send(bp);
                    }
                    pos++;
                }
                if (i==dataRequest.page){
                    break;
                }
                try {
                    Thread.sleep(l1);
                } catch (InterruptedException e) {}
                while (true){
                    try {
                        bytes=blockingQueue.poll(l1,TimeUnit.MILLISECONDS);
                        bytess[bytes[0]+128]= Utils.subByte(bytes,1,bytes.length-1);
                        j=0;
                    } catch (Exception e) {
                        break;
                    }
                }
                j++;
                if (j>3){
                    return null;
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
        File file=new File(sdr.filename);
        if (file.exists()){
            sdr.page= Math.toIntExact(file.length() / 1449);
            if ((file.length() %1449)!=0){sdr.page+=1;}
        }
        String send="DB"+ JSON.toJSONString(sdr);
        senders.send(send.getBytes());
        System.out.println("sdr.page: "+sdr.page);
        byte[] bytes=new byte[1450];
        byte[][] bytess=new byte[sdr.page][];
        int p=0;
        int len=0;
        RandomAccessFile randomFile = null;
        try {
            randomFile = new RandomAccessFile(file,"r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        while (p<sdr.page){
            try {
                len=randomFile.read(bytes,1,1449);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (len!=1449){
                bytes=Utils.subByte(bytes,0,len+1);
            }
            bytes[0]= (byte) (p-128);
            senders.send(bytes);
            bytess[p]=bytes.clone();
            p++;
        }
        byte[] re=null;
        String s = null;
        while (true){
            try {
                re=blockingQueue.poll(3, TimeUnit.SECONDS);
                s=new String(re);
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
            if ("OK".equals(s)){
                break;
            }

            System.out.println("sending: "+re[0]);
            senders.send(bytess[re[0]+128]);

        }
        System.out.println("over");
    }

    @Override
    public int hashCode(){
        return sdr.hashCode();
    }
    @Override
    public boolean equals(Object o){
        return this.hashCode()==o.hashCode()?true:false;
    }

}
