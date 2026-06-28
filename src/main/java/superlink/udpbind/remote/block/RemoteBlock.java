package superlink.udpbind.remote.block;

import superlink.udpbind.client.UserContext;
import superlink.udpbind.remote.RemoteBlockCon;

public class RemoteBlock {
    public UserContext userContext;
    public short id;
    public UserContext.Task task;
    public Long upTime =System.currentTimeMillis();
    public RemoteBlock(UserContext u,short id,UserContext.Task task){
        userContext=u;
        this.id=id;
        this.task=task;
    }
    //初始0，-1为锁，1为释放
    public int mode=0;
    public boolean change=false;
    public Runnable[] runnables=new Runnable[0];
    public Runnable runnable;

    public void lockMode(int mode) {
        this.mode = mode;
        lock();
    }
    public void setMode(int mode) {
        if (mode==0){
            wake();
        }
        try {
            if (mode<0){
                runnable.run();
            }else {
                runs();
            }
        }catch (Exception |Error e){
            e.printStackTrace();
        }

        this.mode = mode;
    }

    public boolean isFree(){
        if (change){
            return false;
        }
        if (mode>0){
            return true;
        }
        return false;
    }

    public void lock() {
        this.change = true;
        userContext.taskMap.put(id,task);
        upTime=System.currentTimeMillis();
        RemoteBlockCon.addBlock(task);
    }
    public void unLock() {
        userContext.taskMap.remove(id,task);
        upTime=System.currentTimeMillis();
        this.change = false;
    }

    public void wake(){
        try {
            this.notifyAll();
        }catch (IllegalMonitorStateException e){

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public boolean timeOut(){
        if (System.currentTimeMillis()-task.block.upTime > 60*1000*10 && (!change && mode>=0)){
           return true;
        }
        return false;
    }

    public void add(Runnable... runnable){
        synchronized (this){
            Runnable[] runnables=new Runnable[this.runnables.length+runnable.length];
            for (int i = 0; i < this.runnables.length; i++) {
                runnables[i]=this.runnables[i];
            }
            for (int i = this.runnables.length; i < runnables.length; i++) {
                runnables[i]=runnable[i-this.runnables.length];
            }
            this.runnables=runnables;
        }
    }
    public void runs(){
        synchronized (this){
            int i = 0;
            Runnable runnable;
            for (int j=0;j<runnables.length;j++){
                runnable=runnables[j];
                try {
                    runnable.run();
                    runnables[j]=null;
                }catch (Exception e){
                    i++;
                }
            }
            if (i==0){
                runnables=new Runnable[0];
            }else {
                Runnable[] runnables=new Runnable[i];
                for (Runnable runna : runnables) {
                    if (runna!=null){
                        runnables[i-1]=runna;
                        i--;
                    }
                }
            }
        }
    }

}
