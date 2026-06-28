package superlink.testjava;


import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ScanPackage {

    public static void main(String[] args){
//        File file1=new File("C:\Users\liushengchang-n/Desktop");
//        File file2=new File("C://Users//liushengchang-n/Desktop");

        File filet=new File("D:\\java\\新建文件夹\\udpclient\\data\\");
        boolean b=filet.isDirectory();
        b=filet.isFile();

        testc testc=new testc();
        try {
            try {
                testc.tt();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        JFileChooser chooser=new JFileChooser();
        chooser.setSize(800,1200);
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setCurrentDirectory(new File("."));
        Frame farme=new Frame();
        farme.setSize(1000,1010);
        int result =chooser.showOpenDialog(farme);
        String path = null;
        if(result== JFileChooser.APPROVE_OPTION) {
             path=chooser.getSelectedFile().getPath();
        }
        File file=new File(path);


        //假设要扫描的包名是com.example
        String packageName = file.getPath();//com.example"java/test/testjava"
        //调用scanPackage方法，传入包名，返回包下的所有类的Class对象的列表
        List<Class<?>> classes = scanPackage(packageName);
        //打印结果
        for (Class<?> clazz : classes) {
            System.out.println("clazz.getName()"+clazz.getName());
            Field[] field=clazz.getDeclaredFields();
            try {
                Class clzz= null;
                try {
                    clzz = Class.forName(clazz.getName());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                Constructor<?>[] constructors=clazz.getDeclaredConstructors();
                for (Constructor<?> constructor:constructors){
                    Class<?>[] paramterTypes=constructor.getParameterTypes();
                    for (Class<?> param:paramterTypes){
                        System.out.println(param);
                    }
                }
                Constructor<?> Bind= clzz.getConstructor(DatagramSocket.class,Integer.class);
                Recivers httpThreadBind = (Recivers) Bind.newInstance(new DatagramSocket(8888),new Integer(10000));
                try {
                    Field i=httpThreadBind.getClass().getDeclaredField(field.toString());
                    i.getName();
                    i.getClass();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }


            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (SocketException e) {
                e.printStackTrace();
            }

        }
    }

    private class Recivers implements Runnable{
        public DatagramSocket datagramSocket;
        public Integer id;

        public Recivers(DatagramSocket datagramSocket,Integer id){
            this.datagramSocket=datagramSocket;

        }

        @Override
        public void run() {
            while (true){
                DatagramPacket packet=new DatagramPacket(new byte[65507],65507);
                try {
                    datagramSocket.receive(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int id=packet.getData()[0]*100+packet.getData()[1];

            }

        }
    }

    public static String path;
    public static ClassLoader classLoader;
    //扫描指定包下的所有类，返回一个Class对象的列表
    public static List<Class<?>> scanPackage(String packageName) {
        List<Class<?>> classes = new ArrayList<>(); //存放结果的列表
        try {
            //将包名中的点替换为斜杠，得到相对路径
            String[] strings=packageName.split("\\\\");
            String ss = null;
            strings[0]="";
            StringBuilder sbu=new StringBuilder("");
            for (String s:strings){
                ss=sbu.toString();
                sbu.append(s).append(".");
            }
            path = packageName.replace(".", "/");
           // path;

            //获取当前线程的类加载器，用于加载资源
            classLoader = Thread.currentThread().getContextClassLoader();
            //获取指定路径下的所有资源的URL对象
            String urlll=System.getProperty("user.dir");
            Enumeration<URL> urls = classLoader.getResources(".");//"/" 无法解析中文字符
            //遍历每个URL对象
            URL urll=new File(path).toURI().toURL();

            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                if (url != null) {
                    String protocol = url.getProtocol(); //获取协议名，判断是文件还是jar包
                    if (protocol.equals("file")) { //如果是文件
                        String dirPath = url.getPath(); //获取文件夹的绝对路径
                        scanDir(packageName, dirPath, classes); //扫描文件夹下的所有类文件，添加到结果列表中
                    } else if ("jar".equals(protocol)) { //如果是jar包
                        scanJar(packageName, url, classes); //扫描jar包下的所有类文件，添加到结果列表中
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classes; //返回结果列表
    }

    //扫描指定文件夹下的所有类文件，添加到结果列表中
    public static void scanDir(String packageName, String dirPath, List<Class<?>> classes) {
//        dirPath=dirPath.substring(1);
        File dir = new File(dirPath); //根据文件夹路径创建File对象
        if (dir.isDirectory()) { //判断是否是文件夹
            File[] files = dir.listFiles(); //获取文件夹下的所有文件或子文件夹
            if (files != null) { //判断是否为空
                for (File file : files) { //遍历每个文件或子文件夹
                    String fileName = file.getName(); //获取文件名或子文件夹名
                    if (file.isFile() && fileName.endsWith(".class")) { //如果是类文件，去掉后缀名，拼接包名和类名，得到全限定类名
                        String className = packageName + "." + fileName.substring(0, fileName.length() - 6);
                        className=className.substring(className.indexOf(".")+1);
                        try {
                            Class<?> clazz = Class.forName(className); //根据全限定类名加载Class对象
                            classes.add(clazz); //添加到结果列表中
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else if (file.isDirectory()) { //如果是子文件夹，递归调用本方法，传入子包名和子文件夹路径
                        scanDir(packageName + "." + fileName, file.getPath(), classes);
                    }
                }
            }
        }
    }

    //扫描指定jar包下的所有类文件，添加到结果列表中
    public static void scanJar(String packageName, URL url, List<Class<?>> classes) {
        try {
            JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection(); //打开jar包连接
            JarFile jarFile = jarURLConnection.getJarFile(); //获取jar包对象

            Enumeration<JarEntry> jarEntries = jarFile.entries(); //获取jar包中的所有条目
            while (jarEntries.hasMoreElements()) { //遍历每个条目
                JarEntry jarEntry = jarEntries.nextElement(); //获取一个条目
                String entryName = jarEntry.getName(); //获取条目名，类似于com/example/A.class
                if (entryName.startsWith(path) && entryName.endsWith(".class")) { //如果是以指定包名开头并且以.class结尾的条目
                    String className = entryName.replace("/", ".").substring(0, entryName.length() - 6); //去掉斜杠，去掉后缀名，得到全限定类名
                    try {
                        Class<?> clazz = classLoader.loadClass(className); //根据全限定类名加载Class对象
                        classes.add(clazz); //添加到结果列表中
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
