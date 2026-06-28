package superlink.udpbind.handle.handler;

import org.dom4j.Document;
import org.dom4j.Element;
import superlink.filemanage.scanpackage.FileScan;
import superlink.filemanage.xmltool.UserGet;
import superlink.filemanage.xmltool.XmlCreate;
import superlink.init.InitClass;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.data.DataTool1;
import superlink.udpbind.cloude.*;
import superlink.udpbind.cloude.util.TendFactory;
import superlink.udpbind.cloude.util.TendMap;
import superlink.udpbind.handle.process.HandlerProcsee;

import java.io.File;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import static superlink.filemanage.xmltool.XmlCreate.createAutoMapXml;
import static superlink.filemanage.xmltool.XmlParser.byetToDocument;
import static superlink.udpbind.client.UDPclient.mainDataQueue;

public class ReqCloudeAutoMap implements HandlerProcsee,Runnable {
    String name;
    int id;

    boolean sy=true;

    public static HandlerProcsee getInstance(String name, int id){
        ReqCloudeAutoMap dealCloudeAutoMap=null;
        if (DealCloudeAutoMap.mapMap.get(name)==null){
            dealCloudeAutoMap=new ReqCloudeAutoMap(name,id);
            DealCloudeAutoMap.mapMap.put(name,dealCloudeAutoMap);
        }
        return DealCloudeAutoMap.mapMap.get(name);
    }


    public ReqCloudeAutoMap(String name, int id){
        this.name=name;
        this.id=id;
    }
    public ReqCloudeAutoMap(String name, int id,boolean sy){
        this.name=name;
        this.id=id;
        this.sy=sy;
    }
    public static boolean st=true;

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
        CludeProcess(name,id);
    }
