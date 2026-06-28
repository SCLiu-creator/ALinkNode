package superlink.httpserver.servelt.action.buf;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import superlink.filemanage.xmltool.XmlParser;
import superlink.httpserver.servelt.WebController;
import superlink.httpserver.servelt.action.Api;
import superlink.filemanage.classprocess.property.ReInfuse;
import superlink.filemanage.classprocess.property.reInject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.channels.FileChannel;

@WebController(name = "file")
@ReInfuse()
public class ActionFile {

    @reInject(name = "scan")
    scanFile scanFile;

    @Api(def = "upJar")
    public String openFileBit(ChannelHandlerContext ctx, FullHttpRequest msg) throws IOException {
        ByteBuf byteBuf= msg.content();
        String name=msg.headers().get("name");
        name= URLDecoder.decode(name,"UTF-8");
        File file=new File(XmlParser.cachepath +"/"+name);
        FileOutputStream fileOutputStream=new FileOutputStream(file);
        FileChannel fileChannel=fileOutputStream.getChannel();
        int len=byteBuf.capacity();
//        fileChannel.
        byteBuf.readBytes(fileChannel,0,len);
        scanFile.scan(file);
        return "true";
    }

    @Api(def = "upJar")
    public String unZip(ChannelHandlerContext ctx, FullHttpRequest msg) throws IOException {
        ByteBuf byteBuf= msg.content();
        String name=msg.headers().get("name");
        name= URLDecoder.decode(name,"UTF-8");
        File file=new File(XmlParser.cachepath +"/"+name);
        FileOutputStream fileOutputStream=new FileOutputStream(file);
        FileChannel fileChannel=fileOutputStream.getChannel();
        int len=byteBuf.capacity();
//        fileChannel.
        byteBuf.readBytes(fileChannel,0,len);
        scanFile.scan(file);
        return "true";
    }

}
