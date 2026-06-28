package superlink.udpbind.client.recives.datalen;

import com.alibaba.fastjson2.JSON;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.ByteBufer;
import superlink.udpbind.client.recives.ByteEpollQue;
import superlink.udpbind.client.recives.Senders;
import superlink.udpbind.usedata.DataRequest;
import superlink.util.Utils;

import java.io.File;
import java.util.concurrent.Callable;

import static superlink.udpbind.client.recives.datalen.DsCon.add;

public class DataSyn extends DataLength {
    Senders senders;
    public ByteBufer blockingQueue;
    Callable<Boolean> signCall;
    Callable<Boolean> process;
    byte[] rev;

    public DataSyn(UserContext userContext, short id){
        System.out.println("sendor");
        this.userContext=userContext;
        this.id=id;
        if (userContext.map.containsKey(id)){
            blockingQueue=userContext.getQueue(id);
        }else {
            blockingQueue=new ByteEpollQue(256,this);
            userContext.setQueue(id,blockingQueue);
        }

        this.senders=new Senders();
        senders.InitInit(this.id,userContext);
        add(this);
        userContext.getTask((short) id).task=this;
    }

    public long time = System.currentTimeMillis();
    public long waittime = System.currentTimeMillis();

    public byte[] star ;
    public void reqFile(String byteName){
        DataRequest sdr=new DataRequest();
        sdr.requestname= UDPclient.userlocal.username;
        sdr.filename=byteName;
        sdr.id=id;

        byte[] dt=("Ds"+ JSON.toJSONString(sdr)).getBytes();

        senders.sendSym(dt);

        final Integer[] i = {0};
        final DataRequest[] dataRequest = new DataRequest[1];
        signCall= () -> {
            if (i[0] >=3){
                throw new  NullPointerException();
            }
            try {
                star =blockingQueue.poll();
                if (star==null){
                    return false;
                }
                dataRequest[0] =JSON.parseObject(star,DataRequest.class);
                signCall=null;
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            senders.sendSym(dt);
            i[0]++;
            return false;
        };

        process= () -> {
            long t2=System.currentTimeMillis();
            long waitTime=userContext.getTime();
            i[0] =0;
            int l;
            byte[][] bytess=null;
            if (dataRequest[0].page > 0){
                int page= dataRequest[0].page;//int page=dataRequest.page-Integer.MAX_VALUE;
                bytess=new byte[page][];
                Integer pos=0;//Integer pos=Integer.MIN_VALUE;
                while (pos<page){
                    byte[] recive=null;
                    try {
                        recive=blockingQueue.poll();
                        pos++;
                        System.out.println("reving:"+pos);
                        int r= Utils.byteArrayToInt(Utils.subByte(recive,0,4));
                        bytess[r]= Utils.subByte(recive,4,recive.length-1);
                        time=System.currentTimeMillis();
                    } catch (Exception e) {
                        i[0]++;
                        e.printStackTrace();
                    }
                }

                while (true){
                    i[0] =0;
                    System.out.println("revcheak");
                    for (byte[] b:bytess){
                        if (b!=null){
                            i[0]++;
                        }else {
                            senders.send(Utils.intToByteArray(i[0]));
                        }
                    }
                    byte[] bytes;
                    while (true){
                        bytes=blockingQueue.poll();
                        if (bytes==null){
                            break;
                        }
                        bytess[Utils.byteArrayToInt(Utils.subByte(bytes,0,4))]=
                                Utils.subByte(bytes,4,bytes.length-1);
                        time=System.currentTimeMillis();
                    }
                    DsCon.waitTimes.put(time,this);
//                i++;
                    waittime =t2- time;
                    if (i[0] != dataRequest[0].page){
                        waittime=System.currentTimeMillis()+time;
                        return false;
                    }else {
                        break;
                    }
                }
            }else {
                return true;
            }
            senders.send("OK".getBytes());
            senders.send("OK".getBytes());
            l=bytess[dataRequest[0].page-1].length;

            l=((dataRequest[0].page-1)*1446)+l;
            rev=new byte[l];
            i[0] =0;
            for (byte[] bytes:bytess){
                System.arraycopy(bytes,0,rev, i[0] *1446,bytes.length);
                i[0]++;
            }
           return true;
        };
        DsCon.run();
    }

    public void sends(DataRequest sdr){
        this.sdr=sdr;
        run();
    }

    @Override
    public void run() {
        byte[] bytes0= DsCon.readData(sdr.filename);
        if (bytes0==null&&sdr.dir!=null){
            DsCon.DataFile dataFile=new DsCon.DataFile();
            dataFile.setFile(new File(sdr.dir));
            DsCon.setData(sdr.filename,dataFile);
            bytes0=DsCon.readData(sdr.filename);
        }
        if (bytes0!=null){
            if (bytes0.length<Integer.MAX_VALUE){
                sdr.page= Math.toIntExact(bytes0.length / 1446);
                if ((bytes0.length %1446)!=0){sdr.page+=1;}
            }
        }
        senders.send(JSON.toJSONBytes(sdr));


        int len=0;
        byte[][] bytess=new byte[sdr.page][];
        final int[] ii = {0};
        byte[] finalBytes = bytes0;
        signCall= () -> {
            int p = ii[0];
            byte[] bytes = new byte[1446];
            try {
                for (; ii[0] < p +128; ii[0]++){
                    bytes =Utils.subByte(finalBytes, ii[0] *1446,1446);
                    System.out.println("send");
                    bytess[ii[0]]= bytes;
                    senders.send(Utils.byteMerger(Utils.intToByteArray(ii[0]), bytes));
                    if (len<sdr.page){
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        };

        process= () -> {
            byte[] re=null;
            String s = "";
            byte[] bytes = new byte[1446];
            while (blockingQueue.size()>0){
                try {
                    System.out.println("sending");
                    re=blockingQueue.poll();
                    s=new String(re);
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }

                if (re.length==2&&"OK".equals(s)){
                    return true;
                }else{
                    try {
                        int pos =Utils.byteArrayToInt(Utils.subByte(re,0,4))*1446;
                        bytes =bytess[pos];
                        senders.send(Utils.byteMerger(Utils.intToByteArray(pos), bytes));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
            return false;
        };
        DsCon.run();
    }


    @Override
    public int hashCode(){
        return userContext.hashCode()^id;
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

