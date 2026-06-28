package superlink.udpbind.client.recives.datalen;

import sun.nio.ch.ThreadPool;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;
import superlink.udpbind.handle.Handler;
import superlink.util.Utils;
import superlink.util.datastack.Data;
import superlink.util.prioityThreadPool.PriorityThreadPoolExecutor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.*;

public class DsCon {
    static int  value=10*1024*1024;
    public static Map<String, DataCreate> byteHashBuffer =new HashMap();
    public static Map<DataSyn, DataSyn> dataSynMap =new HashMap();
    public static TreeMap<Long,DataSyn> waitTimes=new TreeMap();
    public static ArrayBlockingQueue<DataSyn> dataMapBuf =new ArrayBlockingQueue(1000);
    static ThreadPoolExecutor threadPool = new PriorityThreadPoolExecutor(
            0,                      // 核心线程数
            2,                      // 最大线程数
            60L,                    // 空闲线程存活时间
            3, // 任务队列,
            (r, executor) -> {
                return;
            }
    );


    public static DataSyn getInstance(UserContext userContext,short id) {
        DataSyn dataSyn=new DataSyn(userContext,id);
        DataSyn rData=dataSynMap.get(dataSyn);
        if (rData==null){
            dataSynMap.put(dataSyn,dataSyn);
            return dataSyn;
        }else {
            return rData;
        }
    }


    public static long waitIngTime=System.nanoTime();
    public static long sleepTime;
    public static Runnable run=()->{
        Iterator<Map.Entry<String, DataCreate>> iterator=byteHashBuffer.entrySet().iterator();
        if(iterator.hasNext()){
            Map.Entry<String, DataCreate> entry=iterator.next();
            DataCreate dataCreate=entry.getValue();
            if (dataCreate.isReadOver()){
                iterator.remove();
            }
        }
    };

