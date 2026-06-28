package superlink.testjava;

import superlink.util.Utils;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Hashtable;
import java.util.concurrent.locks.ReentrantLock;

public class THttp {
    public THttp(){
        pr();
    }
    public void pr(){
        System.out.println("11111");
    }
    //199.239.199.239
    public static void main(String[] ags) throws IOException {
        File file1=new File("D:/java/新建文件夹/udpclient/web/Flutter-Sign-Up-master");
        System.out.println(file1.getName());;
        File file2=new File("D:\\java\\新建文件夹\\udpclient\\webFlutter-Sign-Up-master");
        File file3=new File("D:\\java\\新建文件夹\\udpclient\\webFlutter-Sign-Up-master\\ios");
        File file4=new File("D:\\java\\新建文件夹\\udpclient\\webFlutter-Sign-Up-master\\lib");
        int h1=file1.hashCode();
        int h2 =file2.hashCode();
        Hashtable<File,File> hashtable=new Hashtable<>();
        hashtable.put(file1,file1);
        hashtable.put(file2,file2);
        hashtable.put(file3,file3);
        while (!file3.isFile()){
            for (File f:file3.listFiles()){
                while (f.isDirectory()){
                    for (File ff:f.listFiles()){

                        System.out.println(ff.hashCode());
                    }
                }
            }
        }
        File filef=hashtable.get(file2);
        InetAddress inetAddress=Utils.getLocalIpv4();
        String bst=Utils.getSubnetMask(null);
        InetAddress inetAddressb=Utils.getBroadcastAddress(inetAddress,"255.255.255.0");
//        HttpThreadBind httpThreadBind=new HttpThreadBind(InetAddress.getByName("127.0.0.1"),8088);
//        httpThreadBind.run();
        new THttp();
        ReentrantLock obje=new ReentrantLock();
        new Thread(new testlock(obje)).start();


        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        obje.lock();
            System.out.println("star2");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        obje.notify();
            obje.unlock();


        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
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

        }
    public static class testlock implements Runnable {
        private ReentrantLock reentrantLock;

        public testlock(ReentrantLock reentrantLock) {
            this.reentrantLock = reentrantLock;
        }

        @Override
        public void run() {
            synchronized (reentrantLock) {
//            reentrantLock.lock();
                System.out.println("star");
                //reentrantLock.notify();
                try {
                    reentrantLock.wait(30000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("over");
//            reentrantLock.unlock();
            }
        }
    }


}
