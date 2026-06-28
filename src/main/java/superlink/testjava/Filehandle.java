package superlink.testjava;
import java.io.*;
import java.nio.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
public class Filehandle {

    public static void main(String[] args) throws IOException {
        String inputFile = "large/file";
        String outputDir = "output/directory";
        int chunkSize = 1024 * 1024; // 1 MB

        splitFile(inputFile, outputDir, chunkSize);

        String outputFileName = "output/file";
        mergeFiles(outputFileName, outputDir);
    }




       public static void assemble(String outputFilePath, String[] inputFilePaths) throws IOException {
            try (FileChannel outputChannel = new FileOutputStream(outputFilePath).getChannel();
                 BufferedOutputStream outputStream = new BufferedOutputStream(Channels.newOutputStream(outputChannel))) {
                // 依次读取所有分段数据
                for (String inputFilePath : inputFilePaths) {
                    try (FileChannel inputChannel = new FileInputStream(inputFilePath).getChannel()) {
                        ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024); // 缓存1MB数据
                        while (inputChannel.read(buffer) > 0 || buffer.position() != 0) {
                            buffer.flip(); // 切换为读模式
                            // 将缓存区的数据写入输出文件中
                            outputStream.write(buffer.array(), 0, buffer.limit());
                            buffer.compact(); // 切换为写模式
                        }
                    }
                }
            }

    }



    public static void readFile(String fileName) throws IOException {
        File file = new File(fileName);
        if (!file.exists() || !file.isFile()) {
            throw new IOException("Input file is not valid");
        }

        long fileSize = file.length();
        try (RandomAccessFile raf = new RandomAccessFile(file, "r");
             FileChannel channel = raf.getChannel()) {
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, fileSize);
            byte[] bytes = new byte[(int) fileSize];
            buffer.get(bytes);
            // Do something with the file contents
        }
    }



    public static void splitFile(String inputFile, String outputDir, int chunkSize) throws IOException {
        File file = new File(inputFile);
        if (!file.exists() || !file.isFile()) {
            throw new IOException("Input file is not valid");
        }

        if (chunkSize <= 0) {
            throw new IllegalArgumentException("Chunk size should be greater than zero");
        }

        int counter = 0;
        byte[] buffer = new byte[chunkSize];
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            int bytesRead = 0;
            while ((bytesRead = bis.read(buffer)) > 0) {
                String chunkName = String.format("%s.part%03d", file.getName(), counter);
                File chunkFile = new File(outputDir, chunkName);
                try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(chunkFile))) {
                    bos.write(buffer, 0, bytesRead);
                }
                counter++;
            }
        }
    }



    public static void mergeFiles(String outputFileName, String inputDir) throws IOException {
        File directory = new File(inputDir);
        if (!directory.exists() || !directory.isDirectory()) {
            throw new IOException("Input directory is not valid");
        }

        File[] files = directory.listFiles();
        if (files == null || files.length == 0) {
            throw new IOException("No files found in input directory");
        }

        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFileName))) {
            byte[] buffer = new byte[1024];
            for (File file : files) {
                try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
                    int bytesRead = 0;
                    while ((bytesRead = bis.read(buffer)) > 0) {
                        bos.write(buffer, 0, bytesRead);
                    }
                }
            }
        }
    }
}