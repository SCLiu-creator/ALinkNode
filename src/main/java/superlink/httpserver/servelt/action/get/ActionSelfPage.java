package superlink.httpserver.servelt.action.get;

import com.alibaba.fastjson2.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.io.XMLWriter;
import superlink.filemanage.xmltool.UserGet;
import superlink.filemanage.xmltool.XmlCreate;
import superlink.filemanage.xmltool.XmlParser;
import superlink.httpserver.servelt.action.Api;
import superlink.httpserver.servelt.action.GetParm;
import superlink.httpserver.servelt.WebController;
import superlink.init.InitClass;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.MainDataQueue;
import superlink.udpbind.client.recives.Senders;
import superlink.udpbind.client.recives.datalen.DataLength;
import superlink.udpbind.client.recives.recor.BindFactory;

import java.io.*;
import java.net.DatagramPacket;
import java.net.URLDecoder;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebController(name = "SelfPage")
public class ActionSelfPage {

    @Api(def = "UpBackPic")
    public String upBackgroundPic(ChannelHandlerContext ctx, FullHttpRequest msg) throws IOException {

        ByteBuf byteBuf= msg.content();
        File file=new File(XmlCreate.userShow+"/"+"background");
        FileOutputStream fileOutputStream=new FileOutputStream(file);
        FileChannel fileChannel=fileOutputStream.getChannel();
        int len=byteBuf.capacity();
//        fileChannel.
        byteBuf.readBytes(fileChannel,0,len);
//        byteBuf.readBytes(fileOutputStream,len-1);
//        byte b=0;
//        while ((b=byteBuf.readByte())!=0 && b!=-128){
//            fileOutputStream.write(b);
//        }
        return null;
    }

    @Api(def = "getBackPic")
    public String getBackgroundPic(ChannelHandlerContext ctx) throws IOException {
        File file=new File(XmlCreate.userShow+"/"+"background");
        FileInputStream fileOutputStream=new FileInputStream(file);
        FileChannel fileChannel=fileOutputStream.getChannel();
        ByteBuf byteBuf= Unpooled.directBuffer(1000, (int) fileChannel.size());

        byte[] tmp = new byte[4096]; // 你可以根据需要调整这个缓冲区大小
        int bytesRead;
        // 处理缓冲区中的数据（这里只是一个示例，你可以根据需要进行处理）
        while ((bytesRead = fileOutputStream.read(tmp)) != -1) {
            byteBuf.writeBytes(tmp, 0, bytesRead);
        }
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
        response.headers().add(HttpHeaderNames.CONTENT_TYPE, "png/jpg");
        response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
        ctx.writeAndFlush(response);
//        byteBuf.readBytes(fileOutputStream,len-1);
//        byte b=0;
//        while ((b=byteBuf.readByte())!=0 && b!=-128){
//            fileOutputStream.write(b);
//        }
        return null;
    }

    @Api(def = "UpUserPic")
    public String upUserPic(ChannelHandlerContext ctx, FullHttpRequest msg) throws IOException {

        ByteBuf byteBuf= msg.content();
        File file=new File(XmlCreate.userShow+"/"+"headPic");
        FileOutputStream fileOutputStream=new FileOutputStream(file);
        FileChannel fileChannel=fileOutputStream.getChannel();
        int len=byteBuf.capacity();
//        fileChannel.
        byteBuf.readBytes(fileChannel,0,len);
//        byteBuf.readBytes(fileOutputStream,len-1);
//        byte b=0;
//        while ((b=byteBuf.readByte())!=0 && b!=-128){
//            fileOutputStream.write(b);
//        }
        return null;
    }

    @Api(def = "getUserPic")
    public String getUserPic(ChannelHandlerContext ctx) throws IOException {
        File file=new File(XmlCreate.userShow+"/"+"headPic");
        FileInputStream fileOutputStream=new FileInputStream(file);
        FileChannel fileChannel=fileOutputStream.getChannel();
        ByteBuf byteBuf= Unpooled.directBuffer(1000, (int) fileChannel.size());

        byte[] tmp = new byte[4096]; // 你可以根据需要调整这个缓冲区大小
        int bytesRead;
        while ((bytesRead = fileOutputStream.read(tmp)) != -1) {
            byteBuf.writeBytes(tmp, 0, bytesRead);
        }
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
        response.headers().add(HttpHeaderNames.CONTENT_TYPE, "png/jpg");
        response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
        ctx.writeAndFlush(response);
        return null;
    }

