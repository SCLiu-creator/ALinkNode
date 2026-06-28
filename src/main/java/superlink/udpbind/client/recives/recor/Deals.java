package superlink.udpbind.client.recives.recor;


import superlink.filemanage.classprocess.DealScan;
import superlink.filemanage.classprocess.OutJarload;
import superlink.filemanage.xmltool.XmlCreate;
import superlink.httpserver.servelt.action.get.ActionIndex;
import superlink.httpserver.servelt.action.post.ActionChat;
import superlink.httpserver.webserver.TcpProxyFactory;
import superlink.httpserver.webserver.TcpProxyServer;
import superlink.init.InitClass;
import superlink.tcpbind.choose.TcpServerBind;
import superlink.udpbind.chat.*;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.recives.*;
import superlink.udpbind.client.recives.data.blockBuffer.ByteStream;
import superlink.udpbind.client.recives.datalen.dataCache.BufferDataCon;
import superlink.udpbind.client.server.DataPacket;
import superlink.udpbind.client.server.ServerCon;
import superlink.udpbind.remote.invoking.InvokeTemplate;
import superlink.udpbind.remote.invoking.LinkCallTemplate;
import superlink.udpbind.remote.invoking.RemoteWorkContrains;
import superlink.udpbind.tcpproxy.ProxySocket;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.data.datastream.DataStreamAB;
import superlink.udpbind.client.recives.datalen.AutoBuffer;


import superlink.udpbind.client.recives.data.*;
import superlink.udpbind.client.recives.data.stream1.QSContrain;
import superlink.udpbind.client.recives.data.stream1.QueueStream;
import superlink.udpbind.cloude.DataCloud;
import superlink.udpbind.client.recives.datalen.*;
import superlink.udpbind.client.recives.datalen.DataLength;
import superlink.udpbind.cloude.CloudLocal;
import superlink.udpbind.cloude.CloudeListenCaset;
import superlink.udpbind.cloude.CloudeSynContainer;
import superlink.udpbind.cloude.data.ChanlsFactory;
import superlink.udpbind.cloude.data.CloudeChanel;
import superlink.udpbind.cloude.FileTrigger;
import superlink.udpbind.cloude.operta.Browse;
import superlink.udpbind.cloude.operta.Consist;
import superlink.udpbind.cloude.operta.Monitor;
import superlink.udpbind.cloude.operta.Server;
import superlink.udpbind.cloude.operta.broadcast.Operta;
import superlink.udpbind.cloude.operta.unicast.UseOperta;
import superlink.udpbind.controller.Controller;
import superlink.udpbind.client.recives.data.DataSend;
import superlink.udpbind.dataLink.UdpData;
import com.alibaba.fastjson2.JSON;
import superlink.udpbind.handle.Handler;
import superlink.udpbind.handle.LiveHandle;
import superlink.udpbind.handle.handler.DealCloudeAutoMap;
import superlink.udpbind.usedata.BufferRequest;
import superlink.udpbind.usedata.DataRequest;
import superlink.udpbind.usedata.UserRequest;
import superlink.util.Utils;
import superlink.util.thread.SThreadPool;
import superlink.util.thread.ThreadFunction;


import javax.swing.*;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import static superlink.udpbind.client.UDPclient.*;
import static superlink.udpbind.client.server.ServerCon.runsend;
import static superlink.udpbind.cloude.CloudLocal.closeCloudeUser;
import static superlink.udpbind.dataLink.data.DataFactory.dataExecutor;
import static superlink.util.Tool.toLF;
import static superlink.util.Utils.*;


public class Deals {
    public TcpServerBind tcpThreadBind;
    public String request;
    public InetAddress inetAddress;
    public Integer port;
    public String username;
    public byte[] bytes;
    public int i = 1;

    DealsRun[] runs;

