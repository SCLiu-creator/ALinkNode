package superlink.udpbind.fileListen.watch;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 文件监听适配器
 *
 * @author zhibo
 * @date 2019-07-31 11:07
 */
public class FileWatchedAdapter implements FileWatchedListener {

    public static Set<ChatEvent> chatEventList =new HashSet<>();

    public static List<String> chatDealEventList =new ArrayList<>();

    @Override
    public void onCreated(WatchEvent<Path> watchEvent) {
        Path fileName = watchEvent.context();
        File file=fileName.toFile();
        String fp=file.getAbsolutePath();
        FileInputStream fm= null;
        try {
            fm = new FileInputStream(file);
            int d=0;
            while ((d=fm.read())!=-1){
                d=d;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(fileName.getFileName());
        System.out.println(String.format("文件【%s】被创建，时间：%s", fileName.toAbsolutePath(), now()));
    }

    @Override
    public void onDeleted(WatchEvent<Path> watchEvent) {
        Path fileName = watchEvent.context();
        File file=fileName.toFile();
        String fp=file.getAbsolutePath();
        System.out.println(String.format("文件【%s】被删除，时间：%s", fileName.toAbsolutePath(), now()));
    }

    @Override
    public void onModified(WatchEvent<Path> watchEvent) {
        Path fileName = watchEvent.context();
        File file=fileName.toFile();
        String fp=file.getAbsolutePath();
        System.out.println(String.format("文件【%s】被修改，时间：%s", fileName.toAbsolutePath(), now()));
    }

    @Override
    public void onOverflowed(WatchEvent<Path> watchEvent) {
        Path fileName = watchEvent.context();
        System.out.println(String.format("文件【%s】被丢弃，时间：%s", fileName.toAbsolutePath(), now()));
    }

    private String now(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
        return dateFormat.format(Calendar.getInstance().getTime());
    }


    public class ChatEvent{
        public String sn;
        public String fileName;
        public boolean f;
    }
}

