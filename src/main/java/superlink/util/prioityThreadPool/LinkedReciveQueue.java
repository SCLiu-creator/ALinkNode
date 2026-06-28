package superlink.util.prioityThreadPool;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

public class LinkedReciveQueue<R extends Runnable> extends LinkedBlockingQueue<Runnable> {

    private static final long serialVersionUID = -2635853580887179627L;

    // 自定义的线程池类，继承自ThreadPoolExecutor
    private PriorityThreadPoolExecutor executor;

    public LinkedReciveQueue(int capacity) {
        super(capacity);
    }

    public void setExecutor(PriorityThreadPoolExecutor exec) {
        executor = exec;
    }

    // offer方法的含义是：将任务提交到队列中，返回值为true/false，分别代表提交成功/提交失败
    @Override
    public boolean offer(Runnable runnable) {
        if (executor == null) {
            throw new RejectedExecutionException("The task queue does not have executor!");
        }System.out.println("offer:"+runnable);
        // 线程池的当前线程数
        int currentPoolThreadSize = executor.getPoolSize();
        if (executor.getSubmittedTaskCount() < currentPoolThreadSize) {
            // 已提交的任务数量小于当前线程数，意味着线程池中有空闲线程，直接扔进队列里，让线程去处理
            return super.offer(runnable);
        }

        // return false to let executor create new worker.
        if (currentPoolThreadSize < executor.getMaximumPoolSize()) {
            // 重点： 当前线程数小于 最大线程数 ，返回false，暗含入队失败，让线程池去创建新的线程
            return false;
        }

        // 重点: 代码运行到此处，说明当前线程数 >= 最大线程数，需要真正的提交到队列中
        return super.offer(runnable);
    }

    public boolean retryOffer(Runnable o, long timeout, TimeUnit unit) throws InterruptedException {
        if (executor.isShutdown()) {
            throw new RejectedExecutionException("Executor is shutdown!");
        }
        return super.offer(o, timeout, unit);
    }
}
