package superlink.util.asynhandle;

import superlink.udpbind.remote.block.Estimater;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

public class AsynHandle {

    public BlockingQueue<Work> list;
    public BlockingQueue<WorkError> errors;
    public Work finalRun;
    public Object obj;
    public AtomicBoolean aBoolean=new AtomicBoolean(true);
    public Estimater estimater;
    public ReentrantLock reentrantLock=new ReentrantLock();

    public static AsynHandle getHandle(){
        AsynHandle asynHandle =new AsynHandle();
        return asynHandle;
    }

    private AsynHandle(){
        list=new LinkedBlockingQueue<>();
        errors=new LinkedBlockingQueue<>();
    }

    public AsynHandle addWork(AsynHandler callable){
        Work work=new Work(this,callable);
        list.add(work);
        AsynThreadPool.addTask(this);
        return this;
    }
    public AsynHandle addWork(AsynHandler... callable){
        for (AsynHandler asynHandler :callable){
            Work work=new Work(this, asynHandler);
            list.add(work);
        }
        AsynThreadPool.addTask(this);
        return this;
    }
    public AsynHandle addFinally(AsynHandler callable){
        Work work=new Work(this,(para)->{
            try {
                obj=callable.call(para);
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if (reentrantLock.isLocked()){
                    reentrantLock.unlock();
                }
            }
            return null;
        });
        finalRun=work;
        AsynThreadPool.addTask(this);
        return this;
    }

    public void setObj(Object obj){
        synchronized (this){
            this.obj=obj;
        }
    }

    public boolean getState() {
        if (estimater!=null){
            return estimater.getState()||aBoolean.get();
        }else {
            return aBoolean.get();
        }
    }

    public void setEstimater(Estimater estimater) {
        this.estimater=estimater;
    }
    public static class Work implements Runnable{

        private Work(AsynHandle handle, AsynHandler callable){
            this.callable=callable;
            this.handle=handle;
        }

        AsynHandler callable;
        AsynHandle handle;

        public void run() {
            try {
                handle.aBoolean.set(false);
                Object o=callable.call(handle.obj);
                handle.setObj(o);
            }catch (Exception | Error e){
                for (WorkError error:handle.errors){
                    if(error.cla.equals((e).getClass())){
                        try {
                            error.callable.call(e);
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                }
                handle.list.clear();
            }finally {
                handle.aBoolean.set(true);
                AsynThreadPool.interrupt();
            }
        }
    }

    public static class WorkError{

        private WorkError(AsynHandle handle, AsynHandler callable, Class cla){
            this.callable=callable;
            this.handle=handle;
            this.cla=cla;
        }

        AsynHandler callable;
        AsynHandle handle;
        public Class cla;

    }
}
