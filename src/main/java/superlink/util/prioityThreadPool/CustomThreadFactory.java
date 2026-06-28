package superlink.util.prioityThreadPool;

import superlink.udpbind.client.recives.recor.irec;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @Description: 定制的线程工厂
 * @author: lys
 */
public class CustomThreadFactory implements ThreadFactory {
    private AtomicInteger count = new AtomicInteger(0);

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r);
        //addandget为先添加后获取，类似于++i
        String threadName = irec.class.getSimpleName() + count.addAndGet(1);
        System.out.println(threadName);
        t.setName(threadName);
        return t;
    }
}

