package superlink.udpbind.client.recives.data.blockBuffer;

import com.alibaba.fastjson2.JSON;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.ByteBufer;
import superlink.udpbind.client.recives.Senders;
import superlink.udpbind.usedata.DataRequest;
import superlink.util.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

//对接流式
public class ByteStream {
    public UserContext userContext;
    public short id;
    Senders senders;
    ByteBufer orderQue;
    public DataStRead stRead;
    public DataStWrite stWrite;
    DataRequest sdr;
    public int fp=1439;


    public ByteStream(UserContext userContext,short id){
        System.out.println("revor");
        this.userContext=userContext;
        this.id=id;
        orderQue =userContext.getQueue(id);
        stRead=new DataStRead(userContext,id);
        this.senders=new Senders();
        senders.InitInit(this.id,userContext);
        userContext.getTask(id).task=this;
    }

    public ByteStream(UserContext userContext,short id, int max){
        System.out.println("revor");
        this.userContext=userContext;
        this.id=id;
        orderQue =userContext.getQueue(id);
        stWrite=new DataStWrite(userContext,id);
        userContext.setQueue((short) id,stWrite);
        this.senders=new Senders();
        senders.InitInit(this.id,userContext);
        fp=max;
        userContext.getTask(id).task=this;
    }

    public void link(byte[] star){
        orderQue.add(Utils.subByte(star,2,star.length-2));
    }

    int itime = 0;
    public void reqFile(DataRequest dataRequest, OutputStream outputStream){
        this.sdr=dataRequest;
        byte[] dt = ("BS" + JSON.toJSONString(sdr)).getBytes();
        senders.sendSym(dt);
        Long time = System.currentTimeMillis();
        byte[] star = null;

        String re;
        userContext.setQueue((short) id,stRead);
//        senders.sendSym(dt);
        while (itime<5) {
            try {
                star = orderQue.poll(3, TimeUnit.SECONDS);
                re = new String(star);
                if (re.equals("BA")){
                    return ;
                }else {
                    if (re.equals("WA")){
                        itime=0;
                        continue;
                    }
                    dataRequest=JSON.parseObject(re, DataRequest.class);
                    break;
                }
            } catch (Exception e) {
                System.out.println("Auto Timeout");
            }
            senders.sendSym(dt);
            itime++;
        }
        if (itime > 5) {
            return ;
        }
        userContext.setQueue((short) id,stRead);

        Long t2 = System.currentTimeMillis();
        time = t2 - time;
        stRead.setTime0(Math.toIntExact(time));
        int l;
        byte[] bytes = null;
        int ts = 0;

        try {
            while (true) {
                bytes = stRead.read();
                if (bytes.length > 4) {
                    outputStream.write(Utils.subByte(bytes, 4, bytes.length - 8));
                    ts++;

                } else {
                    break;
                }
//                System.out.println("hash: "+ Arrays.hashCode(bytes));
                System.out.println(ts);
            }
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("AutoBuffer Null");
        }

        System.out.println("OK");
    }

    public void sends(DataRequest sdr,InputStream inputStream) throws IOException {
        byte[] send=("SB"+JSON.toJSONString(sdr)).getBytes();
        senders.send(send);
        Thread.currentThread().setName("testSt");
//        byte[] cheak=Utils.byteMerger(("BA").getBytes(),Utils.intToByteArray(id));
//        dataAuto.send=cheak;
        int tens=0;
        byte[] bytes=new byte[fp];
        int i;
        while ((i=inputStream.read(bytes))>0){
            stWrite.write(bytes,i);
            tens++;
            System.out.println(tens);
        }
        stWrite.over();
        inputStream.close();
        System.out.println("over");
    }

    public void close(){
        userContext.deltask(id);
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
