package superlink.testjava;

import java.util.concurrent.locks.ReentrantLock;

public class testlock1 implements Runnable{

    private ReentrantLock reentrantLock;
    public testlock1(ReentrantLock reentrantLock){
        this.reentrantLock=reentrantLock;
    }

    @Override
    public void run(){
//            synchronized (reentrantLock){
        reentrantLock.lock();
        System.out.println("star");
        //reentrantLock.notify();
        try {
            this.wait(30000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("over");
        reentrantLock.unlock();
    }
}
