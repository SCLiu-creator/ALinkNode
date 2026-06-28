package superlink.filemanage.scanpackage;

import org.dom4j.QName;
import superlink.filemanage.xmltool.XmlCreate;
import superlink.filemanage.xmltool.XmlParser;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import superlink.util.Utils;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.List;

import static superlink.util.Utils.chooseFile;
import static superlink.util.Utils.chooseFilepath;


public class FileScan {

    public static void main(String[] args) throws Exception {
        String filename=Utils.chooseFile();
        Document document=XmlParser.openXml(filename);
        Element targetElement = (Element) document.getRootElement().element("p").element("p");
        targetElement.clearContent();
        XmlParser.SaveXmlto(document, new File(filename));
        File file0 = new File("");
        file0=new File(file0.getAbsolutePath());
        List<String> stringList=new ArrayList<>();
        scanPackage(file0,stringList,file0.getName());
        for (String s:stringList){
            System.out.println(s);
        }
        //createXmls();
        File file = new File(XmlParser.cloudefile+".xml");
        Document rootElement = new SAXReader().read(file);
        Element element = rootElement.getRootElement();
//        element.setData("addAttribute(\"name\",\"aaaa\")");

        element.addElement("user").addAttribute("name", "aaaaa");
        element.addElement("user").addAttribute("name", "bbbb");
        element.addElement("user").addAttribute("name", "aaaccaa");

        String sss=((List<Element>)element.elements()).get(0).attribute(0).getValue();
        String ssss=element.element(new QName("f")).attribute(0).getValue();
//        createUserXml();
        List<String> strings = new ArrayList<>();
        Element element1 = rootElement.getRootElement().element("user");
        //String string=XmlParser.parserXml(strings,"D:\\java\\新建文件夹\\udpclient\\data\\cloudefile\\d3a3d02374cb154040f9ad01251a2bd6.xml");//"data"+"\\"+"userpage.xml"
        strings = XmlParser.parserXml(element1);
        for (String s : strings) {
//              String[] s1=s.split("\\\\");
//              s=s1[s1.length-1];
            System.out.println("\\" + s);
        }
        System.out.println(rootElement.getRootElement());
    }

    public static void createXmls1() {
        String path = chooseFilepath().replace("\\","/");
        File file = new File(path);

        Document document = XmlParser.createXmlDoc(path);
        Element element = document.getRootElement();
        XmlParser.writeXml(element, file);

        document.setRootElement(element);
        XmlParser.SaveXml(document);
    }

    /*创建cloudfiledir*/
    public static File createXmls(String path) {
        path=path.replace("\\","/");
        File file = new File(path);
        String f= file.getName();
        File filename = XmlCreate.createFile(path);
        Document document = XmlCreate.createXmlDoc1(path, f, filename);
        Element element = document.getRootElement().element("p");
        XmlParser.writeXml(element, file);
        XmlParser.SaveXmlto(document, filename);
        return filename;
    }
    /*targePath是绝对路径*/
    public static Document reCreateXmls(String fileName,String targePath) {
        File file = new File(targePath);
        File filename = XmlCreate.reCreateFile(fileName);
        Document document=XmlParser.openXml(fileName);
        Element targetElement = document.getRootElement().element("p");
        targetElement.clearContent();
//        targetElement.remove()
        XmlParser.writeXml(targetElement, file);
        XmlParser.SaveXmlto(document, filename);
        return document;
    }

    /*创建cloudfiledir*/
    public static File createFileViewCacheXmls(String path) {
        path=path.replace("\\","/");
        File file = new File(path);
        File file1=new File(XmlCreate.userShow+path.hashCode());
        Document document=XmlCreate.createXmlFile(file1,"path");
        Element element=document.getRootElement();
        if (file.isDirectory()){
            File[] files=file.listFiles();
            for (File p:files){
                long l=p.length();
                long time=p.lastModified()/1000;
                Element el=element.addElement("p");
                if (p.isDirectory()){
                    el.addAttribute("p",p.getName());
                }else {
                    el.addAttribute("f",p.getName());
                }
                el.addAttribute("t",String.valueOf(time));
                el.addAttribute("l",String.valueOf(l));
            }
        }
        XmlParser.SaveXmlto(document, file);
        return file;
    }

    public static void createXml() {
        String path = chooseFile();
        File file = new File(path);
        Document document = XmlParser.createXmlDoc(path);
        Element element = document.getRootElement();
        XmlParser.writeXml(element, file);

        document.setRootElement(element);
        XmlParser.SaveXml(document);
    }

    public static String path;
    public static ClassLoader classLoader;
    //扫描指定包下的所有类，返回一个Class对象的列表


    public static void scanPackage(File pathFile,List<String> list,String prex){
        try {
            File[] files=pathFile.listFiles();
            String buf=prex;
            for (File file:files){
                if (file.isDirectory()){
                    String buff=buf+File.separator+file.getName();
                    scanPackage(file,list,buff);
                }else {

                    String s=buf+File.separator+file.getName();
                    list.add(s);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(pathFile);
        }
    }

    public static List<Class<?>> scanPackage(String packageName) {
        List<Class<?>> classes = new ArrayList<>(); //存放结果的列表
        try {
            String[] strings = packageName.split("\\\\");
            strings[0] = "";
            StringBuilder sbu = new StringBuilder("");
            for (String s : strings) {
                sbu.append(s).append(".");
            }
            path = packageName.replace(".", "/");
            //获取当前线程的类加载器，用于加载资源
            classLoader = Thread.currentThread().getContextClassLoader();
            //获取指定路径下的所有资源的URL对象
            String urlll = System.getProperty("user.dir");
            Enumeration<URL> urls = classLoader.getResources(".");//"/"
            //遍历每个URL对象
            URL url = new File(path).toURI().toURL();
            if (url != null) {
                String protocol = url.getProtocol(); //获取协议名，判断是文件还是jar包
                if (protocol.equals("file")) { //如果是文件
                    String dirPath = url.getPath(); //获取文件夹的绝对路径
                    scanDir(packageName, dirPath, classes); //扫描文件夹下的所有类文件，添加到结果列表中
                } else if ("jar".equals(protocol)) { //如果是jar包
                  //  scanJar(packageName, url, classes); //扫描jar包下的所有类文件，添加到结果列表中
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classes; //返回结果列表
    }

    //扫描指定文件夹下的所有类文件，添加到结果列表中
    public static void scanDir(String packageName, String dirPath, List<Class<?>> classes) {
//        dirPath=dirPath.substring(1);
        File dir = new File(dirPath); //根据文件夹路径创建File对象
//        if (!dir.exists()){
//            dir.mkdir();
//        }
        if (dir.isDirectory()) { //判断是否是文件夹
            File[] files = dir.listFiles(); //获取文件夹下的所有文件或子文件夹
            if (files != null) { //判断是否为空
                for (File file : files) { //遍历每个文件或子文件夹
                    String fileName = file.getName(); //获取文件名或子文件夹名
                    if (file.isFile()) { //如果是类文件，去掉后缀名，拼接包名和类名，得到全限定类名
                        String className = packageName + "." + fileName.substring(0, fileName.length() - 6);
                        className = className.substring(className.indexOf(".") + 1);
                        try {
                            System.out.println(className);
                            Class<?> clazz = Class.forName(className); //根据全限定类名加载Class对象
                            classes.add(clazz); //添加到结果列表中
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else if (file.isDirectory()) { //如果是子文件夹，递归调用本方法，传入子包名和子文件夹路径
                        scanDir(packageName + "." + fileName, file.getPath(), classes);
                    }
                }
            }
        }
    }
}


