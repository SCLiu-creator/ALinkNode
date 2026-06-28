package superlink.filemanage.classprocess;

import superlink.clientmain;
import superlink.httpserver.HttpServlet;
import superlink.httpserver.servelt.Interceptor.WebInterceptor;
import superlink.httpserver.ProcessMap;
import superlink.httpserver.servelt.ProcessMapL;
import superlink.httpserver.servelt.action.Action;
import superlink.httpserver.servelt.action.Api;
import superlink.httpserver.servelt.WebController;
import superlink.httpserver.servelt.action.GetParm;
import superlink.httpserver.servelt.action.WebPath;
import superlink.filemanage.classprocess.property.Property;
import superlink.filemanage.classprocess.property.ReInfuse;
import superlink.filemanage.classprocess.property.reInject;
import superlink.linkServer.LinkInterceptor;
import superlink.linkServer.LinkProcessMap;
import superlink.linkServer.Links;
import superlink.linkServer.Mod;
import superlink.util.Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class AutoScan {
    public static boolean b;
    public static URL url;
    static String packageName;
    private Object object;

    {
//        try {
//            Class<?> cls = AutoScan.class;
//            Method mainMethod= cls.getDeclaredMethod("main", String[].class);
//            String[] args = new String [] {"2024"};
//            mainMethod.invoke(null, (Object) args );
//        }catch (Exception e){}
    }
    static {
        //路径应和包名匹配
        try {
            File fileo=new File("");
            String op=fileo.getAbsolutePath();
            System.out.println("classpath :   "+op);
            try {
                String action= clientmain.class.getResource("").toString();
//                String action= HttpServlet.class.getResource("").toString();
                String pa=URLDecoder.decode( action,"UTF-8");
                url= new URL(pa);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String protocol= url.getProtocol();
            packageName= clientmain.class.getPackage().getName();
//            packageName= HttpServlet.class.getPackage().getName();
            if ("jar".equals(protocol)) {
                //jar包启动，扫自己
                b= true;
            }else {
                //dev启动，扫自己
                b= false;
            }
        }catch (Exception e){
            System.out.println("AutoScan error");
        }

        String path="jar:file:/"+"D:/java/新建文件夹/udpclient/target/clientmain.jar"+"!/";

    }
    public static void main(String[] args) {
//        Class c1=ActionCloude.class;
//        Class c2=new ActionCloude().getClass();
//        boolean ni=c1==c2;
        List<Class<?>> classList= AutoScan.scanPackage(url);
        new AutoScan().startscan(classList);
    }

    //clientmain.class.getResource("").toString();
    static public void setPath(Class clazz){

        //路径应和包名匹配
        File file=new File("").getAbsoluteFile();

        try {
            String absolue=clazz.getResource("").toString();
            String pa=URLDecoder.decode( absolue,"UTF-8");
            url= new URL(pa);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("classpath :   "+url.toString());
        String protocol= url.getProtocol();
        if ("jar".equals(protocol)) {
            //jar包启动，absolue
            packageName= clazz.getPackage().getName();;
            b= true;
        }else {
            //开发启动扫描文件路径absolue
            packageName= clazz.getPackage().getName();
            b= false;
        }
//        String path="jar:file:/"+"D:/java/新建文件夹/udpclient/target/clientmain.jar"+"!/";

//        try {
//            path=new File("").toURI().toURL().toString();
//            path=URLDecoder.decode(path,"UTF-8");
//            url=new URL(path);//new URL(path);
//            //url= Paths.get(path).toUri().toURL();
//        } catch (Exception e) {
//            e.printStackTrace();
//
//        }
    }


    public void startscan(List<Class<?>> classes){
        //调用scanPackage方法，传入包名，返回包下的所有类的Class对象的列表
//        List<Class<?>> classes = scanPackage(url);
        //打印结果

        for (Class<?> clazz : classes) {
            if (clazz==null){continue;}
            //System.out.println("clazz.getName():"+clazz.getName());
            try {
                Annotation[] annotations=clazz.getAnnotations();
                if (annotations.length<=0){
                    continue;
                }
                WebController annotation=(WebController)clazz.getAnnotation(WebController.class);
                WebInterceptor interceptorannotation=(WebInterceptor)clazz.getAnnotation(WebInterceptor.class);

                if (interceptorannotation!=null){
                    InterceptScan.scan(clazz);
                }

                if (annotation==null){
                    continue;
                }
                if (! (annotation instanceof WebController)){
                    continue;
                }
                Field[] field=clazz.getDeclaredFields();
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
                    if (clzz.isInterface()|| clzz.isLocalClass() || clzz.isAnnotation()){
                        System.out.println(clzz.toString());
                        continue;
                    }
                    Constructor<?> bind= clzz.getConstructor();
                    //WebController annotation=(WebController)clzz.getAnnotation(WebController.class);
                    if (annotation==null){
                        continue;
                    }
                    if (! (annotation instanceof WebController)){
                        continue;
                    }

                    //System.out.println(JSON.toJSONString(bind));
//                    Action obg=(Action) bind.newInstance();
                    Object obg= bind.newInstance();
//                    Class claob=obg.getClass();
                    String name=annotation.name();

                    Map<String, ProcessMap.Nettybean> map=new HashMap<>();
                    ProcessMap.mapMap.put(name,map);
                    for (Method method:clzz.getMethods()) {

                        Api api= method.getAnnotation(Api.class);
                        if (api==null){
                            continue;
                        }
                        String def=api.def();
                        Type re=method.getGenericReturnType();
                        if (re instanceof ParameterizedType){
                            Class crec=re.getClass();
                            //获取实际参数类型数组，比如List<User>，则获取的是数组[User]，Map<User,String> 则获取的是数组[User,String]
                            ParameterizedType parameterizedType=(ParameterizedType) re;
                            Type[] types=parameterizedType.getActualTypeArguments();
                            if (types.length == 1){
                                ProcessMap.Nettybean nettybean=new ProcessMap.Nettybean<List>();
                                nettybean.setAction(obg).setMethod(method).setReturnType(List.class);
                                map.put(method.getName(),nettybean);
                            }else {
                                Class<?> k=(Class) types[0];
                                if (types[1] instanceof ParameterizedType){
                                    ParameterizedType parameterizedType2=(ParameterizedType) types[1];
                                    Type[] types2=parameterizedType2.getActualTypeArguments();
                                    Class<?> v=(Class) types2[0];
                                }
                                //Class<?> v=(Class) types[1];
                                ProcessMap.Nettybean nettybean=new ProcessMap.Nettybean<Map>();
                                nettybean.setAction(obg).setMethod(method).setReturnType(Map.class);
                                map.put(method.getName(),nettybean);
//                                System.out.println(method.getName()+"的返回值类型是参数化类型，其类型为："+"<"+k.toString()+">");
                            }

                        }else {
                            //不是参数化类型,直接获取返回值类型
                            Class returnType = method.getReturnType();
                            ProcessMap.Nettybean nettybean=new ProcessMap.Nettybean<>();
                            nettybean.setAction(obg).setMethod(method).setReturnType(re);
                            map.put(def,nettybean);
                            //获取返回值类型的类名

                            def = returnType.getName();
//                            System.out.println(method.getName()+"的返回值类型不是参数化类型其类型为："+def);
                        }
                        // method.invoke(obg,new Object(),new Object());
                    }

                }else {
                    Object[] paramters=new Object[paramterTypes.length];
                    int i=0;
                    for (Class c:paramterTypes){
                        String s=c.getName();

                        Object o=c.newInstance();
                        paramters[i]=o;
                        i++;
                    }
                    Constructor<?> Bind= clzz.getConstructor(paramterTypes);
                    //System.out.println(JSON.toJSONString(Bind));

                    Bind= clzz.getConstructor(DatagramSocket.class,Integer.class);
                    Object object = (Object) Bind.newInstance(new DatagramSocket(8888),new Integer(10000));
                    try {
                        Field field1=object.getClass().getDeclaredField(field.toString());
                        Method method=clzz.getMethod("poss");
                        method.invoke(object,"");
                        System.out.println(""+field1.getName());
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

    public void autoscan(List<Class<?>> classes){
        //调用scanPackage方法，传入包名，返回包下的所有类的Class对象的列表
//        List<Class<?>> classes = scanPackage(url);
        //打印结果
        Map<String,Map> mapMap=new HashMap<>();
        Map<String,Class> injMap=new HashMap<>();
        Map<String,Class> proMap=new HashMap<>();
        Map<String,Object> reMap=new HashMap<>();
        for (Class<?> clazz : classes) {
            Annotation[] annotations=clazz.getAnnotations();
            if (annotations.length<=0){
                continue;
            }
            try {
                WebController controller=clazz.getAnnotation(WebController.class);
                Property property=clazz.getAnnotation(Property.class);
                ReInfuse reInfuse=clazz.getAnnotation(ReInfuse.class);
                WebInterceptor interceptorannotation=(WebInterceptor)clazz.getAnnotation(WebInterceptor.class);
                if (property!=null){
                    proMap.put(clazz.getName(),clazz);
                }
                if (reInfuse!=null){
                    injMap.put(clazz.getName(),clazz);
                }
            }catch (Exception e){
                continue;
            }
        }
        injMap.forEach((k,v)->{
            Method[] methods=v.getMethods();
            Field[] fields= v.getFields();
//            for (Method method:methods){
//                reInject reInject=method.getAnnotation(reInject.class);
//                if (reInject!=null){
//
//                }
//            }
            for (Field field:fields){
                field.setAccessible(true);
                reInject reInject=field.getAnnotation(reInject.class);
                if (reInject!=null){

                }
            }
        });

        for (Class<?> clazz : classes) {
            if (clazz==null){continue;}
            //System.out.println("clazz.getName():"+clazz.getName());
            try {
                Annotation[] annotations=clazz.getAnnotations();
                if (annotations.length<=0){
                    continue;
                }
                WebController annotation=(WebController)clazz.getAnnotation(WebController.class);
                WebInterceptor interceptorannotation=(WebInterceptor)clazz.getAnnotation(WebInterceptor.class);

                if (interceptorannotation!=null){
                    InterceptScan.scan(clazz);
                }

                if (annotation==null){
                    continue;
                }
                if (! (annotation instanceof WebController)){
                    continue;
                }
                Field[] field=clazz.getDeclaredFields();
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
                    if (clzz.isInterface()|| clzz.isLocalClass() || clzz.isAnnotation()){
                        System.out.println(clzz.toString());
                        continue;
                    }
                    Constructor<?> bind= clzz.getConstructor();
                    //WebController annotation=(WebController)clzz.getAnnotation(WebController.class);
                    if (annotation==null){
                        continue;
                    }
                    if (! (annotation instanceof WebController)){
                        continue;
                    }

                    //System.out.println(JSON.toJSONString(bind));
                    Action obg=(Action) bind.newInstance();
                    Class claob=obg.getClass();


                    String name=annotation.name();

                    Map<String, ProcessMap.Nettybean> map=new HashMap<>();
                    ProcessMap.mapMap.put(name,map);
                    for (Method method:clzz.getMethods()) {

                        Api api= method.getAnnotation(Api.class);
                        if (api==null){
                            continue;
                        }
                        String def=api.def();
                        Type re=method.getGenericReturnType();
                        if (re instanceof ParameterizedType){
                            Class crec=re.getClass();
                            //获取实际参数类型数组，比如List<User>，则获取的是数组[User]，Map<User,String> 则获取的是数组[User,String]
                            ParameterizedType parameterizedType=(ParameterizedType) re;
                            Type[] types=parameterizedType.getActualTypeArguments();
                            if (types.length == 1){
                                ProcessMap.Nettybean nettybean=new ProcessMap.Nettybean<List>();
                                nettybean.setAction(obg).setMethod(method).setReturnType(List.class);
                                map.put(method.getName(),nettybean);
                            }else {
                                Class<?> k=(Class) types[0];
                                if (types[1] instanceof ParameterizedType){
                                    ParameterizedType parameterizedType2=(ParameterizedType) types[1];
                                    Type[] types2=parameterizedType2.getActualTypeArguments();
                                    Class<?> v=(Class) types2[0];
                                }
                                //Class<?> v=(Class) types[1];
                                ProcessMap.Nettybean nettybean=new ProcessMap.Nettybean<Map>();
                                nettybean.setAction(obg).setMethod(method).setReturnType(Map.class);
                                map.put(method.getName(),nettybean);
//                                System.out.println(method.getName()+"的返回值类型是参数化类型，其类型为："+"<"+k.toString()+">");
                            }

                        }else {
                            //不是参数化类型,直接获取返回值类型
                            Class returnType = method.getReturnType();
                            ProcessMap.Nettybean nettybean=new ProcessMap.Nettybean<>();
                            nettybean.setAction(obg).setMethod(method).setReturnType(re);
                            map.put(def,nettybean);
                            //获取返回值类型的类名

                            def = returnType.getName();
//                            System.out.println(method.getName()+"的返回值类型不是参数化类型其类型为："+def);

                        }
                        // method.invoke(obg,new Object(),new Object());
                    }

                }else {
                    Object[] paramters=new Object[paramterTypes.length];
                    int i=0;
                    for (Class c:paramterTypes){
                        String s=c.getName();

                        Object o=c.newInstance();
                        paramters[i]=o;
                        i++;
                    }
                    Constructor<?> Bind= clzz.getConstructor(paramterTypes);
                    //System.out.println(JSON.toJSONString(Bind));

                    Bind= clzz.getConstructor(DatagramSocket.class,Integer.class);
                    Object object = (Object) Bind.newInstance(new DatagramSocket(8888),new Integer(10000));
                    try {
                        Field field1=object.getClass().getDeclaredField(field.toString());
                        Method method=clzz.getMethod("poss");
                        method.invoke(object,"");
                        System.out.println(""+field1.getName());
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
    public static java.util.List<Class<?>> classes;
    //扫描指定包下的所有类，返回一个Class对象的列表
    public static java.util.List<Class<?>> scanPackage(URL urlpath) {
        classes = new ArrayList<>(); //存放结果的列表
        String packageName=urlpath.toString();
        ScanHook();
        try {
            if (b){
                path="jar:file:/"+path+"!/";
                System.out.println("AutoScan path: "+path);
                //classLoader=Thread.currentThread().getContextClassLoader();
                classLoader=AutoScan.class.getClassLoader();
                //classLoader=ClassLoader.getSystemClassLoader();
                URL[] url1=new URL[1];
                System.out.println("AutoScan url: "+urlpath);
                url1[0]=urlpath;
                Jarloader urlClassLoader=new Jarloader(url1,classLoader);
                urlClassLoader.addURL(urlpath);
                classLoader=urlClassLoader;
                //path = strings[strings.length-1];//.replace(".", "\\");
//                path="com/intellij/rt/debugger/agent/CaptureAgent$KeyProvider.class";
//                path="D:";
                URL url=new File(path).toURI().toURL();
                url=new URL(path);
                path=packageName;
                scanJar(urlpath, classes);
            }else {
                packageName=packageName.replace("file:/","");
                scanDir(AutoScan.packageName,packageName,classes);
            }

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
                //System.out.println(entryName);
                if (entryName.endsWith(".class")) { //如果是以指定包名开头并且以.class结尾的条目  entryName.startsWith(path) &&
                    String className = entryName.replace("/", ".").substring(0, entryName.length() - 6); //去掉斜杠，去掉后缀名，得到全限定类名
                    try {
                        className=className;
//                        Class c= Jarclass.doclass(className,path);
                        Class<?> clazz = classLoader.loadClass(className); //根据全限定类名加载Class对象
                        classes.add(clazz); //添加到结果列表中
                    } catch (ClassNotFoundException | Error e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void scanJar(File file, List<Class<?>> classes) {
        try {
            ZipFile zipFile = new ZipFile(file);
            Enumeration<ZipEntry> jarEntries = (Enumeration<ZipEntry>) zipFile.entries(); //获取jar包中的所有条目
            while (jarEntries.hasMoreElements()) { //遍历每个条目
                ZipEntry jarEntry = jarEntries.nextElement(); //获取一个条目
                String entryName = jarEntry.getName(); //获取条目名，类似于com/example/A.class

                //System.out.println(entryName);
                if (entryName.endsWith(".class")) { //如果是以指定包名开头并且以.class结尾的条目  entryName.startsWith(path) &&
                    String className = entryName.replace("/", ".").substring(0, entryName.length() - 6); //去掉斜杠，去掉后缀名，得到全限定类名
                    Class<?> clazz=null;
                    try {
//                        Class c= Jarclass.doclass(className,path);
                         clazz= classLoader.loadClass(className); //根据全限定类名加载Class对象
                    } catch (ClassNotFoundException | Error e) {
                        e.printStackTrace();
                    }
                    if (clazz==null){
                        try {
//                        Class c= Jarclass.doclass(className,path);
                            clazz= ((Jarloader) classLoader).findClass(className); //根据全限定类名加载Class对象
                        } catch (Exception | Error e) {
                            e.printStackTrace();
                        }
                    }
                    if (clazz==null){
                        try {
                            InputStream inputStream=zipFile.getInputStream(jarEntry);
                            byte[] bytesbuf=new byte[0];
                            byte[] bytes=new byte[1024];
                            int len;
                            while ((len=inputStream.read(bytes))!=-1){
                                bytes=Utils.subByte(bytes,0,len);
                                bytesbuf= Utils.byteMerger(bytesbuf,bytes);
                            }
//                        Class c= Jarclass.doclass(className,path);
                            clazz= ((Jarloader) classLoader).findClass1(className,bytesbuf); //根据全限定类名加载Class对象

                        } catch (Exception | Error e) {
                            e.printStackTrace();
                        }
                    }
                    classes.add(clazz); //添加到结果列表中
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class ReAssemabling {

        public Class aClass;
        public Method aMethod;
        public String name;
        public List<Method> reMethods=new ArrayList<>();
        public List<Field> reFields=new ArrayList<>();
        public Class[] paras;
        public Constructor constructor;
        public Object object;
        public ReAssemabling(Class aClass) {
            this.aClass=aClass;
        }
        public ReAssemabling(Method aMethod) {
            this.aMethod=aMethod;
        }

        public ReAssemabling init()throws IllegalAccessException, InvocationTargetException, InstantiationException {
            try {
                Constructor constructor=aClass.getConstructor();
                object=constructor.newInstance();
            }catch (NoSuchMethodException n){
                constructor=aClass.getConstructors()[0];
                paras=constructor.getParameterTypes();
            }
            return this;
        }
        public ReAssemabling init(Class aClass)throws IllegalAccessException, InvocationTargetException, InstantiationException {
            try {
                this.aClass=aClass;
                Constructor constructor=aClass.getConstructor();
                object=constructor.newInstance();
            }catch (NoSuchMethodException n){
                constructor=aClass.getConstructors()[0];
                paras=constructor.getParameterTypes();
            }
            return this;
        }
        public int size(){
            return reFields.size()+reFields.size();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {return true;};
            if (o == null || getClass() != o.getClass()) {return false;}

            ReAssemabling that = (ReAssemabling) o;

            return aClass != null ? aClass.equals(that.aClass) : that.aClass == null;
        }

        @Override
        public int hashCode() {
            return aClass != null ? aClass.hashCode() : 0;
        }
    }


    //默认以类名注入，ReInfuse可指定bean名，同名则比价ReInfuse等级，
    public static Map<String,ReAssemabling> reAssemablingMap=null;
    public void autoScanWeb(List<Class<?>> classes){
        //调用scanPackage方法，传入包名，返回包下的所有类的Class对象的列表
//        List<Class<?>> classes = scanPackage(url);
        //打印结果
        Map<String,Map> mapMap=new HashMap<>();
        Map<String,Class> conMap=new HashMap<>();
        Map<String,Class> linkMap=new HashMap<>();
        Map<String,Class> injMap=new HashMap<>();
        Map<String,Class> proMap=new HashMap<>();
        Map<String,Class> refMap=new HashMap<>();

        for (Class<?> clazz : classes) {
            Annotation[] annotations=clazz.getAnnotations();
            if (annotations.length<=0){
                continue;
            }
            try {
                WebController controller=clazz.getAnnotation(WebController.class);
                Links linkClass=clazz.getAnnotation(Links.class);
//                Property property=clazz.getAnnotation(Property.class);
                ReInfuse reInfuse=clazz.getAnnotation(ReInfuse.class);

                WebInterceptor interceptorannotation=(WebInterceptor)clazz.getAnnotation(WebInterceptor.class);
//                if (property!=null){
//                    proMap.put(clazz.getName(),clazz);
//                }
                if (reInfuse!=null){
//                    if (reInfuse.name().equals("")){
//                        refMap.put(clazz.getSimpleName(),clazz);
//                    }else {
//                        refMap.put(reInfuse.name(),clazz);
//                    }
                    if (reInfuse.name().equals("")){
                        refMap.compute(clazz.getSimpleName(),(key, value)-> {
                            if (value==null){
                                return clazz;
                            }
                            ReInfuse reo= (ReInfuse) value.getAnnotation(ReInfuse.class);
                            ReInfuse ren= (ReInfuse) clazz.getAnnotation(ReInfuse.class);
                            int i=reo.grade().compareTo(ren.grade());
                            if (i>0){
                                return value;
                            }else {
                                return clazz;
                            }
                        });
                    }else {
                        refMap.compute(reInfuse.name(),(key, value)-> {
                            if (value==null){
                                return clazz;
                            }
                            ReInfuse reo= (ReInfuse) value.getAnnotation(ReInfuse.class);
                            ReInfuse ren= (ReInfuse) clazz.getAnnotation(ReInfuse.class);
                            int i=reo.grade().compareTo(ren.grade());
                            if (i>0){
                                return value;
                            }else {
                                return clazz;
                            }
                        });
//                assemablingMap.put(reInfuse.name(),reAssemabling);
                    }
//                    refMap.put(clazz.getSimpleName(),clazz);
                }
                if (controller!=null){
                    conMap.put(clazz.getSimpleName(),clazz);
                }
                if (linkClass!=null){
                    linkMap.put(clazz.getSimpleName(),clazz);
                }
            }catch (Exception e){
                continue;
            }
        }

        Map<String, ReAssemabling> assemablingMap=new HashMap<>();
        Map<String, ReAssemabling> assemablingMap0=new HashMap<>();
        refMap.forEach((k,v)->{
            ReAssemabling reAssemabling=null;
            Method[] methods=v.getDeclaredMethods();
            Field[] fields= v.getDeclaredFields();

            reAssemabling=new ReAssemabling(v);
            reAssemabling.name=k;
            try {
                reAssemabling.init();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
            ReInfuse reInfuse= (ReInfuse) v.getAnnotation(ReInfuse.class);
            if (reInfuse.name().equals("")){
                ReAssemabling finalReAssemabling = reAssemabling;
                assemablingMap.compute(k,(key, value)-> {
                    if (value==null){
                        return finalReAssemabling;
                    }
//                    ReInfuse reo=null
//                    ReInfuse ren=null;
//                    if (v.)
                    ReInfuse reo= (ReInfuse) value.aClass.getAnnotation(ReInfuse.class);
                    ReInfuse ren= (ReInfuse) finalReAssemabling.aClass.getAnnotation(ReInfuse.class);
                    int i=reo.grade().compareTo(ren.grade());
                    if (i>0){
                        return value;
                    }else {
                        return finalReAssemabling;
                    }
                });
            }else {
                ReAssemabling finalReAssemabling = reAssemabling;
                assemablingMap.compute(reInfuse.name(),(key, value)-> {
                    if (value==null){
                        return finalReAssemabling;
                    }
                    ReInfuse reo= (ReInfuse) value.aClass.getAnnotation(ReInfuse.class);
                    ReInfuse ren= (ReInfuse) finalReAssemabling.aClass.getAnnotation(ReInfuse.class);
                    int i=reo.grade().compareTo(ren.grade());
                    if (i>0){
                        return value;
                    }else {
                        return finalReAssemabling;
                    }
                });
//                assemablingMap.put(reInfuse.name(),reAssemabling);
            }

            for (Method method:methods){
                reInject reInject=method.getAnnotation(reInject.class);
                if (reInject!=null){
                    method.setAccessible(true);
                    reAssemabling.reMethods.add(method);
                }
                ReInfuse reInfuse1=method.getAnnotation(ReInfuse.class);
                if (reInfuse1!=null){
                    ReAssemabling reAssemablingm =new ReAssemabling(method);
                    try {
                        reAssemablingm.init(v);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    }
                    String km=method.getReturnType().getSimpleName();
                    if (reInfuse1.name().equals("")){
                        reAssemablingm.name=km;
                        ReAssemabling finalReAssemablingm=reAssemablingm;
                        assemablingMap.compute(km,(key, value)-> {
                            if (value==null){
                                return finalReAssemablingm;
                            }
                            ReInfuse reo;
                            if (value.aMethod!=null){
                                reo= (ReInfuse) value.aMethod.getAnnotation(ReInfuse.class);
                            }else {
                                reo= (ReInfuse) value.aClass.getAnnotation(ReInfuse.class);
                            }
                            ReInfuse ren= (ReInfuse) finalReAssemablingm.aMethod.getAnnotation(ReInfuse.class);
                            int i=reo.grade().compareTo(ren.grade());
                            if (i>0){
                                return value;
                            }else {
                                return finalReAssemablingm;
                            }
                        });
                    }else {
                        reAssemablingm.name=reInfuse1.name();
                        ReAssemabling finalReAssemablingm=reAssemablingm;
                        assemablingMap.compute(reInfuse1.name(),(key, value)-> {
                            if (value==null){
                                return finalReAssemablingm;
                            }
                            ReInfuse reo;
                            if (value.aMethod!=null){
                                reo= (ReInfuse) value.aMethod.getAnnotation(ReInfuse.class);
                            }else {
                                reo= (ReInfuse) value.aClass.getAnnotation(ReInfuse.class);
                            }
                            ReInfuse ren= (ReInfuse) finalReAssemablingm.aMethod.getAnnotation(ReInfuse.class);
                            int i=reo.grade().compareTo(ren.grade());
                            if (i>0){
                                return value;
                            }else {
                                return finalReAssemablingm;
                            }
                        });
                    }
                }
            }
            for (Field field:fields){
                reInject reInject=field.getAnnotation(reInject.class);
                if (reInject!=null){
                    field.setAccessible(true);
                    reAssemabling.reFields.add(field);
                }
            }
        });

        boolean b=false;
        int times=0;
        while (true){
            times++;
            Iterator<Map.Entry<String,ReAssemabling>> assemablingIterator=assemablingMap.entrySet().iterator();
            while (assemablingIterator.hasNext()){
                Map.Entry<String,ReAssemabling> item = assemablingIterator.next();
                String k=item.getKey();
                ReAssemabling v=item.getValue();
                if (v.object==null){
                        List<Object> pl = new ArrayList<>();
                        for (Class cl : v.paras) {
                            String paraName = cl.getSimpleName();
                            Object o = assemablingMap0.get(paraName);
                            if (o == null) {
                                o = assemablingMap.get(paraName);
                            }
                            if (o != null) {
                                ReAssemabling reAssemabling= (ReAssemabling) o;
                                pl.add(reAssemabling.object);
                            }
                        }
                        if (pl.size() == v.paras.length) {
                            try {
                                v.object = v.constructor.newInstance(pl.toArray());
                            } catch (InstantiationException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }
                }else {
                    if (v.size()>0){
                        if (v.reFields.size()>=0){
                            Iterator<Field> iterator=v.reFields.iterator();
                            while (iterator.hasNext()){
                                Field field=iterator.next();
                                reInject reInject=field.getAnnotation(reInject.class);
                                String fieldName;
                                if ("".equals(reInject.name())){
                                    fieldName=field.getType().getSimpleName();
                                }else {
                                    fieldName=reInject.name();
                                }
                                ReAssemabling r=assemablingMap0.get(fieldName);
                                if (r==null){
                                    r=assemablingMap.get(fieldName);
                                }
                                if (r!=null){
                                    Object o=r.object;
                                    assemablingMap0.put(k,v);
                                    try {
                                        field.set(v.object,o);
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    }catch (IllegalArgumentException e){
                                        e.printStackTrace();
                                    }
                                    iterator.remove();
                                }
                            }
                        }
                        if (v.reMethods.size()>=0){
                            Iterator<Method> iterator=v.reMethods.iterator();
                            while (iterator.hasNext()){
                                Method method=iterator.next();
                                Class[] c=method.getParameterTypes();

                                reInject reInject=method.getAnnotation(reInject.class);
                                String paraName;
                                if ("".equals(reInject.name())){
                                    paraName=c[0].getSimpleName();;
                                }else {
                                    paraName=reInject.name();
                                }

                                ReAssemabling r=assemablingMap0.get(paraName);
                                if (r==null){
                                    r=assemablingMap.get(paraName);
                                }
                                if (r!=null){
                                    Object o=r.object;
                                    assemablingMap0.put(k,v);
                                    try {
                                        method.invoke(v.object,o);
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    } catch (InvocationTargetException e) {
                                        e.printStackTrace();
                                    }
                                    iterator.remove();
                                }
                            }
                        }
                    }else {
                        if (v.aMethod!=null) {
                            if (v.object!=null){
                                List<Object> pl = new ArrayList<>();
                                Object object=null;
                                Class returnClass=v.aMethod.getReturnType();
                                int i=0;
                                for (Class cl : v.aMethod.getParameterTypes()) {
                                    String paraName = cl.getSimpleName();
                                    Object o = assemablingMap0.get(paraName);
                                    if (o == null) {
                                        o = assemablingMap.get(paraName);
                                    }
                                    if (o == null) {
                                        Annotation[] annotations = v.aMethod.getParameterAnnotations()[i];
                                        if(annotations!=null && annotations.length>0){
                                            paraName =((reInject)annotations[0]).name();
                                            o = assemablingMap.get(paraName);
                                        }
                                    }
                                    if (o != null) {
                                        ReAssemabling reAssemabling= (ReAssemabling) o;
                                        pl.add(reAssemabling.object);
                                    }
                                    i++;
                                }
                                if (pl.size() == v.aMethod.getParameterTypes().length) {
                                    try {
                                        object = v.aMethod.invoke(v.object,pl.toArray());
                                    } catch (IllegalAccessException | InvocationTargetException e) {
                                        e.printStackTrace();
                                    }
                                }
                                if (object!=null){
                                    v.object=object;
                                    v.aMethod=null;
                                    v.aClass=returnClass;
                                    assemablingMap0.put(k,v);
                                    assemablingIterator.remove();
                                }
                            }
                        }else {
                            assemablingMap0.put(k,v);
                            assemablingIterator.remove();
                        }
                    }
                }
            };

            if (0==assemablingMap.size()){
                if (b){
                    break;
                }
                b=true;
            }else {
                b=false;

            }
            System.out.println("注入次数： "+times);
            if (times>10){
                try {
                    throw new Exception("无法注入");
                } catch (Exception e) {
                    e.printStackTrace();
                    assemablingMap.forEach((k,v)->{
                        System.out.println(k);
                    });
                    break;
                }
            }
        }
        reAssemablingMap=assemablingMap0;

        conMap.forEach((k,v)->{
            WebController controller= (WebController) v.getAnnotation(WebController.class);
            String name=controller.name();
            for (Method method:v.getMethods()) {
                Api api=method.getAnnotation(Api.class);
                WebPath wpath=method.getAnnotation(WebPath.class);

                ReInfuse reInfuse= (ReInfuse) v.getAnnotation(ReInfuse.class);
                ReAssemabling reA;
                if (reInfuse!=null && !reInfuse.name().equals("")){
                    reA=assemablingMap0.get(reInfuse.name());
                }else {
                    reA=assemablingMap0.get(k);
                }
//                ReAssemabling reA=assemablingMap0.get(k);
                Object obj=null;
                if (reA==null){
                    try {
                        obj=v.newInstance();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }else {
                    obj=reA.object;
                }

                if(api==null&&wpath==null){
                    continue;
                }
                String url="/";
                if (wpath!=null){
                    int l=wpath.name().lastIndexOf("/{");
                    if (l<0){
                        url= name+"/"+wpath.name()+"/";
                    }else {
                        url= name+"/"+wpath.name().substring(0,l)+"/";
                    }

                    ProcessMapL.Nettybean nettybean=new ProcessMapL.Nettybean();
                    nettybean.setMethod(method).setAction(obj).setReturnType(method.getReturnType()).setmurl(url);
                    ProcessMapL.list.add(nettybean);
                }else {
                    if (!"".equals(name)){
                        url= "/"+name;
                        if (!"".equals(api.def())){
                            url= url+"/"+api.def();
                        }
                    }else {
                        url= "/"+api.def();
                    }

                    ProcessMapL.Nettybean nettybean=ProcessMapL.map.get(url);
                    if(nettybean==null){
                        nettybean=new ProcessMapL.Nettybean();
                        nettybean.setMethod(method).setAction(obj).setReturnType(method.getReturnType());
                        ProcessMapL.map.put(url,nettybean);
                    }else {
                        Class claOld = nettybean.getAction().getClass();
                        ReInfuse reold = (ReInfuse) claOld.getAnnotation(ReInfuse.class);
                        if(reold==null){
                            if (reInfuse == null) {
                                System.out.println("重复接口 "+nettybean);
                                continue;
                            }
                            nettybean = new ProcessMapL.Nettybean();
                            nettybean.setMethod(method).setAction(obj).setReturnType(method.getReturnType());
                            ProcessMapL.map.put(url, nettybean);
                            System.out.println("重复接口 "+nettybean);
                        }else {
                            if (reInfuse == null) {
                                System.out.println("重复接口 "+nettybean);
                                continue;
                            }
                            int i = reold.grade().compareTo(reInfuse.grade());
                            if (i < 0) {//reInfuse更大
                                nettybean = new ProcessMapL.Nettybean();
                                nettybean.setMethod(method).setAction(obj).setReturnType(method.getReturnType());
                                ProcessMapL.map.put(url, nettybean);
                            }
                        }

                    }
                }
            }
        });


        for (Class<?> clazz : classes) {
            if (clazz==null){continue;}
            //System.out.println("clazz.getName():"+clazz.getName());
            try {
                Annotation[] annotations=clazz.getAnnotations();
                if (annotations.length<=0){
                    continue;
                }
                WebController annotation=(WebController)clazz.getAnnotation(WebController.class);
                WebInterceptor interceptorannotation=(WebInterceptor)clazz.getAnnotation(WebInterceptor.class);

                if (interceptorannotation!=null){
                    InterceptScan.scan(clazz);
                }

                if (annotation==null){
                    continue;
                }
                if (! (annotation instanceof WebController)){
                    continue;
                }
                Field[] field=clazz.getDeclaredFields();
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
                    if (clzz.isInterface()|| clzz.isLocalClass() || clzz.isAnnotation()){
                        System.out.println(clzz.toString());
                        continue;
                    }
                    Constructor<?> bind= clzz.getConstructor();
                    //WebController annotation=(WebController)clzz.getAnnotation(WebController.class);
                    if (annotation==null){
                        continue;
                    }
                    if (! (annotation instanceof WebController)){
                        continue;
                    }
                    //System.out.println(JSON.toJSONString(bind));
                    String name=annotation.name();

                    Map<String, ProcessMap.Nettybean> map=new HashMap<>();
                    ProcessMap.mapMap.put(name,map);
                    for (Method method:clzz.getMethods()) {
                        Api api= method.getAnnotation(Api.class);
                        if (api==null){
                            continue;
                        }
                        String def=api.def();
                        Type re=method.getGenericReturnType();
                        // method.invoke(obg,new Object(),new Object());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        linkMap.forEach((k,v)->{
            Links controller= (Links) v.getAnnotation(Links.class);
            String name=controller.name();
            for (Method method:v.getMethods()) {
                Mod api=method.getAnnotation(Mod.class);
                if(api==null){
                    continue;
                }
                ReInfuse reInfuse= (ReInfuse) v.getAnnotation(ReInfuse.class);
                ReAssemabling reA;
                if (reInfuse!=null && !reInfuse.name().equals("")){
                    reA=assemablingMap0.get(reInfuse.name());
                }else {
                    reA=assemablingMap0.get(k);
                }
//                ReAssemabling reA=assemablingMap0.get(k);
                Object obj=null;
                if (reA==null){
                    try {
                        obj=v.newInstance();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }else {
                    obj=reA.object;
                }

                String url;
                if (!"".equals(name)){
                    url= name;
                    if (!"".equals(api.def())){
                        url= url+"."+api.def();
                    }
                }else {
                    url= api.def();
                }

//                LinkProcessMap.Linkbean linkbean=new LinkProcessMap.Linkbean();
//                linkbean.setMethod(method).setAction(obj).setReturnType(method.getReturnType());
//                LinkProcessMap.map.put(url,linkbean);


                LinkProcessMap.Linkbean linkbean=LinkProcessMap.map.get(url);
                if(linkbean==null){
                    linkbean=new LinkProcessMap.Linkbean();
                    linkbean.setMethod(method).setAction(obj).setReturnType(method.getReturnType());
                    LinkProcessMap.map.put(url,linkbean);
                }else {
                    Class claOld = linkbean.getAction().getClass().getClass();
                    ReInfuse reold = (ReInfuse) claOld.getAnnotation(ReInfuse.class);
                    if(reold==null){
                        if (reInfuse == null) {
                            System.out.println("重复link接口 "+linkbean);
                            continue;
                        }
                        linkbean=new LinkProcessMap.Linkbean();
                        linkbean.setMethod(method).setAction(obj).setReturnType(method.getReturnType());
                        LinkProcessMap.map.put(url,linkbean);
                        System.out.println("重复link接口 "+linkbean);
                    }else {
                        if (reInfuse == null) {
                            System.out.println("重复link接口 "+linkbean);
                            continue;
                        }
                        int i = reold.grade().compareTo(reInfuse.grade());
                        if (i < 0) {//reInfuse更大
                            linkbean=new LinkProcessMap.Linkbean();;
                            linkbean.setMethod(method).setAction(obj).setReturnType(method.getReturnType());
                            LinkProcessMap.map.put(url,linkbean);
                        }
                    }
                }
            }
        });


        for (Class<?> clazz : classes) {
            if (clazz==null){continue;}
            //System.out.println("clazz.getName():"+clazz.getName());
            try {
                Annotation[] annotations=clazz.getAnnotations();
                if (annotations.length<=0){
                    continue;
                }
                Links annotation=(Links)clazz.getAnnotation(Links.class);
                LinkInterceptor interceptorannotation=(LinkInterceptor)clazz.getAnnotation(LinkInterceptor.class);

                if (interceptorannotation!=null){
                    InterceptScan.scan(clazz);
                }

                if (annotation==null){
                    continue;
                }
                Field[] field=clazz.getDeclaredFields();
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
                    if (clzz.isInterface()|| clzz.isLocalClass() || clzz.isAnnotation()){
                        System.out.println(clzz.toString());
                        continue;
                    }
                    Constructor<?> bind= clzz.getConstructor();
                    //WebController annotation=(WebController)clzz.getAnnotation(WebController.class);
                    //System.out.println(JSON.toJSONString(bind));
                    String name=annotation.name();

                    Map<String, LinkProcessMap.Linkbean> map=new HashMap<>();
                    LinkProcessMap.mapMap.put(name,map);
                    for (Method method:clzz.getMethods()) {
                        Mod api= method.getAnnotation(Mod.class);
                        if (api==null){
                            continue;
                        }
                        String def=api.def();
                        Type re=method.getGenericReturnType();
                        // method.invoke(obg,new Object(),new Object());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    //扫描指定文件夹下的所有类文件，添加到结果列表中
    public static void scanDir(String packageName, String dirPath, List<Class<?>> classes) {
//        dirPath=dirPath.substring(1);
        File dir = new File(dirPath); //根据文件夹路径创建File对象
        if (dir.isDirectory()) { //判断是否是文件夹
            File[] files = dir.listFiles(); //获取文件夹下的所有文件或子文件夹
            if (files != null) { //判断是否为空
                for (File file : files) { //遍历每个文件或子文件夹
                    String className=file.getPath();
                    String fileName = file.getName(); //获取文件名或子文件夹名
                    if (file.isFile() && fileName.contains(".class")) { //如果是类文件，去掉后缀名，拼接包名和类名，得到全限定类名
                        try {
                            //System.out.println(className);
                            //className=packageName+"."+fileName;
                            className=packageName+"."+fileName.replace(".class","");
                            Class<?> clazz = Class.forName(className); //根据全限定类名加载Class对象
                            classes.add(clazz); //添加到结果列表中
                        } catch (Exception | Error e) {
                            e.printStackTrace();
                        }
                    } else if (file.isDirectory()) { //如果是子文件夹，递归调用本方法，传入子包名和子文件夹路径
                        scanDir(packageName + "." + fileName, file.getPath(), classes);
                    }
                }
            }
        }
    }
    public static void ScanHook(){
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            System.out.println("You clicked confirm."); // 打印点击确认按钮的信息
            System.out.println("   "+classes.size());
            System.out.println("   "+classes.get(classes.size()-1).getName());
            // 退出程序
        }));
    }

    public static class PreCreateObject{

        Class aClass;

        Map<String,Field> map;

        public void setaClass(Class aClass) {
            this.aClass = aClass;
        }

    }
    public static class ProCreateObject{

        Class aClass;

        Map<String,Field> mapField;

        Map<String,Class> mapInit;

        public void setaClass(Class aClass) {
            this.aClass = aClass;
        }

    }
}
