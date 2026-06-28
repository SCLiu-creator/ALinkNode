package superlink.udpbind.cloude.operta;

import com.alibaba.fastjson2.JSON;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.cloude.*;
import superlink.udpbind.cloude.operta.broadcast.Operta;
import superlink.udpbind.cloude.util.TendFactory;
import superlink.udpbind.cloude.util.TendMap;
import superlink.util.Utils;

import java.io.File;
import java.nio.file.Files;
import java.util.*;

/*
* 不广播删除
* 删除广播发布到页
* 一旦成功发布到其它端则在固定时长后删除
*
* */
public class Monitor extends Operta {

    public Monitor(){
        this.opertaFutrue=new OpertaFutrue(2);
    }

    long time=0;

    @Override
    public void run() {
        Map<String, CloudBin> mapbin = CloudLocal.getSynContainer().Mapbin;
        thread=Thread.currentThread();
        thread.setName(this.getClass().getName());
//        time++;
        Map<String, Set<FileTrigger.TargetFile>> mapBuffer=Operta.UpDateListMapBuffer();

        mapBuffer.forEach((k, v)->{
            Iterator<FileTrigger.TargetFile> iterator = v.iterator();
            while (iterator.hasNext()) {

                FileTrigger.TargetFile targetFile=null;
                CloudBin cloudBin;
                do {
                    try {
                        if (!iterator.hasNext()) {
                            return;
                        }
                        targetFile = iterator.next();
                        System.out.println("Opera: " + JSON.toJSONString(targetFile));
                    }catch (Exception e){
                        System.out.println(e.getMessage());
                    }

                    cloudBin = mapbin.get(k);
                } while(cloudBin == null);

                String pathKey = targetFile.root + "/" + targetFile.target;
                CloudPage cloudPage = cloudBin.synMap.get(new File(pathKey));
                TendMap tendMap= TendFactory.getTm(k);

                UserContext userContext = UDPclient.getUser(k);

                if (targetFile.syb==0){
                    try {
                        cloudPage.list.remove(targetFile.path);
                        cloudPage.removeNode(targetFile.path);

                        List<String> strings=tendMap.get(pathKey);
                        if (strings!=null){
                            for (String filename:strings){
                                File file=new File(filename+"/"+targetFile.getNotTargetPath());
                                Files.delete(file.toPath());
                            }
                        }
                        File file=new File(targetFile.root + "/" + targetFile.path);
                        if(file.exists()){
                            Files.delete(file.toPath());
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    continue;
                }else if(targetFile.syb==1){
                    try {
                        //接受到对向的广播保存信号
                        cloudPage.list.add(targetFile.path);
                        cloudPage.addNode(targetFile.path);
//                        userContext.stableSend(("TC"+JSON.toJSONString(targetFile)).getBytes());

                        List<String> strings=tendMap.get(pathKey);
                        if (strings!=null){

                            for (String pathRoot:strings){
                                String s=pathRoot+"/"+targetFile.getNotTargetPath();

                                FileTrigger fileTrigger=CloudLocal.getSynContainer().localbin.map.get(pathRoot);
                                fileTrigger.removeNode(s);
                                File file=new File(s);
                                if (file.exists()){
                                    Files.delete(file.toPath());
                                }
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    continue;
                }else if(targetFile.syb==2){
                    try {
                        List<String> strings=tendMap.get(pathKey);
                        if (strings!=null){
                            Utils.dealsSend(k,("TC"+JSON.toJSONString(targetFile)).getBytes());
                        }
                        continue;
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
//                    Utils.dealsSend(k,("TC"+JSON.toJSONString(targetFile)).getBytes());
//                    Utils.dealsSend(k,("TC"+JSON.toJSONString(targetFile)).getBytes());
//                    v.remove(targetFile);

                if (userContext!=null){
                    Operta.put(k,targetFile);
                }
                Operta.put(k,targetFile);
            }
//                CloudDataTranser cloudDataTranser=map.get(k);
//                for (FileTrigger.TargetFile t:v){
//                    cloudDataTranser.rev();
//                }
        });

        int num=0;
        ArrayList arrayList=new ArrayList();
        Set<Map.Entry<String,FileTrigger>> entrySet=CloudLocal.getSynContainer().localbin.map.entrySet();
        Iterator<String> iterator=null;
        for (Map.Entry<String,FileTrigger> entry:entrySet){
            FileTrigger fileTrigger=entry.getValue();
            iterator=fileTrigger.pathlist.iterator();
            while (iterator.hasNext() && num<256){
                FileTrigger.TargetFile targetFile=new FileTrigger.TargetFile();
                targetFile.root =fileTrigger.rootPath;
                targetFile.target=fileTrigger.targetPath;
                targetFile.path=iterator.next();
                targetFile.syb=3;
                arrayList.add(targetFile);
            }
        }
        CloudeListenCaset.cloudeListenCaset.dataCloud.sendque(arrayList);
        Thread.interrupted();
    }
}
