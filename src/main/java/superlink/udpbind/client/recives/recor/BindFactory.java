package superlink.udpbind.client.recives.recor;

import superlink.init.InitClass;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.recives.ByteBufer;
import superlink.udpbind.client.recives.ByteReBuffer;
import superlink.udpbind.client.recives.MainDataQueue;
import superlink.udpbind.client.udplink.ReCallBind;
import superlink.udpbind.handle.Handler;
import superlink.udpbind.handle.LiveHandle;
import superlink.udpbind.remote.RemoteBlockCon;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;

public class BindFactory {
    public static boolean mode=true;
    public static  ConcurrentSkipListMap<String,Bindrec> bindrecs=new ConcurrentSkipListMap();
    public static Bindrec selfIrec;

    public static irec createBindrec(ByteBufer blockingQueue, String username){
        Bindrec bindrec=bindrecs.get(username);
        if (bindrec==null){
            bindrec=new Bindrec(blockingQueue,username);
            bindrecs.put(username,bindrec);
            if (!mode){
                UDPclient.executorService.execute(bindrec);
            }
        }else {
            bindrec.Bindrec(blockingQueue,username);
        }
        return bindrec;
    }

    public static irec selfBindrec(ByteBufer blockingQueue, String username){
        if (selfIrec==null){
            selfIrec=new Bindrec(blockingQueue,username);
        }
        return selfIrec;
    }

    public static Thread checkthread;
    public static void check(){
        long time;
        checkthread=Thread.currentThread();
        checkthread.setName("bindChek");
        checkthread.setPriority(7);
        // 设置未捕获异常处理器
        checkthread.setUncaughtExceptionHandler((t, e) -> {
            if (e instanceof StackOverflowError) {
                System.err.println("线程因栈溢出终止！");
            } else {
                System.err.println("线程因其他异常终止: " + e.getClass());
            }
            checkthread=null;
            System.out.println(Thread.currentThread());
        });
        while (mode){
            try {
                time=System.currentTimeMillis();
                long finalTime = time;
                while (selfIrec.blockingQueue.size()>0){
                    try {
                        byte[] bytes=selfIrec.blockingQueue.poll();
                        if (bytes!=null){
                            selfIrec.deals.setRequest(bytes).deal();
                            selfIrec.t=0;
                            selfIrec.time= finalTime;
                        }
                    }catch (Exception e){
                        System.out.println("except by: selfIrec");
                        e.printStackTrace();
                    }
                }
                bindrecs.forEach((k, v)->{
                    int size=v.blockingQueue.size();
                    if (size==0){
                        v.reLink();
                    }else {
                        while (v.blockingQueue.size()>0){
                            //arrayList.add(bindrec);
                            v.unblockrun();
                        }
                        v.t=0;
                        v.time= finalTime;
                    }
                });

                time=time-System.currentTimeMillis();
//            checkthread.isInterrupted();

                Thread.interrupted();
                if (time>1400){
                    continue;
                }else {
                    try {
                        Thread.sleep(1400-time);
                    }catch (InterruptedException interruptedException){

                    }catch (Exception e){
                        System.out.println(e.getMessage());e.printStackTrace();
                    }
                }
            }catch (Throwable throwable){
                throwable.printStackTrace();
            }
        }
    }

