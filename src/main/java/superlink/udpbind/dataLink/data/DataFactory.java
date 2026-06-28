package superlink.udpbind.dataLink.data;

import superlink.udpbind.usedata.DataRequest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DataFactory {

    public static ExecutorService dataExecutor=new ThreadPoolExecutor(1,1,0L, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>(1000));

    public static DataRecives getRecive(DataRequest dataRequest) {

        return new DataRecives(dataRequest);
    }

    public static DataSends getSend(DataRequest dataRequest) {

        return new DataSends(dataRequest);
    }

}
