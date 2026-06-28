package superlink.filemanage.classprocess;

import com.alibaba.fastjson2.JSON;
import superlink.filemanage.scanpackage.FileScan;
import superlink.filemanage.xmltool.XmlParser;
import superlink.httpserver.ProcessMap;
import superlink.httpserver.servelt.Interceptor.WebInterceptor;
import superlink.httpserver.servelt.ProcessMapL;
import superlink.httpserver.servelt.WebController;
import superlink.httpserver.servelt.action.Api;
import superlink.httpserver.servelt.action.WebPath;
import superlink.filemanage.classprocess.property.ReInfuse;
import superlink.filemanage.classprocess.property.reInject;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.net.DatagramSocket;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class OutJarload {
    public static boolean b;
    static URL url;
    static String packageName;
    static Map<String,Class> classMap=new HashMap<>();

    public static void main(String[] args) {
        try {
            String s="jar:file:/"+"D:\\java\\新建文件夹\\udpclient\\target\\original-clientmain.jar"+"!/";
            URL url=new URL(s);
            java.util.List<Class<?>> classList=scanPackage(url);
            for (Class c:classList){
                System.out.println(c.getName());
            }
            System.out.println(url.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        OutJarload outJarload=new OutJarload();
        outJarload.scanContent();
        outJarload.startscan();
    }
    public List<URL> scanContent(){
        File file=new File(XmlParser.extend);
        List<String> list=new ArrayList();
        FileScan.scanPackage(file,list,XmlParser.extend);
        List<URL> urls=new ArrayList();
        for (String s:list){
            try {
                s="jar:file:/"+s+"!/";
                urls.add(new URL(s));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return urls;
    }

    public static java.util.List<Class<?>> scanPath(String path) throws Exception {
        String s="jar:file:/"+"D:\\java\\新建文件夹\\udpclient\\target\\original-clientmain.jar"+"!/";
        URL url=new URL(s);
        return scanPackage(url);
    }

    public void startscan(){
        try {
            url=new File("jar:file:/"+"D:/java/新建文件夹/udpservlet/target/udpservletmain-1.0-SNAPSHOT-jar-with-dependencies.jar"+"!/").toURI().toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        String path="jar:file:/"+"D:/java/新建文件夹/udpservlet/target/udpservletmain-1.0-SNAPSHOT-jar-with-dependencies.jar"+"!/";
        try {
            url=new URL(path);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        String protocol= url.getProtocol();
        if ("jar".equals(protocol)) {
            b= true;
        }else {
            b= false;
        }
        //调用scanPackage方法，传入包名，返回包下的所有类的Class对象的列表
        List<Class<?>> classes = scanPackage(url);
        //url: classname/methonname
        //打印结果
        for (Class<?> clazz : classes) {
            System.out.println("clazz.getName():"+clazz.getName());
            Field[] field=clazz.getDeclaredFields();
            try {

                Constructor<?>[] constructors=clazz.getDeclaredConstructors();
                Class<?>[] paramterTypes = new Class[0];
//                for (Constructor<?> constructor:constructors){
//                    paramterTypes=constructor.getParameterTypes();
//                    for (Class<?> param:paramterTypes){
//                        System.out.println(param);
//                    }
//                }
                paramterTypes=constructors[0].getParameterTypes();
                if (paramterTypes.length==0){
                    if (clazz.isInterface()|| clazz.isLocalClass() || clazz.isAnnotation()){
                        System.out.println(clazz.toString());
                        continue;
                    }
                    Constructor<?> bind= clazz.getConstructor();
                    System.out.println(JSON.toJSONString(bind));
                    Object obg= bind.newInstance();
                    Class claob=obg.getClass();

                    classMap.put(claob.getName(),claob);
                    Map<String, ProcessMap.Nettybean> map=new HashMap<>();
                    ProcessMap.mapMap.put(claob.getName(),map);
                    for (Method method:clazz.getMethods()) {
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
                                Map<Object,Object> meap=new HashMap<>();
                                System.out.println(method.getName()+"的返回值类型是参数化类型，其类型为："+"<"+k.toString()+">");


                            }

                        }else {
                            //不是参数化类型,直接获取返回值类型
                            Class<?> returnType = method.getReturnType();
                            ProcessMap.Nettybean nettybean=new ProcessMap.Nettybean();
                            nettybean.setAction(obg).setMethod(method).setReturnType(re);
                            map.put(method.getName(),nettybean);
                            //获取返回值类型的类名
                            String name = returnType.getName();
                            System.out.println(method.getName()+"的返回值类型不是参数化类型其类型为："+name);

                        }


                        // method.invoke(obg,new Object(),new Object());
                    }

                }else {
                    Object[] paramter=new Class[paramterTypes.length];
                    int i=0;
                    for (Class c:paramterTypes){
                        String s=c.getName();

                        Object o=c.newInstance();
                        paramter[i]=o;
                        i++;
                    }
                    Constructor<?> Bind= clazz.getConstructor(paramterTypes);
                    System.out.println(JSON.toJSONString(Bind));

                    Bind= clazz.getConstructor(DatagramSocket.class,Integer.class);
                    Object object = (Object) Bind.newInstance(new DatagramSocket(8888),new Integer(10000));
                    try {
                        Field field1=object.getClass().getDeclaredField(field.toString());
                        Method method=clazz.getMethod("poss");
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
    public static java.util.List<Class<?>> scanPackage(URL urlpath) {
        java.util.List<Class<?>> classes = new ArrayList<>(); //存放结果的列表
        String packageName=urlpath.toString();

        try {
            URL[] url1=new URL[1];
            url1[0]=urlpath;
            Jarloader urlClassLoader=new Jarloader(url1,classLoader);
            urlClassLoader.addURL(urlpath);
            classLoader=urlClassLoader;
            //获取当前线程的类加载器，用于加载资源
            //遍历每个URL对象
            path=packageName;
            scanJar(urlpath, classes);
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
                        //Class c= Jarclass.doclass(className,path);
//                        if (className.contains("Retrofit2ConverterFactory")){
//                            continue;
//                        }
                        try {
                            Class<?> clazz = classLoader.loadClass(className);
                            //根据全限定类名加载Class对象
                            classes.add(clazz); //添加到结果列表中
                        }catch (Error|Exception n){
                            n.printStackTrace();
                           System.out.println("errorLoadClass: "+className);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static Map<String, AutoScan.ReAssemabling> autoScanReAssemsble(List<Class<?>> classes){
        Map<String,Class> conMap=new HashMap<>();
        Map<String,Class> refMap=new HashMap<>();
        for (Class<?> clazz : classes) {
            Annotation[] annotations=null;
            try {
                annotations=clazz.getAnnotations();
                if (annotations==null||annotations.length<=0){
                    continue;
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            try {
                WebController controller=clazz.getAnnotation(WebController.class);
                ReInfuse reInfuse=clazz.getAnnotation(ReInfuse.class);

                if (reInfuse!=null){
                    if (reInfuse.name().equals("")){
                        refMap.put(clazz.getSimpleName(),clazz);
                    }else {
                        refMap.put(reInfuse.name(),clazz);
                    }
                }
                if (controller!=null){
                    conMap.put(clazz.getSimpleName(),clazz);
                }
            }catch (Exception e){
                continue;
            }
        }

        Map<String, AutoScan.ReAssemabling> assemablingMap=new HashMap<>();
        Map<String, AutoScan.ReAssemabling> assemablingMap0=new HashMap<>();
        refMap.forEach((k,v)->{
            AutoScan.ReAssemabling reAssemabling=null;
            Method[] methods=v.getDeclaredMethods();
            Field[] fields= v.getDeclaredFields();

            reAssemabling=new AutoScan.ReAssemabling(v);
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
                AutoScan.ReAssemabling finalReAssemabling = reAssemabling;
                assemablingMap.compute(k,(key, value)-> {
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
            }else {
                AutoScan.ReAssemabling finalReAssemabling = reAssemabling;
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
            }

            for (Method method:methods){
                reInject reInject=method.getAnnotation(reInject.class);
                if (reInject!=null){
                    method.setAccessible(true);
                    reAssemabling.reMethods.add(method);
                }
                ReInfuse reInfuse1=method.getAnnotation(ReInfuse.class);
                if (reInfuse1!=null){
                    AutoScan.ReAssemabling reAssemablingm =new AutoScan.ReAssemabling(method);
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
                        AutoScan.ReAssemabling finalReAssemablingm=reAssemablingm;
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
                        AutoScan.ReAssemabling finalReAssemablingm=reAssemablingm;
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
        while (true){
            Iterator<Map.Entry<String, AutoScan.ReAssemabling>> assemablingIterator=assemablingMap.entrySet().iterator();
            while (assemablingIterator.hasNext()){
                Map.Entry<String, AutoScan.ReAssemabling> item = assemablingIterator.next();
                String k=item.getKey();
                AutoScan.ReAssemabling v=item.getValue();
                if (v.object==null){
                    List<Object> pl = new ArrayList<>();
                    for (Class cl : v.paras) {
                        String paraName = cl.getSimpleName();
                        Object o = assemablingMap0.get(paraName);
                        if (o == null) {
                            o = assemablingMap.get(paraName);
                        }
                        if (o != null) {
                            AutoScan.ReAssemabling reAssemabling= (AutoScan.ReAssemabling) o;
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
                                AutoScan.ReAssemabling r=assemablingMap0.get(fieldName);
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

                                AutoScan.ReAssemabling r=assemablingMap0.get(paraName);
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
                                for (Class cl : v.aMethod.getParameterTypes()) {
                                    String paraName = cl.getSimpleName();
                                    Object o = assemablingMap0.get(paraName);
                                    if (o == null) {
                                        o = assemablingMap.get(paraName);
                                    }
                                    if (o != null) {
                                        AutoScan.ReAssemabling reAssemabling= (AutoScan.ReAssemabling) o;
                                        pl.add(reAssemabling.object);
                                    }
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
        }

        return assemablingMap0;
    }

    public static void reIntoWebMap(Map<String, AutoScan.ReAssemabling> reAssemablingMap,List<Class<?>> classes){
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

                WebController controller=annotation;
                ReInfuse reInfuse=clazz.getAnnotation(ReInfuse.class);
                AutoScan.ReAssemabling reAssemabling = null;
                if (reInfuse!=null){
                    if (reInfuse.name().equals("")){
                        reAssemabling=reAssemablingMap.get(clazz.getSimpleName());
                    }else {
                        reAssemabling=reAssemablingMap.get(reInfuse.name());
                    }
                }else {
                    reAssemabling=reAssemablingMap.get(clazz.getSimpleName());
                }
                Object obj=null;
                if (reAssemabling==null){
                    try {
                        obj=clazz.newInstance();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }else {
                    obj=reAssemabling.object;
                }

                String name=controller.name();
                for (Method method:clazz.getMethods()) {
                    Api api=method.getAnnotation(Api.class);
                    WebPath wpath=method.getAnnotation(WebPath.class);

//                ReAssemabling reA=assemablingMap0.get(k);

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
                        }
                        if (!"".equals(name)){
                            url= url+"/"+api.def();
                        }
                        ProcessMapL.Nettybean nettybean=new ProcessMapL.Nettybean();
                        nettybean.setMethod(method).setAction(obj).setReturnType(method.getReturnType());
                        if (!ProcessMapL.map.containsKey(url)){
                            ProcessMapL.map.put(url,nettybean);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
