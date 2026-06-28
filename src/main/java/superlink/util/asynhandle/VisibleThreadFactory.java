package superlink.util.asynhandle;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadFactory;

public class VisibleThreadFactory implements ThreadFactory {

    private final Set<Thread> threads = new HashSet<>();

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r);
        threads.add(t); // 记录新创建的线程
        return t;
    }

    // 提供一个方法来获取所有创建的线程
    public Set<Thread> getAllThreads() {
        return threads;
    }

}
