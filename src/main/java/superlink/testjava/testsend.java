package superlink.testjava;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class testsend {
    public static class con{
        public byte[] bytes;

        public void setBytes(byte[] bytes) {
            this.bytes = bytes;
        }
    }
    public static void main(String[] args) throws IOException {
        String http =
                "POST /map/SelfPage/UpBackPic HTTP/1.1" + "\r" + "\n" +
                        "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7" + "\r" + "\n" +
                        "Accept-Language: zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6" + "\r" + "\n" +
                        "Host: localhost:7860"+"\r" + "\n" +
                        "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36 Edg/116.0.1938.81" + "\r" + "\n" +
                        "Content-Type: multipart/form-data;"+ "\r" + "\n" +
                        "Content-Length: 100000"+ "\r" + "\n" +
                        "Content-Disposition: form-data; "+ "\r" + "\n" +
//        "name="file"; filename="example.png""
                        "  Content-Type: image/png"+ "\r" + "\n" +

                        "sec-ch-ua-platform: \"Windows\"" + "\r" + "\n" + "\r" + "\n";
//                        "12333234242434353543"+"\r" + "\n" +"0"+ "\r" + "\n";
        http=new String(http.getBytes(),"ISO-8859-1");
        Socket socket0h=new Socket();
        socket0h.connect(new InetSocketAddress(InetAddress.getLocalHost(),8004));
        OutputStream outputStreamth=socket0h.getOutputStream();

        for (int i = 0; i <100001 ; i++) {
            http=http+"1";
        }
        outputStreamth.write(http.getBytes());
        ExecutorService executorService= Executors.newScheduledThreadPool(8);
        byte[] bytes1=new byte[1024];
        byte[] bytes2=new byte[1024];
        AtomicInteger i1=new AtomicInteger(0);
        AtomicInteger i2=new AtomicInteger(0);
        BlockingQueue blockingQueue=new LinkedBlockingQueue<byte[]>();//300namiao
        ConcurrentLinkedQueue queue=new ConcurrentLinkedQueue<byte[]>();
        con co=new con();
        executorService.execute(()->{
            long t1=System.currentTimeMillis();
            while (i2.get()<30000){
                i2.getAndIncrement();
              //  blockingQueue.add(bytes1);
                queue.add(bytes1);
                co.setBytes(bytes1);
            }
            long t2=System.currentTimeMillis();
            System.out.println("2:"+(t2-t1));
        });
        executorService.execute(()->{
            long t1=System.currentTimeMillis();
            while (i1.get()<30000){
                i1.getAndIncrement();
                System.arraycopy(bytes1,0,bytes2,0,1024);
                //100纳秒
            }
            long t2=System.currentTimeMillis();
            System.out.println("1:"+(t2-t1));

        });







        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        DatagramSocket socket = null;
        try {
             socket=new DatagramSocket(9001);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        DatagramChannel channel = null;
        SocketAddress inetAddress= null;
        SocketAddress inetAddress1= null;
        try {
            inetAddress = new InetSocketAddress(InetAddress.getLocalHost(),9000);
            inetAddress1= new InetSocketAddress(InetAddress.getLocalHost(),9001);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try {
            channel=DatagramChannel.open();channel.bind(inetAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }

        AtomicInteger a=new AtomicInteger(0);
        DatagramSocket finalSocket = socket;
        DatagramChannel finalChannel = channel;
        SocketAddress finalInetAddress = inetAddress1;
        Thread ts=new Thread(()->{

            byte[] bytes11=new byte[65507];
            ByteBuffer byteBuffer=ByteBuffer.allocate(65507);
            byteBuffer.put(new byte[65507]);
            DatagramPacket datagramPacket=new DatagramPacket(bytes11,bytes11.length,InetAddress.getLoopbackAddress(),9901);
            while (true){
                try {
                //    finalSocket.send(datagramPacket);
                    finalChannel.send(byteBuffer, finalInetAddress);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                a.getAndIncrement();
            }
        });
        DatagramSocket finalSocket1 = socket;
        Thread ts1=new Thread(()->{

            byte[] bytes11=new byte[65507];
            ByteBuffer byteBuffer=ByteBuffer.allocate(65507);
            byteBuffer.put(new byte[65507]);
            DatagramPacket datagramPacket=new DatagramPacket(bytes11,bytes11.length,InetAddress.getLoopbackAddress(),9901);
            while (true){
                try {
                    //    finalSocket.send(datagramPacket);
                    finalSocket1.send(datagramPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                a.getAndIncrement();
            }
        });

        AtomicLong timeMillis= new AtomicLong(System.currentTimeMillis());
        AtomicInteger b=new AtomicInteger(0);
        Thread t=new Thread(()->{
            DatagramPacket packet=new DatagramPacket(new byte[65507],65507);
            timeMillis.set(System.currentTimeMillis());
            while (true){
                long timeMillis1=System.currentTimeMillis();
                if ((timeMillis1- timeMillis.get())>10000.0){break;}
                try {
                    finalSocket1.receive(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                b.getAndIncrement();
            }
            System.out.println(b.get()+":"+a.get());
        });
        Thread t1=new Thread(()->{
            DatagramPacket packet=new DatagramPacket(new byte[65507],65507);
            while (!Thread.interrupted()){
                long timeMillis1=System.currentTimeMillis();

                if ((timeMillis1- timeMillis.get())>10000.0){break;}
//                packet.getData();
//                System.out.println("RECV:" + new String(bytes) + "    ");
                try {
                    finalSocket1.receive(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                b.getAndIncrement();

            }
            System.out.println(b.get()+":"+a.get());
        });
        executorService.execute(t);
        executorService.execute(ts);
////        executorService.execute(t1);
////        executorService.execute(t1);
//        executorService.execute(t1);
        executorService.execute(ts1);
//        t.start();
//        ts.start();
//        t1.start();
//        ts1.start();



    ServerSocket serverSocket = null;


        try {
            serverSocket = new ServerSocket(9000);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ServerSocket finalServerSocket = serverSocket;
        Thread thread=new Thread(()->{
        try {
            while (true){
                Socket socket1= finalServerSocket.accept();
                byte[] bytes=new byte[640000];
                new Thread(()->{
                        try {
                            InputStream tputStream = socket1.getInputStream();
                            while (tputStream.read(bytes) != -1){
                               // b.getAndIncrement();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
        } catch (IOException e) {
            e.printStackTrace();
        }
    });
    long timeMilliss = System.currentTimeMillis();
    AtomicInteger bb=new AtomicInteger(0);
    Thread thread1=new Thread(()->{
        try {

            Socket socket2=new Socket();
            socket2.connect(new InetSocketAddress(InetAddress.getLocalHost(),9000));
            OutputStream tputStream;
            byte[] bytes=new byte[1024];
            while (true){
                long timeMillis1=System.currentTimeMillis();
                if ((timeMillis1-timeMilliss)>10000.0){break;}
                    tputStream = socket2.getOutputStream();
                    tputStream.write(bytes);
                    bb.getAndIncrement();
        }
            System.out.println(bb.get());
        } catch (IOException e) {
            e.printStackTrace();
        }

    });
        Thread thread2=new Thread(()->{
            try {

                Socket socket2=new Socket();
                socket2.connect(new InetSocketAddress(InetAddress.getLocalHost(),9000));
                OutputStream tputStream;
                byte[] bytes=new byte[1024];
                while (true){
                    long timeMillis1=System.currentTimeMillis();
                    if ((timeMillis1-timeMilliss)>10000.0){break;}
                    tputStream = socket2.getOutputStream();
                    tputStream.write(bytes);
                    bb.getAndIncrement();
                }
                System.out.println(bb.get());
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
        Thread thread3=new Thread(()->{
            try {

                Socket socket2=new Socket();
                socket2.connect(new InetSocketAddress(InetAddress.getLocalHost(),9000));
                OutputStream tputStream;
                byte[] bytes=new byte[1024];
                while (true){
                    long timeMillis1=System.currentTimeMillis();
                    if ((timeMillis1-timeMilliss)>10000.0){break;}
                    tputStream = socket2.getOutputStream();
                    tputStream.write(bytes);
                    bb.getAndIncrement();
                }
                System.out.println(bb.get());
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
        Thread thread4=new Thread(()->{
            try {

                Socket socket2=new Socket();
                socket2.connect(new InetSocketAddress(InetAddress.getLocalHost(),9000));
                OutputStream tputStream;
                byte[] bytes=new byte[1024];
                while (true){
                    long timeMillis1=System.currentTimeMillis();
                    if ((timeMillis1-timeMilliss)>10000.0){break;}
                    tputStream = socket2.getOutputStream();
                    tputStream.write(bytes);
                    bb.getAndIncrement();
                }
                System.out.println(bb.get());
            } catch (IOException e) {
                e.printStackTrace();
            }

        });


//    thread1.start();
//    thread2.start();
//     //   thread3.start();//thread4.start();
//
//
//    thread.start();
    }

}
