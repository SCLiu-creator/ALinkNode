package superlink.httpserver.servelt.action.url;

import com.alibaba.fastjson2.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import superlink.filemanage.classprocess.property.ReInfuse;
import superlink.filemanage.classprocess.property.reInject;
import superlink.filemanage.xmltool.XmlCreate;
import superlink.httpserver.servelt.WebController;
import superlink.httpserver.servelt.action.Api;
import superlink.httpserver.servelt.action.GetParm;
import superlink.httpserver.servelt.action.WebPath;
import superlink.httpserver.servelt.action.service.ServiceTest;
import superlink.httpserver.servelt.httptype.ContentType;
import superlink.init.InitClass;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.Senders;
import superlink.udpbind.client.recives.data.datastream.DataStreamAB;
import superlink.udpbind.client.recives.datalen.DataSyn;
import superlink.udpbind.client.recives.datalen.DsCon;
import superlink.udpbind.handle.handler.ReqRemoteHandle;
import superlink.udpbind.handle.handler.ReqRemoteSynHandle;
import superlink.udpbind.remote.invoking.LinkCallTemplate;
import superlink.util.Utils;
import superlink.util.asynhandle.AsynHandle;
import superlink.util.asynhandle.LocalHandle;

import java.io.*;
import java.net.DatagramPacket;
import java.util.Arrays;
import java.util.Map;

