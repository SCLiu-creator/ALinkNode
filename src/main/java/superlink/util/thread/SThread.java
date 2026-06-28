package superlink.util.thread;

public class SThread {

    public static ThreadInterface threadInterface;

    static {
        setThreadInterface(new DefaultThread());
        // threadInterface.;
    }

    public static void setThreadInterface(ThreadInterface threadInterface){
        SThread.threadInterface=threadInterface;

    }

    public static ThreadFunction create(ThreadFunction function) {
        return SThread.threadInterface.create(function);
    }

    public static void start(ThreadFunction function) {
        SThread.threadInterface.start(function);
    }


    static class DefaultThread implements ThreadInterface{

        @Override
        public ThreadFunction create(ThreadFunction function) {
            return function;
        }

        @Override
        public Object start(ThreadFunction function) {
            new Thread(()->{
                function.start();}).start();
            return null;
        }
    }

}
