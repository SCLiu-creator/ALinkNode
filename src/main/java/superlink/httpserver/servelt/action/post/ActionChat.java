package superlink.httpserver.servelt.action.post;


import com.alibaba.fastjson2.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.*;
import org.dom4j.Document;
import org.dom4j.Element;
import superlink.filemanage.xmltool.XmlCreate;
import superlink.filemanage.xmltool.XmlParser;
import superlink.httpserver.servelt.action.Action;
import superlink.httpserver.servelt.action.Api;
import superlink.httpserver.servelt.action.ChannelAwait;
import superlink.httpserver.servelt.action.GetParm;
import superlink.httpserver.servelt.WebController;
import superlink.httpserver.servelt.httptype.ContentType;
import superlink.udpbind.chat.*;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.recives.Senders;
import superlink.udpbind.client.recives.datalen.AutoBuffer;
import superlink.udpbind.client.recives.datalen.DataBuffer;
import superlink.udpbind.client.recives.datalen.dataCache.BufferDataCon;
import superlink.util.JackJson;
import superlink.util.Tool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static superlink.util.Utils.pathSet;

@WebController(name = "chat")
public class ActionChat implements Action {

    public static Map<String, Callable> chatReCallMap=new ConcurrentHashMap<>();

    @Api(def = "chats")
    public List<Map> getUserChats(@GetParm String username,ChannelHandlerContext ctx){
        if (username.equals("null") || username.equals("undefined")){
            return null;
        }
        if(UDPclient.mainDataQueue.getUserContext(username)==null)return null;
        AutoBuffer autoBuffer = new AutoBuffer(username);
        byte[] bytes=autoBuffer.reqData(":chats&:");
        autoBuffer.clear();
        Document document = XmlParser.byetToDocument(bytes);
        Element root=document.getRootElement();
//        ChatGroup chatGroup = ChatContrain.getChatBins(username);
        List list = new ArrayList();
        for(Element element:(List<Element>)root.elements()) {
            Map map=new HashMap();
            String name = element.attributeValue("name");
            Integer num = Integer.valueOf(element.attributeValue("num"));
            map.put("name",name);
            map.put("num",num);
            list.add(map);
        }
        return list;
    }

    @Api(def = "createChats")
    public Map createChats(@GetParm String name,ChannelHandlerContext ctx){
        if (name.equals("null") || name.equals("undefined") || name.equals("") || name==null){
            return null;
        }
        ChatGroupSelf chatGroupSelf = ChatContrain.getSelfChatGroup();
        ChatGs chatGs = chatGroupSelf.createCGS(name);
//        ChatGroup chatGroup = ChatContrain.getChatBins(username);

        Map map=new HashMap();
        map.put("name",chatGs.name);
        map.put("num",chatGs.num);

        return map;
    }

    @Api(def = "getSelfChats")
    public List getSelfChats(){
        ChatGroupSelf chatGroupSelf = ChatContrain.getSelfChatGroup();
        List list=new ArrayList();
        chatGroupSelf.chatGsMap.forEach((num,chatGs)->{
            Map map=new HashMap();
            map.put("num",num);
            map.put("name",chatGs.name);
            map.put("pic",null);
            list.add(map);
        });

        return list;
    }

    @Api(def = "getChats")
    public ChannelAwait getContext(Map<String,Object> map,ChannelHandlerContext ctx){
        String username= (String) map.get("user");
        String name= (String) map.get("name");
        Integer num= (Integer) map.get("num");
        if (username==null||username.equals("null") || username.equals("undefined")){
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR);
            ctx.writeAndFlush(response);
            ctx.flush();
            ctx.close();
            return null;
        }
        if(UDPclient.userlocal.username.equals(username)){
            ChatGroupSelf chatGroupSelf = ChatContrain.getSelfChatGroup();
            ChatGs chatGs = chatGroupSelf.getCGS(num);
            if (chatGs==null){
            }else {
                ArrayList list=new ArrayList();
                chatGs.ringQue.toList().forEach(chatData -> {
                    list.add(chatData.toString());
                });
                String send = JackJson.toJson(list);
                byte[] bytes = send.getBytes(StandardCharsets.UTF_8);
                FullHttpResponse response = new DefaultFullHttpResponse(
                        HttpVersion.HTTP_1_1,
                        HttpResponseStatus.OK,
                        Unpooled.wrappedBuffer(bytes) // 直接写入响应体
                );
                response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8");
                response.headers().set(HttpHeaderNames.CONTENT_LENGTH, bytes.length);
                ctx.writeAndFlush(response).addListener(future -> {
                    if (!future.isSuccess()) {
                        System.err.println("Failed to send response: " + future.cause());
                    }
                    ctx.close(); // 关闭连接
                });
                return null;
            }
        }

