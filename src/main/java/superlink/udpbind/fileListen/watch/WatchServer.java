package superlink.udpbind.fileListen.watch;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

public class WatchServer{

//  对于文件低频变动的场景，这种方案实现简单，基本上可以满足需求。不过像上篇文章中提到的那样，需要注意Java 8和Java 9中File#lastModified的Bug问题。
//          但该方案如果用在文件目录的变化上，缺点就有些明显了，比如：操作频繁，效率都损耗在遍历、保存状态、对比状态上了，无法充分利用OS的功能。
//          方案二：WatchService
//          在Java 7中新增了java.nio.file.WatchService，通过它可以实现文件变动的监听。
//          WatchService是基于操作系统的文件系统监控器，可以监控系统所有文件的变化，无需遍历、无需比较，是一种基于信号收发的监控，效率高。
//          csharp复制代码public class WatchServiceDemo {

    static WatchEvent.Kind[] watchEvent=new WatchEvent.Kind[]{
            StandardWatchEventKinds.ENTRY_MODIFY,
            StandardWatchEventKinds.ENTRY_CREATE,
            StandardWatchEventKinds.ENTRY_DELETE
    };
    public static void main(String[] args) throws IOException {
        // 这里的监听必须是目录
        Path path = Paths.get("C:\\Users\\liushengchang-n\\my-project\\");
        // 创建WatchService，它是对操作系统的文件监视器的封装，相对之前，不需要遍历文件目录，效率要高很多
        WatchService watcher = FileSystems.getDefault().newWatchService();
        // 注册指定目录使用的监听器，监视目录下文件的变化；
        // PS：Path必须是目录，不能是文件；
        // StandardWatchEventKinds.ENTRY_MODIFY，表示监视文件的修改事件
//        path.register(watcher, watchEvent);
        File df=path.toFile();
        long l=df.length();
        watchPath(path.toFile());
        i=0;j=0;
        watchPath(path,watcher);
        System.out.print("over watchPath");
        FileWatchedAdapter listener=new FileWatchedAdapter();
        // 创建一个线程，等待目录下的文件发生变化
        try {
            while (true) {
                // 获取目录的变化:
                // take()是一个阻塞方法，会等待监视器发出的信号才返回。
                // 还可以使用watcher.poll()方法，非阻塞方法，会立即返回当时监视器中是否有信号。
                // 返回结果WatchKey，是一个单例对象，与前面的register方法返回的实例是同一个；
//                WatchKey key = watcher.take();
                Thread.sleep(14000);
                WatchKey key=watcher.poll();
                if (key==null){
                    continue;
                }
                // 处理文件变化事件：
                // key.pollEvents()用于获取文件变化事件，只能获取一次，不能重复获取，类似队列的形式。
                for (WatchEvent<?> event : key.pollEvents()) {
                    // event.kind()：事件类型
                    if (event.kind() == StandardWatchEventKinds.OVERFLOW) {
                        //事件可能lost or discarded
                        continue;
                    }
                    WatchEvent.Kind kind = event.kind();
                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        listener.onOverflowed((WatchEvent<Path>) event);
                        continue;
                    } else if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                        listener.onCreated((WatchEvent<Path>) event);
                        continue;
                    } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                        listener.onModified((WatchEvent<Path>) event);
                        continue;
                    } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                        listener.onDeleted((WatchEvent<Path>) event);
                        continue;
                    }
                    // 返回触发事件的文件或目录的路径（相对路径）
                    Path fileName = (Path) event.context();

                    System.out.println("文件更新: " + fileName);
                    System.out.println("name: " + event.kind().name()+"kind: " + event.kind().type().toString());
                }
                // 每次调用WatchService的take()或poll()方法时需要通过本方法重置
                if (!key.reset()) {
                    key.cancel();
//                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    static volatile int i=0;
    static volatile int j=0;
    public static void watchPath(Path path,WatchService watcher) {
        try {
            path.register(watcher,watchEvent);
            i++;
        } catch (IOException e) {
            e.printStackTrace();
        }
        File file=path.toFile();
        if(file.length()>0){
            if (file.isDirectory()){
                File[] files=file.listFiles();
                if (files!=null){
                    for (File file1:files){
                        if (file1.isDirectory()){
                            watchPath(file1.toPath(),watcher);
                        }
                    }
                }
            }
        }
        System.out.print("\rSize  "+i);
    }
    public static void watchPath(File file) {
        if(file.length()>0){
            if (file.isDirectory()){
                File[] files=file.listFiles();
                if (files!=null){
                    for (File file1:files){
                        if(file1.length()>0){
                            if (file1.isDirectory()){
                                j++;
                                watchPath(file1);
                            }else{
                                i++;
                            }
                        }


                    }
                }
            }else {
                i++;
            }
        }
        System.out.print("\rSize  "+i+"  "+j);
    }
//    PollingWatchService() {
//        // TBD: Make the number of threads configurable
//        scheduledExecutor = Executors
//                .newSingleThreadScheduledExecutor(new ThreadFactory() {
//                    @Override
//                    public Thread newThread(Runnable r) {
//                        Thread t = new Thread(null, r, "FileSystemWatcher", 0, false);
//                        t.setDaemon(true);
//                        return t;
//                    }});
//    }
}