package superlink.httpserver.webserver;

import superlink.udpbind.client.recives.data.stream1.QSContrain;
import superlink.util.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class TcpProxyClient {

    int port = 0;
    ServerSocket serverSocket;
    Socket socket;
    QSContrain qsContraining;
    public boolean state=true;
    public TcpProxyClient(int port , QSContrain qsContraining){
        this.port=port;
        try {
            this.serverSocket=new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.qsContraining=qsContraining;

    }


    public void run() throws IOException {
        Thread.currentThread().setName("TcpProxyClient");

        while (state){
            Socket thisSocket=serverSocket.accept();
            this.socket=thisSocket;

            InputStream inputStream=thisSocket.getInputStream();
            OutputStream outputStream=thisSocket.getOutputStream();


            while (state){
                if (!thisSocket.isClosed()){
                    thisSocket=serverSocket.accept();
                    inputStream=thisSocket.getInputStream();
                    outputStream=thisSocket.getOutputStream();
                }
                System.out.println("new socket:  "+thisSocket.getPort()  );


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

                InputStream finalInputStream = inputStream;

                Thread thread1=new Thread(()->{
                    Thread.currentThread().setName("TcpProxyClient rewiter");
                    int len=0;
                    byte[] bytes=new byte[1450];
                    qsContraining.rewiter.reset();
                    while (true){
                        try {
                            if (!((len= finalInputStream.read(bytes))!=-1)) break;
                        } catch (IOException e) {
                            e.printStackTrace();
                            break;
                        }
                        byte[] bytes1= Utils.subByte(bytes,0,len);
                        System.out.println("        "+new String(bytes1) );
                        qsContraining.rewiter.synWrite(bytes1);
                    }
                    qsContraining.rewiter.over();

                });

                OutputStream finalOutputStream = outputStream;
                Socket finalSocket = thisSocket;
                Thread thread2=new Thread(()->{
                    Thread.currentThread().setName("TcpProxyClient reader");
                    int len=-1;
    //                try {
    //                    finalOutputStream.write(ss.getBytes());
    //                } catch (IOException e) {
    //                    e.printStackTrace();
    //                }
                    while (len!=5){
                        byte[] bytes2=qsContraining.reader.synread();
                        len=bytes2.length;
                        try {
                            try {
                                bytes2= Arrays.copyOfRange(bytes2,5,bytes2.length);
                            }catch (Exception range){
                                break;
                            }

                            System.out.println(new String(bytes2));
                            finalOutputStream.write(bytes2);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
//                    try {
//                        finalSocket.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                });
                qsContraining.reader.thread=thread2;
                qsContraining.rewiter.thread=thread1;
                thread1.start();
                thread2.start();
                try {
                    thread2.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;

            }
        }
    }
    public void run1() throws IOException {
        Thread.currentThread().setName("TcpProxyClient");

        while (state){
            Socket thisSocket=serverSocket.accept();
            this.socket=thisSocket;

            InputStream inputStream=thisSocket.getInputStream();
            OutputStream outputStream=thisSocket.getOutputStream();


            while (state){
                if (!thisSocket.isClosed()){
                    thisSocket=serverSocket.accept();
                    inputStream=thisSocket.getInputStream();
                    outputStream=thisSocket.getOutputStream();
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

                InputStream finalInputStream = inputStream;

                Thread thread1=new Thread(()->{
                    Thread.currentThread().setName("TcpProxyClient rewiter");
                    int len=0;
                    byte[] bytes=new byte[1450];
                    qsContraining.rewiter.reset();
                    while (true){
                        try {
                            if (!((len= finalInputStream.read(bytes))!=-1)) break;
                        } catch (IOException e) {
                            e.printStackTrace();
                            break;
                        }
                        byte[] bytes1= Utils.subByte(bytes,0,len);
                        System.out.println("        "+new String(bytes1) );
                        qsContraining.rewiter.synWrite(bytes1);
                    }
                    qsContraining.rewiter.over();

                });

                OutputStream finalOutputStream = outputStream;
                Socket finalSocket = thisSocket;
                Thread thread2=new Thread(()->{
                    Thread.currentThread().setName("TcpProxyClient reader");
                    int len=-1;
                    while (len!=5){
                        byte[] bytes2=qsContraining.reader.synread();
                        len=bytes2.length;
                        try {
                            try {
                                bytes2= Arrays.copyOfRange(bytes2,5,bytes2.length);
                            }catch (Exception range){
                                break;
                            }

                            System.out.println(new String(bytes2));
                            finalOutputStream.write(bytes2);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        finalSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                qsContraining.reader.thread=thread2;
                qsContraining.rewiter.thread=thread1;
                thread1.start();
                thread2.start();
//                try {
//                    thread2.join();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                break;
            }
        }
    }
}
