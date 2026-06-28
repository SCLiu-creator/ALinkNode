package superlink.udpbind.remote.invoking;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RemoteWorkContrains {

    static public ThreadPoolExecutor threadPool;



    static {
        threadPool = new ThreadPoolExecutor(
                0,1,1000, TimeUnit.SECONDS,new ArrayBlockingQueue<>(256));
    }
}
