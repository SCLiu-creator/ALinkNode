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
import superlink.util.JackJson;
import superlink.util.SHAutils;
import superlink.util.Tool;
import superlink.util.datastack.DataLinkRW;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class ChatGs extends ChatBin {
    public RingQue<ChatData> ringQue;
    public List<String> members;
    public Element root;
    public int num;
    public String name;
    public DataLinkRW dataLinkRW;

    public ChatGs(Element eleChat) throws Exception {
        super();
        root = eleChat;
        name = eleChat.attribute("name").getValue();
        num = Integer.parseInt(eleChat.attribute("num").getValue());
        List<Element > elements = eleChat.elements("member");
        members=new ArrayList<>();
        for (Element element:elements){
            String u=element.attribute("user").getValue();
            members.add(u);
        }
        String hash = SHAutils.getSHA1(UDPclient.userlocal.username+num,false);
        String path = XmlCreate.userChat+hash;
        dataLinkRW = new DataLinkRW(new File(path));
        int s=30;
        ringQue=new RingQue<>(s);

        List<byte[]> list=dataLinkRW.read(-30,30);

        ChatData chatData;
        for (int i = list.size()-1; i >= 0; i--) {
            chatData= (ChatData) JackJson.toObject(new String(list.get(i)),ChatData.class);
            ringQue.add(chatData);
        }
    }
//    public ChatGs(){
//        int s=30;
//        ringQue=new RingQue<>(s);
//    }
    public Element addMember(String member) {
        for (Element memberEle:(List<Element>) root.elements("member")){
            if (memberEle.attribute("user").getValue().equals(member)){
                return memberEle;
            }
        }
        Element memberEle = root.addElement("member");
        memberEle.addAttribute("user",member);
        members.add(member);
        return memberEle;
    }

    public ChatData createData(String text,File file){
        ChatData data=new ChatData();
        data.n=num;
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

    public ChatData add(String text,File file) {
        ChatData data=createData(text,file);
        if (ringQue.size()==ringQue.cap){
            ringQue.poll();
        }
        ringQue.add(data);
        try {
            dataLinkRW.write(data.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
    public ChatData add(ChatData data){
        data.setSn();
        ringQue.add(data);
        try {
            dataLinkRW.write(data.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        try {
            dataLinkRW.del(data.toString().getBytes(),((bytes, target) -> {
                ChatData chatData= (ChatData) JackJson.toObject(new String(bytes),ChatData.class);
                if (data.equals(chatData)){
                    return bytes;
                }else {
                    return null;
                }
            }));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




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