//    SelectNodes("//item[@name='111']")
//    在 SelectNodes("//item[@name]") 的基础上，增加了一个限制，就是要求 name 属性值为 111。
//    注意语法中有引号；如果没有引号，则表示是数字类型，对于数字类型可以使用大于号、小于号等，比如：SelectNodes("//item[@v>333]")。
//    SelectNodes("//item[1]")


    public TendMap conTendMap(List<List> out,String abo,List<List> loacl,String abl){
        TendMap tendMap=new TendMap();
        List<MapCon> l0;
        List<MapCon> o0;
        StringBuilder stringBuildero;
        StringBuilder stringBuilderl = null;
        for (int i = 0; i <3 ; i++) {
            l0=loacl.get(i);
            o0=out.get(i);
            for (MapCon mapCon:l0){
                for (MapCon con:o0){
                    if (mapCon.equalss(con)){
                        stringBuilderl=mapCon.getPath();
                        stringBuildero=con.getPath();
                        stringBuilderl.insert(0,"/").insert(0,abl);
                        stringBuildero.insert(0,"/").insert(0,abo);
                        tendMap.put(stringBuildero.toString(),stringBuilderl.toString());
                    }
                }
            }
        }
        return tendMap;
    }
    public TendMap conTendMap1(List<List> out,String abo,List<List> loacl,String abl){
        TendMap tendMap=new TendMap();
        List<MapCon> l0;
        List<MapCon> o0;
        StringBuilder stringBuildero;
        StringBuilder stringBuilderl = null;
        for (int i = 0; i <3 ; i++) {
            l0=loacl.get(i);
            o0=out.get(i);
            for (MapCon mapCon:l0){
                for (MapCon con:o0){
                    if (mapCon.equals(con)){
                        stringBuilderl=mapCon.getPath();
                        stringBuildero=con.getPath();
                        stringBuilderl.insert(0,"/").insert(0,abl);
                        stringBuildero.insert(0,"/").insert(0,abo);
                        tendMap.put(stringBuildero.toString(),stringBuilderl.toString());
                    }
                }
            }
        }
        return tendMap;
    }

    public List getPathCompare(Element root){
        List<List> list=new ArrayList<>(3);
        List<MapCon> list0=new ArrayList<>();
        List<MapCon> list1=new ArrayList<>();
        List<MapCon> list2=new ArrayList<>();
        list.add(list0);
        list.add(list1);
        list.add(list2);
        for (Element element0: (List<Element>)root.elements()){
            String s0=element0.attributeValue("p");
            List<Element> elements0=element0.elements();
            if (elements0.size()!=0){
                for (Element element1:elements0){
                    String s1=element1.attributeValue("p");
                    List<Element> elements1=element1.elements();
                    if (elements1.size()>0){
                        for (Element element2:elements1){
                            MapCon mapCon=new MapCon(s0);
                            mapCon.addList(s1);
                            mapCon.addList(element2.attributeValue("p"));
                            list2.add(mapCon);
                        }
                    }else {
                        MapCon mapCon=new MapCon(s0);
                        mapCon.addList(s1);
                        list1.add(mapCon);
                    }
                }
            }else {
                MapCon mapCon=new MapCon(s0);
                list0.add(mapCon);
            }
        }
        return list;
    }

    public void udpReciveCache(HashMap<String,byte[]> map, String name, String fliename) {
        UserContext userContext= mainDataQueue.getUserContext(name);
        int id=userContext.newQueue();
        DataTool1 dataTool= new DataTool1(name,id);
        String filedata=dataTool.receiveData(fliename,"Dt");
        map.put(filedata,dataTool.recive);
        dataTool.finalize(dataTool);
    }

    ReentrantLock reentrantLock=new ReentrantLock();
    public  void CludeProcess(String name, int id){
        DataTool1 dataTool= null;
        String filename=XmlCreate.userCloudecache +"Auto.xml";
        try {
            dataTool = UserpageRecive(name,id,filename);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        new ReqCloudeAutoMap.Processor(dataTool).run();
    }

    public DataTool1 dataTool = null;
    public  DataTool1  UserpageRecive(String name, int id,String fliename) throws Exception {
        reentrantLock.lock();
        dataTool= new DataTool1(name,id);
        String filedata=dataTool.receiveData(fliename,"RA");
        reentrantLock.unlock();
        if (filedata==null){
            return null;
        }
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
            Element element=createAutoMapXml();
            List<List> listList=getPathCompare(element);

            reentrantLock.lock();
            if (this.dataTool.recive==null){
                if (!CloudLocal.isInitSynContainer()){
                    CloudLocal.init(60*100*2);
                }
                CloudBin cloudBin=new CloudBin(this.dataTool.userContext);
                CloudLocal.getSynContainer().Mapbin.put(name,cloudBin);

                CloudeListenCaset.FactortCloudeLisentCaset(UserGet.cloudSynSymbol).start() ;

                reentrantLock.unlock();
                return;
            }

            Document document=byetToDocument(this.dataTool.recive);
            List<List> list= getPathCompare(document.getRootElement());
            String name=this.dataTool.userContext.userName;
            TendMap tendMap=null;
            if (false){
                tendMap=conTendMap(list,document.getRootElement().attributeValue("r"),listList,InitClass.roots[InitClass.roots.length-1].getAbsolutePath());
            }else {
                tendMap=conTendMap1(list,document.getRootElement().attributeValue("r"),listList,InitClass.roots[InitClass.roots.length-1].getAbsolutePath());
            }
            tendMap.name=name;

            HashMap<String,byte[]> map=new HashMap<>();
            dataTool.finalize(dataTool);
            for (Map.Entry<String, TendMap.Node> entry:tendMap.map.entrySet()){
                //获取cloudefile：*s
                String path = ":cloudeCreate&:"+entry.getKey();
                try {
                    udpReciveCache(map,name,path);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            CloudBin cloudBin=new CloudBin(this.dataTool.userContext);

            CloudeSynContainer container= CloudLocal.getSynContainer();
            //map中存储的cloudePage目录
            map.entrySet().iterator().forEachRemaining(stringEntry -> {
                String key=stringEntry.getKey();
                byte[] valeu=stringEntry.getValue();
                try {
                    Document doc=byetToDocument(valeu);
                    Element ele= (Element) doc.getRootElement().elements().get(0);

                    String AbsolutePath=doc.getRootElement().attribute(0).getValue().replace("\\","/")
                            + '/'+ele.attribute(0).getValue();
                    File abFlie=new File(AbsolutePath);
                    CloudPage cloudPage=new CloudPage(doc,this.dataTool.userContext);
                    CloudPage cloudPageold=cloudBin.synMap.get(abFlie);
                    if (cloudPageold !=null){
                        cloudPageold.reSetCloudPage(cloudPage);
                    }else {
                        cloudBin.synMap.put(abFlie,cloudPage);
                    }
                }catch (Exception e){
                    System.out.println("mistake:  "+key);
                }
            });

            String[] localpath=tendMap.getInList();
            for (String s:localpath){
                FileScan.createXmls(s) ;
            }
            XmlCreate.createcloudeXml();
            CloudLocal.getSynContainer().reloadLocalBin();

            TendMap tendMapOri=TendFactory.getTm(this.dataTool.userContext.userName);

            //todo
            tendMap=TendFactory.mixTendMap(tendMapOri,tendMap);

            if (container==null){
                CloudLocal.init(60*100*2);
                container=CloudLocal.getSynContainer();}
            container.Mapbin.put(name,cloudBin);

//            container.Listbin.add(cloudBin);
            reentrantLock.unlock();

            CloudeListenCaset.FactortCloudeLisentCaset(0).start() ;

            TendFactory.setTmXml(tendMap);
        }
    }

    public static class MapCon{
        public List<String> list;
        public String name;
        public int mode;
        public MapCon(String s){
            list=new ArrayList<>();
            list.add(s);
            name=s;
        }
        public void setList(List<String> list) {
            this.list = list;
        }
        public void addList(String s) {
            this.list.add(s);
            this.name=s;
        }
        public String getName() {
            return name;
        }
        public StringBuilder getPath(){
            StringBuilder stringBuilder=new StringBuilder();
            for (String s:list){
                stringBuilder.append("/").append(s);
            }
            return stringBuilder;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MapCon mapCon = (MapCon) o;
            return Objects.equals(list, mapCon.list) &&
                    Objects.equals(name, mapCon.name);
        }
        public boolean equalss(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            MapCon mapCon = (MapCon) o;
            return  (Objects.equals(name,mapCon.getName()));
        }

        public int compare(MapCon mapCon){
            int i=list.size();
            int j=mapCon.list.size();
            if (i>j){
                return -1;
            }else {
                if (i==j){
                    return 0;
                }else {
                    return 1;
                }
            }
        }

        @Override
        public int hashCode() {
            return Objects.hash(list, name);
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
