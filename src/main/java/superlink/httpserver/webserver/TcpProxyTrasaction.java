package superlink.httpserver.webserver;

import superlink.udpbind.client.recives.data.stream1.QSContrain;
import superlink.util.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class TcpProxyTrasaction {
    int port = 0;
    Socket socket;
    public TcpProxyTrasaction(int port ){
        this.port=port;
        this.socket=new Socket();


    }


    public void run() throws IOException {

        SocketAddress socketAddress=new InetSocketAddress("",port);
        QSContrain qsContraining=null;

        InputStream inputStream=socket.getInputStream();
        OutputStream outputStream=socket.getOutputStream();
        socket.connect(socketAddress);
        while (true){
            int len=-1;
            while (len!=0){
                byte[] bytes=qsContraining.rewiter.synread();
                len=bytes.length;
                outputStream.write(bytes);
            }
            byte[] bytes=new byte[1450];

            qsContraining.reader.reset();
            while ((len=inputStream.read(bytes))!=-1){
                byte[] bytes1= Utils.subByte(bytes,0,len);
                qsContraining.reader.synWrite(bytes1);
            }
            qsContraining.reader.over();
        }

    }

}
