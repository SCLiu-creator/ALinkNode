package superlink.udpbind.chat;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import superlink.filemanage.xmltool.XmlCreate;
import superlink.udpbind.client.recives.data.RingQue;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static superlink.filemanage.xmltool.XmlParser.SaveXml;
import static superlink.filemanage.xmltool.XmlParser.writeXml;

public class ChatGroup {
    public String username;

    public static Element rootBin;

    public Map<Integer,ChatGs> chatGsMap=new HashMap<>();

    public static void init() {
        File file=new File(XmlCreate.userChat+'/'+"chats.xml");
        if (file.exists()){
            FileInputStream fileInputStream= null;
            try {
                fileInputStream = new FileInputStream(file);
                SAXReader saxReader=new SAXReader();
                Document document=saxReader.read(fileInputStream);
                rootBin=document.getRootElement();
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

    public Element root;

    public ChatGroup(String username){
        this.username=username;
        AtomicBoolean isExist= new AtomicBoolean(false);
        AtomicReference<Element> chat = new AtomicReference<>();
        ((List<Element>)rootBin.elements()).forEach(element -> {
            if(element.getQName("user")!=null){
                if (element.attributeValue("user").equals(username)){
                    isExist.set(true);
                    root=element;
                    chat.set(root);
                }
            }
        });
        if (!isExist.get()){
            chat.set(rootBin.addElement("user"));
            chat.get().addAttribute("user",username);
            root = chat.get();
        }else {
            for (Element element : (List<Element>) chat.get().elements()) {
                if (element.getQName("chat") != null) {
                    String num = element.attributeValue("num");
                    if (num != null) {
                        try {
                            String user=chat.get().attribute("user").getValue();
                            ChatGs chatGs = new ChatGs(element);
                            chatGs.username=user;
                            chatGsMap.put(Integer.parseInt(num),chatGs);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
    }

    public ChatGs createCGS(String name,int num){
        Element chat=root.addElement("chat");
        chat.addAttribute("name",name);
        chat.addAttribute("num",String.valueOf(num));
        try {
            ChatGs chatGs = new ChatGs(chat);
            chatGsMap.put(num,chatGs);
            chatGs.num=num;
            chatGs.username=username;
            chatGs.name = name;
            chatGs.root = chat;
            chat.addAttribute("name",name);
            chat.addAttribute("num",String.valueOf(chatGs.num));
            saveXml();
            return chatGs;
        }catch (Exception e){
            root.remove(chat);
            e.printStackTrace();
        }
        return null;
    }

//    public ChatGs addMember(String name,int  num){
//        ChatGs chatGs = getCGS(num);
//        Element memberEle=chatGs.addMember(name);
//        saveXml();
//        return createCGS(name,chatGsMap.size());
//    }

    public ChatGs getCGS(int id){
        return chatGsMap.get(id);
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
        SaveXml(rootBin.getDocument(),XmlCreate.userChat + '/' + "chats.xml");
    }
}
