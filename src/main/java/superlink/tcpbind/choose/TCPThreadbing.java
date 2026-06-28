package superlink.tcpbind.choose;

import java.net.DatagramPacket;
import java.net.InetAddress;

public class TCPThreadbing implements Runnable{
    public InetAddress address;
    public int port;
    public TCPThreadbing(InetAddress address, int port){
        this.address=address;
        this.port=port;

    }

    @Override
    public void run(){
        String rquset=null;
        byte[] data = rquset.getBytes();//将接收到的数据变成字节数组
        DatagramPacket packet = new DatagramPacket(data, data.length, address, port);//2.创建数据报，包含发送的数据信息
    }


}
