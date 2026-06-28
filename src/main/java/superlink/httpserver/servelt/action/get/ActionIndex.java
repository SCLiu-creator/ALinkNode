package superlink.httpserver.servelt.action.get;

import com.alibaba.fastjson2.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.*;
import superlink.filemanage.classprocess.property.ReInfuse;
import superlink.filemanage.classprocess.property.reInject;
import superlink.filemanage.xmltool.XmlCreate;
import superlink.httpserver.servelt.action.buf.SaveZip;
import superlink.httpserver.servelt.httptype.ContentType;
import superlink.init.InitClass;
import superlink.init.UserLinkCon;
import superlink.udpbind.client.recives.Senders;
import superlink.udpbind.client.recives.datalen.DataLength;
import superlink.udpbind.cloude.CloudBin;
import superlink.udpbind.cloude.CloudLocal;
import superlink.udpbind.remote.invoking.LinkCallTemplate;
import superlink.udpbind.servlet.ClearUser;
import superlink.util.JackJson;
import superlink.util.SHAutils;
import superlink.util.datastack.DataListCon;
import superlink.util.datastack.DataListRW;
import superlink.filemanage.xmltool.UserGet;
import superlink.filemanage.xmltool.XmlParser;
import superlink.httpserver.servelt.WebController;
import superlink.httpserver.servelt.action.*;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.datalen.AutoBuffer;
import superlink.udpbind.client.recives.datalen.AutoData;
import superlink.udpbind.client.recives.datalen.DataReqAuto;
import superlink.udpbind.cloude.util.TendFactory;
import superlink.udpbind.controller.Controller;
import superlink.udpbind.usedata.User;
import superlink.udpbind.usedata.UserRequest;
import superlink.udpbind.user.UserInNetFind;
import superlink.util.Tool;
import superlink.util.Utils;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import static superlink.udpbind.client.UDPclient.*;

@WebController(name = "Index")
public class ActionIndex implements Action {

    @Api(def = "Loginlist")
    public String getLoginlist(ChannelHandlerContext ctx, FullHttpRequest msg){
        if (UDPclient.userlocal!=null){
            if (UDPclient.userlocal.address!=null){
                ArrayList<User> list=new ArrayList<User>();
                list.add(UDPclient.userlocal);
                return JSON.toJSONString(list);
            }
        }
        return JSON.toJSONString(UserGet.userList);
    }

    @Api(def = "getUser")
    public String getUserLocal(){
        return UDPclient.userlocal.toString();
    }

    private static long runtime=System.currentTimeMillis();
    @Api(def = "Userlist")
    public String getUserlist(ChannelHandlerContext ctx, FullHttpRequest msg,
                              @GetParm(name = "start") Integer integer){
        if (UDPclient.userlocal.address==UDPclient.userlocal.inaddress){
            if (runtime-System.currentTimeMillis()>3000){
                new UserInNetFind().scanIp();
                runtime=System.currentTimeMillis();
            }
        }else {
            Controller.upgradeList();
        }
        ClearUser.chaek();

//        String s=msg.uri().split("\\?")[1];
        User[] users= userMap.values().toArray(new User[userMap.size()]);
        int l=users.length;
        int p;
        List<User> users1=new ArrayList<>(16);
        for (int i=0;i<10;i++){
            p=i+integer;
            if (p<l){
                users1.add(users[p]) ;
            }
        }
        String send= JackJson.toJson(users1);
        return send;
    }

