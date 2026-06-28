package superlink.httpserver.nettyProxy;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Random;

public class NettyProxyInitializer extends ChannelInitializer<SocketChannel> {

    public static void newProxyServer(int port){
        Bootstrap bootstrap=new Bootstrap();
        EventLoopGroup work = new NioEventLoopGroup(2);
        bootstrap.group(work)
                .channel(NioSocketChannel.class)
                .handler(new NettyProxyInitializer());
        ChannelFuture channelFuture=null;
        ChannelFuture future=null;
        while (true){
            try {
                Random random=new Random();
                int p=random.nextInt(30000)+3000;
                channelFuture=bootstrap.bind(p);
                future= channelFuture.sync();
                break;
            }catch (Exception e){
                port++;
                continue;
            }
        }
        System.out.println("netty port : "+port);



        //等待服务端口关闭
        try {
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void newProxyClient(int port){
        ServerBootstrap bootstrap=new ServerBootstrap();
        EventLoopGroup work = new NioEventLoopGroup(2);
        bootstrap.group(work)
                .channel(NioServerSocketChannel.class)
                .childHandler(new NettyProxyInitializer());
        ChannelFuture channelFuture=null;
        ChannelFuture future=null;
        while (true){
            try {
                Random random=new Random();
                int p=random.nextInt(30000)+3000;
                channelFuture=bootstrap.bind(p);
                future= channelFuture.sync();
                break;
            }catch (Exception e){
                port++;
                continue;
            }
        }
        System.out.println("netty port : "+port);



        //等待服务端口关闭
        try {
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
    }
}
