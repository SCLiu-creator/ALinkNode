package superlink.init;

import com.alibaba.fastjson2.JSON;
import superlink.filemanage.classprocess.AutoScan;
import superlink.filemanage.xmltool.UserGet;
import superlink.httpserver.HttpServer;
import superlink.httpserver.HttpServlet;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.recives.MainDataQueue;
import superlink.udpbind.client.recives.ServerQueue;
import superlink.udpbind.client.recives.recor.BindFactory;
import superlink.udpbind.farme.*;
import superlink.udpbind.handle.Handler;
import superlink.udpbind.handle.LiveHandle;
import superlink.util.JackJson;
import superlink.util.Tool;
import superlink.util.Utils;
import superlink.util.thread.SThreadPool1;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;

import static javafx.application.Application.launch;
import static superlink.udpbind.client.UDPclient.overHook;

public class InitClass {
    public static InitClass initClass;
    public static String webpath="";
    public static String absolute="";
    public static String path="";
    public static File[] roots;
    public static String[] rootpaths;
    public static int ThreadMode=0;
    public static String[] getRootPaths(){
        File[] files = File.listRoots();
        roots = Tool.mergeAndDeduplicate(roots,files,new File[0]);
        ArrayList<String> strings=new ArrayList(roots.length);
        for (int i=0;i<roots.length;i++){
            try {
                strings.add(roots[i].getAbsolutePath());
            }catch (Exception e){

            }
        }
        rootpaths = strings.toArray(new String[0]);
        return rootpaths;
    };
    public static boolean ipv=true;
    public static HttpServlet httpServlet;

