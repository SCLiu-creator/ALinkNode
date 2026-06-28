package superlink.udpbind.client;

import com.alibaba.fastjson2.JSON;
import superlink.filemanage.xmltool.XmlCreate;
import superlink.init.InitClass;
import superlink.udpbind.chat.ChatGroup;
import superlink.udpbind.client.recives.*;
import superlink.udpbind.client.recives.recor.BindFactory;
import superlink.udpbind.client.recives.recor.Bindrec;
import superlink.udpbind.cloude.CloudeListenCaset;
import superlink.udpbind.farme.WindowDemo2;
import superlink.udpbind.servlet.ChooseDeal;
import superlink.udpbind.servlet.LiveNetServer;
import superlink.udpbind.servlet.MainThread;
import superlink.udpbind.usedata.User;
import superlink.util.JackJson;
import superlink.util.Utils;
import superlink.util.prioityThreadPool.*;


import javax.xml.bind.Element;
import java.io.IOException;
import java.net.*;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.UnsupportedAddressTypeException;
import java.util.*;
import java.util.concurrent.*;


/*
 * 客户端
 */
public class UDPclient {//公共类
    public static Map<String, User> bindUser = new HashMap<>();
    public static Map<String, User> serverUser = new HashMap<>();
    public static Map<String, User> userMap = new ConcurrentHashMap<>();
    public static volatile User userlocal = new User();
    public static InetAddress serverip;
    public static int serverport = 8800;
    private static int state = 0;
    public static DatagramChannel socketchannel;
    public static DatagramSocket socket;
    public static MainDataQueue mainDataQueue;
    public ServerQueue serverQueue;

    public static ArrayList<InetAddress> ipv4List=new ArrayList<>(2);
    public static ArrayList<InetAddress> ipv6List=new ArrayList<>(2);
    public static ExecutorService executorService;

