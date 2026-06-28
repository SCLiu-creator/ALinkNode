package superlink.httpserver.proxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ttp {
    public static void main(String[] args) throws IOException, InterruptedException {
        ServerSocket serverSocket=new ServerSocket(8080);
        Socket socket=serverSocket.accept();
        byte[] bytes=new byte[1024];
        while (true){
            int len=socket.getInputStream().read(bytes);
            System.out.println("len: "+len);
            System.out.println("text: "+new String(bytes));
            Thread.sleep(2000);
        }

    }
}
