package superlink.udpbind.cloude;

import superlink.filemanage.xmltool.XmlCreate;
import superlink.filemanage.xmltool.XmlParser;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import superlink.udpbind.client.UserContext;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class CloudeSynContainer {
    public Map<String,CloudBin> Mapbin=new ConcurrentHashMap<>();
    public CloudBin localbin;
    //弃用
    public static List<String> synlist=new ArrayList<>();
    public CloudeSynContainer(){
        synchronized (this){
            localbin=new CloudBin();
        }
    }

    static {
        SAXReader reader=new SAXReader();
        try {
            File file=new File("synList"+".xml");
            if (!file.exists()){
                Document document= DocumentHelper.createDocument();
                document.addElement("list");//添加根节点
                try {
                    FileOutputStream fileOutputStream=new FileOutputStream(file);
                    XMLWriter xmlWriter=new XMLWriter(fileOutputStream);
                    xmlWriter.write(document);
                    xmlWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Document document=reader.read(file);
            document.getRootElement().elements().forEach(e->{
                synlist.add(((Element)e).attribute(0).getValue());
            });
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    public void saveLocalBin(){
        localbin.map.forEach((f,t)->{
            try {
                t.save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void reloadLocalBin(){
        localbin.reload();
    }
    //弃用
    public void addCloudBin(UserContext userContext,File filename){
        SAXReader saxReader=new SAXReader();
        Document document = null;
        try {
             document=saxReader.read(filename);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        Element rootElement=document.getRootElement();
        List<String> strings=XmlParser.parserXml((Element) rootElement.elements().get(0));
        CloudBin cloudBin=new CloudBin(userContext);
    }

    public void synUserListAdd(String user){
        try {
            File file=new File("synList"+".xml");
            SAXReader reader=new SAXReader();
            Document document=reader.read(file);
            Element element=document.addElement("user");
            element.addAttribute("user",user);
            FileOutputStream fileOutputStream=new FileOutputStream(file);
            XMLWriter writer=new XMLWriter(fileOutputStream);
            writer.write(document);
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
        synlist.add(user);
    }

    public void synUserListRemove(String user){
        try {
            File file=new File("synList"+".xml");
            SAXReader reader=new SAXReader();
            Document document=reader.read(file);
            List<Element> elements=document.getRootElement().elements();
            elements.forEach(e -> {
                if (user.equals(e.attributeValue("user"))){
                    e.getParent().remove(e);
                }
            });
            FileOutputStream fileOutputStream=new FileOutputStream(file);
            XMLWriter writer=new XMLWriter(fileOutputStream);
            writer.write(document);
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
        synlist.remove(user);
    }

    public void remove(CloudBin cloudBin){
        Mapbin.remove(cloudBin);
    }

    /*根据相对路径移除*/
    public void staticRemoveNode(Document document,String filepath,String file){
        try {
            String[] strings=filepath.split("\\\\");
            AtomicReference<Element> e= new AtomicReference<>(document.getRootElement());
            AtomicReference<List<Element>> elements= new AtomicReference<>();
            for (String s:strings){
                elements.set(e.get().elements());
                elements.get().forEach(l->{
                    if (s.equals(l.attribute(0).getValue())){
                        e.set(l);
                        elements.set(l.elements());
                    }
                });
            }
            e.get().getParent().elements().remove(e);

            XMLWriter  writer=new XMLWriter(new FileWriter(file));
            writer.write(document);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*根据相对路径移除*/
    public void staticAddNode(Document document,String filepath,String file){
        try {
            String[] strings=filepath.split("\\\\");
            AtomicReference<Element> e= new AtomicReference<>(document.getRootElement());
            AtomicReference<List<Element>> elements= new AtomicReference<>();
            AtomicInteger i=new AtomicInteger(0);
            for (String s:strings){
                elements.set(e.get().elements());
                elements.get().forEach(l->{
                    if (s.equals(l.attribute(0).getValue())){

                        e.set(l);
                        elements.set(l.elements());
                        i.getAndIncrement();
                    }
                });
            }
            e.get().getParent().elements().remove(e);

            XMLWriter  writer=new XMLWriter(new FileWriter(file));
            writer.write(document);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showRemoveNode(String filepath){
        //todo
        try {
            SAXReader reader=new SAXReader();
            Document document=reader.read(new File(XmlCreate.userShow+".xml"));
            List<Element> e1= document.getRootElement().elements();
            Element buf=null;
            for (Element element:e1){
                if (filepath.equals(element.attribute(0).getValue())){
                    buf=element;
                }
            }

            buf.getParent().remove(buf);
            XMLWriter  writer=new XMLWriter(new FileWriter("data\\userpage.xml"));
            writer.write(document);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void removenode(Element element){
        //todo
        try {
            Element root= element.getParent();
            root.remove(element);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*根据相对路径移除*/
    public static void addNode(Document document,String filepath,String file){
        try {
            String[] strings=filepath.split("\\\\");
            AtomicReference<Element> e= new AtomicReference<>(document.getRootElement());
            AtomicReference<List<Element>> elements= new AtomicReference<>();
            for (String s:strings){
                elements.set(e.get().elements());
                elements.get().forEach(l->{
                    if (s.equals(l.attribute(0).getValue())){
                        e.set(l);
                        elements.set(l.elements());
                    }
                });
            }

            Element element=e.get().addElement("f");
            element.addAttribute("f",file);
            XMLWriter  writer=new XMLWriter(new FileWriter(file));
            writer.write(document);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void finalize(){
        try {
            //CloudLocal.synContainer=null;
            CloudLocal.clearSynContainer();
            if (CloudeListenCaset.cloudeListenCaset!=null){
                CloudeListenCaset.cloudeListenCaset.getFileRunner().manualStop();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.gc();
    }
}
