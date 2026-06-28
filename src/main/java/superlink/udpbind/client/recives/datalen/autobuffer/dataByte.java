package superlink.udpbind.client.recives.datalen.autobuffer;

import com.alibaba.fastjson2.JSON;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.ByteBufer;
import superlink.udpbind.client.recives.datalen.AutoBuffer;
import superlink.udpbind.client.recives.Senders;
import superlink.udpbind.usedata.BufferRequest;
import superlink.util.Utils;

import java.util.concurrent.TimeUnit;

public class dataByte {
    public UserContext userContext;
    public short id;
    Senders senders;
    ByteBufer blockingQueue;
    BufferRequest sdr;
    public int fp=1449;
    AutoBuffer dataAuto;

//    public dataByte(AutoBuffer dataAuto){
//        System.out.println("revor");
//        this.userContext=dataAuto.userContext;
//        this.id=dataAuto.id;
//        blockingQueue=dataAuto.blockingQueue;
//        this.senders=dataAuto.senders;
//        this.dataAuto=dataAuto;
//    }
    public dataByte(AutoBuffer dataAuto,int max){
        System.out.println("revor");
        this.userContext=dataAuto.userContext;
        this.id=dataAuto.id;
        blockingQueue=dataAuto.blockingQueue;
        this.senders=dataAuto.senders;
        this.dataAuto=dataAuto;
        fp=max-1;
    }