@WebController()
@ReInfuse(name = "ActionTest",grade = "b")
public class ActionTest1 {
    //http://192.168.0.141:6049/map/DStest?HJ06RpJMEdCmQJp2
    @Api(def = "ReInfuse")
    public void ReInfuse(@GetParm  String user) {
        System.out.println("2");
    }
    //http://192.168.0.141:6049/map/DStest?HJ06RpJMEdCmQJp2
    @Api(def = "DStest")
    public void DStest(@GetParm  String user){
        UserContext userContext= UDPclient.getUser(user);
        short id=userContext.newQueue();
        DataStreamAB dataStreamAB = new DataStreamAB(user,id);
        boolean sc= dataStreamAB.build();
        if (!sc){
            return;
        }
        File file = new File(XmlCreate.userShow+"/test.jpg");
        File file1 = new File(XmlCreate.userShow.replace(UDPclient.userlocal.username,user)+"/headPic");
        try(FileOutputStream fos = new FileOutputStream(file);FileInputStream fos1 = new FileInputStream(file1)) {
            byte[] bytes = null;
            while ((bytes=dataStreamAB.read0()).length!=4){
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
        } catch (IOException e) {
            System.out.println("not find File");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        file = new File(XmlCreate.userShow+"/"+"headPic");
        try (FileInputStream inputStream = new FileInputStream(file)){
            byte[] bytes = new byte[1048];
            int i=-1;
            int p=0;
            while ((i = inputStream.read(bytes))>0){
                byte[] data = Utils.subByte(bytes,0,i);
                data = Utils.byteMerger(Utils.intToByteArray(p),data);
                dataStreamAB.write( data);
                p++;
            }
            dataStreamAB.write(Utils.byteMerger(Utils.byteMerger(Utils.intToByteArray(0),new byte[0])));
        } catch (IOException e) {
            System.out.println("not find File");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }

    @reInject(name = "ActionTest")
    ActionTest1 actionTest;

    @reInject(name = "rj")
    ServiceTest serviceTest;
    @reInject(name = "rj")
    public void setServiceTest(ServiceTest serviceTest) {
        this.serviceTest = serviceTest;
    }

    @WebPath(name = "getuser/{name}")
    public String geturl(String  context){
        String s=serviceTest.a();
        return s;
    }

    @Api(def = "demo/synData")
    public void getData(String user,String file){
        UserContext userContext= UDPclient.getUser(user);
        short id=userContext.newQueue();
        DataSyn dataSyn = DsCon.getInstance(userContext,id);
        dataSyn.reqFile(file);
    }

    @Api(def = "demo/addSee")
    public void remoteCall(String user){
        Senders.cheak=(p)->{
            DatagramPacket datagramPacket=(DatagramPacket)p[0];
            byte[] bytes=datagramPacket.getData();
            String s=new String(bytes,0,datagramPacket.getLength());
            if (s.contains(user)){
                System.out.println("cheakFind"+s);
            }
            return null;
        };
    }

    @Api(def = "demo/remoteCallTest")
    public Object remoteCallt(String user,Object... para){
        ReqRemoteSynHandle remoteHandle=new ReqRemoteSynHandle(user,"",para);
        remoteHandle.addWork((para1)->{
            if (para1 == null) {
                System.out.println("para is null");
            } else if (para1.length == 0) {
                System.out.println("para is an empty array");
            } else {
                System.out.println("para contains elements");
            }
            return para1;
        }).addWork((para1)->{
            System.out.println(para1.getClass());
            return para1;
        }).addFinally((para1)->{
            System.out.println(para1.getClass());
            return JSON.toJSONString(para1);
        });
        remoteHandle.process();
        return remoteHandle.getValue();
    }

    @Api(def = "demo/remoteCall")
    public Object remoteCall(String user,Object... para){
        ReqRemoteHandle remoteHandle=new ReqRemoteHandle(user,"",para);
        remoteHandle.addWork((para1)->{
            if (para1 == null) {
                System.out.println("para is null");
            } else if (para1.length == 0) {
                System.out.println("para is an empty array");
            } else {
                System.out.println("para contains elements");
            }
            return para1;
        }).addWork((para1)->{
            System.out.println(para1.getClass());
            return para1;
        }).addFinally((para1)->{
            System.out.println(para1.getClass());
            return JSON.toJSONString(para1);
        });
        remoteHandle.process();
        return remoteHandle.getValue();
    }

    @Api(def = "demo/loaclCall")
    public Object loaclCall(Object... para){
        LocalHandle remoteHandle=new LocalHandle(para);
        remoteHandle.addWork((para1)->{
            if (para1 == null) {
                System.out.println("para is null");
            } else if (para1.length == 0) {
                System.out.println("para is an empty array");
            } else {
                System.out.println("para contains elements");
            }
            return para1;
        }).addWork((para1)->{
            System.out.println(para1.getClass());
            return para1;
        }).addFinally((para1)->{
            System.out.println(para1.getClass());
            return JSON.toJSONString(para1);
        });
        remoteHandle.process();
        return remoteHandle.getValue();
    }
    @Api(def = "demo/vall")
    public Object vall(Object... para){
        AsynHandle remoteHandle= AsynHandle.getHandle();;
        remoteHandle.setObj(para);
        remoteHandle.addWork((para1)->{
            if (para1 == null) {
                System.out.println("para is null");
            } else if (para1.length == 0) {
                System.out.println("para is an empty array");
            } else {
                System.out.println("para contains elements");
            }

            return para1;
        }).addWork((para1)->{
            System.out.println(para1.getClass());
            return para1;
        }).addFinally((para1)->{
            System.out.println(para1.getClass());
            return JSON.toJSONString(para1);
        });

        return null;
    }

    @Api(def = "getUI")
    public String getUI(@GetParm String a){
        LinkCallTemplate linkCallTemplate=new LinkCallTemplate(UDPclient.userlocal.username,a);
        linkCallTemplate.para=a;
        linkCallTemplate.req("Linkserver.getUI");
        File file=new File(InitClass.webpath);
        file=new File(file.getParent());
        file=new File(file.getParent());
        return a+a;
    }

    @Api(def = "testrc")
    public String rc(@GetParm String a){
        LinkCallTemplate linkCallTemplate=new LinkCallTemplate(UDPclient.userlocal.username,a);
        linkCallTemplate.para=a;
        for (int i = 0; i < 50; i++) {
            linkCallTemplate.para=linkCallTemplate.para+a;
        }
        byte[] bytes=linkCallTemplate.req("testLinkserver.test");
        return a+a;
    }
//    http://127.0.0.1:50016/map/testrci?{%22user%22:%22YmRcLeWBr9EOTzsu%22,%22path%22:%22C:/Users/liusc/Desktop/1735396534777.png%22}
    @Api(def = "testrci")
    public String rci(@GetParm Map map,ChannelHandlerContext ctx, FullHttpRequest request){
        String user= (String) map.get("user");
        String path= (String) map.get("path");
        LinkCallTemplate linkCallTemplate=new LinkCallTemplate(UDPclient.userlocal.username,user);
        linkCallTemplate.para=path;
        byte[] bytes =linkCallTemplate.req("testLinkserver.testrci");

        ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.writeBytes(bytes);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
        response.headers().add(HttpHeaderNames.CONTENT_TYPE, ContentType.safeValueOf(path.split("\\.")[1]));//"text/html;charset=utf-8"
        response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());

        ctx.writeAndFlush(response);
        return null;
    }

//    @Api(def = "demo/linkCallTest")
//    public Object linksCallt(String user,Object... para){
//        UserContext userContext=UDPclient.getUser(user);
//        int readid=userContext.newQueue();
//        int sendid=userContext.newQueue();
//
//        LinkCallTemplate template=new LinkCallTemplate(user,readid,sendid);
//        template.
//        template.addWork((para1)->{
//            if (para1 == null) {
//                System.out.println("para is null");
//            } else if (para1.length == 0) {
//                System.out.println("para is an empty array");
//            } else {
//                System.out.println("para contains elements");
//            }
//            return para1;
//        }).addWork((para1)->{
//            System.out.println(para1.getClass());
//            return para1;
//        }).addFinally((para1)->{
//            System.out.println(para1.getClass());
//            return JSON.toJSONString(para1);
//        });
//        template.process();
//        return template.getValue();
//    }
    @Api(def = "get/data")
    public void getData(ChannelHandlerContext ctx, FullHttpRequest req) throws IOException {
        String rd=req.headers().get("Range");
        int p1=rd.lastIndexOf("=");
        rd=rd.substring(p1+1);
        int p=rd.lastIndexOf("-");
        String d0=rd.substring(0,p);
        String d1=rd.substring(p+1);
        String pat= InitClass.absolute+"web/"+"van\\custom-video-player\\videos\\gone.mp4";

        FullHttpResponse response=null;
        if (d1 == null || "".equals(d1)) {
            File f=new File(pat);
            BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(f));
            byte[] bytes = new byte[1024000];
            ByteBuf byteBuf = Unpooled.buffer();
            int i;
            inputStream.skip(Long.parseLong(d0));
            byte[] b = new byte[0];
//            while ((i = inputStream.read(bytes)) != -1) {
//                b = Utils.subByte(bytes, 0, i);
//                byteBuf.writeBytes(b);
//            }
            i = inputStream.read(bytes);
            b = Utils.subByte(bytes, 0, i);
            byteBuf.writeBytes(b);
//                            i=inputStream.read(bytes);
//                            byte[] b= Utils.subByte(bytes,0,i);
//                            byteBuf.writeBytes(b);
            // String range="bytes "+bytestart+"-"+f.length();
            String range = "bytes " + d0 + "-" + (byteBuf.readableBytes() + Integer.valueOf(d0)-1) + "/" + f.length();
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.PARTIAL_CONTENT, byteBuf);
            response.headers().add(HttpHeaderNames.CONTENT_TYPE, ContentType.valueOf("mp4").Type());
            response.headers().add(HttpHeaderNames.CONTENT_RANGE, range);//"text/html;charset=utf-8"
            response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
        } else {
            Integer rangestart = Integer.valueOf(d0);
            Integer rangeend = Integer.valueOf(d1);
            BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(new File(pat)));
            byte[] bytes = new byte[rangeend - rangestart];
            ByteBuf byteBuf = Unpooled.buffer();
            int i;
            inputStream.skip(rangestart);
//                            while ((i=inputStream.read(bytes)) != -1){
//                                byte[] b= Utils.subByte(bytes,0,i);
//                                byteBuf.writeBytes(b);
//                            }
            i = inputStream.read(bytes);
            byte[] b = Utils.subByte(bytes, 0, i);
            byteBuf.writeBytes(b);

            // String range="bytes "+bytestart+"-"+f.length();
//            String range = "bytes " + bytestart + "-" + (rangeend - rangestart);
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
            response.headers().add(HttpHeaderNames.CONTENT_TYPE, ContentType.valueOf("mp4").Type());
//            response.headers().add(HttpHeaderNames.CONTENT_RANGE, range);//"text/html;charset=utf-8"
            response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());

        }
        ctx.writeAndFlush(response);

//        response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
//        response.headers().add(HttpHeaderNames.CONTENT_TYPE, ContentType.valueOf("mp4").Type());//"text/html;charset=utf-8"
//        response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
    }
}
