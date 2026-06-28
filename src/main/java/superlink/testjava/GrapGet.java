package superlink.testjava;

import superlink.filemanage.xmltool.XmlParser;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class GrapGet {

    public static void main(String[] args) {



        File file=new File(XmlParser.dir);
        System.out.println(XmlParser.dir.replace("\\","/"));
        File file1=new File(XmlParser.dir.replace("\\","/")+"1");
        file1.mkdirs();
        Map<String,Map<Integer, BlockingQueue<byte[]>>> iplist =new ConcurrentHashMap<String,Map<Integer, BlockingQueue<byte[]>>>();
        Map<Integer, BlockingQueue<byte[]>> map=new ConcurrentHashMap<Integer, BlockingQueue<byte[]>>();
        map.put(0,new LinkedBlockingQueue<>(2000));
        Random random=new Random();
        int rand=random.nextInt();
        while (true){
            rand=random.nextInt();
            if (rand<0){break;}
        }
        AtomicInteger tir=new AtomicInteger(1000000000);
        byte[] test=new byte[]{38,-63,82,17,127,127,44,34};
        Integer integertest16=650203665;
        byte[] test1=new byte[4];
        String s0111="0111";
        long tim=System.currentTimeMillis();
        LinkedBlockingQueue linkedBlockingQueue=new LinkedBlockingQueue<>(2000);
        Integer i0=0;
        while (tir.get()>0){
            tir.decrementAndGet();
//            map.put(tir.get(),linkedBlockingQueue);
////
//            Integer integer1=test[0]*16*16*16*16*16 +16;
//            Integer integer2=test[0]<<4<<4<<4<<4<<4 ^ 16;

            int integer16=test[0]*16*16*16*16*16*16+test[1]*16*16*16*16+test[2]*16*16+test[3];
             Integer integerss=test[0]<<4<<4<<4<<4<<4 ^ test[1]<<4<<4<<4<<4 ^ test[2]<<4<<4<<4 ^ test[3]<<4<<4 ^ test[4]<<4 ^ test[5]<<0;
            Integer integerss16=test[0]<<4<<4<<4<<4<<4<<4 ^ test[1]<<4<<4<<4<<4 ^ test[2]<<4<<4 ^ test[3];
            test1[0]= (byte) ((integertest16>>24)& 0xFF);
            test1[1]= (byte) ((integertest16>>16)& 0xFF);
            test1[2]= (byte) ((integertest16>>8)& 0xFF);
            test1[3]= (byte) ((integertest16)& 0xFF);
            int integers1=(test1[0]& 0xFF)<<8<<8<<8;
            int integers2=(test1[1]& 0xFF)<<8<<8;
            int integers3=(test1[2]& 0xFF)<<8;
            int integers4= (test1[3]& 0xFF)<<0;
            Integer integerss1=test1[0]<<4<<4<<4<<4<<4<<4 ^ test1[1]<<4<<4<<4<<4 ^ test1[2]<<4<<4 ^ test1[3];
            Integer integers=integers1|integers2|integers3|integers4;

            Integer integersss=(test[0]<<4<<4<<4<<4<<4) + (test[1]<<4<<4<<4<<4) + (test[2]<<4<<4<<4) + (test[3]<<4<<4) + (test[4]<<4) + test[5]<<0;
            String ss=Integer.toHexString(integerss);
            BigInteger bigInteger=new BigInteger(ss);
            byte[] bb=bigInteger.toByteArray();
//            bb[0]=integerss%16;
//            bb[1]=(integerss/16)%16;
            byte[] b=ss.getBytes();
            byte[] bytes=integerss.toString().getBytes();
            String s=new String(bb,0,0,6);
            Integer integer=Integer.valueOf(s);
            System.out.println(integer);
//            Integer integer=Integer.valueOf(s0111);
//            String s1=new String(test,0,6,2);
//            Integer integer1=Integer.valueOf(s1);
//           // String ss=
        }
        long tim2=System.currentTimeMillis();
        System.out.println(tim2-tim);

        int t1=187604/3;
        int t2=676254/3;
        int t3=806856/3;
        int t4=8141506/3;
        int T3=623293/3;
        int T2=641487/3;
        int i16=25165824;

        int len=65507;

        iplist.put("aaa",map);
        byte[] bytes=new byte[len];
        for (int i=0;i<len;i++){
            bytes[i]=48;
        }
        DatagramPacket packet=new DatagramPacket(bytes,len);
        DatagramSocket datagramSocket=null;
        try {
            datagramSocket=new DatagramSocket(9000);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        DatagramPacket packets=new DatagramPacket(bytes,len, InetAddress.getLoopbackAddress(),9000);
        try {
            datagramSocket.send(packets);
        } catch (IOException e) {
            e.printStackTrace();
        }

        AtomicInteger b= new AtomicInteger();
        //long timeMillis=System.currentTimeMillis();

        DatagramSocket finalDatagramSocket1 = datagramSocket;
        ExecutorService executor=Executors.newScheduledThreadPool(10);

        Thread ts=new Thread(()->{
            Map<Integer, BlockingQueue<byte[]>> list =iplist.get("aaa");
            BlockingQueue<byte[]> queue = list.get(0);
            byte[] bytes11=new byte[65507];
            DatagramPacket datagramPacket=new DatagramPacket(bytes11,bytes11.length,InetAddress.getLoopbackAddress(),9000);
            while (true){
                try {
                    byte[] bytes1=null;
                    try {
                        bytes1=queue.take();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    datagramPacket.setData(bytes1);
                    try {
                        finalDatagramSocket1.send(datagramPacket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }catch (NoSuchElementException n){

                }

            }
        });
        Thread ts2=new Thread(()->{
            Map<Integer, BlockingQueue<byte[]>> list =iplist.get("aaa");
            BlockingQueue<byte[]> queue = list.get(0);
            byte[] bytes11=new byte[65507];
            DatagramPacket datagramPacket=new DatagramPacket(bytes11,bytes11.length,InetAddress.getLoopbackAddress(),9000);
            while (true){
                try {
                    byte[] bytes1=null;
                    try {
                        bytes1=queue.take();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    datagramPacket.setData(bytes1);
                    try {
                        finalDatagramSocket1.send(datagramPacket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }catch (NoSuchElementException n){

                }

            }
        });

        Thread ts4=new Thread(()->{
            Map<Integer, BlockingQueue<byte[]>> list =iplist.get("aaa");
            BlockingQueue<byte[]> queue = list.get(0);
            while (true){
                try {
                    queue.remove();
                }catch (NoSuchElementException n){

                }

            }
        });
        DatagramSocket finalDatagramSocket = datagramSocket;
        Thread t=new Thread(()->{

            while (!Thread.interrupted()){
//                long timeMillis1=System.currentTimeMillis();
//                if ((timeMillis1-timeMillis)>3000.0){break;}
//                packet.getData();
//                System.out.println("RECV:" + new String(bytes) + "    ");
                try {
                    finalDatagramSocket.receive(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int id = Integer.valueOf(new String(packet.getData(), 0, 4));//0000是LL的序号
                String s=packet.getAddress().toString()+":"+packet.getPort();
                Map<Integer, BlockingQueue<byte[]>> list =iplist.get("aaa");
                BlockingQueue<byte[]> queue = list.get(id);
                try {
                    queue.put(Arrays.copyOfRange(packet.getData(), 0, packet.getLength()));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                b.getAndIncrement();

            }
        });
        Thread tt=new Thread(()->{

            while (!Thread.interrupted()){
//                long timeMillis1=System.currentTimeMillis();
//                if ((timeMillis1-timeMillis)>3000.0){break;}
//                packet.getData();
//                System.out.println("RECV:" + new String(bytes) + "    ");
                try {
                    finalDatagramSocket.receive(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int id = Integer.valueOf(new String(packet.getData(), 0, 4));//0000是LL的序号
                String s=packet.getAddress().toString()+":"+packet.getPort();
                Map<Integer, BlockingQueue<byte[]>> list =iplist.get("aaa");
                BlockingQueue<byte[]> queue = list.get(id);
                try {
                    queue.put(Arrays.copyOfRange(packet.getData(), 0, packet.getLength()));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                b.getAndIncrement();

            }
        });

        Map<Integer, BlockingQueue<byte[]>> list =iplist.get("aaa");
        BlockingQueue<byte[]> queue = list.get(0);
        long timeMillis=System.currentTimeMillis();
int wh=0;
        while (wh<600){
            wh++;
            queue.add(bytes);
        }
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Map<Integer, BlockingQueue<byte[]>> list =iplist.get("aaa");
                BlockingQueue<byte[]> queue = list.get(0);
                byte[] bytes11=new byte[len];
                DatagramPacket datagramPacket=new DatagramPacket(bytes11,bytes11.length,InetAddress.getLoopbackAddress(),9000);
                while (true){
                    try {
                        byte[] bytes1=null;
                        try {
                            bytes1=queue.take();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        datagramPacket.setData(bytes1);
                        try {
                            finalDatagramSocket1.send(datagramPacket);
                            for (byte b:bytes1){}

                            String s=new String(bytes1)+9000+InetAddress.getLocalHost().toString();
                            //s=s+s;

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }catch (NoSuchElementException n){

                    }

                }
            }
        });

        executor.execute(new Runnable() {
            @Override
            public void run() {
                Map<Integer, BlockingQueue<byte[]>> list =iplist.get("aaa");
                BlockingQueue<byte[]> queue = list.get(0);
                byte[] bytes11=new byte[len];
                DatagramPacket datagramPacket=new DatagramPacket(bytes11,bytes11.length,InetAddress.getLoopbackAddress(),9000);
                while (true){
                    try {
                        byte[] bytes1=null;
                        try {
                            bytes1=queue.take();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        datagramPacket.setData(bytes1);
                        try {
                            finalDatagramSocket1.send(datagramPacket);
                            for (byte b:bytes1){}
                            String s="sssss";
                            //String s=new String(bytes1)+9000+InetAddress.getLocalHost().toString();
                            //s=s+s;

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }catch (NoSuchElementException n){

                    }

                }
            }
        });

        executor.execute(new Runnable() {
            @Override
            public void run() {
                Map<Integer, BlockingQueue<byte[]>> list =iplist.get("aaa");
                BlockingQueue<byte[]> queue = list.get(0);
                byte[] bytes11=new byte[len];
                DatagramPacket datagramPacket=new DatagramPacket(bytes11,bytes11.length,InetAddress.getLoopbackAddress(),9000);
                while (true){
                    try {
                        byte[] bytes1=null;
                        try {
                            bytes1=queue.take();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        datagramPacket.setData(bytes1);
                        try {
                            finalDatagramSocket1.send(datagramPacket);
                            for (byte b:bytes1){}
                            String s="sssss";
                            //String s=new String(bytes1)+9000+InetAddress.getLocalHost().toString();
                            //s=s+s;

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }catch (NoSuchElementException n){

                    }

                }
            }
        });
//        executor.execute(new Runnable() {
//            @Override
//            public void run() {
//                while (!Thread.interrupted()){
////                long timeMillis1=System.currentTimeMillis();
////                if ((timeMillis1-timeMillis)>3000.0){break;}
////                packet.getData();
////                System.out.println("RECV:" + new String(bytes) + "    ");
//                    try {
//                        finalDatagramSocket.receive(packet);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    int userid = Integer.valueOf(new String(packet.getData(), 0, 4));//0000是LL的序号
//                    String s=packet.getAddress().toString()+":"+packet.getPort();
//                    Map<Integer, BlockingQueue<byte[]>> list =iplist.get("aaa");
//                    BlockingQueue<byte[]> queue = list.get(userid);
//                    try {
//                        queue.put(Arrays.copyOfRange(packet.getData(), 0, packet.getLength()));
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//                    b.getAndIncrement();
//
//                }
//            }
//        });
        executor.execute(new Runnable() {
            @Override
            public void run() {
                while (!Thread.interrupted()){
//                long timeMillis1=System.currentTimeMillis();
//                if ((timeMillis1-timeMillis)>3000.0){break;}
//                packet.getData();
//                System.out.println("RECV:" + new String(bytes) + "    ");
                    try {
                        finalDatagramSocket.receive(packet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    int id = Integer.valueOf(new String(packet.getData(), 0, 4));//0000是LL的序号
                    String s=packet.getAddress().toString()+":"+packet.getPort();
                    Map<Integer, BlockingQueue<byte[]>> list =iplist.get("aaa");
                    BlockingQueue<byte[]> queue = list.get(id);
                   try {
                        queue.put(Arrays.copyOfRange(packet.getData(), 0, packet.getLength()));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    b.getAndIncrement();

                }
            }
        });

         int nu;

//        Thread tt=new Thread(()->{
//
//            while (true){
//                long timeMillis1=System.currentTimeMillis();
//                if ((timeMillis1-timeMillis)>3000.0){break;}
////                packet.getData();
////                System.out.println("RECV:" + new String(bytes) + "    ");
//                int userid = Integer.valueOf(new String(packet.getData(), 0, 4));//0000是LL的序号
//                String s=packet.getAddress().toString()+":"+packet.getPort();
//                Map<Integer, BlockingQueue<byte[]>> list =iplist.get("aaa");
//                BlockingQueue<byte[]> queue = list.get(userid);
//                queue.add(Arrays.copyOfRange(packet.getData(), 4, packet.getLength()));
//                try {
//                    queue.take();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                b.getAndIncrement();
//            }
//        });
//        Thread ttt=new Thread(()->{
//
//            while (true){
//                long timeMillis1=System.currentTimeMillis();
//                if ((timeMillis1-timeMillis)>3000.0){break;}
////                packet.getData();
////                System.out.println("RECV:" + new String(bytes) + "    ");
//                int userid = Integer.valueOf(new String(packet.getData(), 0, 4));//0000是LL的序号
//                String s=packet.getAddress().toString()+":"+packet.getPort();
//                Map<Integer, BlockingQueue<byte[]>> list =iplist.get("aaa");
//                BlockingQueue<byte[]> queue = list.get(userid);
//                queue.add(Arrays.copyOfRange(packet.getData(), 4, packet.getLength()));
//                try {
//                    queue.take();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                b.getAndIncrement();
//            }
//        });
//        t.start();
//        //tt.start();
//        ts.start();//ts2.start();
//        ts2.start();//ts4.start();




        while (true){
            long timeMillis1=System.currentTimeMillis();
            if ((timeMillis1-timeMillis)>3000.0){t.interrupt();break;}
        }

//        try {
//            t.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        System.out.println(b.getAndIncrement());
        //tt.start();
       // ttt.start();



    }
//
//    public Task StartCaptureAsync()
//    {
//        // 让用户选择哪个应用
//        var picker = new GraphicsCapturePicker();
//        GraphicsCaptureItem item = await picker.PickSingleItemAsync();
//
//        // 如果用户有选择一个应用那么这个属性不为空
//        if (item != null)
//        {
//            // 忽略代码
//        }
//    }
}
