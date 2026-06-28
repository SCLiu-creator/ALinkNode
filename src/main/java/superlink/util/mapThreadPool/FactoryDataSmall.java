package superlink.util.mapThreadPool;

import superlink.udpbind.client.recives.datalen.DataSmall;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
//废弃
public class FactoryDataSmall {
    static Map<String, WeakReference<DataSmall>> stringDataSmallMap=new HashMap<>();
    public static DataSmall getDataSmall(String usernme) throws Exception {
        DataSmall dataSmall=null;
        WeakReference<DataSmall> dataSmallWeakReference=stringDataSmallMap.get(usernme);

        if (dataSmallWeakReference==null){
            dataSmall=new DataSmall(usernme);
            stringDataSmallMap.put(usernme,new WeakReference<>(dataSmall));
        }else {
            dataSmall=dataSmallWeakReference.get();
            if (dataSmall==null){
                dataSmall=new DataSmall(usernme);
                stringDataSmallMap.put(usernme,new WeakReference<>(dataSmall));
            }
        }
        return dataSmall;
    }
}
