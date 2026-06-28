package superlink.udpbind.cloude;

import superlink.filemanage.xmltool.XmlCreate;
import superlink.filemanage.xmltool.XmlParser;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.cloude.operta.broadcast.Operta;

import java.io.*;
import java.util.*;

//public class CloudeListenLocal implements Runnable {
//    public static Map<String,CloudeSynContainer> containerMap=null;
//    public List<CloudBin> Listbin;//=CloudeSynContainer.Listbin;
//    //public List<CloudBin> Listbin=new ArrayList<CloudBin>();
//    public static Object lock=new Object();
//    public static Map<String,List<String>> map=new Hashtable<>();
//    public static Operta operta;
//    public static void init(){
//        //解析userCloudefile为filelist
//        SAXReader reader=new SAXReader();
//        Document document=null;
//        try {
//            document=reader.read(new File(XmlCreate.userCloudefile +".xml"));
//        } catch (DocumentException e) {
//            e.printStackTrace();
//        }
//        Element elementroot=document.getRootElement();
//        String root=elementroot.attributeValue("p");
//        List<Element> elements=elementroot.elements();
//        elements.forEach(element -> {
//            String name=element.attributeValue("f");
//            String dirname=root+"\\\\"+name;
//            Document doc= DocumentHelper.createDocument();
//            File file=new File(dirname);
//            XmlParser.writeXml(doc.getRootElement(),file);
//            try {
//                XMLWriter xmlWriter=new XMLWriter(new FileOutputStream(file));
//                xmlWriter.write(doc);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            List<String> cloudFilePath=new ArrayList<>();
//            XmlParser.parserXml(cloudFilePath,dirname);
//            map.put(dirname,cloudFilePath);
//        });
//
//    }
//
//    public static void sendAll() {
//        int num=0;
//        ArrayList arrayList=new ArrayList();
//        Set<Map.Entry<String,FileTrigger>> entrySet=CloudLocal.getSynContainer().localbin.map.entrySet();
//        Iterator<String> iterator=null;
//        for (Map.Entry<String,FileTrigger> entry:entrySet){
//            FileTrigger fileTrigger=entry.getValue();
//            iterator=fileTrigger.pathlist.iterator();
//            while (iterator.hasNext() && num<256){
//                FileTrigger.TargetFile targetFile=new FileTrigger.TargetFile();
//                targetFile.root =fileTrigger.rootPath;
//                targetFile.target=fileTrigger.targetPath;
//                targetFile.path=iterator.next();
//                targetFile.syb=3;
//                arrayList.add(targetFile);
//            }
//        }
//        CloudeListenCaset.cloudeListenCaset.dataCloud.sendque(arrayList);
//    }
//    @Override
//    public void run() {
//        synchronized (lock){
//            while (true){
//                try {
//                    lock.wait(5*60*1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                SAXReader reader=new SAXReader();
//                Document document=null;
//                try {
//                    document=reader.read(new File(UDPclient.userlocal.username+".xml"));
//                } catch (DocumentException e) {
//                    e.printStackTrace();
//                }
//                Element elementroot=document.getRootElement();
//                String root=elementroot.attributeValue("p");
//                List<Element> elements=elementroot.elements();
//                elements.forEach(element -> {
//                    String name=element.attributeValue("p");
//                    String dirname=root+"/"+name;
//                    SAXReader saxReader=new SAXReader();
//                    Document documentcloud=null;
//                    try {
//                        documentcloud =saxReader.read(new File(dirname));
//                    } catch (DocumentException e) {
//                        e.printStackTrace();
//                    }
//                    String clouddir=documentcloud.getRootElement().attributeValue("p");
//                    Document doc= DocumentHelper.createDocument();
//                    File file=new File(clouddir);
//                    XmlParser.writeXml(doc.getRootElement(),file);
//                    try {
//                        XMLWriter xmlWriter=new XMLWriter(new FileOutputStream(file));
//                        xmlWriter.write(doc);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    List<String> cloudFilePath=new ArrayList<>();
//                    XmlParser.parserXml(cloudFilePath,dirname);
//                    //todo 变更为非静态
//                    CloudeSynContainer synContainer=containerMap.get(clouddir);
//                    for (String path:cloudFilePath){
////                        if (!synContainer.filepathSet.contains(path)){
////                            synContainer.Mapbin.forEach((us,cloudBin) -> {
////                                cloudBin.remind(path);
////                            });
////                        }
//
//                    }
//                    map.put(clouddir,cloudFilePath);
//                });
//            }
//        }
//        }
//
//
//
//}
