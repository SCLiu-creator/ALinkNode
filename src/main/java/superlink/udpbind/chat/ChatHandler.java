package superlink.udpbind.chat;

import com.alibaba.fastjson2.JSON;
import superlink.filemanage.xmltool.XmlCreate;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.Senders;
import superlink.udpbind.client.recives.datalen.AsyBuffer;
import superlink.udpbind.client.recives.datalen.DataReqAuto;
import superlink.udpbind.client.recives.datalen.DataBuffer;
import superlink.util.Utils;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import static superlink.udpbind.client.UDPclient.mainDataQueue;

public class ChatHandler implements Runnable{
    public static volatile ChatHandler chatHandler;

    public static volatile Map<ChatData, Set<String>> stringSetMap=new ConcurrentHashMap<>();
    public static volatile Map<ChatData, Set<String>> stringSetMapBuffer=new ConcurrentHashMap<>();

    public static void adddataBuffer(String u,ChatData data){
        Set<String> set = stringSetMapBuffer.computeIfAbsent(data, k -> new HashSet<>());
        set.add(u);
        getChatHandler().time=1000;
        chatHandler.chatInterrupt();
    }
    public static void removedataBuffer(String u,ChatData data){
        Set<String> set=stringSetMapBuffer.get(data);
        if(set==null){
            return;
        }
        set.remove(u);
        if (set.size()==0){
            stringSetMapBuffer.remove(data);
        }
    }

    public static void adddata(String u,ChatData data){
        Set<String> set = stringSetMap.computeIfAbsent(data, k -> new HashSet<>());
        set.add(u);
        getChatHandler().chatInterrupt();
    }
    public static void removedata(String u,ChatData data){
        ChatBin chatBin=ChatContrain.getChatBin(u);
        chatBin.remove(data);
    }
    public static ChatHandler getChatHandler(){
        if (ChatHandler.chatHandler==null){
            ChatHandler.chatHandler=new ChatHandler();
        }
        if (ChatHandler.chatHandler.thread==null ||
                !ChatHandler.chatHandler.thread.isAlive()||
                currentFuture==null|| currentFuture.isDone()){
            // 取消旧任务（如果存在）
            if (currentFuture != null && !currentFuture.isDone()) {
                currentFuture.cancel(false); // 不中断正在执行的任务
            }
            currentFuture = UDPclient.executorService.submit(ChatHandler.chatHandler);
        }else {
            chatHandler.chatInterrupt();
        }
        return ChatHandler.chatHandler;
    }
    public void chatInterrupt(){
        if (thread==null){
            currentFuture = UDPclient.executorService.submit(ChatHandler.chatHandler);
        }else {
            thread.interrupt();
        }
    }

