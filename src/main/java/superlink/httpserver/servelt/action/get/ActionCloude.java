package superlink.httpserver.servelt.action.get;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import superlink.filemanage.xmltool.UserGet;
import superlink.filemanage.xmltool.XmlCreate;
import superlink.filemanage.xmltool.XmlParser;
import superlink.httpserver.servelt.action.Action;
import superlink.httpserver.servelt.action.Api;
import superlink.httpserver.servelt.action.ChannelAwait;
import superlink.httpserver.servelt.action.GetParm;
import superlink.httpserver.servelt.WebController;
import superlink.httpserver.servelt.httptype.ContentType;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.datalen.AutoBuffer;
import superlink.udpbind.client.recives.datalen.DataLength;
import superlink.udpbind.cloude.DataCloud;
import superlink.udpbind.client.recives.datalen.DataReqAuto;
import superlink.udpbind.cloude.*;
import superlink.udpbind.cloude.operta.unicast.UseOperta;
import superlink.udpbind.fileListen.common.FileMonitor;
import superlink.udpbind.cloude.util.TendFactory;
import superlink.udpbind.cloude.util.TendMap;
import superlink.udpbind.handle.handler.ReqCloudeAutoMap;
import superlink.util.SHAutils;
import superlink.util.Tool;

import java.io.*;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import static superlink.util.Tool.checkNull;
import static superlink.util.Utils.convertToDateTime;
import static superlink.util.Utils.getCurrentDateStringArray;


@WebController(name ="ActionCloude" )
public class ActionCloude implements Action {

