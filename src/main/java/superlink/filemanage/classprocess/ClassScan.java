package superlink.filemanage.classprocess;

import com.alibaba.fastjson2.JSON;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.DatagramSocket;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static superlink.util.Utils.byteMerger;
import static superlink.util.Utils.chooseFile;

public class ClassScan{
        public static void main(String[] args) {

            String path= chooseFile();

            //调用scanPackage方法，传入包名，返回包下的所有类的Class对象的列表
            java.util.List<Class<?>> classes = scanPackage(path);

            //打印结果
            for (Class<?> clazz : classes) {
                System.out.println("clazz.getName():"+clazz.getName());
                Field[] field=clazz.getDeclaredFields();
                try {
                    Class clzz= null;
                    try {
                        clzz = Class.forName(clazz.getName());
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    Constructor<?>[] constructors=clazz.getDeclaredConstructors();
                    Class<?>[] paramterTypes = new Class[0];
                    for (Constructor<?> constructor:constructors){
                        paramterTypes=constructor.getParameterTypes();
                        for (Class<?> param:paramterTypes){
                            System.out.println(param);
                        }
                    }
                    if (paramterTypes.length==0){
                        Constructor<?> Bind= clzz.getConstructor();
                        System.out.println(JSON.toJSONString(Bind));
                    }else {
                        Object[] paramter=new Class[paramterTypes.length];
                        int i=0;
                        for (Class c:paramterTypes){
                            String s=c.getName();

                            Object o=c.newInstance();
                            paramter[i]=o;
                            i++;
                        }
                        Constructor<?> Bind= clzz.getConstructor(paramterTypes);
                        System.out.println(JSON.toJSONString(Bind));

                        Bind= clzz.getConstructor(DatagramSocket.class,Integer.class);
                        Object object = (Object) Bind.newInstance(new DatagramSocket(8888),new Integer(10000));
                        try {
                            Field field1=object.getClass().getDeclaredField(field.toString());
                            Method method=clzz.getMethod("poss");
                            method.invoke(object,"");
                            System.out.println(field1.getName());
                            System.out.println(field1.getClass().toString());
                        } catch (NoSuchFieldException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }


        public static String path;
        public static ClassLoader classLoader;
        //扫描指定包下的所有类，返回一个Class对象的列表
        public static java.util.List<Class<?>> scanPackage(String packageName) {
            java.util.List<Class<?>> classes = new ArrayList<>(); //存放结果的列表
            try {
                //将包名中的点替换为斜杠，得到相对路径
                String[] strings=packageName.split("\\\\");
                strings[0]="";
                StringBuilder sbu=new StringBuilder("");
                for (String s:strings){
                    sbu.append(s).append(".");
                }
                System.out.println(sbu.toString());
                path=packageName.replace("\\","/");
                path="jar:file:/"+path+"!/";
                classLoader=Thread.currentThread().getContextClassLoader();
                //path = strings[strings.length-1];//.replace(".", "\\");
//                path="com/intellij/rt/debugger/agent/CaptureAgent$KeyProvider.class";
//                path="D:";
                //获取当前线程的类加载器，用于加载资源
                //遍历每个URL对象
                URL url=new File(path).toURI().toURL();
                url=new URL(path);
                path=packageName;
                scanJar(url, classes); //扫描jar包下的所有类文件，添加到结果列表中


            } catch (Exception e) {
                e.printStackTrace();
            }
            return classes; //返回结果列表
        }


        //扫描指定jar包下的所有类文件，添加到结果列表中
        public static void scanJar(URL url, List<Class<?>> classes) {
            try {
                JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection(); //打开jar包连接
                JarFile jarFile = jarURLConnection.getJarFile(); //获取jar包对象

                Enumeration<JarEntry> jarEntries = jarFile.entries(); //获取jar包中的所有条目
                while (jarEntries.hasMoreElements()) { //遍历每个条目
                    JarEntry jarEntry = jarEntries.nextElement(); //获取一个条目
                    String entryName = jarEntry.getName(); //获取条目名，类似于com/example/A.class
                    System.out.println(entryName);
                    if (entryName.endsWith(".class")) { //如果是以指定包名开头并且以.class结尾的条目  entryName.startsWith(path) &&
                        String className = entryName.replace("/", ".").substring(0, entryName.length() - 6); //去掉斜杠，去掉后缀名，得到全限定类名
                        try {
                            className=className;
                            Class c=Jarclass.doclass(className,path);
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