    public Deals(byte[] bytes, String name) {
        request = new String(bytes);
        username = name;
        //String[] strings=MainDataQueue.ipname.get(name).substring(1).split(":");
        UserContext userContext = null;
        try {
            userContext = mainDataQueue.getUserContext(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        inetAddress = userContext.inetAddress;
        port = userContext.port;
    }

    public Deals(String name) {
        this.username = name;
        UserContext userContext = null;
        try {
            userContext = mainDataQueue.getUserContext(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        inetAddress = userContext.inetAddress;
        port = userContext.port;

        this.runs=new DealsRun[1];
        runs[0]=new DealRe(this);
    }

    public UserContext Deals(String name) {
        this.username = name;
        UserContext userContext = null;
        try {
            userContext = mainDataQueue.getUserContext(name);
            inetAddress = userContext.inetAddress;
            port = userContext.port;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userContext;
    }

    public Deals setRequest(byte[] bytes) {
        this.bytes = bytes;
        this.request = new String(bytes);
        return this;
    }

    public void deal() throws Exception {
        String choose = request.substring(0, 2);

        if (!DealsFilter.process(username, request)) {
            return;
        }
        i++;
        switch (choose) {
            case "rr": {//重新定位连接
                UserContext userContext = UDPclient.getUser(username);
                ByteReBuffer reBuffer = (ByteReBuffer) userContext.getQueue((short) 7);
                int pos = Utils.byteArrayToInt(bytes,2);
                reBuffer.reSet(pos);
                break;
            }
            case "rc": {//重新定位连接
                UserContext userContext=mainDataQueue.getUserContext(username);
                byte[] bytes1= Arrays.copyOfRange(bytes,0,bytes.length);
                bytes1[1]=0;
                Senders.Sends(userContext.getBothId(),0,userContext.inetAddress,userContext.port,bytes1);
                RemoteWorkContrains.threadPool.execute(()->{
                    AutoBuffer autoBuffer=new AutoBuffer(username);
                    String hash=new String(bytes,3,bytes.length-3);
                    Object o=autoBuffer.reqData(hash);
                    byte[] bytesdata=BufferDataCon.toData(o);
                    autoBuffer.clear();
                    InvokeTemplate invokeTemplate= JSON.parseObject(new String(bytesdata),InvokeTemplate.class);

                    Object obj=invokeTemplate.RI(invokeTemplate.objects);
                    BufferDataCon.setData(hash,JSON.toJSONString(obj).getBytes(),1);
                });
                break;
            }

            case "lc": {//重新定位连接
                UserContext userContext=mainDataQueue.getUserContext(username);
                byte[] bytes1= Arrays.copyOfRange(bytes,0,bytes.length);
                bytes1[1]=0;
                Senders.Sends(userContext.getBothId(),0,userContext.inetAddress,userContext.port,bytes1);
                LinkCallTemplate template= JSON.parseObject(request.substring(2), LinkCallTemplate.class);
                RemoteWorkContrains.threadPool.execute(()->{
                    String s=null;
                    if(template.data!=null || template.objects!=null){
                        AsySteam asySteam=new AsySteam(template.startUser, (short) template.targetId);
                        asySteam.reqData(null);
                        byte[] bytes=asySteam.getbytes();
                        s = new String(bytes);
                        asySteam.clear();
                    }
                    if(template.para!=null && s==null){
                        s=template.para;
                    }
                    Object data=null;
                    try {
                        data=template.RIp(s);
                    }catch (Exception e){
                        e.printStackTrace();
                        data=new byte[0];
                    }
                    AsySteam asySteam = new AsySteam(template.startUser, (short) template.orginId);
                    asySteam.getWrite();
                    if(data!=null){
                        if(data instanceof File){
                            asySteam.writeFile((File) data);
                        }else if(data instanceof InputStream){
                            asySteam.writeInstream((InputStream) data);
                        }else if(data instanceof byte[]){
                            asySteam.writeBytes((byte[]) data);
                        }else if(data instanceof String){
                            asySteam.writeBytes(((String) data).getBytes());
                        }
                    }else {
                        asySteam.writeBytes(new byte[0]);
                    }
//                    asySteam.clear();

//                    AutoBuffer autoBuffer=new AutoBuffer(username);
//                    String hash=new String(bytes,3,bytes.length-3);
//                    Object o=autoBuffer.reqData(hash);
//                    byte[] bytesdata=BufferDataCon.toData(o);
//                    autoBuffer.clear();


//                    Object obj=template.RI(template.objects);
//                    BufferDataCon.setData(hash,JSON.toJSONString(obj).getBytes(),1);
                });
                break;
            }
            case "SR": {//信号转发
                short id = Utils.byteArrayToshort(bytes,2);
                UserContext userContext = UDPclient.getUser(username);
                ByteBufer buffer = userContext.getQueue(id);
                buffer.add(Arrays.copyOfRange(bytes,4,bytes.length-4));
                break;
            }
            case "TT": {//tcp连接
                String info = request.substring(2);
//                String info = request.substring(2);
//                JSONObject jsonObject = JSON.parseObject(info);
//                UserRequest acpectObject = JSON.parseObject(jsonObject.toJSONString(), UserRequest.class);
//                //        String info = new String(data, 0, packet.getLength());//创建字符串对象
//                System.out.println("我是服务器，客户端说：" + info);//输出提示信息
//                UserRequest userRequest = new UserRequest();
//                userRequest.username = acpectObject.username;
//                userRequest.inport = userlocal.inport;
//                userRequest.inaddress = userlocal.inaddress;
//                userRequest.requestport = userlocal.port;
//                userRequest.requestaddress = userlocal.address;
//                userRequest.toaddress = acpectObject.requestaddress;
//                userRequest.toport = acpectObject.requestport;
//                Thread thread = new Thread(new TcpServerBind(acpectObject));
//                thread.start();
                break;
            }
            case "LL": {
//                LiveHandle liveHandle = (LiveHandle) Handler.DispectMap.get("LiveBind");
//                liveHandle.add(request.substring(2));
//                System.out.println("ll heart  "+username);
//                userMap.get(username).notifyAll();
//                if(request.length()==2){
//                    break;
//                }
                UserContext userContext=UDPclient.getUser(username);
                Senders.Sends(userContext.getBothId(),0,
                        userContext.inetAddress,userContext.port,
                        "ll".getBytes());
                Map map = ActionIndex.objectList.get(username);
                if (map != null) {
                    if (map.size() != 0) {
                        map.forEach((k, v) -> {
                            if (v instanceof Runnable) {
                                try {
                                    ((Runnable) v).run();
                                } catch (Exception | Error e) {
                                    e.printStackTrace();
                                }
                                map.remove(k);
                            }
                        });
                    }
                }
                //System.out.println("Deals:"+request);
                break;
            }
            case "ll": {
                UserContext userContext=UDPclient.getUser(username);
                long time=System.currentTimeMillis() -userContext.waitTime;
                userContext.delayTime = (time+userContext.delayTime)/2;
                break;
            }
            case "DE": {
                mainDataQueue.delUser(username);
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
                //todo qx
                System.exit(0);
            }
            case "LS": {
                String DR = request;
                DataRequest dataRequest = JSON.parseObject(DR.substring(2), DataRequest.class);
                DataSend dataSend = null;
                try {
                    dataSend = new DataSend(dataRequest);
                    dataExecutor.execute(dataSend);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            }
            case "GU": {
                String DR = request;
                DataRequest dataRequest = JSON.parseObject(DR.substring(2), DataRequest.class);
                DataSend dataSend = null;
                try {
                    dataSend = new DataSend(dataRequest);
                    MainDataQueue.mainthreadPoolExecutor.execute(dataSend);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            }
            case "LR": {//以QS请求数据
                String LR = request;
                DataRequest dataRequest = JSON.parseObject(LR.substring(2), DataRequest.class);
                UserContext userContext=mainDataQueue.getUserContext(dataRequest.requestname);
                if (userContext == null) {
                    return;
                }
                String data = toLF("QS", userlocal.username, dataRequest.dir, dataRequest.id);
                Senders.Sends( userContext.inetAddress, userContext.port,data.getBytes());
                DataSend dataSend = null;
                try {
                    dataSend = new DataSend(dataRequest);
                    dataExecutor.execute(dataSend);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            }
            case "QS": {//队列数据接收
                String QS = request;
                DataRequest dataRequest = JSON.parseObject(QS.substring(2), DataRequest.class);

                UserContext userContext = null;
                try {
                    userContext = mainDataQueue.getUserContext(dataRequest.requestname);
                } catch (Exception e) {
                    break;
                }
                //.getId(dataRequest.requestname);
                userContext.getDataQue((short) dataRequest.id);
                DataRecive dataRecive = new DataRecive(dataRequest);
                dataRecive.pool.execute(dataRecive);
                break;
            }
            case "DT": {//数据发送
                DataRequest dataRequest = JSON.parseObject(request.substring(2), DataRequest.class);
                new Thread(() -> {
                    DataTool dataTool = null;
                    try {
                        dataTool = new DataTool(dataRequest.requestname, dataRequest.id);
                        dataTool.fp=dataRequest.pl;
                        dataTool.sendfile(dataRequest.filename);
                        dataTool.finalize();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
                break;
            }
            case "Dt": {//数据发送
                DataRequest dataRequest = JSON.parseObject(request.substring(2), DataRequest.class);
                UserContext userContext = mainDataQueue.getUserContext(username);
                ByteBufer blockingQueue = userContext.getQueue((short) dataRequest.id);
                if (blockingQueue != null) {
                    Senders.Sends(dataRequest.requestname, dataRequest.id, "Dt".getBytes());
                } else {
                    if (dataRequest.filename != null) {
                        new Thread(() -> {
                            DataTool1 dataTool = null;
                            try {
                                dataTool = new DataTool1(username, dataRequest.id);
                                dataTool.sendfile(dataRequest.filename);
                                dataTool.finalize(dataTool);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }).start();
                    }
                }
                break;
            }
            case "DB": {//数据发送
                DataRequest dataRequest = JSON.parseObject(request.substring(2), DataRequest.class);

                DataByte dataTool = new DataByte(dataRequest.requestname, dataRequest.id);
                dataTool.sends(dataRequest);
//                DataByte.setThreadPool.reExecute(dataTool);
                break;
            }
            case "Db": {//数据发送
                DataRequest dataRequest = JSON.parseObject(request.substring(2), DataRequest.class);

                DataBuffer dataTool = new DataBuffer(dataRequest.requestname, dataRequest.id);
                dataTool.sends(dataRequest);
//                DataByte.setThreadPool.reExecute(dataTool);
                break;
            }
            case "Ds": {//数据发送
                DataRequest dataRequest = JSON.parseObject(request.substring(2), DataRequest.class);
                UserContext userContext= UDPclient.getUser(username);
                DataSyn dataSyn = DsCon.getInstance(userContext, (short) dataRequest.id);
                dataSyn.sends(dataRequest);
//                DataByte.setThreadPool.reExecute(dataTool);
                break;
            }
            case "sD": {//数据发送
//todo
                break;
            }
            case "DI": {//数据发送

                DataRequest dataRequest = JSON.parseObject(request.substring(2), DataRequest.class);

                DataInteger dataTool = new DataInteger(dataRequest.requestname, dataRequest.id);
                dataTool.sends(dataRequest);
//                DataInteger.setThreadPool.reExecute(dataTool);
                break;
            }
            case "AD": {//数据发送
                DataRequest dataRequest;
                AutoData autoData;
                AutoData autoData0;
                try {
                    dataRequest = JSON.parseObject(request.substring(2), DataRequest.class);
                    autoData = new AutoData(dataRequest.requestname, (short) dataRequest.id);
                    autoData0 = autoData.sends(dataRequest);
                    if (autoData.equals(autoData0)) {
                        autoData.execute(true);
                    } else {
                        autoData.aSend();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case "DA": {//数据发送
                try {
                    UserContext userContext = mainDataQueue.getUserContext(username);
                    short id = Utils.byteArrayToshort(bytes, 2);
//                    Integer hsah=userContext.getUserId()&id;
//                    Integer hsah1=userContext.getBothId()&id;
                    AutoData autoData = new AutoData(username, id);
                    autoData = AutoData.DataMap.get(autoData);
                    if (autoData == null) {
                        Senders.Sends(username, id, "AD".getBytes());
                    } else {
                        autoData.senders.send(autoData.send);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
//            case "ba": {//数据发送
//                BufferRequest dataRequest;
//                AutoBuffer autoBuffer;
//                AutoBuffer autoData0;
//                try {
//                    dataRequest = JSON.parseObject(request.substring(2), BufferRequest.class);
//                    autoBuffer = new AutoBuffer(dataRequest.name, dataRequest.id);
//                    autoData0 = autoBuffer.getBuf(dataRequest);
//                    if (autoBuffer == autoData0) {
//                        autoBuffer.execute(true);
//                    } else {
//                        autoBuffer.aSend();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                break;
//            }
            case "ab": {//数据发送
                try {
                    BufferRequest dataRequest = JSON.parseObject(request.substring(2), BufferRequest.class);
                    short id = (short) dataRequest.id;
                    new Thread(()->{
                        AutoBuffer autoData = new AutoBuffer(username, id);
                        autoData.reqData(dataRequest.bufname);
                        BufferDataCon.setData(dataRequest.bufname,autoData.rev);
                    }).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case "Ad": {//数据发送
                BufferRequest dataRequest;
                AutoAsyFile autoBuffer;
                AutoAsyFile autoData0;
                try {
                    dataRequest = JSON.parseObject(request.substring(2), BufferRequest.class);
                    autoBuffer = new AutoAsyFile(dataRequest.name, dataRequest.id);
                    autoData0 = autoBuffer.getBuf(dataRequest);
                    if (!autoBuffer.equals(autoData0) || autoData0==autoBuffer) {
                        autoBuffer.execute(true);
                    } else {
                        autoData0.aSend();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case "dA": {//数据发送
                try {
                    UserContext userContext = mainDataQueue.getUserContext(username);
                    short id = (short) Utils.byteArrayToInt(bytes, 2);
                    AutoAsyFile autoData = new AutoAsyFile(username, id);
                    autoData = AutoAsyFile.DataMap.get(autoData);
                    if (autoData == null) {
                        Senders.Sends(username, id, "Ad".getBytes());
                        //结束
                    } else {
                        autoData.senders.send(autoData.send);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case "As": {//数据发送
                BufferRequest dataRequest;
                AsySteam autoBuffer;
                AsySteam autoData0;
                try {
                    dataRequest = JSON.parseObject(request.substring(2), BufferRequest.class);
                    autoBuffer = new AsySteam(dataRequest.name, (short) dataRequest.id);
                    autoData0 = AsySteam.getSteam(autoBuffer);
                    autoBuffer.pagelen=dataRequest.pl;
                    if (autoBuffer.equals(autoData0)) {
                        autoData0.bdr =dataRequest;
//                        autoBuffer.execute(true);
                        executorService.submit(()->{
                            autoData0.testSend();
                        });
                    } else {
                        autoData0.aSend();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case "sA": {//数据发送
                try {
                    UserContext userContext = mainDataQueue.getUserContext(username);
                    short id = (short) Utils.byteArrayToInt(bytes, 2);
//                    Integer hsah=userContext.getUserId()&id;
//                    Integer hsah1=userContext.getBothId()&id;
                    AsySteam autoData = new AsySteam(username, id);
                    autoData = AsySteam.DataMap.get(autoData);
                    if (autoData == null) {
                        Senders.Sends(username, id, "As".getBytes());
                        //结束
                    } else {
                        autoData.senders.send(autoData.send);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case "rw": {//数据发送
                try {
                    UserContext userContext = mainDataQueue.getUserContext(username);
                    short id1 = Utils.byteArrayToshort(bytes, 2);
                    short id2 = Utils.byteArrayToshort(bytes, 4);
//                    Integer hsah=userContext.getUserId()&id;
//                    Integer hsah1=userContext.getBothId()&id;
                    AsySteam autoData1 = new AsySteam(username, id1);
                    autoData1.reqData(null);
                    AsySteam autoData2 = new AsySteam(username, id2);
                    autoData2.getWrite();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case "so": {//数据发送
                try {
                    UserContext userContext = mainDataQueue.getUserContext(username);
                    short id = (short) Utils.byteArrayToInt(bytes, 2);
//                    Integer hsah=userContext.getUserId()&id;
//                    Integer hsah1=userContext.getBothId()&id;
                    AsySteam autoData = new AsySteam(username, id);
                    autoData = AsySteam.DataMap.get(autoData);
                    if (autoData != null) {
                        Senders.Sends(username, id, "OK".getBytes());
                        //结束
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case "Ab": {//数据发送
                BufferRequest dataRequest;
                AsyBuffer autoBuffer;
                AsyBuffer autoData0;
                try {
                    dataRequest = JSON.parseObject(request.substring(2), BufferRequest.class);
                    autoBuffer = new AsyBuffer(dataRequest.name, dataRequest.id);
                    autoData0 = autoBuffer.getBuf(dataRequest);
                    if (!autoBuffer.equals(autoData0) || autoData0==autoBuffer) {
                        autoBuffer.execute(true);
                    } else {
                        autoData0.aSend();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case "bA": {//数据发送
                try {
                    UserContext userContext = mainDataQueue.getUserContext(username);
                    short id = (short) Utils.byteArrayToInt(bytes, 2);
//                    Integer hsah=userContext.getUserId()&id;
//                    Integer hsah1=userContext.getBothId()&id;
                    AsyBuffer autoData = new AsyBuffer(username, id);
                    autoData = AsyBuffer.DataMap.get(autoData);
                    if (autoData == null) {
                        Senders.Sends(username, id, "bA".getBytes());
                        //结束
                    } else {
                        autoData.senders.send(autoData.send);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case "AB": {//数据发送
                BufferRequest dataRequest;
                AutoBuffer autoBuffer;
                AutoBuffer autoData0;
                try {
                    dataRequest = JSON.parseObject(request.substring(2), BufferRequest.class);
                    autoBuffer = new AutoBuffer(dataRequest.name, dataRequest.id);
                    autoData0 = autoBuffer.getBuf(dataRequest);
                    if (!autoBuffer.equals(autoData0) || autoData0==autoBuffer) {
                        autoBuffer.execute(true);
                    } else {
                        autoData0.aSend();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case "BA": {//数据发送
                try {
                    UserContext userContext = mainDataQueue.getUserContext(username);
                    short id = (short) Utils.byteArrayToInt(bytes, 2);
//                    Integer hsah=userContext.getUserId()&id;
//                    Integer hsah1=userContext.getBothId()&id;
                    AutoBuffer autoData = new AutoBuffer(username, id);
                    autoData = AutoBuffer.DataMap.get(autoData);
                    if (autoData == null) {
                        Senders.Sends(username, id, "AB".getBytes());
                        //结束
                    } else {
                        autoData.senders.send(autoData.send);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case "DR": {//数据发送
                DataRequest dataRequest = JSON.parseObject(request.substring(2), DataRequest.class);
                DataReqAuto dataReqAuto = null;
                try {
                    dataReqAuto = new DataReqAuto(dataRequest.requestname, dataRequest.id);
                    dataReqAuto.sends(dataRequest);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            //短数据发送
            case "Dr": {//数据发送
                DataRequest dataRequest = JSON.parseObject(request.substring(2), DataRequest.class);
                DataSmall dataTool = (DataSmall) DataLength.mapThreadPool.get(this.username);
                if (dataTool == null) {
                    try {
                        dataTool = new DataSmall(dataRequest.requestname, dataRequest.id);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                dataTool.sends(username, dataRequest);
                break;
            }
            case "CL": {//数据发送

                break;
            }
            case "DC": {//数据发送
                String s = request.substring(2);
                Short id = Short.valueOf(s);
                DataStreamAB queueStream = DataStreamAB.dataStreamMap.get(username+":"+id);
                if (queueStream == null) {
                    queueStream = new DataStreamAB(username, id, true);
                }else {
                    queueStream.senders.send(new byte[0]);
                    break;
                }
                queueStream.senders.send(new byte[]{0,-1,0,-128});

                DataStreamAB finalQueueStream = queueStream;
                Thread task =new Thread(() -> {
                    File file = new File(XmlCreate.userShow+"/"+"headPic");
                    try (FileInputStream inputStream = new FileInputStream(file)){
                        byte[] bytes = new byte[1024];
                        int i=-1;
                        int p=0;
                        while ((i = inputStream.read(bytes))>0){
                            byte[] data = Utils.subByte(bytes,0,i);
                            data = Utils.byteMerger(Utils.intToByteArray(p),data);
                            finalQueueStream.write( data);
                            p++;
                        }
                        finalQueueStream.write(Utils.byteMerger(Utils.intToByteArray(0),new byte[0]));
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    file = new File(XmlCreate.userShow+"/test.jpg");
                    File file1 = new File(XmlCreate.userShow.replace(UDPclient.userlocal.username,username)+"/headPic");
                    try(FileOutputStream fos = new FileOutputStream(file);FileInputStream fos1 = new FileInputStream(file1)) {
                        byte[] bytes = null;
                        while ((bytes=finalQueueStream.read0()).length!=4){
                            System.out.println("dataL:  "+bytes.length);
                            System.out.println("data_pos:"+Utils.byteArrayToInt(bytes));
                            byte[] byteso = Utils.subByte(bytes,4,bytes.length);
                            byte[] bytes1 = new byte[byteso.length];
                            fos1.read(bytes1);
                            if(!Arrays.equals(byteso,bytes1)){
                                System.out.println("错误");
                            }
                            fos.write(byteso );
                        }
                        System.out.println("结束");
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
//                task.start();
                //空置
                break;
            }
            case "cl": {//数据发送
                DataStreamAB queueStream = DataStreamAB.dataStreamMap.get(username);
                if (queueStream == null) {
                    int id = Integer.valueOf(request.substring(2));
                    queueStream = new DataStreamAB(username, (short) id, true);
                    DataStreamAB.dataStreamMap.put(username, queueStream);
                }
                queueStream.senders.send("cl".getBytes());
//                if (queueStream==null){
//                    int id=Integer.valueOf(request.substring(2));
//                    System.out.println("dataStream  +"+username+"   "+id);
//                    queueStream=new DataStreamAB(username,id);
//                    DataStreamAB finalQueueStream = queueStream;
//                    new Thread(()->{
//                        Thread.currentThread().setName("QueueStream");
//                        int i=0;
//                        try {
//                            FileInputStream inputStream=new FileInputStream(new File(Utils.chooseFile()));
//                                    //Utils.chooseFile()C:\\Users\\liushengchang-n\\Desktop\\main.zip
//                            //C:\Users\liusc\Desktop\ed953d6c1d8444ba86c6d829f515eb9c.jpg
//                            int len=0;byte[] bytes1;bytes1=new byte[1450];
//                            while ((len=inputStream.read(bytes1))!=-1){
//                                i++;
//                                byte[] bytes11=Utils.subByte(bytes1,0,len);
//                                finalQueueStream.send(bytes11);
//                            }
//                            finalQueueStream.close();
//                        } catch (Exception | Error e) {
//                            e.printStackTrace();
//                        }finally {
//                            System.out.println("OVER: QueueStesm");
//                        }
//                    }).start();
//                }
                break;
            }
            case "PD": {//数据发送
                short id = Utils.byteArrayToshort(bytes, 2);
                int port = Utils.byteArrayToInt(bytes, 4);
                ProxySocket proxySocket = new ProxySocket(port);
                proxySocket.setMode(false);
                proxySocket.getDataStream(username, id, true);
                proxySocket.run();
                break;
            }
            case "RQ": {//数据发送

                break;
            }
            case "RC": {//数据发送
                String QS = request;
                UserRequest userRequest = JSON.parseObject(QS.substring(2), UserRequest.class);
                if (!CloudLocal.isInitSynContainer()) {
                    CloudLocal.init(60 * 100 * 2);
                }
                Controller.ReqCloudePage(userRequest.username);
                break;
            }
            case "RA": {//数据发送
                String QS = request;
                DataRequest req = JSON.parseObject(QS.substring(2), DataRequest.class);
                if (!CloudLocal.isInitSynContainer()) {
                    CloudLocal.init(60 * 100 * 2);
                }
                DealCloudeAutoMap.getInstance(req.requestname, username, req.id).process();
                break;
            }
            case "CF": {//数据发送
                String QS = request;
                AtomicReference<ChanlsFactory.ID> i = new AtomicReference<>(JSON.parseObject(QS.substring(2), ChanlsFactory.ID.class));
                executorService.submit(() -> {
                    UserContext userContext = null;
                    try {
                        userContext = mainDataQueue.getUserContext(username);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                    if (ChanlsFactory.ChanlsMap.get(username) != null) {
                        i.set(ChanlsFactory.ChanlsMap.get(username).idkey);
                        byte[] bytes = Utils.byteMerger(Utils.getUseridByte(userContext.getBothId(), (short) i.get().you), JSON.toJSONBytes(i));
                        Senders.Sends( userContext.inetAddress, userContext.port,bytes);
                    } else {
                        i.get().you = userContext.newQueue();
                        i.get().you1 = userContext.newQueue();
                        byte[] bytes = Utils.byteMerger(Utils.getUseridByte(userContext.getBothId(), (short) i.get().my), JSON.toJSONBytes(i));
                        Senders.Sends( userContext.inetAddress, userContext.port,bytes);
                        i.get().change().change1();
                        CloudeChanel cloudeChanel = new CloudeChanel();
                        cloudeChanel.build(userContext, i.get());
                        ChanlsFactory.ChanlsMap.put(username, cloudeChanel);
                    }
                });
                break;
            }

            case "CT": {//数据接受
                if (Operta.listMapBuffer == null) {
                    Utils.dealsSend(username, ("TC" + request.substring(2)).getBytes());
                    break;
                }
                FileTrigger.TargetFile targetFile = null;
                synchronized (Operta.class) {
                    Map<String, Set<FileTrigger.TargetFile>> map = Operta.listMapBuffer;
                    targetFile = JSON.parseObject(bytes, 2, bytes.length - 2, Charset.defaultCharset(), FileTrigger.TargetFile.class);
                    Set<FileTrigger.TargetFile> set = map.get(username);
                    if (set == null) {
                        set = new HashSet<>();
                        map.put(username, set);
                    }
                    set.add(targetFile);
                }

                Operta operta = CloudeListenCaset.cloudeListenCaset.operta;
                if (operta instanceof Monitor) {
                    Utils.dealsSend(username, ("TC" + JSON.toJSONString(targetFile)).getBytes());
                }
                if (operta instanceof Consist) {
                    Utils.dealsSend(username, ("TC" + request.substring(2)).getBytes());
                }
                if (operta instanceof Server) {
                    Utils.dealsSend(username, ("TC" + request.substring(2)).getBytes());
                }
                if (operta instanceof Browse) {
                    Utils.dealsSend(username, ("TC" + request.substring(2)).getBytes());
                }
                operta.thread.interrupt();
//                Utils.dealsSend(username,("TC"+request.substring(2)).getBytes());
                break;
            }
            case "TC": {//todo
                Map set = DataCloud.setMap.get(username);
                FileTrigger.TargetFile targetFile = JSON.parseObject(bytes, 2, bytes.length - 2, Charset.defaultCharset(), FileTrigger.TargetFile.class);
                set.remove(targetFile);
                break;
            }
            case "ct": {//数据发送
                if (Operta.listMapBuffer == null) {
                    Utils.dealsSend(username, ("TC" + request.substring(2)).getBytes());
                    break;
                }
                FileTrigger.TargetFile targetFile = null;
                synchronized (Operta.class) {
                    Map<String, Set<FileTrigger.TargetFile>> map = Operta.listMapBuffer;
                    targetFile = JSON.parseObject(bytes, 2, bytes.length - 2, Charset.defaultCharset(), FileTrigger.TargetFile.class);
                    Set<FileTrigger.TargetFile> set = map.get(username);
                    if (set == null) {
                        set = new HashSet<>();
                        map.put(username, set);
                    }
                    set.add(targetFile);
                }

                Operta operta = CloudeListenCaset.cloudeListenCaset.operta;
                if (operta instanceof Monitor) {
                    Utils.dealsSend(username, ("TC" + JSON.toJSONString(targetFile)).getBytes());
                }
                if (operta instanceof Consist) {
                    Utils.dealsSend(username, ("TC" + request.substring(2)).getBytes());
                }
                if (operta instanceof Server) {
                    Utils.dealsSend(username, ("TC" + request.substring(2)).getBytes());
                }
                if (operta instanceof Browse) {
                    Utils.dealsSend(username, ("TC" + request.substring(2)).getBytes());
                }
                operta.thread.interrupt();
//                Utils.dealsSend(username,("TC"+request.substring(2)).getBytes());
                break;
            }
            case "tc": {//todo
                Map set = DataCloud.setMap.get(username);
                FileTrigger.TargetFile targetFile = JSON.parseObject(bytes, 2, bytes.length - 2, Charset.defaultCharset(), FileTrigger.TargetFile.class);
                set.remove(targetFile);
                break;
            }

            case "UT": {//数据处理缓冲区，并发送
                UseOperta.OpertaFile opertaFile = JSON.parseObject(bytes, 2, bytes.length - 2, Charset.defaultCharset(), UseOperta.OpertaFile.class);
                UseOperta.addOpera(opertaFile,username);

                Utils.dealsSend(username, ("TU" + request.substring(2)).getBytes());
                break;
            }
            case "TU": {//todo
                Set set = UseOperta.setUniSendbuffer.get(username);
                UseOperta.OpertaFile opertaFile = JSON.parseObject(bytes, 2, bytes.length - 2, StandardCharsets.UTF_8, UseOperta.OpertaFile.class);
                set.remove(opertaFile);
                break;
            }

            case "co": {//todo
                try {
                    mainDataQueue.delUser(username);
                    if (CloudLocal.isInitSynContainer()) {
                        CloudeSynContainer container = CloudLocal.getSynContainer();
                        if (container.Mapbin != null) {
                            container.Mapbin.remove(username);
                        }
                    }
                } catch (Exception e) {
                    System.out.println(" close with: " + username);
                }
                break;
            }
            case "cc": {//todo
                try {
                    if (CloudLocal.isInitSynContainer()) {
                        if (CloudLocal.getSynContainer().Mapbin != null) {
                            closeCloudeUser(username);
//                        CloudLocal.synContainer.Mapbin.remove(username);
//                        CloudeLisentCaset.cloudeLisentCaset.dataCloud.s
//                                DataCloud.setMap.remove(username);
                        }
                    }

                } catch (Exception e) {
                    System.out.println(" closecloude with: " + username);
                }
                break;
            }
            case "rf": {//todo

                break;
            }
            case "Qt": {
                QueueStream queueStream = QueueStream.map.get(username);
                if (queueStream == null) {
                    int id = Integer.valueOf(request.substring(2));
                    queueStream = new QueueStream(username, QueueStream.defsize, id);
                    QueueStream finalQueueStream = queueStream;
                    new Thread(() -> {
                        Thread.currentThread().setName("QueueStream");
                        int i = 0;
                        try {
                            FileInputStream inputStream = new FileInputStream(
                                    new File(Utils.chooseFile()));
                            //C:\Users\liusc\Desktop\ed953d6c1d8444ba86c6d829f515eb9c.jpg
                            int len = 0;
                            byte[] bytes1;
                            bytes1 = new byte[1450];
                            while ((len = inputStream.read(bytes1)) != -1) {
                                i++;
//                                len=inputStream.read(bytes1);
                                byte[] bytes11 = Utils.subByte(bytes1, 0, len);
                                finalQueueStream.synWrite(bytes11);
                            }
                            System.out.println("OVER: Qu  " + finalQueueStream.posl);
                            System.out.println("OVER: Que  " + finalQueueStream.pos);
                            finalQueueStream.over();
                            System.out.println("over");
                        } catch (Exception | Error e) {
                            e.printStackTrace();
                        } finally {
                            System.out.println("OVER: QueueStesm");
                            System.out.println("OVER: Qu  " + finalQueueStream.posl);
                            System.out.println("OVER: Que  " + finalQueueStream.pos);
                        }
                    }).start();
                }
                break;
            }
            case "QT": {
                QSContrain qsContrain = QSContrain.map.get(username);
                if (qsContrain == null) {
                    qsContrain = QSContrain.getInstance(username);
                    int id = Integer.parseInt(request.substring(2));
                    QueueStream rec = new QueueStream(username, QueueStream.defsize, id);
                    QueueStream se = new QueueStream(username, QueueStream.defsize);
//                    rec.build1();
                    qsContrain.reader = se;
                    se.build1();
                    qsContrain.rewiter = rec;
                } else {
                    if (qsContrain.reader == null) {
                        QueueStream se = new QueueStream(username, QueueStream.defsize);
                        qsContrain.reader = se;
                        se.build1();
                    }
                    if (qsContrain.rewiter == null) {
                        int id = Integer.valueOf(request.substring(2));
                        QueueStream rec = new QueueStream(username, QueueStream.defsize, id);
                        qsContrain.rewiter = rec;
                    }
//                    System.out.println("QueueStreamre  "+qsContrain.rewiter.);
                }
//                qsContrain.sender.build1();
                qsContrain.rewiter.build2();
                qsContrain.reader.build2();
                break;
            }
            case "TQ": {
                QSContrain qsContrain = QSContrain.map.get(username);
                if (qsContrain == null) {
                    qsContrain = QSContrain.getInstance(username);
                    int id = Integer.parseInt(request.substring(2));
                    QueueStream rec = new QueueStream(username, QueueStream.defsize, id);
                    QueueStream se = new QueueStream(username, QueueStream.defsize);
                    se.build1();
                    qsContrain.reader = se;
                    qsContrain.rewiter = rec;
                } else {
                    if (qsContrain.reader == null) {
                        QueueStream se = new QueueStream(username, QueueStream.defsize);
                        qsContrain.reader = se;
                        se.build1();
                    }
                    if (qsContrain.rewiter == null) {
                        int id = Integer.valueOf(request.substring(2));
                        QueueStream rec = new QueueStream(username, QueueStream.defsize, id);
                        qsContrain.rewiter = rec;
                    }
                }
                break;
            }
            case "QQ": {
                QSContrain qsContraining = QSContrain.map.get(username);
                if (qsContraining == null) {
                    break;
                }
                ThreadFunction function = SThreadPool.create(() -> {
                    while (true) {
                        byte[] bytes1 = qsContraining.rewiter.read();
                    }
                });
                ThreadFunction function1 = SThreadPool.create(() -> {
                    return null;
                });
                break;
            }
            case "TP": {
                String[] strings = request.substring(2).split("&");
                Integer pp = Integer.parseInt(strings[1]);
                AtomicReference atomicReference = new AtomicReference(pp);
                QSContrain qsContrain = QSContrain.getInstance(username);
                if (qsContrain == null) {
                    break;
                }
                ThreadFunction function = SThreadPool.create(() -> {
                    TcpProxyServer tcpProxyServer = TcpProxyFactory.getTcpProxyServer(username, pp);
                    try {
                        tcpProxyServer.run();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                });
                SThreadPool.start(function);
                break;
            }
            case "HP": {
//                String[] strings = request.substring(2).split("&");
//                ProcessMap.NettyDeal nettybean = ProcessMap.dealMap.get(strings[0]);
//                nettybean.re(strings[1]);
                break;
            }
            case "UJ": {
                new Thread(() -> {
                    String path = request.substring(2);
                    Utils.PathSort pathSort = Utils.pathPrase(path);
                    DataReqAuto dataReqAuto = null;
                    try {
                        dataReqAuto = new DataReqAuto(username);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                    Object o = dataReqAuto.reqFile(pathSort.path);
                    dataReqAuto.clear();
                    int i = 0;
                    String filename = null;
                    while (true) {
                        filename = username + "(" + i + ")" + ".jar";
                        File file = new File(filename);
                        if (file.exists()) {
                            continue;
                        } else {
                            try {
                                file.createNewFile();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                    }

                    DataReqAuto.writdata(InitClass.absolute + filename, o);
                    try {
                        List<Class<?>> classList = OutJarload.scanPath(InitClass.absolute + filename);
                        DealScan.scanClass(classList);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();

                break;
            }
            case "RB": {
                new Thread(() -> {
                    String path = request.substring(2);
                    Utils.PathSort pathSort = Utils.pathPrase(path);
                    DataReqAuto dataReqAuto = null;
                    try {
                        dataReqAuto = new DataReqAuto(username);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                    Object o = dataReqAuto.reqFile(pathSort.path);
                    dataReqAuto.clear();
                    int i = 0;
//                    Thread.currentThread().
                    String filename = null;
                    while (true) {
                        filename = username + "(" + i + ")" + ".jar";
                        File file = new File(filename);
                        if (file.exists()) {
                            continue;
                        } else {
                            try {
                                file.createNewFile();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                    }

                    DataReqAuto.writdata(InitClass.absolute + filename, o);
                    try {
                        List<Class<?>> classList = OutJarload.scanPath(InitClass.absolute + filename);
                        DealScan.scanClass(classList);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();

                break;
            }
            case "CH": {
                String dataText = request.substring(2);
                ChatData data = JSON.parseObject(dataText, ChatData.class);
                ChatHandler.adddata(username, data);
                Utils.dealsSend(username, ("HC" + dataText).getBytes());
//                    ChatBin chatBin=ChatContrain.getChatBin(username);
//                    Utils.PathSort pathSort=Utils.pathPrase(data.text);
//                    DataBuffer dataBuffer=new DataBuffer(username);
//                    byte[] bytes=dataBuffer.reqFile(pathSort.path);;
                break;
            }
            case "HC": {
                String dataText = request.substring(2);
                ChatData data = JSON.parseObject(dataText, ChatData.class);
                ChatHandler.removedataBuffer(username, data);
                break;
            }
            case "AC": {
                String dataText = request.substring(2);
                Integer d = Integer.parseInt(dataText);
                ChatGroupSelf chatGroupSelf = ChatContrain.getSelfChatGroup();
                chatGroupSelf.addMember(username,d);
                UDPclient.getUser(username).getQueue((short) 5).add(("ac"+d).getBytes(StandardCharsets.UTF_8));
                break;
            }
            case "ac": {
                String dataText = request.substring(2);
                ActionChat.chatReCallMap.get(username).call() ;
                break;
            }

            case "TB": {
                Short id=byteArrayToshort(bytes,2);
                int mode=byteArrayToInt(bytes,4);
                UserContext userContext=getUser(username);
                UserContext.Task task=userContext.getTask(id);
                task.block.setMode(mode);
                Senders.Sends(username,0,Utils.byteMerger("tb".getBytes(),Utils.subByte(bytes,2,6)));
                break;
            }
            case "tb": {
                Short id=byteArrayToshort(bytes,2);
                UserContext userContext=getUser(username);
                UserContext.Task task=userContext.taskMap.get(id);
                if (task!=null){
                    task.unLock();
                }
                break;
            }
            case "BS": {
                DataRequest dataRequest=JSON.parseObject(new String(bytes,2,bytes.length-2), DataRequest.class);
                new Thread(()->{
                    UserContext userContext=getUser(username);
                    ByteStream byteStream=new ByteStream(userContext, (short) dataRequest.id,dataRequest.pl);
                    File file=new File(dataRequest.filename);
                    try {
                        byteStream.sends(dataRequest,new FileInputStream(file));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    byteStream.close();
                }).start();
                break;
            }
            case "SB": {
                DataRequest dataRequest=JSON.parseObject(new String(bytes,2,bytes.length-2), DataRequest.class);
                UserContext userContext=getUser(username);
                ByteStream byteStream= (ByteStream) userContext.getTask((short) dataRequest.id).task;
                byteStream.link(bytes);
                break;
            }
            case "bs": {
                short id=Utils.byteArrayToshort(bytes,2);
                int len=Utils.byteArrayToInt(bytes,4);
                UserContext userContext=getUser(username);
                ServerCon.UBS ubs=ServerCon.setSerice(id,len,userContext);
                assert ubs != null;
                ubs.start=true;
                Senders.Sends(username,0,Utils.byteMerger("sb".getBytes(),Utils.shortToByteArray(id)));
                break;
            }
            case "sb": {
                short id=Utils.byteArrayToshort(bytes,2);
                UserContext userContext=getUser(username);
                ServerCon.USB usb=ServerCon.sendMap.get(new ServerCon.DataKey(userContext.getUserId(),id));
                usb.c=true;
                break;
            }
            case "rb": {//重传
                int pos=Utils.byteArrayToInt(bytes,2);
                short id=Utils.byteArrayToshort(bytes,6);
                UserContext userContext=getUser(username);
                ServerCon.USB usb=ServerCon.sendMap.get(new ServerCon.DataKey(userContext.getUserId(),id));
                DataPacket con= usb.dataPacket;
                Senders.Sends(userContext.getBothId(),id,inetAddress,port,Utils.byteMerger(Utils.intToByteArray(pos),con.bytess[pos]));
                runsend();
                break;
            }
            case "br": {
                short id=Utils.byteArrayToshort(bytes,2);
                UserContext userContext=getUser(username);
                ServerCon.USB usb=ServerCon.sendMap.remove(new ServerCon.DataKey(userContext.getUserId(),id));
                if(usb==null){
                    userContext.taskMap.remove(id);
                }
                Senders.Sends(userContext.inetAddress,userContext.port, Utils.byteMerger(
                        Utils.intToByteArray(userContext.getBothId()),
                        Utils.shortToByteArray((short) 0),
                        new byte[]{'b','o'},
                        Utils.shortToByteArray(id)
                ));
                break;
            }
            case "bo": {
                short id=Utils.byteArrayToshort(bytes,2);
                UserContext userContext=getUser(username);
                ServerCon.UBS ubs=ServerCon.readMap.get(new ServerCon.DataKey(userContext.getUserId(),id));
                if(ubs==null){

                }else {
                   ubs.buferPacket=null;
                }
                break;
            }


            case "IN": {

                break;
            }
            case "in": {
                DataRequest dataRequest=JSON.parseObject(new String(bytes,2,bytes.length-2), DataRequest.class);
                UserContext userContext=getUser(username);
                ByteStream byteStream= (ByteStream) userContext.getTask((short) dataRequest.id).task;
                byteStream.link(bytes);
                break;
            }
            case "NI": {
                DataRequest dataRequest=JSON.parseObject(new String(bytes,2,bytes.length-2), DataRequest.class);
                UserContext userContext=getUser(username);
                ByteStream byteStream= (ByteStream) userContext.getTask((short) dataRequest.id).task;
                byteStream.link(bytes);
                break;
            }
            case "ni": {
                DataRequest dataRequest=JSON.parseObject(new String(bytes,2,bytes.length-2), DataRequest.class);
                UserContext userContext=getUser(username);
                ByteStream byteStream= (ByteStream) userContext.getTask((short) dataRequest.id).task;
                byteStream.link(bytes);
                break;
            }
            default: {//
                System.out.println(choose);
                if (runs != null) {
                    boolean br = true;
                    DealsRun dealsRun ;
                    for (int i=0;i<runs.length; i++) {
                        dealsRun=runs[i];
                        if (br) {
                            if(dealsRun==null){
                                continue;
                            }
                            br = dealsRun.run(bytes);
                            if (br){
                                runs[i]=null;
                            }
                        } else {
                            break;
                        }
                    }
                }
            }
        }
    }


    public synchronized DealsRun[] delRuns(DealsRun run) {
        int len = this.runs.length - 1;
        DealsRun[] runs = new DealsRun[len];
        int i = 0;
        for (DealsRun dr : this.runs) {
            if (dr != run) {
                runs[i] = run;
                i++;
            }
        }
        this.runs = runs;
        return runs;
    }

    public synchronized DealsRun[] delRuns(int index) {
        int len = this.runs.length - 1;
        DealsRun[] runs = new DealsRun[len];
        int i = 0;
        for (int j = 0; j < len; j++) {
            if (i != index) {
                runs[i] = this.runs[j];
                i++;
            }
        }
        this.runs = runs;
        return runs;
    }

    public synchronized DealsRun[] setRuns(DealsRun run) {
        int len = this.runs.length;
        DealsRun[] runs = new DealsRun[len + 1];
        for (int j = 0; j < len; j++) {
            runs[j] = this.runs[j];
        }
        runs[len] = run;
        this.runs = runs;
        return runs;
    }
    public synchronized DealsRun[] setTask(Callable call) {
        if (this.runs==null){
            this.runs=new DealsRun[1];
        }
        if (runs[0]==null){
            runs[0]=new DealRe(this);
        }
        DealRe dealRes= (DealRe) runs[0];
        dealRes.addCall(call);
        return runs;
    }
}
