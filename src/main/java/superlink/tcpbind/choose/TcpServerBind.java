package superlink.tcpbind.choose;

import com.alibaba.fastjson2.JSON;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.recives.Senders;
import superlink.udpbind.usedata.UserRequest;

import java.io.*;
import java.net.*;
import java.time.Duration;
import java.time.Instant;

public class TcpServerBind implements Runnable{
    public UserRequest userRequest=new UserRequest();
    public static ServerSocket serverSocket;

    InputStream inputStream=null;
    InputStreamReader inputStreamReader=null;
    BufferedReader bufferedReader=null;
    OutputStream outputStream=null;
    PrintWriter printWriter=null;
    public TcpServerBind(UserRequest userRequest, boolean b){
        this.userRequest.username=UDPclient.userlocal.username;
        this.userRequest.requestaddress=userRequest.requestaddress;
        this.userRequest.requestport=userRequest.requestport;
        String send="TT"+ userRequest.toString();

        Senders.Sends(userRequest.toaddress,userRequest.toport,send.getBytes());
        try {
            serverSocket=new ServerSocket(UDPclient.userlocal.inport);

        } catch (IOException e) {
            e.printStackTrace();
        }
        //启动服务器
//        TCPbing tcPbing=new TCPbing(userRequest);
//        tcPbing.start();

        Instant instant1=Instant.now();
        while (true){

            try {

                Socket socket=new Socket();
                socket.setSoTimeout(1000);
             //   System.out.println("TCP开始连接"+JSON.toJSONString(userRequest));
                SocketAddress socketAddress=new InetSocketAddress(userRequest.toaddress,userRequest.toport);
                socket.connect(socketAddress,100);
                socket.getReceiveBufferSize();
                outputStream = socket.getOutputStream();
                printWriter = new PrintWriter(outputStream);
                String requesttcp=userRequest.toString();
                printWriter.write(requesttcp);
                printWriter.flush();
                Socket socket1=serverSocket.accept();
                Thread thread=new Thread(new TcpData(socket1));
                thread.start();

                break;
            } catch (IOException e) {
                Instant instant2=Instant.now();
                long time= Duration.between(instant1,instant2).toMillis();
                long t1=instant1.toEpochMilli();
                long t2=instant2.toEpochMilli();
                time=t2-t1;
                long tt=5000;
                boolean b1=time>tt;
                if (b1){
                    System.out.println("TCP开始连接"+userRequest.toString());
                    e.printStackTrace();
                    instant1=Instant.now();
                }

            }


        }

    }

    public TcpServerBind(UserRequest userRequest){
        this.userRequest=userRequest;
        try {
            serverSocket=new ServerSocket(UDPclient.userlocal.inport,50);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //启动服务器
//        TCPbing tcPbing=new TCPbing(userRequest);
//        tcPbing.start();

        Instant instant1=Instant.now();
        while (true){
            try {

                Socket socket=new Socket();
                socket.setSoTimeout(300);
              //  System.out.println("TCP开始连接"+userRequest.toString());
                SocketAddress socketAddress=new InetSocketAddress(userRequest.toaddress,userRequest.toport);
                socket.connect(socketAddress,100);
                outputStream = socket.getOutputStream();
                printWriter = new PrintWriter(outputStream);
                String requesttcp=userRequest.toString();
                printWriter.write(requesttcp);
                printWriter.flush();
                break;
            } catch (IOException e) {
                Instant instant2=Instant.now();
                long time= Duration.between(instant1,instant2).toMillis();
                long t1=instant1.toEpochMilli();
                long t2=instant2.toEpochMilli();
                time=t2-t1;
                long tt=5000;
                boolean b1=time>tt;
                if (b1){
                    System.out.println("TCP开始连接"+userRequest.toString());
                    e.printStackTrace();
                    instant1=Instant.now();
                }
            }


        }

    }

    @Override
    public void run(){//传输流线程


        try {

                while (true) {
                    Socket revice = serverSocket.accept();
                    inputStream = revice.getInputStream();
                    inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                    bufferedReader = new BufferedReader(inputStreamReader);
                    String data = null;
                    while ((data = bufferedReader.readLine()) != null) {
                        System.out.println("我是服务器，客户端提交信息为：" + data);
                    }
                    revice.shutdownInput();//关闭输入流

                    //获取输出流，响应客户端的请求
                    outputStream = revice.getOutputStream();
                    printWriter = new PrintWriter(outputStream);
                    printWriter.write("服务器端响应成功！");
                    printWriter.flush();
                    revice.close();
                    //serverSocket.close();


                }
            } catch (IOException e) {
                e.printStackTrace();
            }







    }
}