    public static Runnable proc=()->{
        while (dataSynMap.size()>0){
            try {
                sleepTime=System.currentTimeMillis();
                if (waitTimes.size()>0){
                    Thread.currentThread().wait(waitTimes.firstKey());
                }else {
                    Thread.currentThread().wait(30);
                }
            } catch (InterruptedException interruptedException) {
                System.out.println(interruptedException.getMessage());
            }


            while (dataMapBuf.size()>0){
                DataSyn dataSyn=dataMapBuf.poll();
                if (dataSyn.signCall!=null){
                    try {
                        boolean b=dataSyn.signCall.call();
                        if(!b){
                            continue;
                        }
                    } catch (Exception e) {
                        dataSynMap.remove(dataSyn);
                    }
                }
                if (dataSyn.process!=null){
                    try {
                        boolean b=dataSyn.process.call();
                        if(b){
                            dataSynMap.remove(dataSyn);
                        }else {
                            waitTimes.put(dataSyn.time,dataSyn);
                        }
                    } catch (Exception e) {
                        dataSynMap.remove(dataSyn);
                    }
                }
            }

            if ((System.currentTimeMillis()-sleepTime)<30){
                continue;
            }

            Iterator<Map.Entry<DataSyn, DataSyn>> iterator=dataSynMap.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry<DataSyn, DataSyn> entry=iterator.next();
                DataSyn dataSyn=entry.getValue();
                if (dataSyn.signCall!=null){
                    try {
                        boolean b=dataSyn.signCall.call();
                        if(!b){
                            continue;
                        }
                    } catch (Exception e) {
                        iterator.remove();
                    }
                }
                if (dataSyn.process!=null){
                    try {
                        boolean b=dataSyn.process.call();
                        if(b){
                            iterator.remove();
                        }else {
                            waitTimes.put(dataSyn.time,dataSyn);
                        }
                    } catch (Exception e) {
                        iterator.remove();
                    }
                }
            }
        }
    };

    public static boolean jt(){
        java.util.Map.Entry<Long, DataSyn> entry=waitTimes.firstEntry();
        if ((System.currentTimeMillis()-entry.getKey())<0){
            return true;
        }else {
            return false;
        }
    }
    public static void add(DataSyn syn){
        dataSynMap.put(syn,syn);
        run();
    }
    public static void run(){
        if(threadPool.getActiveCount()==0){
            threadPool.execute(proc);
        }else {
            if(threadPool.getQueue().size()<1){
                threadPool.execute(proc);
            }
        }
    }
    //
    public static byte[] readData(String name){
        DataCreate dataCreate=byteHashBuffer.get(name);
        byte[] bytes=dataCreate.getData();
        threadPool.execute(run);
        return bytes;
    }
    public static void setData(String name,DataCreate dataCreate){
        byteHashBuffer.put(name,dataCreate);
        threadPool.execute(run);
    }

    public interface DataCreate{
//        long setData(Object o);
        public byte[] getData();
        public boolean isReadOver();
    }

    public static class DataDefault implements DataCreate{

        public long t=System.currentTimeMillis();
        byte[] bytes;
        Integer hash=0;
        Integer byteHash;
        public boolean readOver=false;
        int pos=0;

        public void setData(byte[] o) {
            bytes= o;
            byteHash=getByteHash();
            combineDateAndData(t,byteHash);
//            return 0;
        }

        @Override
        public byte[] getData() {
            byte[] bytes1;
            if (bytes.length>value){
                 bytes1=Utils.subByte(bytes,pos,value);
                pos=pos+bytes1.length;
                if (pos==bytes.length-1){
                    readOver=true;
                }
            }else {
                bytes1=bytes;
                readOver=true;
            }
            return bytes1;
        }

        @Override
        public boolean isReadOver() {
            return readOver;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DataDefault that = (DataDefault) o;
            if (that.byteHash==null){that.getByteHash();}
            if (this.byteHash==null){this.getByteHash();}

            if (that.t == t && Objects.equals(byteHash, that.byteHash)) {
                return true;
            }
            return false;
        }

        public Integer getByteHash() {
            byteHash=Objects.hash(bytes);
            return byteHash;
        }


        @Override
        public int hashCode() {
            if (hash==null){
                hash= Objects.hashCode(bytes);
            }
            return hash;
        }
    }

    public static class DataFile implements DataCreate{
        public Long t=System.currentTimeMillis();
        Integer hash=0;
        Integer byteHash;
        File file;
        boolean readOver;
        long pos=0;
        public int value=1024;
        public void setFile(File file){
            this.file=file;
            byteHash=file.hashCode();
        }

        @Override
        public byte[] getData() {
            try {
                byte[] bytes;
                if (file.length()>value){
                    bytes=new byte[value];
                    FileInputStream input=new FileInputStream(file);
                    input.skip(pos);
                    int len=input.read(bytes);
                    input.close();
                    pos=pos+len;
                    if (pos+1==file.length()){
                        readOver=true;
                    }
                    return bytes;
                }else {
                    bytes=Files.readAllBytes(file.toPath());
                    readOver=true;
                    return bytes;
                }
            } catch (IOException e) {
                e.printStackTrace();
                readOver=true;
                return null;
            }
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DataDefault that = (DataDefault) o;
            if (that.byteHash==null){that.getByteHash();}
            if (this.byteHash==null){this.getByteHash();}

            if (that.t == t && Objects.equals(byteHash, that.byteHash)) {
                return true;
            }
            return false;
        }

        public Integer getByteHash() {
            byteHash=Objects.hash(file);
            return byteHash;
        }
        @Override
        public boolean isReadOver() {
            return readOver;
        }

        @Override
        public int hashCode() {
            if (hash==null){
                hash= Objects.hashCode(file);
            }
            return hash;
        }
    }
    public static class DataInputStream implements DataCreate{

        public Long t=System.currentTimeMillis();
        Integer hash=0;
        Integer byteHash;
        InputStream file;
        boolean readOver;


        public void setFile(InputStream file){
            this.file=file;
            byteHash=file.hashCode();
        }

        @Override
        public byte[] getData() {
            byte[] bytes=new byte[value];
            try {
                int len=file.read(bytes);
                if (bytes.length>len){
                    bytes= Utils.subByte(bytes,0,len);
                }
            }catch (IOException io){
                io.printStackTrace();
                readOver=true;
            }
            return bytes;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DataDefault that = (DataDefault) o;
            if (that.byteHash==null){that.getByteHash();}
            if (this.byteHash==null){this.getByteHash();}

            if (that.t == t && Objects.equals(byteHash, that.byteHash)) {
                return true;
            }
            return false;
        }

        public Integer getByteHash() {
            byteHash=Objects.hash(file);
            return byteHash;
        }
        @Override
        public boolean isReadOver() {
            return readOver;
        }

        @Override
        public int hashCode() {
            if (hash==null){
                hash= Objects.hashCode(file);
            }
            return hash;
        }
    }


    public static long combineDateAndData(long date, int data) {
        // 对数据进行质数乘法，打乱其位模式
        long dataLong = (long) data * 65537L;

        // 将日期左移32位，为数据腾出空间
        long dateShifted = date << 32;

        // 将处理后的数据与原日期的高位进行异或操作（或其他组合方式）
        // 这里选择异或是因为它能在一定程度上打乱位模式，减少碰撞
        // 你也可以尝试其他组合方式，比如加法、乘法等
        long hash = dateShifted ^ dataLong;

        // 返回最终的hash值
        return hash;
    }

}
