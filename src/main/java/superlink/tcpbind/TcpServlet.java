package superlink.tcpbind;

import java.net.ServerSocket;

public class TcpServlet {
    public ServerSocket serverSocket;
    public TcpServlet(ServerSocket serverSocket){
        this.serverSocket=serverSocket;

    }
}
