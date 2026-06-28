package superlink.filemanage.xmltool;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import superlink.init.InitClass;
import superlink.udpbind.client.UDPclient;
import superlink.util.SHAutils;
import superlink.util.Tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static superlink.filemanage.xmltool.XmlParser.*;

public class XmlCreate {

    public static String userCloudefile;
    public static String userCloudecache;
    public static String userShow;
    public static String userChat;
    public static String userCache;

    public static void init(){
        initUserCloudefile();
        initUserShow();
        initUserCloudecache();
        initUserChat();
        initUserCache();
    }
    static void initUserCloudefile(){
        String path=cloudefile+UDPclient.userlocal.username;
        File filedir=new File(path);
        if (!filedir.exists()){
            filedir.mkdirs();
        }
        userCloudefile=path;
    }
    static void initUserShow() {
        String path=showpath+UDPclient.userlocal.username+"/";
        File filedir=new File(path);
        if (!filedir.exists()){
            filedir.mkdirs();
        }
        userShow =path;
    }
    static void initUserCloudecache() {
        String path=cloudecache+UDPclient.userlocal.username+"/";
        File filedir=new File(path);
        if (!filedir.exists()){
            filedir.mkdirs();
        }
        userCloudecache =path;
    }
    static void initUserChat() {
        String path=chatpath+UDPclient.userlocal.username+"/";
        File filedir=new File(path);
        if (!filedir.exists()){
            filedir.mkdirs();
        }
        userChat =path;
    }
    static void initUserCache() {
        String path=cachepath+UDPclient.userlocal.username+"/";
        File filedir=new File(path);
        if (!filedir.exists()){
            filedir.mkdirs();
        }
        userCache =path;
    }


    public static String createcloudeXml(){
        return createcloudeXml(Tool.SortMode.BY_MODIFIED_TIME);
    }
    public static String createcloudeXml(Tool.SortMode sort){
        String name=cloudefile+ UDPclient.userlocal.username+".xml";
        File file=new File(name);
        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            file.delete();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String path=userCloudefile;
        Document document=createXmlDir(name,path);
        Element element=document.getRootElement();
        File file1=new File(cloudefile+UDPclient.userlocal.username+"/");
        writeXml(element,file1,sort);
        SaveXml(document,name);
        return name;
    }

    public static String createUserXml(){
        File file=new File("");
        String name=showpath+UDPclient.userlocal.username+".xml";
        file=new File(name);
        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            file.delete();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String path= userShow;
        Document document=createXmlDir(name,path);
        Element element=document.getRootElement();
        File file1=new File(userShow);
        writeXml(element,file1);
        SaveXml(document,name);
        return name;
    }


//    public static Element addUserXml(String filePath){
//        String name= showpath+UDPclient.userlocal.username+".xml";
//        File file=new File(name);
//        Document document;
//        long l=System.currentTimeMillis();
//        if (!file.exists()){
//            document=createXmlFile(file,"PathName",l/1000);
//        }else {
//            document=XmlParser.openXml(file.getAbsolutePath());
//        }
//        Element rootElement=document.getRootElement();
//        Attribute t=rootElement.attribute("t");
//        if (t==null){
//            rootElement.addAttribute("t",String.valueOf(l/1000));
//            l=l/1000;
//        }else {
//            l=Long.valueOf(t.getValue());
//        }
//
//        File file1=new File(filePath);
//        if (file1.exists()){
//            Long time=l-System.currentTimeMillis()/1000;
//            if (file1.isDirectory()){
//                Element element= rootElement.addElement("p");
//                element.addAttribute("p",filePath);
//                XmlParser.writeXml(element,file1);
//                element.addAttribute("t",time.toString());
//            }else {
//                Element element= rootElement.addElement("f");
//                element.addAttribute("f",filePath);
//                element.addAttribute("t",time.toString());
//            }
//
//        }
//        writeXml(rootElement,file);
//        SaveXml(document,name);
//        return rootElement;
//    }
    public static Element addUserXml(List<String> filePaths){
        String name= showpath+UDPclient.userlocal.username+".xml";
        File file=new File(name);
        Document document;
        long l=System.currentTimeMillis();
        if (!file.exists()){
            document=createXmlFile(file,"PathName",l/1000);
        }else {
            document=XmlParser.openXml(file.getAbsolutePath());
        }
        Element rootElement=document.getRootElement();
        Attribute t=rootElement.attribute("T");
        if (t==null){
            rootElement.addAttribute("T",String.valueOf(l/1000));
            l=l/1000;
        }else {
            l=Long.valueOf(t.getValue());
        }
        String f=null;
        for (String filePath:filePaths){
            File file1=new File(filePath);
            if (file1.exists()){
                Long time=System.currentTimeMillis()/1000-l;
                f= Tool.normalize2(filePath);
                if (file1.isDirectory()){
                    Element element= rootElement.addElement("p");
                    element.addAttribute("p",f);
//                    XmlParser.writeXml(element,file1);
                    element.addAttribute("t",time.toString());
                    element.addAttribute("T",Long.valueOf(System.currentTimeMillis()/1000).toString());
                }else {
                    Element element= rootElement.addElement("f");
                    element.addAttribute("f",f);
                    element.addAttribute("t",time.toString());
                }
            }
        }
//        writeXml(rootElement,file);
        SaveXml(document,name);
        return rootElement;
    }

