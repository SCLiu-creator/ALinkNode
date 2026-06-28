package superlink.udpbind.client.recives.data;


import superlink.filemanage.xmltool.XmlParser;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.ByteBufer;
import superlink.udpbind.client.recives.Senders;
import superlink.udpbind.dataLink.data.DataFactory;
import superlink.udpbind.usedata.DataRequest;
import superlink.util.Utils;

import java.io.*;
import java.net.DatagramPacket;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

import static superlink.util.Utils.byteMerger;
//弃用
public class DataRecive implements Runnable{

    public DataRequest dataRequest;
    protected final int poolSize=6;
    public UserContext userContext;
    public ByteBufer blockingQueue;
    public int id;
    public RandomAccessFile randomAccessFile;
    public int bufferlen=1024;
    public ThreadPoolExecutor pool=new ThreadPoolExecutor(4,
            poolSize,1,TimeUnit.MINUTES,new LinkedBlockingQueue<>(5));
    boolean[] databool;
    ReentrantLock lock=new ReentrantLock(true);

    public DataRecive(DataRequest dataRequest){
        this.dataRequest=dataRequest;
        this.userContext= UDPclient.mainDataQueue.getUserContext(dataRequest.requestname);
        blockingQueue=userContext.getDataQue((short) dataRequest.id);
        this.id=dataRequest.id;

        System.out.println("DataRecives:"+id);
    }


    @Override
    public void run() {

        String resend="star";
        byte[] bsend=byteMerger(Utils.getUseridByte(userContext.getBothId(),(short)id),resend.getBytes());
        System.out.println("largeFileRecive:"+resend);
        Senders.Sends(userContext.inetAddress,userContext.port,bsend);
        databool=new boolean[dataRequest.page];
        if (dataRequest.page<100){
            ThreadRecive threadRecive=new ThreadRecive(databool,blockingQueue);
            pool.execute(threadRecive);
            ReciveCheak reciveCheak=new ReciveCheak(databool, dataRequest.id);
            pool.execute(reciveCheak);
        }else {
            ThreadRecive threadRecive1=new ThreadRecive(databool,blockingQueue);
            pool.execute(threadRecive1);
//                ThreadRecive threadRecive2=new ThreadRecive(databool,blockingQueue);
//                pool.execute(threadRecive2);
            ReciveCheak reciveCheak=new ReciveCheak(databool, dataRequest.id);
            pool.execute(reciveCheak);
        }
        System.out.println("Star Recive");

//        try {
//            CompletableFuture<Boolean> future=CompletableFuture.supplyAsync(()-> {
//            try {
//                return largeFileRecive();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }return null;
//             });
//            boolean result=future.get();
    }

    private class ThreadRecive implements Runnable{

        public boolean[] booleans;
        public volatile ByteBufer blockingQueue;
        public ThreadRecive(boolean[] booleans,ByteBufer blockingQueue){
            this.booleans=booleans;
            this.blockingQueue=blockingQueue;
        }

        @Override
        public void run() {
            System.out.println("ThreadRecive:"+id);
            // DatagramPacket datagramPacket=new DatagramPacket(new byte[65507],65507);
            //File file =new File(dataRequest.filename);"C:\\Users\\liushengchang-n\\Desktop\\v2111111.zip"
            String paths= XmlParser.cloudecache; //"C:\\Users\\liusc\\Desktop\\新建文件夹\\1\\"
            File file =new File(paths+dataRequest.filename);

            if (!file.exists()){
                try {
                    File path =new File(paths);
                    if (!path.exists()){
                        path.mkdirs();
                    }
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                randomAccessFile=new RandomAccessFile(file,"rw");
                int wait=1;
               // lock.lock();
                while (true){
                    try {
                        byte[] bytes;
                        try {
                            bytes=blockingQueue.poll(4,TimeUnit.SECONDS);
                            if (bytes==null){
                                //lock.unlock();
                                if (wait<2){
                                    wait++;
                                    continue;
                                }
                                break;
                            }
                        } catch (InterruptedException | NullPointerException e) {
                            e.printStackTrace();
                            break;
                        }
                        String perx=new String(bytes,0,10);
                        Integer index=Integer.valueOf(perx)-1000000000;
                        System.out.println("etdata"+"  "+bytes.length+"  "+index);
                        //byte[] data=subByte(bytes,10,bytes.length-10);
                        randomAccessFile.seek(index*bufferlen);//65493
                        randomAccessFile.write(bytes,10,bytes.length-10);
                        booleans[index]=true;

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("dataRequest OVER");
                //pool.shutdown();
                lock.unlock();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


        }
    }

    private class ReciveCheak implements Runnable{

        public boolean[] booleans;
        public short id;
        public ReciveCheak(boolean[] booleans,int id){
            this.booleans=booleans;
            this.id=(short)id;
        }


        @Override
        public void run() {
            int i=booleans.length;
            int b=0;
            try {
                Thread.sleep(20*i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (i!=b){
                b=0;
                int c=0;
                for (boolean s:booleans){
                    if (booleans[c]==false){
                        System.out.println("请求zhen:"+c);
                        String prex=String.valueOf(c+1000000000);
                        byte[] bsend=byteMerger(Utils.getUseridByte(userContext.getBothId(),(short)id),prex.getBytes());

                        Senders.Sends(userContext.inetAddress,userContext.port,bsend);
                        try {
                            Thread.sleep(0,100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }else {

                        b++;
                    }
                    c++;
                }
            }

            try {
                randomAccessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String over="OK";
            byte[] bsend=byteMerger(Utils.getUseridByte(userContext.getBothId(),(short)id),over.getBytes());

            Senders.Sends(userContext.inetAddress,userContext.port,over.getBytes());
            System.out.println("DataRecive reOVER");
            userContext.deltask(id);
            pool.shutdownNow();
            System.out.println("DataRecive OVER");
            DataFactory.dataExecutor.shutdown();
        }
    }



}

