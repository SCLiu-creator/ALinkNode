package superlink.httpserver.nettyProxy;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class Datainhandler extends ChannelInboundHandlerAdapter {
    private Channel channel;

    public Datainhandler(Channel channel) {
        this.channel = channel;
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        ChanlsFactory.getCL("").build()
        // 获取读取的数据， 是一个缓冲。
        ByteBuf readBuffer = (ByteBuf) msg;
        //System.out.println("get data: " + readBuffer.toString(CharsetUtil.UTF_8));
        //这里的复位不能省略,不然会因为计数器问题报错.
//        readBuffer.retain();
        //将数据发到远程客户端那边
        channel.writeAndFlush(readBuffer);
    }
}
