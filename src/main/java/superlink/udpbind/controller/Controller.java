package superlink.udpbind.controller;

import superlink.filemanage.xmltool.XmlParser;
import superlink.httpserver.webserver.TcpProxyClient;
import superlink.httpserver.webserver.TcpProxyFactory;
import superlink.tcpbind.choose.TcpServerBind;
import superlink.udpbind.client.recives.Senders;
import superlink.udpbind.client.recives.data.DataRecive;
import superlink.udpbind.client.recives.data.stream1.QSContrain;
import superlink.udpbind.cloude.CloudLocal;
import superlink.udpbind.dataLink.data.DataFactory;
import superlink.udpbind.client.recives.data.DataSend;
import superlink.udpbind.dataLink.data.SlowData;
import superlink.udpbind.dataLink.UdpData;
import superlink.udpbind.dataqueue.DataQueue;
import superlink.udpbind.handle.handler.ReqCloudHander;
import superlink.udpbind.handle.handler.ReqDirHandler;
import superlink.udpbind.handle.handler.ReqHandleMap;
import superlink.udpbind.servlet.LiveNetServer;
import superlink.udpbind.tcpproxy.ProxySocket;
import superlink.util.Tool;
import superlink.udpbind.dataLink.LiveBinds;
import superlink.udpbind.client.UDPclient;
import com.alibaba.fastjson2.JSON;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.MainDataQueue;
import superlink.udpbind.dataqueue.ReciveQueueFactory;
import superlink.udpbind.handle.Handler;
import superlink.udpbind.usedata.DataRequest;
import superlink.udpbind.usedata.User;
import superlink.udpbind.usedata.UserRequest;
import superlink.util.Utils;
import superlink.util.thread.SThread;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

import static superlink.udpbind.client.UDPclient.*;
import static superlink.util.Tool.toLF;

public class Controller {
    public static UserRequest userRequest=new UserRequest();
    public static User user=new User();

    public static void setUserquest(Object o){
 //       int index = list.getSelectedIndex(); // 获取选中项的索引
//                String s=(String) list.getSelectedValue();
        String bt= o.toString();
        System.out.println("You set: " +"   "+bt); // 打印选中项的内容
        user = JSON.parseObject(bt,User.class);
        userRequest.requestaddress= userlocal.address;
        userRequest.requestport= userlocal.port;
        userRequest.toport=user.port;
        userRequest.toaddress=user.address;
        userRequest.username=user.username;
        userRequest.inaddress= userlocal.inaddress;
        userRequest.inport= userlocal.inport;
    }
    public static void setUserquest(User user){
        System.out.println("You set: " +"   "+"user");
        UserRequest userRequest=toUserquest(user);
        Controller.userRequest=userRequest;
        Controller.user=user;
    }
    public static UserRequest toUserquest(User user){
        System.out.println("You set: " +"   "+"user");
        UserRequest userRequest=new UserRequest();
        userRequest.requestaddress= userlocal.address;
        userRequest.requestport= userlocal.port;
        userRequest.toport=user.port;
        userRequest.toaddress=user.address;
        userRequest.username=user.username;
        userRequest.inaddress= userlocal.inaddress;
        userRequest.inport= userlocal.inport;
        return userRequest;
    }
    public static void upgradeList(){
        String send= "SL"+ userlocal.toString();;
        byte[] data = send.getBytes();//将接收到的数据变成字节数组
        DatagramPacket packet = new DatagramPacket(data, data.length, serverip, serverport);//2.创建数据报，包含发送的数据信息
        // while(true) {//通过循环不同的向客户端发送和接受数据
        try {
            UDPclient.socket.send(packet);// 4.向服务器端发送数据报
        } catch (IOException ee) {
            ee.printStackTrace();
        }
    }
    public static void stringQuery(String str){
        User user=Tool.copyUser(userlocal);
        user.nickName=str;
        String send= "SQ"+JSON.toJSONString(user);
        byte[] data = send.getBytes();//将接收到的数据变成字节数组
        // while(true) {//通过循环不同的向客户端发送和接受数据
        Senders.ServerSends(data);
    }

