package superlink.udpbind.handle.handler;

import superlink.filemanage.xmltool.XmlParser;
import org.dom4j.Document;
import superlink.udpbind.cloude.show.UserShowContainer;
import superlink.udpbind.handle.process.HandlerProcsee;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.data.DataTool;
import superlink.util.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import static superlink.filemanage.xmltool.XmlParser.byetToDocument;
import static superlink.udpbind.client.UDPclient.mainDataQueue;

public class ReqDirHandler implements HandlerProcsee,Runnable {
    String name;
    short id;
    String fliename;
    public ReqDirHandler(String name, int id,String fliename){
        this.name=name;
        this.id= (short) id;
        this.fliename=fliename;
    }

    @Override
    public void process() {
//        new Thread( this){}.start();
        run();
    }
    @Override
    public void run() {
        UserpageProcess(name,id,fliename);
    }

    public void udpReciveCache(HashMap<String,byte[]> map, String name, int id, String fliename) throws Exception {
        UserContext userContext= mainDataQueue.getUserContext(name);
        userContext.getDataQue((short)id);
        DataTool dataTool= new DataTool(name,id);
        String filedata=dataTool.receiveData(fliename);
        map.put(filedata,dataTool.recive);

    }

    ReentrantLock reentrantLock=new ReentrantLock();
    public  void UserpageProcess(String name, short id,String fliename){
        DataTool dataTool= null;
        try {
            dataTool = UserpageRecive(name,id,fliename);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        new Process(dataTool).run();
    }

    public DataTool dataTool = null;
    public  DataTool  UserpageRecive(String name, short id,String fliename) throws Exception {
        UserContext userContext= mainDataQueue.getUserContext(name);
        userContext.getDataQue(id);
        dataTool= new DataTool(name,id);
        dataTool.receiveData(fliename);
        String filename=XmlParser.cachepath+name;
        File file=new File(filename);
        file.mkdirs();
        try {
            Utils.toFile(dataTool.recive,filename+".xml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        DataTool data=dataTool;
        return data;
    }


    class Process implements Runnable{
        //todo
        DataTool dataTool;
        public Process(DataTool dataTool){
            this.dataTool=dataTool;
        }
        @Override
        public void run() {
            List<String> list = null;
            reentrantLock.lock();
            try {
                System.out.println(new String(this.dataTool.recive));

                Document document=byetToDocument(this.dataTool.recive);
                UserShowContainer.newInstance(this.dataTool.userContext.userName,document);
                List<String> charests=new ArrayList<>();
                charests.add("p");
                charests.add("f");
                list= XmlParser.parserXml(document.getRootElement(),charests);

                boolean b = false;
                if (!b){
                    return;
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                reentrantLock.unlock();
            }
            HashMap<String,byte[]> map=new HashMap<String,byte[]>();
            int id=this.dataTool.userContext.newQueue();
            for (String s:list){
                String name=this.dataTool.userContext.userName;
                try {
                    udpReciveCache(map,name,id,s);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //todo
            //将来应该直接返回给前端而不是cachepath
            map.entrySet().iterator().forEachRemaining(stringEntry -> {
                String key=stringEntry.getKey();
                byte[] valeu=stringEntry.getValue();
                try {
                    String[] strings=key.split("/");
                    key=strings[strings.length-1];
                    String dir="data\\cachepath\\"+dataTool.userContext.userName;
                    String path=XmlParser.cachepath+dataTool.userContext.userName+"/"+key;
                    //String path=XmlParser.cachepath+dataTool.userContext.user.username+"/cache_"+ SHAutils.getMD5(key,false);
                    File file=new File(path);
                    file.createNewFile();
                    FileOutputStream outputStream=new FileOutputStream(file);
                    outputStream.write(valeu);
                    outputStream.close();

                }catch (IOException e){
                    e.printStackTrace();
                   // reentrantLock.unlock();
                }
            });

        }
    }

}
