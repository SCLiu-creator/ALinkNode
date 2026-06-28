package superlink.udpbind.dataLink.rec;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import superlink.tcpbind.choose.TcpServerBind;
import superlink.udpbind.dataLink.LiveBinds;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.cloude.DataCloud;
import superlink.udpbind.cloude.FileTrigger;
import superlink.udpbind.cloude.operta.broadcast.Operta;
//import superlink.udpbind.dataLink.data.DataFactory;
//import superlink.udpbind.dataLink.data.DataSends;
//import superlink.udpbind.dataLink.data.SlowData;
import superlink.udpbind.dataLink.*;
import superlink.udpbind.dataLink.data.*;
import superlink.udpbind.dataqueue.DataQueue;
import superlink.udpbind.dataqueue.ReciveQueueFactory;
import superlink.udpbind.usedata.User;
import superlink.util.Tool;
import superlink.util.Utils;
import superlink.udpbind.handle.Handler;
import superlink.udpbind.usedata.DataRequest;
import superlink.udpbind.usedata.UserRequest;

import javax.swing.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static superlink.udpbind.client.UDPclient.bindUser;
import static superlink.util.Tool.toLF;


public class linksDeals {


    public TcpServerBind tcpThreadBind;
    public String request;
    public InetAddress inetAddress;
    public Integer port;
    public String username;
    public byte[] bytes;
    public linksDeals(byte[] bytes, String name){
        this.bytes=bytes;
        request = new String(bytes);
        username=name;
        //String[] strings=DataQueue.ipname.get(name).substring(1).split(":");
        UdpData udpData= Handler.UdpMap.get(name);
        inetAddress= udpData.userRequest.toaddress;
        port= udpData.userRequest.toport;
    }
    public linksDeals(String name){
        username=name;
        //String[] strings=DataQueue.ipname.get(name).substring(1).split(":");
        UdpData udpData= Handler.UdpMap.get(name);
        inetAddress= udpData.userRequest.toaddress;
        port= udpData.userRequest.toport;
    }

