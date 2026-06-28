package com.example.myapplication2.client;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication2.MainActivity;
import com.example.myapplication2.MyBackgroundService;
import com.example.myapplication2.MyForeGroundService;
import com.example.myapplication2.R;
import com.example.myapplication2.client.scan.DexWebScan;
import com.example.myapplication2.ui.slideshow.showPicQR;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.util.List;

import superlink.filemanage.classprocess.AutoScan;
import superlink.filemanage.xmltool.XmlParser;
import superlink.init.InitClass;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.recives.MainDataQueue;
import superlink.udpbind.client.recives.MainReciverques;
import superlink.udpbind.client.recives.recor.Bindrec;
import superlink.udpbind.cloude.DataCloud;
import superlink.udpbind.farme.ShowQr;
import superlink.util.ImageTool.ImageUtils;
import superlink.util.Utils;

public class AndriodInitClass {

    public static boolean start0=false;
    public volatile static InitClass initClass;
    public void initStart(Context context) throws PackageManager.NameNotFoundException, IOException, InterruptedException {
        AndriodInitClass.initClass=new InitClass();
        initClass.init();
        InputStream inputStream=context.getResources().openRawResource(R.raw.webui);
//        InitClass.absolute="/data/user/0/com.example.myapplication2/files/data";
        //   " /storage/emulated/0"
        String fileDir = context.getFilesDir().getAbsolutePath();
        ///data/user/0/com.example.myapplication2/files
        String cacheDir = context.getCacheDir().getAbsolutePath();
        String externalCache=context.getExternalCacheDir().getAbsolutePath();
//        对应外部存储路径:/storage/emulated/0/Android/data/packagename/cache
        String externalfileDir=context.getExternalFilesDir("").getAbsolutePath();
        //        对应外部存储路径:/storage/emulated/0/Android/data/packagename/files
        String externalfileDirlog=context.getExternalFilesDir("logs").getAbsolutePath();
        ///storage/emulated/0/Android/data/com.example.myapplication2/files/logs
        String externalStorageDirectory=Environment.getExternalStorageDirectory().getAbsolutePath();
//        对应外部存储路径:/storage/emulated/0
//
        String externalStoragePublicDirectory=Environment.getExternalStoragePublicDirectory("").getAbsolutePath();
//        获取外部存储的共享文件夹路径如：/storage/emulated/0

        String externalStoragePublicDcimDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath();
//        以上便是获取相机DCIM目录，对应获取的路径为:/storage/emulated/0/DCIM。
        //        系统存储目录
        String RootDirectory=Environment.getRootDirectory().getAbsolutePath();
//        对应获取系统分区根路径:/system
//
        String dataDirectory=Environment.getDataDirectory().getAbsolutePath();
//        对应获取用户数据目录路径:/data
//
        String DownloadDirectory=Environment.getDownloadCacheDirectory().getAbsolutePath();
//        对应获取用户缓存目录路径:/cache
        System.out.println(Environment.getExternalStorageDirectory().getAbsolutePath());
        System.out.println(context.getFilesDir().getPath());
        String p3= FileSystems.getDefault().toString();
        String p1=Environment.getExternalStorageDirectory().getAbsolutePath();
        InitClass.rootpaths=new String[]{p1};
        String p2="/data/user/0/com.example.myapplication2/files/";
        p1= context.getFilesDir().getAbsolutePath();
        File fc=new File(p1+"/superlink");
        fc.mkdirs();

        InitClass.webpath=p1+"/superlink/web/webui/android/";
        File filez=new File(p1+"/superlink/web/webui/android/zip");
        if (!filez.exists()){
            Utils.unZip(p1+"/superlink/web",inputStream);
            filez.createNewFile();
        }

//        initClass.setAbsolute(Environment.getExternalStorageDirectory().getAbsolutePath());
//        List<String> list=new ArrayList<>();
//        FileScan.scanPackage(new File("/superlink"),list,"");
//        for (String s:list){
//            System.out.println(s);
//        }
        FileSystems.getDefault().newWatchService();
        initClass.setAbsolute(p1+"/superlink/");
        String[] strings=new String[3];

        Bindrec.overTimes=42;
        DataCloud.c=false;
        strings[0]=p1+"/superlink";
        strings[1]=Environment.getExternalStorageDirectory().getAbsolutePath()+"/superlink";
        strings[2]=Environment.getExternalStorageDirectory().getAbsolutePath();
        InitClass.rootpaths=strings;
        File[] rootFiles=new File[3];
        rootFiles[0]=new File(p1+"/superlink/");
        rootFiles[1]=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/superlink/");
        rootFiles[2]=new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        InitClass.roots=rootFiles;
        InitClass.webpath=p1+"/superlink/web/webui/android/";
//        XmlParser.cloudecache=Environment.getExternalStorageDirectory().getAbsolutePath()+"/superlink/cloudecahe/";
//        XmlParser.showpath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/superlink/show/";
        XmlParser.cloudecache=p1+"/superlink/cloudecahe/";
        XmlParser.showpath=p1+"/superlink/show/";
        XmlParser.extend=Environment.getExternalStorageDirectory().getAbsolutePath()+"/superlink/extend/";
        File extend=new File(XmlParser.extend);
        if (!extend.exists()){
            extend.mkdirs();
        }

        //"/data/user/0/com.example.myapplication2/files/"
        ShowQr.gren=new showPicQR();
        showPicQR.view=new View(context);
        showPicQR.mainActivity= (AppCompatActivity) context;

        ImageUtils.imageProider=new com.example.myapplication2.client.utils.ImageUtils();

        AutoScan autoScan=new AutoScan();
        List<Class<?>> classList= DexWebScan.getFileNameByPackageName(context,context.getPackageName());
        autoScan.autoScanWeb(classList);
        initClass.startNetty();
        AutoScan.classLoader=context.getClassLoader();

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        MyBackgroundService.pendingIntent=pendingIntent;
        UDPclient.mainDataQueue = new MainDataQueue();
//把任务添加到主线程执行，配合安卓的要求，无特殊意义
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                ServiceCon serviceCon=new ServiceCon();
                serviceCon.runnable=()->{
                    MainReciverques m=UDPclient.mainDataQueue.startMainQue(false);
                    m.setMode(true);
                    m.run();
                };
                serviceCon.channelId="2";
                serviceCon.serviceId=2;
//                MyBackgroundService.runnable=serviceCon.runnable;
                MyBackgroundService.conQeque.add(serviceCon);
                Intent serviceIntent=new Intent(context, MyBackgroundService.class);

                context.startService(serviceIntent);
            }
        });