    public static Document createXmlFile(File file,String StringRoot){
        Document document= DocumentHelper.createDocument();
        document.addElement(StringRoot);
//            document.addComment(dir);
        XMLWriter writer=null;
        FileOutputStream fileOutputStream=null;
        try {
            fileOutputStream=new FileOutputStream(file);
            writer=new XMLWriter(fileOutputStream);
            writer.write(document);
            writer.close();
            return document;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    public static Document createXmlFile(File file,String StringRoot,Long time){
        if (!file.exists()){
            Document document= DocumentHelper.createDocument();
            Element root=document.addElement(StringRoot);
            root.addAttribute("T",time.toString());
//            document.addComment(dir);
            XMLWriter writer=null;
            FileOutputStream fileOutputStream=null;
            try {
                fileOutputStream=new FileOutputStream(file);
                writer=new XMLWriter(fileOutputStream);
                writer.write(document);
                writer.close();
                return document;
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    public static File createFile(String filename){
        String name=XmlCreate.userCloudefile+"/"+ SHAutils.getMD5(filename,false) +".xml";
        File file=new File(name);
        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
    public static File reCreateFile(String filename){
        File file=new File(filename);
        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
    /*cloudefile
     * file:this filename
     * pathname:targetPath
     * dir:abtarget
     * */
    public static Document createXmlDoc1(String dir,String pathname,File file){
        Document document= DocumentHelper.createDocument();
        Element root=document.addElement("rootpath");//添加根节点
        FileOutputStream fileOutputStream=null;
        XMLWriter writer=null;
        SAXReader saxReader=null;
        String name=file.getPath();
        String dirfile=new File(dir).getParent().replace("\\","/");
        //absolutpath
        root.addAttribute("p",dirfile);
        //targe
        root.addElement("p").addAttribute("p",pathname);
        FileInputStream fileInputStream=null;
        try {
            fileOutputStream=new FileOutputStream(file);
            writer=new XMLWriter(fileOutputStream);
            writer.write(document);
            writer.close();
            fileInputStream=new FileInputStream(name);
            saxReader=new SAXReader();
            document=saxReader.read(fileInputStream);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return document;
    }
    //
    public static Document createXmlDir(String name,String path){
        Document document= DocumentHelper.createDocument();
        Element root=document.addElement("PathName");//添加根节点
        //root.setText(dir);
        // document.addComment(dir);//添加注释
        FileOutputStream fileOutputStream=null;
        XMLWriter writer=null;
        SAXReader saxReader=null;
        root.addAttribute("p",path);
        FileInputStream fileInputStream=null;
        try {
            File file=new File(name);
            if (!file.exists()){
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return document;
    }

    public static Element createAutoMapXml(){
        Document document= DocumentHelper.createDocument();
        Element root=document.addElement("p");
        root.addAttribute("r", InitClass.roots[InitClass.roots.length-1].getAbsolutePath());
        FileOutputStream fileOutputStream=null;
        XMLWriter writer=null;
        String name= XmlCreate.userCloudecache +"Auto.xml";
        File[ ] files= InitClass.roots[InitClass.roots.length-1].listFiles();
        File[] files1=null;
        File[] files2=null;
        for (File f:files){
            if (f.isDirectory()){
                Element element0=root.addElement("p");
                element0.addAttribute("p",f.getName());
                files1=f.listFiles();
                if (files1==null){
                    continue;
                }
                for (File ff:files1){
                    if (ff.isDirectory()){
                        Element element1=element0.addElement("p");
                        element1.addAttribute("p",ff.getName());
                        files2=ff.listFiles();
                        if (files2==null){
                            continue;
                        }
                        for (File fff:files2){
                            if (fff.isDirectory()){
                                element1.addElement("p").addAttribute("p",fff.getName());
                            }
                        }
                    }
                }
            }
        }

        try {
            fileOutputStream=new FileOutputStream(name);
            writer=new XMLWriter(fileOutputStream);
            writer.write(document);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return root;
    }

}
