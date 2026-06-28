package superlink.udpbind.cloude;

import org.dom4j.Document;
import org.dom4j.Element;
import superlink.filemanage.xmltool.XmlParser;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.util.Utils;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import static superlink.filemanage.xmltool.XmlParser.byetToDocument;


public class CloudPage {
    public Document document;
    public String username;
    public String rootPath;//含target
    //该对象处理名称
    public String targetPath;
    public int id;
    public UserContext userContext;
    public BlockingQueue<byte[]> blockingQueue;
    public List<String> list;

    public CloudPage(Document document,UserContext userContext){
        this.userContext=userContext;
        this.document=document;
        this.username=userContext.userName;
        Element elementroot=document.getRootElement();
        rootPath=elementroot.attribute(0).getValue().replace("\\","/")+ '/';
        Element element= (Element) elementroot.elements().get(0);
        targetPath=element.attribute(0).getValue();
        this.list =XmlParser.parserXmlUnTarget(element);

    }
    public CloudPage(String name,byte[] bytes) throws Exception {
        this(byetToDocument(bytes),UDPclient.mainDataQueue.getUserContext(name));
    }
    public CloudPage reSetCloudPage(CloudPage cloudPage){
        this.userContext=cloudPage.userContext;
        this.document=cloudPage.document;
        this.username=cloudPage.username;
        Element element=(Element)document.getRootElement().elements().get(0);
        this.list =XmlParser.parserXmlUnTarget(element);
        return this;
    }

    public List<String> getRootList(){
        List<String> rootList=new ArrayList<>();
        StringBuilder stringBuilder=new StringBuilder();
        for (String s:this.list){
            stringBuilder.append(stringBuilder.append(rootPath).append(targetPath).append('/').append(s));
            rootList.add(stringBuilder.toString());
            stringBuilder.setLength(0);
        }
        return rootList;
    }
    public List<String> getTagetList(){
        List<String> rootList=new ArrayList<>();
        StringBuilder stringBuilder=new StringBuilder();
        for (String s:this.list){
            stringBuilder.append(stringBuilder.append(targetPath).append('/').append(s));
            rootList.add(stringBuilder.toString());
            stringBuilder.setLength(0);
        }
        return rootList;
    }

    public CloudPage setPath(String pathName){
        File file=new File(pathName);
        rootPath=pathName;
        targetPath=file.getName();
        return this;
    }

    public CloudPage setDoc(Document doc){
        document=doc;
        return this;
    }

    /*根据绝对路径移除*/
    public void removeNode(String absolutepath){
        String filepath=absolutepath.replace(rootPath,"");
        try {
            String[] strings=filepath.split("/");
            Element root=document.getRootElement();
            Element element= root;
            List<Element> elements= null;
            for (String s:strings){
                elements=element.elements();
                for(Element l:elements){
                    if (s.equals(l.attribute(0).getValue())){
                        element=l;
//                        elements=l.elements();
                        break;
                    }
                };
            }
            if (!element.attribute(0).getValue().equals(strings[strings.length-1])){
                return;
            }
            Element parent=element.getParent();
            if (parent.attribute(0).getValue().equals(strings[strings.length-2])){//targetpath
                parent.remove(element);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*根据绝对路径*/
    public void addNode(String absolutepath){
        String filepath=absolutepath.replace(rootPath,"");
        try {
            String[] strings=filepath.split("/");
            Element root=document.getRootElement();
            Element element= root;
            List<Element> elements= null;
            int i=0;
            for (String s:strings){
                elements=element.elements();
                for(Element l:elements){
                    if (s.equals(l.attribute(0).getValue())){
                        element=l;
                        i++;
                        break;
                    }
                };
            }
            while (i<strings.length-1){
                element=element.addElement("p");
                element.addAttribute("p",strings[i]);
                i++;
            }

            element.addElement("f").addAttribute("f",strings[i]);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void addFile(String dir){

    }

    public void add(String dir,byte[] bytes){
        String[] strings= dir.split("/");//"\\\\"
        Element element=document.getRootElement();
        for (String s:strings){
            List<Element> list=element.elements();
            for (Element e:list){
                if (s.equals(e.attributeValue("p"))){
                    element=e;
                    break;
                }else if(s.equals(e.attributeValue("f"))){
                    try {
                        Utils.toFile(bytes,dir);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }

    public void setId(){

    }

    public FileTrigger.TargetFile getTargetFile(){
        FileTrigger.TargetFile targetFile=new FileTrigger.TargetFile();
        targetFile.target=targetPath;
        targetFile.root =rootPath;
        return targetFile;
    }
}
