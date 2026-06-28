package superlink.httpserver.servelt.action.post;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.*;
import superlink.httpserver.servelt.WebController;
import superlink.httpserver.servelt.action.Api;
import superlink.httpserver.servelt.action.ChannelAwait;

import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.server.ServerCon;
import superlink.util.JackJson;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

@WebController(name = "remoteService")
public class ActionRS {
    @Api(def = "postData")
    public ChannelAwait postContext(Map<String,Object> body, ChannelHandlerContext ctx){
        Object o=body.get("data");
        String user= (String) body.get("user");

        UserContext userContext=UDPclient.getUser(user);
        short id=ServerCon.getSerice(o, userContext);

        ChannelPromise promise = ctx.newPromise();
        Runnable runnable=(()-> {
            try {// 操作完成，设置Promise成功
                promise.setSuccess();
            } catch (Exception e) {// 操作失败，设置Promise失败
                promise.setFailure(e);
            }
        });

        ServerCon.UBS ubs= ServerCon.dealSerice(id, UDPclient.getUser(user));
        AtomicReference<byte[]> bytes = new AtomicReference(new byte[0]);
        ubs.runnable=()->{
            bytes.set(ubs.buferPacket.getData());
            System.out.println("returnServioce:  "+ubs.bytes);
            runnable.run();
        };
        // 添加监听器，当Promise完成时执行
        promise.addListener(f -> {
            if (f.isSuccess()) {
                // 异步操作成功，写入HTTP响应
                ByteBuf byteBuf = Unpooled.buffer(bytes.get().length);
                byteBuf.writeBytes(bytes.get());
                FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,byteBuf);
                response.headers().add(HttpHeaderNames.CONTENT_TYPE, "text/plain");
                ctx.writeAndFlush(response);
                ctx.flush();
                ctx.close();
            } else {
                // 异步操作失败，写入错误响应
                FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR);
                ctx.writeAndFlush(response);
                ctx.flush();
                ctx.close();
            }
        });
        return new ChannelAwait(){};
    }
    @Api(def = "posttest")
    public String posttest(Map<String,Object> body){
        return JackJson.toJson(body)+"10000000000";
    }
}
