package superlink.testjava;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ProxyClassTest {
    public static void main(String[] args) {
        fi o= (fi)Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class<?>[]{fi.class}, new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        System.out.println("getName: "+proxy.getClass().getName());
                        System.out.println("getPackage: "+proxy.getClass().getPackage());
                        System.out.println("method: "+method.getName());
                        System.out.println("out args: "+args[1]);
                        System.out.println("getResource: "+proxy.getClass().getResource(""));
                        System.out.println("getResource: "+proxy.getClass().getResource("/"));
                        return proxy;
                    }
                });
        o.apply("1","2");
    }
}
