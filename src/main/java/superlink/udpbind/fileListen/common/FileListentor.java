package superlink.udpbind.fileListen.common;

import superlink.udpbind.fileListen.FileListen;
import superlink.udpbind.cloude.FileTrigger;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class FileListentor implements FileListen {
    //FileAlterationMonitor执行类
    public FileMonitor fileMonitor;
    public Map<String,FileTrigger> stringFileTriggerMap=new HashMap<>();
    public FileListentor(long time){
        this.fileMonitor = new FileMonitor(time);
        try {
            fileMonitor.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public FileListentor(){
        this.fileMonitor = new FileMonitor();
    }

    public void stop() throws Exception {
        this.fileMonitor.monitor.stop();
        this.fileMonitor.runing=false;
    }

    public void start() throws Exception {
        this.fileMonitor.monitor.start();
        this.fileMonitor.runing=true;
    }
    @Override
    public boolean Run() {
        if (fileMonitor==null){
            return false;
        }else {
            fileMonitor.monitor.run();
        }
        return true;
    }

    @Override
    public boolean isRun() {
        if (fileMonitor==null){
            return false;
        }else {
            return fileMonitor.runing;
        }
    }
    @Override
    public void manualStop(){
        if (fileMonitor!=null){
            fileMonitor.manualStop();
        }
    }
    @Override
    public void clearMonitor() {
        if (fileMonitor!=null){
            fileMonitor.clearMonitor();
        }
    }

    public void ReSetTime(long time){
        boolean b = fileMonitor==null?false:true;
        if (fileMonitor==null){
            FileMonitor fileMonitor=new FileMonitor(time);
            try {
                this.fileMonitor.stop();
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
            this.fileMonitor = fileMonitor;
        }else {
            try {
                this.fileMonitor.stop();
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }


        List<FileAlterationObserver> observerList=new ArrayList<>();

        stringFileTriggerMap.forEach((k,v)->{
            AtomicBoolean atomicBoolean=new AtomicBoolean(true);
            Iterable<FileAlterationObserver> observers=fileMonitor.monitor.getObservers();

            observers.forEach((o)->{
                if (o.getDirectory().equals(new File(k))){
                    atomicBoolean.set(false);
                }
            });
            if (atomicBoolean.get()){
                FileAlterationObserver observer=fileMonitor.monitor(v);
                observerList.add(observer);
            }else {
                System.out.println("contrained:  "+ k);
            }

        });


        try {
            if (b){
                Iterable<FileAlterationObserver> observers=fileMonitor.monitor.getObservers();
                observers.forEach((o)->{

                    try {
                        Class mon=o.getClass();
                        Field fieldRootEntry = mon.getDeclaredField("rootEntry");
                        fieldRootEntry.setAccessible(true);
                        FileEntry fileEntry= (FileEntry)fieldRootEntry.get(o);
                        FileEntry[] fileEntries=fileEntry.getChildren();
                        if (fileEntries.length<1){
                            o.initialize();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                });
//                for (FileAlterationObserver o:observers){
//                    observers.
//                }
                fileMonitor.start1();
            }else {
                fileMonitor.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //绝对路径
    public boolean addListenDir(String filepath) {
        try {
            this.fileMonitor.stop();
            fileMonitor.monitor(filepath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            this.fileMonitor.start();
        } catch (Exception e) {
            e.printStackTrace();
            //二次启动
        }
        return true;
    }
    //绝对路径
    @Override
    public boolean addListenDirRuning(FileTrigger fileTrigger) {
        if (stringFileTriggerMap.containsKey(fileTrigger.AbsolutePath)){
            return true;
        }
        try {
            this.fileMonitor.stop();
            //todo
//            fileMonitor.monitor(fileTrigger);
            fileMonitor.monitor(fileTrigger);
            stringFileTriggerMap.put(fileTrigger.AbsolutePath,fileTrigger);
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
            return false;
        }catch (Exception e) {
            e.printStackTrace();
        }
        try {
            this.fileMonitor.start();
            System.out.println("starLisent: "+fileTrigger.AbsolutePath);
        } catch (Exception e) {
            e.printStackTrace();
            //二次启动
        }
        return true;
    }
    //绝对路径
    @Override
    public boolean addListenDirStop(FileTrigger fileTrigger) {
        if (stringFileTriggerMap.containsKey(fileTrigger.AbsolutePath)){
            return true;
        }
        try {
            this.fileMonitor.stop();
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }catch (Exception e) {
            e.printStackTrace();
        }
        try {
            fileMonitor.monitor(fileTrigger);
            stringFileTriggerMap.put(fileTrigger.AbsolutePath,fileTrigger);
            System.out.println("addLisentFile: "+fileTrigger.AbsolutePath);
        }catch (Exception e) {
            e.printStackTrace();
        }
        try {
//            this.fileMonitor.start();
            this.fileMonitor.start1();
        } catch (Exception e) {
            e.printStackTrace();
            //二次启动
        }
        return true;
    }
    //绝对路径
    public boolean removeListenDirRuning(FileTrigger fileTrigger) {
        if (!stringFileTriggerMap.containsKey(fileTrigger.AbsolutePath)){
            return true;
        }
        try {
            this.fileMonitor.stop();
            fileMonitor.monitor(fileTrigger);
            stringFileTriggerMap.remove(fileTrigger.AbsolutePath,fileTrigger);
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
            return false;
        }catch (Exception e) {
            e.printStackTrace();
        }
        try {
            this.fileMonitor.start();
        } catch (Exception e) {
            e.printStackTrace();
            //二次启动
        }
        return true;
    }


public static void main(String[] args) throws Exception {
    long l1=System.currentTimeMillis();
    long l2=21876293;
    l2=l2/100;
    l2=l2/100;
    l2=l2/100;
    System.out.println(l1^l2);
    File d=new File("C:\\Users\\liushengchang-n\\my-project");
    String sss=null;
    "e".equals(sss);
    Path path= Paths.get(d.getPath());
    BasicFileAttributeView basicView=Files.getFileAttributeView(path, BasicFileAttributeView.class);
    BasicFileAttributes basicFileAttributes = basicView.readAttributes();
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    File[] ff=d.listFiles();
    System.out.println(d.getName());
    long t=System.currentTimeMillis();
    System.out.println(t);
    System.out.println(basicFileAttributes.lastModifiedTime().toMillis());
    System.out.println(df.format(new Date(basicFileAttributes.lastModifiedTime().toMillis())));

    FileListentor fileListentor =new FileListentor();
    FileMonitor fileMonitor = new FileMonitor(8000);
    //    String s="aabbcc";
    //    System.out.println(s.replace("aa",""));
    //    System.out.println(s.split("aa")[1]);
    //    System.out.println(s.replace("aa","").length());
//    fileMonitor.monitor(XmlParser.cachepath, new FileListener());
//    ///Users/zzs/temp/
//   // fileMonitor.monitor("E:\\", new FileListener());
//    fileMonitor.start();
//    fileMonitor.stop();
//    fileMonitor.monitor(XmlParser.cloudecache, new FileListener());
//    fileMonitor.monitor(XmlParser.showpath, new FileListener());
//    fileMonitor.start();
//    fileMonitor.stop();
//    fileMonitor.monitor("D:\\BaiduNetdiskDownload\\");
////    fileMonitor.monitor(XmlParser.showpath);
////    fileMonitor.monitor("C:\\Users\\liushengchang-n\\my-project");
//    fileMonitor.start();
//    fileMonitor.monitor.run();
    fileListentor.start();
    fileListentor.addListenDir("C:\\Users\\liushengchang-n\\Desktop\\新建文件夹 (4)");
    while (true) {
        File[] files=new File("C:\\Users\\liushengchang-n\\Desktop\\新建文件夹 (4)").listFiles();
        fileListentor.Run();
        Thread.sleep(3000);
    }
    }
}