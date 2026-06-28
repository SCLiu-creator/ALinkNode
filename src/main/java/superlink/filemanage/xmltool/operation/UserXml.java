package superlink.filemanage.xmltool.operation;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import superlink.filemanage.xmltool.XmlCreate;
import superlink.filemanage.xmltool.XmlParser;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

//
public class UserXml implements Xml{
    private Document document;
    public UserXml(Document document){
        this.document=document;
    }

    @Override
    public List view() {
        return null;
    }

    public void add(String path){
        File file=new File(path);
        if (file.isFile()){
            Element element=document.getRootElement().addElement("f");
            element.addAttribute("f",path);
            XmlParser.SaveXml(document, XmlCreate.userShow +".xml");
        }else {
            XmlParser.writeXml(document.getRootElement(),new File(path));
            XmlParser.SaveXml(document, XmlCreate.userShow +".xml");
        }


    }
    public void del(String path){
        List<Element> elements=document.getRootElement().elements();
        for (Element element:elements){
            List list=element.attributes();
            list.forEach(a->{
                if (((Attribute)a).getValue().equals(path)){document.getRootElement().remove(element);};
            });
        }
        elements.removeIf(element -> {
            List<Attribute> atts=element.attributes();
            return atts.stream().anyMatch(a->a.getValue().equals(path));
        });

        elements.stream().
                map(element->element.attributes().stream()).
                map(a->((Attribute)a).getValue().equals(path)).
                collect(Collectors.toList());

    }
}
