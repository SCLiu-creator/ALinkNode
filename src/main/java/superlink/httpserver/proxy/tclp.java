package superlink.httpserver.proxy;

import superlink.util.Utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class tclp {
    public static void main(String[] args) throws IOException, InterruptedException {
        Socket socket=new Socket();
        socket.connect(new InetSocketAddress("127.0.0.1",8080));
        while (true){
            String s=Utils.sanc();
            if (s.equals("stop")){
                socket.getOutputStream().flush();
                socket.shutdownOutput();
            }
            socket.getOutputStream().write(s.getBytes());

        }
    }
}