    @Api(def = "setName")
    public String setName(@GetParm String name) throws IOException {
        String filename=XmlParser.dir+"userpage.xml";
        try {
            UserGet.user.attribute("label").setValue(name);
            FileOutputStream fileOutputStream=new FileOutputStream(filename);
            XMLWriter writer=new XMLWriter(fileOutputStream);
            writer.write(UserGet.user.getDocument());
            writer.close();
            UDPclient.userlocal.nickName=name;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return UDPclient.userlocal.nickName;
    }

    @Api(def = "clcache")
    public void clcache() {
        File file=new File(XmlParser.cloudecache);
        for (File f:file.listFiles()){
            if (f.isFile()){
                f.delete();
            }
        }
        file=new File(XmlCreate.userCloudecache);
        for (File f:file.listFiles()){
            f.delete();
        }
        file=new File(InitClass.webpath+"zip");
        if (file.exists()){
            file.delete();
        }
    }

    @Api(def = "reload")
    public String reload() {

        byte[] bytes= UDPclient.userlocal.toString().getBytes();
        Senders.ServerSends(bytes);
        if(BindFactory.checkthread==null){
            UDPclient.executorService.execute(()->{
                InitClass.initClass.startBindresCheaklow();
            });
        }else if (BindFactory.checkthread.getState() == Thread.State.TERMINATED) {
            UDPclient.executorService.execute(()->{
                InitClass.initClass.startBindresCheaklow();
            });
        }

        if(MainDataQueue.mainReciverques.thread==null){
            UDPclient.executorService.execute(()->{
                InitClass.initClass.startBindresCheaklow();
            });
        }else if (MainDataQueue.mainReciverques.thread.getState() == Thread.State.TERMINATED) {
            UDPclient.executorService.execute(()->{
                InitClass.initClass.startBindresCheaklow();
            });
        }
        return UDPclient.userlocal.nickName;
    }


    @Api(def = "getTask")
    public List getTask() {
//        final CountDownLatch parserCtl = new CountDownLatch(paths.size());
        ArrayList arrayList = new ArrayList();
        MainDataQueue.quemap.forEach((k,v)->{
            v.taskMap.forEach((id,t)->{
                if(t.task instanceof DataLength){
                    Map map=new HashMap();
                    map.put("id",id);
                    map.put("process",((DataLength) t.task).getprogress());
                    map.put("detail", t.task.toString());
                    map.put("user", ((DataLength) t.task).userContext.userName);
                    arrayList.add(map);
                }
            });
        });
        if(UDPclient.mainDataQueue.reciverques!=null){
            Map map=new HashMap();
            Thread thread = UDPclient.mainDataQueue.reciverques.thread;
            if(thread!=null){
                map.put("id",thread.getName());
                map.put("process",thread.getState());
                map.put("detail", thread.toString());
            }else {
                map.put("id","null");
                map.put("process","die");
                map.put("detail", thread.toString());
            }
            arrayList.add(map);
        }
        if(BindFactory.checkthread!=null){
            Map map=new HashMap();
            Thread thread = BindFactory.checkthread;
            if(thread!=null){
                map.put("id",thread.getName());
                map.put("process",thread.getState());
                map.put("detail", thread.toString());
            }else {
                map.put("id","null");
                map.put("process","die");
                map.put("detail", thread.toString());
            }
            arrayList.add(map);
        }
        return arrayList;
    }

    @Api(def = "delTask")
    public String delTask(Map map) {
//        final CountDownLatch parserCtl = new CountDownLatch(paths.size());
        String user = (String) map.get("user");
        String strid = (String) map.get("id");
        Short id = Short.valueOf(strid);
        UserContext userContext = UDPclient.getUser(user);
        Object o = userContext.deltask(id);
        if (o instanceof DataLength){
            DataLength dataLength=(DataLength)o;
            dataLength.clear();
        }
        return "true";
    }

    @Api(def = "delUser")
    public String delUser(@GetParm String name) throws IOException {
        String filename=XmlParser.dir+"userpage.xml";
        try {
            Document document=UserGet.user.getDocument();
            UserGet.user.getParent().remove(UserGet.user);
            FileOutputStream fileOutputStream=new FileOutputStream(filename);
            XMLWriter writer=new XMLWriter(fileOutputStream);
            writer.write(document);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return UDPclient.userlocal.nickName;
    }

    @Api(def = "upServer")
    public String UpUser(@GetParm String mode) throws IOException {
        String filename=XmlParser.dir+"userpage.xml";
        if (!"server".equals(mode)){
            mode="user";
        }
        try {
            Attribute attribute=UserGet.user.attribute("server");
            if(attribute==null){
                UserGet.user.addAttribute("server",mode);
                FileOutputStream fileOutputStream=new FileOutputStream(filename);
                XMLWriter writer=new XMLWriter(fileOutputStream);
                writer.write(UserGet.user.getDocument());
                writer.close();
            }
            attribute.setValue(mode);
            UDPclient.userlocal.udpstate=1;
            if ("server".equals(mode)){
                UDPclient.userlocal.udpstate=1;
            }else {
                UDPclient.userlocal.udpstate=0;
            }
            return mode;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "user";
    }
    @Api(def = "getupServer")
    public String getUpUser() throws IOException {
        String filename=XmlParser.dir+"userpage.xml";
        try {
            Attribute attribute=UserGet.user.attribute("server");
            if(attribute==null){
                UserGet.user.addAttribute("server","user");
                FileOutputStream fileOutputStream=new FileOutputStream(filename);
                XMLWriter writer=new XMLWriter(fileOutputStream);
                writer.write(UserGet.user.getDocument());
                writer.close();
                return "user";
            }
            String value=attribute.getValue();
            return value;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "user";
    }

}
