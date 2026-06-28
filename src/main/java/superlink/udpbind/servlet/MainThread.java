package superlink.udpbind.servlet;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import superlink.filemanage.xmltool.UserGet;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.MainDataQueue;
import superlink.udpbind.client.recives.Senders;
import superlink.udpbind.client.recives.ServerQueue;
import superlink.udpbind.controller.UdpBindSend;
import superlink.udpbind.farme.WindowDemo2;
import superlink.udpbind.handle.Handler;
import superlink.udpbind.handle.LiveHandle;
import superlink.udpbind.usedata.User;
import superlink.udpbind.usedata.UserRequest;
import superlink.util.JackJson;
import superlink.util.Tool;
import superlink.util.Utils;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;
import java.io.StringReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static superlink.filemanage.xmltool.UserGet.UserSynServer;
import static superlink.udpbind.client.UDPclient.*;

public class MainThread {

    public String reply;
    public static boolean live=false;
    public InetAddress ip;
    public int port;
    // 启用所有宽松解析特性（Jackson 2.10+）
    ObjectMapper mapper = new ObjectMapper().
            enable(JsonParser.Feature.ALLOW_TRAILING_COMMA).
            enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)
            .enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);

    public void Mainhandle(ServerQueue.Node node){
        String reply = new String(node.bytes,0,node.len).replace("\u0000", "");
        ip = node.inetAddress;
        port = node.port;
        this.reply=reply;
        if (reply.startsWith("{")){
            singleBranch();
        }else if (reply.startsWith("[")){
            masterBranch();
        }else {
          //  System.out.println("  ");
        }
    }

    public void masterBranch(){
        String[] strings;
        if (reply.endsWith("]")){
            reply=reply.substring(1,reply.length()-1);
            reply=reply+",";
            strings=reply.split("\\},");
        }else {
            reply=reply.substring(1);
            reply=reply+",";
            strings=reply.split("\\},");
        }
        Arrays.stream(strings).forEach(s -> {
            try {
                if (s.endsWith(",")){
                    s=s.substring(0,reply.length()-1);
                }
                s=s+"}";
//                JsonNode jsonNode = mapper.readTree(s);
//                User acpectObject= mapper.readValue(jsonNode.toString(), User.class);
                User acpectObject = JSON.parseObject(s, User.class);
                if (acpectObject.username!=null) {
//                if (!acpectObject.username.equals(userlocal.username)){
                    User user = userMap.get(acpectObject.username);
                    if (user != null) {
                        Tool.setUser(user, acpectObject);
                        UserContext userContext = mainDataQueue.getUserContext(user.username);
                        if (userContext != null) {
                            userContext.setUserContext(user);
                        }
                    } else {
                        userMap.put(acpectObject.username, acpectObject);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
//                }else {
//                    if (acpectObject.inport==userlocal.inport /**&& acpectObject.inaddress==userlocal.inaddress**/){
//                        userlocal.port=acpectObject.port;
//                        userlocal.address=acpectObject.address;
//                    }             }

//            if (userlocal.username.equals(acpectObject.username) && acpectObject.inport==userlocal.inport
//            /**&& acpectObject.inaddress==userlocal.inaddress**/){
//            }

//            if (acpectObject.request=true){
//                if (acpectObject.choose==1){
//                    TcpServerBind threadBind = new TcpServerBind(userlocal);
//                    threadBind.run();
//                }else if (acpectObject.choose==2){
//                }
//            }
        });
        //String json = "[\"apple\", \"banana\", \"orange\"]"; // 定义一个json字符串

//        new WindowDemo2("baseWindows").yest(JSON.toJSONString(userList),l);
        if (WindowDemo2.b){
            List<User> userList= userMap.values().stream().collect(Collectors.toList());
            JsonReader reader = Json.createReader(new StringReader(JSON.toJSONString(userList))); // 创建一个JsonReader对象
            JsonArray array = reader.readArray(); // 将json字符串转换为JsonArray对象
            Object[] l=array.toArray();
            WindowDemo2 wind=(WindowDemo2)Handler.DispectMap.get("windows");
            wind.reset(JSON.toJSONString(userList),l);
        }
    }

    public void singleBranch(){
        JSONObject jsonObject = JSON.parseObject(reply);
        System.out.println(jsonObject);
        User acpectObject=JSON.parseObject(jsonObject.toJSONString(),User.class);
        boolean change=false;
        userlocal.time++;
        if (userlocal.username.equals(acpectObject.username)){
            // 1. IP 地址：建议比较 getHostAddress() 字符串，避免 InetAddress 子类类型不一致的问题
            boolean ipChanged = !Objects.equals(
                    userlocal.address != null ? userlocal.address.getHostAddress() : null,
                    acpectObject.address != null ? acpectObject.address.getHostAddress() : null
            );

            boolean portChanged = !Objects.equals(userlocal.port, acpectObject.port);
            boolean inPortChanged = !Objects.equals(userlocal.inport, acpectObject.inport);

            if (ipChanged || portChanged || inPortChanged) {
                change = true;
            }
            userlocal.address=acpectObject.address;
            userlocal.port=acpectObject.port;
            LiveHandle.resendOver=true;
            userlocal.inaddress=acpectObject.inaddress;
            userlocal.inport=acpectObject.inport;
            if(!UserSynServer){
                synchronized (UserGet.class){
                    UserGet.class.notifyAll();
                }
            }
            UserSynServer = true;

            if (change){
                for (UserContext userContext: MainDataQueue.quemap.values()){
                    User user = bindUser.get(userContext.userName);
                    if (user == null||user.inaddress==null||user.address==null) {
                        DatagramPacket packet = new DatagramPacket( new byte[0],0,UDPclient.getServerip(),UDPclient.getSport());
                        packet.setData(("QU"+userContext.userName).getBytes());
                        Senders.Sends0(packet);
                        continue;
                    }
                    switch (userContext.sort){
                    case 1:
                        UserRequest userRequest= Tool.UsertoUserRequestbind(user);
                        UdpBindSend.udpBindSend(userRequest);
                        break;
                    case 2:
                        UdpBindSend.UdpBindSendinlocal(user);
                        break;
                    case 3:
                        UdpBindSend.udpReturnBind(user);
                        break;
                    }
                }
            }

        }else {
            if (acpectObject.username!=null) {
//                if (!acpectObject.username.equals(userlocal.username)){
                User user=userMap.get(acpectObject.username);
                if (user!=null){
                    Tool.setUser(user,acpectObject);
//                    UserContext userContext=mainDataQueue.getUserContext(user.username);
//                    if (userContext!=null){
//                        userContext.setUserContext(user);
//                    }
                }else {
                    userMap.put(acpectObject.username,acpectObject);
                }
                if (acpectObject.udpstate>0){
                    serverUser.put(acpectObject.username, acpectObject);
                }
            }
        }
        if (acpectObject.choose==1){
            Senders.Sends(ip,port,Utils.byteMerger(new byte[]{0,0,0,0,0,0}, userlocal.toString().getBytes()));
        }

        if (WindowDemo2.b){
            WindowDemo2 wind=(WindowDemo2)Handler.DispectMap.get("windows");
            if (WindowDemo2.b){
                JsonReader reader = Json.createReader(new StringReader(JSON.toJSONString(userMap.values().toArray()))); // 创建一个JsonReader对象
                JsonArray array = reader.readArray(); // 将json字符串转换为JsonArray对象
                Object[] l=array.toArray();

                WindowDemo2.frame.setTitle(userlocal.username);
                wind.yest(JSON.toJSONString(userMap.values().toArray()),l);
            }
        }
    }

    public void cmdBranch(){

    }

}
