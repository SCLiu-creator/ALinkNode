package superlink.udpbind.tcpproxy;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpProxy {


    public static void main(String[] args) throws IOException {
        byte o=0b00000011;

        byte b= (byte) (0b10000000);
        b=(byte)(b|o);
        b=(byte)(b&0b01111111);
        b= (byte) ~o;
        b= (byte) (o<<1);

        while (true){
            ServerSocket serverSocket=new ServerSocket(8088);
            Socket socket=serverSocket.accept();
            InputStream inputStream=socket.getInputStream();
            int len2;
            byte[] bytes=new byte[1024];
            StringBuilder stringBuilder=new StringBuilder();
            while ((len2 = inputStream.read(bytes)) != -1) {
                //byte[] send = new byte[len2];
                String sb=new String(bytes,0,len2);
                stringBuilder.append(sb);
                //System.arraycopy(bytes, 0, send, 0, len2);

                System.out.println(new String(bytes));//9 9 101 2

            }
            String[] strings=stringBuilder.toString().split("#");
            while (true){
                Integer integer=Integer.valueOf(strings[1]);

            }
        }
    }
}
