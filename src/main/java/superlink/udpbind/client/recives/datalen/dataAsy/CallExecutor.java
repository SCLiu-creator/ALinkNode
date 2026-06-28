package superlink.udpbind.client.recives.datalen.dataAsy;

import superlink.udpbind.client.recives.datalen.AsySteam;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class CallExecutor {

    public static ExecutorService executorService= Executors.newSingleThreadExecutor();

    static ConcurrentHashMap<AsySteam,CallPoll> concurrentHashMap=new ConcurrentHashMap();

    static AtomicBoolean isRunning = new AtomicBoolean(false);
    public static void add(AsySteam steam,CallPoll callPoll){
        concurrentHashMap.put(steam,callPoll);
        if(!isRunning.get()){
            executorService.submit(() -> {
                try {
                    callable.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public static boolean unContain(AsySteam steam){
        return concurrentHashMap.get(steam)==null;
    }

    public static void remove(AsySteam steam){
        concurrentHashMap.remove(steam);
        steam.clear();
        try {
            throw new Exception();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    final static Callable callable=new Callable() {
        @Override
        public Object call() throws Exception {
            isRunning.set(true);
            try {
                while (concurrentHashMap.size()>0){
                    Iterator<Map.Entry<AsySteam,CallPoll>> iterator=concurrentHashMap.entrySet().iterator();
                    while (iterator.hasNext()){
                        Map.Entry<AsySteam,CallPoll> entry=iterator.next();
                        try {
                            entry.getValue().runTime();
                        }catch (Exception e){
                            e.printStackTrace();
                            iterator.remove();
//                            concurrentHashMap.remove()
                        }
                    }
                    try {
                        Thread.sleep(1);
                    }catch (Exception e){

                    }

                }
            }finally {
                isRunning.set(false);
            }

            return null;
        }
    };
}

