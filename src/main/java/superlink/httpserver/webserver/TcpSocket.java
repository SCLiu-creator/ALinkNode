package superlink.httpserver.webserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.CompletableFuture;

public class TcpSocket {
    String ip;
    int port;

    public TcpSocket set(String ip,int port){
        this.ip=ip;
        this.port=port;
        return this;
    }

    public void lisent(){
        try {
            InetSocketAddress inetSocketAddress=new InetSocketAddress(ip,port);
            ServerSocket serverSocket=new ServerSocket(port);
            //serverSocket.accept();
            new Socket().getChannel().connect(inetSocketAddress);
            //CompletableFuture.supplyAsync().thenAccept().thenAccept().thenApply().thenRun().get()
            SocketChannel socket=serverSocket.getChannel().accept();
            ByteBuffer byteBuffer=ByteBuffer.allocateDirect(1024);
            socket.read(byteBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void main(String[] args) {
        new TcpSocket().set("",8008).lisent();
    }
}
