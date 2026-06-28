package superlink.httpserver.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ServerSocket;

public class ProxyTest {
    public void test() {
        // 全局代理
        System.setProperty("proxyHost", "proxy.xx.com");  // 定义代理地址
        System.setProperty("proxyPort", "8080");          // 定义代理端口号


        //System.setProperty("http.proxyHost", proxyHost);
        //System.setProperty("http.proxyPort", proxyPort);
        // 对https开启全局代理
        //System.setProperty("https.proxyHost", proxyHost);
        //System.setProperty("https.proxyPort", proxyPort);

    }

    public static void main(String[] args) {
        System.setProperty("proxyHost", "localhost");  // 定义代理地址
        System.setProperty("proxyPort", "8088");          // 定义代理端口号
        System.setProperty("http.proxyHost", "127.0.0.1");
        System.setProperty("http.proxyPort", "8088");
        Proxy proxy=new Proxy(Proxy.Type.SOCKS,new InetSocketAddress("localhost",8088));

        try {
            ServerSocket serverSocket=new ServerSocket(8088);
            InputStream outputStream=serverSocket.accept().getInputStream();
            byte[] bytes=new byte[1024];
            int len=0;
            while ((len=outputStream.read(bytes))!=-1){
                System.out.println(new String(bytes));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
