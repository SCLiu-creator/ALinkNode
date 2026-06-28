package superlink.httpserver.servelt.action.get;

import com.alibaba.fastjson2.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.dom4j.Document;
import org.dom4j.Element;
import superlink.filemanage.classprocess.property.ReInfuse;
import superlink.filemanage.classprocess.property.reInject;
import superlink.filemanage.xmltool.XmlParser;
import superlink.httpserver.servelt.WebController;
import superlink.httpserver.servelt.action.Api;
import superlink.httpserver.servelt.action.GetParm;
import superlink.httpserver.servelt.action.service.ServiceATCon;
import superlink.httpserver.servelt.httptype.ContentType;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.DataLenMange;
import superlink.udpbind.client.recives.datalen.*;
import superlink.udpbind.cloude.show.ShowBin;
import superlink.udpbind.cloude.show.UserShowContainer;
import superlink.udpbind.handle.handler.ReqDirHandler;
import superlink.util.SHAutils;
import superlink.util.Tool;

import java.io.*;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static superlink.udpbind.client.UDPclient.mainDataQueue;

@WebController(name = "userShow")
@ReInfuse
public class ActionUserShow {

    @Api(def = "get")
    public String cPathList(@GetParm String username){
        UserContext userContext= null;
        try {
            userContext = mainDataQueue.getUserContext(username);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int id=userContext.newQueue();
        String string= ":showpath&:"+username +".xml";
        //todo
        new ReqDirHandler(username,id,string).process();
        return "true";
    }
    @Api(def = "getshow")//废弃
    public String getShow(Map<String,String> map){
        String user=map.get("u");
        String start=map.get("s");
        String len=map.get("l");
        ShowBin showBin=UserShowContainer.showBinMap.get(user);
        List list=showBin.get(Integer.parseInt(start),Integer.valueOf(len));
        return JSON.toJSONString(list);
    }
    @Api(def = "getshowT")
    public String getShowTime(Map<String,Object> map){
        String user= (String) map.get("u");

        Integer start= (Integer) map.get("s");
        Integer len= (Integer) map.get("l");
        if (UDPclient.userlocal.username.equals(user)){
            ShowBin showBin=UserShowContainer.getLocalShowBin();
//        List list=showBin.getBodyByTime(start,Integer.len);
            List list=showBin.getBodyByTime(start,Integer.MAX_VALUE/2);
            return JSON.toJSONString(list);
        }
        ShowBin showBin=UserShowContainer.showBinMap.get(user);
//        List list=showBin.getBodyByTime(start,Integer.len);
        List list=showBin.getBodyByTime(start,Integer.MAX_VALUE/2);
        return JSON.toJSONString(list);
    }
    @Api(def = "getShowPath")
    public String getPathShow(ChannelHandlerContext ctx, @GetParm Map<String,Object> map) throws Exception {
//        final CountDownLatch parserCtl = new CountDownLatch(paths.size());
        String s= null;
        List<String> list= (List<String>) map.get("p");
        String username= (String) map.get("u");
        ShowBin showBin=UserShowContainer.showBinMap.get(username);
        AtomicInteger integer=new AtomicInteger(list.size());
        Element element=showBin.getPathElement(list,integer);
        if (integer.get()>=0&&element.elements().size()<=0){
            StringBuilder path=showBin.getAbsolut(element);
            path.insert(0,":xmlfile&:");
            AsyBuffer autoBuffer=new AsyBuffer(username);
            autoBuffer.setPagelen(DataLenMange.getLen(username));
            byte[] bytes=autoBuffer.reqData(path.toString());
            autoBuffer.clear();
//            Files.write(Paths.get("C:\\Users\\liushengchang-n\\Desktop\\新建文件夹xml1.xml"),bytes);
            String xml = new String(bytes,StandardCharsets.UTF_8);
            Document document=XmlParser.parseStr(xml);
            Element er=document.getRootElement();
            List<Element> elements=er.elements();
            for (Element ele: elements){
                er.remove(ele);
                element.add( ele);
            }
//            Attribute attribute=er.attribute("t");
//            if (attribute!=null){
//                element.add(attribute);
//            }
            element=showBin.getPathElement(list,integer);
        }
        List list1=showBin.getEleList(element,0,Integer.MAX_VALUE/2);
        return JSON.toJSONString(list1);
    }
    //[{t=2025-04-02 22:16:59.0, p=保存到网盘后下载}]
    @Api(def = "getShowFile")
    public ChannelHandlerContext getFileCloude(ChannelHandlerContext ctx, Map<String,String> map) throws Exception {
//        final CountDownLatch parserCtl = new CountDownLatch(paths.size());
        String s= null;
        try {
            s = URLDecoder.decode(map.get("p"),"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String username= map.get("u");
        String cacheFile=XmlParser.cloudecache + SHAutils.getMD5(s, true)+ Tool.getPrex(s);
        File file=new File(cacheFile);
        if (file.exists()){
            ByteBuf byteBuf = Unpooled.buffer((int) file.length());
            byteBuf.writeBytes(Files.readAllBytes(file.toPath()));
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
            try {
                response.headers().add(HttpHeaderNames.CONTENT_TYPE, ContentType.safeValueOf(s.split("\\.")[1].toLowerCase()));
            }catch (Exception e){
                response.headers().add(HttpHeaderNames.CONTENT_TYPE, "application/"+s.split("\\.")[1].toLowerCase());
            }
            String base64= Base64.getEncoder().encodeToString(new File(s).getName().getBytes((StandardCharsets.UTF_8)));
            response.headers().add("filename", base64);
            response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
            ctx.writeAndFlush(response);
//            JSON.toJSONString(UserGet.getUserList())
            return ctx;
        }
//        AsySteam dataAuto=new AsySteam(username);
        AutoAsyFile dataAuto=new AutoAsyFile(username);
        dataAuto.setPagelen(DataLenMange.getLen(username));
        Object o;
        try {
            serviceATCon.add(username,s,dataAuto);
             o=dataAuto.reqFile(s);
        }finally {
            serviceATCon.remove(username,s);
        }

//        dataAuto.reqData(s);
////        Thread.sleep(90*1000);
//        Object o =dataAuto.gettest();

        if (dataAuto.rev!=null || o instanceof byte[]){
            byte[] bytes= (byte[]) o;
            DataLength.writdata(cacheFile,o);
            ByteBuf byteBuf = Unpooled.buffer(bytes.length);
            byteBuf.writeBytes(bytes);
            dataAuto.clear();
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
            try {
//                response.headers().add(HttpHeaderNames.CONTENT_DISPOSITION,"attachment;filename="+s);
                response.headers().add(HttpHeaderNames.CONTENT_TYPE, ContentType.safeValueOf(s.split("\\.")[1].toLowerCase()));
            }catch (Exception e){
                response.headers().add(HttpHeaderNames.CONTENT_TYPE, "application/"+s.split("\\.")[1].toLowerCase());
            }
            String base64= Base64.getEncoder().encodeToString(new File(s).getName().getBytes());
            response.headers().add("filename", base64);
            response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
            ctx.writeAndFlush(response);
//            JSON.toJSONString(UserGet.getUserList())
            return ctx;
        }
        dataAuto.clear();
        if(o instanceof File) {
            ContentType prex=null;
            try {
                prex=ContentType.safeValueOf(Tool.getPrex(s));
                ByteBuf byteBuf = Unpooled.buffer(Math.toIntExact(file.length()));
                byte[] bytes=new byte[Math.toIntExact(file.length())];
                ByteBuffer byteBuffer=ByteBuffer.wrap(bytes);
                if (!file.exists()){
                    file.createNewFile();
                }
                try {
                    FileChannel fileChannel=FileChannel.open(Paths.get(file.getPath()), StandardOpenOption.READ);
                    fileChannel.read(byteBuffer);
                    //len=byteBuf.setBytes(0,fileChannel,0, Math.toIntExact(fileChannel.size()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                byteBuf.writeBytes(bytes);
                FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
                response.headers().add(HttpHeaderNames.CONTENT_TYPE, prex);
                String base64= Base64.getEncoder().encodeToString(new File(s).getName().getBytes());
                response.headers().add("filename", base64);
//                response.headers().add("filename", Utils.encodeString(new File(s).getName()));
                response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
                ctx.writeAndFlush(response);
//                JSON.toJSONString(UserGet.getUserList())
                return ctx;
            }catch (Exception e) {
                try {
                    file.createNewFile();
                    FileChannel fileWrite = new FileInputStream(file).getChannel();
                    FileChannel fileChannel = FileChannel.open(Paths.get(((File) o).getPath()), StandardOpenOption.READ);
                    fileChannel.transferTo(0, fileChannel.size(), fileWrite);
                } catch (IOException ie) {
                    ie.printStackTrace();
                }
                byte[] bytes = ("已经保存到:  " + file.getName()).getBytes();
                ByteBuf byteBuf = Unpooled.buffer(bytes.length);
                byteBuf.writeBytes(bytes);
                FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
                response.headers().add(HttpHeaderNames.CONTENT_TYPE, ContentType.safeValueOf("txt"));
                String base64= Base64.getEncoder().encodeToString(new File(s).getName().getBytes());
                response.headers().add("filename", base64);
                response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
                ctx.writeAndFlush(response);
//                JSON.toJSONString(UserGet.getUserList())
                return ctx;
            }
        }
        return null;
    }

    @reInject
    public ServiceATCon serviceATCon;

    @Api(def = "getProcess")
    public Map getFileProcess(ChannelHandlerContext ctx, Map<String,String> map) throws Exception {
//        final CountDownLatch parserCtl = new CountDownLatch(paths.size());
        String s= null;
        try {
            s = URLDecoder.decode(map.get("p"),"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String username= map.get("u");

        DataLength dataAuto=  serviceATCon.get(username,s);

        Map json=new HashMap(2);
        if (dataAuto==null ||
                dataAuto.userContext==null ||
                dataAuto.userContext.getTask(dataAuto.id)==null){
            json.put("e",400);
        }else {
            json.put("e",200);
            json.put("pr",dataAuto.getprogress());

        }

        return json;
    }

    @Api(def = "downFile")
    public void downFile(@GetParm String path,ChannelHandlerContext ctx) throws Exception {
//        final CountDownLatch parserCtl = new CountDownLatch(paths.size());
        String s = null;
        try {
            s = URLDecoder.decode(path, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String cacheFile = XmlParser.extend + SHAutils.getMD5(s, true) + Tool.getPrex(s);
        File file = new File(cacheFile);

        FileChannel fileChannel = FileChannel.open(Paths.get(file.getPath()), StandardOpenOption.READ);


        ContentType prex=null;
        try {
            prex=ContentType.safeValueOf(Tool.getPrexs(s));
            ByteBuf byteBuf = Unpooled.buffer(Math.toIntExact(file.length()));
            byte[] bytes=new byte[Math.toIntExact(file.length())];
            ByteBuffer byteBuffer=ByteBuffer.wrap(bytes);
            if (!file.exists()){
                return;
            }
            fileChannel.read(byteBuffer);
                //len=byteBuf.setBytes(0,fileChannel,0, Math.toIntExact(fileChannel.size()));

            byteBuf.writeBytes(bytes);
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
            response.headers().add(HttpHeaderNames.CONTENT_TYPE, prex);
            String base64= Base64.getEncoder().encodeToString(new File(s).getName().getBytes());
            response.headers().add("filename", base64);
//                response.headers().add("filename", Utils.encodeString(new File(s).getName()));
            response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
            ctx.writeAndFlush(response);
//                JSON.toJSONString(UserGet.getUserList())

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Api(def = "saveFile")
    public void saveFile(@GetParm String path) throws Exception {
//        final CountDownLatch parserCtl = new CountDownLatch(paths.size());
        String s = null;
        try {
            s = URLDecoder.decode(path, "utf-8");
            if(s.contains("chatpath")){
                File file = new File(XmlParser.extend+new File(path).getName());
                try {
                    file.createNewFile();
                    FileChannel fileWrite = new FileOutputStream(file).getChannel();
                    FileChannel fileChannel = FileChannel.open(Paths.get((new File(s)).getPath()), StandardOpenOption.READ,StandardOpenOption.WRITE);
                    fileChannel.transferTo(0, fileChannel.size(), fileWrite);
                } catch (IOException ie) {
                    ie.printStackTrace();
                }
            }else if(s.contains("cloudecache")){
                String cacheFile = XmlParser.cloudecache + SHAutils.getMD5(s, true) + Tool.getPrex(s);
                File file = new File(XmlParser.extend+new File(path).getName());
                try {
                    file.createNewFile();
                    FileChannel fileWrite = new FileOutputStream(file).getChannel();
                    FileChannel fileChannel = FileChannel.open(Paths.get((new File(cacheFile)).getPath()), StandardOpenOption.READ,StandardOpenOption.WRITE);
                    fileChannel.transferTo(0, fileChannel.size(), fileWrite);
                } catch (IOException ie) {
                    ie.printStackTrace();
                }
            }else{
                String cacheFilePath=XmlParser.cloudecache + SHAutils.getMD5(s, true)+ Tool.getPrex(s);
                File cacheFile=new File(cacheFilePath);
                File file = new File(XmlParser.extend+new File(path).getName());
                try {
                    file.createNewFile();
                    FileChannel fileWrite = new FileOutputStream(file).getChannel();
                    FileChannel fileChannel = FileChannel.open(Paths.get(cacheFile.getPath()), StandardOpenOption.READ,StandardOpenOption.WRITE);
                    fileChannel.transferTo(0, fileChannel.size(), fileWrite);
                } catch (IOException ie) {
                    ie.printStackTrace();
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Api(def = "getVideoRange")
    public void getVideoRange(ChannelHandlerContext ctx, FullHttpRequest req,
                              @GetParm(name="u") String username,
                              @GetParm(name="p") String path
    ) throws Exception {
        String rd=req.headers().get("Range");
        int p1=rd.lastIndexOf("=");
        rd=rd.substring(p1+1);
        int p=rd.lastIndexOf("-");
        String d0=rd.substring(0,p);
        String d1=rd.substring(p+1);
        if ("".equals(d1)){
            d1 = "10240000";
        }
        Integer start= Integer.valueOf(d0);
        Integer len= Integer.valueOf(d1);
        path=":datafl&:"+path;

//        AutoBuffer autoBuffer=new AutoBuffer(username);
        AsyBuffer autoBuffer =new AsyBuffer(username);
        byte[] bytes=autoBuffer.reqData(path,start,len);
        ByteBuf byteBuf = Unpooled.wrappedBuffer(bytes);
        autoBuffer.clear();
//        byteBuf.writeBytes(bytes);

        String range = "bytes " + d0 + "-" + (start + bytes.length-1) + "/"+autoBuffer.bdr.al;
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.PARTIAL_CONTENT, byteBuf);
        response.headers().add(HttpHeaderNames.CONTENT_TYPE, ContentType.safeValueOf(Tool.getPrex(path)));
        response.headers().add(HttpHeaderNames.CONTENT_RANGE, range);//"text/html;charset=utf-8"
//        response.headers().add("filename", new File(s).getName());
        response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
        response.headers().add(HttpHeaderNames.CACHE_CONTROL,"no-store, no-cache, must-revalidate, proxy-revalidate, max-age=0");
        ctx.writeAndFlush(response);
//        return JSON.toJSONString(list1);
    }

    @Api(def = "getVideo")
    public void getVideoShow(ChannelHandlerContext ctx, FullHttpRequest req, @GetParm Map<String,Object> map) throws Exception {
        String s= null;
        List<String> list= (List<String>) map.get("p");
        String username= (String) map.get("u");
        String path= (String) map.get("p");
        Integer start= (Integer) map.get("s");
        Integer len= (Integer) map.get("l");
        path=":datafl&:"+path;

        AutoBuffer autoBuffer=new AutoBuffer(username);
        byte[] bytes=autoBuffer.reqData(path,start,len);
        ByteBuf byteBuf = Unpooled.wrappedBuffer(bytes);
        autoBuffer.clear();
//        byteBuf.writeBytes(bytes);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
        response.headers().add(HttpHeaderNames.CONTENT_TYPE, ContentType.safeValueOf(Tool.getPrex(path)));
//        response.headers().add("filename", new File(s).getName());
        response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
        ctx.writeAndFlush(response);
//        return JSON.toJSONString(list1);
    }
    @Api(def = "getVideopl")
    public void getVideopl(ChannelHandlerContext ctx, Map<String,Object> map) throws Exception {
        String s= null;
//        List<String> list= (List<String>) map.get("p");
        String username= (String) map.get("u");
        String path= (String) map.get("p");
        Integer start= (Integer) map.get("s");
        Integer len= (Integer) map.get("l");
        path=":datafl&:"+path;

        AutoBuffer autoBuffer=new AutoBuffer(username);
        byte[] bytes=autoBuffer.reqData(path,start,len);
        ByteBuf byteBuf = Unpooled.wrappedBuffer(bytes);
        autoBuffer.clear();
//        byteBuf.writeBytes(bytes);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
        response.headers().add(HttpHeaderNames.CONTENT_TYPE, ContentType.safeValueOf(Tool.getPrex(path)));
//        response.headers().add("filename", new File(s).getName());
        response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
        ctx.writeAndFlush(response);
//        return JSON.toJSONString(list1);
    }

}
