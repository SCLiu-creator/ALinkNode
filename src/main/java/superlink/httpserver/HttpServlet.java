package superlink.httpserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import javax.print.attribute.standard.Severity;


public class HttpServlet implements Runnable {
    public static Integer port;
    public int size;
    public HttpServlet(int port){
        HttpServlet.port=port;
    };
    public HttpServlet(int port,int size){
        this.size=size;
        HttpServlet.port=port;
    };
    public static RequestUrimap requestUrimap=new RequestUrimap();

    public void run() {
        //构造两个线程组
        EventLoopGroup mainGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(size);

        try {
            //服务端启动辅助类
            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(mainGroup, workerGroup)
//            bootstrap.group( workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new HttpServerInitializer());
            ChannelFuture channelFuture=null;
            int maxRetries = 10; // 最大重试次数
            int retryCount = 0;
            ChannelFuture future = null;

            while (retryCount < maxRetries) {
                try {
                    channelFuture=bootstrap.bind(port);
                     future= channelFuture.sync();
                    if (future.isSuccess()) {
                        System.out.println("Netty server started on port: " + port);
                        break; // 绑定成功，退出循环
                    }
                    break;
                }catch (Exception e){
                    System.err.println("Bind failed on port " + port + ", retrying... (" + (retryCount + 1) + "/" + maxRetries + ")");
                    port++;
                    retryCount++;
                    continue;
                }
            }
            System.out.println("netty port : "+port);
            Thread.currentThread().setName("netty waitClose");
            //等待服务端口关闭
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            // 优雅退出，释放线程池资源
            mainGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            System.out.println("优雅退出，释放线程池资源");
        }
    }
}
