package superlink.httpserver.proxy;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.util.CharsetUtil;
import superlink.httpserver.HttpServerChannelHandler;

/**
 * @author yx
 * @date 2020/9/22  16:41
 * Description
 */
public class ProxyServer {
    ServerBootstrap serverBootstrap;
    Bootstrap bootstrap;
    EventLoopGroup bossgroup;
    EventLoopGroup workgroup;
    int serverPort = 9999;
    String remoteaddr = "127.0.0.1";
    int remotePort = 6856;

    public ProxyServer() {
        serverBootstrap = new ServerBootstrap();
        bootstrap = new Bootstrap();
        bossgroup = new NioEventLoopGroup(1);
        workgroup = new NioEventLoopGroup(1);
        serverBootstrap.group(bossgroup, workgroup);
        serverBootstrap.channel(NioServerSocketChannel.class);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(bossgroup);
        //下面的代码为缓冲区设置,其实是非必要代码,可以不用设置,也可以根据自己需求设置参数
//        serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024);
//        // SO_SNDBUF发送缓冲区，SO_RCVBUF接收缓冲区，SO_KEEPALIVE开启心跳监测（保证连接有效）
//        serverBootstrap.option(ChannelOption.SO_SNDBUF, 16*16 * 1024)
//                .option(ChannelOption.SO_RCVBUF, 16*16 * 1024)
//                .option(ChannelOption.SO_KEEPALIVE, false);
    }

    public ChannelFuture init() {
        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel cliCh) throws Exception {
                        cliCh.pipeline().addLast(new DataHandler(ch));
                    }
                });
                ChannelFuture sync = bootstrap.connect(remoteaddr, remotePort).sync();
                ch.pipeline().addLast("serverHandler",new DataHandler(sync.channel()));
//                ch.pipeline().addLast("httpServerCodec", new HttpServerCodec());
//                ch.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));
                //pipeline.addLast("httpServerHandler", new HttpServerChannelHandler());
            }
        });
        ChannelFuture future = serverBootstrap.bind(serverPort);
        return future;
    }

    public static void main(String[] args) throws InterruptedException {
        ProxyServer proxyServer = new ProxyServer();
        ChannelFuture init = proxyServer.init();
        try {
            init.sync();
            //注意这里必须写关闭channel的future为同步方法,因为只有主线程结束才会关闭他会一直阻塞在这里,不然当服务启动过后就会结束主线程,整个任务接结束了
            init.channel().closeFuture().sync();
        } finally {
            init.channel().closeFuture().sync();
        }
    }
}
