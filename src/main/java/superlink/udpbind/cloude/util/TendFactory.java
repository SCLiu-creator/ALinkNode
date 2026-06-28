package superlink.udpbind.cloude.util;

import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;
import superlink.filemanage.xmltool.UserGet;
import superlink.filemanage.xmltool.XmlParser;
import superlink.udpbind.cloude.CloudBin;
import superlink.udpbind.cloude.CloudLocal;

import java.io.File;
import java.util.*;

public class TendFactory {

    public static Map<String,TendMap> mapMap=new HashMap<>();
    /*{"AbsoluteTarget":["localAbsoluteTarget1","localAbsoluteTarget2"],
    "targe2":["p2","p3"],
    "targe3":["p2","p3"]
    }*/
    /*user->Tend->user->p->tp
    * */
    public static TendMap getTm(String username){
        if (mapMap.get(username)!= null){return mapMap.get(username);}
        TendMap tendMap=null;
        Element element=UserGet.user.element("Tend");
        if (element==null){
//            Element tend=UserGet.user.addElement("Tend");
//            Element cloudeUser=tend.addElement(username);
            CloudBin cloudBin=CloudLocal.getSynContainer().Mapbin.get(username);
            tendMap=new TendMap();
            tendMap.name=username;
//            cloudBin.synMap.forEach((k,v)->{
//                File file=new File(XmlParser.cloudedown+v.targetPath);
//                if (!file.exists()){
//                    file.mkdirs();
//                }
//                tendMap.put(k.getPath(),XmlParser.cloudedown+v.targetPath);
//            });
            setTmXml(tendMap);
            return tendMap;
        }else {
//            element=XmlParser.getSonElement(element,username);
            List<Element> elements=element.elements();
            for (Element e:elements){
                tendMap=getTm(e);
                tendMap.name=e.attribute("user").getValue();
                mapMap.put(tendMap.name,tendMap);
            }
            tendMap=mapMap.get(username);
            if (tendMap==null){
                tendMap=new TendMap();
                tendMap.name=username;
                setTmXml(tendMap);
            }
            return tendMap;
        }
    }


    public static TendMap getTm(Element element){
        TendMap tendMap=new TendMap();
        List<Element> keyElement=element.elements();
        for (Element key:keyElement){
            //CloudPage
            String path=key.attribute("p").getValue();
            ArrayList list=new ArrayList();
            List<Element> valueElement=key.elements();
            for (Element value:valueElement){
                //FileTigger
                String tp=value.attribute("tp").getValue();
                list.add(tp);
            }
            tendMap.put(path,list);
        }
        tendMap.name=element.attribute("user").getValue();
        return tendMap;
    }
//    public static TendMap getTm(String username){
//        return mapMap.get(username);
//    }

    public static Element setTmXml(TendMap tend){
        Element userElement=UserGet.user;
        Element tendElement = userElement.element("Tend");
        if (tendElement==null){
            tendElement=userElement.addElement("Tend");
        }
        Element username=XmlParser.getSonElementClear(tendElement,"user",tend.name);

        Element username1= tendElement.addElement("u").addAttribute("user",tend.name);
        Map<String,TendMap.Node> map=tend.map;
        Element finalUser = username1;
        map.entrySet().forEach(entry -> {
            String path=entry.getKey();
            List<String> paths=entry.getValue().get();
            Element pathElement=finalUser.addElement("p");
            pathElement.addAttribute("p",path);
            for (String p:paths){
                pathElement.addElement("q").addAttribute("tp",p);
            }
        });
        if (username!=null){tendElement.remove(username); }

        XmlParser.SaveXml(username1.getDocument(),XmlParser.dir+"userpage.xml");
        mapMap.put(tend.name,tend);
//        UserGet.setUser();

        return username;
    }

    public static Map<String,List<String>> getMapList(TendMap tendMap){
        Map<String,List<String>> listMap=new HashMap<>();

        tendMap.map.forEach((k,v)->{
            for (String s:v.to) {
                if (listMap.get(s)==null){
                    listMap.put(s,new ArrayList<>());
                }
            }
        });
        tendMap.map.forEach((k,v)->{
            for (String s:v.to) {
                if (listMap.get(s)!=null){
                    listMap.get(s).add(k);
                }
            }
        });
        return listMap;
    }

    public static TendMap mixTendMap(TendMap tendMap0,TendMap tendMap1){
        TendMap tendMap=new TendMap();
            if (Objects.equals(tendMap0.name,tendMap1.name)){
                tendMap.name=tendMap0.name;
                tendMap0.map.forEach((k,v)->{
                    tendMap.put(v.in,v.to);
                });
                tendMap1.map.forEach((k,v)->{
                    tendMap.put(v.in,v.to);
                });
            }
        return tendMap;
    }

}
