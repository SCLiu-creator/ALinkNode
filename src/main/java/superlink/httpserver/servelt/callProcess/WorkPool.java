package superlink.httpserver.servelt.callProcess;

import sun.nio.ch.ThreadPool;

import java.lang.reflect.Field;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class WorkPool {
    static ThreadPoolExecutor threadPool;
    static BlockingQueue blockingQueue;


    static {
        blockingQueue=new ArrayBlockingQueue<>(1000);
        threadPool = new ThreadPoolExecutor(
                    1,1,1000, TimeUnit.SECONDS,blockingQueue){
            @Override
            protected void finalize() {
                System.out.println("finalize ThreadPool"+this.hashCode());
                super.finalize();
            }
        };
    }

    public static ThreadPoolExecutor getThreadPool() {
        return threadPool;
    }

    public static BlockingQueue getBlockingQueue() {
        return blockingQueue;
    }

    public static void setThreadPool(ThreadPoolExecutor threadPool) {
        if (threadPool==WorkPool.threadPool){
            return;
        }
        try {
            Field field=ThreadPoolExecutor.class.getDeclaredField("workQueue");
            field.setAccessible(true);
            BlockingQueue blockingQueue= (BlockingQueue) field.get(threadPool);
            setBlockingQueue(blockingQueue);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        WorkPool.threadPool.shutdownNow();
        WorkPool.threadPool = threadPool;
    }
    public static void setNewThreadPool(int corePoolSize,
                                        int maximumPoolSize,
                                        long keepAliveTime,
                                        TimeUnit unit) {
        threadPool = new ThreadPoolExecutor(
                1,1,1000, TimeUnit.SECONDS,blockingQueue){
            @Override
            protected void finalize() {
                System.out.println("finalize ThreadPool"+this.hashCode());
                super.finalize();
            }
        };
    }

    public static BlockingQueue  setBlockingQueue(BlockingQueue blockingQueue) {
        int l=WorkPool.blockingQueue.size();
        if (l>0){
            BlockingQueue bl=new ArrayBlockingQueue(l);
            while (WorkPool.blockingQueue.size()>0){
                bl.add(WorkPool.blockingQueue.poll());
            }
            WorkPool.blockingQueue = blockingQueue;
            for (Object o:bl){
                blockingQueue.add(o);
            }
        }else {
            WorkPool.blockingQueue = blockingQueue;
        }
        return blockingQueue;
    }

    public static void main(String[] args) {
        AtomicInteger t=new AtomicInteger(0);
        AtomicInteger finalT1 = t;
//        RunCon con=new RunCon();
//        con.setEnd(()->{
//            con.getO();
//        });
        threadPool.execute(new Runnable(){

            @Override
            public void run() {
                System.out.println(": "+ finalT1.get());
            }
        });
        while (t.get()<200){
            ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
                    3,3,1000, TimeUnit.SECONDS,new ArrayBlockingQueue<>(100)){
                @Override
                protected void finalize() {
                    System.out.println("finalize ThreadPool"+this.hashCode());
                    super.finalize();
                }
            };
            setThreadPool(threadPool);
            AtomicInteger finalT = t;
            blockingQueue.add(new Runnable(){

                int v=finalT.get();
                @Override
                public void run() {

                    Thread.yield();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                    System.out.println(": "+ v);
                }
            });
            setThreadPool(threadPool);
            blockingQueue.add(new Runnable(){

                int v=finalT.get();
                @Override
                public void run() {
                    Thread.yield();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                    System.out.println(": "+ v);
                }
            });
//            AtomicInteger finalT1 = t;
            threadPool.execute(new Runnable(){

                int v=finalT.get();
                @Override
                public void run() {
                    Thread.yield();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                    System.out.println(": "+ v);
                }
            });
            try {
                Thread.sleep(500);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
            t.getAndIncrement();
            System.gc();
        }
    }
}
