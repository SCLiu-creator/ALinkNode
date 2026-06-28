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

public class dataShorts {
    public UserContext userContext;
    public int id;
    Senders senders;
    ByteBufer blockingQueue;
    DataRequest sdr;

    public dataShorts(DataSmall dataAuto){
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
        if (dataRequest.page >= 0){
            long l1= (long) Math.log(time*dataRequest.page)*50;
            int page=dataRequest.page;
            int j=0;
            bytess=new byte[dataRequest.page][];
            System.out.println("dataRequest.page: "+dataRequest.page);
            int pos=0;
            int w=page/1024;
            int ip=0;
            while (pos<page){
                byte[] recive=null;
                try {
                    recive=blockingQueue.poll();
                    pos++;
                    int r=Utils.byteArrayToshort(recive)-Short.MIN_VALUE;
                    bytess[r]= Utils.subByte(recive,2,recive.length-2);
                } catch (Exception e) {

                    if (recive==null){
                        if (w>0){
                            for (;ip<pos;ip++){
                                if(bytess[ip]==null){
                                    senders.send(Utils.shortToByteArray((short)(ip+Short.MIN_VALUE)));
                                }
                            }
                            w--;
                        }else {
                            break;
                        }
                        try {
                            Thread.sleep(l1);
                        } catch (InterruptedException in) { }
                        j++;
                    }
                    System.out.println("Exceotion:"+pos);
                }
            }

            while (true){
                i=0;
                pos=Short.MIN_VALUE;
                System.out.println("revcheak");
                for (byte[] b:bytess){
                    if (b!=null){
                        i++;
                    }else {
                        senders.send(Utils.shortToByteArray((short) pos));
                    }
                    pos++;
                }
                if (i==dataRequest.page){
                    break;
                }
                try {
                    Thread.sleep(l1 );
                } catch (InterruptedException e) {}
                byte[] bytes=null;
                while (true){
                    bytes=blockingQueue.poll();
                    if (bytes==null){
                        break;
                    }
                    j=0;
                    bytess[Utils.byteArrayToshort(bytes)-Short.MIN_VALUE]= Utils.subByte(bytes,2,bytes.length-2);
                }
                j++;
                if (j>3){
                    return null;
                }
            }
            senders.send("OKS".getBytes());
            senders.send("OKS".getBytes());
        }
        l=bytess[dataRequest.page-1].length;
        l=((dataRequest.page-1)*1448)+l;
        byte[] rev=new byte[l];
        i=0;
        for (byte[] bytes:bytess){
            System.arraycopy(bytes,0,rev,i*1448,bytes.length);
            i++;
        }
        return rev;
    }

    public void sends(DataRequest sdr) {
        this.sdr=sdr;
        File file=new File(sdr.filename);
        if (file.exists()){
            sdr.page= Math.toIntExact(file.length() / 1448);
            if ((file.length() %1448)!=0){sdr.page+=1;}
        }
        String send="SB"+ JSON.toJSONString(sdr);
        System.out.println("sdr.page"+sdr.page);
        byte[] bytes=new byte[1448];
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
        senders.send(send.getBytes());
        try {
            while ((len=randomFile.read(bytes))!= -1){
                if (len!=1448){
                    bytes=Utils.subByte(bytes,0,len);
                }
                senders.send(Utils.byteMerger(Utils.shortToByteArray((short) (p+Short.MIN_VALUE)),bytes));
                bytess[p]=bytes.clone();
                p++;
            }
        }catch (IOException e){}

        byte[] re=null;
        String s = "";
        byte[] bytesperx=new byte[2];
        int t=0;
        while (true){
            try {
                re=blockingQueue.poll(2, TimeUnit.SECONDS);
            } catch (Exception e) {
                if (t<3){
                    senders.send(send.getBytes());
                }else {
                    System.out.println("dataShort timeOut");
                    break;
                }
                t++;
            }
            if (re!=null){
                if (re.length==3){
                    s=new String(re);
                    if ("OKS".equals(s)){
                        break;
                    }
                }
                p=Utils.byteArrayToshort(re);
                System.out.println("sending: "+p);
                bytes=bytess[p-Short.MIN_VALUE];
                bytesperx[0]=re[0];
                bytesperx[1]=re[1];
                senders.send(Utils.byteMerger(bytesperx,bytes));
            }else {
                break;
            }
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
