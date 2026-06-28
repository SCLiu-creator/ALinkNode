package superlink.udpbind.cloude.operta.broadcast;

import superlink.udpbind.cloude.FileTrigger;
import superlink.util.prioityThreadPool.PriorityThreadPoolExecutor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

//处理cloudepage源方发来的TargetFile
public abstract class Operta implements Runnable{

    //事件接收缓冲区  username,set
    public static Map<String, Set<FileTrigger.TargetFile>> listMapBuffer;

    public static ThreadPoolExecutor poolExecutor =
            new PriorityThreadPoolExecutor(2,5,10,10);;
    public OpertaFutrue opertaFutrue;

    public Thread thread;

    public void interrupt(){
        thread.interrupt();
    }
    //        FileUtils.copyFile();
    public void allDown(){
        opertaFutrue.setB(-1);
    }

    public OpertaFutrue getFutrue(){
        return this.opertaFutrue;
    }

    public static class OpertaFutrue{
        public OpertaFutrue(int b){
            this.b=b;
        }
        int b;
        public int getB() {
            return b;
        }
        //关闭
        public void setB(int b) {
            this.b = b;
        }
    }

    public static synchronized Map UpDateListMapBuffer() {
        Map<String, Set<FileTrigger.TargetFile>> map=listMapBuffer;
        listMapBuffer=new ConcurrentHashMap();
        return map;
    }

    public static synchronized void put(String k,FileTrigger.TargetFile targetFile) {
        Map<String,Set<FileTrigger.TargetFile>> map=Operta.listMapBuffer;
        Set<FileTrigger.TargetFile> set= map.get(k);
        if (set==null){
            set=new HashSet<>();
            map.put(k,set);
        }
        set.add(targetFile);
    }
}