    public void deal(byte[] bytes){
        if (bytes==null){
            return;
        }
        this.bytes=bytes;
        request = new String(bytes);
        String choose = request.substring(0, 2);

        switch (choose) {
            case "TT": {//tcp连接
                String info = request.substring(2);
                JSONObject jsonObject = JSON.parseObject(info);
                UserRequest acpectObject = JSON.parseObject(jsonObject.toJSONString(), UserRequest.class);

                System.out.println("我是服务器，客户端说：" + info);//输出提示信息

                UserRequest userRequest=new UserRequest();
                userRequest.username= acpectObject.username;
                userRequest.inport=UDPclient.userlocal.inport;
                userRequest.inaddress=UDPclient.userlocal.inaddress;
                userRequest.requestport=UDPclient.userlocal.port;
                userRequest.requestaddress=UDPclient.userlocal.address;
                userRequest.toaddress=acpectObject.requestaddress;
                userRequest.toport=acpectObject.requestport;
                Thread thread =new Thread(new TcpServerBind(acpectObject));
                thread.start();

                break;

            } case "RE":{
                String info = request.substring(2);
                JSONObject jsonObject = JSON.parseObject(info);
                User acpectObject = JSON.parseObject(jsonObject.toJSONString(), User.class);//此处port为目标端口
                acpectObject.udpstate=0;
                String resend=JSON.toJSONString(acpectObject);

                byte[] req = jsonObject.toJSONString().getBytes();
                DatagramPacket reqdp=new DatagramPacket(req,req.length,acpectObject.address,acpectObject.port);
                try {
                    UDPclient.socket.send(reqdp);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            } case "US":{//收到成功的节点返回
                UDPclient.userlocal.udpstate=1;
                System.out.println("UDP bind Successed");
                JOptionPane.showMessageDialog(null,"成功收到节点请求，请选择新操作");
                String requestjson=request.substring(2);
                UserRequest requested=JSON.parseObject(requestjson,UserRequest.class);
                User saveruser= Tool.RequestUsertoUser(requested);
                UDPclient.bindUser.put(saveruser.username,saveruser);

                //保活连接
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                byte[] ll= Utils.byteMerger(new byte[]{0},"LL".getBytes());
                DatagramPacket d=new DatagramPacket(ll,ll.length,inetAddress,port);
                try {
                    UDPclient.socket.send(d);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            } case "RR":{//服务器返回的节点请求
                if ("ss".equals(request.substring(2,4))){
                    String requestjson=request.substring(4);
                    UserRequest requested=JSON.parseObject(requestjson,UserRequest.class);
                    UserRequest userRequest = new UserRequest();
                    userRequest.username=UDPclient.userlocal.username;
                    userRequest.toaddress=requested.requestaddress;
                    userRequest.toport=requested.requestport;
                    userRequest.inaddress=UDPclient.userlocal.inaddress;
                    userRequest.inport=UDPclient.userlocal.inport;
                    userRequest.requestaddress=requested.toaddress;
                    userRequest.requestport=requested.toport;
                    String requestnode="US"+JSON.toJSONString(userRequest);
                    byte[] bytess=requestnode.getBytes();
                    DatagramPacket packetsendtoclient=new DatagramPacket(bytess,bytess.length,userRequest.toaddress,userRequest.toport);
                    try {
                        UDPclient.socket.send(packetsendtoclient);
                        System.out.println("已经对对方主机发送");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }else if ("er".equals(request.substring(2,4))){
                    JOptionPane.showMessageDialog(null,"对方服务器失联","请尝试其他客户服务器",JOptionPane.INFORMATION_MESSAGE);
                    JOptionPane.showMessageDialog(null,"对方服务器失联,请再次尝试");
                }
                break;

            } case "LL":{
                LiveBinds liveBinds = (LiveBinds) Handler.liveMap.get(username);
                liveBinds.queue.offer(bytes);
                break;

            }case "SE":{
                System.exit(0);

            } case "LS":{
                String DR=request;
                DataRequest dataRequest=JSON.parseObject(DR.substring(2),DataRequest.class);
                DataSends dataSends= DataFactory.getSend(dataRequest);
                DataFactory.dataExecutor.execute(dataSends);
                break;
            }case "LR":{//以QS请求数据
                String LR=request;
                DataRequest dataRequest=JSON.parseObject(LR.substring(2),DataRequest.class);
                User user = bindUser.get(dataRequest.requestname);
                if (user==null){return;}
                String data=toLF("QS", UDPclient.userlocal.username,dataRequest.dir,dataRequest.id);
                DatagramPacket datagramPacket=new DatagramPacket(data.getBytes(),data.getBytes().length,user.address,user.port);
                DataRecives dataRecives=DataFactory.getRecive(dataRequest);
                DataFactory.dataExecutor.execute(dataRecives);
                break;
            } case "QS":{//队列数据接收
                String QS=request;
                DataRequest dataRequest=JSON.parseObject(QS.substring(2),DataRequest.class);

                UdpData udpData= Handler.UdpMap.get(dataRequest.requestname);

                DataQueue dataQueue= ReciveQueueFactory.getDataQueue(dataRequest.requestname);
                int id=dataQueue.newId();
                Byte byetId=new Byte((byte) id);
                dataQueue.addtask(byetId,dataRequest.dir);
                DataRecives dataRecives=DataFactory.getRecive(dataRequest);
                dataRecives.pool.execute(dataRecives);
                break;
            }  case "SD":{//数据接收
                UdpData udpData= (UdpData) Handler.UdpMap.get(username);
                SlowData slowData=new SlowData(udpData.userRequest.username,0);
                DataQueue dataQueue= ReciveQueueFactory.getDataQueue(udpData.userRequest.username);
                DataRequest dataRequest=JSON.parseObject(request.substring(2),DataRequest.class);
                slowData.receiveData(dataRequest.page);
                JOptionPane.showMessageDialog(null,"right");
                try {
                    Utils.toFile(slowData.recive,dataRequest.filename);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            //ToDO
            case "DS":{//数据发送
                UdpData udpData= (UdpData) Handler.UdpMap.get(username);
                SlowData slowData=new SlowData(udpData.userRequest.username,0);
                DataRequest dataRequest=JSON.parseObject(request.substring(2),DataRequest.class);
                slowData.sendfile(dataRequest.filename);
                break;
            }
            case "CL":{//数据发送
                UdpData udpData= Handler.UdpMap.get(username);
                int id=Integer.valueOf(request.substring(2));
                Byte byteId=new Byte((byte) id);
                DataQueue dataQueue=ReciveQueueFactory.ReciveData.get(username);
                dataQueue.addQue(byteId);
                byte[] bytes1=Utils.byteMerger(new byte[]{byteId},"LC".getBytes());
                DatagramPacket datagramPacket=new DatagramPacket(bytes1,bytes1.length,udpData.userRequest.toaddress,udpData.userRequest.toport);
                try {
                    udpData.dataSocket.send(datagramPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            case "CT":{//数据发送
                UdpData udpData= Handler.UdpMap.get(username);
                FileTrigger.TargetFile targetFile= JSON.parseObject(bytes,2,bytes.length-2, Charset.defaultCharset(), FileTrigger.TargetFile.class);
                Set<FileTrigger.TargetFile> set= Operta.listMapBuffer.get(username);
                if (set==null){
                    set=new HashSet<>();
                    Operta.listMapBuffer.put(username,set);
                }
                set.add(targetFile);

                byte[] bytes1=Utils.byteMerger(new byte[]{0},"TC".getBytes());
                DatagramPacket datagramPacket=new DatagramPacket(bytes1,bytes1.length,udpData.userRequest.toaddress,udpData.userRequest.toport);
                try {
                    udpData.dataSocket.send(datagramPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            case "TC":{//todo
                Map set= DataCloud.setMap.get(username);
                FileTrigger.TargetFile targetFile= JSON.parseObject(bytes,2,bytes.length-2, Charset.defaultCharset(), FileTrigger.TargetFile.class);
                set.remove(targetFile);

                break;
            }

            default:{

            }

        }


    }


    public void deal(){

        String choose = request.substring(0, 2);

        switch (choose) {
            case "UR": {//收到响应 选择请求
                if ("re".equals(request.substring(2,4))){
                    String info = request.substring(4);
                    JSONObject jsonObject = JSON.parseObject(info);
                    UserRequest acpectObject = JSON.parseObject(jsonObject.toJSONString(), UserRequest.class);
                    //        String info = new String(data, 0, packet.getLength());//创建字符串对象
                    System.out.println("我是服务器，客户端说：" + info);//输出提示信息


                    //  System.out.println("连接：" + JSON.toJSONString(u));
                    String sends = JSON.toJSONString(acpectObject);

                    String out = "UR" + sends;
                    byte[] bytes = out.getBytes();
                    DatagramPacket packetfind = new DatagramPacket(bytes, bytes.length);

                    try {
                        UDPclient.socket.send(packetfind);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }}
                if ("ro".equals(request.substring(2,4))){

                }
                break;

            }
            case "TT": {//tcp连接
                String info = request.substring(2);
                JSONObject jsonObject = JSON.parseObject(info);
                UserRequest acpectObject = JSON.parseObject(jsonObject.toJSONString(), UserRequest.class);
                //        String info = new String(data, 0, packet.getLength());//创建字符串对象
                System.out.println("我是服务器，客户端说：" + info);//输出提示信息
                /*
                 * 向客户端响应数据
                 */
                UserRequest userRequest=new UserRequest();
                userRequest.username= acpectObject.username;
                userRequest.inport=UDPclient.userlocal.inport;
                userRequest.inaddress=UDPclient.userlocal.inaddress;
                userRequest.requestport=UDPclient.userlocal.port;
                userRequest.requestaddress=UDPclient.userlocal.address;
                userRequest.toaddress=acpectObject.requestaddress;
                userRequest.toport=acpectObject.requestport;
                Thread thread =new Thread(new TcpServerBind(acpectObject));
                thread.start();

                break;

            } case "RE":{
                String info = request.substring(2);
                JSONObject jsonObject = JSON.parseObject(info);
                User acpectObject = JSON.parseObject(jsonObject.toJSONString(), User.class);//此处port为目标端口
                acpectObject.udpstate=0;
                String resend=JSON.toJSONString(acpectObject);

                byte[] req = jsonObject.toJSONString().getBytes();
                DatagramPacket reqdp=new DatagramPacket(req,req.length,acpectObject.address,acpectObject.port);
                try {
                    UDPclient.socket.send(reqdp);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;

            } case "US":{//收到成功的节点返回
                UDPclient.userlocal.udpstate=1;
                System.out.println("UDP bind Successed");
                JOptionPane.showMessageDialog(null,"成功收到节点请求，请选择新操作");
                String requestjson=request.substring(2);
                UserRequest requested=JSON.parseObject(requestjson,UserRequest.class);
                User saveruser= Tool.RequestUsertoUser(requested);
                UDPclient.bindUser.put(saveruser.username,saveruser);

                //保活连接
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                DatagramPacket d=new DatagramPacket("0000LL".getBytes(),"0000LL".getBytes().length,inetAddress,port);
                try {
                    UDPclient.socket.send(d);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            } case "RR":{//服务器返回的节点请求
                if ("ss".equals(request.substring(2,4))){
                    String requestjson=request.substring(4);
                    UserRequest requested=JSON.parseObject(requestjson,UserRequest.class);
                    UserRequest userRequest = new UserRequest();
                    userRequest.username=UDPclient.userlocal.username;
                    userRequest.toaddress=requested.requestaddress;
                    userRequest.toport=requested.requestport;
                    userRequest.inaddress=UDPclient.userlocal.inaddress;
                    userRequest.inport=UDPclient.userlocal.inport;
                    userRequest.requestaddress=requested.toaddress;
                    userRequest.requestport=requested.toport;
                    String requestnode="US"+JSON.toJSONString(userRequest);
                    byte[] bytes=requestnode.getBytes();
                    DatagramPacket packetsendtoclient=new DatagramPacket(bytes,bytes.length,userRequest.toaddress,userRequest.toport);
                    try {
                        UDPclient.socket.send(packetsendtoclient);
                        System.out.println("已经对对方主机发送");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }else if ("er".equals(request.substring(2,4))){
                    JOptionPane.showMessageDialog(null,"对方服务器失联","请尝试其他客户服务器",JOptionPane.INFORMATION_MESSAGE);
                    JOptionPane.showMessageDialog(null,"对方服务器失联,请再次尝试");
                }
                break;

            } case "SR":{//服务器转发的连接请求
//                String serverrequset=request.substring(2);
//                UserRequest serverRequest=JSON.parseObject(serverrequset,UserRequest.class);
//                UserRequest userRequest=new UserRequest();
//                userRequest.username=UDPclient.userlocal.username;
//                userRequest.toport=serverRequest.requestport;
//                userRequest.toaddress=serverRequest.requestaddress;
//                userRequest.requestport=serverRequest.toport;
//                userRequest.requestaddress=serverRequest.toaddress;
//                userRequest.inaddress=UDPclient.userlocal.inaddress;
//                userRequest.inport=UDPclient.userlocal.inport;
//                String sendnode="0000US"+JSON.toJSONString(userRequest);
//                byte[] bytes=sendnode.getBytes();
//                DatagramPacket datagramPacket=new DatagramPacket(bytes,bytes.length,userRequest.toaddress,userRequest.toport);
//
//                try {
//                    UDPclient.socket.send(datagramPacket);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                userRequest.username=serverRequest.username;
//                try {
//                    Thread.sleep(20);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                BlockingQueue<byte[]> blockingQueue= UDPclient.mainDataQueue.createQueue(userRequest.username,userRequest.toaddress, userRequest.toport).get(0);
//                UDPclient.executorService.execute(new Bindrec(blockingQueue,userRequest.username));
//
//                //open firewall
//                DatagramPacket ll=new DatagramPacket("OP".getBytes(),"OP".getBytes().length,userRequest.toaddress,userRequest.toport);
//                try {
//                    User saveruser= Tool.RequestUsertoUser(userRequest);
//                    UDPclient.bindList.add(saveruser);
//                    UDPclient.socket.send(ll);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
////                        JOptionPane.showMessageDialog(null,"succeed");
//                break;
                break;
            } case "LL":{
//                try {
//                    Thread.sleep(3000);
//
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                LiveBinds liveBinds = (LiveBinds) Handler.liveMap.get(username);
                liveBinds.queue.add(bytes);
//
//                DatagramPacket d=new DatagramPacket("0000LL".getBytes(),"0000LL".getBytes().length,inetAddress,port);
//                try {
//                    UDPclient.socket.send(d);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                break;
            } case "ND":{//收到数据端口连接请求
//                String databind=request.substring(2);
//                System.out.println("ND:"+request);
//                System.out.println("NDport:"+port);
//                UserRequest dataRequest=JSON.parseObject(databind,UserRequest.class);
//                UdpData udpData=new UdpData(dataRequest);
//                System.out.println("UDPName:"+dataRequest.username);
//                new Thread(udpData).start();

                break;
            }case "DN":{//数据端口返回请求,获取被请求端端口
//                String databind=request.substring(2);
//                UserRequest userRequest=JSON.parseObject(databind,UserRequest.class);
//                UdpData udpData=(UdpData) Handler.UdpMap.get(userRequest.username);
//                udpData.userRequest.toaddress=userRequest.requestaddress;
//                udpData.userRequest.toport=userRequest.requestport;
//                DatagramPacket datagramPacket=new DatagramPacket(JSON.toJSONBytes(udpData.userRequest),JSON.toJSONBytes(udpData.userRequest).length,udpData.userRequest.toaddress,udpData.userRequest.toport);
//                try {
//
//                    udpData.dataSocket.send(datagramPacket);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                break;

            }case "SE":{
                System.exit(0);

            } case "LS":{
                String DR=request;
                DataRequest dataRequest=JSON.parseObject(DR.substring(2),DataRequest.class);
                DataSends dataSends= DataFactory.getSend(dataRequest);
                DataFactory.dataExecutor.execute(dataSends);
                break;
            }case "LR":{//以QS请求数据
                String LR=request;
                DataRequest dataRequest=JSON.parseObject(LR.substring(2),DataRequest.class);
                User user = bindUser.get(dataRequest.requestname);
                if (user==null){return;}
                String data=toLF("QS", UDPclient.userlocal.username,dataRequest.dir,dataRequest.id);
                DatagramPacket datagramPacket=new DatagramPacket(data.getBytes(),data.getBytes().length,user.address,user.port);
                DataRecives dataRecives=DataFactory.getRecive(dataRequest);
                DataFactory.dataExecutor.execute(dataRecives);
                break;
            } case "QS":{//队列数据接收
                String QS=request;
                DataRequest dataRequest=JSON.parseObject(QS.substring(2),DataRequest.class);

                UdpData udpData= Handler.UdpMap.get(dataRequest.requestname);

                DataQueue dataQueue= ReciveQueueFactory.getDataQueue(dataRequest.requestname);
                int id=dataQueue.newId();
                Byte byetId=new Byte((byte) id);
                dataQueue.addtask(byetId,dataRequest.dir);
                DataRecives dataRecives=DataFactory.getRecive(dataRequest);
                dataRecives.pool.execute(dataRecives);
                break;
            }  case "SD":{//数据接收
                UdpData udpData= (UdpData) Handler.UdpMap.get(username);
                SlowData slowData=new SlowData(udpData.userRequest.username,0);
                DataQueue dataQueue= ReciveQueueFactory.getDataQueue(udpData.userRequest.username);
                DataRequest dataRequest=JSON.parseObject(request.substring(2),DataRequest.class);
                slowData.receiveData(dataRequest.page);
                JOptionPane.showMessageDialog(null,"right");
                try {
                    Utils.toFile(slowData.recive,dataRequest.filename);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            //ToDO
            case "DS":{//数据发送
                UdpData udpData= (UdpData) Handler.UdpMap.get(username);
                SlowData slowData=new SlowData(udpData.userRequest.username,0);
                DataRequest dataRequest=JSON.parseObject(request.substring(2),DataRequest.class);
                slowData.sendfile(dataRequest.filename);
                break;
            }

            default:{

            }

        }


    }

}
