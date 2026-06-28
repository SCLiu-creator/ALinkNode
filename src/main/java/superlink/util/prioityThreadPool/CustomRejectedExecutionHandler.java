package superlink.util.prioityThreadPool;

import javax.swing.*;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 * @Description: 拒绝策略
 * @author: lys
 */
public class CustomRejectedExecutionHandler implements RejectedExecutionHandler {

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        try {
            // 核心改造点，由blockingqueue的offer改成put阻塞方法
            // offer方法当队列满，而且放入时间超过设定时间时，返回false;
            // put方法当队列满时，会调用wait方法，put方法会等待一个空的位置出来，然后再执行insert
            executor.getQueue().put(r);
//            JOptionPane.showMessageDialog(null,"Udpclient服务满载","访问量达到1000",JOptionPane.INFORMATION_MESSAGE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}