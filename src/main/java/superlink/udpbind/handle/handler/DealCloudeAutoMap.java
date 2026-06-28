package superlink.udpbind.handle.handler;

import superlink.filemanage.xmltool.UserGet;
import superlink.filemanage.xmltool.XmlCreate;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.recives.Senders;
import superlink.udpbind.client.recives.data.DataTool1;
import superlink.udpbind.cloude.*;
import superlink.udpbind.handle.process.HandlerProcsee;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class DealCloudeAutoMap implements HandlerProcsee,Runnable {
    String name;
    int id;
    String fliename;

    public static Map<String,HandlerProcsee> mapMap=new HashMap<>(); ;

    public static HandlerProcsee getInstance(String name, int id){
        DealCloudeAutoMap dealCloudeAutoMap=null;
        if (mapMap.get(name)==null){
            dealCloudeAutoMap=new DealCloudeAutoMap(name,id);
            mapMap.put(name,dealCloudeAutoMap);
        }
        return mapMap.get(name);
    }
    public static HandlerProcsee getInstance(String prex,String name, int id){
        DealCloudeAutoMap dealCloudeAutoMap=null;
        if (mapMap.get(name)==null){
            dealCloudeAutoMap=new DealCloudeAutoMap(prex,name,id);
            mapMap.put(name,dealCloudeAutoMap);
        }
        return mapMap.get(name);
    }
    public static DealCloudeAutoMap delInstance(String u){
        DealCloudeAutoMap dealCloudeAutoMap= (DealCloudeAutoMap) mapMap.remove(u);
        if (dealCloudeAutoMap!=null){
            dealCloudeAutoMap.dataTool.finalize(dealCloudeAutoMap.dataTool);
        }
        return dealCloudeAutoMap;
    }
    boolean sy=false;
    String prex;
    private DealCloudeAutoMap(String prex,String name, int id){
        this.name=name;
        this.id=id;
        this.prex=prex;
    }
    private DealCloudeAutoMap(String name, int id){
        this.name=name;
        this.id=id;
    }
    private DealCloudeAutoMap(String name, int id,boolean sy){
        this.name=name;
        this.id=id;
        this.sy=sy;
    }

    public boolean st=true;
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
        if (st){
            st=false;
            try {
                CludeProcess(name,id);
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                delInstance(name);
            }
        }else {
            Senders.Sends(name,id,prex.getBytes());
        }
    }

    ReentrantLock reentrantLock=new ReentrantLock();
    // CloudeSynContainer synContainer=new CloudeSynContainer();
    public  void CludeProcess(String name, int id){
        try {
            dataTool= new DataTool1(prex,name,id);
            XmlCreate.createAutoMapXml();
            dataTool.sendfile(XmlCreate.userCloudecache +"Auto.xml");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        new DealCloudeAutoMap.Processor(dataTool).run();
    }

    public DataTool1 dataTool = null;
    public  DataTool1  UserpageRecive(String name, int id,String fliename) throws Exception {
//        UserContext userContext= mainDataQueue.getUserContext(name);
//        userContext.getDataQue(this.id);
        reentrantLock.lock();
        dataTool= new DataTool1(name,id);
        String filedata=dataTool.receiveData(fliename,"Dt");
        reentrantLock.unlock();
        return dataTool;
    }


    class Processor implements Runnable{
        //todo
        DataTool1 dataTool;
        public Processor(DataTool1 dataTool){
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
                CloudLocal.getSynContainer().Mapbin.put(name,cloudBin);
                CloudeListenCaset caset=CloudeListenCaset.FactortCloudeLisentCaset(UserGet.cloudSynSymbol);
                if (caset.state==false){
                    CloudeListenCaset.cloudeListenCaset.start();
                }
                reentrantLock.unlock();
                return;
            }
        }
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DealCloudeAutoMap that = (DealCloudeAutoMap) o;
        if (id != that.id) return false;
        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + id;
        return result;
    }
}
