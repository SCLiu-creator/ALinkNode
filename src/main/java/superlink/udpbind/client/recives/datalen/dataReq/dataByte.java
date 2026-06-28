package superlink.udpbind.client.recives.datalen.dataReq;

import com.alibaba.fastjson2.JSON;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.ByteBufer;
import superlink.udpbind.client.recives.Senders;
import superlink.udpbind.client.recives.datalen.DataReqAuto;
import superlink.udpbind.usedata.DataRequest;
import superlink.util.Utils;

import java.io.*;
import java.util.concurrent.TimeUnit;

public class dataByte {
    public UserContext userContext;
    public short id;
    Senders senders;
    ByteBufer blockingQueue;
    public int fp=1449;
    DataReqAuto dataReqAuto;

    public dataByte(DataReqAuto dataReqAuto){
        this.userContext= dataReqAuto.userContext;
        this.id= dataReqAuto.id;
        blockingQueue= dataReqAuto.blockingQueue;
        this.senders= dataReqAuto.senders;
        this.dataReqAuto = dataReqAuto;
    }

    public byte[] reqFile(long timeLong){
        fp = dataReqAuto.sdr.pl-1;
        int time= (int) timeLong;
        int i=0;
        int l;
        byte[][] bytess=null;
        long t0=time;
        if (dataReqAuto.sdr.page > 0){
            bytess=new byte[dataReqAuto.sdr.page][];
            dataReqAuto.data=bytess;
            int pos=0;
            int j=0;
            long l1 = 0;
            byte[] bytes=null;
            while (true){
                i=0;
                pos=Byte.MIN_VALUE;
                System.out.println("revcheak");
                for (byte[] b:bytess){
                    if (b!=null){
                        i++;
                    }else {
                        senders.send(new byte[]{(byte) pos,0});
                    }
                    pos++;
                }
//                i++;
                if (i==dataReqAuto.sdr.page){
                    break;
                }

                try {
                    l1= (long) ((long) Math.log1p(time*time)*Math.log(258-i)*32);
//                    Thread.sleep(l1 /2);
                } catch (Exception e) {
                    System.out.println("value of byteTtime: "+time);
                    time= (int) t0;
                }

                while (true){
                    bytes=blockingQueue.poll();
                    if (bytes==null){
                        try {
                            bytes=blockingQueue.poll(l1,TimeUnit.MILLISECONDS);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (bytes==null){
                            break;
                        }
                    }
                    l= (int) Utils.calculateChecksum(bytes,0,bytes.length-4);
                    if(l==Utils.byteArrayToInt(bytes,bytes.length-4)){
                        bytess[bytes[0]+128]= Utils.subByte(bytes,1,bytes.length-5);
                    }
                    j=0;
                    //bytess[bytes[0]+128]= Utils.subByte(bytes,1,bytes.length-1);
                }
                if (bytes==null){
                    time=time*2;
                    if (time<0){
                        time=-time;
                    }
                }else {
                    if (time>4){
                        time=time/2;
                    }
                }
                dataReqAuto.cj=j;
                if (j%4==0){
                    UserContext userContext=UDPclient.mainDataQueue.contrainUser(this.userContext.userName);
                    if (userContext==null){
                        break;
                    }else {
                        if (userContext.getQueue(id)!=this.blockingQueue){
                            break;
                        }else {
                            this.userContext=userContext;
                            this.senders.InitInit(this.id,userContext);
                        }
                    }
                    if (j>14){
                        return null;
                    }
                }
                j++;
            }
            senders.send("OK".getBytes());
            senders.send("OK".getBytes());
        }else {
            return new byte[0];
        }
        l=bytess[dataReqAuto.sdr.page-1].length;
        l=((dataReqAuto.sdr.page-1)*fp)+l;
        byte[] rev=new byte[l];
        i=0;
        for (byte[] bytes:bytess){
            System.arraycopy(bytes,0,rev,i*fp,bytes.length);
            i++;
        }
        return rev;
    }

    public void sends(){
        fp = dataReqAuto.sdr.pl-1;
        File file=new File(dataReqAuto.sdr.filename);
        if (file.exists()){
            dataReqAuto.sdr.page= Math.toIntExact(file.length() / fp);
            if ((file.length() %fp)!=0){dataReqAuto.sdr.page+=1;}
        }else {
            dataReqAuto.sdr.page=0;
            String send="DB"+JSON.toJSONString(dataReqAuto.sdr);
            senders.send(send.getBytes());
            senders.send(send.getBytes());
            return;
        }
        byte[] send=("DB"+JSON.toJSONString(dataReqAuto.sdr)).getBytes();
        senders.send(send);
        System.out.println(dataReqAuto.sdr.page);

        byte[] bytes=new byte[fp];
        int p=0;
        int len=0;
        RandomAccessFile randomFile = null;
        BufferedInputStream buffer=null;
        try {
            randomFile = new RandomAccessFile(file,"r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        //buffer.mark(1449);
        byte[][] cache=new byte[dataReqAuto.sdr.page][];
        byte[] besnd = new byte[0];
        byte[] re=null;
        String s = "";
        bytes=new byte[fp];
        int t=0;
        long l;
        boolean st=false;
        while (true){
            try {
                re=blockingQueue.poll(4,TimeUnit.SECONDS);
                if (re[1]!=0){
                    s=new String(re);
//                    System.out.println("sending: "+re[0]);
                    if ("OK".equals(s)){
                        break;
                    }
                    continue;
                }
            } catch (Exception e) {
                UserContext userContext=UDPclient.mainDataQueue.contrainUser(this.userContext.userName);
                if (userContext==null){
                    break;
                }else {
                    if (t/2>0){
                        senders.send(send);
                    }
                    if (t%4==0){
                        this.userContext=userContext;
                        this.senders.InitInit(this.id,userContext);
                    }

                }
                System.out.println("dataByte timeOut");
                if(t>6){
                    break;
                }
                t++;
                continue;
            }

//            assert re != null;
            p=(re[0]+128);
            t=0;
            if (cache[p]!=null){
                senders.send(cache[p]);
                continue;
            }
            try {
                randomFile.seek(p*fp);
                len=randomFile.read(bytes);
                //randomFile.read(bytes,1,1499);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (len!=fp){
                besnd=Utils.subByte(bytes,0,len);
            }else {
                besnd=bytes;
            }
            send=Utils.byteMerger(new byte[]{re[0]},besnd);
            l=Utils.calculateChecksum(send,0,send.length);
            send=Utils.byteMerger(send,Utils.intToByteArray((int) l));
            cache[p]=send;
            senders.send(send);
        }
        try {
            randomFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("over");
    }

    @Override
    public int hashCode(){
        return dataReqAuto.sdr.hashCode();
    }
    @Override
    public boolean equals(Object o){
        return this.hashCode()==o.hashCode()?true:false;
    }

}
