package superlink.testjava;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;


public class TcpProxy {
    public void s() throws IOException {
        SocketAddress socketAddress=new InetSocketAddress(InetAddress.getLocalHost(),8000);
        SocketChannel socketChannel1=SocketChannel.open();
        socketChannel1.socket().getInputStream();
        SocketAddress socketAddress1=new InetSocketAddress(InetAddress.getLocalHost(),8001);
        SocketChannel socketChannel2=SocketChannel.open();
    }
}