    public UDPclient udPclient;
    public String username;
    public InetAddress address ;
    public InetAddress ipv4;
    public InetAddress ipv6;
    public void defaultInit(){
        init().startScanWeb().startNetty().startNpmView().showQr().startDataQueue().startChoose().startBindresCheak().startClient();
    }
    public void lowInit(){
        init().startScanWeb().startNetty().startNpmView().showQr().startDataQueue().startChoose().startClientlow().startBindresCheaklow();
    }
    public InitClass init(){
        InitClass.initClass=this;
        new Handler();
        overHook();
        if (Utils.getOs()==1){
            WindowDemo2.b=false;
            WindowDemo2 w=new WindowDemo2("windows");
        }


       // 1.定义服务器的地址、端口号、数据
        try {
//            address = InetAddress.getByName("localhost");
            ipv4=Utils.getLocalIpv4();
            String ts= JackJson.toJson(ipv4);
            ipv6=Utils.getLocalIpv6();
            if (ipv){
                if (ipv4!=null){
                    address=ipv4;
                }else {
                    address=InetAddress.getByName("127.0.0.1");
                }
            }else {
                if (ipv6!=null){
                    address=ipv6;
                }else {
                    address=InetAddress.getByName("localhost");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
//        System.out.println("输入端口：");
//        Scanner scanner = new Scanner(System.in);//从键盘接受数据
//        String getport = scanner.nextLine();//nextLine方式接受字符串
//        Integer port=Integer.valueOf(getport);
        String ts= JSON.toJSONString(address);
        udPclient = new UDPclient(address);

//        new AutoScan().startscan(AutoScan.scanPackage(AutoScan.url));
//        new Thread(new HttpServlet(UDPclient.userlocal.inport)).start();//userlocal.inport
//        Tool.changeport(UDPclient.userlocal.inport);
//        try {
//            ProcessBuilder processBuilder = new ProcessBuilder();
//            processBuilder.command("cmd", "/c", "npm start ").
//                    directory(new File("web/electron-quick-start"));
//            processBuilder.start();
////            Runtime.getRuntime().exec("web/electron-quick-start/");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        username=new UserGet().chooseUser();
//        UDPclient.userlocal.username = username;
//        UDPclient.userlocal.inaddress = address;
//        System.out.println(username);
//        UDPclient.mainDataQueue=new MainDataQueue(UDPclient.userlocal.username);
//        udPclient.blockingQueue=UDPclient.mainDataQueue.getQueueServer("server").get(0);
//        LiveHandle liveBind=new LiveHandle();
//        Handler.DispectMap.put("LiveBind",liveBind);
//        UDPclient.executorService.execute(liveBind);
//        udPclient.client();
        return this;
    }
    //不要输入反斜杠路径
    public InitClass setAbsolute(String path){
        absolute=path;
        return this;
    }
    public InitClass startScanWeb(){
        AutoScan autoScan = new AutoScan();
        java.util.List<Class<?>> classList = AutoScan.scanPackage(AutoScan.url);
        autoScan.autoScanWeb(classList);
        return this;
    }
    public InitClass startNetty(){
        httpServlet=new HttpServlet(UDPclient.userlocal.inport,4);
        new Thread(httpServlet).start();//userlocal.inport
        return this;
    }
    public InitClass showQr(){
        ShowQr.gren=new ShowQrWin();
        return this;
    }
    public InitClass startNpmView(){
//        new jfx(address,UDPclient.userlocal.inport);
//        launch();
//        Tool.changeport(UDPclient.userlocal.inport);
        Tool.changeport(HttpServlet.port);
        try {
//            ProcessBuilder processBuilder = new ProcessBuilder();

            File projectRoot = new File(System.getProperty("user.dir"));
            File nodeExe = new File(projectRoot, "web/node-v24.14.0-win-x64/node.exe");
            File electronMain = new File(projectRoot, "web/electron-quick-start/node_modules/electron/cli.js");

            ProcessBuilder processBuilder = new ProcessBuilder(
                    nodeExe.getAbsolutePath(),
                    electronMain.getAbsolutePath(),
                    "."
            );
            processBuilder.directory(new File(projectRoot, "web/electron-quick-start"));
            processBuilder.redirectErrorStream(true); // 将错误流合并到标准输出流
            Process process = processBuilder.start();

// 读取输出流以查看日志
            new Thread(()->{
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (Exception e) {
                e.printStackTrace();
            }
            }).start();

//            Runtime.getRuntime().exec("web/electron-quick-start/");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }
    public InitClass startChoose(){
        username=new UserGet().chooseUser();
        UserLinkCon.init();
        return this;
    }
    public InitClass startChoose1(){
        username=new UserGet().chooseUser1();
        return this;
    }
    public InitClass startChoose2(){
        username=new UserGet().chooseUser2();
        return this;
    }
    public InitClass startBindresCheak(){
        BindFactory.mode=true;
        SThreadPool1.execute(()->{BindFactory.check();return null;});
        return this;
    }
    public InitClass startBindresCheaklow(){
        BindFactory.setMode(true);
        if(ThreadMode==1){
            BindFactory.checkOne();
        }else {
            BindFactory.checkLow();
        }
//        BindFactory.checkAll();
        return this;
    }
    public InitClass startDataQueue(){
        if (UDPclient.mainDataQueue==null){
            UDPclient.mainDataQueue=new MainDataQueue();
        }else {
            UDPclient.mainDataQueue.reSet();
        }
        UDPclient.mainDataQueue.startMainQue(true);
        return this;
    }
    public void startClient(){
        UDPclient.userlocal.username = username;
        UDPclient.userlocal.nickName = UserGet.user.attribute("label").getValue();
        UDPclient.userlocal.inaddress = address;
        System.out.println(username);
        udPclient.serverQueue = (ServerQueue) UDPclient.mainDataQueue.getQueServer().get((short)0);
        LiveHandle liveBind=new LiveHandle(true);
        Handler.DispectMap.put("LiveBind",liveBind);
        UDPclient.executorService.execute(liveBind);
        udPclient.client(true);
    }

    public InitClass startClientlow(){
        UDPclient.userlocal.username = username;
        UDPclient.userlocal.nickName = UserGet.user.attribute("label").getValue();
        UDPclient.userlocal.inaddress = address;
        System.out.println(username);
        udPclient.serverQueue = (ServerQueue) UDPclient.mainDataQueue.getQueServer().get((short)0);
        LiveHandle liveBind=new LiveHandle(false);
        Handler.DispectMap.put("LiveBind",liveBind);
        udPclient.client(false);
        return this;
    }


    static {
        File file=new File("");
        absolute=file.getAbsolutePath().replace("\\","/")+"/";
    }
    static {
        File file=new File("");
        //android
        webpath=file.getAbsolutePath().replace("\\","/")+"/web/webui/android/";
    }
    static {
        File file=new File("");
        path=file.getAbsolutePath().replace("\\","/");
    }
    static {
        roots = File.listRoots();
        String[] strings=new String[roots.length];
        for (int i=0;i<strings.length;i++){
            strings[i]=roots[i].getAbsolutePath();
        }
        rootpaths=strings;
    }
}