//        UDPclient.userlocal.inaddress = Utils.getLocalIpv4();
        initClass.startChoose().startClientlow();

//        MyBackgroundService.MyBackgroundService(()->{initClass.startClient();});

//        context.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                startService(serviceIntent);
//            }
//        });


        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                ServiceCon serviceCon=new ServiceCon();
//                MyBackgroundService.MyBackgroundService(()->{
//                    BindFactory.mode=true;
//                    BindFactory.check();
//                    });
                serviceCon.runnable=()->{
                    initClass.startBindresCheaklow();
                };
                serviceCon.channelId="1";
                serviceCon.serviceId=1;
//                MyBackgroundService.runnable=serviceCon.runnable;
//                MyBackgroundService.conQeque.add(serviceCon);
                MyForeGroundService.runnable = serviceCon.runnable;
                Intent serviceIntent=new Intent(context, MyForeGroundService.class);

                context.startService(serviceIntent);
            }
        });

//        new Handler(Looper.getMainLooper()).post(new Runnable() {
//            @Override
//            public void run() {
//                ServiceCon serviceCon=new ServiceCon();
////                MyBackgroundService.MyBackgroundService(()->{
////                    BindFactory.mode=true;
////                    BindFactory.check();
////                    });
//                serviceCon.runnable=()->{
//                    LiveHandle liveBind=new LiveHandle(true);
//                    superlink.udpbind.handle.Handler.DispectMap.put("LiveBind",liveBind);
//                    liveBind.run();
//                };
//                serviceCon.channelId="4";
//                serviceCon.serviceId=4;
//                MyBackgroundService.conQeque.add(serviceCon);
//                Intent serviceIntent=new Intent(context, MyBackgroundService.class);
//
//                context.startService(serviceIntent);
//            }
//        });
//        new Handler(Looper.getMainLooper()).post(new Runnable() {
//            @Override
//            public void run() {
//                ServiceCon serviceCon=new ServiceCon();
////                MyBackgroundService.MyBackgroundService(()->{
////                    BindFactory.mode=true;
////                    BindFactory.check();
////                    });
//                serviceCon.runnable=()->{
//                    UDPclient.userlocal.username = initClass.username;
//                    UDPclient.userlocal.nickName = UserGet.user.attribute("label").getValue();
//                    UDPclient.userlocal.inaddress = initClass.address;
//                    initClass.udPclient.blockingQueue=UDPclient.mainDataQueue.getQueueServer("server").get((short)0);
//                    initClass.udPclient.client(true);
//                };
//                serviceCon.channelId="2";
//                serviceCon.serviceId=2;
//                MyBackgroundService.conQeque.add(serviceCon);
//                Intent serviceIntent=new Intent(context, MyBackgroundService.class);
//
//                context.startService(serviceIntent);
//            }
//        });



//        new Handler(Looper.getMainLooper()).post(new Runnable() {
//            @Override
//            public void run() {
//                MyBackgroundService.serviceId=3;
//                MyBackgroundService.channelId="3";
//                MyForeGroundService.runnable=()->{
//                    initClass.startClient();
//                };
//                Intent serviceIntent=new Intent(context, MyForeGroundService.class);
//
//                context.startService(serviceIntent);
//            }
//        });
//        Intent serviceIntent=new Intent(context, MyBackgroundService.class);

//        context.startService(serviceIntent);
//        initClass.startClient();
        start0=true;
    }


}
