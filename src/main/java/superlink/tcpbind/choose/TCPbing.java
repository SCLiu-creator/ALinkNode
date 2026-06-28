package superlink.tcpbind.choose;

import superlink.udpbind.usedata.UserRequest;

import java.io.IOException;
import java.net.Socket;

public class TCPbing extends Thread{
    public UserRequest userRequest;
    public TCPbing(UserRequest userRequest){
        this.userRequest=userRequest;
    }
    @Override
    public void run(){
        try {
            while (true){
            Socket socket=TcpServerBind.serverSocket.accept();

//            Thread thread=new Thread(new TcpData(socket));
//            thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
