package superlink.udpbind.cloude.operta.unicast;

import com.alibaba.fastjson2.JSON;
import superlink.filemanage.xmltool.XmlParser;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.datalen.DataReqAuto;
import superlink.udpbind.cloude.CloudeListenCaset;
import superlink.udpbind.cloude.operta.broadcast.Operta;
import superlink.util.SHAutils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UseOperta extends Operta {
    public static Map<String, Set<OpertaFile>> setUnibuffer = new HashMap<>();;
    public static Map<String, Set<OpertaFile>> setUniSendbuffer = new ConcurrentHashMap<>();
    public OpertaFutrue opertaFutrue;

    public UseOperta() {
        this.opertaFutrue = new OpertaFutrue(0);
    }
//todo
    public static void addOpera(OpertaFile opertaFile,String username){
        Set<UseOperta.OpertaFile> set = UseOperta.setUnibuffer.get(username);
        if (set == null) {
            set = new HashSet<>();
            UseOperta.setUnibuffer.put(username, set);
        }
        set.add(opertaFile);
        UseOperta useOperta;
        if (CloudeListenCaset.cloudeListenCaset.useOperta==null){
            useOperta=CloudeListenCaset.cloudeListenCaset.opertaOn();
        }else {
            useOperta=CloudeListenCaset.cloudeListenCaset.useOperta;
        }
        if (CloudeListenCaset.cloudeListenCaset.useOperta.thread!=null){
            CloudeListenCaset.cloudeListenCaset.useOperta.thread.interrupt();
        }else {
            Operta.poolExecutor.execute(useOperta);
        }
    }

    public static void delete(String opertaUser, String opertaPath) {
        OpertaFile opertaFile = new OpertaFile();
        opertaFile.o = opertaPath;
        opertaFile.syb = 0;
        Set set = setUniSendbuffer.get(opertaUser);
        if (set == null) {
            set = new HashSet();
            set.add(opertaFile);
            setUniSendbuffer.put(opertaUser, set);
        } else {
            setUniSendbuffer.put(opertaUser, set);
        }
        CloudeListenCaset.cloudeListenCaset.dataCloud.immediate();
    }

    public static void change(String tagetPath, String opertaUser, String opertaPath) {
        OpertaFile opertaFile = new OpertaFile();
        opertaFile.t = tagetPath;
        opertaFile.o = opertaPath;
        opertaFile.syb = 1;
        Set set = setUniSendbuffer.get(opertaUser);
        if (set == null) {
            set = new HashSet();
            set.add(opertaFile);
            setUniSendbuffer.put(opertaUser, set);
        } else {
            setUniSendbuffer.put(opertaUser, set);
        }
        CloudeListenCaset.cloudeListenCaset.dataCloud.immediate();
    }

    public static void add(String tagetPath, String opertaUser, String opertaPath) {
        OpertaFile opertaFile = new OpertaFile();
        opertaFile.t = tagetPath;
        opertaFile.o = opertaPath;
        opertaFile.syb = 2;
        Set set = setUniSendbuffer.get(opertaUser);
        if (set == null) {
            set = new HashSet();
            set.add(opertaFile);
            setUniSendbuffer.put(opertaUser, set);
        } else {
            setUniSendbuffer.put(opertaUser, set);
        }
        CloudeListenCaset.cloudeListenCaset.dataCloud.immediate();
    }
    public static void add(OpertaFile opertaFile,String opertaUser) {
        Set set = setUniSendbuffer.get(opertaUser);
        if (set == null) {
            set = new HashSet();
            set.add(opertaFile);
            setUniSendbuffer.put(opertaUser, set);
        } else {
            setUniSendbuffer.put(opertaUser, set);
        }
        CloudeListenCaset.cloudeListenCaset.dataCloud.immediate();
    }

    public static void push(String targetUser, String tagetPath, String opertaUser, String opertaPath) throws Exception {
        DataReqAuto dataReqAuto = new DataReqAuto(targetUser);
        Object object = dataReqAuto.reqFile(tagetPath);
        dataReqAuto.clear();
        String filename = null;
        if (object instanceof File) {
            filename = ((File) object).getAbsolutePath();
        } else {
            filename = XmlParser.cloudecache + SHAutils.getMD5(tagetPath, true);
            DataReqAuto.writdata(filename, object);
        }
        OpertaFile opertaFile = new OpertaFile();
        opertaFile.t = filename;
        opertaFile.o = opertaPath;
        opertaFile.syb = 2;
        Set set = setUniSendbuffer.get(opertaUser);
        if (set == null) {
            set = new HashSet();
            set.add(opertaFile);
            setUniSendbuffer.put(opertaUser, set);
        } else {
            setUniSendbuffer.put(opertaUser, set);
        }
        CloudeListenCaset.cloudeListenCaset.dataCloud.immediate();
    }

    public static ConcurrentHashMap<UserContext.Task,Runnable> taskMap =new ConcurrentHashMap();
    public Thread thread;
    @Override
    public void run() {
        thread=Thread.currentThread();
        String string=thread.getName();
        thread.setName("UserOperta");
        while (setUnibuffer.size()!=0) {
            setUnibuffer.forEach((k, v) -> {
                Iterator<OpertaFile> iterator = v.iterator();
                while (iterator.hasNext()) {
                    OpertaFile opertaFile = iterator.next();
                    System.out.println("Brows Change: " + JSON.toJSONString(opertaFile));
                    switch (opertaFile.syb) {
                        case (0): {
                            try {
                                File file = new File(opertaFile.t);
                                file.delete();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                        case (1): {
                            File sFile = new File(opertaFile.o);
                            String file=sFile.getName();
                            File fileTarget = new File(opertaFile.t+"/"+file);

                            try {
//                                Files.copy(fileTarget.toPath(), sFile.toPath());
                                Files.copy(sFile.toPath(), fileTarget.toPath(),
                                        StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
                                sFile.delete();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            UserContext userContext=UDPclient.getUser(k);
                            userContext.getTask((short) opertaFile.bid).unLock();
                            break;
                        }
                        case (2): {
                            DataReqAuto dataReqAuto = null;
                            try {
                                dataReqAuto = new DataReqAuto(k);
                            } catch (Exception e) {
                                e.printStackTrace();
                                continue;
                            }
                            Object object = dataReqAuto.reqFile(opertaFile.t);
                            dataReqAuto.clear();
                            DataReqAuto.writdata(opertaFile.o, object);
                            break;
                        }
                        case (-1): {
                            UserContext userContext=UDPclient.mainDataQueue.getUserContext(opertaFile.ou);
                            UserContext userContext1=UDPclient.getUser(k);
                            if (userContext==null){
//                                if (userContext1.getTask(opertaFile.bid).block.mode<0){
//                                    continue;
//                                }else {
//                                    userContext1.getTask(opertaFile.bid).block.lockMode(-1);
//                                }
                                UserContext.Task task1=userContext1.getTask((short) opertaFile.bid);
                                int bid2=userContext1.newQueue();
                                UserContext.Task task2=userContext1.getTask((short) bid2);

                                OpertaFile opertaFile1=new OpertaFile();
                                opertaFile1.ou=opertaFile.ou;
                                opertaFile1.syb=-12;
                                opertaFile1.bid=bid2;
                                opertaFile1.o=opertaFile.o;
                                opertaFile1.t="cacheF:";
                                UseOperta.add(opertaFile1,k);

                                task2.block.lockMode(-1);
                                Runnable runnable=new Runnable() {
                                    @Override
                                    public void run() {
                                        DataReqAuto dataReqAuto = null;
                                        try {
                                            dataReqAuto = new DataReqAuto(k);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        Object object = dataReqAuto.reqFile("cacheF:&"+SHAutils.getMD5(opertaFile.t,false));
                                        dataReqAuto.clear();
                                        DataReqAuto.writdata(opertaFile.t, object);

                                        task1.block.lockMode(1);
                                    }
                                };
                                taskMap.put(task2,runnable);
                            }else {
                                DataReqAuto dataReqAuto = null;
                                try {
                                    dataReqAuto = new DataReqAuto(userContext.userName);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Object object = dataReqAuto.reqFile(opertaFile.t);
                                dataReqAuto.clear();
                                DataReqAuto.writdata(opertaFile.t, object);
                                userContext1.getTask((short) opertaFile.bid).block.lockMode(1);
                            }
                            break;
//                            userContext1.getTask(opertaFile.bid). block.lockMode(1);
                        }
                        case (-12): {

                            DataReqAuto dataReqAuto = null;
                            try {
                                dataReqAuto = new DataReqAuto(k);
                            } catch (Exception e) {
                                e.printStackTrace();
                                continue;
                            }
                            Object object = dataReqAuto.reqFile(opertaFile.t);
                            dataReqAuto.clear();
                            DataReqAuto.writdata(opertaFile.o, object);
                            UserContext userContext=UDPclient.mainDataQueue.getUserContext(opertaFile.ou);
                            userContext.getTask((short) opertaFile.bid).block.lockMode(1);
                            break;
                        } default:{
                            break;
                        }
                    }

                    taskMap.forEach((task, run)->{
                        if (task.block.mode>0){
                            run.run();
                            taskMap.remove(task);
                        }
                    });
                    v.remove(opertaFile);
                }

//                try {
//                    Thread.sleep(10000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            });
            setUnibuffer.entrySet().removeIf(entry -> entry.getValue().size() == 0);

        }
        thread.setName(string);
        thread=null;
    }

    public static class OpertaFile {
        //0,delete;1,moveto;2,reqfile;-1,reFmoveto;-2,getReqfile
        public int bid;
        public String ou;
        public int syb;
        public long len;
        public String t;
        public String o;
        public Integer hash;

        @Override
        public boolean equals(Object o) {
            OpertaFile opertaFile=(OpertaFile)o;
            if (this.t.equals(opertaFile.t) && this.len == opertaFile.len && this.syb == opertaFile.syb) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            if (hash != null) {
                return hash;
            }
            hash = Objects.hash(ou, UDPclient.userlocal.username, t, syb, len);
            return hash;
        }

        public String getFileName() {
            return t;
        }
    }

}