    static {
        try {

            ipv6List.add(InetAddress.getByName("2402:4e00:c032:2100:6cd0:15dc:ccf2:0"));
            ipv4List.add(InetAddress.getByName("122.51.51.35"));
            serverip = ipv4List.get(0);//122.51.51.35&127.0.0.1
            //122.51.51.35&127.0.0.1
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public UDPclient(InetAddress address) {//主程序入口
        executorService = new PriorityThreadPoolExecutor(1, 16, 40,100);
        Utils.RandomPort randomPort = new Utils.RandomPort();
        while (true) {
            try {
                int port = randomPort.create();
                userlocal.inport = port;
                if (InitClass.ThreadMode==0){
                    socket = new DatagramSocket(port,InetAddress.getByName("0.0.0.0"));
//                    socket = new DatagramSocket(port,InetAddress.getByName("0.0.0.0"));
                }else {
                    socketchannel = DatagramChannel.open();
                    socketchannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
                    socketchannel.configureBlocking(false);
                    socketchannel.bind(new InetSocketAddress(port));
//                socketchannel.disconnect();
//                socket = socketchannel.socket();
                    socket=new CompatibleDatagramSocket(socketchannel);
                }
                socket.setSoTimeout(3*60*1000);
                socket.setReuseAddress(true);
                socket.setReceiveBufferSize(65537 * 4);
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String ts=JSON.toJSONString(address);
        userlocal.setAddress(address);
//        System.out.println("userlocal.port:"+userlocal.inport);
//        Tool.changeport(userlocal.inport);
//        new AutoScan().startscan(AutoScan.scanPackage(AutoScan.url));
//        new Thread(new HttpServlet(userlocal.inport)).start();//userlocal.inport
//
//        try {
//            ProcessBuilder processBuilder = new ProcessBuilder();
//            processBuilder.command("cmd", "/c", "npm start ").
//                    directory(new File("web/electron-quick-start"));
//            processBuilder.start();
////            Runtime.getRuntime().exec("web/electron-quick-start/");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        String name=new UserGet().chooseUser();
////        Random randomport = new Random();
////        StringBuilder send=new StringBuilder();
////        for (int i=0;i<5;i++){
////            send=send.append((char)(randomport.nextInt(70)+48));
////        }
//        userlocal.username = name;
//        userlocal.inaddress = address;
//        System.out.println(name);
//        mainDataQueue=new MainDataQueue(userlocal.username);
//        blockingQueue=mainDataQueue.getQueueServer("server").get(0);
//        LiveHandle liveBind=new LiveHandle();
//        Handler.DispectMap.put("LiveBind",liveBind);
//        executorService.execute(liveBind);
    }

    public static void bindServer() {
        String send = userlocal.toString();;
        byte[] data = send.getBytes();//将接收到的数据变成字节数组

        if (state == 0) {
            Senders.ServerSends(data);// 4.向服务器端发送数据报
        }
    }
    public static void initSocket(){
        try {
            if(socket!=null){
                try {
                    socket.close();
                } catch (Exception e){ }
            }

            if (InitClass.ThreadMode==0){
                socket = new DatagramSocket(userlocal.inport);
            }else {
                socketchannel = DatagramChannel.open();
                socketchannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
                socketchannel.configureBlocking(false);
                socketchannel.bind(new InetSocketAddress(userlocal.inport));
                socket=new CompatibleDatagramSocket(socketchannel);
            }
//            socketchannel = DatagramChannel.open();
//            socketchannel.bind(new InetSocketAddress(userlocal.inport));
//            socket = socketchannel.socket();
//            userlocal.inport = userlocal.inport;
            socket.setReceiveBufferSize(65537 * 4);
            socket.setSoTimeout(3*60*1000);
            mainDataQueue.reciverques.initSocket(socket);
        } catch (BindException  e) {
            e.printStackTrace();
            try {
                socketchannel.close();
                socket.close();
//                socketchannel = DatagramChannel.open();
//                socketchannel.bind(new InetSocketAddress(userlocal.inport));
//                socket = socketchannel.socket();
//                socket.setReceiveBufferSize(65537 * 4);
//                mainDataQueue.reciverques.datagramSocket=socket;
            } catch (IOException s) {
            e.printStackTrace();
            }
        } catch (UnsupportedAddressTypeException e) {
        System.err.println("Invalid address type (e.g., IPv6 not supported)");
        } catch (ClosedChannelException e) {
            System.err.println("Channel was closed before binding!");
        } catch (SecurityException e) {
            System.err.println("Security manager denied binding permission!");
        } catch (IOException e) {
            System.err.println("Failed to bind: " + e.getMessage());
        }
    }

    public Runnable runnable;

    public UDPclient client(boolean mode) {
        XmlCreate.init();
        ChatGroup.init();
        Thread.currentThread().setName("ChooseDeal");
        if (Utils.getOs() == 1) {
            if (!WindowDemo2.b) {
//                userlocal.choose = 1;
            }
        }

        MainThread mainThread = new MainThread();

        ChooseDeal chooseDeal = new ChooseDeal();

        UserContext self=mainDataQueue.createSelfContext(userlocal.getInaddress(),userlocal.inport);
        self.setBothId(self.getUserId());
        ByteBufer selfdata= self.getQueue((short)0);
        Bindrec bindrec = (Bindrec) BindFactory.selfBindrec(selfdata, self.userName);
        bindrec.time=Long.MAX_VALUE;


        while (mode) {//主线程循环接收
            byte[] rec = null;
            ServerQueue.Node node=serverQueue.reNode;
            try {
                node = serverQueue.pollNode(node);// 2.接收服务器响应的数据
                chooseDeal.setData(node).run();
                String test = new String(rec).replace("\u0000", "");
                test.getBytes();
                //            int len1=packet2.getLength();
                //             System.out.println("testgetdata"+test);
                //            state=1;

//            System.out.println("服务器端响应" + test + "   " + packet2.getPort());
//            String reply = new String(data2, 0, packet2.getLength());//创建字符串对象
                //              System.out.println("服务器端响应"+reply+"   "+packet2.getPort());
                //              int len2=reply.compareTo(test);
                mainThread.Mainhandle(node);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        this.runnable = new Runnable() {
            ServerQueue.Node node;
            ServerQueue.Node buferNode=serverQueue.reNode;
            @Override
            public void run() {
                try {
                    node = serverQueue.pollNode(buferNode);// 2.接收服务器响应的数据
                    chooseDeal.setData(buferNode).run();
                    mainThread.Mainhandle(buferNode);
                } catch (Exception e) {
                    if(node!=null){
                        System.out.println(node);
                    }
                    e.printStackTrace();
                }finally {
                    buferNode = node;
                }
            }
        };
        return this;
    }


    public static UserContext getUser(String username) {

//        userMap.remove(username);
        return mainDataQueue.getUserContext(username);
    }

    public static User delUser1(String username) throws Exception {
        User user=userMap.get(username);
        bindUser.remove(username);
//        if (user!=null){
            return user;
//        }
//        userMap.remove(username);
//        throw new Exception("delUser1:  " + username);
    }

    public static InetAddress getServerip() {
        return serverip;
    }

    public static int getSport() {
        return serverport;
    }
    public static InetAddress getSelfIp() {
        return userlocal.inaddress;
    }

    public static int getSelfPort() {
        return userlocal.port;
    }

    public static void overHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> over()));
    }

    public static void over() {
        System.out.println("You clicked confirm."); // 打印点击确认按钮的信息
        String s = "ex" + userlocal.toString();;
        Senders.ServerSends(s.getBytes());
        MainDataQueue.quemap.forEach((i,userContext)->{
            try {
                Senders.Sends(userContext.getBothId(),0,userContext.inetAddress,userContext.port,
                        "DE".getBytes());
                Senders.Sends(userContext.getBothId(),0,userContext.inetAddress,userContext.port,
                        "DE".getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        if (CloudeListenCaset.cloudeListenCaset != null && CloudeListenCaset.cloudeListenCaset.fileRunner != null) {
            try {
                CloudeListenCaset.cloudeListenCaset.fileRunner.manualStop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        new Thread(() -> System.exit(0)).start();
        // 退出程序
    }

    public static void main(String[] args) {
        byte[] bytes=new byte[]{0,6,48,48};
        int i =Utils.byteArrayToInt(bytes);
        System.out.println(i);
    }
}
