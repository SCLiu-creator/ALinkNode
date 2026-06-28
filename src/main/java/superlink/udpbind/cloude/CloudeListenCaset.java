package superlink.udpbind.cloude;

import superlink.filemanage.xmltool.UserGet;
import superlink.udpbind.fileListen.FileListen;
import superlink.udpbind.fileListen.common.FileListentor;
import superlink.udpbind.cloude.operta.Browse;
import superlink.udpbind.cloude.operta.Monitor;
import superlink.udpbind.cloude.operta.broadcast.Operta;
import superlink.udpbind.cloude.operta.Server;
import superlink.udpbind.cloude.operta.Consist;
import superlink.udpbind.cloude.operta.unicast.UseOperta;
import superlink.util.SHAutils;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;

public class CloudeListenCaset implements Runnable{
    public static CloudeListenCaset cloudeListenCaset;
    public static Object lock=new Object();
    public static final int BROWSE=0;
    public static final int SYN=1;
    public static final int SERVER=2;
    public static final int MONITOR=3;
    public static int B=0;
    public static int Y=1;
    public static int S=2;
    public static long casetTime=1000*60;
    public Operta operta;
    public UseOperta useOperta;

    public DataCloud dataCloud;
    public volatile boolean state=false;
    public Operta.OpertaFutrue opertaFutrue;
    public Thread castThread;

    public FileListen fileRunner;

    public static CloudeListenCaset FactortCloudeLisentCaset(){
        if (!CloudLocal.isInitSynContainer()){
            CloudLocal.getSynContainer();
        }
        if (cloudeListenCaset == null){
            cloudeListenCaset =new CloudeListenCaset();
            int mode=UserGet.getCloudeMode();
            if (mode>=0){
                cloudeListenCaset.setMode(mode);
            }
        }
        return cloudeListenCaset;
    }
    public static CloudeListenCaset FactortCloudeLisentCaset(int type){
        if (!CloudLocal.isInitSynContainer()){
            CloudLocal.getSynContainer();
        }
        if (cloudeListenCaset == null){
            cloudeListenCaset =new CloudeListenCaset(type);
        }else {

        }
        return cloudeListenCaset;
    }

    private CloudeListenCaset(){
        this.dataCloud=new DataCloud();
        //this.bin=CloudLocal.synContainer.localbin;
    }
    private CloudeListenCaset(int type){
        Operta.listMapBuffer=new ConcurrentHashMap<>();
        switch (type){
            case MONITOR:{this.operta=new Monitor();break;}
            case SYN:{this.operta=new Consist();break;}
            case SERVER:{this.operta=new Server();break;}
            case BROWSE:{this.operta=new Browse();break;}
            default:{
                return;
            }
        }
        this.dataCloud=new DataCloud();
        opertaFutrue=this.operta.getFutrue();

//        Operta.poolExecutor.execute(operta);
        Operta.poolExecutor.execute(opertaOn());
//        Future<?> f=operta.poolExecutor.submit(()->{});
//        f.cancel()
    }

    public CloudeListenCaset setMode(int type){
        if (Operta.listMapBuffer==null){
            Operta.listMapBuffer=new ConcurrentHashMap<>();
        }
//        if(this.operta!=null){
//            try {
//                this.operta.allDown();
//                this.operta.interrupt();
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }
        switch (type){
            case MONITOR:{this.operta=new Monitor();break;}
            case SYN:{this.operta=new Consist();break;}
            case SERVER:{this.operta=new Server();break;}
            case BROWSE:{this.operta=new Browse();break;}
            default:{
                this.operta=null;
                //todo
                state=false;
                return this;
            }
        }

