package superlink.httpserver;


import com.alibaba.fastjson2.JSON;
import superlink.httpserver.servelt.action.ChannelAwait;
import superlink.httpserver.servelt.httptype.ContentType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import superlink.init.InitClass;
import superlink.util.Utils;

import javax.json.JsonString;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class HttpServerChannelHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    public static String feild="";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {

        ctx.channel().remoteAddress();
        FullHttpRequest request = msg;
        String pre=msg.headers().get("Accept-Encoding");

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
        if ("GET".equals(request.method().name())){
            String[] stringuri=request.uri().split("\\?",0);
            String geturi=stringuri[0];
            String[] strings=geturi.split("/");
            if (strings.length==0){
                String s = InitClass.webpath+ "index.html";
                File file = new File(s);
                String[] u = request.uri().split("\\.");
                String prex = u[u.length - 1];
                FileInputStream fileInputStream = new FileInputStream(file);
                BufferedInputStream inputStream = new BufferedInputStream(fileInputStream);
                byte[] bytes = new byte[1024];
                ByteBuf byteBuf = Unpooled.buffer();
                int len;
                while ((len = inputStream.read(bytes)) != -1) {
                    bytes = Utils.subByte(bytes, 0, len);
                    byteBuf.writeBytes(bytes);
                }
                response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
                response.headers().add(HttpHeaderNames.CONTENT_TYPE, ContentType.valueOf("html").Type());//"text/html;charset=utf-8"
                response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
                inputStream.close();
                fileInputStream.close();
//                ByteBuf buf = request.content();
//                System.out.print(buf.toString(CharsetUtil.UTF_8));
//                ByteBuf byteBuf = Unpooled.copiedBuffer("hello world", CharsetUtil.UTF_8);
//                response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
//                response.headers().add(HttpHeaderNames.CONTENT_TYPE, "text/plain");
//                response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
                ctx.writeAndFlush(response);
                return;
            }
            if (strings[1].equals("map")){
                // http://localhost:7464/map/controller/methon
                Map<String, ProcessMap.Nettybean> nettybeanMap=ProcessMap.mapMap.get(strings[2]);
                ProcessMap.Nettybean n=nettybeanMap.get(strings[3]);
                Object jsre=  n.re(ctx,msg);
                if (jsre==ctx){
                    return;
                }
                if (jsre==null){
                    if (n.getReturnType().equals(Void.TYPE)){
                        response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                        response.headers().add(HttpHeaderNames.CONTENT_TYPE, "text/plain");
                        response.headers().add(HttpHeaderNames.CONTENT_LENGTH, 0);
                        ctx.writeAndFlush(response);
                        return;
                    }
                    jsre= "{}";}
                if (jsre instanceof ChannelAwait || n.getReturnType().equals(ChannelAwait.class)){return;}
                if(!(jsre instanceof String || jsre instanceof JsonString)){
                    jsre=JSON.toJSONString(jsre);
                }

                ByteBuf byteBuf = Unpooled.copiedBuffer(jsre.toString(), CharsetUtil.UTF_8);
                response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
                response.headers().add(HttpHeaderNames.CONTENT_TYPE, "text/plain");
                response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
                ctx.writeAndFlush(response);
                return;
            }else if (strings.length>=2){
                try {
                    response=HttpServlet.requestUrimap.headleUri(ctx,msg,strings[1],strings[2]);
                }catch (Exception e){
                    e.printStackTrace();
                    System.out.println(request.uri());
                }
                if (response==null){
                    try {
                        strings=request.uri().split(feild,2);
                        strings=strings[1].split("/",2);
                        Map<String, ProcessMap.Nettybean> nettybeanMap=ProcessMap.mapMap.get(strings[0]);
                        ProcessMap.Nettybean n=nettybeanMap.get(strings[1]);
                        Object jsre=  n.re(ctx,msg);
                        if (jsre==null){
                            if (n.getReturnType().equals(Void.TYPE)){
                                return;
                            }
                            jsre= "{}";}
                        if (jsre instanceof ChannelAwait || n.getReturnType().equals(ChannelAwait.class)){return;}
                        if(!(jsre instanceof String || jsre instanceof JsonString)){
                            jsre=JSON.toJSONString(jsre);
                        }

                        ByteBuf byteBuf = Unpooled.copiedBuffer(jsre.toString(), CharsetUtil.UTF_8);
                        response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
                        response.headers().add(HttpHeaderNames.CONTENT_TYPE, "text/plain");
                        response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
                        ctx.writeAndFlush(response);
                        return;
                    }catch (Exception | Error e){
                        System.out.println( e.getMessage());
                        System.out.println( request.uri());
                    }
                }
                if (response==null){
                    response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                    response.headers().add(HttpHeaderNames.CONTENT_TYPE, "text/plain");
                    response.headers().add(HttpHeaderNames.CONTENT_LENGTH, 0);
                }
            }

            ctx.writeAndFlush(response);
            ctx.channel().close();
            return;

        }else if ("POST".equals(request.method().name())){
            String[] strings=request.uri().split("\\?")[0].split("/");
            if (strings[1].equals("map")){
                // http://localhost:7464/map/methon/
                //body:{a:b,}
                Map<String, ProcessMap.Nettybean> nettybeanMap=ProcessMap.mapMap.get(strings[2]);
                ProcessMap.Nettybean n=nettybeanMap.get(strings[3]);
                Object jsre= n.re(ctx,msg);
                if (jsre==null){jsre= "";}
                if(!(jsre instanceof String)){
                    jsre=JSON.toJSONString(jsre);
                }
                String send= JSON.toJSONString(jsre);
                ByteBuf byteBuf = Unpooled.copiedBuffer(send, CharsetUtil.UTF_8);
                response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
                response.headers().add(HttpHeaderNames.CONTENT_TYPE, "text/plain");
                response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
                ctx.writeAndFlush(response);
            }else {
                try {
                    strings=request.uri().split(feild,2);
                    strings=strings[1].split("/",2);
                    Map<String, ProcessMap.Nettybean> nettybeanMap=ProcessMap.mapMap.get(strings[0]);
                    ProcessMap.Nettybean n=nettybeanMap.get(strings[1]);
                    Object jsre=  n.re(ctx,msg);
                    if (jsre==null){
                        if (n.getReturnType().equals(Void.TYPE)){
                            return;
                        }
                        jsre= "{}";}
                    if (jsre instanceof ChannelAwait || n.getReturnType().equals(ChannelAwait.class)){return;}
                    if(!(jsre instanceof String || jsre instanceof JsonString)){
                        jsre=JSON.toJSONString(jsre);
                    }

                    ByteBuf byteBuf = Unpooled.copiedBuffer(jsre.toString(), CharsetUtil.UTF_8);
                    response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
                    response.headers().add(HttpHeaderNames.CONTENT_TYPE, "text/plain");
                    response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
                    ctx.writeAndFlush(response);
                    return;
                }catch (Exception | Error e){
                    e.printStackTrace();
                }



                System.out.println("uri:" + request.uri());
                ByteBuf buf = request.content();
                System.out.print("buf:"+buf.toString(CharsetUtil.UTF_8));
                File file=new File("C:\\old\\html文件\\新建文本文档.html");
                long l=file.length();
                FileReader fileReader=new FileReader(file);
                StringBuilder stringBuilder=new StringBuilder();
                fileReader.close();
                StringReader stringReader=new StringReader("D:\\java\\bbs-pro-1\\src\\main\\resources\\WEB-INF\\example\\addPrivateMessage.html");
                stringBuilder.toString();
                BufferedInputStream inputStream=new BufferedInputStream(new FileInputStream(file));
                BufferedInputStream inputStream1=new BufferedInputStream(new FileInputStream(new File("C:\\old\\html文件\\无标题.png")));//C:\old\html文件
                byte[] bytes=new byte[1024];
                ByteBuffer buffer=ByteBuffer.allocate(1024*1024);ByteBuf byteBuf = Unpooled.buffer();
                while (inputStream.read(bytes) != -1){
                    byteBuf.writeBytes(bytes);
                }
                ByteBuf byteBuf1=Unpooled.buffer();
                //.copiedBuffer("hello world", CharsetUtil.UTF_8);
                //byteBuf.writeBytes(buffer.array());
                response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,byteBuf);
                // response.content().writeBytes(byteBuf);
                response.headers().add(HttpHeaderNames.CONTENT_TYPE, "text/html;charset=utf-8");//"text/html;charset=utf-8"
                //response.headers().add(HttpHeaderNames.CONTENT_ENCODING,"br");
                response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
                ctx.writeAndFlush(response);
            }
        }else {
            try {
                String[] strings=request.uri().split(feild,2);
                strings=strings[1].split("/",2);
                Map<String, ProcessMap.Nettybean> nettybeanMap=ProcessMap.mapMap.get(strings[0]);
                ProcessMap.Nettybean n=nettybeanMap.get(strings[1]);
                Object jsre=  n.re(ctx,msg);
                if (jsre==null){
                    if (n.getReturnType().equals(Void.TYPE)){
                        return;
                    }
                    jsre= "{}";}
                if (jsre instanceof ChannelAwait || n.getReturnType().equals(ChannelAwait.class)){return;}
                if(!(jsre instanceof String || jsre instanceof JsonString)){
                    jsre=JSON.toJSONString(jsre);
                }

                ByteBuf byteBuf = Unpooled.copiedBuffer(jsre.toString(), CharsetUtil.UTF_8);
                response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
                response.headers().add(HttpHeaderNames.CONTENT_TYPE, "text/plain");
                response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
                ctx.writeAndFlush(response);
                return;
            }catch (Exception | Error e){
                e.printStackTrace();
            }
        }
    }
}