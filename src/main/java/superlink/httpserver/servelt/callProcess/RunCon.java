package superlink.httpserver.servelt.callProcess;

import com.alibaba.fastjson2.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.*;

import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import superlink.httpserver.servelt.action.ChannelAwait;

import javax.json.JsonString;
import java.util.concurrent.*;

public class RunCon implements Runnable{
    public ChannelHandlerContext ctx;
    Callable task;
    Runnable end;
    ChannelPromise promise;
    Object o;
    public RunCon(ChannelHandlerContext ctx){
        this.ctx=ctx;
        promise = ctx.newPromise();
        // 添加监听器，当Promise完成时执行

//        GenericFutureListener future=new GenericFutureListener() {
//            if (future.isSuccess()) {
//                // 异步操作成功，写入HTTP响应
//                FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
//                response.headers().add(HttpHeaderNames.CONTENT_TYPE, "text/plain");
//                ctx.writeAndFlush(response);
//                ctx.flush();
//                ctx.close();
//            } else {
//                // 异步操作失败，写入错误响应
//                FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR);
//                ctx.writeAndFlush(response);
//                ctx.flush();
//                ctx.close();
//            }
//        }

        promise.addListener(f -> {
            if (end==null){
                if (o==null){
                    if (f.isSuccess()) {
                        // 异步操作成功，写入HTTP响应
                        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
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
                }else {
                    if (o==ctx){
                        return;
                    }
                    if (o==null){
                        o= "{}";}
                    if(!(o instanceof String || o instanceof JsonString)){
                        o= JSON.toJSONString(o);
                    }

                    ByteBuf byteBuf = Unpooled.copiedBuffer(o.toString(), CharsetUtil.UTF_8);
                    if (f.isSuccess()) {
                        // 异步操作成功，写入HTTP响应
                        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,byteBuf);
                        response.headers().add(HttpHeaderNames.CONTENT_TYPE, "text/plain");
                        response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
                        ctx.writeAndFlush(response);
                        ctx.flush();
                        ctx.close();
                    } else {
                        // 异步操作失败，写入错误响应
                        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR,byteBuf);
                        response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
                        ctx.writeAndFlush(response);
                        ctx.flush();
                        ctx.close();
                    }
                }
            }else {
                end.run();
            }
        });
    }

    public void setTask(Callable task) {
        this.task = task;
    }

    public void setEnd(Runnable end) {
        this.end = end;
    }

    public Object getO() {
        return o;
    }

    public void setO(Object o) {
        this.o = o;
    }

    @Override
    public void run() {
        try {
            Object o=task.call();
            if (o!=null){
                setO(o);
            }
            try {// 操作完成，设置Promise成功
                promise.setSuccess();
            } catch (Exception e) {// 操作失败，设置Promise失败
                promise.setFailure(e);
            }
        }catch (Error | Exception e){
            e.printStackTrace();
        }
    }
}
