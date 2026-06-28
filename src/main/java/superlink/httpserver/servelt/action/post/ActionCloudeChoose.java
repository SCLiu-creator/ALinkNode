package superlink.httpserver.servelt.action.post;

import org.dom4j.Document;
import org.dom4j.Element;
import superlink.filemanage.xmltool.XmlCreate;
import superlink.filemanage.xmltool.XmlParser;
import superlink.httpserver.servelt.action.Action;
import superlink.httpserver.servelt.action.Api;
import superlink.httpserver.servelt.WebController;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.cloude.CloudLocal;
import superlink.udpbind.cloude.show.ShowBin;
import superlink.udpbind.cloude.show.UserShowContainer;

import java.io.File;
import java.util.List;

import static superlink.filemanage.scanpackage.FileScan.createXmls;
import static superlink.filemanage.xmltool.XmlCreate.createcloudeXml;

@WebController(name = "CloudeChoose")
public class ActionCloudeChoose implements Action {

    @Api(def = "rightChoose")
    public void postChooseFiles(List<String> list){
        for (String s:list){
            createXmls(s);
        }
        createcloudeXml();
        if (CloudLocal.isInitSynContainer()){
            CloudLocal.getSynContainer().reloadLocalBin();
        }
    }

    @Api(def = "selectFile")
    public void postChooseFile(List<String> list){
        Element element=XmlCreate.addUserXml(list);
        ShowBin showBin= UserShowContainer.showBinMap.get(UDPclient.userlocal.username);
        if (showBin != null) {
            showBin.documentfile=element.getDocument();
        }
    }
//    @Api(def = "getSelectFile")
//    public void getChooseFile(List<String> list){
//        String name= showpath+ UDPclient.userlocal.username+".xml";
//        File file=new File(name);
//        Document document;
//        long l=System.currentTimeMillis();
//        if (file.exists()){
//            document=XmlParser.openXml(file.getAbsolutePath());
//        }else {
//            return;
//        }
//        Element rootElement=document.getRootElement();
//    }
}
