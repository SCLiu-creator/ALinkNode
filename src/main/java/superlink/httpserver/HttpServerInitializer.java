package superlink.httpserver;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;


import java.util.List;

public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {
    public HttpServerInitializer(){
        System.out.println("init");
    }
    @Override
    protected void initChannel(SocketChannel sc) throws Exception {
        ChannelPipeline pipeline = sc.pipeline();
        //处理http消息的编解码
        pipeline.addLast("httpServerCodec", new HttpServerCodec());
        pipeline.addLast("aggregator", new HttpObjectAggregator(300 * 1024 * 1024));
        //添加自定义的ChannelHandler
        pipeline.addLast("httpServerHandler", new HttpServerChannelHandle());
        pipeline.addLast(new ChannelInboundHandlerAdapter() {
            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                // 打印异常信息
                Channel channel = ctx.channel();
                String remoteAddress = channel.remoteAddress() != null ? channel.remoteAddress().toString() : "Unknown";
                String channelId = channel.id().asShortText();

                // 打印详细信息
                System.err.println("Exception caught in channel with ID: " + channelId);
                System.err.println("Remote address: " + remoteAddress);
                System.err.println("Exception type: " + cause.getClass().getName());
                System.err.println("Exception message: " + cause.getMessage());
                cause.printStackTrace(); // 打印堆栈跟踪

                // 关闭连接
                ctx.close();
            }
        });
    }


     class CommonDecoder extends ByteToMessageDecoder {

        // CHECKSTYLE:OFF
        private int oilPort;
        private int electricPort;

        public CommonDecoder(int oilPort, int electricPort) {
            this.oilPort = oilPort;
            this.electricPort = electricPort;
        }

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {

            //拿到接收端口,分别解码
            String servicePort = ctx.channel().localAddress().toString();
            int port = Integer.parseInt(servicePort.split(":")[1]);

            String outMsg = null;
            String data = ByteBufUtil.hexDump(byteBuf);
            if(port == oilPort){
                //调用燃油车协议的拆包逻辑
                //长度小于4个字节 不处理
                if(byteBuf.readableBytes() < 4){
                    return ;
                }
                //不是固定的包头，则直接丢弃掉该数据，防止污染到后边的数据包长计算
                if(!data.startsWith("AAA")){//ConstantValue.DATA_HEAD.getCode()
                    byteBuf.readBytes(byteBuf.readableBytes());
                    return ;
                }

                //第3 4字节为包长
                int len =Integer.valueOf(data.substring(4, 8));// ConvertUtil.convert16To10(DataParseUtil.startSmallAndEndBig(data.substring(4, 8)));
                // 如果长度不够该包长，则发生了拆包
                if(data.length() < len*2){
                    return ;
                }
                //如果长度超过该包长，则发生了粘包 当普通处理 只取固定长度
                outMsg = data.substring(0,len*2);
                byteBuf.readBytes(len);

            }else if(port == electricPort){
                //调用电动协议的拆包逻辑
                //长度小于24个字节 不处理
                if(byteBuf.readableBytes() < 24){
                    return ;
                }
                //不是固定的包头，则直接丢弃掉该数据，防止污染到后边的数据包长计算
                if(!data.startsWith("BB")){//ConstantValue.ELECTRIC_DATA_HEAD.getCode()
                    byteBuf.readBytes(byteBuf.readableBytes());
                    return ;
                }

                //第22 23字节为包长
                int len = Integer.valueOf(data.substring(44,48));//ConvertUtil.convert16To10(data.substring(44, 48));
                // 如果长度不够该包长，则发生了拆包
                if(data.length() < (len+25)*2){
                    return ;
                }
                //如果长度超过该包长，则发生了粘包 当普通处理 只取固定长度
                outMsg = data.substring(0,(len+25)*2);
                byteBuf.readBytes(len+25);
            }
            if(outMsg != null){
                out.add(outMsg);
            }
            // CHECKSTYLE:ON
        }
    }

}
