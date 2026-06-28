package superlink.udpbind.fileListen.common;


import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import superlink.filemanage.xmltool.XmlCreate;
import superlink.filemanage.xmltool.XmlParser;
import superlink.udpbind.cloude.*;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

public class FileMonitor {

    public FileAlterationMonitor monitor;

    public static long defaulttime=30000;//10*60*1000;

    public static long settime=60000;//10*60*1000;
    public volatile boolean runing=false;

    public FileMonitor(long interval) {
        settime=interval;
        monitor = new FileAlterationMonitor(interval);
    }
    public FileMonitor() {
        monitor = new FileAlterationMonitor(defaulttime);
    }
    /**
     * 给文件添加监听
     * @param path     文件路径
     */
    public void monitor(String path) {
        FileAlterationObserver observer = new FileAlterationObserver(new File(path));
        FileAlterationListener listener=new FileListener(path);
        observer.addListener(listener);
        monitor.addObserver(observer);

    }
    public FileAlterationObserver monitor(FileTrigger fileTrigger) {
        FileAlterationObserver observer = new FileAlterationObserver(fileTrigger);
        FileAlterationListener listener=new FileListener(fileTrigger);
        observer.addListener(listener);
        monitor.addObserver(observer);
        return observer;
    }
//    public FileAlterationObserver monitor(FileTrigger fileTrigger) {
//        FileAlterationObserver observer = new FileAlterationObserver(new File(fileTrigger.AbsolutePath));
//        FileAlterationListener listener=new FileListener(fileTrigger);
//        observer.addListener(listener);
//        monitor.addObserver(observer);
//        return observer;
//    }
    //FileListener()
    public void monitor(String path,FileAlterationListener listener) {
        FileAlterationObserver observer = new FileAlterationObserver(new File(path));
        observer.addListener(listener);
        monitor.addObserver(observer);

    }

    public void stop() throws Exception {
        monitor.stop();
        runing=false;
    }

    public void start() throws Exception {
        monitor.start();
        runing=true;
    }
    public synchronized void start1() throws Exception {
        Class mon=monitor.getClass();
        Field fieldRun = mon.getDeclaredField("running");
        fieldRun.setAccessible(true);
        Field fieldT = mon.getDeclaredField("thread");
        fieldT.setAccessible(true);
        Field fieldO = mon.getDeclaredField("observers");
        fieldO.setAccessible(true);


        boolean running =(Boolean) fieldRun.get(monitor);
        Thread thread = null;
        List<FileAlterationObserver> observers = (List<FileAlterationObserver>)fieldO.get(monitor);

        if (running) {
            throw new IllegalStateException("Monitor is already running");
        }

        fieldRun.set(monitor,true);
        if (monitor.getThread()!=null){
            monitor.getThread().interrupt();
        }
//        running = true;

//        if (threadFactory != null) {
//            thread = threadFactory.newThread(monitor);
//        } else {
//            thread = new Thread(monitor);
//        }
//        fieldT.set(monitor,thread);
//        thread.start();
    }

    public void manualStop() {
       if (CloudLocal.getSynContainer().localbin.updataSate){
           SAXReader reader=new SAXReader();
           Document document=null;
           try {
               document=reader.read(new File(XmlCreate.userCloudefile +".xml"));
           } catch (DocumentException e) {
               e.printStackTrace();
               System.out.println(Thread.currentThread().getName());
               return;
           }
           long t=System.currentTimeMillis();
//        DateFormat.getInstance().
           document.getRootElement().addAttribute("t",String.valueOf(t));
           XmlParser.SaveXml(document,XmlCreate.userCloudefile +".xml");
       }


        try {
            monitor.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearMonitor() {
        try {
            monitor.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        monitor=null;
    }
}
