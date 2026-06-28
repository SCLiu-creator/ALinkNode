package superlink.util;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class NativeUtils {

    /**
     * The minimum length a prefix for a file has to have according to {@link File#createTempFile(String, String)}}.
     */
    private static final int MIN_PREFIX_LENGTH = 3;
    public static final String NATIVE_FOLDER_PATH_PREFIX = "nativeutils";

    /**
     * Temporary directory which will contain the DLLs.
     */
    private static File temporaryDir;

    /**
     * Private constructor - this class will never be instanced
     */
    private NativeUtils() {
    }

    public static synchronized void loadLibraryFromJarByDir(String dir) {
        String path = NativeUtils.class.getResource("/lib").getPath();
        File file = new File(path);
        File[] files = file.listFiles();
        try {
            Files.walkFileTree(file.toPath(), new FileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
//                    System.out.println("pre visit dir:" + dir);
                    return FileVisitResult.CONTINUE;
                }
                //重点关注该函数，本题会使用噢
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    System.out.println("visit file:" + file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
//                    System.out.println("visit file failed:" + file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
//                    System.out.println("post visit dir:" + dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 从jar包中加载动态库
     * 先将jar包中的动态库复制到系统临时文件夹，然后加载动态库，并且在JVM退出时自动删除。
     * The file from JAR is copied into system temporary directory and then loaded. The temporary file is deleted after
     * exiting.
     * Method uses String as filename because the pathname is "abstract", not system-dependent.
     *
     * @param path      要加载动态库的路径，必须以'/'开始,比如 /lib/mylib.so，必须以'/'开始
     * @param loadClass 用于提供{@link ClassLoader}加载动态库的类，如果为null,则使用NativeUtils.class
     * @throws IOException           动态库读写错误
     * @throws FileNotFoundException 没有在jar包中找到指定的文件
     */
    public static synchronized void loadLibraryFromJar(String path, Class<?> loadClass) throws IOException {

//        if (null == path || !path.startsWith("/")) {
//            throw new IllegalArgumentException("The path has to be absolute (start with '/').");
//        }

        // Obtain filename from path
        String[] parts = path.split("/");
        String filename = (parts.length > 1) ? parts[parts.length - 1] : null;
        filename=path;
        // Check if the filename is okay
        if (filename == null || filename.length() < MIN_PREFIX_LENGTH) {
            throw new IllegalArgumentException("The filename has to be at least 3 characters long.");
        }

        // 创建临时文件夹
        if (temporaryDir == null) {
            temporaryDir = createTempDirectory(NATIVE_FOLDER_PATH_PREFIX);
            temporaryDir.deleteOnExit();
        }
        // 临时文件夹下的动态库名
        File temp = new File(temporaryDir, filename);
        temp=new File(filename);
        Class<?> clazz = loadClass == null ? NativeUtils.class : loadClass;
        // 从jar包中复制文件到系统临时文件夹
        try (InputStream is = clazz.getResourceAsStream(path)) {
            assert is != null;
            Files.copy(is, temp.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            temp.delete();
            throw e;
        } catch (NullPointerException e) {
            temp.delete();
            throw new FileNotFoundException("File " + path + " was not found inside JAR.");
        }
        // 加载临时文件夹中的动态库
        try {
            System.load(temp.getAbsolutePath());
        } finally {
            // 设置在JVM结束时删除临时文件
            temp.deleteOnExit();
        }
    }

    /**
     * 在系统临时文件夹下创建临时文件夹
     */
    private static File createTempDirectory(String prefix) throws IOException {
        String tempDir = System.getProperty("java.io.tmpdir");
        File generatedDir = new File(tempDir, prefix + System.nanoTime());
        System.out.println("创建了临时文件：" + generatedDir.getAbsolutePath());
        if (!generatedDir.mkdir())
            throw new IOException("Failed to create temp directory " + generatedDir.getName());

        return generatedDir;
    }
}

