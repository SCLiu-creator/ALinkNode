package superlink.udpbind.client.recives.data;

import com.alibaba.fastjson2.JSON;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.ByteBufer;
import superlink.udpbind.client.recives.Senders;
import superlink.udpbind.usedata.DataRequest;
import superlink.util.ExceptionSmall;
import superlink.util.Utils;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static superlink.util.Utils.byteMerger;

//待弃用
public class DataTool1 {
    public UserContext userContext;

    public byte[] recive=new byte[0];
    public ByteBufer blockingQueue;
    public short id;
    public long time;
    public Senders senders;
    public static int fp=1024;
    String prex="Dt";

    public DataTool1(String uesrname, int id){
        if (uesrname.equals(UDPclient.userlocal.username)){
            return;
        }
        this.userContext= UDPclient.mainDataQueue.getUserContext(uesrname);
        this.blockingQueue=userContext.getDataQue(this.id);
        this.id=(short)id;
        this.senders=new Senders();
        senders.Init(this.id,uesrname);
        System.out.println("Datatool:  " +id+"  uid: " +userContext.getUserId());
    }
    public DataTool1(String prex,String uesrname, int id){
        if (uesrname.equals(UDPclient.userlocal.username)){
            return;
        }
        this.userContext= UDPclient.mainDataQueue.getUserContext(uesrname);
        this.blockingQueue=userContext.getDataQue(this.id);
        this.id=(short)id;
        this.senders=new Senders();
        senders.Init(this.id,uesrname);
        System.out.println("Datatool:  " +id+"  uid: " +userContext.getUserId());
        this.prex=prex;
    }


    public String receiveData(String filename){
        return receiveData(filename,"Dt");
    }

    public byte[] dt;

