package superlink.testjava;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;

import java.io.File;
import java.net.URL;

public class VideoPlayer {
    static {
        System.setProperty("java.awt.headless", "false");
        String path = VideoPlayer.class.getClassLoader().getResource("").getPath();
        System.out.println(path);
        System.setProperty("java.library.path","D:\\openCv\\opencv\\build\\java\\x86\\");
//        System.out.println(System.getProperty("java.library.path"));
        // 加载OpenCV本地库。这通常需要在你的项目设置或运行配置中完成，
        // 或者通过java.library.path系统属性指定。
        // 加载动态库
        File file=new File("udpclient/target/classes/lib/opencv/opencv_java490.dll");
        System.out.println(file.exists());
        URL url = ClassLoader.getSystemResource("D:\\openCv\\opencv\\build\\java\\x86\\opencv_java490.dll");
//        System.load("D:\\openCv\\opencv\\build\\java\\x86\\opencv_java490.dll");
        System.loadLibrary("opencv_java490.dll");
//        System.loadLibrary("lib/opencv/opencv_java490.dll");
//        try {
//            NativeUtils.loadLibraryFromJar("D:\\openCv\\opencv\\build\\java\\x64\\opencv_java490.dll",null);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//        D:\openCv\opencv\build\java\x64
    }

    public static void main(String[] args) {
        String videoPath = "D:\\epic\\ue5\\UE_5.4\\Templates\\TP_ME_VProdBP\\Content\\Movies\\MediaExample.mp4";  // 替换为你的视频文件路径
        try {
            // 创建FFmpegFrameGrabber对象
            FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(videoPath);
            frameGrabber.start();  // 开始抓取视频

            // 创建CanvasFrame对象，用于显示视频
            CanvasFrame canvasFrame = new CanvasFrame("Video Player");
            canvasFrame.setDefaultCloseOperation(CanvasFrame.EXIT_ON_CLOSE);
            canvasFrame.setAlwaysOnTop(true);

            // 逐帧读取并显示视频
            Frame frame;
            while ((frame = frameGrabber.grabFrame()) != null) {
                canvasFrame.setCanvasSize(frame.imageWidth, frame.imageHeight);
                canvasFrame.showImage(frame);
            }

            // 释放资源
            frameGrabber.stop();
            canvasFrame.dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
