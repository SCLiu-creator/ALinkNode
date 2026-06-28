package superlink.httpserver;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerUpgradeHandler;
import io.netty.handler.codec.http2.*;

import io.netty.handler.ssl.ApplicationProtocolNames;
import io.netty.handler.ssl.SslContext;

public class Http2ServerInitializer extends ChannelInitializer<SocketChannel> {
    private final SslContext sslContext;

    public Http2ServerInitializer(SslContext sslContext) {
        this.sslContext = sslContext;
        System.out.println("init");
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // 添加 SSL 支持（如果需要的话）
        if (sslContext != null) {
            pipeline.addLast(sslContext.newHandler(ch.alloc()));
        }

        // 处理协议升级从 HTTP/1.1 到 HTTP/2
//        final Http2ServerUpgradeHandler upgradeHandler = new Http2ServerUpgradeHandler(
//                new HttpServerCodec(),
//                new HttpServerUpgradeHandler(new Http2ServerUpgradeCodec()),
//                new Http2Settings());
//
//        CleartextHttp2ServerUpgradeHandler cleartextHttp2ServerUpgradeHandler=
//                new CleartextHttp2ServerUpgradeHandler(new HttpServerCodec(),
//                        new HttpServerUpgradeHandler( new HttpServerCodec,new Http2ServerUpgradeCodec()),
//                        )
//
//        pipeline.addLast(upgradeHandler);
//        pipeline.addLast(new Http2FrameCodecBuilder(true).build());
//        pipeline.addLast(new Http2StreamFrameHandler(new YourHttp2FrameListener()));
    }

    // 实现 Http2FrameListener 接口来处理 HTTP/2 帧
    private static class YourHttp2FrameListener implements Http2FrameListener {
        @Override
        public int onDataRead(ChannelHandlerContext ctx, int streamId, ByteBuf data, int padding, boolean endOfStream) throws Http2Exception {
            return 0;
        }

        @Override
        public void onHeadersRead(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int padding, boolean endOfStream) throws Http2Exception {

        }

        @Override
        public void onHeadersRead(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int streamDependency, short weight, boolean exclusive, int padding, boolean endOfStream) throws Http2Exception {

        }

        @Override
        public void onPriorityRead(ChannelHandlerContext ctx, int streamId, int streamDependency, short weight, boolean exclusive) throws Http2Exception {

        }

        @Override
        public void onRstStreamRead(ChannelHandlerContext ctx, int streamId, long errorCode) throws Http2Exception {

        }

        @Override
        public void onSettingsAckRead(ChannelHandlerContext ctx) throws Http2Exception {

        }

        @Override
        public void onSettingsRead(ChannelHandlerContext ctx, Http2Settings settings) throws Http2Exception {

        }

        @Override
        public void onPingRead(ChannelHandlerContext ctx, long data) throws Http2Exception {

        }

        @Override
        public void onPingAckRead(ChannelHandlerContext ctx, long data) throws Http2Exception {

        }

        @Override
        public void onPushPromiseRead(ChannelHandlerContext ctx, int streamId, int promisedStreamId, Http2Headers headers, int padding) throws Http2Exception {

        }

        @Override
        public void onGoAwayRead(ChannelHandlerContext ctx, int lastStreamId, long errorCode, ByteBuf debugData) throws Http2Exception {

        }

        @Override
        public void onWindowUpdateRead(ChannelHandlerContext ctx, int streamId, int windowSizeIncrement) throws Http2Exception {

        }

        @Override
        public void onUnknownFrame(ChannelHandlerContext ctx, byte frameType, int streamId, Http2Flags flags, ByteBuf payload) throws Http2Exception {

        }
        // 实现 Http2FrameListener 的方法来处理不同类型的帧
        // 例如，onHeadersRead, onDataRead, onPingRead 等。
    }
}