    @Api(def = "secrchUserlist")
    public String secrchUserlist(@GetParm String s){
        String para=s;
        if (para.contains("U:")){
            para=s.replace("U:","");
            Controller.stringQuery(para);
            try {
                Thread.sleep(700);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        }else {
            Controller.upgradeList();
        }
        List<User> users1=new ArrayList<>(16);
        String finalPara = para;
        userMap.forEach((k, v)->{
            if ((v.username).contains(finalPara)|| v.nickName.contains(finalPara)){
                users1.add(v) ;
            }
        });

        return JSON.toJSONString(users1);
    }

    @Api(def = "showUserIp")
    public String showUserIp(@GetParm String s){

        User user=userMap.get(s);

        return user.toString();
    }

    @Api(def = "secrchBindlist")
    public String secrchBindlist(@GetParm String para){
        System.out.println("secrchBindlist");
        ArrayList arrayList=new ArrayList();
        bindUser.forEach((u, user) -> {
            if (user.nickName.contains(para)||user.username.contains(para) ){
                arrayList.add(user);
            }
        });
        return JSON.toJSONString(arrayList);
    }

    @Api(def = "Bindlist")
    public String getBindlist(){
        System.out.println("getBindlist");
        AtomicReference<User> userU=new AtomicReference<>();
        ArrayList<User> arrayList=new ArrayList(bindUser.size());
        bindUser.forEach((u, user) -> {
            UserContext userContext=UDPclient.getUser(u);
            User user1=new User();
            if(user==null)return;
            user1.username=userContext.userName;
            user1.nickName=user.nickName;
            user1.address=userContext.inetAddress;
            user1.port=userContext.port;
            arrayList.add(user1);
        });
        return JSON.toJSONString(arrayList);
    }

    @Api(def = "getAutoLink")
    public List<String> getAutoLink(){
        ArrayList<String> arrayList=new ArrayList(UserLinkCon.linkConList.size());
        for (UserLinkCon user:UserLinkCon.linkConList){
            arrayList.add(user.user);
        }
        return arrayList;
    }

    @Api(def = "setAutoLink")
    public String setAutoLink(@GetParm String username){
        UserLinkCon userLinkCon = null;
        for (UserLinkCon user:UserLinkCon.linkConList){
            if(user.user.equals(username)){
                userLinkCon=user;
            }
        }

        if(userLinkCon==null){
            UserLinkCon.add(username);
        }
        return "true";
    }
    @Api(def = "unsetAutoLink")
    public String UnsetAutoLink(@GetParm String username){
        UserLinkCon userLinkCon = null;
        for (UserLinkCon user:UserLinkCon.linkConList){
            if(user.user.equals(username)){
                userLinkCon=user;
            }
        }

        if(userLinkCon!=null){
            UserLinkCon.remove(username);
        }
        return "true";

    }


    @Api(def = "Picture")
    public String getPicture(@GetParm String path,ChannelHandlerContext ctx){
        File file=new File(path);
        if (!file.exists()){
            file=new File(XmlParser.dir+path);
        }
        try {
            int len= (int) file.length();
            FileInputStream fis=new FileInputStream(file);
            ByteBuf byteBuf=Unpooled.directBuffer(1000,len);

            byte[] tmp = new byte[4096]; // 你可以根据需要调整这个缓冲区大小
            int bytesRead;
            // 处理缓冲区中的数据（这里只是一个示例，你可以根据需要进行处理）
            while ((bytesRead = fis.read(tmp)) != -1) {
                byteBuf.writeBytes(tmp, 0, bytesRead);
            }
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
            response.headers().add(HttpHeaderNames.CONTENT_TYPE, "png/jpg");
            response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
            ctx.writeAndFlush(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Api(def = "headPicture")
    public void getHeadPicture(ChannelHandlerContext ctx,@GetParm String username) throws Exception {
        UserContext userContext=UDPclient.mainDataQueue.getUserContext(username);
        String cacheFile=XmlParser.cloudecache + username+"headPic";
        File file=new File(cacheFile);
        if (file.exists()){
            ByteBuf byteBuf = Unpooled.buffer((int) file.length());
            byteBuf.writeBytes(Files.readAllBytes(file.toPath()));
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
            response.headers().add(HttpHeaderNames.CONTENT_TYPE, ContentType.safeValueOf(""));
            response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
            ctx.writeAndFlush(response);
        }else {
            DataReqAuto dataReqAuto =new DataReqAuto(username);
            Object o = dataReqAuto.reqFile(":headPic&:headPic");
            ByteBuf byteBuf=null;
            if (o==null){
                byteBuf= Unpooled.copiedBuffer(new byte[0]);
            }else {
                if(o instanceof byte[]){
                    byteBuf= Unpooled.copiedBuffer((byte[]) o);
                    DataLength.writdata(cacheFile,o);
                }
                if(o instanceof ByteBuffer) {
                    byteBuf= Unpooled.copiedBuffer(((ByteBuffer)o).array());
                    DataLength.writdata(cacheFile,o);
                }
                if(o instanceof File) {
                    byteBuf= Unpooled.copiedBuffer(Files.readAllBytes(((File)o).toPath()));
                    DataLength.writdata(cacheFile,o);
                }
            }

            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
            response.headers().add(HttpHeaderNames.CONTENT_TYPE, "text/plain");
            response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
            ctx.writeAndFlush(response);
        }
    }

    @Api(def = "map")
    public Map<String, List<User>> getmap(ChannelHandlerContext ctx, FullHttpRequest msg){
        String[] strings=msg.uri().split("\\\\");
        String s=strings[strings.length-1];
        System.out.println("getBindlist");
        Map map=new HashMap();
        map.put(Controller.userRequest.username, TendFactory.getTm(Controller.userRequest.username));
        return map;
    }

    @Api(def = "secrchUserBind")
    public String secrchUserBind(@GetParm String username){
        UserContext user= UDPclient.getUser(username);
        HashMap hashMap=new HashMap();
        if (user==null){
            hashMap.put("e","false");
            hashMap.put("p",String.valueOf(-1));
        }else {
            if (!Objects.equals(userlocal.username,username)){
                DataListRW dataListRW=DataListCon.getListRW(UDPclient.userlocal.username);
                byte b=dataListRW.find(username);
                hashMap.put("e","true");
                hashMap.put("p",String.valueOf(b));
            }
            hashMap.put("sort",user.sort);
        }

        return JSON.toJSONString(hashMap);
    }
    @Api(def = "setUserPermiss")
    public String setUserPermiss(@GetParm Map<Object,Object> map){
        String username= (String) map.get("u");
        int p= (Integer) map.get("p");
        DataListRW dataListRW=DataListCon.getListRW(UDPclient.userlocal.username);
        try {
            dataListRW.write(username,(byte) p);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "true";

    }
    @Api(def = "UnUserPermiss")
    public String UnUserPermiss(@GetParm Map<Object,Object> map){
        String username= (String) map.get("u");
        int p= (Integer) map.get("p");
        DataListRW dataListRW=DataListCon.getListRW(UDPclient.userlocal.username);
        try {
            dataListRW.write(username,(byte) -1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "true";

    }

    @Api(def = "linkUser")
    public ChannelAwait binduser(FullHttpRequest msg, ChannelHandlerContext ctx){
        String[] strings=msg.uri().split("\\?");
        String s=strings[1];
        User user = userMap.get(s);
        UserRequest userRequest= Tool.UsertoUserRequestbind(user);
        Controller.requestNode(userRequest);
        addobjectList(ctx,s);
        return new ChannelAwait(){};
    }
    @Api(def = "inlinkUser")
    public ChannelAwait inbinduser(@GetParm String username, ChannelHandlerContext ctx){
        User user = (User) userMap.get(username);
        Controller.requestNodeIn(user);
        addobjectList(ctx,username);
        return new ChannelAwait(){};
    }
    public static volatile HashMap<String,Map<String,Object>> objectList=new HashMap<>();

    @Api(def = "reCallLinkUser")
    public ChannelAwait reCallLinkUser(@GetParm String username, ChannelHandlerContext ctx){
        User user = (User) userMap.get(username);
        Controller.requestNodeReturn(user);
        addobjectList(ctx,username);
        return new ChannelAwait(){};
    }
    @Api(def = "unLinkUser")
    public String unLinkUser(@GetParm String username, ChannelHandlerContext ctx){
        try {
            UserContext userContext=UDPclient.mainDataQueue.delUser(username);
            Senders.Sends(userContext.getBothId(),0,userContext.inetAddress,userContext.port,
                    "DE".getBytes());
            Senders.Sends(userContext.getBothId(),0,userContext.inetAddress,userContext.port,
                    "DE".getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        CloudBin cloudBin=CloudLocal.getSynContainer().Mapbin.get(username);
        CloudLocal.getSynContainer().remove(cloudBin);

        return "ok";
    }

    @reInject(name = "uz")
    SaveZip saveZip;

    @Api(def = "getUI")
    public String getUI(@GetParm String username){
        try {
            UserContext userContext=UDPclient.getUser(username);
            if(userContext==null)return "none";
            LinkCallTemplate linkCallTemplate=new LinkCallTemplate(UDPclient.userlocal.username,username);
            byte[] bytes = linkCallTemplate.req("Linkserver.getUI");
            File file=new File(InitClass.webpath);
            file=new File(file.getParent());
            file=new File(file.getParent());
            InputStream inputStream = new ByteArrayInputStream(bytes);
            Utils.unZip(XmlParser.extend+"ui",inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "ok";
    }

    @Api(def = "Logout")
    public void logout(){
        UDPclient.over();
    }


    @Api(def = "scanIp")
    public void scanIp(@GetParm String ip){
        try {
            if (ip ==null || Objects.equals(ip,"")){
                InetAddress inetAddress=UDPclient.userlocal.inaddress;
                String subnetMask=Utils.getSubnetMask(inetAddress);
                InetAddress address=null;
                if (subnetMask!=null){
                    address=Utils.getBroadcastAddress(inetAddress,subnetMask);
                }else {
                    new UserInNetFind().scanIp();
                    return;
                }
                byte[] bytes=new byte[6];
                User user=UDPclient.userlocal.copy();
                user.choose = 1;
                String data= "TF"+user.toString();
                bytes= Utils.byteMerger(bytes,data.getBytes());
                DatagramPacket packet=new DatagramPacket(bytes,bytes.length);
                packet.setAddress(address);
                UserInNetFind.BroadcastSend(packet);
            }else {
                InetAddress inetAddress=InetAddress.getByName(ip);
                byte[] bytes=new byte[6];
                User user=UDPclient.userlocal.copy();
                user.choose = 1;
                String data= "TF"+user.toString();
                bytes= Utils.byteMerger(bytes,data.getBytes());
                DatagramPacket packet=new DatagramPacket(bytes,bytes.length);
                packet.setAddress(inetAddress);
                UserInNetFind.BroadcastSend(packet);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Api(def = "proxyTcp")
    public void proxyTcp(Map<String,String> map){
        String user = map.get("user");
        String sp = map.get("sp");
        String cp = map.get("cp");
        Controller.ProxyTcpPort(user,Integer.parseInt(sp),Integer.parseInt(cp));
    }


    public static void addobjectList(ChannelHandlerContext ctx,String username){
        ChannelPromise promise = ctx.newPromise();
        Runnable runnable=(()-> {
            try {// 操作完成，设置Promise成功
                promise.setSuccess();
            } catch (Exception e) {// 操作失败，设置Promise失败
                promise.setFailure(e);

            } });
        Map map=objectList.get(username);
        if (map==null){
            map=new ConcurrentHashMap();
            objectList.put(username,map);
        }
        map.put(username,runnable);

        // 2. 设置超时任务（例如 5 秒后超时）
        final long timeoutMillis = 5000;
        Runnable timeoutTask = () -> {
            if (!promise.isDone()) { // 如果 Promise 未完成
                promise.setFailure(new TimeoutException("Operation timed out"));
                objectList.remove(username); // 清理回调
            }
        };
        // 在 EventLoop 中调度超时任务
        ctx.channel().eventLoop().schedule(timeoutTask, timeoutMillis, TimeUnit.MILLISECONDS);

        // 添加监听器，当Promise完成时执行
        promise.addListener(f -> {
            if (f.isSuccess()) {
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
    }

}
