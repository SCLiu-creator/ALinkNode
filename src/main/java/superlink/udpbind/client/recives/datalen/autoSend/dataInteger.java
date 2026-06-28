package superlink.udpbind.client.recives.datalen.autoSend;

import com.alibaba.fastjson2.JSON;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.ByteBufer;
import superlink.udpbind.client.recives.Senders;
import superlink.udpbind.client.recives.datalen.AutoData;
import superlink.udpbind.usedata.DataRequest;
import superlink.util.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.concurrent.TimeUnit;

public class dataInteger {
    public UserContext userContext;
    public short id;
    Senders senders;
    ByteBufer blockingQueue;
    DataRequest sdr;
    public int fp=1442;

    AutoData dataAuto;
    public dataInteger(AutoData dataAuto,int max){
        System.out.println("revor");
        this.userContext=dataAuto.userContext;
        this.id=dataAuto.id;
        blockingQueue=dataAuto.blockingQueue;
        this.senders=dataAuto.senders;
        this.dataAuto=dataAuto;
        this.fp=max-8;
    }

    byte[] rev=null;
    public byte[] reqFile(DataRequest dataRequest,long timeLong){
        long time= timeLong;
        int i=0;
        int l;
        byte[][] bytess=null;
        int c0=0;
        long ot=time;
        byte[][] bytessText=new byte[dataRequest.page][];
        if (dataRequest.page <= 0){
            return new byte[0];
        }else {
            int page=dataRequest.page;//int page=dataRequest.page-Integer.MAX_VALUE;

            bytess=new byte[dataRequest.page][];
            dataAuto.data=bytess;
            dataAuto.sdr.page=dataRequest.page;
            Integer pos=0;//Integer pos=Integer.MIN_VALUE;
            int j = 0,t = 0,to=0;
            int sr=0,in=0;
            int index=0;
            blockingQueue.clear();
            System.out.println("page:  "+page );
            byte[] bytes=null;
            long l1 = 10;
            while (true){
                long time0=System.currentTimeMillis();
                index=0;
                long waitTime=System.currentTimeMillis();
                while (true){
                    bytes=blockingQueue.poll();
                    if (bytes==null){
                        try {
//                            l1=l1-(System.currentTimeMillis()-time0);
                            bytes=blockingQueue.poll(l1/2,TimeUnit.MILLISECONDS);
                            if (bytes==null){
                                for (int k = index; k >pos ; k--) {
                                    if (bytess[k]==null){
                                        senders.send(Utils.intToByteArray(k));
                                    }
                                }
                                bytes=blockingQueue.poll(l1/4,TimeUnit.MILLISECONDS);
                            }
                            if (bytes[0]!=0){
                                j=0;
                                t++;
                                continue;
                            }
                            l1=to;
                        } catch (Exception e) {
                            time0=System.currentTimeMillis()-time0;
                            System.out.println("Null  waittime   "+time0+"   nowtime   "+l1);
                            break;
                        }

                    }
                    try {
                        index=Utils.byteArrayToInt(Utils.subByte(bytes,0,4));
                        if (bytess[index]==null){
                            l= (int) Utils.calculateChecksum(bytes,0,bytes.length-4);
                            c0=Utils.byteArrayToInt(bytes,bytes.length-4);
                            if(l!=c0){
                                System.out.println("Ehe: "+l);
                                System.out.println("Ehei: "+index);
                                continue;
                            }
                            bytess[index]= Utils.subByte(bytes,4,bytes.length-8);
                            System.out.println("revoer+"+index);
                        }else {
                            long now = System.currentTimeMillis();
                            if(now-waitTime>l1/2 && blockingQueue.size()==0){
                                break;
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    j=0;
                    t++;
                }

                i=0;
                pos=0;
                System.out.println("revcheak");
                byte[] b;
                for (int k = sr ,len=bytess.length-1; k <=len ; k++) {
                    b=bytess[k];
                    if (b==null){
                        i++;
                        senders.send(Utils.intToByteArray(k));
                        dataAuto.send = Utils.intToByteArray(k);
                        if (blockingQueue.size()>=i){
                            break;
                        }
                    }else {
                        if (i==0){
                            sr=pos;
                        }
                    }
                    if (i == 256){
                        break;
                    }
                    pos++;
                }
                if (i==0){
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

                if (j>=5){
                     UserContext userContext=UDPclient.mainDataQueue.contrainUser(this.userContext.userName);
                    if (userContext==null){
                        break;
                    }else {
                        if (j>14){
                            break;
                        }
                        if (j%3==0){
                            ByteBufer bufer=userContext.getQueue(id);
                            if (bufer==null){
                                break;
                            }
                            if (bufer!=this.blockingQueue){
                                this.blockingQueue=bufer;
                            } else {
//                                this.userContext=userContext;
//                                this.senders.InitInit(this.id,userContext);
                                senders.sendSym(dataAuto.dt);
                            }
                        }
                    }
                }

                try {
                    if (t==0){
                        if (time==0){
                            time=ot;
                        }
                        if (time<1024){
                            time=time*2+2;
                        }
                        if (time>=1024){
                            time=1024;
                        }
                        if (time<0){
                            time=Math.abs(time);
                        }
                    }else {
                        if(time>8){
                            time=time/2;
                        }
                    }
                    l1=(long) (time*Math.log1p(i+time0));
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

    public boolean state=true;
    public void sends(DataRequest sdr){
        this.sdr=sdr;
        File file=new File(sdr.filename);
        long fl=file.length();
        if (file.exists()){
            if (fl<Integer.MAX_VALUE){
                sdr.page= Math.toIntExact(fl / fp);
                if ((fl %fp)!=0){sdr.page+=1;}
            }
        }else {
            sdr.page=0;
            String send="DI"+JSON.toJSONString(sdr);
            senders.send(send.getBytes());
            senders.send(send.getBytes());
            return;
        }
        byte[] send=("DI"+JSON.toJSONString(sdr)).getBytes();
        senders.send(send);
        byte[] cheak=Utils.byteMerger(("DA").getBytes(),Utils.shortToByteArray(id));
        dataAuto.send=cheak;
        byte[] bytes=new byte[fp];
        int p=0;
        int len=0;
        RandomAccessFile randomFile=null;
        try {
             randomFile = new RandomAccessFile(file,"r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        byte[][] cache=new byte[sdr.page][];
        byte[] re=null;
        String s = "";
        byte[] pre=null;
        int j=0;

        long l=0;
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
        }
        int star=0;
        while (state){
            //System.out.println("sending");
            int i;
            p=star;
            for ( i= 0; i < 512&&(p=i+star)<sdr.page; i++) {
//                p=i+star;
                try {
                    randomFile.seek(p*fp);
                    len=randomFile.read(bytes);
                    if (len!=fp){
                        bytes=Utils.subByte(bytes,0,len);
                    }
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
                System.out.println("send+ "+p);
            }
            star=p+1;
            try {
                re=blockingQueue.poll(3,TimeUnit.SECONDS);
                if (re.length!=4){
                    s=new String(re,0,2);
                    if ("OK".equals(s)||"AD".equals(s)){
                        state=false;
                        break;
                    }
                    if ("DA".equals(s) && re.length==2){
                        return;
                    }
                    j=0;
                    continue;
                }
                j=0;
            } catch (Exception e) {
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
                pre=Utils.subByte(re,0,4);
                p=Utils.byteArrayToInt(re);
                if (cache[p]!=null){
                    senders.send(cache[p]);
                    System.out.println("send+ "+p);
                    continue;
                }else {
                    randomFile.seek(p*fp);
                    len=randomFile.read(bytes);
                    if (len!=fp){
                        bytes=Utils.subByte(bytes,0,len);
                    }
                    pre=Utils.intToByteArray(p);
                    send=Utils.byteMerger(pre,bytes);
                    l=Utils.calculateChecksum(send,0,send.length);
                    pre=Utils.intToByteArray((int) l);
                    send=Utils.byteMerger(send,pre);
                    senders.send(send);
                    cache[p]=send;
                    System.out.println("send+ "+p);
                }
            }catch (ArrayIndexOutOfBoundsException e){
                e.printStackTrace();
                System.out.println("AOE: "+new String(re));
            } catch (Exception e){
                e.printStackTrace();
            }

            System.out.println("sending "+Utils.byteArrayToInt(pre));
            j=0;
        }
//        String fn=sdr.filename.replace(".png","_1.png");
//        String fn2=sdr.filename.replace(".png","_2.png");
//        int length=cache.length-1;
//        byte[] bytesss=new byte[(cache.length-1)*fp+length];
//        l=bytesss.length;
//
//        int i=0;

//        try{
//            File file1=new File(fn);
//            FileOutputStream inputStream1=new FileOutputStream(file1);
//            File file2=new File(fn2);
//            FileOutputStream inputStream2=new FileOutputStream(file2);
//            for (byte[] bytess:cache){
//                System.arraycopy(bytess,4,bytesss,i*fp,bytess.length-8);
//                inputStream2.write(bytesss,4,bytess.length-4);
//                i++;
////            System.out.println(Arrays.toString(bytes));
//            }
//            inputStream1.write(bytesss);
//
//        }catch (Exception e){
//            e.getMessage();
//        }

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
