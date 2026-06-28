package superlink.udpbind.dataqueue;


import superlink.udpbind.dataLink.UdpData;
import superlink.udpbind.handle.Handler;
import superlink.util.prioityThreadPool.PriorityThreadPoolExecutor;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;

public class DataQueue {


    public ThreadPoolExecutor threadPoolExecutor;

    //接收某端口，根据任务id存储
    public Map<Byte,BlockingQueue<byte[]>> quemap =new ConcurrentHashMap<Byte, BlockingQueue<byte[]>>();
    //根据名称获取任务id
   // public Map<String,Integer> taskkey =new ConcurrentHashMap<String,Integer>();

    public String requestname;//对方名称
    public UdpData udpData;
    public DataQueue(String requestname){
        threadPoolExecutor= new PriorityThreadPoolExecutor(2,
                        20, 30, 6);
        this.requestname=requestname;
        this.udpData=Handler.UdpMap.get(requestname);
        Reciverques reciverques =new Reciverques(udpData,requestname,this);
        this.threadPoolExecutor.execute(reciverques);
//        Reciverques reciverques1 =new Reciverques(udpData,requestname,this);
//        this.threadPoolExecutor.execute(reciverques1);
    }
    //根据名称获取数据
//    public  byte[] getdata(String dirname,Long time) throws InterruptedException {
//        Integer key=taskkey.get(dirname);
//        BlockingQueue<byte[]> blockingQueue= quemap.get(key);
//        byte[] bytes=blockingQueue.poll(time,TimeUnit.SECONDS);
//        return bytes;
//    }
    //根据id获取数据
    public  byte[] getdata(int id,Long time) throws InterruptedException {
        BlockingQueue<byte[]> blockingQueue= quemap.get((byte)id);
        byte[] bytes=blockingQueue.poll(time,TimeUnit.SECONDS);
        return bytes;
    }

    //添加接收队列
    public  boolean addtask(Byte id, String dirname){
       // taskkey.put(dirname,id);
        this.quemap.put(id,new LinkedBlockingQueue<byte[]>());
        return true;
    }
//    public synchronized boolean deltask(String dir){
//        int id=taskkey.get(dir);
//        taskkey.remove(dir);
//        quemap.remove(id);
//        return true;
//    }
    public synchronized boolean deltask(Byte b){
        quemap.remove(b);
        return true;
    }

    //增加接收线程数量
    public void addspead(){
        Reciverques reciverques =new Reciverques(udpData,requestname,this);
        this.threadPoolExecutor.execute(reciverques);
    }

    //public int getId(String dir){
//        return taskkey.get(dir);
//    }

    public byte newId(){
        Random random=new Random();
        int intid=0;
        while (quemap.get(intid)!=null){
            intid=random.nextInt(255)-128;
        }
        Byte b=new Byte((byte) intid);
        quemap.put(b,new LinkedBlockingQueue<>());
        return b;
    }
    public void addQue(byte b){
        quemap.put(b,new LinkedBlockingQueue<>());
    }

    @Override
    public void finalize()throws Throwable{
        threadPoolExecutor.shutdownNow();

    }


}
