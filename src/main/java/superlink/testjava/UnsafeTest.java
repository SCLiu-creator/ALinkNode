package superlink.testjava;

import superlink.httpserver.servelt.httptype.ContentType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import sun.misc.Unsafe;
import superlink.util.Utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UnsafeTest {

//    static {
//        new Thread(new HttpServlet(8082)).start();
//    }
    public static Map map=new HashMap();
    public static int in=0;
    public static void main(String[] args) {//需要jdk12以上才能直接控制

        ConcurrentHashMap<String,String> concurrentHashMap=new ConcurrentHashMap<>();

        concurrentHashMap.put("123","123");

        concurrentHashMap.entrySet().forEach((e)->{
            System.out.println(e.getKey());
            concurrentHashMap.remove(e.getKey());
        });

        concurrentHashMap.entrySet().forEach((e)->{
            System.out.println(e.getKey());
            concurrentHashMap.remove(e.getKey());
        });

        Utils.unZip("C:\\Users\\liusc\\Desktop\\新建文件夹 (2)",
                new File("C:\\Users\\liusc\\Desktop\\spring-framework-main.zip"));

        String s="aaa:&:b";
        int ind=s.indexOf("&:");
        String s1=s.substring(0,ind);
        String[] strings=s.split("&:");
        Unsafe unsafe = Unsafe.getUnsafe();
        unsafe.allocateMemory(1024);
        unsafe.reallocateMemory(1024, 1024);
        unsafe.freeMemory(1024);
        ByteBuffer buffer = ByteBuffer.allocateDirect(10 * 1024 * 1024);
    }

    public void testctx(String path) throws Exception {



        ChannelHandlerContext ctx= (ChannelHandlerContext) map.get(0);
        //  s=f.toURI().toURL().toString();
        BufferedInputStream inputStream=new BufferedInputStream(new FileInputStream(new File(path)));
//        new FileInputStream(new File(path)).getChannel().transferTo()
        byte[] bytes=new byte[1024];
        ByteBuf byteBuf = Unpooled.buffer();
        int i;
        while ((i=inputStream.read(bytes)) != -1){
            byte[] b= Utils.subByte(bytes,0,i);
            byteBuf.writeBytes(b);
        }
        String prx=path.split("\\.")[1];
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,byteBuf);
        response.headers().add(HttpHeaderNames.CONTENT_TYPE, ContentType.safeValueOf(prx).Type());//"text/html;charset=utf-8"
        response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
        ctx.writeAndFlush(response);
    }
}