package superlink.httpserver.servelt.action.get;

import com.alibaba.fastjson2.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.*;
import superlink.filemanage.xmltool.UserGet;
import superlink.filemanage.xmltool.XmlParser;
import superlink.httpserver.servelt.WebController;
import superlink.httpserver.servelt.action.Action;
import superlink.httpserver.servelt.action.Api;
import superlink.httpserver.servelt.action.ChannelAwait;
import superlink.httpserver.servelt.action.GetParm;
import superlink.httpserver.servelt.httptype.ContentType;
import superlink.init.UserLinkCon;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.Senders;
import superlink.udpbind.client.recives.datalen.DataLength;
import superlink.udpbind.client.recives.datalen.DataReqAuto;
import superlink.udpbind.cloude.CloudBin;
import superlink.udpbind.cloude.CloudLocal;
import superlink.udpbind.cloude.util.TendFactory;
import superlink.udpbind.controller.Controller;
import superlink.udpbind.servlet.ClearUser;
import superlink.udpbind.usedata.User;
import superlink.udpbind.usedata.UserRequest;
import superlink.udpbind.user.UserInNetFind;
import superlink.util.JackJson;
import superlink.util.SHAutils;
import superlink.util.Tool;
import superlink.util.Utils;
import superlink.util.datastack.DataListCon;
import superlink.util.datastack.DataListRW;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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

@WebController(name = "over")
public class ActionOver implements Action {

    @Api(def = "Logout")
    public void logout(){
        UDPclient.over();
    }


//    @Api(def = "scanIp")
//    public void scanIp(@GetParm String ip){
//        try {
//            if (ip ==null || Objects.equals(ip,"")){
//                InetAddress inetAddress=UDPclient.userlocal.inaddress;
//                String subnetMask=Utils.getSubnetMask(inetAddress);
//                InetAddress address=null;
//                if (subnetMask!=null){
//                    address=Utils.getBroadcastAddress(inetAddress,subnetMask);
//                }else {
//                    new UserInNetFind().scanIp();
//                    return;
//                }
//                byte[] bytes=new byte[6];
//                User user=UDPclient.userlocal.copy();
//                user.choose = 1;
//                String data= "TF"+user.toString();
//                bytes= Utils.byteMerger(bytes,data.getBytes());
//                DatagramPacket packet=new DatagramPacket(bytes,bytes.length);
//                packet.setAddress(address);
//                UserInNetFind.BroadcastSend(packet);
//            }else {
//                InetAddress inetAddress=InetAddress.getByName(ip);
//                byte[] bytes=new byte[6];
//                User user=UDPclient.userlocal.copy();
//                user.choose = 1;
//                String data= "TF"+user.toString();
//                bytes= Utils.byteMerger(bytes,data.getBytes());
//                DatagramPacket packet=new DatagramPacket(bytes,bytes.length);
//                packet.setAddress(inetAddress);
//                UserInNetFind.BroadcastSend(packet);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }


}
