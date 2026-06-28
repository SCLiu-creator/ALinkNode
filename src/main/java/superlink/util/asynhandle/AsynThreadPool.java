package superlink.util.asynhandle;

import java.util.concurrent.*;

public class AsynThreadPool extends ThreadPoolExecutor {

    public static ThreadPoolExecutor threadPool;
    private static final VisibleThreadFactory factory;
    private static Thread cheakThread;
    public static ConcurrentHashMap<AsynHandle, AsynHandle> taskMap=new ConcurrentHashMap();
    static Runnable runnable;
    static {
        factory=new VisibleThreadFactory();

        threadPool = new ThreadPoolExecutor(
                0,3,1000, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),factory);
         runnable=()->{
             cheakThread=new Thread();
             cheakThread.setName("SynThreadPool cheakThread");
            while (taskMap.size()>0){
                try {
                    taskMap.forEach((k,v)->{
                        if (v.getState()){
                            if (v.list.size()>0){
                                AsynHandle.Work work=v.list.poll();
                                threadPool.execute(work);
                            }else {
                                if (v.finalRun!=null){
                                    AsynHandle.Work work=v.finalRun;
                                    v.finalRun=null;
                                    threadPool.execute(work);
                                }
                            }
                        }else {
                            if (v.list.size()==0){
                                taskMap.remove(k);
                            }
                        }
                    });
                } catch (Exception  e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(1000);
                }catch (Exception e){

                }
            }
        };

    }



    public AsynThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public static void addTask(AsynHandle handle){
        taskMap.put(handle,handle);
        if(cheakThread==null){
            threadPool.execute(runnable);
        }else if (cheakThread.getState()!=Thread.State.RUNNABLE &&
                cheakThread.getState()!=Thread.State.WAITING&&
                cheakThread.getState()!=Thread.State.TIMED_WAITING&&
                cheakThread.getState()!=Thread.State.BLOCKED){
            threadPool.execute(runnable);
        }else {
            try {
                cheakThread.interrupt();
            }catch (Exception e){

            }
        }
    }
    public static void interrupt(){
        try {
            cheakThread.interrupt();
        }catch (Exception e){

        }
    }




}