    //向服务器查找用户
    public static void findUser(String str){
        if(str!=null && str.length()==16){
            String send= "QU"+JSON.toJSONString(user);
            byte[] data = send.getBytes();//将接收到的数据变成字节数组
            // while(true) {//通过循环不同的向客户端发送和接受数据
            Senders.ServerSends(data);
        }
    }
    public static UserRequest chooseUserRequest(String name){
        User user = UDPclient.userMap.get(name);
        setUserquest(user);
        return userRequest;
    }
public static void liveServer(String user, InetAddress inetAddress, int port){
//    if (LiveNetServer.liveServer == false) {
        Utils.CreateName createName = new Utils.CreateName();
        new LiveNetServer(createName.create()+" "+user,inetAddress,port );
//    }
}
    public static void requestNode(UserRequest userRequest){
//        new Thread(new UdpBindSend()).start();
        UdpBindSend.udpBindSend(userRequest);
    }
    public static void requestNodeIn(User userRequest){
//        new Thread(new UdpBindSend()).start();
        UdpBindSend.UdpBindSendinlocal(userRequest);
    }
    public static void requestNodeReturn(User userRequest){
//        new Thread(new UdpBindSend()).start();
        UdpBindSend.udpReturnBind(userRequest);
    }
    public static void udpDataLink(UserRequest userRequest){
        new Thread(){
            @Override
            public void run(){
                UdpData udpData=new UdpData(userRequest,true);
                System.out.println("UDPName:"+userRequest.username);
                udpData.run();
            }
        }.start();

    }
    public static void tcpDataLink(UserRequest userRequest){
        new Thread(){
            @Override
            public void run(){
                new TcpServerBind(userRequest,true).run();
            }
        }.start();
    }