    public byte[] reqFile(BufferRequest dataRequest,long timeLong){
        int time= (int) timeLong;
        int i=0;
        int l;
        byte[][] bytess=null;
        long t0=time;
        byte[] cheak=Utils.byteMerger(("BA").getBytes(),Utils.intToByteArray(id));
        if (dataRequest.page > 0){
            bytess=new byte[dataRequest.page][];
            int pos=0;
            int j=0;
            long l1 =10;
            byte[] bytes=null;
            byte[] bytec=new byte[]{'A','B'};
            while (true){
                try {
                    long waitTime=System.currentTimeMillis();
                    while (true){
                        bytes=blockingQueue.poll();
                        if (bytes==null){
                            try {
                                bytes=blockingQueue.poll(l1,TimeUnit.MILLISECONDS);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        if (bytes==null){
                            break;
                        }else {
                            if (bytes.length>5){
                                if (bytess[bytes[0]+128]==null){
                                    l= (int) Utils.calculateChecksum(bytes,0,bytes.length-4);
                                    if(l==Utils.byteArrayToInt(bytes,bytes.length-4)){
                                        bytess[bytes[0]+128]= Utils.subByte(bytes,1,bytes.length-5);
                                    }
                                }else {
                                    long now = System.currentTimeMillis();
                                    if(now-waitTime>l1/2 && blockingQueue.size()==0){
                                        break;
                                    }
                                }
                            }else {
                                if (bytes.length==2 && Utils.equals(bytes,bytec)){
                                    return null;
                                }
                            }
                            j=0;
                        }
                    }
                }catch (Exception e){
                    System.out.println("AutoBuffer Null");
                }

                if (bytes==null){
                    time=time*2;
                    if (time<0){
                        time=-time;
                    }
                }else {
                    if (time>2){
                        time=time/2;
                    }
                    if (time==0){
                        time= Math.toIntExact(timeLong / 2);
                    }
                }
                if (j%4==0&&j!=0){
                    if (this.userContext.cheak()){
                        break;
                    }else {
                        senders.sendSym(cheak);
                        if (userContext.getQueue(id)!=this.blockingQueue){
                            break;
                        }
//                        else {
//                            this.userContext=UDPclient.mainDataQueue.getUserContext(userContext.getUserId());
//                            this.senders.InitInit(this.id,userContext);
//                        }
                    }
                }
                if (j>11&&l1>1000){
                    break;
                }
                j++;
                i=0;
                pos=Byte.MIN_VALUE;
                System.out.println("revcheak");
                for (byte[] b:bytess){
                    if (b!=null){
                        i++;
                    }else {
                        senders.send(new byte[]{(byte) pos});
                    }
                    pos++;
                }
                if (i==dataRequest.page){
                    break;
                }

                try {
                    l1= (long) ((long) Math.log(safeMultiply(time,time))*Math.log(258-i)*32);
                    if (l1<0||l1>30*1000){
                        l1=timeLong;
                    }
                } catch (Exception e) {
                    System.out.println("value of byteTtime: "+time);
                    time= (int) t0;
                }
            }
            senders.send("OK".getBytes());
            senders.send("OK".getBytes());
        }else {
            return new byte[0];
        }
        l=bytess[dataRequest.page-1].length;
        l=((dataRequest.page-1)*fp)+l;
        byte[] rev=new byte[l];
        i=0;
        for (byte[] bytes:bytess){
            System.arraycopy(bytes,0,rev,i*fp,bytes.length);
            i++;
        }
        return rev;
    }

    public void sends(BufferRequest sdr,byte[] data){
        sdr.page= Math.toIntExact(data.length / fp);
        if ((data.length %fp)!=0){sdr.page+=1;}

        byte[] send=("DB"+JSON.toJSONString(sdr)).getBytes();
        senders.send(send);
        sdr.name=UDPclient.userlocal.username;
        sdr.bufname=null;
        byte[] cheak=Utils.byteMerger(("BA").getBytes(),Utils.intToByteArray(id));
        dataAuto.send=cheak;
        System.out.println(sdr.page);

        int sp = 0;
        byte[] bytes=new byte[fp];
        int p=0;
        int len=0;
        byte[][] cache=new byte[sdr.page][];
        byte[] re=null;
        String s = "";
        bytes=new byte[fp];
        int t=1;
        long l;
        boolean st=false;
        for (int i = 0; i <sdr.page ; i++) {
            try {
                len=data.length-i*fp;
                if (len>fp){
                    bytes=new byte[fp];
                }else {
                    bytes=new byte[len];
                }
                System.arraycopy(data,i*fp,bytes,0,bytes.length);
            } catch (Exception e) {
                e.printStackTrace();
            }

            send=Utils.byteMerger(new byte[]{(byte) (i-128)},bytes);
            l=Utils.calculateChecksum(send,0,send.length);
            send=Utils.byteMerger(send,Utils.intToByteArray((int) l));
            cache[i]=send;
            sp = i;
            senders.send(send);
        }

        while (true){
            try {
                re=blockingQueue.poll(3,TimeUnit.SECONDS);
                if (re.length==1){
                    p=(re[0]+128);
                    senders.send(cache[p]);
                }else {
                    s=new String(re);
                    if ("OK".equals(s)){
                        break;
                    }
                    if ("AB".equals(s)){
                        return;
                    }
                }
                t=0;
            } catch (Exception e) {
                if (this.userContext.cheak()){
                    break;
                }else {
                    if (t/2>0){
                        senders.sendSym(cheak);
                    }
                    if (t%4==0){
                        this.senders.InitInit(this.id,userContext);
                    }
                }
                send = cache[sp];
                sp = sp+1;
                if (sp==cache.length){
                    sp=0;
                }
                senders.send(send);
                System.out.println("dataByte timeOut");
                if(t>9){
                    break;
                }
                t++;
            }
        }
        System.out.println("over");
    }

    public long safeMultiply(long a, long b) {
        // 使用 Math.multiplyExact 检测溢出，但捕获异常
        try {
            return Math.multiplyExact(a, b);
        } catch (ArithmeticException e) {
            return Long.MAX_VALUE/64;
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

//    public static void main(String[] args) {
////        long l1=0;
////        int i= (int) Long.MAX_VALUE-100;
////        long time=safeMultiply(64*64*64*64*64,64*64*64*64*64);
////        l1= (long) ((long) Math.log(Long.MAX_VALUE)*Math.log(258-i)*16);
////        System.out.println(l1);
//    }

}
