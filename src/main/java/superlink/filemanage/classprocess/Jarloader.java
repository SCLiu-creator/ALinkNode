package superlink.filemanage.classprocess;

import java.io.IOException;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Jarloader extends URLClassLoader {
    public Jarloader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public Jarloader(URL[] urls) {
        super(urls);
    }

    public Jarloader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
        super(urls, parent, factory);
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }

    @Override
    public final Class<?> loadClass(String name, boolean resolve)
    {
        try {
            return super.loadClass(name, resolve);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
    public Class defineClass(String clazz,byte[] bytes){
        return super.defineClass(clazz,bytes,0,bytes.length);
    }

    @Override
    public Class<?> findClass(String classname){
        try {
            return super.findClass(classname);
        } catch (Exception | Error e ) {
            System.out.println("parent cant find mistake:" +classname);
            //e.printStackTrace();
            return null;
        }

    }

    public Class<?> findClass1(String classname,byte[] bytes){
        try {
                // defineClass将字节数组转换成Class对象
            return defineClass(classname, bytes, 0, bytes.length);

        } catch (Exception | Error e ) {
            System.out.println("parent cant find mistake:" +classname);
            //e.printStackTrace();
            return null;
        }

    }

    public Class<?> defineClass(String classname,String pathname) {
        String myPath = pathname + classname.replace(".","/") + ".class";
        System.out.println(myPath);
        byte[] cLassBytes = null;
        Path path = null;
        try {
            path = Paths.get(new URI(myPath));
            cLassBytes = Files.readAllBytes(path);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        Class clazz = defineClass(classname, cLassBytes, 0, cLassBytes.length);
        return clazz;
    }


    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        Class<?> cla=null;
        try {
            cla=findClass(name);
            cla=findLoadedClass(name);
            //cla=ClassLoader.getSystemClassLoader().loadClass(name);
            if (cla==null){
                cla=loadClass(name, false);
            }
        }catch (Exception e){
            System.out.println("parent cant findloader mistake:" +name);

            //e.printStackTrace();
            cla=loadClass(name, false);
            Class c=cla.getClass();
            ClassLoader classLoR=Runnable.class.getClassLoader();
            ClassLoader classLo=cla.getClassLoader();
            ClassLoader classLoader=c.getClassLoader();
            System.out.println("");
        }
        return cla;
    }
}
