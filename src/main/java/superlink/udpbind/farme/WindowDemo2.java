package superlink.udpbind.farme;


import com.alibaba.fastjson2.JSON;
import superlink.filemanage.xmltool.XmlParser;
import superlink.tcpbind.choose.TcpServerBind;
import superlink.udpbind.client.recives.Senders;
import superlink.udpbind.client.recives.data.blockBuffer.ByteStream;
import superlink.udpbind.tcpproxy.ProxySocket;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.data.datastream.DataStreamAB;
import superlink.udpbind.client.recives.data.stream1.QueueStream;
import superlink.udpbind.client.recives.datalen.DataReqAuto;
import superlink.udpbind.client.recives.datalen.DataSmall;
import superlink.udpbind.cloude.CloudLocal;
import superlink.udpbind.dataLink.UdpData;
import superlink.udpbind.dataLink.LiveBinds;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.controller.Controller;
import superlink.udpbind.handle.Handler;
import superlink.udpbind.usedata.DataRequest;
import superlink.udpbind.usedata.User;
import superlink.udpbind.usedata.UserRequest;
import superlink.util.Utils;
import superlink.util.mapThreadPool.FactoryDataSmall;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.*;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import static superlink.filemanage.scanpackage.FileScan.createXmls;
import static superlink.filemanage.xmltool.UserGet.setDefaultUser;
import static superlink.filemanage.xmltool.UserGet.unsetDefaultUser;
import static superlink.filemanage.xmltool.XmlCreate.createUserXml;
import static superlink.filemanage.xmltool.XmlCreate.createcloudeXml;
import static superlink.udpbind.client.UDPclient.*;
import static superlink.udpbind.controller.Controller.*;
import static superlink.util.Utils.chooseFilepath;


public class WindowDemo2 {
    public static JFrame frame;
    public static boolean b=false;
    public static JScrollPane scrollPane;
    public static Object[] lstatic;
    static JList liststatic ; // 创建一个列表，并将字符串数组作为参数传递给它
    JFileChooser chooser;
    JsonArray array;
    String jsons;
    JPanel panell;
    public WindowDemo2(String name){
        frame = new JFrame("Window Demo");
        Handler.DispectMap.put(name,this);
    }

    public static User user=new User();
    public static UserRequest userRequest=new UserRequest();


