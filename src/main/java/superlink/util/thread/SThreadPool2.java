package superlink.util.thread;

import superlink.udpbind.client.UDPclient;

public class SThreadPool2 {

    public static ThreadInterface threadPoolInterface;

    static {
        setThreadInterface(new SThreadPool.DefaultThreadPool());
        // threadInterface.;
    }

    public static void setThreadInterface(ThreadInterface threadInterface){
        SThreadPool.threadPoolInterface=threadInterface;
    }

    public static void execute(ThreadFunction function) {

        SThreadPool.threadPoolInterface.start(SThreadPool.threadPoolInterface.create(function));
    }

    public static ThreadFunction create(ThreadFunction function) {
        return SThreadPool.threadPoolInterface.create(function);
    }

    public static void start(ThreadFunction function) {
        SThreadPool.threadPoolInterface.start(function);
    }


    static class DefaultThreadPool<E> implements ThreadInterface{

        @Override
        public ThreadFunction create(ThreadFunction function) {
            return function;
        }

        @Override
        public E start(ThreadFunction function) {
            UDPclient.executorService.execute(()->{
                function.start();
            });
            return null;
        }
    }

}

