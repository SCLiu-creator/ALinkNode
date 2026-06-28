package superlink.testjava;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import superlink.filemanage.xmltool.XmlParser;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class testlock {
    public static void main(String[] args) throws IOException, DocumentException {
        String[] strings="/".split("/");
        System.out.println(strings.length);
        Map<String ,String> stringMap=new HashMap<>();
        String sm=stringMap.get("sss");
        stringMap.forEach((k,v)->{
            System.out.println(v);
        });
        for (Map.Entry entry:stringMap.entrySet()){
            System.out.println(entry);
        }

        File file=new File("synList.xml");
        Document document=new SAXReader().read(file);
        Element element=document.getRootElement();
        String ss1=element.attribute(0).getValue();
        InputStream outputStream=new FileInputStream(file);
        int io=outputStream.read();

        DatagramSocket datagramSocket1=new DatagramSocket(9000);
        DatagramSocket datagramSocket2=new DatagramSocket(8000);

        Callable callable=new ft();
        FutureTask<String> future=new FutureTask<String>(callable);
        new Thread(future).start();

        while (true){
            String s = null;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                 s=future.get(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {

            }

            if (s==null){
                    System.out.println("wait");
                    continue;
                }else {
                    System.out.println(s);
                    break;
                }


        }



        ReentrantLock lock=new ReentrantLock(true);
        AtomicInteger i=new AtomicInteger();
        byte[] bytes=new byte[10001];
        new Thread(()->{
            while (true){
                i.getAndIncrement();
                byte[] bytes1=new byte[1466];
                DatagramPacket datagram=new DatagramPacket(bytes1,bytes1.length);
                DatagramPacket datagramPacket = null;
                try {
                     datagramPacket=new DatagramPacket(bytes1,bytes1.length, InetAddress.getLocalHost(),8000);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                try {
                    datagramSocket1.receive(datagram);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(new String(datagram.getData()));
                System.out.println("int:"+i.get());
                try {
                    datagramSocket1.send(datagramPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // System.out.println("Thread" + new String(bytes).split("1"));
                }



        }).start();
        new Thread(()->{
            while (true){
                i.getAndIncrement();
                byte[] bytes1=new byte[1466];

                DatagramPacket datagram=new DatagramPacket(bytes1,bytes1.length);
                DatagramPacket datagramPacket = null;
                try {
                     datagramPacket=new DatagramPacket(bytes1,bytes1.length, InetAddress.getLocalHost(),9000);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }   try {
                    datagramSocket2.send(datagramPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    datagramSocket2.receive(datagram);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(new String(datagram.getData()));
                System.out.println("int:"+i.get());


                // System.out.println("Thread" + new String(bytes).split("1"));
            }

                //    System.out.println("Thread1" + new String(bytes).split("1"));

        }).start();
        new Thread(()->{
            while (true){
                synchronized (lock) {
                    for (int j = 9000; j < 10000; j++) {
                        int l = j - 9000 + 48;
                        bytes[j] = (byte) l;
                    }

                 //   System.out.println("Thread2" + new String(bytes).split("1"));
                }
            }

        });
//        while (true){
//
//            while(true){
//                i.getAndIncrement();
//               // t.run();
//                synchronized (lock) {
//                    for (int j = 9000; j < 10000; j++) {
//                        int l = j - 9000 + 30;
//                        bytes[j] = (byte) l;
//                    }
//                    System.out.println("while" + new String(bytes).split("1") + "     " + i.get());
//                }
//            }
//        }
    }

    public static class ft implements Callable<String>{

        @Override
        public String call() throws Exception {
            Thread.sleep(10000);
            return "succed";
        }
    }
}
