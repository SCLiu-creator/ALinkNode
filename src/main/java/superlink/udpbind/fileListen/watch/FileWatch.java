package superlink.udpbind.fileListen.watch;

import java.io.File;
import java.io.IOException;

public class FileWatch {

    /**
     * 上次更新时间
     */
    public static long LAST_TIME = 0L;

    public static void main(String[] args) throws IOException {

        String fileName = "D:\\java\\新建文件夹\\udpclient";
        //"/Users/zzs/temp/1.txt";
        // 创建文件，仅为实例，实践中由其他程序触发文件的变更
        createFile(fileName);

        // 执行2次
        for (int i = 0; i < 2; i++) {
            long timestamp = readLastModified(fileName);
            if (timestamp != LAST_TIME) {
                System.out.println("文件已被更新：" + timestamp);
                LAST_TIME = timestamp;
                // 重新加载，文件内容
            } else {
                System.out.println("文件未更新");
            }
        }
    }

    public static void createFile(String fileName) throws IOException {
        File file = new File(fileName);
        if (!file.exists()) {
            boolean result = file.createNewFile();
            System.out.println("创建文件：" + result);
        }
    }

    public static long readLastModified(String fileName) {
        File file = new File(fileName);
        return file.lastModified();
    }
}



