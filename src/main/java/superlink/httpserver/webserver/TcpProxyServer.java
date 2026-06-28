package superlink.httpserver.webserver;

import superlink.udpbind.client.recives.data.stream1.QSContrain;
import superlink.util.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Arrays;

public class TcpProxyServer {
    int port = 0;
    Socket socket;
    QSContrain qsContraining;
    public boolean state=true;
    public TcpProxyServer(int port , QSContrain qsContraining){
        this.port=port;
        this.socket=new Socket();
        this.qsContraining=qsContraining;

    }


    public void run() throws IOException {
        Thread.currentThread().setName("TcpProxyServer");

        SocketAddress socketAddress=new InetSocketAddress("localhost",port);
        socket.connect(socketAddress);
        InputStream inputStream=socket.getInputStream();
        OutputStream outputStream=socket.getOutputStream();

        while (state){
//            if (!socket.isConnected()){
//                socket.connect(socketAddress);
//                 inputStream=socket.getInputStream();
//                 outputStream=socket.getOutputStream();
//            }
            if (!socket.isClosed()||!socket.isConnected()){
                socket=new Socket();
                socket.connect(socketAddress);
                inputStream=socket.getInputStream();
                outputStream=socket.getOutputStream();
            }
            while (true){
                if (qsContraining==null||qsContraining.rewiter ==null){
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    break;
                }
            }


            OutputStream finalOutputStream = outputStream;
            Thread thread1=new Thread(()->{
                Thread.currentThread().setName("TcpProxyServer   reader");
                int len=-1;
                while (len!=5){
                    byte[] bytes=qsContraining.reader.synread();
                    len=bytes.length;
                    try {
                        bytes= Arrays.copyOfRange(bytes,5,bytes.length);
                        System.out.println("      "+new String(bytes));
                        finalOutputStream.write(bytes);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            InputStream finalInputStream = inputStream;
            Thread thread2=new Thread(()->{
                Thread.currentThread().setName("TcpProxyServer   rewiter");
                byte[] bytes=new byte[1450];

                qsContraining.rewiter.reset();

                int len=0;
                while (true){
                    try {
                        if (!((len= finalInputStream.read(bytes))!=-1)) break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    byte[] bytes1= Utils.subByte(bytes,0,len);
                    System.out.println(new String(bytes1));
                    qsContraining.rewiter.synWrite(bytes1);
                }
                qsContraining.rewiter.over();

            });
            qsContraining.reader.thread=thread1;
            qsContraining.rewiter.thread=thread2;
            thread1.start();
            thread2.start();
            try {
                thread1.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

    public void run1() throws IOException {
        Thread.currentThread().setName("TcpProxyServer");

        SocketAddress socketAddress=new InetSocketAddress("localhost",port);
        socket.connect(socketAddress);
        InputStream inputStream=socket.getInputStream();
        OutputStream outputStream=socket.getOutputStream();

        while (state){
//            if (!socket.isConnected()){
//                socket.connect(socketAddress);
//                 inputStream=socket.getInputStream();
//                 outputStream=socket.getOutputStream();
//            }
            if (!socket.isClosed()){
                socket=new Socket();
                socket.connect(socketAddress);
                inputStream=socket.getInputStream();
                outputStream=socket.getOutputStream();
            }
            while (true){
                if (qsContraining==null||qsContraining.rewiter ==null){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    break;
                }
            }


            OutputStream finalOutputStream = outputStream;
            Thread thread1=new Thread(()->{
                Thread.currentThread().setName("TcpProxyServer   reader");
                int len=-1;
                while (len!=5){
                    byte[] bytes=qsContraining.reader.synread();
                    len=bytes.length;
                    try {
                        bytes= Arrays.copyOfRange(bytes,5,bytes.length);
                        System.out.println("      "+new String(bytes));
                        finalOutputStream.write(bytes);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            InputStream finalInputStream = inputStream;
            Thread thread2=new Thread(()->{
                Thread.currentThread().setName("TcpProxyServer   rewiter");
                byte[] bytes=new byte[1450];

                qsContraining.rewiter.reset();

                int len=0;
                while (true){
                    try {
                        if (!((len= finalInputStream.read(bytes))!=-1)) break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    byte[] bytes1= Utils.subByte(bytes,0,len);
                    System.out.println(new String(bytes1));
                    qsContraining.rewiter.synWrite(bytes1);
                }
                qsContraining.rewiter.over();

            });
            qsContraining.reader.thread=thread1;
            qsContraining.rewiter.thread=thread2;
            thread1.start();
            thread2.start();
            try {
                thread1.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }
}
