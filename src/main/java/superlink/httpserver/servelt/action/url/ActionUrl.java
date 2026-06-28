package superlink.httpserver.servelt.action.url;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import superlink.httpserver.servelt.WebController;
import superlink.httpserver.servelt.action.Api;
import superlink.httpserver.servelt.httptype.ContentType;
import superlink.init.InitClass;
import superlink.util.Utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

@WebController()
public class ActionUrl {

//    @WebPath(name = "getuser/{name}")
//    public void geturl(String  context){
//        System.out.println(JSON.toJSONString(context));
//    }

//    @Api(name = "")
    @Api(def="")
    public void getIndex(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        String[] stringuri=request.uri().split("\\?",0);
        String geturi=stringuri[0];
        String[] strings=geturi.split("/");
        FullHttpResponse response = null;
        if (strings.length==0) {
            String s = InitClass.webpath + "index.html";
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
    }
}
