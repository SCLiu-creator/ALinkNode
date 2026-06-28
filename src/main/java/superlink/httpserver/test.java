package superlink.httpserver;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import superlink.util.Utils;

import java.net.*;
import java.nio.channels.ServerSocketChannel;

public class test {

    class HttpClient extends ChannelInboundHandlerAdapter{

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            URI uri = new URI("/user/get");

            URLConnection urlConnection=uri.toURL().openConnection();

            FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_0, HttpMethod.GET, uri.toASCIIString());
            request.headers().add(HttpHeaderNames.CONNECTION,HttpHeaderValues.KEEP_ALIVE);
            request.headers().add(HttpHeaderNames.CONTENT_LENGTH,request.content().readableBytes());
            ctx.writeAndFlush(request);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg)
                throws Exception {
            System.out.println("msg -> " + msg);
            if (msg instanceof FullHttpResponse) {
                FullHttpResponse response = (FullHttpResponse) msg;
                ByteBuf buf = response.content();
                String result = buf.toString(CharsetUtil.UTF_8);
                System.out.println("response -> " + result);
            }

        }

    }

    public static void main(String[] args) throws Exception {
        byte[] bytes = Utils.byteMerger(Utils.intToByteArray(0),Utils.shortToByteArray((short) 678),"sadadad".getBytes(),new byte[9]);
        DatagramPacket datagramPacket= new DatagramPacket(bytes,19);
        DatagramSocket socket = new DatagramSocket(9988);
        datagramPacket.setAddress(InetAddress.getByName("192.168.123.175"));
        datagramPacket.setPort(12009);
        socket.send(datagramPacket);
        socket.send(datagramPacket);

        socket.send(datagramPacket);


    }
}
