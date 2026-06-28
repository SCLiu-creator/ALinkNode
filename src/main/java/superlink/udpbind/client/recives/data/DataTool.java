package superlink.udpbind.client.recives.data;

import com.alibaba.fastjson2.JSON;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.ByteBufer;
import superlink.udpbind.client.recives.DataLenMange;
import superlink.udpbind.client.recives.Senders;
import superlink.udpbind.usedata.DataRequest;
import superlink.util.ExceptionSmall;
import superlink.util.Utils;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static superlink.util.Utils.byteMerger;
import static superlink.util.Utils.subByte;

//待弃用
public class DataTool {

    public UserContext userContext;

    public byte[] recive=null;
    public ByteBufer blockingQueue;
    public short id;
    public long time;
    public Senders senders;
    public int fp=1024;
    public DataTool(String uesrname, int id) throws Exception {
        if (uesrname.equals(UDPclient.userlocal.username)){
            throw new Exception();
        }
        this.userContext= UDPclient.mainDataQueue.getUserContext(uesrname);
        this.id=(short)id;
        this.blockingQueue=userContext.getDataQue(this.id);
        this.senders=new Senders();
        senders.Init(this.id,uesrname);
        System.out.println("Datatool:  " +id+"  uid: " +userContext.getUserId());
        fp= DataLenMange.getLen(uesrname);
    }

