package superlink.filemanage.classprocess;

import io.netty.handler.codec.http.FullHttpRequest;
import superlink.httpserver.ProcessMap;

public class InterceptScan {
    public static void scan(Class clazz){
        Object o = null;
        try {
            o=clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (o!=null){
            ProcessMap.NettyIntercepter intercepter=new ProcessMap.NettyIntercepter();
            try {
                intercepter.setIntercepter(o).setMethod(clazz.getMethod("prehandle", FullHttpRequest.class));
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            ProcessMap.interceptorList.add(intercepter);
        }

    }
}
