package superlink.udpbind.servlet;

import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.recives.MainDataQueue;
import superlink.udpbind.usedata.User;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ClearUser implements Runnable{

    public static long waitTime=System.currentTimeMillis();

    public static void chaek(){
        long time = System.currentTimeMillis();
        if (time-waitTime>60*1000*3){
            waitTime=time;
            new ClearUser().run();
        }
    }

    @Override
    public void run() {
        Iterator<Map.Entry<String,User> > iterator=UDPclient.userMap.entrySet().iterator();

        while (iterator.hasNext()){
            HashMap.Entry<String,User> entry=iterator.next();
            User user=entry.getValue();

            if(MainDataQueue.usermap.containsKey(user.username)){
                user.time=0;
            }else {
                user.time++;
            }
            if(user.time>2){
                iterator.remove();
            }
        }
    }
}
