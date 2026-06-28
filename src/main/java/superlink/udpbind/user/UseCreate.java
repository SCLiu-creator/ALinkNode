package superlink.udpbind.user;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UseCreate {
    public static void getUser(String username) {
        File f=new File("");
        String pathname=f.getAbsolutePath()+"\\data\\user.xml";
        Document document= create(pathname);
        Element rootElement=document.getRootElement();
        List<String> list=new ArrayList();
        Cheak(list,rootElement,username);
        for (String s:list){
            if (s == null){
                Element element=rootElement.addElement("user");
                element.addAttribute("user",username);
            }
        }

        document.setRootElement(rootElement);
        SaveXml(pathname,document);
    }
    public static void createUser(String username) {
        File f=new File("");
        String pathname=f.getAbsolutePath()+"\\data\\user.xml";
        Document document= create(pathname);
        Element rootElement=document.getRootElement();
        String s=Cheak(rootElement,username);
        if (s == null){
            Element element=rootElement.addElement("user");
            element.addAttribute("user",username);
        }
        document.setRootElement(rootElement);
        SaveXml(pathname,document);
    }
    public static Document create(String pathname){
        Document document= DocumentHelper.createDocument();
        document.addElement("USER");//添加根节点
        FileOutputStream fileOutputStream=null;
        XMLWriter writer=null;
        SAXReader saxReader=null;
        FileInputStream fileInputStream=null;
        try {
            File file=new File(pathname);
            if (!file.exists()){
                file.createNewFile();
            }
            fileOutputStream=new FileOutputStream(file);
            writer=new XMLWriter(fileOutputStream);
            writer.write(document);
            writer.close();
            fileInputStream=new FileInputStream(pathname);
            saxReader=new SAXReader();
            document=saxReader.read(fileInputStream);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return document;
    }
    public static String Cheak(Element element,String username) {

            List<Element> elements = element.elements();
            for (Element e : elements) {
                String name = e.attributeValue("user");
                if (name.equals(username)) {
                    return username;
                }
            }
      return null;
    }
    public static void Cheak(List<String> list,Element element,String username) {
        List<Element> elements = element.elements();
        for (Element e : elements) {
            String name = e.attributeValue("user");
           list.add(name);
        }

    }
    public static void SaveXml(String pathname,Document document){
        try {
            File file=new File(pathname);
            FileOutputStream fileOutputStream=new FileOutputStream(file);
            XMLWriter writer=new XMLWriter(fileOutputStream);
            writer.write(document);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
