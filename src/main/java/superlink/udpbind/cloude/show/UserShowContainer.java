package superlink.udpbind.cloude.show;

import org.dom4j.Document;
import org.dom4j.Element;
import superlink.filemanage.xmltool.XmlParser;
import superlink.udpbind.client.UDPclient;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

import static superlink.filemanage.xmltool.XmlParser.showpath;

public class UserShowContainer {

    public static ShowBin showBin;

    public static Map<ShowBin,ShowBin> skipListMap=new ConcurrentSkipListMap<>();
    public static Map<String,ShowBin> showBinMap=new ConcurrentSkipListMap<>();

    public static ShowBin newInstance(String user, Document document){
        ShowBin showBin=new ShowBin(user,document);
        put(showBin);
        return showBin;
    }
    public static ShowBin getLocalShowBin(){
        ShowBin showBin=UserShowContainer.showBinMap.get(UDPclient.userlocal.username);
        if (showBin!=null){
            return showBin;
        }
        String name= showpath+ UDPclient.userlocal.username+".xml";
        File file=new File(name);
        Document document;
        if (!file.exists()){
            return null;
        }else {
            document= XmlParser.openXml(file.getAbsolutePath());
        }
        showBin=UserShowContainer.newInstance(UDPclient.userlocal.username,document);

        return showBin;
    }

    public static void put(ShowBin showBin){
        if(skipListMap.size()>20){
            Iterator iterator=  skipListMap.entrySet().iterator();
            ShowBin s= (ShowBin) iterator.next();
            iterator.remove();;
            showBinMap.remove(s.user);
        }
        showBinMap.put(showBin.user,showBin);
        skipListMap.put(showBin,showBin);
    }

    public static void writeXml(Element element, File pathFile, int i){
        try {
            if (i<=0){
                return;
            }
            File[] files=pathFile.listFiles();
            long ro=pathFile.lastModified();
            for (File file:files){
                if (file.isDirectory()){
                    Element e=element.addElement("p");
                    String s=file.getName();
                    e.addAttribute("p",s);
                    e.addAttribute("t",String.valueOf((file.lastModified()-ro)/1000));
                    e.addAttribute("T",String.valueOf(file.lastModified()/1000));
                    writeXml(e,file,i-1);
                }else {
                    Element e=element.addElement("f");
                    String s=file.getName();
                    e.addAttribute("f",s);
                    e.addAttribute("t",String.valueOf((file.lastModified()-ro)/1000));
                }
            }
        }catch (Exception e){
            System.out.println("writeXml  "+e.getMessage());
            System.out.println(pathFile);
        }
    }

}
