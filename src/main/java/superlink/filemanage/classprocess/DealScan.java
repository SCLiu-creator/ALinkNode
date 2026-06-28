package superlink.filemanage.classprocess;

import superlink.httpserver.ProcessMap;
import superlink.httpserver.dealAction.Deal;

import java.util.List;

public class DealScan {

    public static void scanClass(List<Class<?>> classes){

        for (Class clazz:classes){
            Deal dealannotation=(Deal)clazz.getAnnotation(Deal.class);
            if (dealannotation!=null){
                DealScan.scan(clazz);
            }
        }

    }

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
            ProcessMap.NettyDeal nettyDeal=new ProcessMap.NettyDeal();
            try {
                nettyDeal.setDealer(o).setMethod(clazz.getMethod("deal",String.class));
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            ProcessMap.dealMap.put("deal",nettyDeal);
        }

    }
}
