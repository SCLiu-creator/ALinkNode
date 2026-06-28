package superlink.udpbind.handle.handler;

import superlink.udpbind.client.UserContext;
import superlink.udpbind.client.recives.Senders;
import superlink.udpbind.client.recives.datalen.AutoBuffer;
import superlink.udpbind.client.recives.recor.BindFactory;
import superlink.udpbind.client.recives.recor.Deals;
import superlink.udpbind.handle.process.HandlerProcsee;
import superlink.udpbind.usedata.BufferRequest;
import superlink.util.Utils;
import superlink.util.asynhandle.AsynHandle;
import superlink.util.asynhandle.AsynHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static superlink.udpbind.client.UDPclient.mainDataQueue;



public class ReqRemoteHandle implements HandlerProcsee, Callable {
    String name;
    int id;
    String medthon;
    AsynHandle asynHandle;
    String hashCode;
    UserContext userContext;

    public ReqRemoteHandle(String name,String medthon,Object... para){
        this.name=name;
        this.medthon=medthon;
        userContext=mainDataQueue.getUserContext(name);
        this.id=userContext.newQueue();
        asynHandle = AsynHandle.getHandle();
        hashCode=Utils.getRandom(16);
        reSend=Utils.byteMerger(new byte[]{0,1,1},hashCode.getBytes());
        asynHandle.setObj(para);
        this.para=para;

    }

    Object para;
    public void setPara(Object... para) {
        BufferRequest bufferRequest=new BufferRequest();
        this.para=para;
    }
    public Object getValue(){
        asynHandle.reentrantLock.lock();
        while (!userContext.cheak()){
            try {
                asynHandle.reentrantLock.wait(10000);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        }
        asynHandle.reentrantLock.unlock();
        return asynHandle.obj;
    }
    @Override
    public void process() {
        try {
//            RemoteWorkContrains.threadPool.submit(this);
            Deals deals=BindFactory.bindrecs.get(name).deals;
            deals.setTask(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String mingling=null;

    List<AsynHandler> list=new ArrayList();
    public AsynHandle addWork(AsynHandler asynHandler){
        list.add(asynHandler);
        return asynHandle;
    }

    byte[] reSend=null;

    @Override
    public Object call() throws Exception {
        UserContext userContext=mainDataQueue.getUserContext(name);
        if (mingling!=null){
//            synHandle.setObj(o);
            asynHandle.addWork((para)->{
                AutoBuffer autoData=new AutoBuffer(name);
                Object o=autoData.reqData(mingling);
                autoData.clear();
                return o;
            });
            for (AsynHandler handle:list){
                asynHandle.addWork(handle);
            }
        }else {
            Senders.Sends(userContext.getBothId(),id,userContext.inetAddress,userContext.port,reSend);
//            RemoteWorkContrains.threadPool.submit(this);
            BindFactory.bindrecs.get(name).deals.setTask(this);
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReqRemoteHandle that = (ReqRemoteHandle) o;

        return hashCode != null ? hashCode.equals(that.hashCode) : that.hashCode == null;
    }

    @Override
    public int hashCode() {
        return hashCode != null ? hashCode.hashCode() : 0;
    }
}