        if (this.opertaFutrue==null){
            this.opertaFutrue=this.operta.getFutrue();
        }else {
            this.opertaFutrue.setB(type);
            this.opertaFutrue=this.operta.getFutrue();
        }
//        Operta.poolExecutor.execute(operta);
        return this;
    }

    public boolean symbol =true;
    public CloudeListenCaset broadcast(boolean b){
        symbol=b;
//        Operta.poolExecutor.execute(this);
        return this;
    }
    public CloudeListenCaset castTimeSet(long time){
        CloudeListenCaset.casetTime=time;
        return this;
    }

    public CloudeListenCaset start(){
        if (this.castThread==null){
            Operta.poolExecutor.execute(this);
        }else {
            if (!this.castThread.isAlive()){
                Operta.poolExecutor.execute(this);
            }else {
                try {
                    this.notifyAll();
                }catch (IllegalMonitorStateException e){
                    System.out.println("CloudeListenCaset start notifyAll"+e.getMessage());
                }catch (Exception e){
                    e.printStackTrace();
                }
                state=true;
            }
        }
        return this;
    }
    public FileListen getFileRunner(){
        if (state==false){
            return null;
        }
        if (this.fileRunner==null){
            this.fileRunner=new FileListentor();
        }
        return this.fileRunner;
    }

    public FileListen setFileRunner(long time){
        this.fileRunner=new FileListentor(time);
        return this.fileRunner;
    }

    public CloudeListenCaset immediate(){
        if (castThread==null){
            this.start();
            dataCloud.immediate();
            return this;
        }
        if (castThread.getState()==Thread.State.TIMED_WAITING){
            castThread.interrupt();
        }
        dataCloud.immediate();
        return this;
    }
    public UseOperta opertaOn(){
        UseOperta useOperta=new UseOperta();
        this.useOperta=useOperta;
        return useOperta;
    }

    public void stop(){
//        symbol=false;
        state=false;
        this.castThread.interrupt();
        state=false;
    }

    public void opertaIntrrupt(){
        if (this.operta!=null && this.operta.thread!=null){
            this.operta.thread.interrupt();
        }
        if (this.useOperta!=null && this.useOperta.thread!=null){
            this.useOperta.thread.interrupt();
        }
    }

    public boolean immediate;

    @Override
    public void run() {
        //统一发送FileTrigger.TargetFile消息
        if(state){
            return;
        }
        this.castThread=Thread.currentThread();
        castThread.setName("CloudeListenCaset");
        synchronized (CloudeListenCaset.class){
            System.out.println("cast Start/n/\n");
            state=true;
            long runTime=System.currentTimeMillis();
            while (state) {
                if (symbol) {
                    runTime = obversCast(runTime);
                }else {
                    runTime = obversOnly(runTime);
                }
                if(operta!=null){
                    if (operta.opertaFutrue.getB()==-1){
                        operta=null;
                    }else {
                        operta.run();
                    }
                }

                try {
                    Thread.sleep(casetTime);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        castThread=null;
    }

    public long obversCast(long runTime){
        long time = runTime;
        if((System.currentTimeMillis()-runTime>casetTime)){
            this.getFileRunner().Run();
            try {
                CloudeSynContainer synContainer=CloudLocal.getSynContainer();
                for (Map.Entry<String, FileTrigger> fileTriggerEntry : synContainer.localbin.map.entrySet()) {
                    BlockingQueue<FileTrigger.TargetFile> addque = fileTriggerEntry.getValue().addque;
                    for (FileTrigger.TargetFile f : addque) {
                        dataCloud.sendque(addque.poll());
                    }
                    BlockingQueue<FileTrigger.TargetFile> delque = fileTriggerEntry.getValue().delque;
                    for (FileTrigger.TargetFile f : delque) {
                        try {
                            dataCloud.sendque(delque.take());
//                                removeCache(f);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    BlockingQueue<FileTrigger.TargetFile> changque = fileTriggerEntry.getValue().changque;
                    for (FileTrigger.TargetFile f : changque) {
                        try {
                            dataCloud.sendque(changque.take());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
//                        dataCloud.sendque(fileTriggerEntry.getValue().deal0());
                }
                dataCloud.immediate();
                time=System.currentTimeMillis();
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }

        if (!state){
            synchronized (this){
                try {
                    Thread.interrupted();
                    this.wait();
                }catch (InterruptedException interruptedException) {
                    System.out.println("Cloudelist_299: "+interruptedException.getMessage());
                } catch (Exception interruptedException) {
                    interruptedException.printStackTrace();
                }finally {
                    state=true;
                }
            }
        }
        return time;
    }

    public long obversOnly(long runTime){
        long time = runTime;
        if((System.currentTimeMillis()-runTime>casetTime)){
            //监听文件但不广播
            CloudeSynContainer synContainer=CloudLocal.getSynContainer();
            for (Map.Entry<String, FileTrigger> fileTriggerEntry : synContainer.localbin.map.entrySet()) {
                BlockingQueue<FileTrigger.TargetFile> addque = fileTriggerEntry.getValue().addque;
                for (FileTrigger.TargetFile f : addque) {
                    try {
                        addque.poll();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                BlockingQueue<FileTrigger.TargetFile> delque = fileTriggerEntry.getValue().delque;
                for (FileTrigger.TargetFile f : delque) {
                    try {
                        delque.take();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                BlockingQueue<FileTrigger.TargetFile> changque = fileTriggerEntry.getValue().changque;
                for (FileTrigger.TargetFile f : changque) {
                    try {
                        changque.take();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //dataCloud.sendque(t.getValue().deal0());
            }
            time=System.currentTimeMillis();
        }
        return time;
    }

    public void removeCache(FileTrigger.TargetFile file){
        File f=new File(SHAutils.getSHA1(file.getATP(),false));
        if (f.exists()){f.delete();}
    }
    public static class Obverse implements Runnable{
        BlockingQueue queue;
        public Obverse(BlockingQueue blockingQueue){
            queue=blockingQueue;
        }

        @Override
        public void run() {

        }
    }
}

