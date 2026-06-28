package superlink.httpserver.servelt.action.post;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import superlink.filemanage.xmltool.UserGet;
import superlink.httpserver.servelt.action.Action;
import superlink.httpserver.servelt.action.Api;
import superlink.httpserver.servelt.action.GetParm;
import superlink.httpserver.servelt.WebController;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.cloude.CloudLocal;
import superlink.udpbind.cloude.CloudeListenCaset;
import superlink.udpbind.cloude.operta.Browse;
import superlink.udpbind.cloude.operta.Consist;
import superlink.udpbind.cloude.operta.Monitor;
import superlink.udpbind.cloude.operta.Server;
import superlink.udpbind.cloude.operta.broadcast.Operta;
import superlink.udpbind.cloude.util.TendFactory;
import superlink.udpbind.cloude.util.TendMap;
import superlink.udpbind.controller.Controller;
import superlink.udpbind.usedata.User;
import superlink.udpbind.usedata.UserRequest;
import superlink.util.Tool;

import java.util.List;
import java.util.Map;

@WebController(name = "Cloude")
public class ActionCloudepage implements Action {

    @Api(def = "requestNode")
    public void requestNode(ChannelHandlerContext ctx, FullHttpRequest msg){
        String[] s=msg.uri().split("/");
        String s1=s[s.length-1];
        UserRequest userRequest= Controller.chooseUserRequest(s1);
        Controller.requestNode(userRequest);
        ByteBuf buf = msg.content();
        System.out.print(buf.toString(CharsetUtil.UTF_8));
        ByteBuf byteBuf = Unpooled.copiedBuffer("Send Over", CharsetUtil.UTF_8);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
        response.headers().add(HttpHeaderNames.CONTENT_TYPE, "text/plain");
        response.headers().add(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
        ctx.writeAndFlush(response);
    }
    @Api(def = "TendMap")
    public void postTendMAp(List<Map<String,List<String>>> mapList, @GetParm String username){
        TendMap tendMap=TendMap.toTendMap(mapList);
        tendMap.name=username;
        TendFactory.setTmXml(tendMap);
    }


    @Api(def = "getCloudeMode")
    public String getCloudeMode(){
        CloudeListenCaset cloudeListenCaset=CloudeListenCaset.FactortCloudeLisentCaset();;
        Operta operta= cloudeListenCaset.operta;
        String s="off";
        if (operta instanceof Monitor){
            s="Monitor";
        } else if (operta instanceof Consist){
            s="Consist";
        } else if (operta instanceof Server){
            s="Server";
        } else if (operta instanceof Browse){
            s="Browse";
        }
        if("off".equals(s)&&operta==null){
            if(CloudLocal.getSynContainer().Mapbin.size()>0){
                cloudeListenCaset.setMode(0);
                s="Browse";
            }
        }
        return s;
    }

    @Api(def = "changeMode")
    public String changeMode(@GetParm String mode){

        int m;
        if ("Monitor".equals(mode)){
            m=3;
        } else if ("Consist".equals(mode)){
            m=1;
        } else if ("Server".equals(mode)){
            m=2;
        } else if ("Browse".equals(mode)){
            m=0;
        }else {
            m=-1;
        }
        m++;
        if (m>3){m=-1;}
        CloudeListenCaset cloudeListenCaset=CloudeListenCaset.FactortCloudeLisentCaset();
        cloudeListenCaset.setMode(m);
        UserGet.setCloudeMode(m);
        Operta operta= CloudeListenCaset.cloudeListenCaset.operta;
        String s="off";
        if (operta instanceof Monitor){
            s="Monitor";
        } else if (operta instanceof Consist){
            s="Consist";
        } else if (operta instanceof Server){
            s="Server";
        } else if (operta instanceof Browse){
            s="Browse";
        }
        return s;
    }

    @Api(def = "setMon")
    public void setMon(Map<String,String> map){
        String fileNum = map.get("fn");
        String fileSize = map.get("fz");
    }

    @Api(def = "startCloud")
    public void startCloud(Integer integer,Long l){
        CloudLocal.init(l);
        CloudeListenCaset.cloudeListenCaset.setMode(integer);
    }
    @Api(def = "closeCloude")
    public void closeCloude(Integer integer){
        CloudeListenCaset.cloudeListenCaset.immediate();
    }

    @Api(def = "reqCloude")
    public void reqCloude(FullHttpRequest msg){
        String[] strings=msg.uri().split("\\?");
        String s=strings[1];
        if (UDPclient.userlocal.username.equals(s)){
            return;
        }
        User user = UDPclient.userMap.get(s);
        if (user==null){
            user=UDPclient.bindUser.get(s);
        }

        UserRequest userRequest= Tool.UsertoUserRequestbind(user);
        Controller.ReqCloudePage(userRequest,true);
    }

    @Api(def = "reqCloudeMirror")
    public void ReqCloudeMirror(FullHttpRequest msg){
        String[] strings=msg.uri().split("\\?");
        String s=strings[1];
        User user = UDPclient.userMap.get(s);
        UserRequest userRequest= Tool.UsertoUserRequestbind(user);
        Controller.ReqCloudePageMirror(userRequest);
    }
}
