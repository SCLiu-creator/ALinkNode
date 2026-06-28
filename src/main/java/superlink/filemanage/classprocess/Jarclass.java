package superlink.filemanage.classprocess;

import cn.hutool.core.lang.JarClassLoader;

import java.io.File;

public class Jarclass {
    public static Class doclass(String path,String pack){
        ClassLoader classLoader= JarClassLoader.loadJarToSystemClassLoader(new File(pack));
        Class cla=null;
        try {
            cla=classLoader.loadClass(path);
        }catch (Exception e){
            e.printStackTrace();
        }
        return cla;
    }
}