    public static void ReqHandelMap(String user){
        UserContext userContext= null;
        try {
            userContext = mainDataQueue.getUserContext1(user);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        int id=userContext.newQueue();
        new ReqHandleMap(userRequest.username,id).process();
    }
    public static void ReqCloudePage(String username){
        UserContext userContext= null;
        try {
            userContext = mainDataQueue.getUserContext1(username);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        int id=userContext.newQueue();
        String string= ":cloudefile&:" +username+".xml";
        new ReqCloudHander(username,id,string).process();
    }
    public static void ReqCloudePage(UserRequest userRequest,boolean sy){
        UserContext userContext= null;
        try {
            userContext = mainDataQueue.getUserContext1(userRequest.username);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        int id=userContext.newQueue();
        String string= ":cloudefile&:" +userRequest.username+".xml";
        new ReqCloudHander(userRequest.username,id,string,sy).process();
    }

    public static void ReqCloudePageMirror(UserRequest userRequest){
        UserContext userContext= null;
        try {
            userContext = mainDataQueue.getUserContext(userRequest.username);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int id=userContext.newQueue();
        String string= ":cloudefile&:" +userRequest.username+".xml";
        //todo
        new ReqCloudHander(userRequest.username,id,string).process();
        ReqStarCloud(userRequest);
    }

    private static void ReqStarCloud(UserRequest userRequest){
        UserRequest request=Tool.toUserRequest(userRequest);
        String s="RC"+request.toString();
        Utils.dealsSend(userRequest.username,s.getBytes());
    }

    public static void ReqUserPage(UserRequest userRequest){
        UserContext userContext= null;
        try {
            userContext = mainDataQueue.getUserContext(userRequest.username);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int id=userContext.newQueue();
        String string= XmlParser.showpath+userRequest.username +".xml";
        //todo
        new ReqDirHandler(userRequest.username,id,string).process();
    }

    //按鈕使用

    public static void udpDataSends(UserRequest userRequest){
        UdpData udpData=(UdpData)Handler.UdpMap.get(userRequest.username);
        LiveBinds liveBinds =(LiveBinds)Handler.liveMap.get(userRequest.username);
        String string=null;
        string=filechooser();
        if (string!=null){
            String finalString = string;
            new Thread(){
                @Override
                public void run(){
                    SlowData slowData=new SlowData(userRequest.username,0);
                    slowData.sendfile(finalString);
                    try {
                        byte[] ll= Utils.byteMerger(new byte[]{0},"LL".getBytes());
                        udpData.dataSocket.send(new DatagramPacket(ll,ll.length,udpData.userRequest.toaddress,udpData.userRequest.toport));
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }}.start();
        }
    }


    public static void closeUser(UserRequest userRequest){
        byte[] bytes="SE".getBytes();
        UserContext userContext= null;
        try {
            userContext = mainDataQueue.getUserContext(userRequest.username);
        } catch (Exception e) {
            e.printStackTrace();
        }
        bytes=Utils.byteMerger(Utils.getUseridByte(userContext.getBothId(), (short) 0),bytes);
        Senders.Sends(userRequest.toaddress,userRequest.toport,bytes);
    }

    public static void largeFileSendWait(UserRequest userRequest) throws InterruptedException, IOException {
        User user = bindUser.get(userRequest.username);
        if (user==null){
            requestNode(userRequest);
            Thread.sleep(50);
            udpDataLink(userRequest);
            Thread.sleep(200);
            user = bindUser.get(userRequest.username);
        }
        UdpData udpData=(UdpData)Handler.UdpMap.get(userRequest.username);
        LiveBinds liveBinds =(LiveBinds)Handler.liveMap.get(userRequest.username);

        String path=filechooser();
        String data=toLF("LR",userRequest.username,path,65497);//65497
//        DatagramPacket datagramPacket=new DatagramPacket(data.getBytes(),data.getBytes().length,user.address,user.port);
//        socket.send(datagramPacket);
        Senders.Sends(userRequest.username,0,data.getBytes());
        DataRequest dataRequest= Tool.URtoDR(userRequest,path,65497);//65497
        DataFactory.dataExecutor.execute(DataFactory.getSend(dataRequest));
    }

    public static void rFileSend(UserRequest userRequest,String filename) throws InterruptedException, IOException {
        String path=filename;
        UserContext userContext= null;
        try {
            userContext = mainDataQueue.getUserContext(userRequest.username);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int id= userContext.newQueue();
        String data= Tool.toLF("LS", userlocal.username,path,id,1024);//65497
        mainDataQueue.addtask(userRequest.username,(short)id);
//        int both=userContext.getBothId();
//        byte[] b=Utils.getUseridByte(both, (short) 0);
//        byte[] bytes= Utils.byteMerger(b,data.getBytes());
//        DatagramPacket datagramPacket=new DatagramPacket(bytes,bytes.length,user.address,user.port);
//        socket.send(datagramPacket);
        Senders.Sends(userRequest.username,0,data.getBytes());
        DataRequest dataRequest= Tool.URtoDR(userRequest,path,id,1024);
        MainDataQueue.mainthreadPoolExecutor.execute(DataFactory.getSend(dataRequest));
    }

    public static void GetUserXmlSend(UserRequest userRequest) throws InterruptedException, IOException {
        String path="data\\userpage.xml";
        UserContext userContext= null;
        try {
            userContext = mainDataQueue.getUserContext(userRequest.username);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int id= userContext.newQueue();
        String data= Tool.toLF("GU", userlocal.username,path,id,1024);//65497
        mainDataQueue.addtask(userRequest.username,(short)id);
//        int both=userContext.getBothId();
//        byte[] b=Utils.getUseridByte(both, (short) 0);
//        byte[] bytes= Utils.byteMerger(b,data.getBytes());
//        DatagramPacket datagramPacket=new DatagramPacket(bytes,bytes.length,user.address,user.port);
//        socket.send(datagramPacket);
        Senders.Sends(userRequest.username,0,data.getBytes());
        DataRequest dataRequest= Tool.URtoDR(userRequest,path,id,1024);
        MainDataQueue.mainthreadPoolExecutor.execute(new DataRecive(dataRequest));
    }

    public static void queFileRecive(UserRequest userRequest,String filename) throws IOException {
        User user = bindUser.get(userRequest.username);
        String path=filename;
        UserContext userContext= null;
        try {
            userContext = mainDataQueue.getUserContext(userRequest.username);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int id= userContext.newQueue();
            String data= Tool.toLF("QS", userlocal.username,path,id,1024);//65497
            mainDataQueue.addtask(userRequest.username,(short)id,1024);
//            int both=userContext.getBothId();
//            byte[] b=Utils.getUseridByte(both, (short) 0);
//            byte[] bytes= Utils.byteMerger(b,data.getBytes());
//            DatagramPacket datagramPacket=new DatagramPacket(bytes,bytes.length,user.address,user.port);
//            socket.send(datagramPacket);
        Senders.Sends(userRequest.username,0,data.getBytes());
            DataRequest dataRequest= Tool.URtoDR(userRequest,path,id,1024);
            MainDataQueue.mainthreadPoolExecutor.execute(new DataRecive(dataRequest));


    }

    public static void queFileSend(UserRequest userRequest) throws Exception, IOException {
        User user = bindUser.get(userRequest.username);
//        if (user==null){
//            requestNode(userRequest);
//            Thread.sleep(50);
//            udpDataLink(userRequest);
//            Thread.sleep(200);
//            for (User u:bindList){
//                if (userRequest.username.equals(u.username)){
//                    user=u;
//                    break;}
//            }
//        }
            String path=filechooser();
        if (path!=null){
            UserContext userContext= null;
            try {
                userContext = mainDataQueue.getUserContext(userRequest.username);
            } catch (Exception e) {
                e.printStackTrace();
            }
            int id= userContext.newQueue();
            String data= Tool.toLF("QS", userlocal.username,path,id,1024);//65497
            mainDataQueue.addtask(userRequest.username,(short)id);
//            int both=userContext.getBothId();
//            byte[] b=Utils.getUseridByte(both, (short) 0);
//            byte[] bytes= Utils.byteMerger(b,data.getBytes());
//            DatagramPacket datagramPacket=new DatagramPacket(bytes,bytes.length,user.address,user.port);
//            socket.send(datagramPacket);
            Senders.Sends(userRequest.username,0,data.getBytes());
            DataRequest dataRequest= Tool.URtoDR(userRequest,path,id,1024);
            MainDataQueue.mainthreadPoolExecutor.execute(new DataSend(dataRequest));
        }

    }
//内部连接使用
    public static void queFileSends(UserRequest userRequest) throws InterruptedException, IOException {
        UdpData udpData=Handler.UdpMap.get(userRequest.username);
        LiveBinds liveBinds =(LiveBinds)Handler.liveMap.get(userRequest.username);
        if (udpData==null){return;}
            String path=filechooser();
        if (path!=null){
            DataQueue dataQueue=ReciveQueueFactory.getDataQueue(userRequest.username);
            int id= dataQueue.newId();
            String data= Tool.toLF("QS", userlocal.username,path,id,1024);//65497
            Byte byteid=new Byte((byte) id);
            dataQueue.addtask(byteid,path);
            byte[] b0=new byte[]{0};
            byte[] bytes=Utils.byteMerger(b0,data.getBytes());
            DatagramPacket datagramPacket=new DatagramPacket(bytes,bytes.length,udpData.userRequest.toaddress,udpData.userRequest.toport);
            udpData.dataSocket.send(datagramPacket);
            DataRequest dataRequest= Tool.URtoDR(userRequest,path,id,1024);
            //liveBind.stoplive();
            DataFactory.dataExecutor.execute(DataFactory.getSend(dataRequest));
        }
    }



    public static void tcpProxy(UserRequest userRequest,int port ,int pp){
        String s="TP"+port+"&"+pp;
        SThread.start(SThread.create(()->{
            try {
                QSContrain qsContrain=QSContrain.createQs(userRequest.username);
            } catch (Exception e) {
                e.printStackTrace();
            }
            UserContext userContext= null;
            try {
                userContext = mainDataQueue.getUserContext(userRequest.username);
            } catch (Exception e) {
                e.printStackTrace();
            }
            byte[] bytes= Utils.byteMerger(Utils.getUseridByte(userContext.getBothId(), (short) 0),s.getBytes());

            TcpProxyClient tcpProxyClient=TcpProxyFactory.getTcpProxyClient(userRequest.username,port);
            Senders.Sends(user.address,user.port,bytes);
            try {
                tcpProxyClient.run();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }));
    }

    public static void ProxyTcpPort(String username,int port ,int pp){
        ProxySocket proxySocket=new ProxySocket(port);
        UserContext userContext= null;
        try {
            userContext = mainDataQueue.getUserContext(username);
        } catch (Exception exception) {
            exception.printStackTrace();
            return;
        }
        short id= userContext.newQueue();
        try {
            proxySocket.createDataStream(username,id);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        if (!proxySocket.bulid(pp)){;}

        proxySocket.run();
        System.out.println("Create Succeed");
    }



    public static void starCloud() {
        System.out.println("please enter listening interval");
        long time= Long.parseLong(Utils.sanc());
        new Thread(()->{
            try {
                CloudLocal.init(time);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void starCloud(int defaut) {
        new Thread(()->{
            try {
                CloudLocal.init(defaut);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static String filechooser(){
        JFileChooser chooser=new JFileChooser();
        chooser.setSize(800,1200);
        chooser.setCurrentDirectory(new File("."));
        int result =chooser.showOpenDialog(null);
        String path=null;
        if(result== JFileChooser.APPROVE_OPTION) {
             path=chooser.getSelectedFile().getPath();
             return path;
        }else {
            return null;
        }
    }



}
