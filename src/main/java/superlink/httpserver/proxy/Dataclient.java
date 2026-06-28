package superlink.httpserver.proxy;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class Dataclient extends ChannelInboundHandlerAdapter {
    Channel channel;
    public Dataclient(Channel channel){
        this.channel=channel;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        System.out.println("client");
        channel.writeAndFlush(msg);
        ByteBuf readBuffer = (ByteBuf) msg;
        readBuffer.retain();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx){

    }
}
