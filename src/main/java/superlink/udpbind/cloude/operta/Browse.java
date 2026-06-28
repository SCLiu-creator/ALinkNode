package superlink.udpbind.cloude.operta;

import com.alibaba.fastjson2.JSON;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.cloude.*;
import superlink.udpbind.cloude.FileTrigger;
import superlink.udpbind.cloude.operta.broadcast.Operta;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Browse extends Operta {
    public Browse(){
        this.opertaFutrue=new OpertaFutrue(0);
    }


    @Override
    public void run() {
        Map<String, CloudBin> mapbin = CloudLocal.getSynContainer().Mapbin;
        thread=Thread.currentThread();
        thread.setName(this.getClass().getName());

        Map<String, Set<FileTrigger.TargetFile>> mapBuffer=Operta.UpDateListMapBuffer();
        try {
            mapBuffer.forEach((k, v) -> {
                Iterator<FileTrigger.TargetFile> iterator = v.iterator();
                while (iterator.hasNext()) {
                    FileTrigger.TargetFile targetFile = iterator.next();
                    System.out.println("Brows Change: "+ JSON.toJSONString(targetFile));
//                    Utils.dealsSend(k,("TC"+JSON.toJSONString(targetFile)).getBytes());
                    String pathKey = targetFile.root +"/"+ targetFile.target;
                    CloudBin cloudBin = mapbin.get(k);
                   if (cloudBin==null){
                       continue;
                   }
                    CloudPage cloudPage = cloudBin.synMap.get(new File(pathKey));
                    if (targetFile.syb==0){
                        try {
                            cloudPage.list.remove(targetFile.path);
                            cloudPage.removeNode(targetFile.path);
                            continue;
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }else if(targetFile.syb==1){
                        if (targetFile.hash!=null){
                            cloudPage.list.add(targetFile.path);
                        }
                        cloudPage.addNode(targetFile.path);
                        continue;
                    }else {
                        continue;
                    }
                    UserContext userContext = UDPclient.getUser(k);
                    if (userContext!=null){
                        Operta.put(k,targetFile);
                    }
                    Operta.put(k,targetFile);
                }
            });
            Thread.interrupted();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
