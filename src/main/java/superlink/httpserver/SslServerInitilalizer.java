package superlink.httpserver;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.security.cert.X509Certificate;
import java.io.File;
import java.net.URI;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SslServerInitilalizer extends ChannelInitializer<SocketChannel> {

    private SslContext sslContext;
    {
        File certChainFile = new File("D:\\OpenSSL-Win64\\bin\\client\\client-cert.pem");
        File keyFile = new File("D:\\OpenSSL-Win64\\bin\\client\\client-key.pem");
        File rootFile = new File("D:\\OpenSSL-Win64\\bin\\ca\\ca-cert.pem");

        try {
            SslContext sslCtx = SslContextBuilder.forClient().keyManager(certChainFile, keyFile).trustManager(rootFile).build();
//            new SslServerInitilalizer(sslCtx);

        } catch (SSLException e) {
            e.printStackTrace();
        }

    }

    public SslServerInitilalizer(SslContext sslContext) {
        this.sslContext = sslContext;
    }

    @Override
    protected void initChannel(SocketChannel channel) {

        // 添加SSL安装验证
        channel.pipeline().addFirst(sslContext.newHandler(channel.alloc()));
        //直接解析原始HEX字节
        channel.pipeline().addLast(new StringEncoder(StandardCharsets.ISO_8859_1));
        //自定义解析数据handler
        channel.pipeline().addLast("httpServerCodec", new HttpServerCodec());
        channel.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));
        //添加自定义的ChannelHandler
        channel.pipeline().addLast("httpServerHandler", new HttpServerChannelHandler());
        channel.pipeline().addLast(new ByteArrayEncoder());
    }

    //验证客户端证书
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.pipeline().get(SslHandler.class).handshakeFuture().addListener(
                new GenericFutureListener<Future<Channel>>() {
                    @Override
                    public void operationComplete(Future<Channel> future) throws Exception {
                        if(future.isSuccess()){
                            System.out.println("握手成功");

                            SSLSession ss =  ctx.pipeline().get(SslHandler.class).engine().getSession();
                            System.out.println("cipherSuite:"+ss.getCipherSuite());
                            X509Certificate cert = ss.getPeerCertificateChain()[0];
                            String info = null;
                            // 获得证书版本
                            info = String.valueOf(cert.getVersion());
                            System.out.println("证书版本:" + info);
                            // 获得证书序列号
                            info = cert.getSerialNumber().toString(16);
                            System.out.println("证书序列号:" + info);
                            // 获得证书有效期
                            Date beforedate = cert.getNotBefore();
                            info = new SimpleDateFormat("yyyy/MM/dd").format(beforedate);
                            System.out.println("证书生效日期:" + info);
                            Date afterdate = (Date) cert.getNotAfter();
                            info = new SimpleDateFormat("yyyy/MM/dd").format(afterdate);
                            System.out.println("证书失效日期:" + info);
                            // 获得证书主体信息
                            info = cert.getSubjectDN().getName();
                            System.out.println("证书拥有者:" + info);
                            // 获得证书颁发者信息
                            info = cert.getIssuerDN().getName();
                            System.out.println("证书颁发者:" + info);
                            // 获得证书签名算法名称
                            info = cert.getSigAlgName();
                            System.out.println("证书签名算法:" + info);

                        }else{
                            System.out.println("握手失败");
                        }
                    }
                });

        SocketChannel channel = (SocketChannel) ctx.channel();
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+" conn:");
        System.out.println("IP:" + channel.localAddress().getHostString());
        System.out.println("Port:" + channel.localAddress().getPort());
    }


}
