package superlink.udpbind.handle.handler;

import superlink.udpbind.client.UserContext;
import superlink.udpbind.handle.process.HandlerProcsee;
import superlink.udpbind.remote.block.Estimater;
import superlink.udpbind.remote.block.RemoteBlock;
import superlink.udpbind.remote.invoking.RemoteWorkContrains;
import superlink.util.Utils;
import superlink.util.asynhandle.AsynHandle;
import superlink.util.asynhandle.AsynHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static superlink.udpbind.client.UDPclient.mainDataQueue;

public class ReqRemoteSynHandle  implements HandlerProcsee, Callable {
        String name;
        short id;
        String medthon;
        AsynHandle asynHandle;
        String hashCode;
        UserContext userContext;
        RemoteBlock block;

        public ReqRemoteSynHandle(String name,String medthon,Object... para){
            this.name=name;
            this.medthon=medthon;
            userContext=mainDataQueue.getUserContext(name);
            this.id=userContext.newQueue();
            asynHandle = AsynHandle.getHandle();
            hashCode= Utils.getRandom(16);
            asynHandle.setObj(para);
            this.para=para;
            Estimater estimater=new Estimater() {
                @Override
                public boolean getState() {
                    block=userContext.getTask(id).block;
                    if (block.change){
                        return false;
                    }else {
                        if(block.mode>=0){
                            return true;
                        }
                    }
                    return false;
                }
            };
            asynHandle.setEstimater(estimater);
        }

        Object para;
        public void setPara(Object... para) {
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
            RemoteWorkContrains.threadPool.submit(this);
//                Deals deals= BindFactory.bindrecs.get(name).deals;
//                deals.setTask(this);
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
            try {
                for (AsynHandler handle:list){
                    asynHandle.addWork(((para)->{
                        Object o=handle.call(para);
                        block.setMode(-1);
                        return o;
                    }));
                }
            }catch (Exception exception){
                exception.printStackTrace();
            }finally {
                try {
                    asynHandle.reentrantLock.notify();
                }catch (Exception exception){
                    exception.printStackTrace();
                }
            }

            return null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {return true;}
            if (o == null || getClass() != o.getClass()) {return false;}

            superlink.udpbind.handle.handler.ReqRemoteHandle that = (superlink.udpbind.handle.handler.ReqRemoteHandle) o;

            return hashCode != null ? hashCode.equals(that.hashCode) : that.hashCode == null;
        }

        @Override
        public int hashCode() {
            return hashCode != null ? hashCode.hashCode() : 0;
        }
    }