    public static void checkLow(){
        long time;
        long timeo=System.currentTimeMillis();
        checkthread=Thread.currentThread();
        checkthread.setName("bindChek");
        checkthread.setPriority(7);
        // 设置未捕获异常处理器
        checkthread.setUncaughtExceptionHandler((t, e) -> {
            if (e instanceof StackOverflowError) {
                System.err.println("线程因栈溢出终止！");
            } else {
                System.err.println("线程因其他异常终止: " + e.getClass());
            }
            checkthread=null;
            System.out.println(Thread.currentThread());
        });
        while (mode){
            try {
                while (InitClass.initClass.udPclient.serverQueue.size()>0){
                    InitClass.initClass.udPclient.runnable.run();
                }

                long finalTime=System.currentTimeMillis();
                while (selfIrec.blockingQueue.size()>0){
                    try {
                        byte[] bytes=selfIrec.blockingQueue.poll();
                        if (bytes!=null){
                            selfIrec.deals.setRequest(bytes).deal();
                            selfIrec.t=0;
                            selfIrec.time= finalTime;
                        }
                    }catch (Exception e){
                        System.out.println("except by: selfIrec");
                        e.printStackTrace();
                    }
                }
                try {
                    bindrecs.forEach((k,v)->{
                        ByteBufer blockingQueue=v.blockingQueue;
                        int size=blockingQueue.size();
                        if (size==0){
                            v.reLink();
                        }else {
                            while (blockingQueue.size()>0){
                                //arrayList.add(bindrec);
                                try {
                                    v.unblockrun();
                                }catch (Exception e){
                                    System.out.println("except by:"+k);
                                    e.printStackTrace();
                                }
                            }
                            v.t=0;
                            v.time= finalTime;
                        }
                        byte[] bytes;
                        while (true) {                     // 无限循环
                            bytes = v.reBufer.poll();      // 取出数据
                            if (bytes == null) {
                                if(v.reBufer.cheak(3200)){
                                    v.reBufer.reqdata();
                                }
                                break;                     // 遇到 null 或空数据时终止循环
                            }
                            if(bytes.length==0){
                                break;
                            }
                            try {
                                v.deals.setRequest(bytes).deal();
                                v.t = 0;
                                v.time = finalTime;
                            } catch (Exception e) {
                                System.out.println("except by:" + k);
                                e.printStackTrace();
                            }
                        }
                        if (v.rsBufer.size()!=0){
                            v.rsBufer.poll();
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }

                time=System.currentTimeMillis()-timeo;
//            checkthread.isInterrupted();

                if (time<3200){
                    if (RemoteBlockCon.blockList.size()>0 || RemoteBlockCon.listBuf.size()>0 ){
                        RemoteBlockCon.check();
                    }
                    try {
                        Thread.sleep(1000);
                    }catch (InterruptedException ignored){
                    }catch (Exception e){
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                    continue;
                }
//            Thread.interrupted();
//            System.out.println("wait checktime:"+time);
                LiveHandle handle= (LiveHandle)Handler.DispectMap.get("LiveBind");
                timeo=System.currentTimeMillis();
                handle.run(200);

//            System.out.println("spend Time:  "+(System.currentTimeMillis()-timeo)/1000);
            }catch (Throwable e){
                e.printStackTrace();
            }
        }
    }

    public static void checkOne(){
        long time;
        long timeo=System.currentTimeMillis();
        checkthread=Thread.currentThread();
        checkthread.setName("bindChek");
        checkthread.setPriority(7);
        // 设置未捕获异常处理器
        checkthread.setUncaughtExceptionHandler((t, e) -> {
            if (e instanceof StackOverflowError) {
                System.err.println("线程因栈溢出终止！");
            } else {
                System.err.println("线程因其他异常终止: " + e.getClass());
            }
            checkthread=null;
            System.out.println(Thread.currentThread());
        });
        while (mode){
            try {
                MainDataQueue.mainReciverques.process();
                while (InitClass.initClass.udPclient.serverQueue.size()>0){
                    MainDataQueue.mainReciverques.process();
                    InitClass.initClass.udPclient.runnable.run();
                }

                long finalTime=System.currentTimeMillis();
                while (selfIrec.blockingQueue.size()>0){
//                    MainDataQueue.mainReciverques.process();
                    try {
                        MainDataQueue.mainReciverques.process();
                        byte[] bytes=selfIrec.blockingQueue.poll();
                        if (bytes!=null){
                            selfIrec.deals.setRequest(bytes).deal();
                            selfIrec.t=0;
                            selfIrec.time= finalTime;
                        }
                    }catch (Exception e){
                        System.out.println("except by: selfIrec");
                        e.printStackTrace();
                    }
                }
                try {
                    bindrecs.forEach((k,v)->{
                        MainDataQueue.mainReciverques.process();
                        ByteBufer blockingQueue=v.blockingQueue;
                        int size=blockingQueue.size();
                        if (size==0){
                            v.reLink();
                        }else {
                            while (blockingQueue.size()>0){
                                //arrayList.add(bindrec);
                                try {
                                    v.unblockrun();
                                }catch (Exception e){
                                    System.out.println("except by:"+k);
                                    e.printStackTrace();
                                }
                            }
                            v.t=0;
                            v.time= finalTime;
                        }
                        byte[] bytes;
                        while (true) {
                            MainDataQueue.mainReciverques.process();
                            // 无限循环
                            bytes = v.reBufer.poll();      // 取出数据
                            if (bytes == null) {
                                if(v.reBufer.cheak(3200)){
                                    v.reBufer.reqdata();
                                }
                                break;                     // 遇到 null 或空数据时终止循环
                            }
                            if(bytes.length==0){
                                break;
                            }
                            try {
                                v.deals.setRequest(bytes).deal();
                                v.t = 0;
                                v.time = finalTime;
                            } catch (Exception e) {
                                System.out.println("except by:" + k);
                                e.printStackTrace();
                            }
                        }
                        if (v.rsBufer.size()!=0){
                            MainDataQueue.mainReciverques.process();
                            v.rsBufer.poll();
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }

                time=System.currentTimeMillis()-timeo;
//            checkthread.isInterrupted();
                if (time<3200){
                    if (RemoteBlockCon.blockList.size()>0 || RemoteBlockCon.listBuf.size()>0 ){
                        RemoteBlockCon.check();
                    }
                    try {
                        if (InitClass.ThreadMode!=1){
                            MainDataQueue.mainReciverques.process();
                            Thread.sleep(0);
//                            Thread.sleep(1);
                        }else {
                            Thread.sleep(1000);
                        }

                    }catch (InterruptedException ignored){

                    }catch (Exception e){
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                    continue;
                }
//            Thread.interrupted();
//            System.out.println("wait checktime:"+time);
                LiveHandle handle= (LiveHandle)Handler.DispectMap.get("LiveBind");
                timeo=System.currentTimeMillis();
                handle.run(200);

//            System.out.println("spend Time:  "+(System.currentTimeMillis()-timeo)/1000);
            }catch (Throwable e){
                e.printStackTrace();
            }
        }
    }

    public static void checkAll() {
        checkthread=Thread.currentThread();
        checkthread.setName("bindChek");
        checkthread.setPriority(7);
        // 设置未捕获异常处理器
        checkthread.setUncaughtExceptionHandler((t, e) -> {
            if (e instanceof StackOverflowError) {
                System.err.println("线程因栈溢出终止！");
            } else {
                System.err.println("线程因其他异常终止: " + e.getClass());
            }
            checkthread=null;
            System.out.println(Thread.currentThread());
        });
        long time = 3300;
        while (true){
            long timwNow = System.currentTimeMillis();
            try {
                time=checkAll(time);
            }catch (Throwable throwable){
                throwable.printStackTrace();
            }
//            System.out.println(time);
            if(time>1000){
                try {
                    Thread.sleep(1000);
                    time=time-1000;
                }catch (InterruptedException interruptedException){
                    time=time-(System.currentTimeMillis()-timwNow);
                }catch (Exception e){
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }else {
                try {
                    Thread.sleep(time-10);
                    time=10;
                }catch (InterruptedException interruptedException){
                    time=time-(System.currentTimeMillis()-timwNow);
                }catch (Exception e){
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    public static long onceTime = 3300;
    public static void checkAllonce() {
        onceTime=checkAll(onceTime);
//            System.out.println(time);
    }

    public static long checkAll(long timeValue) {
        long timestart = System.currentTimeMillis();
        while (InitClass.initClass.udPclient.serverQueue.size() > 0) {
            InitClass.initClass.udPclient.runnable.run();
        }

        long finalTime = System.currentTimeMillis();
        while (selfIrec.blockingQueue.size() > 0) {
            try {
                byte[] bytes = selfIrec.blockingQueue.poll();
                if (bytes != null) {
                    selfIrec.deals.setRequest(bytes).deal();
                    selfIrec.t = 0;
                    selfIrec.time = finalTime;
                }
            } catch (Exception e) {
                System.out.println("except by: selfIrec");
                e.printStackTrace();
            }
        }
        try {
            bindrecs.forEach((k, v) -> {
                ByteBufer blockingQueue = v.blockingQueue;
                int size = blockingQueue.size();
                if (size == 0) {
                    v.reLink();
                } else {
                    while (blockingQueue.size() > 0) {
                        //arrayList.add(bindrec);
                        try {
                            v.unblockrun();
                        } catch (Exception e) {
                            System.out.println("except by:" + k);
                            e.printStackTrace();
                        }
                    }
                    v.t = 0;
                    v.time = finalTime;
                }
                byte[] bytes;
                while (true) {                     // 无限循环
                    bytes = v.reBufer.poll();      // 取出数据
                    if (bytes == null) {
                        if (v.reBufer.cheak(3200)) {
                            v.reBufer.reqdata();
                        }
                        break;                     // 遇到 null 或空数据时终止循环
                    }
                    if (bytes.length == 0) {
                        break;
                    }
                    try {
                        v.deals.setRequest(bytes).deal();
                        v.t = 0;
                        v.time = finalTime;
                    } catch (Exception e) {
                        System.out.println("except by:" + k);
                        e.printStackTrace();
                    }
                }
                if (v.rsBufer.size() != 0) {
                    v.rsBufer.poll();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        long time = System.currentTimeMillis() - timestart;
        time = timeValue - time;

        if (RemoteBlockCon.blockList.size() > 0 || RemoteBlockCon.listBuf.size() > 0) {
            RemoteBlockCon.check();
        }
        if (time < 300) {
            LiveHandle handle = (LiveHandle) Handler.DispectMap.get("LiveBind");
            handle.run(200);
            time=3300;
        }else {
            time = System.currentTimeMillis() - timestart;
            time = timeValue - time;
        }

//            System.out.println("wait checktime:"+time);
//            System.out.println("spend Time:  "+(System.currentTimeMillis()-timeo)/1000);
        return time;
    }

    public static void checkSize(){
        long time;
        int i=0;
        long time0;
        ArrayList<Bindrec> arrayList=new ArrayList();
        while (mode){
            try {
                checkthread=Thread.currentThread();
                time0=System.currentTimeMillis();

                bindrecs.forEach((k,v)->{
                    int size=v.blockingQueue.size();
                    if (size==0){
                        v.reLink();
                    }else {
                        int si=v.unblockrun(i);
                        if (si!=i){
                            arrayList.add(v);
                        }
                        v.t=0;
                    }
                });

                time=time0-System.currentTimeMillis();
//            checkthread.isInterrupted();
                Thread.interrupted();
                if (time>1500){
                    continue;
                }else {
                    while (time<1400 && arrayList.size()>0){
                        Bindrec bindrec=null;
                        Iterator<Bindrec> iterator=arrayList.iterator();
                        while (iterator.hasNext()){
                            bindrec= iterator.next();
                            int s=bindrec.unblockrun(0);
                            if (s==0){
                                iterator.remove();
                            }
                        }
                        time=time0-System.currentTimeMillis();
                    }
                    try {
                        Thread.sleep(1500 - time);
                    }catch (InterruptedException interruptedException){
                    }catch (Exception e){
                        System.out.println(e.getMessage());
                    }
                }
                arrayList.clear();
            }catch (Throwable throwable){
                throwable.printStackTrace();
            }
        }
    }

    public static boolean setMode(boolean b){
        mode=b;
        return mode;
    }

    public static class synFactory implements factory{

        @Override
        public irec get() {
            return null;
        }

        @Override
        public String del() {
            return null;
        }
    }
    public static class asynFactory implements factory{

        @Override
        public irec get() {
            return null;
        }

        @Override
        public String del() {
            return null;
        }
    }

    public interface factory{
        public irec get();
        public String del();
    }

}
