package superlink.filemanage.xmltool.operation;

import org.dom4j.Document;

import java.util.List;

public class CloudeXml implements Xml{
    private Document document;
    public CloudeXml(Document document){
        this.document=document;
    }

    @Override
    public List view() {
        return null;
    }

    public void add(String path){

    }
    public void del(String path){

    }
}
