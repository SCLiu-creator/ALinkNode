package superlink.udpbind.chat;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import superlink.filemanage.xmltool.XmlCreate;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.recives.data.RingQue;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static superlink.filemanage.xmltool.XmlParser.SaveXml;

public class ChatGroupSelf {
    public String username;

    public static Element rootBin;

    public Map<Integer,ChatGs> chatGsMap=new HashMap<>();

    public ChatGroupSelf(){
        File file=new File(XmlCreate.userChat+'/'+"bin.xml");
        if (file.exists()){
            FileInputStream fileInputStream= null;
            try {
                fileInputStream = new FileInputStream(file);
                SAXReader saxReader=new SAXReader();
                Document document=saxReader.read(fileInputStream);
                rootBin=document.getRootElement();
                List<Element> list = rootBin.elements("chat");
                list.forEach(e -> {
                    try {
                        ChatGs chatGs = new ChatGs(e);
                        chatGs.username= UDPclient.userlocal.username;
                        chatGsMap.put(Integer.parseInt(e.attributeValue("num")),chatGs);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
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
            rootBin=document.getRootElement();
        }
    }


    public ChatGs createCGS(String name){
        Element chat=rootBin.addElement("chat");
        try {
            Random random = new Random();
            int i = 0;
            Integer d=null;
            while (true){
                if(i>100){
                    throw new Exception("guoduo");
                }
                d = random.nextInt();
                if (chatGsMap.containsKey(d)){
                    i++;
                }else {
                    break;
                }
            }
            chat.addAttribute("name",name);
            chat.addAttribute("num",String.valueOf(d));
            saveXml();
            ChatGs chatGs = new ChatGs(chat);
            chatGs.username= UDPclient.userlocal.username;
            chatGsMap.put(d,chatGs);
            chatGs.num=d;
            chatGs.name = name;
            chatGs.root = chat;
            return chatGs;
        }catch (Exception e){
            rootBin.remove(chat);
            e.printStackTrace();
        }
        return null;
    }

    public ChatGs getCGS(int id){
        return chatGsMap.get(id);
    }

    public ChatGs addMember(String name,int  num){
        ChatGs chatGs = getCGS(num);
        chatGs.addMember(name);
        saveXml();
        return chatGs;
    }

    public ChatData createData(String text,File file){
        ChatData data=null;
        for (ChatBin chatBin:ChatContrain.chatBinMap.values()){
            data = chatBin.createData(text,file);
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

    public void saveXml(){
        SaveXml(rootBin.getDocument(),XmlCreate.userChat + '/' + "bin.xml");
    }
}
