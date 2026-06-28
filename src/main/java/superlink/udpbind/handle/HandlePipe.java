package superlink.udpbind.handle;

import org.dom4j.DocumentFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class HandlePipe {
    public static List<?> list;

    public void g(byte[] bytes){
        ByteArrayInputStream bais=new ByteArrayInputStream(bytes);
        DocumentBuilderFactory df=DocumentBuilderFactory.newInstance();

        DocumentFactory documentFactory=DocumentFactory.getInstance();
        org.dom4j.Document d=documentFactory.createDocument("");
        try {
            DocumentBuilder builder=df.newDocumentBuilder();
            Document document=builder.parse(bais);
            document.getDocumentElement().normalize();
            System.out.println(document.getDocumentElement().getNodeName());
            NodeList nodeList=document.getElementsByTagName("");
            nodeList.item(0).getTextContent();

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static ReentrantLock lock=new ReentrantLock();
    public static ConcurrentHashMap map=new ConcurrentHashMap();

    protected void process(){
        while (!lock.isLocked()){
            map.entrySet().forEach(o -> {
                Map.Entry m= (Map.Entry) o;
                String[] strings=m.getValue().getClass().toString().split("\\.");
                String cl=strings[strings.length-1];
                switch (cl){
                    case "":{}
                    case "1":{}
                    case "2":{}

                }

            });


        }

    }

}
