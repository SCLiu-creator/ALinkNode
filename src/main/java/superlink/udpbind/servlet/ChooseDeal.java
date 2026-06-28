package superlink.udpbind.servlet;

import com.alibaba.fastjson2.TypeReference;
import superlink.init.Initor;
import superlink.udpbind.chat.ChatBin;
import superlink.udpbind.chat.ChatData;
import superlink.udpbind.chat.ChatHandler;
import superlink.udpbind.client.recives.MainDataQueue;
import superlink.udpbind.client.recives.Senders;
import superlink.udpbind.client.recives.ServerQueue;
import superlink.udpbind.client.server.TranByteBufer;
import superlink.udpbind.remote.remoteImp.DataRemote;
import superlink.udpbind.client.recives.ByteBufer;
import superlink.udpbind.client.recives.recor.BindFactory;
import superlink.udpbind.client.udplink.ReCallBind;
import superlink.udpbind.controller.Controller;
import superlink.udpbind.controller.Invoke;
import superlink.udpbind.dataqueue.DataQueue;
import superlink.tcpbind.choose.TcpServerBind;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.dataqueue.ReciveQueueFactory;
import superlink.udpbind.dataLink.UdpData;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import superlink.udpbind.handle.Handler;
import superlink.udpbind.usedata.*;
import superlink.udpbind.dataLink.data.DataFactory;
import superlink.udpbind.dataLink.data.DataRecives;
import superlink.udpbind.dataLink.data.DataSends;
import superlink.util.JackJson;
import superlink.util.OneMap;
import superlink.util.Tool;
import superlink.util.Utils;


import java.io.IOException;
import java.net.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static superlink.udpbind.client.UDPclient.*;
import static superlink.udpbind.dataLink.data.DataFactory.dataExecutor;
import static superlink.util.Tool.toLF;


public class ChooseDeal implements Runnable{

    public TcpServerBind tcpThreadBind;
    public String request;
    public byte[] b=new byte[]{0,0,0,0,0,0};
    public InetAddress inetAddress;
    public int port;
    public ChooseDeal(byte[] bytes){
        request = new String(bytes);
    }
    public ChooseDeal(){
    }
    public ChooseDeal setData(byte[] b){
        request=new String(b);
        return this;
    }
    public ChooseDeal setData(ServerQueue.Node node){
        request=new String(node.bytes,0,node.len);
        port=node.port;
        inetAddress=node.inetAddress;
        return this;
    }