        ChatGroup chatGroup= ChatContrain.getChatGroups(username);
        ChatGs chatGs = chatGroup.getCGS(num);
        if (chatGs==null){
            UDPclient.getUser( username).getQueue((short)5).add(("AC"+num).getBytes(StandardCharsets.UTF_8));
            ChannelPromise promise = ctx.newPromise();
            chatReCallMap.put(username, ()-> {
                try {// 操作完成，设置Promise成功
                    promise.setSuccess();
                } catch (Exception e) {// 操作失败，设置Promise失败
                    promise.setFailure(e);
                }
                return null;
            });
            // 2. 设置超时任务（例如 5 秒后超时）
            final long timeoutMillis = 5000;
            Runnable timeoutTask = () -> {
                if (!promise.isDone()) { // 如果 Promise 未完成
                    promise.setFailure(new TimeoutException("Operation timed out"));
                    chatReCallMap.remove(username); // 清理回调
                }
            };
            // 在 EventLoop 中调度超时任务
            ctx.channel().eventLoop().schedule(timeoutTask, timeoutMillis, TimeUnit.MILLISECONDS);

            String finalUsername = username;
            promise.addListener(f -> {
                if (f.isSuccess()) {
                    AutoBuffer autoBuffer = new AutoBuffer(finalUsername);
                    byte[] bytes=autoBuffer.reqData(":chats&:");
                    autoBuffer.clear();
                    Document document = XmlParser.byetToDocument(bytes);
                    Element root=document.getRootElement();
                    for (Element element:(List<Element>)root.elements()){
                        if (element.attributeValue("num").equals(num.toString())){
                            chatGroup.createCGS(name, num);
                            break;
                        }
                    }
                    // 异步操作成功，写入HTTP响应
                    ArrayList list=new ArrayList();
                    chatGs.ringQue.toList().forEach(chatData -> {
                        if(chatData!=null){
                            list.add(chatData.toString());
                        }
                    });
                    String send = JackJson.toJson(list);
                    bytes = send.getBytes(StandardCharsets.UTF_8);
                    ByteBuf byteBuf= Unpooled.buffer(bytes.length);
                    byteBuf.writeBytes(bytes);

                    FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                    response.headers().add(HttpHeaderNames.CONTENT_TYPE, ContentType.json);
                    response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
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
        }else {
            ArrayList list=new ArrayList();
            chatGs.ringQue.toList().forEach(chatData -> {
                list.add(chatData.toString());
            });
            String send = JackJson.toJson(list);
            byte[] bytes = send.getBytes(StandardCharsets.UTF_8);
            FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK,
                    Unpooled.wrappedBuffer(bytes) // 直接写入响应体
            );

// 设置正确的 Content-Type（如果是 JSON 数据）
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, bytes.length);

// 发送响应
            ctx.writeAndFlush(response).addListener(future -> {
                if (!future.isSuccess()) {
                    System.err.println("Failed to send response: " + future.cause());
                }
                ctx.close(); // 关闭连接
            });
            return null;
        }
        return  new ChannelAwait(){};
    }

    @Api(def = "context")
    public String getContext(Map<String,Object> map){
        String username = (String)map.get("user");
        Integer num= (Integer) map.get("num");
        if (username ==null||username.equals("null") || username.equals("undefined")){
            username= UDPclient.userlocal.username;
        }
        List<ChatData> list = null;
        if(num==null||num==0){
            ChatBin chatBin= ChatContrain.getChatBin(username);
            list=chatBin.ringQue.toList();
        }else {
            ChatGs chatBin= ChatContrain.getChatGroups(username,num);
            list=chatBin.ringQue.toList();
        }

        List li=new ArrayList();
        ChatHandler.getChatHandler();
        ChatData da;
        for (ChatData data:list){
            da=data.copy();
            da.setSn();
//            if (data.tl>1000){
//                da.text= new String(DataBuffer.byteHashBuffer.get(data));
//            }
            li.add(da);
        }
        return JackJson.toJson(li);
    }

