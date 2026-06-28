package superlink.tcpbind;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.*;
import java.net.Socket;

public class NettyDualModeExample {
    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            int port = 9000;

            // 创建服务器端的Bootstrap对象
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            // 配置服务器端的ChannelPipeline
                            ChannelPipeline pipeline = ch.pipeline();
                            // 添加业务处理逻辑
                            pipeline.addLast(new ServerHandler());
                        }
                    });

            // 绑定服务器端口并开始监听
            ChannelFuture serverFuture = serverBootstrap.bind(port).sync();
            System.out.println("Server started on port " + port);

            // 创建客户端的Bootstrap对象
            Bootstrap clientBootstrap = new Bootstrap();
//            clientBootstrap.bind("127.0.0.1",port);
//            clientBootstrap.connect("121.36.11.172",8800);
            clientBootstrap.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            // 配置客户端的ChannelPipeline
                            ChannelPipeline pipeline = ch.pipeline();
                            // 添加业务处理逻辑
                            pipeline.addLast(new ClientHandler());
                        }
                    });

            // 发起主动连接
            String serverHost = "localhost";
            ChannelFuture clientFuture = clientBootstrap.connect(serverHost, port).sync();
            System.out.println("Client connected to " + serverHost + ":" + port);

            // 等待服务器和客户端连接关闭
            serverFuture.channel().closeFuture().sync();
            clientFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    // 服务器端业务处理逻辑
    static class ServerHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            // 处理接收到的数据
            System.out.println("Received from client: " + msg);

        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }
    }

    // 客户端业务处理逻辑
    static class ClientHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            // 发送数据给服务器端
            String message = "Hello from client";
            ctx.writeAndFlush(message);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            // 处理接收到的数据
            System.out.println("Received from server: " + msg);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }
    }


    // 处理连接请求
    private static void handleConnection(Socket socket) {
        try {
            // 获取输入流和输出流
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();

            // 处理读取和写入操作
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            PrintWriter writer = new PrintWriter(outputStream, true);

            // 读取数据
            String receivedData = reader.readLine();
            System.out.println("Received from client: " + receivedData);

            // 发送数据
            String responseData = "Hello from server";
            writer.println(responseData);
            System.out.println("Sent to client: " + responseData);

            // 关闭连接
            socket.close();
        } catch ( IOException e) {
            e.printStackTrace();
        }
    }

    // 发起客户端连接
    private static void startClient(String host, int port) {
        new Thread(() -> {
            try {
                // 创建客户端Socket并连接服务器端
                Socket clientSocket = new Socket(host, port);
                System.out.println("Client connected to " + host + ":" + port);

                // 获取输入流和输出流
                InputStream inputStream = clientSocket.getInputStream();
                OutputStream outputStream = clientSocket.getOutputStream();

                // 处理读取和写入操作
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                PrintWriter writer = new PrintWriter(outputStream, true);

                // 发送数据
                String requestData = "Hello from client";
                writer.println(requestData);
                System.out.println("Sent to server: " + requestData);

                // 读取数据
                String responseData = reader.readLine();
                System.out.println("Received from server: " + responseData);

                // 关闭连接
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