    public void yest(String json,Object[] l) {
        chooser=new JFileChooser();//创建选择窗体
        chooser.setSize(800,1200);
        chooser.setCurrentDirectory(new File("."));
        jsons=json;
        lstatic=l;
        liststatic = new JList(lstatic);
        scrollPane = new JScrollPane(liststatic); // 创建一个滚动面板，并将列表作为它的客户端
        //String json = "[\"apple\", \"banana\", \"orange\"]"; // 定义一个json字符串
        JsonReader reader = Json.createReader(new StringReader(json)); // 创建一个JsonReader对象
        array = reader.readArray(); // 将json字符串转换为JsonArray对象

//        Object[] l=array.toArray();
//        System.out.println(array.toArray());
//        reader.close(); // 关闭JsonReader对象
//        String[] data = new String[array.size()]; // 创建一个字符串数组
//        for (int i = 0; i < array.size(); i++) {
//            data[i] = array.getString(i); // 将JsonArray中的每个元素赋值给字符串数组中对应的位置
//        }

       // frame.setSize(0, 600); // 设置窗口的大小
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 设置窗口关闭时的操作
        //添加页面
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setPreferredSize(new Dimension(430, 340));
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.setTabPlacement(JTabbedPane.LEFT);
        frame.add(tabbedPane);
        frame.setContentPane(tabbedPane);
        JPanel panel = new JPanel(); // 创建一个面板
        Color color=new Color(128,128,128);
        panel.setBackground(/**color**/color); // 设置面板的背景颜色
        frame.add(panel, BorderLayout.EAST); // 将面板添加到窗口的东部位置

        // 创建一个JPanel对象，并设置其布局为BoxLayout
         panell = new JPanel();
        JPanel panell1 = new JPanel();
        JPanel panell2 = new JPanel();
        panell.setLayout(new GridLayout(1,2,5,5));
       // panell1.setLayout(new BoxLayout(panell2, BoxLayout.Y_AXIS));
       // panell.setLayout(new BoxLayout(panell1,BoxLayout.X_AXIS));
        // 创建若干个JButton对象，并添加到JPanel对象中
        scrollPane.setSize(400,600);
        //panell.setLayout(new BoxLayout(scrollPane,BoxLayout.X_AXIS));
        setButton(panell2);
        panell.add(scrollPane);
        panell.add(panell2);

        // 创建一个JLabel对象，并设置其文本为你想要显示的内容
        JLabel label = new JLabel("This is a label");
        label.setIcon(  new ImageIcon("C:\\Users\\liusc\\Pictures\\Screenshots\\屏幕截图(1).png"));
        // 将JLabel对象添加到窗体的左上角，例如使用BorderLayout.NORTH
        frame.add(label);//, BorderLayout.NORTH);
        // 将JPanel对象添加到窗体的左侧，例如使用BorderLayout.WEST

        frame.add(panell, BorderLayout.WEST);

//        EmbeddedMediaPlayerComponent
        frame.setVisible(true); // 设置窗口可见
        JList list = liststatic; // 创建一个列表，并将字符串数组作为参数传递给它

       // frame.add(scrollPane, BorderLayout.EAST); // 将滚动面板添加到窗口的东部位置，而不是直接添加面板

        JButton button = new JButton("Confirm"); // 创建一个按钮，并设置它的文本
        frame.add(button, BorderLayout.SOUTH); // 将按钮添加到窗口的南部位置
        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) { // 如果选择已经确定
                int index = list.getSelectedIndex(); // 获取选中项的索引
//                String s=(String) list.getSelectedValue();
                if (index<array.toArray().length-1){
                    return;
                }
                String bt= array.toArray()[index].toString();
                System.out.println("You selected: " +"   "+bt); // 打印选中项的内容
                user = JSON.parseObject(bt,User.class);
                setUserquest(user);
                userRequest.requestaddress= userlocal.address;
                userRequest.requestport= userlocal.port;
                userRequest.toport=user.port;
                userRequest.toaddress=user.address;
                userRequest.username=user.username;
                userRequest.inaddress= userlocal.inaddress;
                userRequest.inport= userlocal.inport;
            }
        });

        button.addActionListener(e -> {
            System.out.println("You clicked confirm."); // 打印点击确认按钮的信息
            String s="ex"+ userlocal.toString();;
            Senders.ServerSends(s.getBytes());
            System.exit(0); // 退出程序
        });

        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
               // frame.remove(label);
                ImageIcon imageIcon=new ImageIcon(new ImageIcon("C:\\\\Users\\\\liusc\\\\Pictures\\\\Screenshots\\\\屏幕截图(1).png")
                        .getImage().getScaledInstance(frame.getWidth(),frame.getHeight(),Image.SCALE_DEFAULT));
                label.setIcon(imageIcon);
                label.setHorizontalAlignment(SwingConstants.CENTER);
              //  label.repaint();
                //frame.add(label);
            }
        });

        frame.setSize(1200,600);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// 设置窗口关闭时的操作
        frame.setVisible(true);
        b=!b;

//        frame.remove(scrollPane);
//        scrollPane.revalidate();
//        liststatic.revalidate();
//        frame.add(scrollPane, BorderLayout.EAST);
//        frame.revalidate();

    }
    public void reset(String json,Object[] l){
        lstatic=l;
        //liststatic.setListData(l);
        //frame.remove(scrollPane);
        panell.remove(scrollPane);
        scrollPane.remove(liststatic);
        liststatic = new JList(lstatic);
        JsonReader reader = Json.createReader(new StringReader(json)); // 创建一个JsonReader对象
        array = reader.readArray(); // 将json字符串转换为JsonArray对象

        liststatic.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) { // 如果选择已经确定
                int index = liststatic.getSelectedIndex(); // 获取选中项的索引
//                String s=(String) list.getSelectedValue();
                if (index>array.toArray().length-1){
                    return;
                }
                String bt= array.toArray()[index].toString();
                System.out.println("You selected: " +"   "+bt); // 打印选中项的内容
                user = JSON.parseObject(bt,User.class);
                setUserquest(user);
                userRequest.requestaddress= userlocal.address;
                userRequest.requestport= userlocal.port;
                userRequest.toport=user.port;
                userRequest.toaddress=user.address;
                userRequest.username=user.username;
                userRequest.inaddress= userlocal.inaddress;
                userRequest.inport= userlocal.inport;
            }
        });
