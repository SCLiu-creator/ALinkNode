package superlink.httpserver.webserver;

import superlink.udpbind.client.recives.data.stream1.QSContrain;
import superlink.util.Utils;
import superlink.util.thread.SThread;
import superlink.util.thread.SThreadPool;
import superlink.util.thread.ThreadFunction;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class TcpProxyServlet {

    public static Map<String, TcpProxyServer> proxyTrasactionMap=new HashMap<>();

    public static void start(String username,int port){
        ThreadFunction function= SThread.create(()->{
            while (true){
                try {
                    Socket socket=new ServerSocket(port).accept();
                    QSContrain qsContrained=QSContrain.getInstance(username);
                    ThreadFunction function1= SThreadPool.create(()->{
                        try {
                            int len=1;
                            OutputStream outputStream=socket.getOutputStream();
                            while (len!=0){
                                byte[] bytes=qsContrained.rewiter.synread();
                                len=bytes.length;
                                outputStream.write(bytes);
                            }
                            outputStream.flush();
                            outputStream.close();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    });
                    ThreadFunction function2= SThreadPool.create(()->{
                        try {
                            int len=1;
                            InputStream inputStream=socket.getInputStream();
                            byte[] bytes=new byte[1450];
                            while ((len=inputStream.read(bytes))!=-1){
                                byte[] bytes11= Utils.subByte(bytes,0,len);
                                qsContrained.reader.synWrite(bytes11);
                            }
                            qsContrained.reader.over();
                            inputStream.close();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    });
                    SThreadPool.start(function1);
                    SThreadPool.start(function2);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });
        SThread.start(function);
        
    }
}