    @Override
    public void run(){
        String choose = request.substring(0, 2);
        switch (choose) {
            case "TT": {//tcp连接
                String info = request.substring(2);
                JSONObject jsonObject = JSON.parseObject(info);
                UserRequest acpectObject = JSON.parseObject(jsonObject.toJSONString(), UserRequest.class);
                //        String info = new String(data, 0, packet.getLength());//创建字符串对象
                System.out.println("我是服务器，客户端说：" + info);//输出提示信息

                UserRequest userRequest = new UserRequest();
                userRequest.username = acpectObject.username;
                userRequest.inport = userlocal.inport;
                userRequest.inaddress = userlocal.inaddress;
                userRequest.requestport = userlocal.port;
                userRequest.requestaddress = userlocal.address;
                userRequest.toaddress = acpectObject.requestaddress;
                userRequest.toport = acpectObject.requestport;
                Thread thread = new Thread(new TcpServerBind(acpectObject));
                thread.start();
                break;
            }
            case "RE": {
                String info = request.substring(2);
                JSONObject jsonObject = JSON.parseObject(info);
                User acpectObject = JSON.parseObject(jsonObject.toJSONString(), User.class);//此处port为目标端口
//                acpectObject.udpstate = 0;
                byte[] req = jsonObject.toJSONString().getBytes();
                Senders.Sends(acpectObject.address, acpectObject.port,req);
                break;
            }
            case "DU": {
                String info = request.substring(2);
                JSONObject jsonObject = JSON.parseObject(info);
                User acpectObject = JSON.parseObject(jsonObject.toJSONString(), User.class);//此处port为目标端口
//                acpectObject.udpstate = 0;
                byte[] req = jsonObject.toJSONString().getBytes();
                Senders.Sends(acpectObject.address, acpectObject.port,req);
                break;
            }
            case "QU": {
                String info = request.substring(2);
                if (userlocal.username.equals(info)){
                    Senders.Sends(inetAddress, port,JSON.toJSONBytes(userlocal));
                    break;
                }else {
                    byte[] req = Utils.byteMerger(new byte[6],JSON.toJSONBytes(userlocal));
                    Senders.Sends(inetAddress, port,req);
                    UserContext userContext=UDPclient.getUser(info);
                    req = Utils.byteMerger(Utils.intToByteArray(userContext.getBothId()),Utils.shortToByteArray((short) 0),
                            ("LL"+userlocal.username).getBytes());
                    Senders.Sends(inetAddress, port,req);
                }
                break;
            }
            case "US": {//收到成功的节点返回
                //UDPclient.userlocal.udpstate=1;
                String requestjson = request.substring(2);
                UserRequest requested = JSON.parseObject(requestjson, UserRequest.class);
                UserContext userContext = null;

                try {
                    userContext = mainDataQueue.getUserContext(requested.username);
                    userContext.setBothId(requested.userid);
                } catch (Exception var35) {
                    System.out.println("LinkExcepytirequested.username:    " + requested.username);
                    System.out.println("local.username:    " + userlocal.username);
                    break;
                }

                User saveruser = new User();
                saveruser.address=requested.requestaddress;
                saveruser.port=requested.requestport;
                saveruser.inaddress=requested.inaddress;
                saveruser.inport=requested.inport;
                saveruser.username=requested.username;

                bindUser.put(saveruser.username,saveruser);
                ReCallBind.traversalMap.remove(saveruser.username);

                //JOptionPane.showMessageDialog(null,"成功收到节点请求，请选择新操作");
                if (Initor.usersNodeMap.containsKey(saveruser.username)) {
                    Controller.ReqCloudePage(requested.username);
                }

//                try {
//                    Thread.sleep(20);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                byte[] bytes = ("SU" + userlocal.username).getBytes();
                bytes = Utils.byteMerger(b, bytes);

                Senders.Sends(userContext.inetAddress, userContext.port,bytes);
                userContext.succeedSort();
                System.out.println("UDP bind  Successed sort: "+userContext);
                break;

            }
            case "SU": {//收到成功的节点返回
                //UDPclient.userlocal.udpstate=1;
                String username = request.substring(2);

                ReCallBind.traversalMap.remove(username);
                UserContext userContext = null;
                try {
                    userContext = mainDataQueue.getUserContext(username);
                } catch (Exception e) {
                    break;
                }

                System.out.println("UDP bind Successed");
                //JOptionPane.showMessageDialog(null,"成功收到节点请求，请选择新操作");
//                if (Initor.usersNodeMap.containsKey(username)) {
//                    Controller.ReqCloudePage(null);
//                }
//                try {
//                    Thread.sleep(20);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                byte[] bytes = ("LL" + userlocal.username).getBytes();
                int userid = userContext.getBothId();
                bytes = Utils.byteMerger(Utils.getUseridByte(userid, (short) 0), bytes);
                Senders.Sends(userContext.inetAddress, userContext.port,bytes);
                Senders.Sends(userContext.inetAddress, userContext.port,bytes);
                userContext.succeedSort();
                break;
            }
            case "SI": {//服务器转发的连接请求
                String serverrequset = request.substring(2);
                UserRequest serverRequest = JSON.parseObject(serverrequset, UserRequest.class);
                UserRequest sendRequest = new UserRequest();
                sendRequest.username = userlocal.username;
                sendRequest.toport = serverRequest.requestport;
                sendRequest.toaddress = serverRequest.requestaddress;
                sendRequest.requestport = userlocal.inport;
                sendRequest.requestaddress = userlocal.inaddress;
                sendRequest.inaddress = userlocal.inaddress;
                sendRequest.inport = userlocal.inport;
                UserContext userContext = mainDataQueue.createUserContext(serverRequest.username, serverRequest.requestaddress, serverRequest.requestport);
                userContext.setBothId(serverRequest.userid);//
                System.out.println("setBothId:" + serverRequest.userid);
                ByteBufer blockingQueue = userContext.getQueue((short) 0);

                BindFactory.createBindrec(blockingQueue, serverRequest.username);
                sendRequest.userid = userContext.getUserId();
                System.out.println("Userid:" + sendRequest.userid);
                String sendnode = "US" + sendRequest.toString();
                byte[] bytes = sendnode.getBytes();
                bytes = Utils.byteMerger(b, bytes);

                ReCallBind.ReCallBindFactory(sendRequest.toaddress, sendRequest.toport,bytes,serverRequest.username);
                Senders.Sends(sendRequest.toaddress, sendRequest.toport,bytes);
//                sendRequest.username = serverRequest.username;
//                try {
//                    Thread.sleep(20);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                //open firewall

                User user = new User();
                user.address = serverRequest.requestaddress;
                user.port = serverRequest.requestport;
                user.username = serverRequest.username;

                bindUser.put(user.username,user);
                Senders.Sends(sendRequest.toaddress, sendRequest.toport,"OP".getBytes());

                userContext.sort=2;
//                        JOptionPane.showMessageDialog(null,"succeed");
                break;
            }
            case "SR": {//服务器转发的连接请求
                String serverrequset = request.substring(2);
                UserRequest serverRequest = JSON.parseObject(serverrequset, UserRequest.class);
                UserRequest sendRequest = new UserRequest();
                sendRequest.username = userlocal.username;
                sendRequest.toport = serverRequest.requestport;
                sendRequest.toaddress = serverRequest.requestaddress;
                sendRequest.requestport = serverRequest.toport;
                sendRequest.requestaddress = serverRequest.toaddress;
                sendRequest.inaddress = userlocal.inaddress;
                sendRequest.inport = userlocal.inport;
                UserContext userContext = mainDataQueue.createUserContext(serverRequest.username, serverRequest.requestaddress, serverRequest.requestport);
                userContext.setBothId(serverRequest.userid);//
                userContext.sort=-1;
                System.out.println("setBothId:" + serverRequest.userid);
                ByteBufer blockingQueue = userContext.getQueue((short) 0);

                BindFactory.createBindrec(blockingQueue, serverRequest.username);
                sendRequest.userid = userContext.getUserId();
                System.out.println("Userid:" + sendRequest.userid);
                String sendnode = "US" + sendRequest.toString();
                byte[] bytes = sendnode.getBytes();
                bytes = Utils.byteMerger(b, bytes);

                ReCallBind.ReCallBindFactory(sendRequest.toaddress, sendRequest.toport,bytes,serverRequest.username);
                Senders.Sends(sendRequest.toaddress, sendRequest.toport,bytes);
//                sendRequest.username = serverRequest.username;
//                try {
//                    Thread.sleep(20);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                //open firewall
                User user = new User();
                user.address = serverRequest.requestaddress;
                user.port = serverRequest.requestport;
                user.username = serverRequest.username;

                bindUser.put(user.username,user);

                Senders.Sends(sendRequest.toaddress, sendRequest.toport,Utils.byteMerger(b,"LL".getBytes()));

//                        JOptionPane.showMessageDialog(null,"succeed");
                break;
            }
            case "IR": {//服务器转发的连接请求
                //todo
                String serverrequset = request.substring(2);
                UserRequest serverRequest = JSON.parseObject(serverrequset, UserRequest.class);
                UserRequest userRequest = new UserRequest();
                userRequest.username = userlocal.username;
                userRequest.toport = serverRequest.requestport;
                userRequest.toaddress = serverRequest.requestaddress;
                userRequest.requestport = serverRequest.toport;
                userRequest.requestaddress = serverRequest.toaddress;
                userRequest.inaddress = userlocal.inaddress;
                userRequest.inport = userlocal.inport;
                UserContext userContext = mainDataQueue.createUserContext(serverRequest.username, userRequest.toaddress, userRequest.toport);
                userContext.setBothId(serverRequest.userid);//
                System.out.println("setBothId:" + serverRequest.userid);
                ByteBufer blockingQueue = userContext.getQueue((short) 0);
                //UDPclient.executorService.execute(new Bindrec(blockingQueue,serverRequest.username));
                BindFactory.createBindrec(blockingQueue, serverRequest.username);
                userRequest.userid = userContext.getUserId();
                System.out.println("Userid:" + userRequest.userid);
                String sendnode = "US" + userRequest.toString();
                byte[] bytes = sendnode.getBytes();
                bytes = Utils.byteMerger(b, bytes);
                userContext.sort=3;

                ReCallBind.ReCallBindFactory(userRequest.toaddress, userRequest.toport,bytes,serverRequest.username);
                Senders.Sends(userRequest.toaddress, userRequest.toport,bytes);

                userRequest.username = serverRequest.username;
//                try {
//                    Thread.sleep(20);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                //open firewall
                User user = new User();
                user.address = userRequest.toaddress;
                user.port = userRequest.toport;
                user.username = userRequest.username;
                bindUser.put(user.username,user);
                Senders.Sends(userRequest.toaddress, userRequest.toport,Utils.byteMerger(b,"LL".getBytes()));
//                        JOptionPane.showMessageDialog(null,"succeed");
                break;
            }
            case "RN": {
                //来自服务器转发，接受方
                String serverrequset = request.substring(2);
                RSRequest serverRequest = JSON.parseObject(serverrequset, RSRequest.class);

                RSRequest sendRequest = new RSRequest();
                sendRequest.username = userlocal.username;
                sendRequest.toport = serverRequest.requestport;
                sendRequest.toaddress = serverRequest.requestaddress;//请求方外部地址
                sendRequest.requestport = userlocal.port;
                sendRequest.requestaddress = userlocal.address;
                sendRequest.inaddress = userlocal.inaddress;
                sendRequest.inport = userlocal.port;

                ReCallBind reCallBind=ReCallBind.traversalMap.get(serverRequest.username);
                if (reCallBind==null){
                    new LiveReturnServer(serverRequest.username,LiveReturnServer.inetAddress,LiveReturnServer.port);
                }
                UserContext userContext = mainDataQueue.
                        createUserContext(serverRequest.username, LiveReturnServer.inetAddress,LiveReturnServer.port);
                userContext.setBothId(serverRequest.userid);//

                System.out.println("setBothId:" + serverRequest.userid);
                ByteBufer blockingQueue = userContext.getQueue((short)0);

                BindFactory.createBindrec(blockingQueue, serverRequest.username);

                sendRequest.userid = userContext.getUserId();
                sendRequest.bothid = userContext.getBothId();
                System.out.println("Userid:" + sendRequest.userid);
                userContext.sort=-3;
                String sendnode = "NR" + sendRequest.toString();
                byte[] bytes = sendnode.getBytes();
//                bytes = Utils.byteMerger(b, bytes);
//                ReCallBind.ReCallBindFactory(datagramPacket,serverRequest.username);
                Senders.ServerSends(bytes);

                User user =userMap.get(serverRequest.username);
                if (user==null){
                    user=new User();
                    user.address = serverRequest.inaddress;
                    user.port = serverRequest.inport;
                    user.username = serverRequest.username;
                }
                bindUser.put(user.username,user);
                Senders.Sends( LiveReturnServer.inetAddress,LiveReturnServer.port,
                        Utils.byteMerger(Utils.intToByteArray(userContext.getUserId()),new byte[]{127,127}));
                Senders.Sends( LiveReturnServer.inetAddress,LiveReturnServer.port,
                        Utils.byteMerger(Utils.intToByteArray(userContext.getUserId()),new byte[]{127,127}));

//                String buildRNsend=null;
//                try {
//                    buildRNsend=serverRequest.inaddress.getHostAddress();
//                    byte[] chackb=Utils.byteMerger(Utils.getUseridByte(userContext.getUserId(), (short) 0),buildRNsend.getBytes());
//                    chackb[4]=127;
//                    DatagramPacket cheak = new DatagramPacket(chackb, chackb.length, LiveReturnServer.inetAddress,LiveReturnServer.port);
//                    UDPclient.socket.send(cheak);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                break;
            }
            case "NR": {
                //发起方收到
                //todo
                String requestjson = request.substring(2);
                RSRequest requested = JSON.parseObject(requestjson, RSRequest.class);
                UserContext userContext = null;

                try {
                    userContext = MainDataQueue.createBuf.get(requested.username);
                    userContext.setBothId(requested.userid);
                    User user =userMap.get(requested.username);
                    if (user==null){
                        user=new User();
                    }
                    user.address = requested.requestaddress;
                    user.port = requested.requestport;
                    user.username = requested.username;
//                    user.address = LiveReturnServer.inetAddress;
//                    user.port = LiveReturnServer.port;
//                    user.username = requested.username;
//                    bindUser.put(user.username,user);
                } catch (Exception var35) {
                    MainDataQueue.createBuf.remove(requested.username);
                    MainDataQueue.usermap.remove(requested.username);
                    System.out.println("requested.username:    " + requested.username);
                    System.out.println("local.username:    " + userlocal.username);
                    ReCallBind.traversalMap.remove(requested.username);
                    break;
                }

                userContext.sort=3;
                mainDataQueue.addUserContext(userContext);
                ByteBufer blockingQueue=  userContext.getQueue((short)0);
                BindFactory.createBindrec(blockingQueue,userContext.userName);

                ReCallBind.traversalMap.remove(requested.username);
//                System.out.println("UDP bind Successed");
                //JOptionPane.showMessageDialog(null,"成功收到节点请求，请选择新操作");
                if (Initor.usersNodeMap.containsKey(requested.username)) {
                    Controller.ReqCloudePage(requested.username);
                }
//                try {
//                    Thread.sleep(20);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                User user1=new User();
                user1.username= userlocal.username;
                user1.address=requested.requestaddress;
                user1.port=requested.requestport;//被请求方外部端口
                user1.inaddress=userlocal.address;
                user1.inport=userlocal.port;
                byte[] bytes = ("SU" + user1.toString()).getBytes();

                Senders.Sends( LiveReturnServer.inetAddress,LiveReturnServer.port,
                        Utils.byteMerger(Utils.intToByteArray(userContext.getUserId()),new byte[]{127,127}));
                Senders.Sends( LiveReturnServer.inetAddress,LiveReturnServer.port,
                        Utils.byteMerger(Utils.intToByteArray(userContext.getUserId()),new byte[]{127,127}));

                Senders.ServerSends(bytes);//转发给被接受方
                Senders.ServerSends(bytes);//转发给被接受方
                System.out.println("UDP bind NR");
                break;
            }
//            case "UR": {//收到成功的节点返回
//                //UDPclient.userlocal.udpstate=1;
//                String username = request.substring(2);
//
//                ReCallBind.traversalMap.remove(username);
//                UserContext userContext = null;
//                try {
//                    userContext = mainDataQueue.getUserContext(username);
//                } catch (Exception e) {
//                    break;
//                }
//
//                System.out.println("UDP bind Successed");
//                byte[] bytes = ("LL" + userlocal.username).getBytes();
//                int userid = userContext.getBothId();
//                bytes = Utils.byteMerger(Utils.getUseridByte(userid, (short) 0), bytes);
//                Senders.Sends(userContext.inetAddress, userContext.port,bytes);
//                Senders.Sends(userContext.inetAddress, userContext.port,bytes);
//                if (userContext.sort<0){
//                    userContext.sort= -userContext.sort;
//                }
//                break;
//            }
            case "rn": {
                String serverrequset = request.substring(2);
                TranSpondUser request = JSON.parseObject(serverrequset, TranSpondUser.class);

                ReCallBind reCallBind=ReCallBind.traversalMap.get(request.sUser);
                if (reCallBind==null){
                    new LiveReturnServer(request.sUser,request.serverAddress,request.serverPort);
                }
                UserContext userContext = mainDataQueue.
                        createUserContext(request.sUser, request.serverAddress,request.serverPort);
                userContext.setBothId(request.tranId);//

                System.out.println("setBothId:" + request.sId);
                ByteBufer blockingQueue = userContext.getQueue((short)0);

                BindFactory.createBindrec(blockingQueue, request.sUser);
                request.tId = userContext.getUserId();
                System.out.println("Userid:" + request.tId);
                userContext.sort=3;
                String sendnode = "nr" + request.toString();
                byte[] bytes = sendnode.getBytes();
//                bytes = Utils.byteMerger(b, bytes);
//                ReCallBind.ReCallBindFactory(datagramPacket,request.username);
                Senders.ServerSends(bytes);

                User user =userMap.get(request.sUser);
                if (user==null){
                    user=new User();
                    user.address = request.sInetAddress;
                    user.port = request.sPort;
                    user.username = request.sUser;
                }
                bindUser.put(user.username,user);
                Senders.Sends(request.serverAddress,request.serverPort,
                        Utils.byteMerger(Utils.intToByteArray(userContext.getUserId()),new byte[]{0,0},"LL".getBytes()));
                break;
            }
            case "nr": {
                //todo
                String requestjson = request.substring(2);
                TranSpondUser request = JSON.parseObject(requestjson, TranSpondUser.class);
                UserContext userContext = null;
                User user = null;
                try {
                    userContext = mainDataQueue.getUserContext(request.tUser);
                    userContext.setBothId(request.tranId);
                    user =userMap.get(request.tUser);
                    if (user==null){
                        user=new User();
                    }
                    user.address = request.tInetAddress;
                    user.port = request.tPort;
                    user.username = request.tUser;
                    bindUser.put(user.username,user);
                } catch (Exception var35) {
                    System.out.println("requested.username:    " + request.tUser);
                    System.out.println("local.username:    " + userlocal.username);
                    ReCallBind.traversalMap.remove(request.tUser);
                }

                userContext.sort=3;

                ReCallBind reCallBind=ReCallBind.traversalMap.remove(request.tUser);
//                System.out.println("UDP bind Successed");
                //JOptionPane.showMessageDialog(null,"成功收到节点请求，请选择新操作");
                if (Initor.usersNodeMap.containsKey(request.tUser)) {
                    Controller.ReqCloudePage(user.username);
                }
//                try {
//                    Thread.sleep(20);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                User user1=new User();
                user1.username= userlocal.username;
                user1.address=request.tInetAddress;
                user1.port=request.tPort;;//被请求方外部端口
                user1.inaddress=userlocal.address;
                user1.inport=userlocal.port;
                byte[] bytes = ("SU" + user1.toString()).getBytes();
                Senders.ServerSends(bytes);//转发给被接受方
                Senders.Sends( request.serverAddress,request.serverPort,
                        Utils.byteMerger(Utils.intToByteArray(userContext.getUserId()),new byte[]{0,0}));
                Senders.Sends( request.serverAddress,request.serverPort,
                        Utils.byteMerger(Utils.intToByteArray(userContext.getUserId()),new byte[]{0,0}));

                break;
            }case "rr":{
                String rn=request.substring(2);
                TranSpondUser tranUser=JSON.parseObject(rn,TranSpondUser.class);
//                User staruser=UDPServlet.ipuser.get(Objects.hash(tranUser.inaddress,tranUser.inport));//外部端口
                //连接发起方名称
//                User touser=UDPServlet.ipuser.get(tranUser.toaddress.toString()+tranUser.toport);
//                Map<Integer,User> map=UdpReturnServer.integerUserMap(address);
                //生成到达方的返回路径映射
                //tranUser.requestaddress 为对方ip
                UserContext userContext= mainDataQueue.createtranUserContext(tranUser.sUser,tranUser.sInetAddress,tranUser.sPort);//to
                userContext.map=new OneMap();
                tranUser.tranId=userContext.getUserId();
                //返回路径映射中添加自己（发送方），uid为发起方
                //touser.address 为本机ip
                byte[] bs=Utils.byteMerger("rn".getBytes(),rn.getBytes());

                System.out.println("rr   "+tranUser.sUser+"   "+tranUser.sInetAddress.toString()+" "+tranUser.sPort);
                Senders.Sends(tranUser.tInetAddress,tranUser.tPort,bs);
//                logger.info("RN :  连接发起方名称 "+tranUser.username,
//                        "生成到达方的返回路径映射 "+ tranUser.requestaddress);
                break;

            } case "nn": {
                String rn = request.substring(2);
                TranSpondUser tranUser = JSON.parseObject(rn, TranSpondUser.class);
                //               User staruser = UDPServlet.ipuser.get(Objects.hash(userRequest.inaddress,userRequest.inport));
                // 回传连接发起方名称
//                Map<Integer, User> map = UdpReturnServer.integerUserMap(address);//to  发起方外部ip
                //根据请求方外部地址生成请求方到自己的映射
                UserContext toContext= mainDataQueue.createtranUserContext(tranUser.tUser,tranUser.tInetAddress,tranUser.tPort);//to
                toContext.map=new OneMap();
                toContext.map.put((short) 0,new TranByteBufer(toContext,tranUser.sId));
                toContext.setBothId(tranUser.tId);
                tranUser.tranId=toContext.getUserId();

                UserContext sContext= mainDataQueue.createtranUserContext(tranUser.sUser,tranUser.sInetAddress,tranUser.sPort);//to
                sContext.map.put(null,new TranByteBufer(sContext,tranUser.tId));
                tranUser.tranId=sContext.getUserId();
                //返回路径映射中添加自己（返回方），uid为返回方
                byte[] bs = Utils.byteMerger(b, rn.getBytes());//to  发起方外部ip

                System.out.println("rr   "+tranUser.sUser+"   "+tranUser.sInetAddress.toString()+" "+tranUser.sPort);
                Senders.Sends(tranUser.sInetAddress,tranUser.sPort,bs);
//                logger.info("NR :  回传连接发起方名称 "+userRequest.username,
//                        "根据请求方外部地址生成请求方到自己的映射 "+userRequest.toaddress);
                break;
            }
            case "ND": {//收到数据端口连接请求
                String databind = request.substring(2);
                System.out.println("ND:" + request);
                //System.out.println("NDport:".getPort());
                UserRequest dataRequest = JSON.parseObject(databind, UserRequest.class);
                UdpData udpData = new UdpData(dataRequest);
                System.out.println("UDPName:" + dataRequest.username);
                new Thread(udpData).start();
                break;
            }
            case "DN": {//数据端口返回请求,获取被请求端端口
                String databind = request.substring(2);
                UserRequest userRequest = JSON.parseObject(databind, UserRequest.class);
                UdpData udpData = (UdpData) Handler.UdpMap.get(userRequest.username);
                udpData.userRequest.toaddress = userRequest.requestaddress;
                udpData.userRequest.toport = userRequest.requestport;
                DatagramPacket datagramPacket = new DatagramPacket(JSON.toJSONBytes(udpData.userRequest), JSON.toJSONBytes(udpData.userRequest).length, udpData.userRequest.toaddress, udpData.userRequest.toport);
                try {
                    udpData.dataSocket.send(datagramPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            case "SE": {
                String s = "ex" + userlocal.toString();;
                Senders.ServerSends(s.getBytes());
                System.exit(0);
            }
            case "CH": {
                String dataText = request.substring(2);
                Map<String, ChatData> uc=JSON.parseObject(dataText,new  TypeReference<Map<String,ChatData>>(){});
                Map.Entry<String,ChatData> entry=uc.entrySet().iterator().next();
                ChatData data = entry.getValue();
                String username=entry.getKey();
                ChatHandler.adddata(username, data);
                break;
            }
            case "LS": {
                String DR = request.substring(2);
                DataRequest dataRequest = JSON.parseObject(DR.substring(2), DataRequest.class);
                DataSends dataSends = DataFactory.getSend(dataRequest);
                dataExecutor.execute(dataSends);
            }
            case "QS": {
                String QS = request;
                DataRequest dataRequest = JSON.parseObject(QS.substring(2), DataRequest.class);
                UdpData udpData = (UdpData) Handler.UdpMap.get(dataRequest.requestname);
                DataQueue dataQueue = ReciveQueueFactory.getDataQueue(dataRequest.requestname);
                int id = dataQueue.newId();
                Byte byetId = new Byte((byte) id);
                dataQueue.addtask(byetId, dataRequest.dir);
                DataRecives dataRecives = DataFactory.getRecive(dataRequest);
                dataRecives.pool.execute(dataRecives);
                break;
            }
            case "RC": {
                String QS = request;
                UserRequest userRequest = JSON.parseObject(QS.substring(2), UserRequest.class);
                Controller.starCloud();
                break;
            }
            //转发数据
            case "RU": {//todo
                User user = JSON.parseObject(request.substring(2), User.class);
                UserRequest userRequest = Tool.UsertoUserRequest(user);
                Controller.requestNode(userRequest);
                Invoke.node node = Initor.usersNodeMap.get(user.username);
                node.setB(true).i++;
                Initor.thread.isInterrupted();
                break;
            }
            //转发数据
            case "RB": {//todo
                ReUse reUse=JSON.parseObject(request.substring(2), ReUse.class);
                UserContext userContext= mainDataQueue.createUserContext(reUse.username,reUse.sInetAddress,reUse.sport);
                break;
            }
            case "SC": {//todo
                ReUse reUse=JSON.parseObject(request.substring(2), ReUse.class);
                UserContext userContext= mainDataQueue.createUserContext(reUse.username,reUse.sInetAddress,reUse.sport);
                break;
            }
            //转发查找
            case "RF": {
                DataRemote dataRemote = JSON.parseObject(request.substring(2), DataRemote.class);
                String[] remote = dataRemote.inetAddress;
                DatagramPacket datagramPacket;
                int len=0;
                for (String s:remote){
                    if (s!=null){
                        len++;
                    }
                }
                if (userlocal.username.equals(dataRemote.data)){
                    String[] op = dataRemote.inetAddress[len-1].split(":");
                    dataRemote.inetAddress[len-1]=null;
                    byte[] bytes = JSON.toJSONBytes(dataRemote);
                    bytes=Utils.byteMerger(Utils.intToByteArray(0),Utils.shortToByteArray((short) 0),"FR".getBytes(),bytes);
                    try {
                        Senders.Sends(InetAddress.getByName(op[0]), Integer.parseInt(op[1]),bytes);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }else {
                    String[] strings = new String[remote.length + 1];
                    strings[len] = userlocal.address.toString() + ":" + userlocal.port;
                    dataRemote.inetAddress = strings;
                    byte[] bytes = JSON.toJSONBytes(dataRemote);
                    bytes=Utils.byteMerger(Utils.intToByteArray(0),Utils.shortToByteArray((short) 0),"RF".getBytes(),bytes);
                    for (User u : bindUser.values()) {
                        if (userlocal.username.equals(u.username)){
                            continue;
                        }
                        Senders.Sends( u.address, u.port,bytes);
                    }
                }
                break;
            }
            //转发查找 回传
            case "FR": {
                DataRemote dataRemote = JSON.parseObject(request.substring(2), DataRemote.class);
                String[] remote = dataRemote.inetAddress;
                DatagramPacket datagramPacket;
                int i = 0;
                for (; i < remote.length-1; i++) {
                    if (remote[i]==null){
                        break;
                    }
                }
                if (i==0){
                    //todo
                    break;
                }
                String[] op = dataRemote.inetAddress[i].split(":");
                dataRemote.inetAddress[i] = null;
                byte[] bytes = JSON.toJSONBytes(dataRemote);
                bytes=Utils.byteMerger(Utils.intToByteArray(0),Utils.shortToByteArray((short) 0),"FR".getBytes(),bytes);
                try {
//                    Senders.Sends(InetAddress.getByName(op[0]), Integer.parseInt(op[1]),bytes);
                    Senders.Sends(inetAddress, port,bytes);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            }case "TF":{
                String data=request.substring(2);
                User user=JSON.parseObject(data,User.class);
                if (user.username!=null) {
                    User user0=userMap.get(user.username);
                    if (user0!=null){
                        Tool.setUser(user0,user);
                        UserContext userContext=mainDataQueue.getUserContext(user0.username);
                        if (userContext!=null){
                            userContext.setUserContext(user0);
                        }
                    }else {
                        userMap.put(user.username,user);
                    }
                }

                byte[] bytes = JSON.toJSONBytes(userlocal);
                Senders.SendMain(inetAddress, port,bytes);
                break;
            }
            default: {

            }
        }
    }

    public static void main(String[] args) {
        String[] strings=new String[]{"1","2","2","5",null,null};
        String s=JSON.toJSONString(strings);
        List<String> strings4=  JSON.parseArray(s, String.class);
        Object strings1=  JSON.parse(s);
        String[] strings2=  JSON.parseObject(s,String[].class);
        List<String> strings3=  JSON.parseObject(s, List.class);

        System.out.println();

    }

}
