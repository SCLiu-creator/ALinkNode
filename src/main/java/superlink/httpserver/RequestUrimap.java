package superlink.httpserver;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import superlink.filemanage.xmltool.UserGet;
import superlink.httpserver.servelt.httptype.ContentType;
//import superlink.test.testjava.UnsafeTest;
import superlink.init.InitClass;
import superlink.util.Utils;

import java.io.*;

public class RequestUrimap {
    public String uri;
    public RequestUrimap(){

    }
//    public void setUri(String uri){
//        this.uri=uri;
//    }

    public FullHttpResponse headleUri(ChannelHandlerContext ctx,FullHttpRequest msg, String type, String name) throws IOException {
        String ouri=msg.uri();
        switch (type) {
            case "pic": {
                String s = "C:\\old\\html文件\\" + name;
                BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(new File(s)));
                byte[] bytes = new byte[1024];
                ByteBuf byteBuf = Unpooled.buffer();
                while (inputStream.read(bytes) != -1) {
                    byteBuf.writeBytes(bytes);
                }
                FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
                response.headers().add(HttpHeaderNames.CONTENT_TYPE, "png");//"text/html;charset=utf-8"
                response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
                return response;
            }
            case "req": {
                String s = "D:\\java\\bbs-pro-1\\src\\main\\resources\\WEB-INF\\example\\" + name;
                BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(new File(s)));
                byte[] bytes = new byte[1024];
                ByteBuf byteBuf = Unpooled.buffer();
                while (inputStream.read(bytes) != -1) {
                    byteBuf.writeBytes(bytes);
                }
                FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
                response.headers().add(HttpHeaderNames.CONTENT_TYPE, "html/text");//"text/html;charset=utf-8"
                response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
                return response;
            }
            case "js": {
                FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                return response;
            }
            case "pro": {
                return null;
            }
            case "static": {
//                if (UserGet.user==null){
//                    return null;
//                }
                //http://localhost:7005/static/webui/ii#
                if (ouri.split("\\.").length < 2) {//name.equals("index")
                    String s = InitClass.webpath + "/index.html";
                    File file = new File(s);
                    FileInputStream fileInputStream = new FileInputStream(file);
                    BufferedInputStream inputStream = new BufferedInputStream(fileInputStream);
                    byte[] bytes = new byte[1024];
                    ByteBuf byteBuf = Unpooled.buffer();
                    int len;
                    while ((len = inputStream.read(bytes)) != -1) {
                        bytes = Utils.subByte(bytes, 0, len);
                        byteBuf.writeBytes(bytes);
                    }
                    FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
                    response.headers().add(HttpHeaderNames.CONTENT_TYPE, ContentType.valueOf("html").Type());//"text/html;charset=utf-8"
                    response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
                    inputStream.close();
                    fileInputStream.close();
                    return response;
                } else {

                    String s = InitClass.webpath + ouri.replace("/static/webui/", "/");
                    File file = new File(s);
                    String[] u = ouri.split("\\.");
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
                    FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
                    response.headers().add(HttpHeaderNames.CONTENT_TYPE, ContentType.valueOf(prex).Type());//"text/html;charset=utf-8"
                    response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
                    inputStream.close();
                    fileInputStream.close();
                    return response;
                }

            }
            case "web": {
                //http://localhost:7005/static/webui/ii#
                if (ouri.split("\\.").length < 2) {//name.equals("index")
                    String s = InitClass.absolute+"web/" + name + "/index.html";
                    File file = new File(s);
                    FileInputStream fileInputStream = new FileInputStream(file);
                    BufferedInputStream inputStream = new BufferedInputStream(fileInputStream);
                    byte[] bytes = new byte[1024];
                    ByteBuf byteBuf = Unpooled.buffer();
                    int len;
                    while ((len = inputStream.read(bytes)) != -1) {
                        bytes = Utils.subByte(bytes, 0, len);
                        byteBuf.writeBytes(bytes);
                    }
                    FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
                    response.headers().add(HttpHeaderNames.CONTENT_TYPE, ContentType.valueOf("html").Type());//"text/html;charset=utf-8"
                    response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
                    inputStream.close();
                    fileInputStream.close();
                    return response;
                } else {

                    String s = InitClass.webpath+ouri.replace("web/webui/", "");
                    File file = new File(s);
                    String[] u = ouri.split("\\.");
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
                    FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
                    response.headers().add(HttpHeaderNames.CONTENT_TYPE, ContentType.valueOf(prex).Type());//"text/html;charset=utf-8"
                    response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
                    inputStream.close();
                    fileInputStream.close();
                    return response;
                }

            }
            case "van": {
                // http://localhost:7464/van/breakout-game/
                FullHttpResponse response = null;
                FullHttpRequest request = msg;
                String[] strings = request.uri().split("/");
                if (strings.length == 3) {
                    ctx.channel();
                    String s = InitClass.absolute+ "web\\"+type+"/"+name+"/index.html";//"D:\\js\\vanill\\vanillawebprojects-master\\custom-video-player\\index.html"
                    BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(new File(s)));
                    byte[] bytes = new byte[1024];
                    ByteBuf byteBuf = Unpooled.buffer();
                    int i;
                    while ((i = inputStream.read(bytes)) != -1) {
                        byte[] b = Utils.subByte(bytes, 0, i);
                        byteBuf.writeBytes(b);
                    }
                    response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
                    response.headers().add(HttpHeaderNames.CONTENT_TYPE, "text/html");//"text/html;charset=utf-8"
                    response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
                } else if ("".equals(strings[0])) {
                    StringBuilder names = new StringBuilder("");

                    for (String s : strings) {
                        names.append("\\").append(s);
                    }
                    String prx = strings[strings.length - 1].split("\\.")[1];
                    //prx;
                    String s = InitClass.absolute+"web\\" + names.toString();//D:\js\vanill\vanillawebprojects-master\custom-video-player
//                    s=s.replace("\\\\van","");
                    File f = new File(s);
                    //  s=f.toURI().toURL().toString();
                    if (f.length() < 4 * 1024 * 1024) {
                        BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(new File(s)));
                        byte[] bytes = new byte[1024];
                        ByteBuf byteBuf = Unpooled.buffer();
                        int i;
                        while ((i = inputStream.read(bytes)) != -1) {
                            byte[] b = Utils.subByte(bytes, 0, i);
                            byteBuf.writeBytes(b);
                        }
                        response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
                        response.headers().add(HttpHeaderNames.CONTENT_TYPE, ContentType.valueOf(prx).Type());//"text/html;charset=utf-8"
                        response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
                    } else {
                        if (msg.headers().get("Range") == null) {
                            BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(new File(s)));
                            byte[] bytes = new byte[1024];
                            ByteBuf byteBuf = Unpooled.buffer();
                            int i;
                            while ((i = inputStream.read(bytes)) != -1) {
                                byte[] b = Utils.subByte(bytes, 0, i);
                                byteBuf.writeBytes(b);
                            }
                            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
                            response.headers().add(HttpHeaderNames.CONTENT_TYPE, ContentType.valueOf(prx).Type());//"text/html;charset=utf-8"
                            response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
                        } else {
                            String[] s1 = msg.headers().get("Range").split("=");

                            String bytestart = s1[1].split("-")[0];
                            String byteend = null;
                            try {
                                byteend = s1[1].split("-")[1];
                            } catch (ArrayIndexOutOfBoundsException a) {

                            }

                            if (byteend == null) {
                                BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(new File(s)));
                                byte[] bytes = new byte[102400];
                                ByteBuf byteBuf = Unpooled.buffer();
                                int i;
                                inputStream.skip(Long.parseLong(bytestart));
                                byte[] b = new byte[0];
                                while ((i = inputStream.read(bytes)) != -1) {
                                    b = Utils.subByte(bytes, 0, i);
                                    byteBuf.writeBytes(b);
                                }
//                            i=inputStream.read(bytes);
//                            byte[] b= Utils.subByte(bytes,0,i);
//                            byteBuf.writeBytes(b);

                                // String range="bytes "+bytestart+"-"+f.length();
                                String range = "bytes " + bytestart + "-" + (byteBuf.readableBytes() + Integer.valueOf(bytestart)) + "/" + f.length();
                                response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
                                response.headers().add(HttpHeaderNames.CONTENT_TYPE, ContentType.valueOf(prx).Type());
                                response.headers().add(HttpHeaderNames.CONTENT_RANGE, range);//"text/html;charset=utf-8"
                                response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
                            } else {
                                Integer rangestart = Integer.valueOf(bytestart);
                                Integer rangeend = Integer.valueOf(byteend);
                                BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(new File(s)));
                                byte[] bytes = new byte[rangeend - rangestart];
                                ByteBuf byteBuf = Unpooled.buffer();
                                int i;
                                inputStream.skip(Long.parseLong(bytestart));
//                            while ((i=inputStream.read(bytes)) != -1){
//                                byte[] b= Utils.subByte(bytes,0,i);
//                                byteBuf.writeBytes(b);
//                            }
                                i = inputStream.read(bytes);
                                byte[] b = Utils.subByte(bytes, 0, i);
                                byteBuf.writeBytes(b);

                                // String range="bytes "+bytestart+"-"+f.length();
                                String range = "bytes " + bytestart + "-" + (rangeend - rangestart);
                                response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
                                response.headers().add(HttpHeaderNames.CONTENT_TYPE, ContentType.valueOf(prx).Type());
                                response.headers().add(HttpHeaderNames.CONTENT_RANGE, range);//"text/html;charset=utf-8"
                                response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
                            }

                        }
                    }
                }
                return response;
            }
        }
        return null;
    }
}
