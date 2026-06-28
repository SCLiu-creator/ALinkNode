package superlink.tcpbind;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import sun.misc.Unsafe;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.PrivilegedExceptionAction;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class CacheDemo {

    //创建一个缓存对象
    private static Cache<String, String> cache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.SECONDS) //设置缓存过期时间为10秒
            .maximumSize(100) //设置缓存最大容量为100
            .build();


    //创建一个阻塞队列
    private static BlockingQueue<String> queue = new LinkedBlockingQueue<>(1);
    public  static ByteBuffer buffer2=ByteBuffer.wrap(new byte[1000]);//ByteBuffer.allocateDirect(1024*1024*1024);
    public static void main(String[] args) throws InterruptedException, IOException {
        String s0="0121231234";
        String[] strings0=s0.split("1",0);
        String[] strings1=s0.split("1",1);
        String[] strings2=s0.split("1",2);
        String[] strings3=s0.split("1",3);


        ServerSocket serverSocketket=new ServerSocket(8000,3);

        ServerSocket serverSocketket1=new ServerSocket(8001);
        ServerSocket serverSocketket2=new ServerSocket(8001);
        new Thread(){
            @Override
            public void run(){
                try {
                    byte[] bytes=new byte[10000];bytes[0]=74;bytes[2]=76;
                    Socket socket=serverSocketket.accept();
                    socket.getOutputStream().write(bytes);
                    socket.getOutputStream().write(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(){
            @Override
            public void run(){
                try {
                    byte[] bytes=new byte[10000];bytes[0]=77;bytes[2]=78;
                    Socket socket=serverSocketket.accept();
                    socket.getOutputStream().write(bytes);
                    socket.getOutputStream().write(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };


        Unsafe unsafe=Unsafe.getUnsafe();
        unsafe.allocateMemory(1024*1024*1024);
        unsafe.allocateMemory(1111);

        ByteBuffer buffer = ByteBuffer.allocate(1024);//分配1024个字节的空间
        ByteBuffer buffer2=ByteBuffer.allocateDirect(1024*1024*1024);
        buffer.put("value".getBytes()); //向缓冲区中写入字节数据


        //创建一个线程模拟缓存数据的更新
        new Thread(() -> {
            try {
                Thread.sleep(50000); //模拟延迟5秒
                buffer.put("aaaa".getBytes());
                cache.put("key", "value"); //更新缓存数据
                System.out.println("缓存数据已更新");
                queue.put("done"); //向队列中放入一个标志
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        //调用方法获取缓存数据
        ByteBuffer data = getData2("key");
        System.out.println("获取到的数据：" + new String(data.array()));

        //调用方法获取缓存数据
        String data2 = getData("key");
        System.out.println("获取到的数据：" + data);
    }

    //定义一个方法，返回缓存数据，如果缓存为空就阻塞线程直到缓存有值为止
    public static String getData(String key) throws InterruptedException {
        String data = cache.getIfPresent(key); //从缓存中获取数据
        if (data == null) { //如果缓存为空
            System.out.println("缓存为空，等待更新");
            queue.take(); //从队列中取出一个元素，如果队列为空则阻塞
            data = cache.getIfPresent(key); //再次从缓存中获取数据
        }
        return data; //返回数据
    }

    //定义一个方法，返回缓存数据，如果缓存为空就阻塞线程直到缓存有值为止
    public static ByteBuffer getData2(String key) throws InterruptedException {
        ByteBuffer data = buffer2.get(new byte[10]); //从缓存中获取数据
        if (data == null) { //如果缓存为空
            System.out.println("缓存为空，等待更新");
            queue.take(); //从队列中取出一个元素，如果队列为空则阻塞
            data = buffer2.get(new byte[10]); //再次从缓存中获取数据
        }
        return data; //返回数据
    }


    public void getunsafe() throws NoSuchFieldException, IllegalAccessException {
        Class klass = Unsafe.class;
        Field field = klass.getDeclaredField("theUnsafe");
        field.setAccessible(true);
        Unsafe unsafe = (Unsafe) field.get(null);
        System.out.println(unsafe.toString());
    }

    public static Unsafe getUnsafe() {
        try {
            return sun.misc.Unsafe.getUnsafe();
        } catch (SecurityException tryReflectionInstead) {
        }
        try {
            return java.security.AccessController.doPrivileged(
                    (PrivilegedExceptionAction<Unsafe>) () -> {
                        Class<Unsafe> k = Unsafe.class;
                        for (Field f : k.getDeclaredFields()) {
                            f.setAccessible(true);
                            Object x = f.get(null);
                            if (k.isInstance(x)) return k.cast(x);
                        }
                        throw new NoSuchFieldError("the Unsafe");
                    });
        } catch (java.security.PrivilegedActionException e) {
            throw new RuntimeException("Could not initialize intrinsics", e.getCause());
        }
    }

    public static Unsafe reflectGetUnsafe() {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            return (Unsafe) field.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main2(String[] args) throws NoSuchFieldException, IllegalAccessException, InstantiationException {
        Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        Unsafe unsafe = (Unsafe) theUnsafe.get(null);
        InnerClass o = (InnerClass)unsafe.allocateInstance(InnerClass.class);
        o.print(); // print 100
        Field a = o.getClass().getDeclaredField("value");
        unsafe.putLong(o, unsafe.objectFieldOffset(a), 10000);
        o.print(); // print 10000
        unsafe.compareAndSwapLong(o, unsafe.objectFieldOffset(a), 10000, 1111);
        o.print(); // print 1111
        unsafe.compareAndSwapLong(o, unsafe.objectFieldOffset(a), 1000, 10000);
        o.print(); // print 1111
    }

    static class InnerClass {
        // 保证内存可见性
        private volatile long value;
        InnerClass() {
            value = 100L;
        }
        void print() {
            System.err.println("value==>" + value);
        }
    }

}
