package superlink.httpserver.servelt.action.get;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandlerContext;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import superlink.filemanage.xmltool.XmlParser;
import superlink.httpserver.servelt.WebController;
import superlink.httpserver.servelt.action.Api;
import superlink.httpserver.servelt.action.GetParm;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.recives.datalen.AutoBuffer;
import superlink.udpbind.cloude.show.ShowBin;
import superlink.udpbind.cloude.show.UserShowContainer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static superlink.filemanage.xmltool.XmlParser.SaveXml;
import static superlink.filemanage.xmltool.XmlParser.showpath;

@WebController(name = "editShow")
public class ActionEditShow {

    @Api(def = "get")
    public String getShow(){
        ShowBin showBin=UserShowContainer.getLocalShowBin();
        Element element=showBin.documentfile.getRootElement();

        List<Element> elements=element.elements();
        List list1= showBin.getListMap(element,elements,0,600);
        return JSON.toJSONString(list1);
    }

    @Api(def = "getShowPath")
    public String getPathShow( @GetParm Map<String,Object> map) throws Exception {
        String s= null;
        List<String> list= (List<String>) map.get("p");
        ShowBin showBin=UserShowContainer.getLocalShowBin();
        AtomicInteger integer=new AtomicInteger(list.size());
        Element element=showBin.getPathElement(list,integer);
        List<Element> elements=element.elements();
        List list1=showBin.getListMap(element,elements,0,600);
        return JSON.toJSONString(list1);
    }

    @Api(def = "delShowPath")
    public String delPathShow(Map<String,List<String>> map) throws Exception {
        List<String> listp=  map.get("prex");
        List<String> list= map.get("p");
        ShowBin showBin=UserShowContainer.getLocalShowBin();
        AtomicInteger integer=new AtomicInteger(listp.size());
        Element element=showBin.getPathElement(listp,integer);
        List<File> files=new ArrayList<>(list.size());
        for (String s:list){
            files.add(new File(s));
        }
        List<Element> elements=element.elements();
        boolean b=false;
        for (Element ele:elements){
            File file=new File(ele.attribute(0).getValue());
            for (int i = 0; i < files.size(); i++){
                File f=files.get(i);
                if (file.equals(f)){
                    b=element.remove(ele);
                }
            }
            if (b==true){
                break;
            }
        }
        elements=element.elements();
        XmlParser.SaveXml(showBin.documentfile,showpath+ UDPclient.userlocal.username+".xml");
        List list1=showBin.getListMap(element,elements,0,600);
        return JSON.toJSONString(list1);
    }
}
