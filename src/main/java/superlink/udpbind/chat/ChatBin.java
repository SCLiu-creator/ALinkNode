package superlink.udpbind.chat;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import superlink.filemanage.xmltool.XmlCreate;
import superlink.filemanage.xmltool.XmlParser;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.recives.data.RingQue;
import superlink.util.Tool;
import superlink.util.Utils;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class ChatBin {
    public RingQue<ChatData> ringQue;
    public String username;

    public Element root;

    public int anInt;

    public ChatBin(){}
    public ChatBin(String username){
        this.username=username;
        File file=new File(XmlCreate.userChat+'/'+username+".xml");
        if (file.exists()){
            FileInputStream fileInputStream= null;
            try {
                fileInputStream = new FileInputStream(file);
                SAXReader saxReader=new SAXReader();
                Document document=saxReader.read(fileInputStream);
                root=document.getRootElement();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Document document=createChatDoc(file);
            root=document.getRootElement();
        }
        int s=30;
        ringQue=new RingQue<>(s);

        List list=root.elements();
        if (list.size()>s){
            Element element;
            for (int i = list.size()-1; i >= list.size()-s; i--) {
                 element= (Element) list.get(i);
                ringQue.add(elementToData(element));
            }
        }else {
            Element element;
            for (int i = list.size()-1; i >= 0; i--) {
                element= (Element) list.get(i);
                ringQue.add(elementToData(element));
            }
        }
    }

    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");

    public ChatData createData(String text,File file){
        ChatData data=new ChatData();
        data.u= UDPclient.userlocal.username;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
//        dateFormat.format(Calendar.getInstance().getTime());
        data.date=dateFormat.format(Calendar.getInstance().getTime());
        if (file!=null){
            data.file=file.getAbsolutePath();
        }
        data.text=text;
        data.setSn();
        return data;
    }

    public ChatData add(String text,File file){
        ChatData data=createData(text,file);
        if (ringQue.size()==ringQue.cap){
            ringQue.poll();
        }
        ringQue.add(data);
        addElement(data);
        return data;
    }
    public ChatData add(ChatData data){
        data.setSn();
        if(ringQue.contains(data)){
            return data;
        }
        ringQue.add(data);
        addElement(data);
        return data;
    }
//    public data add(String text,File file,Integer sn){
//        data data=createData(text,file);
//        data.setSn(sn);
//        if (ringQue.size()==ringQue.cap){
//            if (!ringQue.contains(data)){
//                ringQue.poll();
//                ringQue.add(data);
//                addElement(data);
//            }
//        }else {
////            throw new Exception("yjcz");
//        }
//        return data;
//    }

    public void remove( ChatData data){
        ringQue.remove(data);
        reMoveElement(data);
    }

    public ChatData elementToData(Element element){
        ChatData data=new ChatData();
        Attribute attribute;
        attribute=element.attribute("d");
        if (attribute!=null){
            data.date=attribute.getValue();
        }
        attribute=element.attribute("t");
        if (attribute!=null) {
            data.text=attribute.getValue();
        }
        attribute=element.attribute("f");
        if (attribute!=null){
            data.file=attribute.getValue();
        }
        attribute=element.attribute("u");
        if (attribute!=null){
            data.u=attribute.getValue();
        }
        attribute=element.attribute("n");
        if (attribute!=null){
            data.n= Integer.parseInt(attribute.getValue());
        }
        return data;
    }

    public Element addElement(ChatData data){
        Element element;
        String text=XmlParser.escapeSpecialCharactersForXml(data.text);
        if (data.file==null){
            element=root.addElement("t");
            element.addAttribute("d",data.date);
            element.addAttribute("u",data.u);
            element.addAttribute("t",text);
            element.addAttribute("n", String.valueOf(data.n));
        }else {
            element=root.addElement("f");
            element.addAttribute("u",data.u);
            element.addAttribute("d",data.date);
            element.addAttribute("f",data.file);
            element.addAttribute("t",text);
            element.addAttribute("n", String.valueOf(data.n));
        }
        XmlParser.SaveXml(root.getDocument(), XmlCreate.userChat+username+".xml");
        return element;
    }
    public Element reMoveElement(ChatData data){
        List<Element> elements=root.elements();
        Element element = null;
        for (Element e:elements){
            if (e.attribute("u").getValue().equals(data.u)){
            if (e.attribute("d").getValue().equals(data.date)){
                if (e.attribute("t").getValue().equals(data.text)){
                    if (data.file==null||"undefined".equals(data.file)||"".equals(data.file)){
                        root.remove(e);
                       element=e;
                       break;
                    }else {
                        if (Tool.esc(e.attribute("f").getValue()).equals(Tool.esc(data.file))){
                            root.remove(e);
                            element=e;
                            break;
                        }
                    }
                }
            }}
        }
        XmlParser.SaveXml(root.getDocument(), XmlCreate.userChat+username+".xml");
        return element;
    }


//
//    public static class data{
//        public String u;
//        //记录所属组
//        public int n=0;
//        public String sn;
//        public String date;
//        public String text;
//        public int tl;
//        public String file;
//        public int fl;
//        //状态字1为删除
//        public int i;
//
//        private data(){}
////        public data(String date,String text){
////            this.date=date;
////            this.text=text;
////        }
//        public data(String user,String date,String text,String file){
//            this.u=user;
//            this.date=date;
//            this.text=text;
//            this.file=file;
//        }
//
//        @Override
//        public boolean equals(Object o) {
//            if (this == o) return true;
//            if (o == null || getClass() != o.getClass()) return false;
//            data data = (data) o;
//            if (sn!=null&& data.sn!=null){
//                if(sn.equals(data.sn)){
//                    return true;
//                };
//            }
//            if (data.file==null){
//                return Objects.equals(date, data.date) &&
//                        Objects.equals(n, data.n) &&
//                        Objects.equals(text, data.text)&&
//                        Objects.equals(u, data.u);
//            }else {
//                return Objects.equals(date, data.date) &&
//                        Objects.equals(n, data.n) &&
//                        Objects.equals(text, data.text) &&
//                        Objects.equals(file, data.file) &&
//                        Objects.equals(u, data.u);
//            }
//
//        }
//
//        public void setSn(){
//            sn=String.valueOf(Objects.hash(text, file,u)&(((long)date.hashCode())<<24));
//        }
//
//        @Override
//        public int hashCode() {
//            if (sn==null){
//                setSn();
//            }
//            return sn.hashCode();
//        }
//        public data copy(){
//            data data=new data();
//            data.date=date;
//            data.text=text;
//            data.file=file;
//            data.fl=fl;
//            data.tl=tl;
//            data.sn=sn;
//            data.u=u;
//            data.n=n;
//            return data;
//        }
//
//        @Override
//        public String toString() {
//            return "data{" +
//                    "u='" + u + '\'' +
//                    ", n=" + n +
//                    ", sn=" + sn +
//                    ", date='" + date + '\'' +
//                    ", text='" + text + '\'' +
//                    ", file='" + file + '\'' +
//                    '}';
//        }
//    }


    public static Document createChatDoc(File file){
        Document document= DocumentHelper.createDocument();
        Element root=document.addElement("chat");//添加根节点
        FileOutputStream fileOutputStream=null;
        XMLWriter writer=null;
        SAXReader saxReader=null;
        root.addAttribute("t","");
        FileInputStream fileInputStream=null;
        try {
            if (!file.exists()){
                file.createNewFile();
            }
            fileOutputStream=new FileOutputStream(file);
            writer=new XMLWriter(fileOutputStream);
            writer.write(document);
            writer.close();
            fileInputStream=new FileInputStream(file);
            saxReader=new SAXReader();
            document=saxReader.read(fileInputStream);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return document;
    }


    public static Document ChatRead(File file,RingQue ringQue,String data) throws Exception {
        RandomAccessFile randomAc=new RandomAccessFile(file,"r");
//        randomAc


        Document document= DocumentHelper.createDocument();
        Element root=document.addElement("chat");//添加根节点
        FileOutputStream fileOutputStream=null;
        XMLWriter writer=null;
        SAXReader saxReader=null;
        root.addAttribute("t","");
        FileInputStream fileInputStream=null;
        try {
            if (!file.exists()){
                file.createNewFile();
            }
            fileOutputStream=new FileOutputStream(file);
            writer=new XMLWriter(fileOutputStream);
            writer.write(document);
            writer.close();
            fileInputStream=new FileInputStream(file);
            saxReader=new SAXReader();
            document=saxReader.read(fileInputStream);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return document;
    }
}
