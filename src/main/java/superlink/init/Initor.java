package superlink.init;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import superlink.filemanage.xmltool.UserGet;
import superlink.udpbind.controller.Invoke;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import static superlink.udpbind.controller.Controller.starCloud;

public class Initor implements Runnable{
    public static Map<String,Invoke.node> usersNodeMap=new HashMap<>();
    public static Thread thread;
    public static boolean timeSyn=false;
    //默认关闭
    public static boolean sleepState=false;

    @Override
    public void run() {
        thread=Thread.currentThread();
        Element user= UserGet.user;
        //List<Element> list=user.elements("CloudeUser");

        Attribute attribute=user.attribute("CloudeSyn");
        if (attribute!=null){
            String cloudeSyn= attribute.getValue();

            if (cloudeSyn.equals("on")){starCloud();}
        }
        Attribute cloudeTimeSyn=user.attribute("CloudeTimeSyn");
        if (cloudeTimeSyn!=null){
            String cloudeSyn= attribute.getValue();
            if (cloudeSyn.equals("on")){timeSyn=true;}
        }
        Attribute sleepstate=user.attribute("sleepState");
        if (sleepstate!=null){
            String cloudeSyn= attribute.getValue();
            if (cloudeSyn.equals("on")){sleepState=true;}
        }

        List<Element> elements=user.elements();
        if (elements.size() <= 0){return;}
        for (Element element:elements){
            String username=element.attribute("name").getValue();
            usersNodeMap.put(username,new Invoke.node());
            Invoke.requestUserCall(username);
        }

        while (true){
            try {
                usersNodeMap.wait(60*1000);
            } catch (InterruptedException e) {
                usersNodeMap.forEach((k,v)->{
                    Invoke.requestUserCall(k);
                });
                e.printStackTrace();
            }

            usersNodeMap.forEach((k,v)->{
                if (v.getB()) {
                 usersNodeMap.remove(k);
                }
            });
            if (usersNodeMap.size()==0){

                break;
            }

        }
//unuseful

//        List<Future> futureList=new ArrayList<>();
//        if (elements.size()!=0){
//            for (Element e:elements){
//                String name=e.attribute("name").getValue();
//                Future futureTask=Invoke.requestUserCall(name);
//                futureList.add(futureTask);
//            }
//        }
//
//
//        while (futureList.size()!=0){
//            for (Future<Invoke.node> future:futureList){
//                if (future.isDone()){
//                    try {
//                        Invoke.node node=future.get();
//                        if (node.getB()){
//                            //todo 开始同步
//
//                        }
//                        futureList.remove(future);
//
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    } catch (ExecutionException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }

    }
}
