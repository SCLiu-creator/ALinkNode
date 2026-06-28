package superlink.udpbind.client.recives.datalen.dataCache;

import superlink.util.SHAutils;

import java.util.HashMap;
import java.util.Map;

public class ByteCache {
    public static cacheInterface cache=null;
    {

    }
    public static Object get(String string){
        return cache.get(string);

    }
    public static void set(Object s,Object o){
         cache.set(s,o);
    }

    public static String set(Object o){
        return cache.set((byte[]) o);
    }


    public static class byteCacheDefault implements cacheInterface{

        Map<Object,byte[]> map=new HashMap<>();


        @Override
        public Object get(String s) {
            return map.get(s);
        }

        @Override
        public void set(Object o, Object s) {
            map.put(o,(byte[]) s);
        }


        @Override
        public String set(byte[] b) {
            String s=SHAutils.getShaFromByte(b,SHAutils.SHA_1,false);
            map.put(s,b);
            return s;
        }
    }

}
