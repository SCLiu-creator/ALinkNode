package superlink.httpserver.servelt.action.post;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import superlink.filemanage.xmltool.UserGet;
import superlink.filemanage.xmltool.XmlParser;
import superlink.httpserver.servelt.action.Action;
import superlink.httpserver.servelt.action.Api;
import superlink.httpserver.servelt.action.GetParm;
import superlink.httpserver.servelt.WebController;
import superlink.init.InitClass;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.recives.MainDataQueue;
import superlink.udpbind.usedata.User;
import superlink.util.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.net.DatagramSocket;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static superlink.filemanage.xmltool.UserGet.*;
import static superlink.udpbind.client.UDPclient.*;

@WebController(name = "login")
public class ActionLogin implements Action {

    @Api(def = "loginuser")
    public String loginUser(ChannelHandlerContext context, FullHttpRequest request) {
        ByteBuf byteBuf = request.content();
        JSONObject jsonObject = JSON.parseObject(byteBuf.toString(io.netty.util.CharsetUtil.UTF_8));
        Integer prex = new Integer(0);

        String send = (String) jsonObject.get("user");
        String user = null;
        try {
//            Integer input=Integer.valueOf(send);
            for (String s : userlist) {
                if (s.equals(send)) {
                    user = s;
                }
                prex++;
            }
        } catch (Exception e) {
            System.out.println("CREATE USER!");
        }
        String label = (String) jsonObject.get("name");
        ;
        if (user == null) {
            user = new Utils.CreateName().create();
            String filename = XmlParser.dir + "userpage.xml";
            File file=new File(filename);
            try {
                Document document = new SAXReader().read(new File(filename));
                document.getRootElement().addElement("user").addAttribute("name", user).addAttribute("label", label);
                FileOutputStream fileOutputStream = new FileOutputStream(filename);
                XMLWriter writer = new XMLWriter(fileOutputStream);
                writer.write(document);
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String filename = XmlParser.dir + "userpage.xml";
        Document document = null;
        try {
            document = new SAXReader().read(new File(filename));
        } catch (
                DocumentException e) {
            e.printStackTrace();
        }
        List<Element> list = document.getRootElement().elements();
        for (Element element : list) {
            String name = element.attribute("name").getValue();
            if (user.equals(name)) {
                UserGet.user = element;
                UDPclient.userlocal.username = name;
                UDPclient.userlocal.nickName = label;
            }
        }

        synchronized (userlist) {
            System.out.println(UserGet.class);
            userlist.notify();
        }

        synchronized (UserGet.class) {
            int i = 9;
            while (UDPclient.userlocal.address == null) {
                UDPclient.bindServer();
                try {
                    UserGet.class.wait(800);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i--;
                if (i < 0) {
                    return "error";
                }
            }
            return user;
        }
    }

    @Api(def = "loginIn")
    public String loginIn(FullHttpRequest request){
        ByteBuf byteBuf=request.content();
        JSONObject jsonObject= JSON.parseObject(byteBuf.toString(io.netty.util.CharsetUtil.UTF_8));
        Integer prex=new Integer(0);
        String send=(String) jsonObject.get("user");
        String user=null;
        try {
            for (String s: userlist){
                if (s.equals(send)){
                    user=s;
                }
                prex++;
            }
        }catch (Exception e){
            System.out.println("CREATE USER!");
        }
        String label=(String) jsonObject.get("name");;
        if (user==null){
            user=new Utils.CreateName().create();
            String filename= XmlParser.dir+"userpage.xml";
            try {
                Document document=new SAXReader().read(new File(filename));
                document.getRootElement().addElement("user").addAttribute("name",user).addAttribute("label",label);
                FileOutputStream fileOutputStream=new FileOutputStream(filename);
                XMLWriter writer=new XMLWriter(fileOutputStream);
                writer.write(document);
                writer.close();
            } catch (Exception  e) {
                e.printStackTrace();
            }
        }
        String filename=XmlParser.dir+"userpage.xml";
        Document document= null;
        try {
            document = new SAXReader().read(new File(filename));
        } catch (
                DocumentException e) {
            e.printStackTrace();
        }
        List<Element> list=document.getRootElement().elements();
        for (Element element:list){
            String name=element.attribute("name").getValue();
            if (user.equals(name)){
                UserGet.user=element;
                UDPclient.userlocal.username=name;
                UDPclient.userlocal.nickName=label;
            }
        }

        synchronized (userlist){
            System.out.println(UserGet.class);
            userlist.notify();
        }
        UDPclient.userlocal.address=UDPclient.userlocal.inaddress;
        UDPclient.userlocal.port=UDPclient.userlocal.inport;
        return user;
    }

    @Api(def = "setIp")
    public void setIp(@GetParm Boolean b){
        InitClass.ipv=b;
        InetAddress inetAddress;
        if (b){
            inetAddress=Utils.getLocalIpv4();
            InitClass initClass=InitClass.initClass;
            initClass.ipv4=inetAddress;
            initClass.address=initClass.ipv4;
            UDPclient.userlocal.inaddress= initClass.address;
            try {
//                UDPclient.socket = new DatagramSocket(UDPclient.userlocal.port,UDPclient.userlocal.inaddress);
//                UDPclient.socket = new DatagramSocket(UDPclient.userlocal.port);
                UDPclient.serverip = ipv4List.get(0);
//                UDPclient.socket .setReceiveBufferSize(65537*4);
//                UDPclient.serverip = InetAddress.getByName("122.51.51.35");
//                UDPclient.serverip = InetAddress.getByName("127.0.0.1");
                InitClass.initClass.startDataQueue();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            inetAddress=Utils.getLocalIpv6();
            InitClass initClass=InitClass.initClass;
            initClass.ipv6=inetAddress;
            initClass.address=initClass.ipv6;
            UDPclient.userlocal.inaddress= initClass.address;
            try {
//                UDPclient.socket  = new DatagramSocket(UDPclient.userlocal.port,UDPclient.userlocal.inaddress);
//                UDPclient.socket .setReceiveBufferSize(65537*4);
//                UDPclient.serverip = Inet6Address.getLocalHost();
                UDPclient.serverip= ipv6List.get(0);
//                UDPclient.serverip=InetAddress.getByName("2409:8d21:0:6261:8723:7f75:2e12:e63");
                InitClass.initClass.startDataQueue();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    @Api(def = "getuser")
    public void lUer(User context){
        System.out.println(JSON.toJSONString(context));
    }
    @Api(def = "getusers")
    public void lUer(List<User> context){
        Map<String ,User> m=new HashMap<>();
        m.put("aaa",context.get(0));
        m.put("bbb",context.get(0));
        System.out.println(JSON.toJSONString(m));
    }

    @Api(def = "getState")
    public Map getState(){
        HashMap hashMap=new HashMap();
        if(InitClass.initClass.address instanceof Inet6Address ){
            hashMap.put("ip","ipv6");
        }
        if(InitClass.initClass.address instanceof Inet6Address ){
            hashMap.put("ip","ipv4");
        }
        return hashMap;
    }
    @Api(def = "getuserss")
    public void mUer(Map<String,User> context){
        User user=context.get("aaa");
        System.out.println(JSON.toJSONString(context));
    }
    @Api(def = "getuserml")
    public void mlUer(Map<String,List<User>> context){
        User user=context.get("aaa").get(0);
        System.out.println(JSON.toJSONString(context));
    }
    @Api(def = "getuserlm")
    public void lmUer(List<Map<String,User>> context){
        User user=context.get(0).get("aaa");
        System.out.println(JSON.toJSONString(context));
    }

    public static void main(String[] args) throws UnknownHostException {
        User user=JSON.parseObject("{\"choose\":0,\"inaddress\":\"fe80::9858:4ff:fe34:ac1f\",\"inport\":8001,\"nickName\":\"x100u\",\"port\":0,\"request\":false,\"udpstate\":0,\"username\":\"eeNec5SiTeCRDwYY\"}",User.class);
        InetAddress inetAddress=InetAddress.getByName("fe80::9858:4ff:fe34:ac1f%rmnet_data0" );
        System.out.println(inetAddress.toString());
    }
}
