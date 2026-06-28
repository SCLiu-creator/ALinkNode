package superlink.httpserver;

import com.alibaba.fastjson2.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import superlink.httpserver.servelt.ProcessMapL;
import superlink.httpserver.servelt.action.ChannelAwait;

import javax.json.JsonString;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class HttpServerChannelHandle extends SimpleChannelInboundHandler<FullHttpRequest> {
    public static String feild="";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        ctx.channel().remoteAddress();
        String pre=msg.headers().get("Accept-Encoding");
        String uri=msg.uri();
        System.out.println(ctx.channel().id()+"  "+uri);
        AtomicBoolean atomicBoolean=new AtomicBoolean(false);
        ProcessMap.interceptorList.forEach((l)->{
            try {
                Object aBoolean =  l.re(ctx,msg);
                if (aBoolean!=null){
                    atomicBoolean.set((Boolean)aBoolean||atomicBoolean.get());
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        });
        if (atomicBoolean.get()){
            ctx.channel().close();
            return;
        }
//        System.out.println("请求方法名称:" + request.method().name());
//        System.out.println("uri:" + request.uri());
        FullHttpResponse response = null;
        uri=uri.replace("/map","");
        String[] strings=uri.split("\\?",2);
        boolean b=msg.content().readableBytes()>0||strings.length>1;
        Object jsre=null;
        ProcessMapL.Nettybean nettybean=null;
        nettybean=ProcessMapL.map.get(strings[0]);
        if (nettybean==null){
            for (ProcessMapL.Nettybean n:ProcessMapL.list){
                if (strings[0].contains(n.murl)){
                    nettybean=n;
                    jsre=  nettybean.re(ctx,msg);
                    break;
                }
            }
        }else {
            jsre=  nettybean.re(ctx,msg);
        }
        if (nettybean==null){
            defaultFullHttpResponse(ctx, msg);
            return;
        }

        if (jsre==ctx){
            return;
        }
        if (jsre==null){
            if (nettybean.getReturnType().equals(Void.TYPE)){
                response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                response.headers().add(HttpHeaderNames.CONTENT_TYPE, "text/plain");
                response.headers().add(HttpHeaderNames.CONTENT_LENGTH, 0);
                ctx.writeAndFlush(response);
                return;
            }
            jsre= "{}";
        }
        if (jsre instanceof ChannelAwait || nettybean.getReturnType().equals(ChannelAwait.class)){return;}
        if(!(jsre instanceof String || jsre instanceof JsonString)){
            jsre= JSON.toJSONString(jsre);
        }

        ByteBuf byteBuf = Unpooled.copiedBuffer(jsre.toString(), CharsetUtil.UTF_8);
        response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
        response.headers().add(HttpHeaderNames.CONTENT_TYPE, "text/plain");
        response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
        ctx.writeAndFlush(response);
        return;
    }

    public Object defaultFullHttpResponse(ChannelHandlerContext ctx, FullHttpRequest msg){
        String[] strings=msg.uri().split("/");
        FullHttpResponse response = null;
        try {
            response=HttpServlet.requestUrimap.headleUri(ctx,msg,strings[1],strings[2]);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(msg.uri());
        }
        if (response==null){
            try {
                strings=msg.uri().split(feild,2);
                strings=strings[1].split("/",2);
                Map<String, ProcessMap.Nettybean> nettybeanMap=ProcessMap.mapMap.get(strings[0]);
                ProcessMap.Nettybean n=nettybeanMap.get(strings[1]);
                Object jsre=  n.re(ctx,msg);
                if (jsre==null){
                    if (n.getReturnType().equals(Void.TYPE)){
                       jsre="";
                    }else {
                        jsre= "{}";
                    }
                }
                if (jsre instanceof ChannelAwait || n.getReturnType().equals(ChannelAwait.class)){return null;}
                if(!(jsre instanceof String || jsre instanceof JsonString)){
                    jsre=JSON.toJSONString(jsre);
                }

                ByteBuf byteBuf = Unpooled.copiedBuffer(jsre.toString(), CharsetUtil.UTF_8);
                response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
                response.headers().add(HttpHeaderNames.CONTENT_TYPE, "text/plain");
                response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
                ctx.writeAndFlush(response);
                return jsre;
            }catch (Exception | Error e){
                System.out.println( e.getMessage());
                System.out.println( msg.uri());
            }
        }
        if (response==null){
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            response.headers().add(HttpHeaderNames.CONTENT_TYPE, "text/plain");
            response.headers().add(HttpHeaderNames.CONTENT_LENGTH, 0);
        }
        ctx.writeAndFlush(response);
        return "";
    }
}
