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
import java.nio.file.Files;
import java.util.*;

import static superlink.util.Tool.copyFileIfNotExists;

/*
* 无条件接收
* 传输删除信息
* 广播发布和删除
* */
public class Consist extends Operta {

    public Consist(){
        this.opertaFutrue=new OpertaFutrue(1);
    }
    @Override
    public void run() {
        thread=Thread.currentThread();
        thread.setName(this.getClass().getSimpleName());

        Map<String, CloudBin> mapbin = CloudLocal.getSynContainer().Mapbin;

        Map<String, Set<FileTrigger.TargetFile>> mapBuffer=Operta.UpDateListMapBuffer();

        mapBuffer.forEach((k, v)->{
            Iterator<FileTrigger.TargetFile> iterator = v.iterator();
            while (iterator.hasNext()) {
                FileTrigger.TargetFile targetFile=null;
                try {
                    targetFile = iterator.next();
                }catch (Exception e){
                    System.out.println(e.getMessage());
                    continue;
                }
                System.out.println("Opera: "+ JSON.toJSONString(targetFile));

                CloudBin cloudBin = mapbin.get(k);
                if (cloudBin==null){
                    continue;
                }
                String pathKey = targetFile.root+"/"+targetFile.target;
                File fpk=new File(pathKey);
                CloudPage cloudPage = cloudBin.synMap.get(fpk);
                TendMap tendMap=TendFactory.getTm(k);

                if (targetFile.syb==0){
                    try {
//                            File fil=new File(targetFile.root+"/"+targetFile.path);
//                            if(fil.exists()){
//                                fil.delete();
//                            }
                        if (cloudPage!=null){
                            cloudPage.list.remove(targetFile.path);
                            cloudPage.removeNode(targetFile.path);
                        }

                        List<String> strings=tendMap.get(pathKey);//无/
                        if (strings!=null){
                            for (String filePath:strings){
                                File file=new File(filePath+targetFile.getNotTargetPath());
                                if (file.exists()){
                                    Files.delete(file.toPath());
                                }
                            }
                        }
//                            iterator.remove();
                        continue;
                    }catch (Exception e){
                        e.printStackTrace();
                        if (cloudPage==null){
                            continue;
                        }
                    }

                }else if(targetFile.syb==1){//add
                    try {
                        List<String> strings=tendMap.get(pathKey);
                        if (targetFile.hash!=null){
                            File exists=null;

                            if (strings!=null){
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
                            else {
                                for (String pathname:strings){
                                    File file=new File(pathname+"/"+targetFile.getNotTargetPath());
                                    file.mkdirs();
                                }
                            }
                            cloudPage.list.add(targetFile.path);
                            cloudPage.addNode(targetFile.path);
                        }else {
                            for (String pathname:strings){
                                File file=new File(pathname+"/"+targetFile.getNotTargetPath());
                                file.mkdirs();
                            }
                        }

//                            iterator.remove();
                        continue;
                    }catch (Exception e){
                        e.printStackTrace();
                    }


                }else if(targetFile.syb==2){//change
                    try {

                        List<String> strings=tendMap.get(pathKey);
                        if (strings!=null){
                            File exists=null;
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
//                                CloudDataTranser transer=map.get(k);
//                                //todo
//                                byte[] bytes=transer.rev();
                            if(exists==null){
                                String hash;
                                int r=0;
                                DataReqAuto dataReqAuto =new DataReqAuto(k);
                                Object fileobject=null;
                                do {

                                    fileobject= dataReqAuto.reqFile(targetFile.getATP());
                                    dataReqAuto.clear();
                                    hash= DataReqAuto.getHash(fileobject);
//                                    DataTool   dataTool=new DataTool(k,id);
//                                    dataTool.receiveData(targetFile.getFileName());
//                                    bytes = dataTool.recive;
//                                    hash= SHAutils.getShaFromByte(bytes,SHAutils.MD_5,false);
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

                                    String filePath=pathname+"/"+targetFile.getNotTargetPath();
                                    String filehash= SHAutils.getShaFromFile(filePath,SHAutils.MD_5,false);
                                    if (filehash.equals(hash)) {
                                        continue;
                                    }
                                    File file=new File(filePath);
                                    DataReqAuto.writdata(file.getAbsolutePath(),fileobject);
//                                    OutputStream outputStream=new FileOutputStream(file);
//                                    outputStream.write(bytes);
//                                    outputStream.close();
                                }
                            }else {
                                for (String pathname:strings){
                                    File file=new File(pathname+"/"+targetFile.getNotTargetPath());
                                    copyFileIfNotExists(exists,file);
//                                        file.createNewFile();
                                }
                            }

                        }
//                            iterator.remove();
                        continue;
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    continue;
                }
                UserContext userContext = UDPclient.getUser(k);
                if (userContext!=null){
                    Operta.put(k,targetFile);
                }
            }
//                CloudDataTranser cloudDataTranser=map.get(k);
//                for (FileTrigger.TargetFile t:v){
//                    cloudDataTranser.rev();
//                }
        });

//            Thread.interrupted();
//            try {
//                Thread.sleep(30*1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
    }

}
