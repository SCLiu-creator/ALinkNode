package superlink.udpbind.handle.handler;

import org.dom4j.Document;
import org.dom4j.Element;
import superlink.filemanage.xmltool.XmlParser;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.recives.datalen.DataReqAuto;
import superlink.udpbind.handle.process.HandlerProcsee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static superlink.filemanage.xmltool.XmlParser.byetToDocument;

public class ReqHandleMap implements HandlerProcsee,Runnable {
    String name;
    int id;
    String fliename=":map&:";

    boolean sy=false;
    public ReqHandleMap(String name, int id){
        this.name=name;
        fliename=fliename+name;
        this.id=id;
    }

    @Override
    public void process() {
        if (sy){
            this.run();
        }else {
            UDPclient.executorService.execute(this);
//            UDPclient.executorService.submit(this);
        }
    }
    @Override
    public void run() {
        CludeProcess(name, (short) id,fliename);
    }

    public  void CludeProcess(String name, short id,String fliename){
        new ReqHandleMap.Processor(name,id).run();
    }


    class Processor implements Runnable{
        //todo
        DataReqAuto dataReqAuto;
        public Processor(String name,short id){
            this.dataReqAuto =new DataReqAuto(name,id);
        }
        @Override
        public void run() {
            dataReqAuto.reqFile(fliename);
            if (this.dataReqAuto.rev==null){
                return;
            }

            Document document=byetToDocument(this.dataReqAuto.rev);
            List<String> list= XmlParser.parserXml(document);
            HashMap<String,byte[]> map=new HashMap<>();
            dataTool.finalize();



        }
    }


    public List<String> getcloudefile(Document document){
        List<String> list=new ArrayList<>();
        Element element=document.getRootElement();
        List<Element> elements=element.elements();
        for (Element e:elements){
            list.add((String) e.getData());
        }
        return list;
    }

}
