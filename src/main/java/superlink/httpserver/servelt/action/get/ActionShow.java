package superlink.httpserver.servelt.action.get;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import superlink.httpserver.servelt.action.Api;
import superlink.httpserver.servelt.action.GetParm;
import superlink.httpserver.servelt.WebController;
import superlink.init.InitClass;
import superlink.udpbind.chat.ChatBin;
import superlink.udpbind.chat.ChatContrain;
import superlink.udpbind.chat.ChatData;
import superlink.udpbind.chat.ChatHandler;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.Senders;
import superlink.udpbind.client.recives.data.blockBuffer.ByteStream;
import superlink.udpbind.cloude.CloudBin;
import superlink.udpbind.cloude.CloudLocal;
import superlink.udpbind.farme.ShowQr;
import superlink.udpbind.usedata.DataRequest;
import superlink.util.JackJson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebController(name = "show")
public class ActionShow  {

    @Api(def = "cPathList")//cloude list模式
    public String cPathList(Map<String,String> map){
        String name=map.get("name");
        String ab=map.get("ab");
        String path=map.get("path");
        CloudBin cloudBin=CloudLocal.getSynContainer().Mapbin.get(name);
        JSONObject jsonObject=cloudBin.getPagePathList(ab,path);
        return jsonObject.toJSONString();
    }
    @Api(def = "cPathList1")//using cloude list drag模式 view
    public String cPathList1(@GetParm Map<String,String> map){
        String name=map.get("name");
        String ab=map.get("ab");
        String path=map.get("path");
        CloudBin cloudBin=CloudLocal.getSynContainer().Mapbin.get(name);
        JSONObject jsonObject=cloudBin.getPagePathList(ab,path);
        return jsonObject.toJSONString();
    }//{"D://tu/1.png":"f","D://tu/123456":"p","D://tu/2.png":"f","D://tu/3.png":"f",...}
    @Api(def = "lPathList")
    public String lPathList(Map<String,String> map){
        String ab=map.get("ab");
        String path=map.get("path");
        CloudBin cloudBin=CloudLocal.getSynContainer().localbin;
        JSONObject jsonObject=cloudBin.getLocalPathList(ab,path);
        return jsonObject.toJSONString();
    }

    @Api(def = "openPathRoot")
    public List<String> openPathRoot(){
        List<String> list=new ArrayList<>();
        for (String s: InitClass.getRootPaths()){
            list.add(s);
        }
        return list;
    }
    @Api(def = "openPath")
    public List<String> openPath(@GetParm String path){
        List<String> list=new ArrayList<>();
        if (path==null){
            for (String s: InitClass.getRootPaths()){
                list.add(s);
            }
        }else {
            File[] files=new File(path).listFiles();
            String s="";
            for (File file: files){
                if (!file.isFile()){
                    s=file.getName();
//                    s=s.replace("\\","/");
                    list.add(s);
                }
            }
        }
        return list;
    }

    @Api(def = "openParentPath")
    public List<String> openParentPath(@GetParm String path){
        List<String> list0=new ArrayList<>();
        List<String> list=new ArrayList<>();

        String parent=null;
        try {
            parent=new File(path).getParent();
        }catch (Exception e) {
        }

        if (parent==null){
            for (String s: InitClass.getRootPaths()){
                if (!new File(s).isFile()){
                    list.add(s);
                }
            }
            list0.add(0,null);
            list0.add(1, JSON.toJSONString(list));
            return list0;
        }
        String[] strings=null;
        try {
            strings=new File(parent).list();
            for (String s: strings){
                if (new File(parent+"/"+s).isDirectory()){
                    s=s.replace("\\","/");
                    list.add(s);
                }
            }
            list0.add(0,parent);
            list0.add(1, JSON.toJSONString(list));
            return list0;
        }catch (Exception e){
            for (String s: InitClass.getRootPaths()){
                if (!new File(s).isFile()){
                    list.add(s);
                }
            }
            list0.add(0,null);
            list0.add(1, JSON.toJSONString(list));
            return list0;
        }
    }


    @Api(def = "showQR")
    public void showQR(ChannelHandlerContext ctx) {
        String url = "http://" + UDPclient.userlocal.inaddress.getHostAddress() + ":" + UDPclient.userlocal.inport;
        byte[] bytes=ShowQr.gren.show(url);
        FullHttpResponse response=null;
        if (bytes == null) {
             response= new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            response.headers().add(HttpHeaderNames.CONTENT_TYPE, "png/jpg");
            response.headers().add(HttpHeaderNames.CONTENT_LENGTH, 0);
        }else {
            ByteBuf byteBuf= Unpooled.copiedBuffer(bytes);
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
            response.headers().add(HttpHeaderNames.CONTENT_TYPE, "png/jpg");
            response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
        }

        ctx.writeAndFlush(response);
    }

    @Api(def = "showIp")
    public String showIp() {
        Map map=new HashMap();
        map.put("ip",UDPclient.userlocal.address.getHostAddress());
        map.put("port",UDPclient.userlocal.port);
        map.put("inip",UDPclient.userlocal.inaddress.getHostAddress());
        map.put("inport",UDPclient.userlocal.inport);
        byte[] bytes= UDPclient.userlocal.toString().getBytes();
        Senders.ServerSends(bytes);
       return JSON.toJSONString(map);
    }

    @Api(def = "gets")
    public String gets(@GetParm String username) throws IOException {
        String requestserver = "C:\\Users\\liushengchang-n\\Desktop\\h.png";//chooseFilepath();
        DataRequest dataRequest=new DataRequest();
        dataRequest.pl=1440;
        dataRequest.filename=requestserver;
        UserContext userContext=UDPclient.mainDataQueue.getUserContext(username);
        short id=userContext.newQueue();
        ByteStream d=new ByteStream(userContext,id);
        File file=new File(new File(requestserver).getName());
        if (!file.exists()){
            file.createNewFile();
        }
        dataRequest.id=id;
        try {
            d.reqFile(dataRequest,new FileOutputStream(file));
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }
        d.close();
        return "cg";
    }


    @Api(def = "gets")
    public String getChats(@GetParm String username) throws IOException {
        if (username.equals("null") || username.equals("undefined")){
            username= UDPclient.userlocal.username;
        }
        ChatBin chatBin= ChatContrain.getChatBin(username);
        List<ChatData> list=chatBin.ringQue.toList();
        List li=new ArrayList();
        ChatHandler.getChatHandler();
        ChatData da=null;
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

}
