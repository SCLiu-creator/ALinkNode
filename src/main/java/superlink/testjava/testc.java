package superlink.testjava;

import superlink.util.Tool;
import superlink.util.Utils;

import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static superlink.util.SHAutils.byteArrayToHexString;

public class testc {
    public static void  aa(){
        System.out.println("init:int");
    }
    public void tt() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException, IOException {
//        byte[] bytesb=AutoBuffer.praseBuffer(":xmlfile&:D:\\tu");
//        Document document=byetToDocument(bytesb);
//        Document document1=XmlParser.openXml("C:\\Users\\liusc\\Desktop\\新建文件夹xml.xml");
//
//        Element element=document1.getRootElement();
//        while (element.elements().size()>1){
//            element= (Element) element.elements().get(0);
//        }
//        Element ele=document.getRootElement().getParent();
//        element.add(document.getRootElement());
//        ele=document.getRootElement().getParent();
//        XmlParser.SaveXml(document1,"C:\\Users\\liusc\\Desktop\\新建文件夹xml1.xml");
        MessageDigest messageDigest;
        try {
            //获得SHA转换器
            messageDigest = MessageDigest.getInstance("MD5");
            //bytes是输入字符串转换得到的字节数组
            messageDigest.update(new String().getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA签名过程中出现错误,算法异常");
        }
        //转换并返回结果，也是字节数组，包含16个元素
        byte[] digest = messageDigest.digest();
        String result = byteArrayToHexString(messageDigest.digest());
        byte[] bytesss= Utils.intToByteArray(1817228887);

        if ((Object)bytesss instanceof Byte[]){
            System.out.println("true");
        }
        File file=new File("C:\\Users\\liushengchang-n\\Desktop\\keras.py");
        String s= Tool.getPrex("C:\\Users\\liushengchang-n\\Desktop\\keras.py");
        FileInputStream fileInputStream=new FileInputStream(file);
        BufferedInputStream byteArrayInputStream=new BufferedInputStream(fileInputStream);

        byte[] bytes=new byte[20];
        byteArrayInputStream.mark(1);
        byteArrayInputStream.read(bytes);
        System.out.println(new String(bytes));
        byteArrayInputStream.reset();
        byteArrayInputStream.skip(0);
        byteArrayInputStream.read(bytes);
        System.out.println(new String(bytes));


        String projectPath = System.getProperty("user.dir");					//获取当前eclipse工程路径
        String classPath = this.getClass().getResource("/").toString();			//获取当前classPath
        URL classPath2 = this.getClass().getClassLoader().getResource("");	//获取当前classPath等同上一行代码
        String classFullPath = this.getClass().getResource("").toString();		//获取当前类基于classPath的完整路径

        System.out.println("projectPath"+projectPath);
        System.out.println(classPath);
        System.out.println(classPath2.toString());
        System.out.println("classFullPath"+classFullPath);

        Ii l=new I1();
        Class<?> cc=Class.forName("testjava.Ii");
        URLClassLoader classLoader= (URLClassLoader) l.getClass().getClassLoader();
        System.out.println("getResource:"+this.getClass().getResource("/").toString());
        System.out.println(Ii.class);
        System.out.println(cc);
        System.out.println(cc.toString());
        URL url=classLoader.findResource(String.valueOf(cc));
        //  String s=url.getPath();
//        Object c=classLoader.loadClass(String.valueOf(cc));

        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
//        Class<?> helloClass = systemClassLoader.loadClass(String.valueOf(cc));
        Method method=cc.getMethod("ll",null);
        cc=Class.forName("testjava.I1");
        String sna=cc.getName();
        Class ca=Class.forName(sna);
        Object a = ca.newInstance();

//        Object a1 = Class.forName("a").newInstance();
//        Object b = Class.forName("b").newInstance();
        //通过反射调用a和b的方法
        //method.invoke(a, "hello");
        method.invoke(a);
      //  method.invoke(b, "world");
    }

    public static class my implements InvocationHandler{

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return null;
        }
    }

    public static void main(String[] args) {
        long l1= (long) Math.log(120*20000);
        try {
            new testc().tt();
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        Class c=testc.class;
        System.out.println(c.hashCode());
        //new Object().toString()
        System.out.println(Thread.currentThread().getContextClassLoader());

        //Proxy.newProxyInstance(Thread.currentThread().getClass().getClassLoader(), (Class<?>[]) new Object(),new my());
    }
}