    public String receiveData(String filename,String prex){
        DataRequest sdr=new DataRequest();
        sdr.requestname= prex;
        sdr.filename=filename;
        sdr.id=id;
        int i = 1;//10000001
        byte[] dt=(prex+JSON.toJSONString(sdr)).getBytes();

        byte[] re=getSendbyte(dt);

        sdr.filename=null;
        sdr.dir=null;
        this.dt=("Dt"+JSON.toJSONString(sdr)).getBytes();

        senders.sendSym(dt);

        DatagramPacket sPacket=new DatagramPacket(re,re.length,userContext.inetAddress,userContext.port);
        boolean a=true;
        byte[] bytes=new byte[1472];
        DatagramPacket datagramPacket=new DatagramPacket(bytes,bytes.length);
        byte[] data = null;
        String s=null;
        int l = 0;
        boolean b=false;
        int breaki0=0;
        DataRequest dataRequest = null;
        try {
            while (a) {
                try {
                    data = blockingQueue.poll(3000, TimeUnit.MILLISECONDS);
                    if (data == null) {
                        if (breaki0>3){
                            b=cheakSelf(breaki0);
                            if (b){
                                return null;
                            }
                        }else {
                            senders.send1(re);
                            senders.sendSym(dt);
                            breaki0++;
                            continue;
                        }
                    }
                    System.out.println(new String(data) + "::" + datagramPacket.getPort());
                    if ("SD".equals(new String(data, 0, 2))) {
                        dataRequest=JSON.parseObject(new String(data).substring(2),DataRequest.class);
                        l=dataRequest.page;
                        senders.send1(re);
                        a = false;
                    }
                    if ("NU".equals(new String(data, 0, 2))) {
                        return null;
                    }
                    if (prex.equals(new String(data, 0, 2))) {
                        breaki0=0;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            byte[][] arry=new byte[l][];
            byte[] OK = getSendbyte("OK".getBytes());
            OK= byteMerger(OK,new byte[4]);

            breaki0=0;
            byte[] er=getSendbyte("er".getBytes());
            int t=0;
            int cal=0;
            int cal1=0;
            int que;
            while (l >= i) {
                try {
                    data = blockingQueue.poll(3000, TimeUnit.MILLISECONDS);
                    if (data == null) {
                        senders.send1(OK);
                        if (t>4){
                            b=cheakSelf(t);
                            if (b){
                                break;
                            }
                        }
                        t++;
                        continue;
                    }else {
                        t=0;
                    }
                    //   , ok   ,ch
                    if (data.length==2 || (data[0]==83&&data[1]==68) || (data[0]==99&&data[1]==104)){
                        s = new String(data);
//                        if (s.equals(prex)){
//
//                        }
                        senders.send1(OK);
                        continue;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    que = Utils.byteArrayToInt(data,2);
                    cal= (int) Utils.calculateChecksum(data,10,data.length-10);
                    cal1=Utils.byteArrayToInt(data,6);
                }catch (Exception n){
                    n.getMessage();
                    continue;
                }
                if (que==i) {
                    if (cal==cal1){
                        arry[i-1]=data;
                        i = i + 1;
                        System.out.println("OKRECV: "+que);
                        sendOk(OK,que);
                        senders.send1(OK);
                    }else {
                        senders.send1(er);
                    }
                }else {
                    if (que<i) {
                        sendOk(OK,que);
                        senders.send1(OK);
                    }
                }

            }
            // int length=list.get(l-10000000).length;
            int length=arry[l-1].length-10;
            recive=new byte[fp*(l-1)+length];
            AtomicInteger atomicInteger=new AtomicInteger(0);
            System.out.println("Revice   : "+filename);
            for (byte[] bytesbuf:arry ){
                System.arraycopy(bytesbuf,10,recive,atomicInteger.get()*fp,bytesbuf.length-10);
                atomicInteger.getAndIncrement();
            }

        }catch (Exception e){
            e.printStackTrace();
            return null;
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
                byte[] NU= byteMerger(Utils.getUseridByte(userContext.getBothId(),(short)id),nu.getBytes());;
                senders.send1(NU);//发起请求
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

            dataRequest.filename =pathSort.path.substring(pathSort.path.lastIndexOf("/")+1);;
            dataRequest.dir=pathSort.path;
            dataRequest.requestname= UDPclient.userlocal.username;
            dataRequest.id=id;

            String sends="SD"+ JSON.toJSONString(dataRequest);
            byte[] send= byteMerger(Utils.getUseridByte(userContext.getBothId(),(short)id),sends.getBytes());

            dataRequest.filename=null;
            dataRequest.dir=null;
            this.dt=("Dt"+JSON.toJSONString(dataRequest)).getBytes();

            senders.send1(send);//发起请求

            long timestar=System.currentTimeMillis();
            byte[] repacket= null;//ready
            int t=0;
            boolean b=false;
            while (true) {
                try {
                    repacket = blockingQueue.poll(2000, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (repacket==null){
                    t++;
                    senders.send1(send);
                    b=cheakSelf(t);
                    if (b){
                        return;
                    }
                    continue;
                }
                System.out.println("rePacket:"+new String(repacket));
                if (prex.equals(new String(repacket,0,2))){
                    senders.send1(send);
                    break;
                }
            }
            long timestar2=System.currentTimeMillis();
            time=timestar2-timestar;
            System.out.println("datatooltime:  "+time);

            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            fileInputStream=new FileInputStream(pathSort.path);
            bufferedInputStream=new BufferedInputStream(fileInputStream);
            bytes=new byte[fp+16];

            setprex(Utils.getUseridByte(userContext.getBothId(),(short)id),"OO",bytes);
            int cal=0;
            while (dataRequest.page>que){//发送数据
                que=que+1;
                len=bufferedInputStream.read(bytes,16,fp);
//                if (bytes.length!=len){
//                    bytes= subByte(bytes,0,len);
//                }
                //bytes=Utils.byteMerger(Utils.byteMerger("OO".getBytes(),Utils.intToByteArray(que)),bytes);

                System.arraycopy(Utils.intToByteArray(que),0,bytes,8,4);
                cal=(int)Utils.calculateChecksum(bytes,16,len);
                System.arraycopy(Utils.intToByteArray(cal),0,bytes,12,4);
                String OK="";
                int a=0;
                int i=0;
                byte[] rev = null;
                do{
                    try {
                        senders.send(bytes,16+len);
                        rev=blockingQueue.poll(3, TimeUnit.SECONDS);
                        OK=new String(rev,0,2);
                        i=Utils.byteArrayToInt(rev,2);
                    }catch (Exception e){
                        senders.send1(send);
                        b=cheakSelf(a);
                        if (b){
                            return;
                        }
                        if (a/3==0){
                            senders.send("ch".getBytes(),Utils.intToByteArray(que));
                        }
                        if (a>9){
                            throw new ExceptionSmall("a>5");
                        }
                        a++;
                    }
                    if (("OK".equals(OK)&&(i==que))){
                        break;
                    }else {
                        if ("Dt".equals(OK)){
                            a=0;
                        }
                    }
                }while (true);
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

    public boolean cheakSelf(int t){
        if (t>3){
            UserContext user=UDPclient.mainDataQueue.getUserContext(userContext.userName);
            if (user!=null){
                if (user.getQueue(id)==blockingQueue){
                    senders.sendSym(this.dt);
                }else {
                    return true;
                }
            }else {
                return true;
            }
            if (t>10){
                return true;
            }
        }
        return false;
    }

    /*给bytes加上userid和id*/
    public byte[] getSendbyte(byte[] bytes){
        return byteMerger(Utils.getUseridByte(this.userContext.getBothId(),(short)id),bytes);
    }

    public byte[] sendOk(byte[] bytes,int i) {
        bytes[8] = (byte) ((i >> 24) & 0xFF);
        bytes[9] = (byte) ((i >> 16) & 0xFF);
        bytes[10] = (byte) ((i >> 8) & 0xFF);
        bytes[11] = (byte) (i & 0xFF);
        return bytes;
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

    public void finalize(DataTool1 dataTool1){
        System.out.println("userContext.deltask(id): "+id);
        userContext.deltask(id);
    }
}

