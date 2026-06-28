package superlink.util.setThreadPool;

import superlink.util.prioityThreadPool.*;

import java.util.Set;
import java.util.concurrent.*;

public class SetThreadPoolExecutor extends PriorityThreadPoolExecutor {

    protected Set<Runnable> runnableSet;

    public SetThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, int linkedLen) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, linkedLen);
        runnableSet=new ConcurrentSkipListSet<>();
    }


    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        runnableSet.remove(r);
        super.afterExecute(r,t);
    }



    public boolean reExecute(Runnable command) {
        if (!exist(command)){
            runnableSet.add(command);
            super.execute(command);
            return true;
        }else {
            return false;
        }
    }
    public boolean exist(Runnable r){
       return runnableSet.contains(r);
    }
}
