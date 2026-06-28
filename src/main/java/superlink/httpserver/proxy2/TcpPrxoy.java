package superlink.httpserver.proxy2;


    import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

    public class TcpPrxoy {

        private Bootstrap bootstrap;
        private ServerBootstrap server;

        private NioEventLoopGroup bossgroup;
        private NioEventLoopGroup workgroup;

        public void init() {
            this.bossgroup = new NioEventLoopGroup();
            this.workgroup = new NioEventLoopGroup();
            this.server = new ServerBootstrap();
            this.bootstrap = new Bootstrap();
            this.bootstrap.channel(NioSocketChannel.class);
            this.bootstrap.group(bossgroup);
            this.server.group(bossgroup, workgroup);
            this.server.channel(NioServerSocketChannel.class)
                    //处理打印日志
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //服务端channel，将服务端的数据发送给客户端，所以构造函数参数要传入客户端的channel
                            ch.pipeline().addLast("serverHandler", new DataHandle(getClientChannel(ch)));
                        }
                    });//.option(ChannelOption.SO_BACKLOG, 1024)
                    // SO_SNDBUF发送缓冲区，SO_RCVBUF接收缓冲区，SO_KEEPALIVE开启心跳监测（保证连接有效）
//                    .option(ChannelOption.SO_SNDBUF, 16 * 1024)
//                    .option(ChannelOption.SO_RCVBUF, 16 * 1024)
//                    .option(ChannelOption.SO_KEEPALIVE, true);
            server.bind(9999).syncUninterruptibly()
                    .addListener((ChannelFutureListener) channelFuture -> {
                        if (channelFuture.isSuccess()) {
                            System.out.println("服务器启动成功");
                        } else {
                            System.out.println("服务器启动失败");
                        }
                    });
        }

        private Channel getClientChannel(SocketChannel ch) throws InterruptedException {
            this.bootstrap
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            //客户端端channel，客户端返回的数据给服务端，所以构造函数参数要传入服务端的channel
                            socketChannel.pipeline().addLast("clientHandler", new DataHandle(ch));
                        }
                    });
            ChannelFuture sync = bootstrap.connect("127.0.0.1", 3000).sync();
            return sync.channel();
        }


        public static void main(String[] args) {
            TcpPrxoy tcpServer = new TcpPrxoy();
            tcpServer.init();
        }

}
