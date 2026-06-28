package superlink.udpbind.controller;

import superlink.udpbind.client.recives.ByteBufer;
import superlink.udpbind.client.recives.Senders;
import superlink.udpbind.client.recives.recor.BindFactory;
import superlink.udpbind.client.UDPclient;
import com.alibaba.fastjson2.JSON;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.udplink.ReCallBind;
import superlink.udpbind.client.udplink.ReCallInBind;
import superlink.udpbind.servlet.LiveReturnServer;
import superlink.udpbind.usedata.RSRequest;
import superlink.udpbind.usedata.TranSpondUser;
import superlink.udpbind.usedata.User;
import superlink.udpbind.usedata.UserRequest;
import superlink.util.Tool;
import superlink.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static superlink.util.Utils.getRandomValue;

public class UdpBindSend extends Thread{

    public static void udpBindSend(UserRequest userRequest){
        UserRequest request= Tool.getUserRequestBind(userRequest);
        request.username=UDPclient.userlocal.username;
        if (userRequest.toaddress==null){
            return;
        }
        UserContext userContext=UDPclient.mainDataQueue.createUserContext(userRequest.username,userRequest.toaddress, userRequest.toport);
        System.out.println("ID:"+request.toString());
        request.userid=userContext.getUserId();
        System.out.println("ID:"+request.userid);
        String requestserver ="RS"+request.toString();//请求服务器和和对方主机
        byte[] bytes= requestserver.getBytes();

        Senders.ServerSends(bytes);

        ByteBufer blockingQueue=  userContext.getQueue((short)0);
        BindFactory.createBindrec(blockingQueue,userRequest.username);
        ReCallBind.ReCallBindFactory(userRequest.toaddress,userRequest.toport,bytes,userRequest.username);
        //端口本地防火墙
        Senders.Sends(userRequest.toaddress,userRequest.toport,bytes);
        userContext.sort=-1;
    }
    public static void UdpBindSendInside(UserRequest userRequest){
        UserRequest request= Tool.getUserRequestBind(userRequest);
        request.username=UDPclient.userlocal.username;
        UserContext userContext=UDPclient.mainDataQueue.createUserContext(userRequest.username,userRequest.toaddress, userRequest.toport);
        System.out.println("ID:"+request.toString());
        request.userid=userContext.getUserId();
        System.out.println("ID:"+request.userid);
        String requestserver ="SR"+request.toString();//请求服务器和和对方主机
        byte[] bytes= requestserver.getBytes();

        Senders.Sends(userRequest.toaddress,userRequest.toport,bytes);

        ByteBufer blockingQueue=  userContext.getQueue((short)0);
        BindFactory.createBindrec(blockingQueue,userRequest.username);

        Senders.Sends(userRequest.toaddress,userRequest.toport,bytes);//端口本地防火墙
    }
    public static void UdpBindSendinlocal(User user){
        UserRequest userRequest=new UserRequest();
        userRequest.toaddress=user.inaddress;
        userRequest.toport=user.inport;
        userRequest.requestport= UDPclient.userlocal.inport;
        userRequest.requestaddress=UDPclient.userlocal.inaddress;
        userRequest.username=UDPclient.userlocal.username;
        userRequest.inaddress=UDPclient.userlocal.inaddress;
        userRequest.inport= UDPclient.userlocal.inport;
        userRequest.username=UDPclient.userlocal.username;
        if (user.inaddress==null){
            return;
        }

        UserContext userContext=UDPclient.mainDataQueue.createUserContext(user.username,user.inaddress, user.inport);
        System.out.println("ID:"+JSON.toJSONString(userRequest));
        userRequest.userid=userContext.getUserId();
        System.out.println("ID:"+userRequest.userid);
        String requestserver ="SI"+userRequest.toString();//请求服务器和和对方主机
        byte[] bytes= requestserver.getBytes();
        bytes=Utils.byteMerger(new byte[]{0,0,0,0,0,0},bytes);

        Senders.Sends(user.inaddress,user.inport,bytes);

        ByteBufer blockingQueue=  userContext.getQueue((short)0);
        BindFactory.createBindrec(blockingQueue,user.username);
        ReCallInBind.ReCallBindFactory(user.inaddress,user.inport,bytes,user.username);

        //端口本地防火墙
        Senders.Sends(user.inaddress,user.inport,bytes);
        userContext.sort=-2;
    }

    public static void udpReturnBind(User user){
        RSRequest request= new RSRequest();
        request.username=UDPclient.userlocal.username;
        request.requestaddress=UDPclient.userlocal.address;//daindao
        request.requestport=UDPclient.userlocal.port;//daindao
        request.inaddress=UDPclient.userlocal.inaddress;
        request.inport=UDPclient.userlocal.inport;
        request.toaddress= user.address;
        request.toport=user.port;

        UserContext userContext=UDPclient.mainDataQueue.createUserBuf(user.username,LiveReturnServer.inetAddress,LiveReturnServer.port);
        System.out.println("ID:"+request.toString());

        request.userid=userContext.getUserId();

        System.out.println("ID:"+request.userid);
        String requestserver ="RN"+request.toString();//请求服务器和和对方主机
        byte[] bytes= requestserver.getBytes();

        Senders.ServerSends(bytes);
        requestserver ="RN"+request.toString();
        bytes= requestserver.getBytes();

        ReCallBind.ReCallBindFactory(LiveReturnServer.inetAddress,LiveReturnServer.port,bytes,user.username);
        //端口本地防火墙
        Senders.Sends(LiveReturnServer.inetAddress,LiveReturnServer.port,bytes);
        userContext.sort=-3;
    }

    public static void udpTranSpondUser(User user){
        TranSpondUser request= new TranSpondUser();
        request.sUser=UDPclient.userlocal.username;
        request.tUser=user.username;
        request.tInetAddress=user.address;//daindao
        request.tPort=user.port;//daindao
        request.sInetAddress=UDPclient.userlocal.address;
        request.sPort=UDPclient.userlocal.port;

        UserContext userContext=UDPclient.mainDataQueue.createUserContext(user.username,LiveReturnServer.inetAddress,LiveReturnServer.port);
        request.sId=userContext.getUserId();

        System.out.println("ID:"+request.toString());
        String requestserver ="rr"+request.toString();//请求服务器和和对方主机
        byte[] bytes= requestserver.getBytes();

        User server=getRandomValue(UDPclient.serverUser);
        request.server=server.username;
        Senders.Sends(server.inaddress,server.port,bytes);

        ByteBufer blockingQueue=  userContext.getQueue((short)0);
        BindFactory.createBindrec(blockingQueue,user.username);
        ReCallBind.ReCallBindFactory(server.inaddress,server.port,bytes,user.username);

        userContext.sort=-3;
    }
}
