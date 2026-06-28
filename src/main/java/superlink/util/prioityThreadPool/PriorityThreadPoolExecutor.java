package superlink.util.prioityThreadPool;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class PriorityThreadPoolExecutor extends ThreadPoolExecutor {

    /**
     * 定义一个成员变量，用于记录当前线程池中已提交的任务数量
     */
    private final AtomicInteger submittedTaskCount = new AtomicInteger(0);

    public PriorityThreadPoolExecutor(int corePoolSize,
                                      int maximumPoolSize,
                                      long keepAliveTime,
                                      int linkedLen) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS,
                new LinkedReciveQueue<>(linkedLen), new CustomThreadFactory(), new CustomRejectedExecutionHandler());
        ((LinkedReciveQueue)super.getQueue()).setExecutor(this);
    }
    public PriorityThreadPoolExecutor(int corePoolSize,
                                      int maximumPoolSize,
                                      long keepAliveTime,
                                      int linkedLen,
                                      RejectedExecutionHandler reject) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS,
                new LinkedReciveQueue<>(linkedLen), new CustomThreadFactory(), reject);
        ((LinkedReciveQueue)super.getQueue()).setExecutor(this);
    }

    public PriorityThreadPoolExecutor(int corePoolSize,
                                      int maximumPoolSize,
                                      long keepAliveTime,
                                      TimeUnit unit,
                                      LinkedReciveQueue<Runnable> workQueue,
                                      ThreadFactory threadFactory,
                                      RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        workQueue.setExecutor(this);
    }


    public int getSubmittedTaskCount() {
        return submittedTaskCount.get();
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        // ThreadPoolExecutor的勾子方法，在task执行完后需要将池中已提交的任务数 - 1
        submittedTaskCount.decrementAndGet();
    }

    @Override
    public void execute(Runnable command) {
        if (command == null) {
            throw new NullPointerException();
        }
        // do not increment in method beforeExecute!
        // 将池中已提交的任务数 + 1
        submittedTaskCount.incrementAndGet();
        try {
            super.execute(command);
        } catch (RejectedExecutionException rx) {
            // retry to offer the task into queue.
            final LinkedReciveQueue queue = (LinkedReciveQueue) super.getQueue();
            try {
                if (!queue.retryOffer(command, 0, TimeUnit.MILLISECONDS)) {
                    submittedTaskCount.decrementAndGet();
                    throw new RejectedExecutionException("Queue capacity is full.", rx);
                }
            } catch (InterruptedException x) {
                submittedTaskCount.decrementAndGet();
                throw new RejectedExecutionException(x);
            }
        } catch (Throwable t) {
            // decrease any way
            submittedTaskCount.decrementAndGet();
            throw t;
        }
    }

    @Override
    public void shutdown() {
        try {
            throw new Exception("shutdown");
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(e);
        }
        super.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        try {
            throw new Exception("shutdown");
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(e);
        }
        return super.shutdownNow();
    }
}
