package superlink.udpbind.client.recives;

import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.util.asynhandle.AsynHandle;

import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.concurrent.*;

//废弃
public class ByteQueFactory {

    public static Thread thread;


    public static ByteBufer getInstance(int u, Short i) {
        UserContext userContext= UDPclient.mainDataQueue.getUserContext(u);
        ByteQueCon byteQueCon=new ByteQueCon();
        userContext.setQueue(i,byteQueCon);

        if (thread==null){
            thread=new Thread();
            thread.setName("ByteQueFactory");
        }

        return byteQueCon;
    }
    public static CopyOnWriteArrayList alist;


    public class Run implements Runnable{
        public Run(){
            thread=Thread.currentThread();
        }
        ArrayList<Callable> list=new ArrayList();

        @Override
        public void run() {
            boolean b=true;
            while (b){
                list.clear();
                list.addAll(alist);
                alist.clear();
                for (Callable synReqFile:list){
                    try {
                        Object object=synReqFile.call();
                        if (object==null){
                            alist.add(synReqFile);
                        }else {
                            AsynHandle.getHandle().addWork().addFinally((objects)->{

                                return null;
                            });
                        }
                    }catch (Exception e){

                    }

                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

//            Semaphore semaphore=new Semaphore(0);
//            CountDownLatch countDownLatch=null;
//            countDownLatch.await();
        }
    }


    public static class ByteQueCon implements ByteBufer{

        public void add(DatagramPacket packet) {
            throw new IllegalStateException("UnImplement");
        }

        @Override
        public boolean add(byte[] e) {
            return false;
        }

        @Override
        public byte[] poll() {
            return new byte[0];
        }

        @Override
        public byte[] take() throws InterruptedException {
            return new byte[0];
        }

        @Override
        public byte[] poll(long timeout, TimeUnit unit) throws InterruptedException {
            return new byte[0];
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public void clear() {

        }
    }

}
