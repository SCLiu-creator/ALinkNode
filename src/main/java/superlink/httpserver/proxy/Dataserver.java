package superlink.httpserver.proxy;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import javax.xml.crypto.Data;


public class Dataserver extends ChannelInboundHandlerAdapter {
    Channel channel;
    public Dataserver(Channel channel){
        this.channel=channel;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx,Object msg){
        System.out.println("server");
        channel.writeAndFlush(msg);
        ByteBuf readBuffer = (ByteBuf) msg;
        //readBuffer.retain();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx){
       // channel.writeAndFlush(ctx.channel());
    }
}