    volatile Thread thread;
    public static Future<?> currentFuture;
    long time=30*100;
    @Override
    public void run() {
        if (thread!=null){
            return;
        }
        try {
            thread=Thread.currentThread();
            thread.setName("ChatHandlIng");
            long timeRc=System.currentTimeMillis();
            while (stringSetMapBuffer.size()>0 || stringSetMap.size()>0){
                if ((System.currentTimeMillis()-timeRc)>time){
                    stringSetMapBuffer.forEach((data,set)->{
                        for (String u:set){
//                    ChatBin chatBin=ChatContrain.getChatBin(u);
                            String send="CH"+JSON.toJSONString(data);
                            UserContext userContext = mainDataQueue.getUserContext(u);
                            if (userContext==null){
//                            Senders.Sends(UDPclient.getServerip(),UDPclient.getSport(),send.getBytes());
                            }else {
                                Senders.Sends(userContext.getBothId(),(short) 0,
                                        userContext.inetAddress,userContext.port,send.getBytes());
                            }
                        }
                    });
                    timeRc=System.currentTimeMillis();
                }
                try {
                    Iterator<Map.Entry<ChatData, Set<String>>> iteratorsm=stringSetMap.entrySet().iterator();
                    while (iteratorsm.hasNext()){
                        Map.Entry<ChatData, Set<String>> entry=iteratorsm.next();
                        ChatData data=entry.getKey();
                        Set<String> set=entry.getValue();
                        Iterator<String> iterator=set.iterator();
                        String u;
                        if (data.n==0){
                            while (iterator.hasNext()){
                                try {
                                    u= iterator.next();
                                    ChatBin chatBin= ChatContrain.getChatBin(u);
                                    if (chatBin.ringQue.contains(data)){
                                        iterator.remove();
                                        continue;
                                    }
                                    if (data.i==1){
                                        chatBin.remove(data);
                                        iterator.remove();
                                        continue;
                                    }

                                    if (data.tl>1000){
                                        Utils.PathSort pathSort=Utils.pathPrase(data.text);
//                                        DataBuffer dataBuffer=new DataBuffer(u);
                                        AsyBuffer asyBuffer = new AsyBuffer(u);
                                        String pp =  ":data&:"+pathSort.path;
                                        byte[] bytes=asyBuffer.reqData(pp);
                                        data.text=new String(bytes);
                                    }

                                    File file=null;
                                    if (data.file!=null&&data.file!=""){
                                        DataReqAuto dataReqAuto = null;
                                        Object o;
                                        String hash;
                                        try {
                                            dataReqAuto = new DataReqAuto(u);
                                            o= dataReqAuto.reqFile(data.file);
                                            //                            hash=DataAuto.getHash(o);
                                            //                            dataAuto.finalize(dataAuto);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            continue;
                                        }
                                        String path=new File(data.file).getName();
                                        String[] fs = path.split("\\.");
                                        String prex=null;
                                        String base=null;
                                        if (fs.length != 2) {
                                            prex="";
                                            base=path;
                                        }else {
                                            prex=fs[1];
                                            base=fs[0];
                                        }
                                        int times=1;
                                        File fileb=null;
                                        path=XmlCreate.userChat+base+"."+prex;
                                        while (true){
                                            fileb=new File(path);
                                            if(fileb.exists()){
                                                path=XmlCreate.userChat+base+"("+times+")."+prex;
                                                times++;
                                            }else {
                                                break;
                                            }
                                        }

                                        DataReqAuto.writdata(path,o);
                                        file=new File(path);
                                        data.file=file.getAbsolutePath();
                                    }
                                    chatBin.add(data);
                                }catch (NullPointerException n){
                                    n.printStackTrace();
                                }
                                iterator.remove();
                            }
                        }else {
                            while (iterator.hasNext()){
                                u= iterator.next();
                                ChatGs chatGs;
                                if(data.s==1||data.s==-1){
                                    chatGs=ChatContrain.getSelfChatGroup().getCGS(data.n);
                                }else {
                                    chatGs=ChatContrain.getChatGroups(u,data.n);
                                }

                                try {
                                    if (data.i==1){
                                        if (data.s!=-1){
                                            chatGs.remove(data);
                                        }
                                        iterator.remove();
                                        if(data.s!=0){
                                            ChatData chatData=data.copy();
                                            chatData.s=0;
                                            for (String user:chatGs.members){
                                                if(user.equals(chatData.u)){
                                                    continue;
                                                }
                                                adddataBuffer(user,chatData);
                                            }
                                        }
                                        continue;
                                    }
//                                    !Objects.equals(u,UDPclient.userlocal.username)

                                    if (data.s==0 || data.s==1){
                                        if (data.tl>1000){
                                            Utils.PathSort pathSort=Utils.pathPrase(data.text);

//                                            DataBuffer dataBuffer=new DataBuffer(u);
//                                            byte[] bytes=dataBuffer.reqFile(pathSort.path);
                                            AsyBuffer asyBuffer = new AsyBuffer(u);
                                            String pp = ":data&:"+pathSort.path;
                                            byte[] bytes=asyBuffer.reqData(pp);
                                            data.text=new String(bytes);
                                        }
                                        if (!chatGs.ringQue.contains(data)){
                                            File file=null;
                                            String path=null;
                                            if (data.file!=null&& !data.file.equals("")){
                                                DataReqAuto dataReqAuto = null;
                                                Object o;
                                                String hash;
                                                try {
                                                    dataReqAuto = new DataReqAuto(u);
                                                    o= dataReqAuto.reqFile(data.file);
                                                    //                            hash=DataAuto.getHash(o);
                                                    dataReqAuto.clear();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    continue;
                                                }
                                                path=new File(data.file).getName();
                                                String[] fs = path.split("\\.");
                                                String prex=null;
                                                String base=null;
                                                if (fs.length != 2) {
                                                    prex="";
                                                    base=path;
                                                }else {
                                                    prex=fs[1];
                                                    base=fs[0];
                                                }
                                                int times=1;
                                                File fileb=null;
                                                path=XmlCreate.userChat+base+"."+prex;
                                                while (true){
                                                    fileb=new File(path);
                                                    if(fileb.exists()){
                                                        path=XmlCreate.userChat+base+"("+times+")."+prex;
                                                        times++;
                                                    }else {
                                                        break;
                                                    }
                                                }
                                                DataReqAuto.writdata(path,o);
                                                file=new File(path);
                                                data.file=file.getAbsolutePath();
                                            }

                                            chatGs.add(data);
                                            ChatData chatData = data.copy();
                                            chatData.file=path;
                                            if(data.s==1){
                                                chatData.s=0;
                                                for (String user:chatGs.members){
                                                    if(user.equals(chatData.u)||user.equals(UDPclient.userlocal.username)){
                                                        continue;
                                                    }
                                                    adddataBuffer(user,chatData);
                                                }
                                            }
                                        }
                                    } else if(data.s==-1){
                                        ChatData chatData = data.copy();
                                        chatData.s=0;
                                        for (String user:chatGs.members){
                                            if(user.equals(chatData.u)){
                                                continue;
                                            }
                                            adddataBuffer(user,chatData);
                                        }
                                    }
                                }catch (NullPointerException n){
                                    n.printStackTrace();
                                }
                                iterator.remove();
                            }
                        }
                        if (set.size()==0){
                            iteratorsm.remove();
                        }
                    }
                    if(stringSetMapBuffer.size()>0 || stringSetMap.size()>0){
                        Thread.sleep(500*3);
                    }else {
                        Thread.sleep(500*60*3);
                    }

                } catch (InterruptedException e) {
                    System.out.println("ChatHanderThread "+e.getMessage());
                }catch (Exception e) {
                    System.out.println("ChatHanderThread "+e.getMessage());
                }
            }
        }finally {
            thread=null;
            Thread.currentThread().setName("Free");
        }
    }
}
