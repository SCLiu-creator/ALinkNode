package superlink.udpbind.client.recives.datalen.dataCache;

import superlink.util.datastack.Data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;

public class BufferDataCon {
    public static ConcurrentHashMap<String,DataCon> dataMap=new ConcurrentHashMap();

    public static byte[] getData(String key){
        DataCon dataCon=dataMap.get(key);
        if (dataCon==null)return null;
        if (dataCon.i==null){
            if (dataCon.time==null){
                return dataCon.bytes;
            }else {
                if ((dataCon.time-System.currentTimeMillis())>0){
                    return dataCon.bytes;
                }else {
                    dataMap.remove(key);
                    return null;
                }
            }
        }else {
            if (dataCon.i<=0){
                dataMap.remove(key);
                return null;
            }else {
                if (dataCon.time==null){
                    dataCon.i--;
                    if (dataCon.i<=0){
                        dataMap.remove(key);
                    }
                    return dataCon.bytes;
                }else {
                    if ((dataCon.time-System.currentTimeMillis())>0){
                        dataCon.i--;
                        if (dataCon.i<=0){
                            dataMap.remove(key);
                        }
                        return dataCon.bytes;
                    }else {
                        dataMap.remove(key);
                        return null;
                    }
                }
            }
        }
    }
    public static void setData(String key,byte[] bytes){
        DataCon dataCon=new DataCon();
        dataMap.put(key,dataCon);
        dataCon.bytes=bytes;

    }
    public static void setData(String key,byte[] bytes,int i){
        DataCon dataCon=new DataCon();
        dataMap.put(key,dataCon);
        dataCon.bytes=bytes;
        dataCon.i=i;
    }
    public static void setData(String key,byte[] bytes,Long l){
        DataCon dataCon=new DataCon();
        dataMap.put(key,dataCon);
        dataCon.bytes=bytes;
        dataCon.time=System.currentTimeMillis()+l;
    }
    public static void setData(String key,byte[] bytes,int i,Long l){
        DataCon dataCon=new DataCon();
        dataMap.put(key,dataCon);
        dataCon.bytes=bytes;
        dataCon.i=i;
        dataCon.time=System.currentTimeMillis()+l;
    }

    public static byte[] toData(Object data){
        if (data==null){//file..mkdirs();
            return new byte[0];
        }
        if (data instanceof byte[]){
            return (byte[])data;
        }
        if (data instanceof ByteBuffer){
            ByteBuffer buffer=(ByteBuffer)data;
            return buffer.array();
        }
        if (data instanceof File){
            FileInputStream fileFrom;
            File file=(File)data;
            try {
                fileFrom=new FileInputStream(file);
                byte[] bytes=new byte[(int) file.length()];
                fileFrom.read(bytes);
                fileFrom.close();
                return bytes;
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                ((File) data).delete();
            }
        }
        return null;
    }


    public static class DataCon{
        public Integer i=null;
        public Long time =null;
        public byte[] bytes;
    }
}
