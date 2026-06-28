package superlink.testjava;

import com.google.zxing.WriterException;
import superlink.init.InitClass;
import superlink.util.GeneratorQR;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.locks.ReentrantLock;

public class LockTest {

    public void test(){
        ReentrantLock obje=new ReentrantLock();
        testlock tt=new testlock(obje);
        Thread thread=new Thread(tt);
        thread.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //obje.lock();

        //obje.notify();
        //thread.interrupt();
        Thread.interrupted();
        synchronized (obje){
            System.out.println("star2");
            //obje.notify();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("t2:"+i);
            i++;

        }

        //obje.unlock();
        System.out.println("over2");

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    //   ServerSocket socket=new ServerSocket(8084);

//        while (true){
//            Socket socketq=socket.accept();
//            byte[] bytes=new byte[1024];
//            ByteBuffer byteBuffer=ByteBuffer.allocate(1024);
//            int i;
//            do {
//                i=socketq.getInputStream().read(bytes) ;
//                System.out.println(new String(bytes, Charset.forName("UTF-8")));
//            }while (i != -1);
//
//
//
//            }
    //System.out.println(bytes.length);


    public  class testlock implements Runnable {
        private ReentrantLock reentrantLock;

        public testlock(ReentrantLock reentrantLock) {
            this.reentrantLock = reentrantLock;
        }

        @Override
        public void run() {

                //reentrantLock.lock();
                System.out.println("star");
                //reentrantLock.notify();
            synchronized (reentrantLock){
                System.out.println("t1:"+i);
                i++;
                try {
                    reentrantLock.wait(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

                System.out.println("over");
            System.out.println("t11:"+i);

                //reentrantLock.unlock();

        }
    }
    Integer i=1;
    public static void main(String[] args) {
        byte bs=127;
        bs++;
        System.out.println(bs);
        bs++;
        System.out.println(bs);
        try {
            byte[] bytes=GeneratorQR.getQRCodeImage("http://192.168.0.103:8987/",126,126);
            File fileqr=new File(InitClass.absolute+"qr.png");
            OutputStream outputStreamqr=new FileOutputStream(fileqr);
            outputStreamqr.write(bytes);
            outputStreamqr.flush();
            outputStreamqr.close();
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        ReentrantLock lock=new ReentrantLock();
        new Thread(()->{
            synchronized (lock){
            try {

                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                System.out.println("wake");
                }
                System.out.println("wake");
            }
        }).start();
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
      //
        synchronized (lock){
            lock.notifyAll();
        }

       // lock.unlock();
        //new LockTest().test();
    }


}


