package superlink.udpbind.client;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpThreadBind implements Runnable{

    public int port;
    public InetAddress address;

    public HttpThreadBind(InetAddress address,int port){
        this.address=address;
        this.port=port;

    }

    @Override
    public void run(){
        ServerSocket server = null;
        try {
            server = new ServerSocket(port,100,address);

        while(true){
            Socket socket = server.accept();
            DataInputStream datain=new DataInputStream(socket.getInputStream());
            byte[] bytes=new byte[3000];
            datain.read(bytes);
            System.out.println(new String(bytes));

            InputStreamReader r = new InputStreamReader(socket.getInputStream());
            BufferedReader br = new BufferedReader(r);
//            String readLine = br.readLine();
//            while(readLine != null && !readLine.equals("")){
//                System.out.println("获取到数据：" + readLine);
//                readLine = br.readLine();
//            }

            String html = "http/1.1 200 ok\n"
                    +"\n\n"
                    +"1234服务端。。。。";
            String s="HTTP/1.1 200 ok"+"\r"+"\n"+
                    "Host: localhost:8888"+"\r"+"\n"+
                    "Transfer-Encoding: chunked"+"\r"+"\n"+
                    "Connection:keep - alive"+"\r"+"\n"+
                    "Cache - Control:max - age = 0"+"\r"+"\n"+
                    "sec - ch - ua:"+"Microsoft Edge;"+"v = 105,"+"\r"+"\n"+
                    "sec - ch - ua - mobile: ?0"+"\r"+"\n"+
                    "sec - ch - ua - platform:"+"Windows"+"\r"+"\n"+
                    "User - Agent:Mozilla / 5.0 (Windows NT 10.0; Win64; x64)+AppleWebKit / 537.36 (KHTML, like Gecko)Chrome / 105.0 .0 .0 Safari / 537.36 Edg / 105.0 .1343 .33"+"\r"+"\n"+
                    "Accept:text / html, application / xhtml + xml, application / xml;+ q = 0.9, image / webp, image / apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9"+"\r"+"\n"+
                    "Accept-Encoding: gzip, deflate, br"+"\r"+"\n"+
                    "Accept-Language: zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6"+"\r"+"\n"+"\r"+"\n"+
                    //60+"\r"+"\n"+
                    "<!DOCTYPE html>"+"\r"+"\n"+
                    "<html><title>aaaaaaaa</title></html>"+
                    "\r"+"\n"+"\r"+"\n0"+"\r"+"\n"+"nnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnbbbbbbbbbbbbbbbbbbbbbdddddddddddddddddd";;

            String bod= "<!DOCTYPE html>"+//"\r"+"\n"+
                    "<html>" +
                    "<head>"+
                    "<title>aaaaaaaa</title>" +
                    "</head>"+
                    "<body>" +
                    "<select>" +
                    "<option value=\"1\">op1</option>" +
                    "<option value=\"2\">op2</option>" +
                    "</select>" +
                    "</body>"+
                    "</html>";
            String ss="HTTP/1.1 200 ok"+"\r"+"\n"+
                    "Content-Length: "+bod.getBytes().length+"\r\n"+
                    "Content-Type: text/html; charset=UTF-8"+"\r\n"+"\r\n"+
                    bod;

            //流结束符\r\n0\r\n
            //chunked

            DataOutputStream dataou=new DataOutputStream(socket.getOutputStream());
            //dataou.write();
            socket.getOutputStream().write(s.getBytes());
            BufferedOutputStream bufferedOutputStream=new BufferedOutputStream(socket.getOutputStream());
//            ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
//            BufferedOutputStream bufferedOutputStream=new BufferedOutputStream(socket.getOutputStream());

//            PrintWriter pw = new PrintWriter(socket.getOutputStream());
//            pw.println(s);
//            pw.flush();


            }
        } catch (IOException e) {
        e.printStackTrace();
    }
}
}
