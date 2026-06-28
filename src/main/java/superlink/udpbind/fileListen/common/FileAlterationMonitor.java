package superlink.udpbind.fileListen.common;

import superlink.udpbind.client.recives.recor.BindFactory;
import superlink.udpbind.cloude.CloudeListenCaset;

import java.util.List;
import java.util.concurrent.*;

public class FileAlterationMonitor implements Runnable {

    private final long interval;
    private final List<FileAlterationObserver> observers = new CopyOnWriteArrayList<>();
    private Thread thread = null;
    private volatile boolean running = false;

    /**
     * Constructs a monitor with a default interval of 10 seconds.
     */
    public FileAlterationMonitor() {
        this(10000);
    }

    /**
     * Constructs a monitor with the specified interval.
     *
     * @param interval The amount of time in milliseconds to wait between
     * checks of the file system
     */
    public FileAlterationMonitor(final long interval) {
        this.interval = interval;
    }

    /**
     * Returns the interval.
     *
     * @return the interval
     */
    public long getInterval() {
        return interval;
    }


    public void addObserver(final FileAlterationObserver observer) {
        if (observer != null) {
            observers.add(observer);
        }
    }


    public void removeObserver(final FileAlterationObserver observer) {
        if (observer != null) {
            while (observers.remove(observer)) {
                // empty
            }
        }
    }

    public Iterable<FileAlterationObserver> getObservers() {
        return observers;
    }

    /**
     * Starts monitoring.
     *
     * @throws Exception if an error occurs initializing the observer
     */
    public synchronized void start() throws Exception {
        if (running) {
            throw new IllegalStateException("Monitor is already running");
        }
        for (final FileAlterationObserver observer : observers) {
            observer.initialize();

        }
        running = true;
        if (CloudeListenCaset.cloudeListenCaset!=null&& CloudeListenCaset.cloudeListenCaset.castThread!=null){
            CloudeListenCaset.cloudeListenCaset.castThread.interrupt();
        }else {
            thread= BindFactory.checkthread;
        }
    }

    public synchronized void stop() throws Exception {
        if (!running) {
            throw new IllegalStateException("Monitor is not running");
        }
        running = false;
        try {
            if (CloudeListenCaset.cloudeListenCaset!=null){
                CloudeListenCaset.cloudeListenCaset.stop();
            }
        } catch (final Exception e) {
            Thread.currentThread().interrupt();
        }
    }

    public Thread getThread(){
        return thread;
    }

    @Override
    public void run() {
        thread=Thread.currentThread();
        String threadName = thread.getName();
        thread.setName("cheakFiles");
//        thread.setName(this.getClass().getName());
        for (final FileAlterationObserver observer : observers) {
            observer.checkAndNotify();
        }
        thread.setName(threadName);
        if (!running) {
            return;
        }
    }
}