    @Api(def = "postData")
    public String postContext(@GetParm Map<String,Object> ctx, FullHttpRequest req){
        String username= (String) ctx.get("user");
        Integer num= (Integer) ctx.get("num");

        if (username.equals("null") || username.equals("undefined")){
            username= UDPclient.userlocal.username;
        }
        ChatBin chatBin;;
        if(num==null||num==0){
            chatBin = ChatContrain.getChatBin(username);
        }else {
            chatBin= ChatContrain.getChatGroups(username,num);
        }
        String text= (String) ctx.get("text");
        Integer textlen= (Integer) ctx.get("textlen");
        ChatData data=null;
        if (UDPclient.getUser(username)==null){
            //对未连接用户发送
            if (textlen<120){
                data=chatBin.add(text,null);
                data.setSn();
                data.s=0;
                data=data.copy();
                Map map=new HashMap(3);
                map.put("d",data);
                map.put("s",UDPclient.userlocal.username);
                map.put("e",username);
                String send="CH"+JSON.toJSONString(map);
                Senders.Sends(UDPclient.getServerip(),UDPclient.getSport(),send.getBytes());
                return "true";
            }else {
                return "flase";
            }
        }

        if (textlen>1000){
            text=req.content().toString(StandardCharsets.UTF_8);
            data=chatBin.add(text,null);
            UUID uuid=UUID.randomUUID();
            data.text=pathSet("data",uuid.toString());
            data.tl=textlen;
//            DataBuffer.byteHashBuffer.put(uuid.toString(),text.getBytes());
            BufferDataCon.setData(uuid.toString(),text.getBytes(),1);
        }else {
            String file= (String) ctx.get("file");
            if (file==null||"".equals(file)){
                data=chatBin.add(text,null);
            }else {
                String[] fs = file.split("\\.");
                String prex=null;
                String base=null;
                if (fs.length != 2) {
                    prex="";
                    base=file;
                }else {
                    prex=fs[1];
                    base=fs[0];
                }
                int times=1;
                File fileb=null;
                file=XmlCreate.userCache+base+"."+prex;
                while (true){
                    fileb=new File(file);
                    if(fileb.exists()){
                        file=XmlCreate.userCache+base+"("+times+")."+prex;
                        times++;
                    }else {
                        break;
                    }
                }

                ByteBuf byteBuf= req.content();

                try {
                    if (!fileb.exists()){
                        fileb.createNewFile();
                    }
                    FileOutputStream fileOutputStream=new FileOutputStream(fileb);
                    FileChannel fileChannel=fileOutputStream.getChannel();
                    int len=byteBuf.capacity();
                    byteBuf.readBytes(fileChannel,0,len);
                }catch (IOException e){
                    e.printStackTrace();
                }
                Integer filelen=(Integer) ctx.get("filesize");
                data=chatBin.add(text,new File(file));
                data.fl=filelen;
            }
        }

        data.n=num;
        data.setSn();
        data=data.copy();
        if(username.equals(UDPclient.userlocal.username)){
            if (chatBin instanceof ChatGs){
                ChatGs chatGs = (ChatGs) chatBin;
                if(chatGs.username.equals(UDPclient.userlocal.username)){
                    data.s=-1;
                    ChatHandler.getChatHandler();
                    ChatHandler.adddata(username,data);
                    return "true";
                }
            }
        }
//        data.text=text;
        ChatHandler.getChatHandler();
        ChatHandler.adddataBuffer(username,data);
        return "true";
    }

    @Api(def = "delData")
    public String delContext(@GetParm String username, Map<String,Object> ctx){
        String text=(String) ctx.get("text");
        String file=(String) ctx.get("file");
        String date=(String) ctx.get("date");
        String user=(String) ctx.get("user");
        String snString=(String) ctx.get("sn");
        Integer num= (Integer) ctx.get("num");
        ChatBin chatBin;
        if(num==null||num==0){
            num=0;
            chatBin = ChatContrain.getChatBin(username);
        }else {
            chatBin= ChatContrain.getChatGroups(username,num);
        }
        ChatData data=new ChatData(user,date,text,file);
        data.n=num;
        data.setSn();
        if (!snString.equals(data.sn)){
            return null;
        }
        data.i=1;
        chatBin.remove(data);
        if(UDPclient.userlocal.username.equals(user)&&num==0){
            return null;
        }
        if (ChatHandler.chatHandler==null){
            ChatHandler.chatHandler=new ChatHandler();
            UDPclient.executorService.execute(ChatHandler.chatHandler);
        }
        ChatHandler.adddataBuffer(username,data);
        return null;
    }

    @Api(def = "getData")
    public void getData(@GetParm Map<String,String> map, ChannelHandlerContext ctx){
        try {
            String file=map.get("file");
            File file1 = new File(file);
            if (!file1.exists()){
                return;
            }
            Path parentPathu = Paths.get(XmlCreate.userChat).normalize();
            Path parentPathc = Paths.get(XmlCreate.userCache).normalize();
            Path childPath = Paths.get(file).normalize();
            if (childPath.startsWith(parentPathu) || childPath.startsWith(parentPathc)){
                byte[] bytes=Files.readAllBytes(file1.toPath());
                ByteBuf byteBuf= Unpooled.buffer(bytes.length);
                byteBuf.writeBytes(bytes);
                String prex=Tool.getPrexs(file);
                ContentType type=ContentType.safeValueOf(prex);
                FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
                response.headers().add(HttpHeaderNames.CONTENT_TYPE, type);
                response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
                ctx.writeAndFlush(response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
