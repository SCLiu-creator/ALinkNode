package superlink.udpbind.dataLink;

import superlink.udpbind.dataqueue.DataQueue;
import superlink.udpbind.client.UDPclient;
import com.alibaba.fastjson2.JSON;
import superlink.udpbind.dataLink.rec.Bindsrec;
import superlink.udpbind.handle.Handler;
import superlink.udpbind.usedata.UserRequest;
import superlink.udpbind.dataqueue.ReciveQueueFactory;
import superlink.util.Utils;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

import static superlink.udpbind.client.UDPclient.userlocal;

//udp数据传输连接
public class UdpData implements Runnable{
    public DatagramSocket dataSocket;
    public int dataport;
    public UserRequest userRequest=new UserRequest();
    public LiveBinds liveBinds;
    public DataQueue dataQueue;
    public Bindsrec bindsrec;
    public boolean state=true;

    public UdpData(UserRequest user,Boolean b){
            try {
                while (true){
                    try {
                        Random randomport = new Random();
                        int port=randomport.nextInt(3000)+6000;
                        dataSocket = new DatagramSocket(port);
                       // dataSocket.setSoTimeout(8);
                        dataport=port;
                        userRequest.inport=port;
                        userRequest.username=user.username;
                        break;
                    }catch (SocketException s){
                        System.out.println("port already in use");
                    }
                }

                userRequest.toaddress=user.toaddress;
                userRequest.toport=user.toport;
                String send="FD"+userRequest.toString();
                DatagramPacket datagramPacket=new DatagramPacket(send.getBytes(),send.getBytes().length, UDPclient.serverip,UDPclient.serverport);
                try {//请求服务器获取外部端口
                    dataSocket.send(datagramPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                byte[] bytes=new byte[1024];
                DatagramPacket sd=new DatagramPacket(bytes,bytes.length);
                dataSocket.receive(sd);
                System.out.println("服务器返回requestport"+new String(sd.getData()));
                System.out.println("服务器返回requestport"+sd.getPort()+"//"+sd.getAddress());

                //String string=new String(sd.getData());
                UserRequest userRequestram=JSON.parseObject(Arrays.copyOfRange(sd.getData(),4,sd.getData().length),UserRequest.class);
                userRequest.requestaddress=userRequestram.requestaddress;
                userRequest.requestport=userRequestram.requestport;

                UserRequest requestND=new UserRequest();
                requestND.toport=userRequest.toport;
                requestND.toaddress=userRequest.toaddress;
                requestND.requestaddress=userRequest.requestaddress;
                requestND.requestport=userRequest.requestport;
                requestND.username= userlocal.username;
                System.out.println("UserRequest1:"+userRequest.toString());
                String sendND="ND"+JSON.toJSONString(requestND);
                datagramPacket=new DatagramPacket(sendND.getBytes(),sendND.getBytes().length, UDPclient.serverip,UDPclient.serverport);

                //通知对象节点
                try {
                    dataSocket.send(datagramPacket);//打通信息端口和服务器
//                    DatagramPacket finalDatagramPacket = datagramPacket;
//                    new Thread(){
//                        @Override
//                        public void run(){
//                            try {
//                                dataSocket.send(finalDatagramPacket);
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    };
                } catch (IOException e) {
                    e.printStackTrace();
                }
                DatagramPacket getPacket=new DatagramPacket(new byte[256],256);//接收服务器返回的DN，DN来自对方接收ND所处理
              //  System.out.println(new String(getPacket.getData(),0,getPacket.getLength())) ;
                dataSocket.receive(getPacket);
//                if (getPacket.getData()[0]!=48){
//                    getPacket.setData(new byte[256]);
//                    dataSocket.receive(getPacket);
//                }
//                while (getPacket.getPort()!=UDPclient.serverport){
//                    getPacket.setData(new byte[256]);
//                    dataSocket.receive(getPacket);
//                }

                String getbind=new String(getPacket.getData(),0,getPacket.getLength());
                System.out.println("getbind:"+getbind+";getbindport:"+getPacket.getPort());

                UserRequest thisuserRequest=JSON.parseObject(getbind.substring(8),UserRequest.class);
                userRequest.toport=thisuserRequest.requestport;
                userRequest.toaddress=thisuserRequest.requestaddress;//设置真正的端口地址
                byte[] ll= Utils.byteMerger(new byte[]{0},"LL".getBytes());
                DatagramPacket datagramPacket1=new DatagramPacket(ll,ll.length,userRequest.toaddress,userRequest.toport);
                dataSocket.send(datagramPacket1);
                System.out.println("对象返回"+new String( getPacket.getData())+";返回port"+getPacket.getPort());
                System.out.println("本地对象"+userRequest.toString());


            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

    }
    public UdpData(UserRequest user){
        while (true){
            System.out.println("接收到连接请求"+JSON.toJSONString(user));
            while (true){
                try {
                    Random randomport = new Random();
                    int port=randomport.nextInt(3000)+6000;
                    dataSocket = new DatagramSocket(port);
                    //dataSocket.setSoTimeout(8);
                    dataport=port;
                    userRequest.inport=port;
                    userRequest.username=user.username;
                    break;
                }catch (SocketException s){
                    System.out.println("port already in use");
                }
            }

            userRequest.inaddress=dataSocket.getLocalAddress();
            userRequest.toport=user.requestport;//得到请求方外部端口
            userRequest.toaddress=user.requestaddress;
            String send="FD"+userRequest.toString();

            DatagramPacket datagramPacket=new DatagramPacket(send.getBytes(),send.getBytes().length, UDPclient.serverip,UDPclient.serverport);

            try {//请求服务器获取外部端口
                dataSocket.send(datagramPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte[] bytes=new byte[1024];
            DatagramPacket sd=new DatagramPacket(bytes,bytes.length);
            try {
                dataSocket.receive(sd);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //String string=new String(sd.getData());
            UserRequest userRequestram=JSON.parseObject(Arrays.copyOfRange(sd.getData(),4,sd.getData().length),UserRequest.class);
            userRequest.requestaddress=userRequestram.requestaddress;
            userRequest.requestport=userRequestram.requestport;
            send="DN"+userRequest.toString();//已经拥有对方外部端口和自己外部端口
            System.out.println("接收方连接usrq"+send);

            datagramPacket=new DatagramPacket(send.getBytes(),send.getBytes().length, UDPclient.serverip,UDPclient.serverport);

            try {//要求对方主机发出请求
                dataSocket.send(datagramPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            DatagramPacket sendPacket=new DatagramPacket(send.getBytes(),send.getBytes().length,userRequest.toaddress,userRequest.toport);
            try {//发出连接请求，本地防火墙打开
                dataSocket.send(sendPacket);
                System.out.println("发出连接请求，本地防火墙打开");
            } catch (IOException e) {
                e.printStackTrace();
            }
            break;
        }
    }


    @Override
    public void run(){
        System.out.println("UDpip:   "+dataSocket.toString()+"  port:  "+dataport );
        Handler.UdpMap.put(userRequest.username,this);
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
       // threadPoolExecutor=new ThreadPoolExecutor(1,4,3,TimeUnit.MINUTES,new LinkedBlockingQueue<>(2));
        ReciveQueueFactory.getDataQueue(userRequest.username);//添加长连接0号队列
        try {
            dataSocket.setReceiveBufferSize(65537*10);
            dataSocket.setSoTimeout(10*1000*60);//设置超时时间，待配置
        } catch (SocketException e) {
            e.printStackTrace();
        }

        dataQueue=ReciveQueueFactory.getDataQueue(userRequest.username);
        BlockingQueue<byte[]> blockingQueue=dataQueue.quemap.get((byte)0);
        this.bindsrec=new Bindsrec(blockingQueue,userRequest.username);

        this.liveBinds =new LiveBinds(userRequest);
        Handler.liveMap.put(userRequest.username, liveBinds);

        dataQueue.threadPoolExecutor.execute(this.bindsrec);
        dataQueue.threadPoolExecutor.execute(liveBinds);


//        Thread live=new Thread(liveBinds);
//        live.start();
//        this.liveBinds = liveBinds;

    }

    public void over(){
        this.bindsrec.over();
        this.liveBinds.over();
        state=false;
    }

//    @Override
//    public void finalize(){
//        System.out.println("UdpData Recycle:"+userRequest.username+":"+userRequest.toport);
//        state=false;
//    }




    /**
     *   从内存中读取字节数组——byte数组截取
     */
    public static void ByteArrayInputStream(String[] args) throws IOException {
        String str1 = "132asd";
        byte[] b = new byte[3];
        ByteArrayInputStream in = new ByteArrayInputStream(str1.getBytes());
        in.read(b);
        System.out.println(new String(b));
        in.read(b);
        System.out.println(new String(b));
    }
    /**
     * 将所有的字节数组全部写入内存中，之后将其转化为字节数组——byte数组合并
     */
    public static void ByteArrayOutputStream(String[] args) throws IOException {
        String str1 = "132";
        String str2 = "asd";
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        os.write(str1.getBytes());
        os.write(str2.getBytes());
        byte[] byteArray = os.toByteArray();
        System.out.println(new String(byteArray));
    }
    //发送图片
    public void sendpicture(){
        File file=new File("");
        try {
            FileInputStream fileInputStream=new FileInputStream(file);

        ByteArrayOutputStream byteArrayInputStream=new ByteArrayOutputStream();
        byte[] bytes=new byte[1024];
        int len=0;
            while ((len=fileInputStream.read(bytes))!=-1) {
                byteArrayInputStream.write(bytes,0,len);

            }
            fileInputStream.close();
            byte[] data=byteArrayInputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static byte[] getFile(String name) {//提取图片
        File file = new File(name);//"C:/a"
        byte[] bytes=null;
        try {
            FileInputStream fos = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fos);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = fos.read(buff)) != -1) {
                byteArrayOutputStream.write(buff, 0, len);
            }
             bytes= byteArrayOutputStream.toByteArray();


        } catch (IOException e) {
            e.printStackTrace();
        }return bytes;
    }

    public void toPicture2(byte[] data){//保存为图片
        File file=new File("");
        try {
//            BASE64Decoder decoder = new BASE64Decoder();
//            byte[] imgbyte = decoder.decodeBuffer("刚刚将字节数组转成的字符串");
            OutputStream fos = new FileOutputStream(file);
            fos.write(data,0,data.length);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void sd(){

    }



    public byte[] stablerecive(DatagramSocket socket,DatagramPacket packet) throws IOException {

        DatagramPacket rev=new DatagramPacket(new byte[2],2);
        boolean b=true;
        do{
            try {
                socket.receive(packet);
                String s=new String(packet.getData());

            }catch (IOException i){
                i.printStackTrace();
            }
        }while (! Thread.interrupted());
        return packet.getData();
    }


}
