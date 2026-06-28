package superlink.udpbind.cloude.operta;

import com.alibaba.fastjson2.JSON;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.datalen.DataReqAuto;
import superlink.udpbind.cloude.CloudBin;
import superlink.udpbind.cloude.CloudLocal;
import superlink.udpbind.cloude.CloudPage;
import superlink.udpbind.cloude.FileTrigger;
import superlink.udpbind.cloude.operta.broadcast.Operta;
import superlink.udpbind.cloude.util.TendFactory;
import superlink.udpbind.cloude.util.TendMap;
import superlink.util.SHAutils;
import superlink.util.Tool;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static superlink.util.Tool.copyFileIfNotExists;

/*
* 自动根据Cloudpag获取文件
* 无条件接收数据
* 选择接受数据删除
* 发布到页
* */
public class Server extends Operta {

    public boolean delbool=false;
    public Server(){
        this.opertaFutrue=new OpertaFutrue(3);
    }
    @Override
    public void run() {
        thread=Thread.currentThread();
        thread.setName(this.getClass().getName());
        Map<String, CloudBin> mapbin = CloudLocal.getSynContainer().Mapbin;
        Map<String, Set<FileTrigger.TargetFile>> mapBuffer=Operta.UpDateListMapBuffer();

        mapBuffer.forEach((k, v) -> {
            Iterator<FileTrigger.TargetFile> iterator = v.iterator();
            while (iterator.hasNext()) {


                FileTrigger.TargetFile targetFile = iterator.next();
                System.out.println("Brows Change: " + JSON.toJSONString(targetFile));
                String pathKey=targetFile.root+"/"+targetFile.target;
                CloudBin cloudBin = mapbin.get(k);
                if (cloudBin == null) {
                    continue;
                }
                CloudPage cloudPage = cloudBin.synMap.get(new File(pathKey));
                if (cloudPage==null){
                    continue;
                }
                if (targetFile.syb == 0) {
                    try {
                        cloudPage.list.remove(targetFile.path);
                        cloudPage.removeNode(targetFile.path);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (delbool){
                        TendMap tendMap = TendFactory.getTm(k);
//                            List<String> strings = tendMap.get(targetFile.getAbsoluteTargetPath().getPath());
                        List<String> strings=tendMap.get(pathKey);
                        for (String filename:strings){
                            File file = new File(filename +"/"+ targetFile.getNotTargetPath());
                            try {
                                Files.delete(file.toPath());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    continue;
                } else if (targetFile.syb == 1||targetFile.syb==-2) {
                    try {
                        TendMap tendMap = TendFactory.getTm(k);
                        List<String> strings=tendMap.get(pathKey);
                        if (strings != null) {
                            //todo
                            //CloudDataTranser transer = map.get(k);transer.rev();
                            File exists=null;
                            //CloudDataTranser transer = map.get(k);transer.rev();
                            for (String pathname:strings){
                                File file= new File(pathname+"/"+targetFile.getNotTargetPath());
                                if(file.exists()){
                                    String filehash= SHAutils.getShaFromFile(file.getAbsolutePath(),SHAutils.MD_5,false);
                                    if(filehash.equals(targetFile.hash)){
                                        exists=file;
                                        break;
                                    }
                                }
                            }
                            if(exists==null){
                                String hash;
                                int r=0;
                                DataReqAuto dataReqAuto =new DataReqAuto(k);
                                Object fileobject=null;
                                do {
                                    fileobject= dataReqAuto.reqFile(targetFile.getATP());
                                    dataReqAuto.clear();
                                    hash= DataReqAuto.getHash(fileobject);
                                    if (r>3){
                                        System.out.println("md5 mistake");
                                        break;
                                    }
                                    r++;

                                }while (!hash.equals(targetFile.hash));
                                if (r>3){
                                    System.out.println("md5 mistake");
                                    continue;
                                }
                                //存储到本地的路径
                                for (String pathname:strings){
                                    File file= Tool.createFile(pathname,pathname+"/"+targetFile.getNotTargetPath());
//                                        file.createNewFile();
                                    DataReqAuto.writdata(file.getAbsolutePath(),fileobject);
                                }
                            }else {
                                for (String pathname:strings){
                                    File file=new File(pathname+"/"+targetFile.getNotTargetPath());
                                    copyFileIfNotExists(exists,file);
//                                        file.createNewFile();
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    cloudPage.list.add(targetFile.path);
                    cloudPage.addNode(targetFile.path);
                    continue;
                } else if (targetFile.syb == 2) {
                    try {
                        TendMap tendMap = TendFactory.getTm(k);

                        List<String> strings=tendMap.get(pathKey);
                        if (strings != null) {
                            File exists=null;
                            //CloudDataTranser transer = map.get(k);transer.rev();
                            for (String pathname:strings){
                                File file= new File(pathname+"/"+targetFile.getNotTargetPath());
                                if(file.exists()){
                                    String filehash= SHAutils.getShaFromFile(file.getAbsolutePath(),SHAutils.MD_5,false);
                                    if(filehash.equals(targetFile.hash)){
                                        exists=file;
                                        break;
                                    }
                                }
                            }
                            if(exists==null){
                                String hash;
                                int r=0;
                                DataReqAuto dataReqAuto =new DataReqAuto(k);
                                Object fileobject=null;
                                do {
                                    fileobject= dataReqAuto.reqFile(targetFile.getATP());
                                    dataReqAuto.clear();
                                    hash= DataReqAuto.getHash(fileobject);
                                    if (r > 3) {
                                        System.out.println("md5 mistake");
                                        break;
                                    }
                                    r++;

                                } while (!hash.equals(targetFile.hash));
                                if (r > 3) {
                                    System.out.println("md5 mistake");
                                }

                                //存储到本地的路径
                                for (String filename : strings) {

                                    String prex = filename +"/"+ targetFile.getNotTargetPath();
                                    String filehash = SHAutils.getShaFromFile(prex, SHAutils.MD_5, false);
                                    if (filehash.equals(hash)) {
                                        continue;
                                    }
                                    File file = new File(prex);
                                    DataReqAuto.writdata(file.getAbsolutePath(),fileobject);
    //
                                }
                            }else {
                                for (String filename : strings) {
                                    String prex = filename +"/"+ targetFile.getNotTargetPath();
                                    String filehash = SHAutils.getShaFromFile(prex, SHAutils.MD_5, false);
                                    if (filehash.equals(exists)) {
                                        continue;
                                    }
                                    File file = new File(prex);
                                    copyFileIfNotExists(exists,file);
                                    //
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    continue;
                }

//                    v.remove(targetFile);
                UserContext userContext = UDPclient.getUser(k);
                if (userContext!=null){
                    Operta.put(k,targetFile);
                }
                Operta.put(k,targetFile);
            }
            //CloudDataTranser cloudDataTranser = map.get(k);
//                for (FileTrigger.TargetFile t : v) {
//                    cloudDataTranser.rev();
//                }
        });

    }
}
