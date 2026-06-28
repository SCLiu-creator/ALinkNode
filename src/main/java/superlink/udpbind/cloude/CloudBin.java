package superlink.udpbind.cloude;

import com.alibaba.fastjson2.JSONObject;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import superlink.filemanage.xmltool.XmlCreate;
import superlink.filemanage.xmltool.XmlParser;
import superlink.init.Initor;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.fileListen.FileListen;
import superlink.util.SHAutils;
import superlink.util.Tool;
import superlink.util.Utils;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class CloudBin {
    //存储本机cloude
//    public volatile IdentityHashMap<String, FileTrigger> map=new IdentityHashMap<>();
    public volatile ConcurrentHashMap<String, FileTrigger> map=new ConcurrentHashMap<>();
    //存储其他主机cloude
    public Map<File, CloudPage> synMap=new Hashtable<File, CloudPage>();
    //该对象处理名称
    public int id;
    public UserContext userContext=null;
    public BlockingQueue<byte[]> blockingQueue;


    public CloudBin(UserContext userContext){
        String AbsolutePath;
        this.userContext=userContext;
    }
    public CloudBin(){
        SAXReader reader=new SAXReader();
        Document document=null;

        try {
            document=reader.read(new File(XmlCreate.userCloudefile +".xml"));
        } catch (Exception e) {
            XmlCreate.createcloudeXml();
            e.printStackTrace();
            System.out.println(Thread.currentThread().getName());
            return;
        }
        Element elementroot=document.getRootElement();
        String root=elementroot.attributeValue("p");
        String time=elementroot.attributeValue("t");
        List<Element> elements=elementroot.elements();
//        IdentityHashMap<String, FileTrigger> map=new IdentityHashMap();
        ConcurrentHashMap<String, FileTrigger> map=new ConcurrentHashMap();
        elements.forEach(element -> {
            String name=element.attributeValue("f");
            String dirname=root+"/"+name;
            Set<String> stringSet=null;
            if (!new File(dirname).exists()){
                elementroot.remove(element);
                XmlParser.SaveXml(elementroot.getDocument(),XmlCreate.userCloudefile +".xml");
                return;
            }
            FileTrigger fileTrigger =new FileTrigger(dirname);
            fileTrigger.setBin(this);
            map.put(fileTrigger.AbsolutePath, fileTrigger);
            if (Initor.sleepState){
                //默认关闭
                stringSet=XmlParser.parserXmlSet(dirname);
                sleepProgram(fileTrigger,time,stringSet);
            }
            //本地监听
            if (CloudeListenCaset.cloudeListenCaset==null){

            }else {
                FileListen fileRunner=CloudeListenCaset.cloudeListenCaset.getFileRunner();
                if (fileRunner !=null ){
                    if (fileRunner.isRun()){
                        fileRunner.addListenDirRuning(fileTrigger);
                    }else {
                        fileRunner.addListenDirStop(fileTrigger);
                    }
                }
            }

        });
        this.map=map;
    }

    public void reload(){
        SAXReader reader=new SAXReader();
        Document document=null;
        try {
            document=reader.read(new File(XmlCreate.userCloudefile +".xml"));
        } catch (DocumentException e) {
            e.printStackTrace();
            System.out.println(Thread.currentThread().getName());
            return;
        }
        Element elementroot=document.getRootElement();
        String root=elementroot.attributeValue("p");
        String time=elementroot.attributeValue("t");
        List<Element> elements=elementroot.elements();
//        IdentityHashMap<String, FileTrigger> map=new IdentityHashMap();
        ConcurrentHashMap<String, FileTrigger> map=new ConcurrentHashMap();
        elements.forEach(element -> {
            String name=element.attributeValue("f");
            String dirname=root+"/"+name;
            Set<String> stringSet=null;
            FileTrigger fileTrigger =new FileTrigger(dirname);
            fileTrigger.setBin(this);
            map.put(fileTrigger.AbsolutePath, fileTrigger);
            if (Initor.sleepState){
                //默认关闭
                stringSet=XmlParser.parserXmlSet(dirname);
                sleepProgram(fileTrigger,time,stringSet);
            }
            //本地监听
            if (CloudeListenCaset.cloudeListenCaset==null){

            }else {
                FileListen fileRunner=CloudeListenCaset.cloudeListenCaset.getFileRunner();
                if (fileRunner !=null ){
                    if (fileRunner.isRun()){
                        fileRunner.addListenDirRuning(fileTrigger);
                    }else {
                        fileRunner.addListenDirStop(fileTrigger);
                    }
                }
            }
        });
        this.map=map;
    }


    @Override
    public boolean equals(Object o){
        try {
            return userContext.userName.equals(((CloudBin)o).userContext.userName);
        }catch (NullPointerException n){
            return false;
        }
    }

    public boolean updataSate=false;

    public void sleepProgram(FileTrigger fileTrigger,String time,Set<String> stringSet){
        if (!Initor.sleepState){
            return;
        }

        if (time==null || "".equals(time)){return;}
        Long t=Long.valueOf(time);
        for (String filename:fileTrigger.pathlist){
            File file=new File(fileTrigger.rootPath+filename);
            Path path= Paths.get(file.getPath());
            BasicFileAttributeView basicView=Files.getFileAttributeView(path, BasicFileAttributeView.class);
            long t1;
            long t2;
            try {
                t1= basicView.readAttributes().creationTime().toMillis();
                t2= basicView.readAttributes().lastModifiedTime().toMillis();
            } catch (IOException e) {
                t1=0;
                t2=0;
                e.printStackTrace();
            }
            if (t1>t){
                FileTrigger.TargetFile targetFile=new FileTrigger.TargetFile();
                targetFile.root =fileTrigger.rootPath;
                targetFile.target=fileTrigger.targetPath;
                targetFile.path=filename;
                targetFile.hash= SHAutils.getShaFromFile(targetFile.root +filename,SHAutils.MD_5,false);
                targetFile.syb=1;
                targetFile.len=new File(targetFile.root +filename).length();
                fileTrigger.addque.add(targetFile);

            }else if (t2>t){
                FileTrigger.TargetFile targetFile=new FileTrigger.TargetFile();
                targetFile.root =fileTrigger.rootPath;
                targetFile.target=fileTrigger.targetPath;
                targetFile.path=filename;
                targetFile.hash= SHAutils.getShaFromFile(targetFile.root +filename,SHAutils.MD_5,false);
                targetFile.syb=2;
                targetFile.len=new File(targetFile.root +filename).length();
                fileTrigger.changque.add(targetFile);
            }
            stringSet.remove(filename);
        }
        for (String filename:stringSet){
            FileTrigger.TargetFile targetFile=new FileTrigger.TargetFile();
            targetFile.root =fileTrigger.rootPath;
            targetFile.target=fileTrigger.targetPath;
            targetFile.path=filename;
            targetFile.hash= SHAutils.getShaFromFile(targetFile.root +filename,SHAutils.MD_5,false);
            targetFile.syb=0;
            targetFile.len=new File(targetFile.root +filename).length();
            fileTrigger.delque.add(targetFile);
        }


        new Thread(()->{
            while (fileTrigger.delque.size()==0&&fileTrigger.addque.size()==0&&fileTrigger.changque.size()==0){
                try {
                    Thread.sleep(CloudeListenCaset.casetTime/3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            updataSate=true;
        }).start();
    }

    public Set<FileTrigger.TargetFile> timeSend(FileTrigger fileTrigger,Long t){
        Set<FileTrigger.TargetFile> targetFiles=new HashSet<>();
        for (String filename:fileTrigger.pathlist){
            File file=new File(fileTrigger.rootPath+filename);
            Path path= Paths.get(file.getPath());
            BasicFileAttributeView basicView=Files.getFileAttributeView(path, BasicFileAttributeView.class);
            long t1;
            long t2;
            try {
                t1= basicView.readAttributes().creationTime().toMillis();
                t2= basicView.readAttributes().lastModifiedTime().toMillis();
            } catch (Exception e) {
                t1=0;
                t2=0;
                e.printStackTrace();
            }
            if (t1>t){
                FileTrigger.TargetFile targetFile=new FileTrigger.TargetFile();
                targetFile.root =fileTrigger.rootPath;
                targetFile.target=fileTrigger.targetPath;
                targetFile.path=filename;
                targetFile.hash= SHAutils.getShaFromFile(targetFile.root +filename,SHAutils.MD_5,false);
                targetFile.syb=1;
                targetFile.len=new File(targetFile.root +filename).length();
                targetFiles.add(targetFile);

            }else if (t2>t){
                FileTrigger.TargetFile targetFile=new FileTrigger.TargetFile();
                targetFile.root =fileTrigger.rootPath;
                targetFile.target=fileTrigger.targetPath;
                targetFile.path=filename;
                targetFile.hash= SHAutils.getShaFromFile(targetFile.root +filename,SHAutils.MD_5,false);
                targetFile.syb=2;
                targetFile.len=new File(targetFile.root +filename).length();
                targetFiles.add(targetFile);
            }
        }
        return targetFiles;
    }
    public Set<FileTrigger.TargetFile> timeSend0(FileTrigger fileTrigger,Long t){
        ArrayList<File> arrayList=new ArrayList();
        Set<FileTrigger.TargetFile> targetFiles=new HashSet<>();
        Tool.getFiles(arrayList,new File(fileTrigger.rootPath+"/"+fileTrigger.targetPath));
        for (File file:arrayList){
            Path path= Paths.get(file.getPath());
            BasicFileAttributeView basicView=Files.getFileAttributeView(path, BasicFileAttributeView.class);
            long t1;
            long t2;
            try {
                t1= basicView.readAttributes().creationTime().toMillis();
                t2= basicView.readAttributes().lastModifiedTime().toMillis();
            } catch (Exception e) {
                t1=0;
                t2=0;
                e.printStackTrace();
                continue;
            }
            String filename=file.getAbsolutePath();
            filename=Tool.normalize2(filename);
            filename=filename.replace(fileTrigger.rootPath,"");
            if (t1>t){
                FileTrigger.TargetFile targetFile=new FileTrigger.TargetFile();
                targetFile.root =fileTrigger.rootPath;
                targetFile.target=fileTrigger.targetPath;
                targetFile.path=filename;
                targetFile.hash= SHAutils.getShaFromFile(targetFile.root +filename,SHAutils.MD_5,false);
                targetFile.syb=1;
                targetFile.len=new File(targetFile.root +filename).length();
                targetFiles.add(targetFile);

            }else if (t2>t){
                FileTrigger.TargetFile targetFile=new FileTrigger.TargetFile();
                targetFile.root =fileTrigger.rootPath;
                targetFile.target=fileTrigger.targetPath;
                targetFile.path=filename;
                targetFile.hash= SHAutils.getShaFromFile(targetFile.root +filename,SHAutils.MD_5,false);
                targetFile.syb=2;
                targetFile.len=new File(targetFile.root +filename).length();
                targetFiles.add(targetFile);
            }
        }
        return targetFiles;
    }

    public JSONObject getLocalPathList(String ab,String file){
        FileTrigger fileTrigger=map.get(ab);
        Element rootElement=fileTrigger.document.getRootElement();
        Element element= getElement(file,rootElement);
        Attribute attribute=element.attribute(0);
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder=reBackElement1(stringBuilder,element);
        JSONObject json=new JSONObject();
        if (attribute.getName().equals("f")){
            json.put(stringBuilder.toString(),"f");
        }else {
            StringBuilder finalStringBuilder = stringBuilder;
            element.elements().forEach((e)->{
                String t=((Element)e).attribute(0).getName();
                String p=((Element)e).attribute(0).getValue();
                json.put( finalStringBuilder.append('/').append(p).toString(),t);
            });
        }
        return json;
    }
    public JSONObject getPagePathList(String ab,String file){
        File fileab=new File(ab);
        CloudPage cloudPage=synMap.get(fileab);
        Element rootElement=cloudPage.document.getRootElement();
        Element element= getElement(file,rootElement);
        Attribute attribute=element.attribute(0);
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder=reBackElement1(stringBuilder,element);
        JSONObject json=new JSONObject();
        if (attribute.getName().equals("f")){
            json.put(stringBuilder.toString(),"f");
        }else {
            StringBuilder finalStringBuilder = stringBuilder.append('/');
            element.elements().forEach((e)->{
                String t=((Element)e).attribute(0).getName();
                String p=((Element)e).attribute(0).getValue();
                json.put( finalStringBuilder.toString()+p,t);
            });
        }
        return json;
    }
    public Element getElement(String string, Element element){
        if (element.attribute(0).getValue().equals(string)){
            return element;
        }else {
            List<Element> elements=element.elements();
            if (elements.size()>0){
                for (Element e:elements){
                    if (e.attribute(0).getValue().equals(string)){
                        return e;
                    }
                    Element element1= getElement(string,e);
                    if (element1!=null){
                        return element1;
                    }
                    }
                }
            }
        return null;
    }

    public StringBuilder reBackElement(StringBuilder stringBuilder,Element element){
        stringBuilder.insert(0,element.attribute(0).getValue()).insert(0,'/');
        Element element1=element.getParent();
        if (element1==null){
            return stringBuilder;
        }else {
            return reBackElement(stringBuilder,element1);
        }
    }
    public StringBuilder reBackElement1(StringBuilder stringBuilder,Element element){
        Element element1=element.getParent();
        if (element1==null){
            stringBuilder.append(element.attribute(0).getValue());
            return stringBuilder;
        }else {
            reBackElement1(stringBuilder,element1);

            return stringBuilder.append('/').append(element.attribute(0).getValue());
        }
    }
    public JSONObject getPathList(String ab,String file){
        FileTrigger fileTrigger=map.get(ab);
        Element rootElement=fileTrigger.document.getRootElement();
        List<String > list=new ArrayList<>();
        List<Element> listbuffer=new ArrayList<>();
        AtomicReference<Element> element= new AtomicReference<>(rootElement);
        List<Element> elements=rootElement.elements();
        listbuffer=elements;
        if (element.get().attribute(0).getValue().equals(file)){
        }else {
            outer:
            while (listbuffer.size()>0){
                elements.clear();
                elements.addAll(listbuffer);
                elements= element.get().elements();
                listbuffer.clear();
                for(Element element1:elements){
                    if (element1.attribute(0).getValue().equals(file)){
                        element.set(element1);
                        break outer;
                    }else {
                        if (element1.getName().equals('p')){
                            listbuffer.addAll(element1.elements());
                        }
                    }
                }
            }
        }

        Attribute attribute=element.get().attribute(0);
        elements=element.get().elements();
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder=reBackElement1(stringBuilder,element.get());
        JSONObject json=new JSONObject();
        if (attribute.getName().equals("f")){
            json.put(stringBuilder.toString(),"f");
        }else {
            StringBuilder finalStringBuilder = stringBuilder;
            element.get().elements().forEach((e)->{
                String t=((Element)e).attribute(0).getName();
                String p=((Element)e).attribute(0).getValue();
                json.put( finalStringBuilder.append('/').append(p).toString(),t);
            });
        }
        return json;

    }
    public JSONObject getPathList1(String ab,String abpath){
        FileTrigger fileTrigger=map.get(ab);
        Element rootElement=fileTrigger.document.getRootElement();
        List<String > list=new ArrayList<>();
        List<Element> elementList=rootElement.elements();
        Element element= rootElement;
        abpath=abpath.substring(fileTrigger.rootPath.length());
        List<String> stringList=Arrays.asList(abpath.split("/"));
        element=element.element("p");
        if (element.attribute(0).getValue().equals(abpath)){
        }else {
            while (stringList.size()>0){
                List<Element> elements=element.elements();

                String s=stringList.get(0);
                stringList.remove(0);
                for(Element element1:elements){
                    if (element1.attribute(0).getValue().equals(s)){
                        element=element1;
                        break ;
                    }
                }
            }
        }

        Attribute attribute=element.attribute(0);
        elementList=element.elements();
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder=reBackElement1(stringBuilder,element);
        JSONObject json=new JSONObject();
        if (attribute.getName().equals("f")){
            json.put(stringBuilder.toString(),"f");
        }else {
            StringBuilder finalStringBuilder = stringBuilder;
            element.elements().forEach((e)->{
                String t=((Element)e).attribute(0).getName();
                String p=((Element)e).attribute(0).getValue();
                json.put( finalStringBuilder.append('/').append(p).toString(),t);
            });
        }
        return json;
    }

    public void PagemovePathToPage(String path1,String opath1,String path2,String opath2){
        CloudPage cloudPage1=synMap.get(path1);
        CloudPage cloudPage2=synMap.get(path2);
        Element element1=getNodeByAbsolute(opath1,cloudPage1.document.getRootElement());
        Element element2=getNodeByAbsolute(opath2,cloudPage2.document.getRootElement());
        Element p1=element1.getParent();
        Element p2=element2.getParent();
        element1.getParent().add(element2);
        p1.remove(element1);
        p2.remove(element2);
        p1.add(element2);
        p2.add(element1);
    }
    public void moveToPathLocal(String path1,String opath1,String path2,String opath2){


    }

    public Element getNodeByAbsolute(String absolutepath,Element root){
        String rootPath=root.attribute(0).getValue();
        String filepath=absolutepath.replace(rootPath,"");
        try {
            String[] strings=filepath.split("/");
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
            return element;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


//    public void finalize(){
//        try {
//            CloudLocal.fileRunner.fileMonitor.stop();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        System.gc();
//    }
}
