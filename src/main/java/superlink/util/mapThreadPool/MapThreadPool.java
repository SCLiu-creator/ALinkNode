package superlink.util.mapThreadPool;

import superlink.udpbind.client.recives.datalen.DataSmall;
import superlink.util.prioityThreadPool.PriorityThreadPoolExecutor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MapThreadPool extends PriorityThreadPoolExecutor {

    protected Map<String,DataSmall> runnableMap;
    protected Set<Runnable> workSet;
    public MapThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, int linkedLen) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, linkedLen);
        runnableMap=new ConcurrentHashMap<>();
        workSet=new HashSet<>();
    }


    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        if (r instanceof DataSmall){
            ((DataSmall)r).createTime=System.currentTimeMillis();
        }
        workSet.remove(r);
        super.afterExecute(r,t);
    }

    public void reExecute(String usernmae,Runnable command) {
       Runnable runnable=runnableMap.get(usernmae);
        if (runnable==null){
            runnableMap.put(usernmae,(DataSmall) command);
        }
        if (!exist(command)){
            workSet.add(command);
            super.execute(command);
        }

        long now=System.currentTimeMillis();
        runnableMap.forEach((k,v)->{
            if (v instanceof DataSmall){
                if ((now-v.createTime)>3*60*1000){
                    runnableMap.remove(k,v);
                }
            }
        });
    }
    public boolean exist(Runnable r){
        return workSet.contains(r);
    }
    public Runnable get(String s){
        return runnableMap.get(s);
    }
}
