package superlink.udpbind.dataLink.data;

import superlink.filemanage.xmltool.XmlParser;
import superlink.udpbind.dataLink.UdpData;
import superlink.udpbind.dataqueue.DataQueue;
import superlink.udpbind.dataqueue.ReciveQueueFactory;
import superlink.udpbind.handle.Handler;
import superlink.udpbind.usedata.DataRequest;
import superlink.util.Utils;

import java.io.*;
import java.net.DatagramPacket;
import java.util.concurrent.*;

//todo
public class DataRecives implements Runnable{

    public DataRequest dataRequest;
    public UdpData udpData;
    protected final int poolSize=6;
    public DataQueue dataQueue;
    public BlockingQueue<byte[]> blockingQueue;
    public int id;
    public byte[] byteid;
    public RandomAccessFile randomAccessFile;
    public int bufferlen=1024;
    public ThreadPoolExecutor pool=new ThreadPoolExecutor(4,
            poolSize,1,TimeUnit.MINUTES,new LinkedBlockingQueue<>(5));
    boolean[] databool;

    public DataRecives(DataRequest dataRequest){
        this.dataRequest=dataRequest;
        this.udpData= Handler.UdpMap.get(dataRequest.requestname);
        this.dataQueue= ReciveQueueFactory.getDataQueue(udpData.userRequest.username);
        this.id=dataQueue.newId();
        this.byteid=new byte[]{(byte) this.id};
        System.out.println("DataRecives:"+id);
    }


    @Override
    public void run() {
        try {
            byte[] resend="star".getBytes();
            System.out.println("largeFileRecive:"+resend);
            resend= Utils.byteMerger(byteid,resend);
            DatagramPacket dstart=new DatagramPacket(resend,resend.length,this.udpData.userRequest.toaddress,this.udpData.userRequest.toport);
            this.udpData.dataSocket.send(dstart);

            databool=new boolean[dataRequest.page];
            if (dataRequest.page<100){
                ThreadRecive threadRecive=new ThreadRecive(databool);
                pool.execute(threadRecive);
                ReciveCheak reciveCheak=new ReciveCheak(databool, dataRequest.id);
                pool.execute(reciveCheak);
            }else {
                ThreadRecive threadRecive=new ThreadRecive(databool);
                pool.execute(threadRecive);
                ThreadRecive threadRecive1=new ThreadRecive(databool);
                pool.execute(threadRecive1);
                ThreadRecive threadRecive2=new ThreadRecive(databool);
                pool.execute(threadRecive2);
                ReciveCheak reciveCheak=new ReciveCheak(databool, dataRequest.id);
                pool.execute(reciveCheak);

            }
            System.out.println("Star Recive");

        } catch (IOException e) {
            e.printStackTrace();
        };

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
        public volatile BlockingQueue<byte[]> blockingQueue;
        public ThreadRecive(boolean[] booleans){
            this.booleans=booleans;
            blockingQueue=dataQueue.quemap.get(id);
        }

        @Override
        public void run() {
            System.out.println("ThreadRecive:"+id);
           // DatagramPacket datagramPacket=new DatagramPacket(new byte[65507],65507);
            //File file =new File(dataRequest.filename);"C:\\Users\\liushengchang-n\\Desktop\\v2111111.zip"
            String paths= XmlParser.cachepath; //"C:\\Users\\liusc\\Desktop\\新建文件夹\\1\\"
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

                while (true){
                    try {
                        byte[] bytes;
                        try {
                            bytes=dataQueue.getdata(id,(long)12);
                            if (bytes==null){
                                break;
                            }
                        } catch (InterruptedException | NullPointerException e) {
                            e.printStackTrace();
                            break;
                        }

                        String perx=new String(bytes,0,10);

                        Integer index=Integer.valueOf(perx)-1000000000;
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

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


        }
    }

    private class ReciveCheak implements Runnable{

        public boolean[] booleans;
        public int id;
        public ReciveCheak(boolean[] booleans,int id){
            this.booleans=booleans;
            this.id=id;
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
                        byte[] bsend=byteMerger(byteid,prex.getBytes());
                        DatagramPacket packet=new DatagramPacket(bsend,bsend.length,udpData.userRequest.toaddress,udpData.userRequest.toport);
                        try {
                            udpData.dataSocket.send(packet);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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

            byte[] over=byteMerger(byteid,"OK".getBytes());
            DatagramPacket packet=new DatagramPacket(over,over.length,udpData.userRequest.toaddress,udpData.userRequest.toport);
            try {
                udpData.dataSocket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("DataRecive reOVER");
            dataQueue.deltask(byteid[0]);
                pool.shutdownNow();
                System.out.println("DataRecive OVER");
                DataFactory.dataExecutor.shutdown();



        }
    }




    /**
     * 截取byte数组   不改变原数组
     * @param b 原数组
     * @param off 偏差值（索引）
     * @param length 长度
     * @return 截取后的数组
     */
    public byte[] subByte(byte[] b,int off,int length){
        byte[] b1 = new byte[length];
        System.arraycopy(b, off, b1, 0, length);
        return b1;
    }

    /**
     * 合并byte[]数组 （不改变原数组）
     * @param byte_1
     * @param byte_2
     * @return 合并后的数组
     */
    public byte[] byteMerger(byte[] byte_1, byte[] byte_2){
        byte[] byte_3 = new byte[byte_1.length+byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

}
