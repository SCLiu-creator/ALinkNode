package superlink.util.asynhandle;

import superlink.udpbind.handle.process.HandlerProcsee;
import superlink.udpbind.remote.invoking.RemoteWorkContrains;
import superlink.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class LocalHandle implements HandlerProcsee, Callable {
    AsynHandle asynHandle;
    String hashCode;

    public LocalHandle(Object... para){
        asynHandle = AsynHandle.getHandle();
        hashCode=Utils.getRandom(16);
        asynHandle.setObj(para);

    }

    public Object getValue(){
        asynHandle.reentrantLock.lock();
            try {
                asynHandle.reentrantLock.wait();
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        return asynHandle.obj;
    }
    @Override
    public void process() {
        try {
            RemoteWorkContrains.threadPool.submit(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    List<AsynHandler> list=new ArrayList();
    public AsynHandle addWork(AsynHandler asynHandler){
        list.add(asynHandler);
        return asynHandle;
    }


    @Override
    public Object call() throws Exception {
        for (AsynHandler handle:list){
            asynHandle.addWork(handle);
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocalHandle that = (LocalHandle) o;

        return hashCode != null ? hashCode.equals(that.hashCode) : that.hashCode == null;
    }

    @Override
    public int hashCode() {
        return hashCode != null ? hashCode.hashCode() : 0;
    }
}

