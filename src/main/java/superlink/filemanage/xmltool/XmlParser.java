package superlink.filemanage.xmltool;

import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import superlink.init.InitClass;
import superlink.udpbind.usedata.User;
import superlink.util.SHAutils;
import superlink.util.Tool;

import java.io.*;
import java.util.*;

public class XmlParser {
    public static String extend="/";
    public static String dir="/";
    public static String cloudefile="/";
    public static String cloudefileRel="/";
    public static String showpath="/";
    public static String showpathRel="/";
    public static String cachepath="/";
    public static String cloudedown="/";
    public static String cloudecache="/";
    public static String chatpath="/";


    static {
        String path= InitClass.absolute+"data"+"/";
        dir=path;
    }

    static {
        String path=InitClass.absolute+"extends"+"/";
        File file=new File(path);
        file.mkdirs();
        extend=path;
    }

    static {
        cloudefileRel="data/"+"cloudefile"+"/";
        String path=InitClass.absolute+cloudefileRel;
        File file=new File(path);
        file.mkdirs();
        cloudefile=path;
    }
    static {
        showpathRel="data"+"/"+"show"+"/";
        String path=InitClass.absolute+showpathRel;
        File file=new File(path);
        file.mkdirs();
        showpath=path;
    }
    static {
        String path=dir+"cachepath"+"/";
        File file=new File(path);
        file.mkdirs();
        cachepath=path;
    }
    static {
        String path=InitClass.absolute+"down"+"/";
        File file=new File(path);
        file.mkdirs();
        cloudedown=path;
    }
    static {
        String path=dir+"cloudecache"+"/";
        File file=new File(path);
        file.mkdirs();
        cloudecache=path;
    }
    static {
        String path=dir+"chatpath"+"/";
        File file=new File(path);
        file.mkdirs();
        chatpath=path;
    }

    public static String escapeSpecialCharactersForXml(String input) {
        if (input == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            switch (c) {
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                case '"':
                    sb.append("&quot;");
                    break;
                case '\'':
                    // XML没有官方的单引号实体引用，但可以用&apos;或双引号代替
                    sb.append("&apos;");
                    break;
                default:
                    // 对于其他字符，直接添加
                    sb.append(c);
            }
        }
        return sb.toString();
    }

    public static Document openXml(String path){
        SAXReader reader = new SAXReader();
        Document document=null;
        FileInputStream fileInputStream=null;
        try {
            fileInputStream=new FileInputStream(path);
            document=reader.read(fileInputStream);
        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
        }

        return document;
    }

