package superlink.testjava;

import superlink.udpbind.client.recives.data.RingQue;
import superlink.udpbind.usedata.User;
import superlink.util.thread.SThread;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

public class Fim <T, U, R>implements fi{
    public fi f;
    public Fim(fi f){
        this.f=f;
    }
    public void df(){
        String ss="df";
        System.out.println("df");
        fi n=f.andThen((s)->{
            System.out.println("null");
            return "null";
        });
        fi nn=n.andThen((s)-> {
            System.out.println(s);
            return new Object();
        });
        System.out.println("andThen");
        nn.apply(ss,"");
    }
//    @Override
//当一个线程获取了锁之后，是不会被interrupt()方法中断的。因为调用interrupt()方法不能中断正在运行过程中的线程，只能中断阻塞过程中的线程。
//因此当通过lockInterruptibly()方法获取某个锁时，如果不能获取到，只有进行等待的情况下，是可以响应中断的。
//而用synchronized修饰的话，当一个线程处于等待某个锁的状态，是无法被中断的，只有一直等待下去。

//原文链接：https://blog.csdn.net/qq_43323776/article/details/82939344
    public static void main(String[] args)throws Exception {
        String s11="aaaaaaaaaaaaaaaaaaaaaa";
        String s12="cccccccccccccccccccccc";
        Map map=new HashMap();
        map.put(1,s11);
        map.put(1,s12);
        User user=(User)map.get(2);
        File fc=new File("C://.GamingRoot");
        byte i1=24;
        byte i2=56;
        byte i3=24^56;
        byte i4= (byte) (i3^56);
        String f=new File(new File("").getAbsolutePath()).getParent();

        String f1=new File(f).getParent();
        Arrays.stream(new File(f).list()).iterator().forEachRemaining((fp)->{
            System.out.println(fp);
        });
        String f2=new File(f1).getParent();
        String f3=new File(f2).getParent();
        String f4=new File(f3).getParent();

        DatagramSocket datagramSocket=new DatagramSocket(8080);
        DatagramSocket datagramSocket1=new DatagramSocket(8082);
        datagramSocket.send(new DatagramPacket(new byte[]{2,2,23,3,3,4,6},7,new InetSocketAddress("127.0.0.1",8082)));
        DatagramPacket datagramPacket=new DatagramPacket(new byte[10],10);
        datagramSocket1.receive(datagramPacket);
        System.out.println(Arrays.toString(datagramPacket.getData()));
        Socket socket0=new Socket();
//        new HashMap<>(new Comparator<>());
//        new Integer().compareTo()
//
//        new TreeMap<>().put().keySet()
        System.out.println(socket0.hashCode());
        System.out.println(((Object)"s").hashCode());
        System.out.println("socket0.hashCode()".hashCode());
        new Thread(()->{
            try {
                socket0.connect(new InetSocketAddress(9090));
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                socket0.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                socket0.connect(new InetSocketAddress(9090));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        AtomicReference<Socket> socket=new AtomicReference<>();
        new Thread(()->{
            try {
                ServerSocket serverSocket=new ServerSocket(9090);
                while (true){

                     socket.set(serverSocket.accept());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();

        System.out.println(socket.get().getPort());
        String st="null";
    Thread threads=new Thread(()->{
        try {
            InputStream inputStream = socket.get().getInputStream();
            byte[] bytes=new byte[1024];
            int len=0;
            while (true){
                try {
                    if (!((len= inputStream.read(bytes))!=-1)) break;
                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println(new String(bytes));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName());
        synchronized (st){
            try {
                st.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(st);
        System.out.println("state"+Thread.currentThread().getState());
    });

        long ls=0;
        for (int i = 0; i < 10; i++) {
            System.out.println(Thread.currentThread().getName());
            threads.start();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            socket.get().close();
            threads.interrupt();
            System.out.println("main  "+Thread.currentThread().getState());
            System.out.println("main state "+threads.getState());
            if (threads.getState().equals(Thread.State.RUNNABLE))
            threads.run();
//                    queue.take();
            ls =i*i;
            ls=ls<<2;
//            System.out.println(ls);

//                   System.out.println(queue.take());
        }

        ReentrantLock lock=new ReentrantLock();
//        try {
//            Socket socket=new ServerSocket(8080).accept();
//            socket.getInputStream().reset();
//            InputStream stream=socket.getInputStream();
//            int len=0;
//            byte[] bytes =new byte[1024];
//            while ((len=stream.read(bytes))!=-1){
//                System.out.println(new String(bytes));
//            }
//            System.out.println("over");
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        lock.lock();
        Condition condition=lock.newCondition();
        new Thread(()->{
            lock.lock();
            try {
                condition.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lock.unlock();
        }).start();
        try {
            lock.newCondition().await(5,TimeUnit.MICROSECONDS);
            condition.signal();
        } catch (Exception e) {
            e.printStackTrace();
        }
        lock.unlock();
        BlockingQueue queue = new RingQue(12);
//        SThread.start(SThread.create(() -> {
//            for (int i = 0; i < 10000000; i++) {
////                try {
////                    queue.put(i);
////                } catch (InterruptedException e) {
////                    e.printStackTrace();
////                }
//                String s=i+"i";
//                s=s+"";
//            }
//
//        }));

        SThread.start(SThread.create(() -> {
            long l1=System.currentTimeMillis();
            try {
                long s=0;
                for (int i = 0; i < 10000000; i++) {
//                    queue.take();
                   s =i*i;
                    s=s<<2;
                    System.out.println(s);
                    Thread.sleep(0);
//                   System.out.println(queue.take());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            long l2=System.currentTimeMillis();
            System.out.println(l2-l1);
            return null;
        }));
    }

//    {
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        long lt= TimeUnit.NANOSECONDS.toNanos(1000);
//        byte[] byteso=new byte[1];
////        queue.add(byteso);
////        queue.add(byteso);
////        queue.add(byteso);
//
//        byte[] bytes=new byte[]{86,-18,-70,-21,30,2,2,11,1,44,4,5,52};
//        int i=QueueStream.byteArrayToInt1(bytes);
//        bytes=new byte[10];
//        byte[] bytes1= Arrays.copyOfRange(bytes,6,8);
//
//        AtomicInteger atomicInteger;
//
//        ReentrantReadWriteLock lock=new ReentrantReadWriteLock();
//        ReentrantReadWriteLock.ReadLock obj=lock.readLock();
//
//        Thread thread=new Thread(()->{
//
//            try {
////                obj.lock();
////                Thread.sleep(5000);
////                Thread.currentThread().getState()
//                synchronized (obj) {
//                    obj.wait();
//                }
//
//
////
////obj.lockInterruptibly();
////                obj.wait();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }finally {
////                obj.unlock();
//            }
////            obj.unlock();
//            System.out.println("obj:"+obj);
//        });
//        thread.start();
//        try {
//            Thread.sleep(1);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        try {
////            obj.lock();
//                obj.notifyAll();
////            obj.unlock();
////            obj.unlock();
////            thread.interrupt();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//
//
//
//        Fim fim=new Fim((a,b)->{
//            System.out.println("aaaa");
//            return a;
//        });
//        fim.df();
//
//
//    }



    @Override
    public Object apply(Object o, Object o2) {
        return null;
    }

    @Override
    public fi andThen(Function after) {
        return null;
    }
}
