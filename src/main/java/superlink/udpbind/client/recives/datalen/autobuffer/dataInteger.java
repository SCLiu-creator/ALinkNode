package superlink.udpbind.client.recives.datalen.autobuffer;

import com.alibaba.fastjson2.JSON;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.ByteBufer;
import superlink.udpbind.client.recives.datalen.AutoBuffer;
import superlink.udpbind.client.recives.Senders;
import superlink.udpbind.usedata.BufferRequest;
import superlink.util.Utils;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class dataInteger {
    public UserContext userContext;
    public short id;
    Senders senders;
    ByteBufer blockingQueue;
    BufferRequest sdr;
//    public int fp=1446;
    public int fp=1442;

    AutoBuffer dataAuto;
//    public dataInteger(AutoBuffer dataAuto){
//        System.out.println("revor");
//        this.userContext=dataAuto.userContext;
//        this.id=dataAuto.id;
//        blockingQueue=dataAuto.blockingQueue;
//        this.senders=dataAuto.senders;
//        this.dataAuto=dataAuto;
//    }
    public dataInteger(AutoBuffer dataAuto,int max){
        System.out.println("revor");
        this.userContext=dataAuto.userContext;
        this.id=dataAuto.id;
        blockingQueue=dataAuto.blockingQueue;
        this.senders=dataAuto.senders;
        this.dataAuto=dataAuto;
        this.fp=max-8;
    }

    byte[] rev=null;
    public byte[] reqFile(BufferRequest dataRequest,long timeLong){
        long time= timeLong;
        int i=0;
        int l;
        byte[][] bytess=null;
        int c0=0;
        long ot=time;
        byte[] cheak=Utils.byteMerger(("BA").getBytes(),Utils.intToByteArray(id));
//        byte[][] bytessText=new byte[dataRequest.page][];
        if (dataRequest.page <= 0){
            return new byte[0];
        }else {
            int page=dataRequest.page;//int page=dataRequest.page-Integer.MAX_VALUE;
            bytess=new byte[dataRequest.page][];
            Integer pos=0;//Integer pos=Integer.MIN_VALUE;
            int j = 0,t = 0;
            int sr=0;
            int index=0;
//            blockingQueue.clear();
            System.out.println("page:  "+page );
            byte[] bytes=null;
            long l1 = 10;
            long time0=0;
            int tt=126;
            int rt=0;
            long waitTime = System.currentTimeMillis();
            int alen=0;
            long startTime = System.currentTimeMillis();
            while (true){
                time0=System.currentTimeMillis();
//                index=0;

                while (true){
//                    bytes=blockingQueue.poll();
//                    if (bytes==null){
                        try {
//                            l1=l1-(System.currentTimeMillis()-time0);
                            bytes=blockingQueue.poll(l1/2,TimeUnit.MILLISECONDS);
                            if (bytes==null){
                                int qt=16;
                                for (int k = index; k >pos&&qt>0 ; k--) {
                                    if (bytess[k]==null){
                                        senders.send(Utils.intToByteArray(k));
                                        System.out.println("rreq "+k+"fp");
                                        qt--;
                                    }
                                }
                                bytes=blockingQueue.poll(l1/2,TimeUnit.MILLISECONDS);
                            }
                            if (bytes[0]!=0){
                                if (Arrays.equals(bytes,"AB".getBytes())){
                                    return null;
                                }
                                j=0;
                                t++;
                                continue;
                            }
                            l1=ot;
                        } catch (InterruptedException e) {
                            time0=System.currentTimeMillis()-time0;
                            System.out.println("Interrupt  waittime   "+time0+"   nowtime   "+l1);
                            break;
                        } catch (Exception e) {
                            time0=System.currentTimeMillis()-time0;
                            System.out.println("Null  waittime   "+time0+"   nowtime   "+l1);
                            break;
                        }
//                    }
                    try {
                        index=Utils.byteArrayToInt(Utils.subByte(bytes,0,4));
                        if (bytess[index]==null){
                            l= (int) Utils.calculateChecksum(bytes,0,bytes.length-4);
                            c0=Utils.byteArrayToInt(bytes,bytes.length-4);
                            if(l!=c0){
                                System.out.println("Ehe cal: "+l);
                                System.out.println("Ehe index: "+index);
                                continue;
                            }
                            bytess[index]= Utils.subByte(bytes,4,bytes.length-8);

                            System.out.println("revoer+"+index);
                            alen+=1;
                            rt++;
                            senders.send(Utils.subByte(bytes,0,4),"del".getBytes());
                        }else {
                            long now = System.currentTimeMillis();
                            if(now-startTime>l1 && blockingQueue.size()==0){
                                break;
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    j=0;
                    t++;
                }
                waitTime = System.currentTimeMillis()-startTime;
                i=0;
                pos=0;
                if (waitTime>userContext.delayTime/2 || rt>250){
                    startTime = System.currentTimeMillis();
                    tt=255;
                    rt=0;
                }
                if (tt>0){
                    byte[] b;
                    int k = sr;
                    for (int len=bytess.length; k <len ; k++) {
                        b=bytess[k];
                        if (b==null){
                            i++;
                            senders.send(Utils.intToByteArray(k));
                            System.out.println("req "+k+"fp");
                            tt=tt-1;
//                        senders.send(Utils.intToByteArray(k));
                            if (blockingQueue.size()>=i){
                                break;
                            }
                        }
                        if (i==1){
                            sr=pos;
                        }
                        if (i == 256){
                            break;
                        }
                        pos++;
                    }
                    dataAuto.send = Utils.intToByteArray(k);
                }

                System.out.println("revcheak   " + (bytess.length-i)/bytess.length);
//                if (i==0){
                if (alen>=page){
                    try {
                        byte[] bytesss=bytess[bytess.length-1];
                        l=bytesss.length;
                        l=((dataRequest.page-1)*fp)+l;
                        rev=new byte[l];
                        i=0;
                        for (byte[] bt:bytess){
                            System.arraycopy(bt,0,rev,i*fp,bt.length);
                            i++;
                        }
                        break;
                    }catch (Exception e){
                        e.printStackTrace();
                        sr=0;
                    }
                }

                if (j>=4){
                     UserContext userContext=UDPclient.mainDataQueue.contrainUser(this.userContext.userName);
                    if (userContext==null){
                        return  null;
                    }else {
                        if (j>11&&l1>1000){
                            break;
                        }
                        if (j%3==0){
                            if (userContext.getQueue(id)!=this.blockingQueue){
                                break;
                            }
                            senders.sendSym(cheak);
                            if(this.userContext!=userContext) {
                                this.userContext=userContext;
                                this.senders.InitInit(this.id,userContext);
                            }
                        }
                    }
                }

                try {
                    if (t==0){
                        if (time<1024){
                            time=time*2+2;
                        }
                        if (time>=1024){
                            time=1024;
                        }
                        if (time<0){
                            time=(int) ot;
                        }
                    }else {
                        if(time>8){
                            time=time/2;
                        }
                    }
                    l1=safeMultiply(time, toTime(j));
                } catch (Exception e) {
                    e.printStackTrace();
                    time= (int) ot;
                }
                t=0;
                j++;
            }
            senders.send("OK".getBytes());
            senders.send("OK".getBytes());

            System.out.println(i);
            System.out.println("over");
            return rev;
        }
    }
//    public static long toTime(int j){
//        double v=1;
//        if(j>7){
//            j=8;
//        }
//        for (; j >0 ; j--) {
//            v=v*(3-(j-2)*(j-2));
//        }
//        v= Math.abs(v);
//        return (long) v;
//    }
    public static long toTime(int j){
        double v=1;
        if(j>7){
            j=8;
        }
        for (; j >0 ; j--) {
            v=v+(v*Math.abs(j-3));
        }
        v= Math.abs(v);
        return (long) v;
    }
    public long safeMultiply(long a, long b) {
        // 使用 Math.multiplyExact 检测溢出，但捕获异常
        try {
            return a+b;
        } catch (ArithmeticException e) {
            return Long.MAX_VALUE;
        }
    }
    public boolean state=true;
    public void sends(BufferRequest sdr,byte[] data){
        this.sdr=sdr;
        sdr.page= Math.toIntExact(data.length / fp);
        if ((data.length %fp)!=0){sdr.page+=1;}

        byte[] send=("DI"+JSON.toJSONString(sdr)).getBytes();
        senders.send(send);
        byte[] cheak=Utils.byteMerger(("BA").getBytes(),Utils.intToByteArray(id));
        dataAuto.send=cheak;
        byte[] bytes=new byte[fp];
        int p=0;
        int len=0;
        byte[] zero=new byte[0];
        byte[] buffer;
        byte[][] cache=new byte[sdr.page][];
        byte[] re=null;
        String s = "";
        byte[] pre=null;
        int j=0;
        int ts=0;
        long l=0;
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
        }
        long waitTime = userContext.getTime()/2;
        int star=0;
        while (state){
            //System.out.println("sending");
            int i;
            p=star;
            if(ts>=64){
                for ( i= 0; i < 128; i++) {
                    p=i+star;
                    if (p<sdr.page &&cache[p]==null){
                        try {
                            len=data.length-p*fp;
                            if (len>fp){
                                bytes=new byte[fp];
                            }else {
                                if (len<0){
                                    continue;
                                }else {
                                    bytes=new byte[len];
                                }
                            }
                            System.arraycopy(data,p*fp,bytes,0,bytes.length);
                        } catch (Exception e) {
                            e.printStackTrace();
                            break;
                        }
                        pre=Utils.intToByteArray(p);
                        send=Utils.byteMerger(pre,bytes);
                        l=Utils.calculateChecksum(send,0,send.length);
                        pre=Utils.intToByteArray((int) l);
                        send=Utils.byteMerger(send,pre);
                        senders.send(send);
                        cache[p]=send;
                        System.out.println("send+ "+p+"fp");
                    }
                }
                ts=0;
            }

            star=p;
            try {
                re=blockingQueue.poll(waitTime,TimeUnit.MILLISECONDS);
                if (re.length!=4){
                    if(re.length==7){
                        try {
                            p=Utils.byteArrayToInt(re);
                            cache[p]=zero;
                            if(p>star-128){
                                ts++;
                            }
                            continue;
                        }catch (Exception e){
                        }
                    }
                    s=new String(re,0,2);
                    if ("OK".equals(s)||"AB".equals(s)){
                        state=false;
                        break;
                    }
                    if (re[0]=='B'&&re[1]=='A' && re.length==2){
                        return;
                    }
                    j=0;
                    continue;
                }
                dataAuto.send=send;
                j=0;
            } catch (Exception e) {
                ts=ts+64;
                senders.sendSym(cheak);
                System.out.println("send(send)   "+j);
                if (re==null){
                    if (j>3){
                        UserContext userContext=UDPclient.mainDataQueue.contrainUser(this.userContext.userName);
                        if (userContext==null){
                            break;
                        }else {
                            if (userContext.getQueue(id)==null){
                                break;
                            }
                            if (j%3==0){
                                if (this.userContext!=userContext){
                                    this.senders.InitInit(this.id,userContext);
                                }
                            }
                            if (j>12){
                                state=false;
                                break;
                            }
                        }
                    }
                    j++;
                }
                continue;
            }
            try {
//                pre=Utils.subByte(re,0,4);
                p=Utils.byteArrayToInt(re);
                buffer = cache[p];
                if (buffer!=null){
                    if(buffer==zero)continue;
                    senders.send(buffer);
                    System.out.println("send+ "+p+"fp");
                    continue;
                }else {
                    len=data.length-p*fp;
                    if (len > fp) {
                        bytes = new byte[fp];  // 正常情况：取 fp 长度
                    } else if (len > 0) {
                        bytes = new byte[len]; // 不足 fp 时取剩余全部
                    } else {
                        continue; // 无数据可处理，跳过
                    }
                    System.arraycopy(data,p*fp,bytes,0,bytes.length);
                    pre=Utils.intToByteArray(p);
                    send=Utils.byteMerger(pre,bytes);
                    l=Utils.calculateChecksum(send,0,send.length);
                    pre=Utils.intToByteArray((int) l);
                    send=Utils.byteMerger(send,pre);
                    senders.send(send);
                    System.out.println("send+ "+p+"fp");
                }
            }catch (ArrayIndexOutOfBoundsException e){
                System.out.println("exc re "+ Arrays.toString(re));
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }

//            System.out.println("sending "+Utils.byteArrayToInt(pre));
            j=0;
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
    }

    @Override
    public int hashCode(){
        return sdr.hashCode();
    }
    @Override
    public boolean equals(Object o){
        return this.hashCode()==o.hashCode()?true:false;
    }

    public static void main(String[] args) {
        for (int i=0;i<15;i++){
            System.out.println(toTime(i));
        }
    }
}
