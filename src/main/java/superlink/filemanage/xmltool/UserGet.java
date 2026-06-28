package superlink.filemanage.xmltool;

import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.usedata.User;
import superlink.util.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static superlink.filemanage.xmltool.XmlParser.openXml;
import static superlink.util.Utils.sanc;

public class UserGet {
    public static List<String> userlist=new ArrayList<>();
    public static List<User> userList=new ArrayList<>();
    public static volatile Element user;
    public static volatile boolean UserSynServer=false;
    public static Integer cloudSynSymbol=0;
    //jswing和http登录模式选择
    public static boolean c=false;
    static {
        String filename=XmlParser.dir+"userpage.xml";
        File file=new File(filename);
        Document document = null;
        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            document= DocumentHelper.createDocument();
            document.addElement("USER");
            try {
                FileOutputStream fileOutputStream=new FileOutputStream(filename);
                XMLWriter writer=new XMLWriter(fileOutputStream);
                writer.write(document);
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        try {
            document=new SAXReader().read(file);
        } catch (DocumentException e) {
            document= DocumentHelper.createDocument();
            document.addElement("USER");
            try {
                FileOutputStream fileOutputStream=new FileOutputStream(filename);
                XMLWriter writer=new XMLWriter(fileOutputStream);
                writer.write(document);
                writer.close();
            } catch (Exception ee) {
                e.getMessage();
            }
        }


        try {
            Attribute attribute=document.getRootElement().attribute("c");
            if (attribute!=null){if("True".equals(attribute.getValue())){c=true;}}
            userlist=XmlParser.parserUserXml(document);
            userList=XmlParser.parserUserXml1(document);
            Attribute def=document.getRootElement().attribute("defuser");
            if(def!= null){
                String defuser=def.getValue();
                if (!defuser.equals("")){
                    for (String u:userlist){
                        if (u.equals(defuser)){
                            List<Element> list=document.getRootElement().elements();
                            for (Element element:list){
                                String name=element.attribute("name").getValue();
                                if (defuser.equals(name)){
                                    UserGet.user=element;}
                            }
                        }
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String chooseUser(){
        if (user!=null){
            return user.attribute("name").getValue();
        }
        if (c){
            return chooseUser1();
        }
        else {
            return chooseUser2();
        }
    }
    public String chooseUser1(){
        Integer prex=new Integer(0);
        for (String s:userlist){
            System.out.println(prex+":   "+s);
            prex++;
        }
        System.out.println("选择用户 :   ");
        String send =sanc();
        send=send.replace(" ","").replace("\n","");
        String user=null;
        try {
            Integer input=Integer.valueOf(send);
            prex=0;
            for (String s:userlist){
                if (prex.equals(input)){
                    user=s;
                }
                prex++;
            }
        }catch (Exception e){
            System.out.println("CREATE USER!");
        }

        if (user==null){
            user=new Utils.CreateName().create();
            String label=sanc();
            label=label.trim();
            String filename=XmlParser.dir+"userpage.xml";
            try {
                Document document=new SAXReader().read(new File(filename));
                document.getRootElement().addElement("user").addAttribute("name",user).addAttribute("label",label);
                FileOutputStream fileOutputStream=new FileOutputStream(filename);
                XMLWriter writer=new XMLWriter(fileOutputStream);
                writer.write(document);
                writer.close();
            } catch (Exception  e) {
                e.printStackTrace();
            }
        }
        String filename=XmlParser.dir+"userpage.xml";
        Document document= null;
        try {
            document = new SAXReader().read(new File(filename));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        List<Element> list=document.getRootElement().elements();
        for (Element element:list){
            String name=element.attribute("name").getValue();
            if (user.equals(name)){UserGet.user=element;}
        }

        return user;
    }


    public String chooseUser2(){
        synchronized (userlist){
            Thread.currentThread().setName("chooseUser");
            try {
            userlist.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String user=UserGet.user.attribute("name").getValue();
        return user;
    }

    public String userList(){
        Integer prex=new Integer(0);
        for (String s:userlist){
            System.out.println(prex+":   "+s);
            prex++;
        }
        System.out.println("选择用户 :   ");
        String send =sanc();
        send=send.replace(" ","").replace("\n","");
        String user=null;
        try {
            Integer input=Integer.valueOf(send);
            prex=0;
            for (String s:userlist){
                if (prex.equals(input)){
                    user=s;
                }
                prex++;
            }
        }catch (Exception e){
            System.out.println("CREATE USER!");
        }

        if (user==null){
            user=new Utils.CreateName().create();
            String label=sanc();
            label=label.trim();
            String filename=XmlParser.dir+"userpage.xml";
            try {
                Document document=new SAXReader().read(new File(filename));
                document.getRootElement().addElement("user").addAttribute("name",user).addAttribute("label",label);
                FileOutputStream fileOutputStream=new FileOutputStream(filename);
                XMLWriter writer=new XMLWriter(fileOutputStream);
                writer.write(document);
                writer.close();
            } catch (Exception  e) {
                e.printStackTrace();
            }
        }
        String filename=XmlParser.dir+"userpage.xml";
        Document document= null;
        try {
            document = new SAXReader().read(new File(filename));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        List<Element> list=document.getRootElement().elements();
        for (Element element:list){
            String name=element.attribute("name").getValue();
            if (user.equals(name)){UserGet.user=element;}
        }

        return user;
    }
    public static List<String> getUserList(){
        return userlist;
    }

    //完成初始化后才能运行
    public static void setUser(){
        Document document=openXml(XmlParser.dir+"userpage.xml");
        Element root=document.getRootElement();
        for (Element e:(List<Element>)root.elements()){
            if (e.attribute("name").getValue().equals(UDPclient.userlocal.username)){
                UserGet.user=e;
                break;
            }
        }
    }

    public static void setDefaultUser(){
        Document document=openXml(XmlParser.dir+"userpage.xml");
        Element root=document.getRootElement();
        root.addAttribute("defuser",user.attributeValue("name"));
        XmlParser.SaveXml(document,XmlParser.dir+"userpage.xml");
        System.out.println("setOver");
    }
    public static void unsetDefaultUser(){
        Document document=openXml(XmlParser.dir+"userpage.xml");
        Element root=document.getRootElement();
        root.addAttribute("defuser","");
        XmlParser.SaveXml(document,XmlParser.dir+"userpage.xml");
        System.out.println("unsetOver");
    }
    public static void setSyml(){
        Attribute attribute=user.attribute("CloudSynSym");
        if (attribute!=null){
            cloudSynSymbol=Integer.valueOf(attribute.getValue());
        }
    }

    public static void save(){
        Document document=user.getDocument();
        XmlParser.SaveXml(document,XmlParser.dir+"userpage.xml");
    }


    public void add(Element element){
        user.add(element);
        XmlParser.SaveXml(element.getDocument(),XmlParser.dir+"userpage.xml");
    }


    public void adduser(String name){
        Element element=user.addElement("u");
        element.addAttribute("name",name);
        XmlParser.SaveXml(element.getDocument(),XmlParser.dir+"userpage.xml");
    }

    public static void setSyn(boolean b){
        if (b){
            Element element=user.addAttribute("CloudeSyn","on");
        }else {
            Element element=user.addAttribute("CloudeSyn","off");
        }

        XmlParser.SaveXml(user.getDocument(),XmlParser.dir+"userpage.xml");
    }
    public static boolean getSyn(){
        if (user!=null){
            Attribute attribute=user.attribute("CloudeSyn");
            if("on".equals(attribute.getValue())){
                return true;
            }
        }
        return false;
    }
    public static void setCloudeMode(int i){
        String mode = String.valueOf(i);
        user.addAttribute("CloudeMode",mode);
        XmlParser.SaveXml(user.getDocument(),XmlParser.dir+"userpage.xml");
    }
    public static int getCloudeMode(){
        if (user!=null){
            Attribute attribute=user.attribute("CloudeMode");
            if(attribute!=null&&attribute.getValue()!=null){
                return Integer.parseInt(attribute.getValue());
            }
        }
        return -1;
    }

}
