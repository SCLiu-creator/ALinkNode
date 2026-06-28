package superlink.util.thread;

import java.util.concurrent.Executors;

public class ThreadPacket {

    public static ThreadInterface threadInterface;

    static {
        setThreadInterface((t)->{
            new Thread(()->{t.start();});
            return null;
        });
       // threadInterface.;
    }

    public static void setThreadInterface(ThreadInterface threadInterface){
        ThreadPacket.threadInterface=threadInterface;

    }

    public static void main(String[] args) {
        ThreadFunction threadInterface1= SThread.create(()->{
            System.out.println("aaaaaaaaaaa");
            return null;
        });
        SThread.start(threadInterface1);

        setThreadInterface((o)->{
            Executors.newSingleThreadExecutor().execute(()->{
                o.start();
            });

//            System.out.println("aaa");
            return o;
        });

        setThreadInterface(function -> {

            return function;
        });

        setThreadInterface(new tt());

        String s="AASDAS";
        ThreadFunction function=threadInterface.create(()->{
            System.out.println("bbbb");
            System.out.println(s);
            return null;
        });

        threadInterface.create(()->{
            System.out.println("cccc");
            return null;
        });
        threadInterface.create(()->{
            System.out.println("dddd");
            return null;
        });
        //threadInterface.start(function);
//        function.start();
    }
    static class tt implements ThreadInterface{

        @Override
        public ThreadFunction create(ThreadFunction function) {
            return function;
        }

        @Override
        public Object start(ThreadFunction function) {
//            new Thread(()->{
//                function.start();}).start();
            Executors.newSingleThreadExecutor().execute(()->{
                function.start();
            });
            return null;
        }
    }

}
