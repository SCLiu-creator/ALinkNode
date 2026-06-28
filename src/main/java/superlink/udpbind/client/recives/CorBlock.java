package superlink.udpbind.client.recives;

import superlink.util.Utils;

import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class CorBlock {


    public void lock(){
        while (CorBlockCon.runIng) {
            try {
                RunTime runTime;
                Iterator<RunTime> iterator = CorBlockCon.linkedBlockingQueue.iterator();
                if(iterator.hasNext()){
                    while (iterator.hasNext()) {
                        runTime = iterator.next();
                        try {
                            runTime.process();
                            runTime.decTimes();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        iterator.remove();
                        if(runTime.getTime()>0){
                            CorBlockCon.linkedBlockingQueue.add(runTime);
                        }
                    }
                }else {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    public void lock(long time){
        long time0=System.currentTimeMillis();
        while (true) {
            RunTime runTime;
            Iterator<RunTime> iterator = CorBlockCon.linkedBlockingQueue.iterator();
            if(iterator.hasNext()){
                runTime = iterator.next();
                try {
                    runTime.process();
                    runTime.decTimes();
                }catch (Exception e){
                    e.printStackTrace();
                }
                iterator.remove();
                if(runTime.getTime()>0){
                    CorBlockCon.linkedBlockingQueue.add(runTime);
                }
                if ((System.currentTimeMillis() - time0) > time) {
                    return;
                }

            }else {
                long wt=System.currentTimeMillis() - time0;
                if(wt<1){
                    return;
                }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        }
    }
//blockingQueue 0.7秒 一百万次
    public static void main(String[] args) {

        LinkedBlockingQueue<byte[]> blockingQueue=new LinkedBlockingQueue();
        Thread thread1= new Thread(new Runnable() {
            LinkedBlockingQueue<byte[]> blockingQueue1=blockingQueue;
            @Override
            public void run() {
                long t=System.currentTimeMillis();
                for (int i = 0; i < 100*1000*1000; i++) {
                    byte[] bytes=new byte[1780];
                    byte[] bytes1= Utils.intToByteArray(i);
                    bytes=Utils.byteMerger(bytes1,bytes);
                    blockingQueue1.add(bytes);
                }
                System.out.println(System.currentTimeMillis()-t);
            }
        });

        Thread thread2= new Thread(new Runnable() {
            LinkedBlockingQueue<byte[]> blockingQueue1=blockingQueue;
            Runtime runtime = Runtime.getRuntime();
            @Override
            public void run() {
                long t=System.currentTimeMillis();
                long t1=System.currentTimeMillis();
                for (int i = 0; i < 100*1000*1000; i++) {
                    try {
                        byte[] bytes =blockingQueue1.poll(1000L, TimeUnit.MILLISECONDS);
                        if(i%(1000*1000)==0){
                            System.out.println(Utils.byteArrayToInt(bytes));
                            System.out.println("now sp");
                            System.out.println(System.currentTimeMillis()-t1);
                            t1=System.currentTimeMillis();
                            // 获取 Runtime 实例

                            // 转换为 MB 单位
                            long mb = 1024 * 1024;
                            // 打印内存信息
                            System.out.println("##### 堆内存使用情况 #####");
                            System.out.println("最大内存: " + runtime.maxMemory() / mb + " MB");
                            System.out.println("已分配内存: " + runtime.totalMemory() / mb + " MB");
                            System.out.println("已使用内存: " + (runtime.totalMemory() - runtime.freeMemory()) / mb + " MB");
                            System.out.println("可用内存: " + runtime.freeMemory() / mb + " MB");
                        }
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }
                System.out.println(System.currentTimeMillis()-t);
            }
        });
        thread2.start();
        thread1.start();
    }
}

