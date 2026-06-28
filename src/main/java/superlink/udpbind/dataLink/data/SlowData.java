package superlink.udpbind.dataLink.data;

import com.alibaba.fastjson2.JSON;
import superlink.udpbind.dataLink.UdpData;
import superlink.udpbind.handle.Handler;
import superlink.udpbind.usedata.DataRequest;
import superlink.udpbind.usedata.UserRequest;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import static superlink.udpbind.client.UDPclient.userlocal;
import static superlink.util.Utils.byteMerger;
import static superlink.util.Utils.subByte;

public class SlowData {

    public UdpData udpData;
    public UserRequest userRequest;
    public DatagramSocket dataSocket;
    public byte[] recive=new byte[0];

    public SlowData(String uesrname,int id){
        this.udpData= (UdpData) Handler.UdpMap.get(uesrname);
        this.userRequest=udpData.userRequest;
        this.dataSocket=udpData.dataSocket;

    }

    public void receiveData(int l){
        int i = 10000001;
        DatagramPacket sPacket=new DatagramPacket("re".getBytes(),"re".getBytes().length,userRequest.toaddress,userRequest.toport);
//        JOptionPane.showMessageDialog(null,"成功send");
        try {
            dataSocket.send(sPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
        boolean a=false;
        while (l> i) {
            byte[] bytes=new byte[1472];
            DatagramPacket datagramPacket=new DatagramPacket(bytes,bytes.length);
            Integer ii=i;
//            Thread time=new Thread(){
//                @Override
//                public void run(){
//                    try {
//                        Thread.sleep(2000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    DatagramPacket yanzheng=new DatagramPacket(("CO"+ii).getBytes(),10,userRequest.toaddress,userRequest.toport);
//
//                    try {
//                        dataSocket.send(yanzheng);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            };
//            time.start();
            try {
                dataSocket.receive(datagramPacket);
                System.out.println(new String(datagramPacket.getData())+"::"+datagramPacket.getPort());
                if ("SD".equals(new String(datagramPacket.getData(),0,2))){
                    dataSocket.send(sPacket);
                };
                //               time.interrupt();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String s=new String(bytes,0,2);

            if ("OO".equals(s)){
                String ss=new String(bytes,0,datagramPacket.getLength()).substring(2,10);
                Integer que=Integer.valueOf(ss);
                if (que.equals(i)){
                    i = i +1;
                    byte[] bytes1=subByte(bytes,10,1462);
                    recive=byteMerger(recive,bytes1);
                    String string="OC"+i;
                    DatagramPacket sePacket=new DatagramPacket("OK".getBytes(),2,userRequest.toaddress,userRequest.toport);
                    try {
                        dataSocket.send(sePacket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    DatagramPacket sePacket=new DatagramPacket("OS".getBytes(),2,userRequest.toaddress,userRequest.toport);
                    try {
                        dataSocket.send(sePacket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void sendfile(String name){
        try {
            File file=new File(name);
            byte[] bytes=new byte[1462];
            byte[] flush=new byte[1462];
            int len;
            int que=10000000;
            //    DatagramPacket datagramPacket =new DatagramPacket(bytes,bytes.length,userRequest.toaddress,userRequest.toport);
            DatagramPacket rePacket =new DatagramPacket(flush,flush.length);
            if (!file.exists()){
                DataRequest dataRequest=new DataRequest();
                String sends="SD"+ JSON.toJSONString(dataRequest);
                return;
            }
            long fileLong=file.length();
            int filelength= Math.toIntExact(fileLong);
            DataRequest dataRequest=new DataRequest();
            if (filelength%1462 != 0){
                dataRequest.page=filelength/1462+10000001;}
            else { dataRequest.page=filelength/1462+10000000;}
            dataRequest.filename =name.substring(name.lastIndexOf("/")+1);;
//            File tempFile =new File(name);
//            String fileName = tempFile.getName();
            dataRequest.dir=name;
            dataRequest.requestname=userlocal.username;

            String sends="SD"+JSON.toJSONString(dataRequest);
            byte[] sendbyte= byteMerger(new byte[]{0},sends.getBytes());
            DatagramPacket datagramPacket=new DatagramPacket(sendbyte,0,sendbyte.length,userRequest.toaddress,userRequest.toport);
            while (true) {
                dataSocket.send(datagramPacket);
                //datagramPacket.setData(new byte[1472]);
                dataSocket.receive(rePacket);
                System.out.println("rePacket:"+new String(rePacket.getData()));

                if ("re".equals(new String(rePacket.getData(),0,2))){
                    break;
                }else {
                    rePacket.setData(new byte[10]);
                }
            }

            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            FileInputStream fileInputStream=new FileInputStream(name);
            BufferedInputStream bufferedInputStream=new BufferedInputStream(fileInputStream);

            while (dataRequest.page>que){//发送数据
                DatagramPacket redata=new DatagramPacket(new byte[20],20);
                que=que+1;
                len=bufferedInputStream.read(bytes);
                DatagramPacket senddata=new DatagramPacket(setdataed(que,bytes),0,len+10,userRequest.toaddress,userRequest.toport);
                stablesend(dataSocket,senddata);
//                dataSocket.send(senddata);
//                while (true) {
//                    dataSocket.receive(redata);
//                    redata.setData(new byte[1472],0,1472);
//                    System.out.println("redata:"+new String(redata.getData()));
//                    System.out.println(redata.getPort());
//                    int pro = Integer.valueOf(new String(redata.getData(), 2, 8));
//                    if (pro==que){
//                        dataSocket.send(senddata);
//                    }else {
//                        break;
//                    }
//
//                }
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    public byte[] setdataed(int page,byte[] after){
        String datehead="OO"+page;
        return byteMerger(datehead.getBytes(),after);
    }

    public boolean stablesend(DatagramSocket socket, DatagramPacket packetse) throws IOException {
        socket.send(packetse);

        Thread thread=new Thread(){
            @Override
            public void run(){
                while (! isInterrupted() ){
                    try {
                        Thread.sleep(400);
                        socket.send(packetse);
                    } catch (InterruptedException | IOException e) {
                        Thread.currentThread().interrupt();
                        e.printStackTrace();
                        System.out.println("outThread");
                        break;
                    }

                }
            }
        };
        thread.start();
        DatagramPacket rev=new DatagramPacket(new byte[2],2);
        String OK="";
        do{
            try {
                socket.receive(rev);
                String s=new String(rev.getData());
                OK=s;
                System.out.println("s:"+s);
                thread.interrupt();

            }catch (IOException i){
                i.printStackTrace();
            }
        }while (!OK.equals("OK"));
        System.out.println("lastto");
        return true;
    }

}
