package superlink.httpserver.proxy;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.CharsetUtil;

//@ChannelHandler.Sharable//单例
//class DataHandler extends ChannelHandlerAdapter {
class DataHandler extends ChannelInboundHandlerAdapter{//SimpleChannelInboundHandler<FullHttpRequest>
    Channel channel;

    public DataHandler(Channel channel) {
        this.channel = channel;
    }

    /**
     * 业务处理逻辑
     * 用于处理读取数据请求的逻辑。
     * ctx - 上下文对象。其中包含于客户端建立连接的所有资源。 如： 对应的Channel
     * msg - 读取到的数据。 默认类型是ByteBuf，是Netty自定义的。是对ByteBuffer的封装。 因为要把读取到的数据写入另外一个通道所以必须要考虑缓冲区复位问题,不然会报错。
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 获取读取的数据， 是一个缓冲。
        ByteBuf readBuffer = (ByteBuf) msg;
        System.out.println("get data: " + readBuffer.toString(CharsetUtil.UTF_8)+"\n\n");
        //这里的复位不能省略,不然会因为计数器问题报错.
        readBuffer.retain();
        channel.writeAndFlush(readBuffer);

    }

    //@Override
//    public void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest msg) throws Exception {
//        FullHttpRequest request = msg;
//
//        System.out.println("请求方法名称:" + request.method().name());
//        System.out.println("uri:" + request.uri());
//        return;
//    }

    /**
     * 异常处理逻辑， 当客户端异常退出的时候，也会运行。
     * ChannelHandlerContext关闭，也代表当前与客户端连接的资源关闭。
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("server exceptionCaught method run...");
        channel.closeFuture().sync();
        ctx.close();
        // cause.printStackTrace();

    }
}