//        JsonReader reader = Json.createReader(new StringReader(jsons.substring(0,jsons.length()-1)+","+json.substring(1))); // 创建一个JsonReader对象
//        array = reader.readArray(); // 将json字符串转换为JsonArray对象
        scrollPane.updateUI();
        scrollPane=new JScrollPane(liststatic);
        scrollPane.repaint();

        scrollPane.setViewportView(liststatic);
        //scrollPane.revalidate();
//        liststatic.revalidate();
        panell.add(scrollPane,BoxLayout.X_AXIS);
        panell.repaint();
        //frame.add(scrollPane, BorderLayout.EAST);
        frame.revalidate();
        frame.repaint();
    }

    public void setButton(JPanel panell){
        JButton button1 = new JButton("请求节点");
        JButton button01 = new JButton("请求节点i");
        JButton button001 = new JButton("请求节点r");
        JButton button2 = new JButton("刷新列表");
        JButton button3 = new JButton("快速数据连接");
        JButton button4 = new JButton("稳定数据连接");
        JButton button5 = new JButton("udp数据传输");
        JButton button6 = new JButton("定时");
        JButton button7 = new JButton("结束程序");
        JButton button8 = new JButton("结束server");
        JButton button9 = new JButton("Large");
        JButton button10=new JButton("quesend");
        JButton button11=new JButton("quesends");
        JButton button12=new JButton("reqFile");
        JButton button13=new JButton("ReqUserPage");
        JButton button14=new JButton("creatXml");
        JButton button15=new JButton("creatUserpage");
        JButton button16=new JButton("read");
        JButton button17=new JButton("reqCloudxml");
        JButton button18=new JButton("BothCloudxml");
        JButton button19=new JButton("CloudeON");
        JButton button20=new JButton("CloudeOFF");
        JButton button201=new JButton("DateByte");
        JButton button202=new JButton("DefaultUser");
        JButton button203=new JButton("unDefaultUser");
        JButton button204=new JButton("TcpProxy");
        JButton button205=new JButton("DataStream");
        JButton button206=new JButton("ProxySocket");
        JButton button207=new JButton("print All Data");
        JButton button208=new JButton("print All Data to Flie");
        JButton button209=new JButton("Data Flie");
        panell.add(button1);
        panell.add(button01);
        panell.add(button001);
        panell.add(button2);
        panell.add(button3);
        panell.add(button4);
        panell.add(button5);
        panell.add(button6);
        panell.add(button7);
        panell.add(button8);
        panell.add(button9);
        panell.add(button10);
        panell.add(button11);
        panell.add(button12);
        panell.add(button13);
        panell.add(button14);
        panell.add(button15);
        panell.add(button16);
        panell.add(button17);
        panell.add(button18);
        panell.add(button19);
        panell.add(button20);
        panell.add(button201);
        panell.add(button202);
        panell.add(button203);
        panell.add(button204);
        panell.add(button205);
        panell.add(button206);
        panell.add(button207);
        panell.add(button208);
        panell.add(button209);
        new HurdlerTimer(button6).start();
        button209.addActionListener(e->{
            String requestserver =chooseFilepath();
            DataRequest dataRequest=new DataRequest();
            dataRequest.pl=1440;
            dataRequest.filename=requestserver;
            UserContext userContext=UDPclient.mainDataQueue.getUserContext(userRequest.username);
            short id=userContext.newQueue();
            ByteStream d=new ByteStream(userContext,id);
            File file=new File(requestserver.substring(requestserver.indexOf("\\")));
            try {
                d.reqFile(dataRequest,new FileOutputStream(file));
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
        });
        button208.addActionListener(e->{
            String requestserver ="ot";//请求服务器和和对方主机
            byte[] bytes= requestserver.getBytes();

            Senders.ServerSends(bytes);
        });
        button207.addActionListener(e->{
            String requestserver ="PA";//请求服务器和和对方主机
            byte[] bytes= requestserver.getBytes();
            Senders.ServerSends(bytes);
        });
        button206.addActionListener(e->{
            ProxySocket proxySocket=new ProxySocket(7527);
            UserContext userContext= null;
            try {
                userContext = mainDataQueue.getUserContext(userRequest.username);
            } catch (Exception exception) {
                exception.printStackTrace();
                return;
            }
            short id= (short) userContext.newQueue();
            try {
                proxySocket.createDataStream(userRequest.username,id);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            if (!proxySocket.bulid(8090)){;}

            proxySocket.run();
            System.out.println("Create Succeed");
        });
        button205.addActionListener(e->{
            DataStreamAB queueStream=DataStreamAB.dataStreamMap.get(userRequest.username);
            if (queueStream==null){
                UserContext userContext= null;
                try {
                    userContext = mainDataQueue.getUserContext(userRequest.username);
                } catch (Exception exception) {
                    exception.printStackTrace();
                    return;
                }
                int id=userContext.newQueue();
                System.out.println("dataStream  +"+userRequest.username+"   "+id);
                try {
                    queueStream=new DataStreamAB(userRequest.username,(short) userContext.newQueue());
                } catch (Exception exception) {
                    exception.printStackTrace();
                    return;
                }
                queueStream.build();
                DataStreamAB finalQueueStream = queueStream;
                AtomicReference<FileOutputStream> inputStream = new AtomicReference<>();
                new Thread(()->{
                    Thread.currentThread().setName("finalQueueStream");
                    try {
                        //C:\\\\Users\\\\liushengchang-n\\\\Desktop\\\\h1.zip
                        File file=new File("C:\\Users\\liusc\\Desktop\\test1.zip");
                        if (!file.exists()){
                            file.createNewFile();
                        }
                         inputStream.set(new FileOutputStream(file));

                        int len=1;
                        byte[] bytes1;
                        int i=0;
                        while (len!=0){

                            i++;
                            bytes1=finalQueueStream.read0();
//                            bytes1=Arrays.copyOfRange(bytes1,0,bytes1.length);
                            len=bytes1.length;
                            if (len==3){
                                break;
                            }
                            inputStream.get().write(Arrays.copyOfRange(bytes1,3,bytes1.length));
                        }

                        System.out.println("over    "+i);
//                        finalQueueStream.finalize();
                        inputStream.get().close();
                    } catch (Exception ee) {
                        ee.printStackTrace();
                    }finally {
                        try {
                            inputStream.get().close();
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                    System.out.println("over");
                }).start();
            }

            System.out.println("Create Succeed");
        });
        button204.addActionListener(e->{
//            System.out.println("scanc");
//            String sanc=Utils.sanc();
//            Integer port=Integer.parseInt(sanc);
//            System.out.println("scanc");
//            String sanc1=Utils.sanc();
//            Integer pp=Integer.parseInt(sanc1);

            int port=8888;
            int pp=userRequest.inport;
            Controller.tcpProxy(userRequest,port,pp);
        });
        button203.addActionListener(e->{
            unsetDefaultUser();
        });
        button202.addActionListener(e->{
            setDefaultUser();
        });
        button201.addActionListener(e -> {

            new Thread(()->{
                Thread.currentThread().setName("DataAuto");
//                UserContext userContext= mainDataQueue.getUserContext(userRequest.username);

                DataSmall dataByte= null;
                try {
                    dataByte = FactoryDataSmall.getDataSmall(userRequest.username);
                } catch (Exception exception) {
                    exception.printStackTrace();
                    return;
                }
                String s=FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath()+"\\t1.zip";
                String fn=Utils.chooseFile();
                s=XmlParser.cachepath+"aaa.s";
                //dataByte.r(s);
                FileOutputStream fileOutputStream=null;
                Object rev=dataByte.reqFile(fn);
                DataReqAuto.writdata(s,rev);

            }).start();

        });

        button20.addActionListener(e -> {
            System.out.println("OVER Cloud");
            if (CloudLocal.isInitSynContainer()){
                CloudLocal.getSynContainer().finalize();
            }
        });

        button19.addActionListener(e -> {

            starCloud();
        });


        button18.addActionListener(e -> {

                ReqCloudePageMirror(userRequest);
        });

        button17.addActionListener(e -> {

                ReqCloudePage(userRequest.username);
        });


        button16.addActionListener(e -> {

            QueueStream queueStream=QueueStream.map.get(userRequest.username);
            if (queueStream==null){
                try {
                    queueStream=new QueueStream(userRequest.username,QueueStream.defsize);
                } catch (Exception exception) {
                    exception.printStackTrace();
                    return;
                }

                QueueStream finalQueueStream = queueStream;
                new Thread(()->{
                    Thread.currentThread().setName("finalQueueStream");
                    finalQueueStream.build();
                    try {
                        //C:\Users\liusc\Desktop\ed953d6c1d8444ba86c6d829f515eb9c1.jpg
                        File file=new File("C:\\Users\\liushengchang-n\\Desktop\\h1.png");
                        if (!file.exists()){
                            file.createNewFile();
                        }
                        FileOutputStream inputStream=new FileOutputStream(file);

                        int len=1;
                        byte[] bytes1;
                        int i=0;
                        while (len!=0){
                            i++;
                            bytes1=finalQueueStream.synread();
                            bytes1=Arrays.copyOfRange(bytes1,5,bytes1.length);
                            len=bytes1.length;
                            inputStream.write(bytes1);

                        }
                        System.out.println("over");
                    } catch (Exception ee) {
                        ee.printStackTrace();
                    }
                    System.out.println("over");
                }).start();
            }

            System.out.println("Create Succeed");
        });

        button15.addActionListener(e -> {
            try {
                createUserXml();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            System.out.println("Create Succeed");
        });

        button14.addActionListener(e -> {
            try {
                //添加同步文件夹
                createXmls(chooseFilepath());
                createcloudeXml();
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            System.out.println("Create Succeed");
        });

        button13.addActionListener(e -> {
            Controller.ReqUserPage(userRequest);
        });


        // 为每个JButton对象添加一个ActionListener对象，并在actionPerformed方法中编写交互内容
        button1.addActionListener(e -> {
            // ...交互内容
            System.out.println("You clicked Button 1");

            Controller.requestNode(userRequest);
        });
        button01.addActionListener(e -> {
            // ...交互内容
            System.out.println("You clicked Button 01");

            Controller.requestNodeIn(user);
        });
        button001.addActionListener(e -> {
            // ...交互内容
            System.out.println("You clicked Button 01");

            Controller.requestNodeReturn(user);
        });

        button2.addActionListener(e -> {
            // ...交互内容
            System.out.println("You clicked Button 2");
            Controller.upgradeList();

        });

        button3.addActionListener(e -> {
            // ...交互内容
            System.out.println("You clicked Button 3");

            new Thread(){
                @Override
                public void run(){
                    UdpData udpData=new UdpData(userRequest,true);
                    System.out.println("UDPName:"+userRequest.username);
                    Handler.UdpMap.put(userRequest.username,udpData);
                    udpData.run();
                }
            }.start();
        });

        button4.addActionListener(e -> {
            // ...交互内容
            System.out.println("You clicked Button 4");
            new Thread(){
                @Override
                public void run(){
                    new TcpServerBind(userRequest,true).run();
                }
            }.start();

        });

        button6.addActionListener(e -> {
            // ...交互内容
            System.out.println("You clicked Button 4");
            udpDataSends(userRequest);

        });


        button7.addActionListener(e -> {
            // ...交互内容
            try {
                Controller.closeUser(Controller.userRequest);
            } catch (Exception Exception) {
                Exception.printStackTrace();
            }
        });

        button8.addActionListener(e -> {
            Senders.ServerSends("SE".getBytes());
        });
        button9.addActionListener(e->{
            try {
                largeFileSendWait(Controller.userRequest);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        button10.addActionListener(e->{
            try {
                queFileSend(userRequest);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            } catch (Exception ioException) {
                ioException.printStackTrace();
            }
        });
        button11.addActionListener(
                e->{
                    try {
                        queFileSends(userRequest);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                });

    }

    class HurdlerTimer {

        private static final int TIMER_PERIOD = 3000;
        protected static final int MAX_COUNT = 10;
        private JButton welcome; // holds a reference to the Welcome class
        private int count;
        public HurdlerTimer(JButton welcome) {
            this.welcome = welcome; // initializes the reference to the Welcome class.
            String text = "(" + (MAX_COUNT - count) + ") seconds left";
           // welcome.setCountDownLabelText(text);
        }
        public void start() {
            new Timer(TIMER_PERIOD, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (count < MAX_COUNT) {
                        count++;
                        String text = "(" + (MAX_COUNT - count) + ") seconds left";
//                        welcome.setCountDownLabelText(text); // uses the reference to Welcome
                        LiveBinds liveBinds = (LiveBinds) Handler.liveMap.get(userRequest.username);
                        welcome.setBackground(Color.black);
                    } else {
                        ((Timer) e.getSource()).stop();
//                        welcome.showNextPanel();
                    }
                }
            }).start();
        }
    }
    public void setTitle(){

    }

    public void sendSocket(String s){
        byte[] bytes= s.getBytes();
        Senders.ServerSends(bytes);
    }
}