    //存储至*datapath
    public static void SaveXml(Document document){
        String path=document.getRootElement().attributeValue("p");
        path=SHAutils.getMD5(path,false);
        try {
            File file=new File(cloudefile +path+".xml");
            FileOutputStream fileOutputStream=new FileOutputStream(file);
            XMLWriter writer=new XMLWriter(fileOutputStream);
            writer.write(document);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void SaveXmlto(Document document,File file){

        try {
//            File file=new File(path+".xml");
            FileOutputStream fileOutputStream=new FileOutputStream(file);
            XMLWriter writer=new XMLWriter(fileOutputStream);
            writer.write(document);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void SaveXml(Document document,String name){
        try {
            synchronized (document){
                File file=new File(name);
                FileOutputStream fileOutputStream=new FileOutputStream(file);
                XMLWriter writer=new XMLWriter(fileOutputStream);
                writer.write(document);
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //fq
    public static Document createXmlDoc(String dir){
        Document document= DocumentHelper.createDocument();
        Element root=document.addElement("PathName");//添加根节点
        //root.setText(dir);
       // document.addComment(dir);//添加注释
        FileOutputStream fileOutputStream=null;
        XMLWriter writer=null;
        SAXReader saxReader=null;
        String name=cloudefile+ SHAutils.getMD5(dir,false) +".xml";
        root.addAttribute("p",dir);
        FileInputStream fileInputStream=null;
        try {
            File file=new File(name);
            if (!file.exists()){
                file.createNewFile();
            }
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

//    public static void createXml(String dir){
//        Document document= DocumentHelper.createDocument();
//        Element root=document.addElement(dir);
//        document.addComment(dir);
//        FileOutputStream fileOutputStream=null;
//        XMLWriter writer=null;
//        SAXReader saxReader=null;
//       // dir.replace("\\","-");
//        String name=cloudefile+dir+".xml";
//        FileInputStream fileInputStream=null;
//        try {
//
//            fileOutputStream=new FileOutputStream(name);
//            writer=new XMLWriter(fileOutputStream);
//            writer.write(document);
//            writer.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    /*document将写入数据的根节点
    *pathFile将写入节点的路径
    * */
//    OutputFormat format = OutputFormat.createPrettyPrint();
//    format.setEncoding("UTF-8");// 设置XML文件的编码格式
//    format.setIndent("    ");//设置缩进
//    format.setNewlines(true);// 设置换行
//    File file = new File(filePath);//获得文件
    //writer = new XMLWriter(new FileWriter(file), format);
    public static void writeXml(Element element,File pathFile){
        try {
            File[] files=pathFile.listFiles();
            for (File file:files){
                if (file.isDirectory()){
                    Element e=element.addElement("p");
                    String s=file.getName();
                    e.addAttribute("p",s);
                    writeXml(e,file);
                }else {
                    Element e=element.addElement("f");
                    String s=file.getName();
                    e.addAttribute("f",s);
                }
            }
        }catch (Exception e){
            System.out.println("writeXml  "+e.getMessage());
            System.out.println(pathFile);
        }
    }
    public static void writeXml(Element element,File pathFile,Tool.SortMode sort){
        try {
            File[] files=pathFile.listFiles();
            if (files != null) {
                // 按修改时间升序
                File[] sortedFiles = Tool.sortFiles(files,sort);

                for (File file : sortedFiles) {
                    if (file.isDirectory()) {
                        Element e = element.addElement("p");
                        String s = file.getName();
                        e.addAttribute("p", s);
                        writeXml(e, file);
                    } else {
                        Element e = element.addElement("f");
                        String s = file.getName();
                        e.addAttribute("f", s);
                    }
                }
            }
        }catch (Exception e){
            System.out.println("writeXml  "+e.getMessage());
            System.out.println(pathFile);
        }
    }

    public static void writeXml(Element element, File pathFile, int i){
        try {
            if (i<=0){
                return;
            }
            File[] files=pathFile.listFiles();
            for (File file:files){
                if (file.isDirectory()){
                    Element e=element.addElement("p");
                    String s=file.getName();
                    e.addAttribute("p",s);
                    writeXml(e,file,i-1);
                }else {
                    Element e=element.addElement("f");
                    String s=file.getName();
                    e.addAttribute("f",s);
                }
            }
        }catch (Exception e){
            System.out.println("writeXml  "+e.getMessage());
            System.out.println(pathFile);
        }
    }

    /*包含root节点的list*/
    public static List<String> parserXmlRoot(String datapath){
        List<String> list=new LinkedList<>();
        SAXReader saxReader=new SAXReader();
        Document document = null;
        try {
            File file=new File(datapath);
            document=saxReader.read(file);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        Element root=document.getRootElement();
        String path=root.attribute(0).getValue();
        //path="";
        parserXml(path,root,list);
        return list;
    }
    public static Set<String> parserXmlSet(String datapath){
        Set<String> list=new HashSet<>();
        SAXReader saxReader=new SAXReader();
        Document document = null;
        try {
            File file=new File(datapath);
            document=saxReader.read(file);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        Element root=document.getRootElement();
        Element element=root.element("p");
        String path="";
        parserXml(path,element,list);
        return list;
    }
    /*不包含root节点的list,返回rootpath*/
    public static String parserXml(List<String> list,String datapath){
        SAXReader saxReader=new SAXReader();
        Document document = null;
        try {
            File file=new File(datapath);
            document=saxReader.read(file);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        Element root=document.getRootElement();
        String rootpath=root.attribute(0).getValue();
        String path="";
        List<Element> elements=root.elements();
        Element e=elements.get(0);
        parserXml(path,e,list);
        return rootpath;
    }
    public static List<String> parserUserXml(Document document){
        List<String> list=new LinkedList<>();
        Element root=document.getRootElement();//Users
        String path="";
        List<Element> elements=root.elements();
        for (Element e:elements) {
            list.add(e.attribute(0).getValue());
        }
        return list;
    }
    public static List<User> parserUserXml1(Document document){
        List<User> list=new LinkedList<>();
        Element root=document.getRootElement();//Users
        String path="";
        List<Element> elements=root.elements();
        for (Element e:elements) {
            User user=new User();
            user.username=e.attribute(0).getValue();
            user.nickName=e.attribute("label").getValue();
            list.add(user);
        }
        return list;
    }
    public static List<String> parserXml(Document document){
        List<String> list=new LinkedList<>();
        Element root=document.getRootElement();
        String path;//=root.attribute(0).getValue();
        path="";
        parserXml(path,root,list);
        return list;
    }
    public static List<String> parserXml(Element element){
        List<String> list=new ArrayList<>();
        String path="";
        parserXml(path,element,list);
        return list;
    }
    public static List<String> parserXml(Element element,String charest){
        List<String> list=new ArrayList<>();
        String path="";
        parserXml(path,element,list,charest);
        return list;
    }
    public static List<String> parserXml(Element element,List<String> charests){
        List<String> list=new ArrayList<>();
        String path="";
        parserXml(path,element,list,charests);
        return list;
    }
    public static List<String> parserXmlUnTarget(Element element){
        List<String> list=new ArrayList<>();
        String path="";
        for (Element e:(List<Element>)element.elements()){
            parserXml(path,e,list);
        }
        return list;
    }

    private static void parserXml(String datapath,Element element,List<String> strings){
        if ("p".equals(element.attribute(0).getName())){
            String path=datapath+element.attribute(0).getValue();
            List<Element> elements=element.elements();
            for (Element e:elements){
                parserXml(path+'/',e,strings);
            }
        }else {
            String file=datapath+element.attribute(0).getValue();
            strings.add(file);
        }
    }
    private static void parserXml(String datapath,Element element,List<String> strings,String charest){
        List<Attribute> attributes=element.attributes();
        Attribute attribute=null;
        for (Attribute att:attributes){
            if (charest.equals(att.getName())||"p".equals(att.getName())){
                attribute=att;
            }
        }
        if (!charest.equals(attribute.getName())){
            String path=datapath+element.attribute(0).getValue();
            List<Element> elements=element.elements();
            for (Element e:elements){
                parserXml(path+'/',e,strings,charest);
            }
        }else {
            String file=datapath+element.attribute(0).getValue();
            strings.add(file);
        }
    }
    private static void parserXml(String datapath,Element element,List<String> strings,List<String> charests){
        List<Attribute> attributes=element.attributes();
        Attribute attribute=null;
        for (Attribute att:attributes){
            if (charests.contains(att.getName())){
                attribute=att;
            }
        }
        List<Element> elements=element.elements();
        if (attribute!=null){
            if (elements.size()>0){
                String path=datapath+attribute.getValue();
                for (Element e:elements){
                    parserXml(path+'/',e,strings,charests);
                }
            }else {
                String file=datapath+attribute.getValue();
                strings.add(file);
            }
        }else {
            if (elements.size()>0){
                for (Element e:elements){
                    parserXml(datapath,e,strings,charests);
                }
            }
        }
    }
    private static void parserXml(String datapath,Element element,Set<String> strings){
        if ("p".equals(element.attribute(0).getName())){
            String path=datapath+element.attribute(0).getValue();
            List<Element> elements=element.elements();
            for (Element e:elements){
                parserXml(path+'/',e,strings);
            }
        }else {
            String file=datapath+element.attribute(0).getValue();
            strings.add(file);
        }
    }

    public static Document byetToDocument(byte[] bytes){
        SAXReader reader=new SAXReader();
        Document document = null;
        try {
            document=reader.read(new ByteArrayInputStream(bytes));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return document;
    }

    public static Document parseStr(String xmlString) throws DocumentException {
        if (xmlString == null || xmlString.trim().isEmpty()) {
            throw new IllegalArgumentException("XML string is empty");
        }

        // 简单判断是否是完整 XML（以 <?xml 开头）
        if (xmlString.trim().startsWith("<?xml")) {
            SAXReader reader = new SAXReader();
            return reader.read(new StringReader(xmlString));
        } else {
            return DocumentHelper.parseText(xmlString);
        }
    }

    public static Element getSonElement(Element element,String qname,String s){
        List<Element> elements=element.elements();
        Element element1=null;
        for (Element e:elements){
            if (s.equals(e.attribute(qname).getValue())){
                element1=e;
            }
        }
        return element1;
    }
    public static Element getSonElementClear(Element element,String qname,String s){
        List<Element> elements=element.elements();
        Element element1=null;
        for (Element e:elements){
            if (s.equals(e.attribute(qname).getValue())){
                if (element1!=null){
                    e.remove(element);
                }
                element1=e;
            }
        }
        return element1;
    }

}
