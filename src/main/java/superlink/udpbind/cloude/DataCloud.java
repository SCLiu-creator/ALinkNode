package superlink.udpbind.cloude;


import com.alibaba.fastjson2.JSON;
import superlink.udpbind.cloude.data.ChanlsFactory;
import superlink.udpbind.cloude.data.CloudeChanel;
import superlink.udpbind.cloude.operta.unicast.UseOperta;
import superlink.util.prioityThreadPool.PriorityThreadPoolExecutor;
import superlink.util.Utils;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class DataCloud {
    public CloudeChanel cloudeChanel;
    public volatile List<FileTrigger.TargetFile> targetFiles;
    public static Map<String, Map<FileTrigger.TargetFile,FileTrigger.TargetFile>> setMap;//数据缓冲
    public static ThreadPoolExecutor sendExecutor=new PriorityThreadPoolExecutor(1,2,3, 5);
    //public static ThreadPoolExecutor dataExecutor=new ThreadPoolExecutor(1,3,3, TimeUnit.SECONDS,new LinkedBlockingQueue<>());

    public ReentrantLock Lock=new ReentrantLock();
    public boolean dataCloudState=true;
    public static boolean c = false;
    public DataCloud(String name){
        try {
            this.cloudeChanel=ChanlsFactory.getCL(name);
            this.targetFiles=new ArrayList<>();
            if (c) {
                sendExecutor.execute(new SendFileTarge(this));
            }else {
                sendExecutor=null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public DataCloud(){
        setMap=new ConcurrentHashMap<>();
        this.targetFiles=new ArrayList<>();
        if (c){
            sendExecutor.execute(new SendFileTarge(this));
        }else {
            sendExecutor=null;
        }
    }

    //进入缓冲区
    public void sendque(FileTrigger.TargetFile file){
        synchronized (targetFiles){
            targetFiles.add(file);
        }
    }
    public void sendque(List<FileTrigger.TargetFile> files){
        synchronized (targetFiles){
            targetFiles.addAll(files);
        }
    }
    public void start(){
        synchronized (DataCloud.class){
            DataCloud.class.notifyAll();
        }
    }

    public void removeque(FileTrigger.TargetFile file){
        synchronized (targetFiles){
            targetFiles.remove(file);
        }
    }
    public void removeque(int index){
        synchronized (targetFiles){
            targetFiles.remove(index);
        }
    }
    public void send(byte[] bytes){

    }
    public void send(File filename){

    }
    public DataCloud immediate(){
        if (c){
            synchronized (DataCloud.class){
                DataCloud.class.notifyAll();
            }
        }else {
            run();
        }
        return this;
    }
    public byte[] revice(){
        return cloudeChanel.transmit();
    }

    public class SendFileTarge implements Runnable{
        private DataCloud dataCloud;
        public SendFileTarge(DataCloud dataCloud){
            this.dataCloud=dataCloud;
        }
        @Override
        public void run() {
            System.out.println("DataCloud:  start");
            while (dataCloudState){
                System.out.println("DataCloud: runing");
                Thread.currentThread().setName("DataCloud");
                int setMapSzie=0;
                synchronized (DataCloud.class){
                    List<FileTrigger.TargetFile> Files;
                    synchronized (targetFiles){
                        Files=this.dataCloud.targetFiles;
                        this.dataCloud.targetFiles=new ArrayList<>();
                    }

                    Map<String,CloudBin> Mapbin=CloudLocal.getSynContainer().Mapbin;
                    Mapbin.forEach((user,bin)->{
                        Map<FileTrigger.TargetFile,FileTrigger.TargetFile> set=setMap.get(bin.userContext.userName);
                        if (set==null){
                            set=new ConcurrentHashMap();
                            setMap.put(bin.userContext.userName,set);
                        }
                        //进入发送检验缓冲区
                        for (FileTrigger.TargetFile file:Files){
                            if(file.user==null){
                                set.put(file,file);
                            }else {
                                if (file.user.equals(user)){
                                    set.put(file,file);
                                }
                            }
                        }
//                        set.addAll(dataCloud.targetFiles);
                    });
                    AtomicInteger sz= new AtomicInteger();
                    setMap.forEach((k,v)->{
                        if(v.size()==0){
                            setMap.remove(k);
                            sz.getAndIncrement();
                        }
                        for (FileTrigger.TargetFile file:v.values()){
                            //
                            String CT="CT"+JSON.toJSONString(file);
                            System.out.println(file.getATP());
                            boolean b = Utils.dealsSend(k, CT.getBytes());
                            if (!b){
                                v.remove(file);
                            }
                        }
                    });
//                    dataCloud.targetFiles.clear();
                    if (sz.get() >0){
                        CloudLocal.getSynContainer().saveLocalBin();
                    }

                    UseOperta.setUniSendbuffer.forEach((username,set)->{
                        Iterator<UseOperta.OpertaFile> iterator= set.iterator();
                        while (iterator.hasNext()){
                            UseOperta.OpertaFile file=iterator.next();
                            //
                            String CT="UT"+JSON.toJSONString(file);
                            boolean b = Utils.dealsSend(username, CT.getBytes());
                            if (!b){
                                iterator.remove();
                            }
                        }
                    });


                    try {
                        DataCloud.class.wait();
                    } catch (InterruptedException e) {
                        System.out.println("DataCloud run :  "+e.getMessage());
                    }
                    //dataCloud.Lock.unlock();
                }
            }

        }
    }

    public synchronized void run() {
        System.out.println("DataCloud: runing");
        CloudLocal.getSynContainer().Mapbin.forEach((user,bin)->{
            Map<FileTrigger.TargetFile,FileTrigger.TargetFile> set=setMap.get(bin.userContext.userName);
            if (set==null){
                set=new ConcurrentHashMap();
                setMap.put(bin.userContext.userName,set);
            }
            //进入发送检验缓冲区
            for (FileTrigger.TargetFile file:this.targetFiles){
                set.put(file,file);
            }
        });
        this.targetFiles.clear();
        AtomicInteger sz= new AtomicInteger();
        setMap.forEach((u,v)->{
            if(v.size()==0){
                setMap.remove(u);
                sz.getAndIncrement();
            }
            for (FileTrigger.TargetFile file:v.values()){
                String CT="CT"+JSON.toJSONString(file);
                System.out.println(file.getATP());
                boolean b = Utils.dealsSend(u, CT.getBytes());
                if (!b){
                    v.remove(file);
                }
            }
        });

        if (sz.get() >0){
            CloudLocal.getSynContainer().saveLocalBin();
        }


        UseOperta.setUniSendbuffer.forEach((username,set)->{
            Iterator<UseOperta.OpertaFile> iterator= set.iterator();
            while (iterator.hasNext()){
                UseOperta.OpertaFile file=iterator.next();
                //
                String CT="UT"+JSON.toJSONString(file);
                boolean b = Utils.dealsSend(username, CT.getBytes());
                if (!b){
                    iterator.remove();
                }
            }
        });
    }

    public class h1 implements Runnable{

        @Override
        public void run() {
            cloudeChanel.transmit(targetFiles.get(0));
            targetFiles.remove(0);
        }
    }

}