    /**http://localhost:7464/map/ActionCloude/getFile/?
    **D:\java\新建文件夹\client\web\celan\image\00000007.jpg
    * **/
    @Api(def = "getFile")
    public String getLoginlist(ChannelHandlerContext ctx, FullHttpRequest msg){
        String[] s=msg.uri().split("\\?");
        String s1=s[1];
        try {
            s1= URLDecoder.decode(s1,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String prex=s1.split("\\.")[1];
        File file=new File(s1);
        ByteBuf byteBuf = Unpooled.buffer(Math.toIntExact(file.length()));
        byte[] bytes=new byte[Math.toIntExact(file.length())];
        ByteBuffer byteBuffer=ByteBuffer.wrap(bytes);
        FileChannel fileChannel = null;
        int len=0;
        try {
            fileChannel=FileChannel.open(Paths.get(file.getPath()), StandardOpenOption.READ);
            fileChannel.read(byteBuffer);
            //len=byteBuf.setBytes(0,fileChannel,0, Math.toIntExact(fileChannel.size()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        byteBuf.writeBytes(bytes);
        ByteBuf buf = msg.content();
        System.out.print(buf.toString(CharsetUtil.UTF_8));

        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
        response.headers().add(HttpHeaderNames.CONTENT_TYPE, ContentType.safeValueOf(prex).Type());
        response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
        ctx.writeAndFlush(response);
        return JSON.toJSONString(UserGet.getUserList());
    }
    /*http://localhost:7240/map/ActionCloude/getFilel/&%7B%221%22:%22E:/udpclient/web/webui/images/00000007.jpg%22%7D*/
    @Api(def = "getFilel")
    public String getFile(ChannelHandlerContext ctx, FullHttpRequest msg,@GetParm Map<String,String> map){
        String s= null;
        try {
            s = URLDecoder.decode(map.get("1"),"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        File file=new File(map.get("1"));
        ByteBuf byteBuf = Unpooled.buffer(Math.toIntExact(file.length()));
        byte[] bytes=new byte[Math.toIntExact(file.length())];
        ByteBuffer byteBuffer=ByteBuffer.wrap(bytes);
        FileChannel fileChannel = null;
        int len=0;
        try {
            fileChannel=FileChannel.open(Paths.get(file.getPath()), StandardOpenOption.READ);
            fileChannel.read(byteBuffer);
            //len=byteBuf.setBytes(0,fileChannel,0, Math.toIntExact(fileChannel.size()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        byteBuf.writeBytes(bytes);
        ByteBuf buf = msg.content();
        System.out.print(buf.toString(CharsetUtil.UTF_8));

        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
        response.headers().add(HttpHeaderNames.CONTENT_TYPE, ContentType.safeValueOf(s.split("\\.")[1]));
        response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
        ctx.writeAndFlush(response);
        return JSON.toJSONString(UserGet.getUserList());
    }

    /*http://localhost:7240/map/ActionCloude/getFilel/&%7B%221%22:%22E:/udpclient/web/webui/images/00000007.jpg%22%7D*/
    @Api(def = "getCloudeFile")
    public String getFileCloude(ChannelHandlerContext ctx,@GetParm Map<String,String> map) throws Exception {
//        final CountDownLatch parserCtl = new CountDownLatch(paths.size());
        String s= null;
        try {
            s = URLDecoder.decode(map.get("file"),"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String username= map.get("name");
        String cacheFile=XmlParser.cloudecache + SHAutils.getMD5(s, true)+Tool.getPrex(s);
        File file=new File(cacheFile);
        if (file.exists()){
            ByteBuf byteBuf = Unpooled.buffer((int) file.length());
            byteBuf.writeBytes(Files.readAllBytes(file.toPath()));
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
            try {
                response.headers().add(HttpHeaderNames.CONTENT_TYPE, ContentType.safeValueOf(s.split("\\.")[1]));
            }catch (Exception e){
                response.headers().add(HttpHeaderNames.CONTENT_TYPE, "application/"+s.split("\\.")[1]);
            }
            response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
            ctx.writeAndFlush(response);
//            JSON.toJSONString(UserGet.getUserList())
            return null;
        }
        DataReqAuto dataReqAuto =new DataReqAuto(username);
        Object o= dataReqAuto.reqFile(s);
        if (dataReqAuto.rev!=null){
            DataLength.writdata(cacheFile,o);
            dataReqAuto.clear();
            ByteBuf byteBuf = Unpooled.buffer(dataReqAuto.rev.length);
            byteBuf.writeBytes(dataReqAuto.rev);
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
            try {
//                response.headers().add(HttpHeaderNames.CONTENT_DISPOSITION,"attachment;filename="+s);
                response.headers().add(HttpHeaderNames.CONTENT_TYPE, ContentType.safeValueOf(s.split("\\.")[1]));
            }catch (Exception e){
                response.headers().add(HttpHeaderNames.CONTENT_TYPE, "application/"+s.split("\\.")[1]);
            }
            response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
            ctx.writeAndFlush(response);
//            JSON.toJSONString(UserGet.getUserList())
            return null;
        }
        dataReqAuto.clear();
        if(o instanceof File) {
            ContentType prex=null;
            try {
                prex=ContentType.safeValueOf(s.split("\\.",2)[1]);
                ByteBuf byteBuf = Unpooled.buffer(Math.toIntExact(file.length()));
                byte[] bytes=new byte[Math.toIntExact(file.length())];
                ByteBuffer byteBuffer=ByteBuffer.wrap(bytes);
                if (!file.exists()){
                    file.createNewFile();
                }
                try {
                    FileChannel fileChannel=FileChannel.open(Paths.get(file.getPath()), StandardOpenOption.READ);
                    fileChannel.read(byteBuffer);
                    fileChannel.close();
                    //len=byteBuf.setBytes(0,fileChannel,0, Math.toIntExact(fileChannel.size()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                byteBuf.writeBytes(bytes);
                FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
                response.headers().add(HttpHeaderNames.CONTENT_TYPE, prex);
                response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
                ctx.writeAndFlush(response);
//                JSON.toJSONString(UserGet.getUserList())
                return null;
            }catch (Exception e) {
                try {
                    file.createNewFile();
                    FileChannel fileWrite = new FileInputStream(file).getChannel();
                    FileChannel fileChannel = FileChannel.open(Paths.get(((File) o).getPath()), StandardOpenOption.READ);
                    fileChannel.transferTo(0, fileChannel.size(), fileWrite);
                    fileWrite.close();
                    fileChannel.close();
                } catch (IOException ie) {
                    ie.printStackTrace();
                }
                byte[] bytes = ("已经保存到:  " + file.getName()).getBytes();
                ByteBuf byteBuf = Unpooled.buffer(bytes.length);
                byteBuf.writeBytes(bytes);
                FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
                response.headers().add(HttpHeaderNames.CONTENT_TYPE, ContentType.safeValueOf("txt"));
                response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
                ctx.writeAndFlush(response);
//                JSON.toJSONString(UserGet.getUserList())
                return null;
            }
        }
        return "flase";
    }


    @Api(def = "closeCloude")
    public ChannelAwait closeCloude(ChannelHandlerContext ctx, @GetParm String user) throws Exception {
//        final CountDownLatch parserCtl = new CountDownLatch(paths.size());
        CloudLocal.closeCloudeUser(user);
        return new ChannelAwait() {};
    }

    @Api(def = "removeCloudeFile")
    public String removeFileCloude(ChannelHandlerContext ctx,  Map<String,String> map) throws Exception {
//        final CountDownLatch parserCtl = new CountDownLatch(paths.size());
        String s = null;
        String path = null;
        try {
            s = URLDecoder.decode(map.get("file"), "utf-8");
            path = URLDecoder.decode(map.get("path"), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String username= map.get("user");
        FileTrigger.TargetFile targetFile=new FileTrigger.TargetFile();
        // 找到最后一个斜杠的位置（无论是正斜杠还是反斜杠）
        int lastSlashIndex = Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\'));

        // 分割字符串
        String part1 = path.substring(0, lastSlashIndex);
        String part2 = path.substring(lastSlashIndex + 1);
        targetFile.target=part2;
        targetFile.root=part1;
        targetFile.syb=0;
        targetFile.user=username;
        targetFile.path=s.replace(targetFile.root,"");

        CloudeListenCaset.FactortCloudeLisentCaset().dataCloud.sendque(targetFile);

        CloudBin cloudBin=CloudLocal.getSynContainer().Mapbin.get(username);
        CloudPage cloudPage=cloudBin.synMap.get(new File(path));
        cloudPage.removeNode(s);
        return "成功";
    }


    @Api(def = "moveCloudeFile")
    public ChannelAwait moveFileRemote(ChannelHandlerContext ctx, @GetParm Map<String,String> map) throws Exception {
//        final CountDownLatch parserCtl = new CountDownLatch(paths.size());
        String s = null;
        try {
            s = URLDecoder.decode(map.get("file"), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String username= map.get("name");
        String startUser= map.get("startUser");
        String path= map.get("path");
        if (checkNull(s,username,path,startUser)){
            return null;
        }
        UseOperta.OpertaFile opertaFile=new UseOperta.OpertaFile();
        UserContext.Task task=null;
        if (username.equals(UDPclient.userlocal.username)||username.equals(startUser)){
            opertaFile.t=path;
            opertaFile.o=s;
            opertaFile.ou=UDPclient.userlocal.username;
            opertaFile.syb=1;
            UserContext userContext=UDPclient.getUser(username);
            short id = userContext.newQueue();
            opertaFile.bid=id;
            task=userContext.newTask(id);
            UseOperta.addOpera(opertaFile,username);
        }else {
            opertaFile.t=path;
            opertaFile.o=s;
            opertaFile.ou=UDPclient.userlocal.username;
            opertaFile.syb=-1;
            UseOperta.add(opertaFile,username);
            UserContext userContext=UDPclient.getUser(username);
            short id = userContext.newQueue();
            task=userContext.newTask(id);
        }

        ChannelPromise promise = ctx.newPromise();
//        task.block.setMode(-1);
        String finalS = s;
        task.block.add(()->{
            try {
                // 操作完成，设置Promise成功
                promise.setSuccess();

                File caFile=new File(XmlCreate.userCache+"/"+SHAutils.getMD5(finalS,false));
                if (caFile.exists()){
                    caFile.delete();
                }
            } catch (Exception e) {// 操作失败，设置Promise失败
                promise.setFailure(e);
            }
        });
        task.inLock();
        promise.addListener(r->{
            if (r.isSuccess()) {
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
        });

        return new ChannelAwait() {};
    }

    @Api(def = "getVideo")
    public void getVideoShow(ChannelHandlerContext ctx, FullHttpRequest req, Map<String,Object> map) throws Exception {
        String username= (String) map.get("u");
        String path= (String) map.get("p");
        Integer start= (Integer) map.get("s");
        Integer len= (Integer) map.get("l");
        path=":datafl&:"+path;

        AutoBuffer autoBuffer=new AutoBuffer(username);
        byte[] bytes=autoBuffer.reqData(path,start,len);
        ByteBuf byteBuf = Unpooled.wrappedBuffer(bytes,0,len);
//        byteBuf.writeBytes(bytes);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
        response.headers().add(HttpHeaderNames.CONTENT_TYPE, ContentType.safeValueOf(Tool.getType(path)));
//        response.headers().add("filename", new File(s).getName());
        response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
        ctx.writeAndFlush(response);
    }
    @Api(def = "getVideodata")
    public void getVideodata(ChannelHandlerContext ctx, FullHttpRequest req, Map<String,Object> map) throws Exception {
        String username= (String) map.get("u");
        String path= (String) map.get("p");
        Integer start= (Integer) map.get("s");
        Integer len= (Integer) map.get("l");
        path=":datafl&:"+path;

        FullHttpResponse response =null;

        String rd=req.headers().get("Range");
        int p1=rd.lastIndexOf("=");
        rd=rd.substring(p1+1);
        int p=rd.lastIndexOf("-");
        String d0=rd.substring(0,p);
        String d1=rd.substring(p+1);

        if (d1 == null || "".equals(d1)) {
            AutoBuffer autoBuffer=new AutoBuffer(username);
            byte[] bytes=autoBuffer.reqData(path,start,len);
            autoBuffer.clear();
            ByteBuf byteBuf = Unpooled.wrappedBuffer(bytes,0,len);

            String range = "bytes " + d0 + "-" + (byteBuf.readableBytes() + Integer.valueOf(d0)-1) + "/" + bytes.length*2;
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.PARTIAL_CONTENT, byteBuf);
            response.headers().add(HttpHeaderNames.CONTENT_TYPE, ContentType.safeValueOf("mp4").Type());
            response.headers().add(HttpHeaderNames.CONTENT_RANGE, range);//"text/html;charset=utf-8"
            response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
        } else {
            Integer rangestart = Integer.valueOf(d0);
            Integer rangeend = Integer.valueOf(d1);
            AutoBuffer autoBuffer=new AutoBuffer(username);
            byte[] bytes=autoBuffer.reqData(path,rangestart,rangeend);
            autoBuffer.clear();
            ByteBuf byteBuf = Unpooled.wrappedBuffer(bytes,0,rangeend - rangestart);
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
            response.headers().add(HttpHeaderNames.CONTENT_TYPE, ContentType.safeValueOf("mp4").Type());
//            response.headers().add(HttpHeaderNames.CONTENT_RANGE, range);//"text/html;charset=utf-8"
            response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());

        }
        ctx.writeAndFlush(response);
    }

    @Api(def = "reViewPic")
    public ChannelHandlerContext getPicView(ChannelHandlerContext ctx,@GetParm Map<String,String> map) throws Exception {
//        final CountDownLatch parserCtl = new CountDownLatch(paths.size());
        String s= null;
        try {
            s = URLDecoder.decode(map.get("file"),"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String p=XmlParser.cloudecache +"/cache_"+ SHAutils.getMD5(s, true)+Tool.getPrex(s);
        File file=new File(p);
        ByteBuf byteBuf=null;
        if (file.exists() && file.length()>0){
            byte[] bytes= Files.readAllBytes(file.toPath());
            byteBuf = Unpooled.buffer(bytes.length);
            byteBuf.writeBytes(bytes);
        }else {
            String username= map.get("name");
            DataReqAuto dataReqAuto =new DataReqAuto(username);
            Object o= dataReqAuto.reqFile(":cacheF&:"+s);
            if (o!=null){
                DataReqAuto.writdata(p,o);
            }
            byteBuf = Unpooled.buffer(dataReqAuto.rev.length);
            byteBuf.writeBytes(dataReqAuto.rev);
            dataReqAuto.clear();
        }
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
        try {
            response.headers().add(HttpHeaderNames.CONTENT_TYPE, ContentType.safeValueOf(s.split("\\.")[1]));
        }catch (Exception e){
            response.headers().add(HttpHeaderNames.CONTENT_TYPE, "application/"+s.split("\\.")[1]);
        }
        response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
        ctx.writeAndFlush(response);
//            JSON.toJSONString(UserGet.getUserList())
        return null;
    }

    @Api(def = "getCloudePage")
    public String getCloudPageList(FullHttpRequest msg){
        String username=msg.uri().split("\\?")[1];
        CloudBin cloudBin=null;
        if (UDPclient.userlocal.username.equals(username)){
            return null;
        }
        if (!CloudLocal.isInitSynContainer()){
            return "{}";
        }else {
            cloudBin=CloudLocal.getSynContainer().Mapbin.get(username);
        }
        if (cloudBin==null){
            return "{}";
        }
        Map m=new LinkedHashMap();
        cloudBin.synMap.forEach((k,v)->{
            m.put(v.targetPath,v.getTargetFile());
        });

        String json=JSON.toJSONString(m);

//        json=json.replaceAll("\\\\\\\\null\"", "\"");  ;
        return json;
//        return JSON.toJSONString(m);
    }
    //{知识图谱=TargetFile{syb='0'user='null', path='null'}, home=TargetFile{syb='0'user='null', path='null'}, tu=TargetFile{syb='0'user='null', path='null'},...}
    @Api(def = "getCloudeTrigger")
    public String getCloudTriggerList(@GetParm Long time){

        if (!CloudLocal.isInitSynContainer()) {
            if (time==null){
                CloudLocal.init();
//                CloudeListenCaset.FactortCloudeLisentCaset();
            }else {
                CloudLocal.init(time);
//                CloudeListenCaset.FactortCloudeLisentCaset(Math.toIntExact(time));
            }
        }
        CloudBin cloudBin=CloudLocal.getSynContainer().localbin;

//        Map m=new LinkedHashMap();
        Map m=new JSONObject();
        cloudBin.map.forEach((k,v)->{
//            k=k.replace("\\","");
            m.put(v.targetPath,v.AbsolutePath);
        });
//        String json=JSON.toJSONString(m);
        String json=m.toString();
        return json;
//        return JSON.toJSONString(m);
    }
    @Api(def = "removeCloudeTrigger")
    public String removeCloudTriggerList(@GetParm String path){
        if (!CloudLocal.isInitSynContainer()) {
//            CloudLocal.init();
//            CloudeListenCaset.FactortCloudeLisentCaset();
        }
        CloudBin cloudBin=CloudLocal.getSynContainer().localbin;
        AtomicReference<String> filePath=new AtomicReference<>();
        cloudBin.map.forEach((k,v)->{
            if (Objects.equals(path,k)){
                filePath.set(k);
            }
        });
        FileTrigger fileTrigger= cloudBin.map.get(filePath.get());
//        String filename=fileTrigger.fileName;
        cloudBin.map.remove(filePath.get(),fileTrigger);
        if (CloudeListenCaset.cloudeListenCaset!=null &&
                CloudeListenCaset.cloudeListenCaset.fileRunner!=null){
            CloudeListenCaset.cloudeListenCaset.fileRunner.removeListenDirRuning(fileTrigger);
        }
        try {
            Document document=null;
            SAXReader reader=new SAXReader();
            document=reader.read(new File(XmlCreate.userCloudefile +".xml"));
            Element elementroot=document.getRootElement();
            String root=elementroot.attributeValue("p");
            List<Element> elements=elementroot.elements();
            elements.forEach(element -> {
                String name=element.attributeValue("f");
                String dirname=root+"/"+name;
                if (dirname.equals(fileTrigger.fileName)){
                    elementroot.remove(element);
                }
            });
            XmlParser.SaveXml(document,XmlCreate.userCloudefile +".xml");
            File file=new File(fileTrigger.fileName);
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(Thread.currentThread().getName());
        }

        return "";
//        return JSON.toJSONString(m);
    }

    @Api(def = "getMapTend")
    public String getMapTend(@GetParm String username){
        CloudLocal.getSynContainer();
        TendMap tendMap=TendFactory.getTm(username);
        Map<String, List<String>> stringListMap=TendFactory.getMapList(tendMap);

        String json=JSON.toJSONString(stringListMap);
        return json;
//        return JSON.toJSONString(m);
    }



    @Api(def = "cloudeOn")
    public String getCloudeOn(@GetParm Long time){
        if (time==null){
            CloudLocal.init(FileMonitor.defaulttime);
        }else {
            if (time<20*1000){
                return "时长过短";
            }
            CloudLocal.init(time);
        }
        return "cloudeOff";
//        return JSON.toJSONString(m);
    }

    @Api(def = "cloudeOff")
    public String getCloudeOff(){
        CloudeListenCaset.FactortCloudeLisentCaset().getFileRunner().manualStop();
        return "cloudeOn";
//        return JSON.toJSONString(m);
    }

    @Api(def = "cloudeClear")
    public String cloudeClear(){
        CloudLocal.CloudClear();
        return "成功";
//        return JSON.toJSONString(m);
    }

    @Api(def = "immediate")
    public String getImmediate(){
        CloudeListenCaset.FactortCloudeLisentCaset().immediate();
        return "成功";
//        return JSON.toJSONString(m);
    }

    @Api(def = "CloudeTime")
    public String TimeTran(@GetParm Map<String,String> map){
        String user=map.get("user");
        String time=map.get("time");

        String[] strings=time.split(":");

        strings[0]="";
        String[] string1=getCurrentDateStringArray();
        boolean b=false;
        for (int i = 1; i < strings.length; i++) {
            try {
                if (strings[i]!=null && !strings[i].equals("")){
                    int v=Integer.valueOf(strings[i]).compareTo(Integer.valueOf(string1[i-1]));
                    if (v<0){
                        string1[i-1]=strings[i];
                        b=true;
                    }else{
                        if (v==0){
                            continue;
                        }
                        if(b){
                            string1[i-1]=strings[i];
                        }else {
                            break;
                        }
                    }
                }
            }catch (Exception e){
                e.getMessage();
            }
        }

        LocalDateTime dateTime=convertToDateTime(string1,0);

        long longtime=dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        if ((System.currentTimeMillis()-longtime)>FileMonitor.settime){
            try {
                CloudeListenCaset.FactortCloudeLisentCaset().getFileRunner().stop();
//                CloudeListenCaset.cloudeListenCaset.fileRunner.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            CloudBin cloudBin=CloudLocal.getSynContainer().localbin;

            for (java.util.Map.Entry<String,FileTrigger> entry:cloudBin.map.entrySet()){
                Set<FileTrigger.TargetFile> set=cloudBin.timeSend0(entry.getValue(),longtime);
                synchronized (DataCloud.class){
                    Map set1=DataCloud.setMap.get(user);
                    if (set1==null){
                        set1=new ConcurrentHashMap();
                        DataCloud.setMap.put(user,set1);
                    }
                    for (FileTrigger.TargetFile targetFile:set){
                        set1.put(targetFile,targetFile);
                    }
                }
            }
        }catch (Exception | Error e){
            e.printStackTrace();
        }

        CloudeListenCaset.FactortCloudeLisentCaset().immediate();
        return "成功";
//        return JSON.toJSONString(m);
    }

    @Api(def = "autoMap")
    public String autoMapTendMap(@GetParm String username){
        UserContext userContext=UDPclient.mainDataQueue.getUserContext(username);
        ReqCloudeAutoMap reqCloudeAutoMap=new ReqCloudeAutoMap(username,userContext.newQueue(),true);
        ReqCloudeAutoMap.st=false;
        reqCloudeAutoMap.process();
        return "cg";
    }

}