    public String receiveData(String filename){
        DataRequest sdr=new DataRequest();
        sdr.requestname= UDPclient.userlocal.username;
        sdr.filename=filename;
        sdr.id=id;
        sdr.pl=fp;
        int i = 1;//10000001
        byte[] dt=("DT"+JSON.toJSONString(sdr)).getBytes();
        byte[] DT= byteMerger(Utils.getUseridByte(this.userContext.getBothId(), (short) 0),dt);
        byte[] re=getSendbyte(dt);
        //todo

        Senders.Sends(userContext.inetAddress,userContext.port,DT);

        boolean a=true;
        byte[] bytes=new byte[1472];
//        DatagramPacket datagramPacket=new DatagramPacket(bytes,bytes.length);
        byte[] data = null;
        String s=null;
        int l = 0;
        int breaki0=0;
        DataRequest dataRequest = null;
        try {
            while (a) {
                try {
                    data = blockingQueue.poll(2000, TimeUnit.MILLISECONDS);
                    if (data == null) {
                        if (breaki0>4){
                            return null;
                        }else {
                            Senders.Sends(userContext.inetAddress,userContext.port,re);
                            breaki0++;
                            continue;
                        }
                    }
//                    System.out.println(new String(data) + "::" + datagramPacket.getPort());
                    if ("SD".equals(new String(data, 0, 2))) {
                        dataRequest=JSON.parseObject(new String(data).substring(2),DataRequest.class);
                        l=dataRequest.page;
                        Senders.Sends(userContext.inetAddress,userContext.port,re);
                        a = false;
                    }
                    if ("NU".equals(new String(data, 0, 2))) {
                       return null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            List<byte[]> list=new ArrayList<>();
            byte[][] arry=new byte[l][];
            byte[] OK = getSendbyte("OK".getBytes());
            OK=Utils.byteMerger(OK,new byte[4]);

            breaki0=0;
            int t=0;
            while (l >= i) {
                try {
                    data = blockingQueue.poll(4000, TimeUnit.MILLISECONDS);
                    if (data == null) {
                        Senders.Sends(userContext.inetAddress,userContext.port,OK);

                        t++;
                        if (t>3){
                            return null;
                        }else {
                            continue;
                        }

                    }else {
                        t=0;
                    }
//                    s = new String(data, 0, 2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                    if ("OO".equals(s)) { }
                int que;
                try {
                    //String ss = new String(data,2,8).substring(2, 10);
//                    String ss = new String(data,2,8);
//                     que = Integer.valueOf(ss);
                    que = byteArrayToInt2(data);
                }catch (NumberFormatException n){
                    continue;
                }
                if (que==i) {
//                if (que==i ||que<i) {
//                if (que.equals(i)) {
//                    byte[] bytes1 = subByte(data, 10, data.length - 10);
//                    arry[i-10000001]=bytes1;
                    arry[i-1]=data;
                    //list.add(bytes1);
                    i = i + 1;
                    System.out.println("OKRECV: "+que);
                    try {
                        setRe(OK,Utils.intToByteArray(que));
                        sendOk(que);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    if (que<i) {
                        try {

                            setRe(OK,Utils.intToByteArray(que));
                            sendOk(que);
//                            dataSocket.send(sePacket);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
           // int length=list.get(l-10000000).length;
            int length=arry[l-1].length-6;
            recive=new byte[fp*(l-1)+length];
            AtomicInteger atomicInteger=new AtomicInteger(0);
//                list.iterator().forEachRemaining( action-> {
//                    atomicInteger.getAndIncrement();
//                    System.arraycopy(action,0,recive,atomicInteger.get()*fp,action.length);
//                });
            System.out.println("Revice   : "+filename);
            for (byte[] bytesbuf:arry ){
                System.arraycopy(bytesbuf,6,recive,atomicInteger.get()*fp,bytesbuf.length-6);
                atomicInteger.getAndIncrement();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("Revice Over   : "+filename);
        return dataRequest.filename;

    }

    public void sendfile(String name){
        Utils.PathSort pathSort=Utils.pathPrase(name);
        FileInputStream fileInputStream=null;
        BufferedInputStream bufferedInputStream=null;
        try {
            File file=new File(pathSort.path);
            byte[] bytes=new byte[fp];
            int len;
            int que=0;//10000000;
            if (!file.exists()){
                String nu="NU";
                byte[] NU= byteMerger(Utils.getUseridByte(userContext.getBothId(), (short) id),nu.getBytes());;
                Senders.Sends(userContext.inetAddress,userContext.port,NU);
                //发起请求
                return;
            }
            long fileLong=new FileInputStream(file).getChannel().size();
            int filelength= Math.toIntExact(fileLong);
            DataRequest dataRequest=new DataRequest();
            if (filelength%fp != 0){
                dataRequest.page=filelength/fp+1;}
            else {
                dataRequest.page=filelength/fp;}

            System.out.println("page:   "+dataRequest.page);
            //todo
            dataRequest.filename =pathSort.path.substring(pathSort.path.lastIndexOf("/")+1);;
            dataRequest.dir=pathSort.path;
            dataRequest.requestname= UDPclient.userlocal.username;
            dataRequest.id=id;

            String sends="SD"+ JSON.toJSONString(dataRequest);
            byte[] send= byteMerger(Utils.getUseridByte(userContext.getBothId(), (short) id),sends.getBytes());
            //发起请求
            Senders.Sends(userContext.inetAddress,userContext.port,send);

            long timestar=System.currentTimeMillis();
//            send= byteMerger(Utils.getUseridByte(userContext.getBothId(),id),sends.getBytes());
//            datagramPacket=new DatagramPacket(send,send.length,userContext.inetAddress,userContext.port);
            byte[] repacket= null;//ready
            int t=0;
            while (true) {

                try {
                    repacket = blockingQueue.poll(2000, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (repacket==null){
                    t++;
                    if (t>4){
                        return;
                    }
                    Senders.Sends(userContext.inetAddress,userContext.port,send);
                    continue;
                }
                System.out.println("rePacket:"+new String(repacket));
                if ("DT".equals(new String(repacket,0,2))){
                    Senders.Sends(userContext.inetAddress,userContext.port,send);
                    break;
                }
            }
            long timestar2=System.currentTimeMillis();
            time=timestar2-timestar;
            System.out.println("datatooltime:  "+time);

            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            fileInputStream=new FileInputStream(pathSort.path);
            bufferedInputStream=new BufferedInputStream(fileInputStream);

            bytes=new byte[fp+12];

            setprex(Utils.getUseridByte(userContext.getBothId(), (short) id),"OO",bytes);
            while (dataRequest.page>que){//发送数据

                que=que+1;
//                len=bufferedInputStream.read(bytes,12,1456);
//                if (bytes.length!=len+12){
//                    bytes= subByte(bytes,0,len+12);
//                }
////                String.valueOf(que).getBytes()
//                setpage(Utils.intToByteArray(que),bytes);
////                bytes=getSendbyte(setdataed(que,bytes));
////                DatagramPacket senddata=new DatagramPacket(bytes,0,bytes.length,userContext.inetAddress,userContext.port);
////                stablesend(dataSocket,senddata);
//                stablesend(bytes,que);

                len=bufferedInputStream.read(bytes,0,fp);
                if (bytes.length!=len){
                    bytes= subByte(bytes,0,len);
                }

                bytes=Utils.byteMerger(Utils.byteMerger("OO".getBytes(),Utils.intToByteArray(que)),bytes);
                stablesend1(bytes,que);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                fileInputStream.close();
                bufferedInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("sendfile Over   : "+name);
    }

    public boolean stablesend1(byte[] bytes,int que) throws Exception {
        String OK="";
        int a=0;
        int i=0;
        byte[] rev = new byte[0];
        do{
            try {
                senders.send(bytes);
                rev=blockingQueue.poll(3, TimeUnit.SECONDS);
                OK=new String(rev,0,2);
                i=byteArrayToInt2(rev);
            }catch (Exception e){
                a++;
                if (a>4){
                    throw new ExceptionSmall("a>5");
                }
            }
        }while (!(OK.equals("OK")&&(i==que)));
//        System.out.println("OKSend: "+que);
//        System.out.println("lastto");
        return true;
    }
    public void sendOk(int q) throws Exception {
        byte[] bytes=Utils.byteMerger("OK".getBytes(),Utils.intToByteArray(q));
        senders.send(bytes);
    }

    /*给bytes加上userid和id*/
    public byte[] getSendbyte(byte[] bytes){
        return byteMerger(Utils.getUseridByte(this.userContext.getBothId(), (short) id),bytes);
    }

    public void setprex(byte[] prex,String s,byte[] data){
        byte[] bytes=s.getBytes();
        data[0]=prex[0];
        data[1]=prex[1];
        data[2]=prex[2];
        data[3]=prex[3];
        data[4]=prex[4];
        data[5]=prex[5];
        data[6]=bytes[0];
        data[7]=bytes[1];
    }
    public void setRe(byte[] data,byte[] bytes){
        data[8]=bytes[0];
        data[9]=bytes[1];
        data[10]=bytes[2];
        data[11]=bytes[3];
    }

    public int byteArrayToInt(byte[] bytes,int from){
        int value = 0;
        for (int i = from; i < 4+from; i++) {
            int shift = (3+from - i) * 8;
            value += (bytes[i] & 0xFF) << shift;
        }
        return value;
    }
    public int byteArrayToInt2(byte[] bytes){
        int value = 0;
        for (int i = 2; i < 6; i++) {
            int shift = (5 - i) * 8;
            value += (bytes[i] & 0xFF) << shift;
        }
        return value;
    }
    public int byteArrayToInt8(byte[] bytes){
        int value = 0;
        for (int i = 8; i < 12; i++) {
            int shift = (11 - i) * 8;
            value += (bytes[i] & 0xFF) << shift;
        }
        return value;
    }


    @Override
    public void finalize(){
        System.out.println("userContext.deltask(id): "+id);
        userContext.deltask(id);
    }

}
