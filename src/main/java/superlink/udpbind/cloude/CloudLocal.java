package superlink.udpbind.cloude;

import org.dom4j.Document;
import org.dom4j.Element;
import superlink.udpbind.fileListen.FileListen;
import superlink.udpbind.cloude.operta.broadcast.Operta;
import superlink.udpbind.cloude.operta.unicast.UseOperta;
import superlink.util.ImageTool.ImageUtils;
import superlink.util.Utils;


import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static superlink.util.ImageTool.ImageUtils.generateFixedSizeImage;

public class CloudLocal {
    public static List<Document> localdocuments;
    private static final Object MAX = null;
    private static CloudeSynContainer synContainer;


    public CloudLocal setDoc(Document doc){
        for (Document document:localdocuments){
            String dir=document.getRootElement().attributeValue("p");
            if (!dir.equals(doc.getRootElement().attributeValue("p"))){
                localdocuments.add(doc);
                return this;
            }
        }
        return this;
    }

    public static synchronized void init(long time){
        if (CloudLocal.synContainer!=null){
            CloudLocal.synContainer.reloadLocalBin();
        }else {
            CloudLocal.synContainer=new CloudeSynContainer();
        }

        CloudeListenCaset caset=CloudeListenCaset.FactortCloudeLisentCaset();

        FileListen fileRunner=caset.fileRunner;
        if (fileRunner==null){
            FileListen finalFileRunner = CloudeListenCaset.cloudeListenCaset.setFileRunner(time);
            synContainer.localbin.map.forEach((path, trigger)->{
                if (finalFileRunner.isRun()){
                    finalFileRunner.addListenDirRuning(trigger);
                }else {
                    finalFileRunner.addListenDirStop(trigger);
                }
            });
        }else {
            fileRunner.ReSetTime(time);
        }
        caset.start();

        System.out.println("init Succeed");
    }

    //不启动本地监听
    public synchronized static void init(){
        if (CloudeListenCaset.cloudeListenCaset!=null && CloudLocal.synContainer!=null){
            System.out.println("init Succeeded");
            return;
        }
        if ( CloudLocal.synContainer==null ){
            CloudLocal.synContainer=new CloudeSynContainer();
        }

        CloudeListenCaset.FactortCloudeLisentCaset().getFileRunner();

    }

    public static CloudeSynContainer getSynContainer(){
        if (synContainer==null){
            synContainer=new CloudeSynContainer();
        }
        return synContainer;
    }
    public static boolean isInitSynContainer(){
        if (synContainer==null){
            return false;
        }else {
            return true;
        }
    }
    public static void clearSynContainer(){
        synContainer=null;

    }
    public synchronized static void CloudClear(){
        if (CloudeListenCaset.cloudeListenCaset!=null ){
            if (CloudeListenCaset.cloudeListenCaset.fileRunner!=null ){
                CloudeListenCaset.cloudeListenCaset.fileRunner.clearMonitor();
                CloudeListenCaset.cloudeListenCaset.fileRunner=null;
                CloudeListenCaset.cloudeListenCaset=null;
            }
        }
    }

    public static void createSmallImage(){
        if (synContainer.localbin==null){return;}
        CloudBin cloudBin=synContainer.localbin;
        cloudBin.map.forEach((k,v)->{
            FileTrigger fileTrigger=v;
            fileTrigger.pathlist.forEach(l->{
                ImageUtils.getImgObject().generateFixedSizeImage(fileTrigger.AbsolutePath+l);
            });
        });

    }
    public static void closeCloudeUser(String username){
        CloudLocal.synContainer.Mapbin.remove(username);
        Operta.listMapBuffer.remove(username);
        DataCloud.setMap.remove(username);
        UseOperta.setUniSendbuffer.remove(username);
        UseOperta.setUnibuffer.remove(username);
        Utils.dealsSend(username,"cc".getBytes());
    }

    public Element findnode(String filename){
        AtomicReference<Element> stack = new AtomicReference<>();
        Map<String, FileTrigger> maplocal=CloudLocal.synContainer.localbin.map;
        maplocal.forEach((k,v)->{
            Element element=v.document.getRootElement();
            LinkedList<List<Element>> liststack=new LinkedList<>();
            liststack.add(element.elements());

            while (true){
                List<Element> list=liststack.getLast();
                if(list.size()>0){
                    list.forEach(e->{
                        if (e.elements().size()>0){
                            liststack.add(e.elements());
                        }else {
                            if (filename.equals(e.attribute(0).getValue())){
                                stack.set(element);
                            }
                            list.remove(e);
                        }
                    });
                }else {
                    liststack.remove(liststack.size()-1);
                    if (filename.equals(element.attribute(0).getValue())){
                        stack.set(element);
                    }
                }
            }
        });
        return stack.get();
    }

    public String findnodelist(String filename){
        AtomicReference<String> stack = new AtomicReference<>();
        Map<String, FileTrigger> maplocal=CloudLocal.synContainer.localbin.map;
        maplocal.forEach((k,v)->{
            v.pathlist.forEach(l->{
                if (l.contains(filename)){
                    stack.set(l);
                }
            });
        });
        return stack.get();
    }


    public void slan(File file, Element element){
        try {
            File[] files=file.listFiles();
          List<Element> elements=element.elements();

            for (File f:files){
                if (f.isFile()){

                    slan(f,element);

                }

            }
        }catch (Exception o){
            o.printStackTrace();
        }

    }

}
