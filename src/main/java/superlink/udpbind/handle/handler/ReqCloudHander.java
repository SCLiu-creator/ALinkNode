package superlink.udpbind.handle.handler;

import superlink.filemanage.xmltool.XmlParser;
import org.dom4j.Document;
import org.dom4j.Element;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.cloude.*;
import superlink.udpbind.handle.process.HandlerProcsee;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.data.DataTool;

import java.io.File;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import static superlink.filemanage.xmltool.XmlParser.byetToDocument;
import static superlink.udpbind.client.UDPclient.mainDataQueue;

public class ReqCloudHander implements HandlerProcsee,Runnable {
    String name;
    int id;
    String fliename;

    boolean sy=false;
    public ReqCloudHander(String name, int id,String fliename){
        this.name=name;
        this.id=id;
        this.fliename=fliename;
    }
    public ReqCloudHander(String name, int id,String fliename,boolean sy){
        this.name=name;
        this.id=id;
        this.fliename=fliename;
        this.sy=sy;
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
        CludeProcess(name,id,fliename);
    }

    public void udpReciveCache(HashMap<String,byte[]> map, String name, String fliename) throws Exception {
        UserContext userContext= mainDataQueue.getUserContext(name);
//        userContext.getDataQue(this.id);
        int id=userContext.newQueue();
        DataTool dataTool= new DataTool(name,id);
        String filedata=dataTool.receiveData(fliename);
        map.put(filedata,dataTool.recive);
        dataTool.finalize();
    }

    ReentrantLock reentrantLock=new ReentrantLock();
   // CloudeSynContainer synContainer=new CloudeSynContainer();
    public  void CludeProcess(String name, int id,String fliename){
        //UserCloudFile
        DataTool dataTool= null;
        try {
            dataTool = UserpageRecive(name,id,fliename);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        //List<String> cludepath=getcloudefile(byetToDocument(dataTool.recive));
        new Processor(dataTool).run();
    }

    public DataTool dataTool = null;
    public  DataTool  UserpageRecive(String name, int id,String fliename) throws Exception {
//        UserContext userContext= mainDataQueue.getUserContext(name);
//        userContext.getDataQue(this.id);
        reentrantLock.lock();
        dataTool= new DataTool(name,id);
        String filedata=dataTool.receiveData(fliename);
//        try {
//            Utils.toFile(dataTool.recive,filedata);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        reentrantLock.unlock();
        return dataTool;
    }


    class Processor implements Runnable{
        //todo
        DataTool dataTool;
        public Processor(DataTool dataTool){
            this.dataTool=dataTool;
        }
        @Override
        public void run() {
            reentrantLock.lock();

            if (this.dataTool.recive==null){
                if (!CloudLocal.isInitSynContainer()){
                    CloudLocal.init(60*100*2);
                }
                CloudBin cloudBin=new CloudBin(this.dataTool.userContext);
//                CloudLocal.synContainer.Listbin.add(cloudBin);
                CloudLocal.getSynContainer().Mapbin.put(name,cloudBin);

                reentrantLock.unlock();
                return;
            }

            Document document=byetToDocument(this.dataTool.recive);
            List<String> list= XmlParser.parserXml(document);
            HashMap<String,byte[]> map=new HashMap<>();
            dataTool.finalize();
            for (String s:list){
                String name=this.dataTool.userContext.userName;
                //获取cloudefile：*s
                try {
                    udpReciveCache(map,name,s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            CloudBin cloudBin=new CloudBin(this.dataTool.userContext);
            //将来可以直接返回给前端
            //todo
            CloudeSynContainer container= CloudLocal.getSynContainer();
            //map中存储的cloudePage目录
            map.entrySet().iterator().forEachRemaining(stringEntry -> {
                String key=stringEntry.getKey();
                byte[] valeu=stringEntry.getValue();
                try {
//                    System.out.println(new String(valeu));
                    Document doc=byetToDocument(valeu);
                    Element element= (Element) doc.getRootElement().elements().get(0);

                    String AbsolutePath=doc.getRootElement().attribute(0).getValue().replace("\\","/")
                            + '/'+element.attribute(0).getValue();
                    File abFlie=new File(AbsolutePath);
                    CloudPage cloudPage=new CloudPage(doc,this.dataTool.userContext);
                    CloudPage cloudPageold=cloudBin.synMap.get(abFlie);
                    if (cloudPageold !=null){
                        cloudPageold.reSetCloudPage(cloudPage);

                    }else {
                        cloudBin.synMap.put(abFlie,cloudPage);
                    }
//                    for(CloudBin bin:container.Listbin){
//                        if (element.attribute(0).getValue().equals(bin.targetPath)) {
//                            Set<Object> set=new HashSet<>();
//                            //set.contains();
//                        }
//                    }
                }catch (Exception e){
//                    e.printStackTrace();
                    System.out.println("mistake:  "+key);
                    // reentrantLock.unlock();
                }
            });

            if (container==null){
                CloudLocal.init(60*100*2);
                container=CloudLocal.getSynContainer();}

            container.Mapbin.put(name,cloudBin);
//            container.Listbin.add(cloudBin);
            reentrantLock.unlock();


            CloudeListenCaset.FactortCloudeLisentCaset(0) ;

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