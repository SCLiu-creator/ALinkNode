package superlink.udpbind.client.recives.datalen;

import superlink.udpbind.client.UserContext;
import superlink.udpbind.usedata.BufferRequest;
import superlink.udpbind.usedata.DataRequest;
import superlink.util.SHAutils;
import superlink.util.mapThreadPool.MapThreadPool;
import superlink.util.setThreadPool.SetThreadPoolExecutor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public abstract class DataLength implements Runnable, Comparable{
    public static SetThreadPoolExecutor setThreadPool=new SetThreadPoolExecutor(1,10,300,50);
    public static MapThreadPool mapThreadPool=new MapThreadPool(1,10,300,50);
    public long createTime=System.currentTimeMillis();

    public Object data=null;
    public UserContext userContext;
    public DataRequest sdr;
    public volatile BufferRequest bdr;
    public short id;
    public int pagelen=1450;

    @Override
    public int compareTo(Object o) {
        if (this.hashCode()==o.hashCode()){
            return 0;
        }else {
            return (int) (this.createTime-((DataLength)o).createTime);
        }
    }
    @Override
    public String toString(){
        // 手动拼接 JSON 字符串
        StringBuilder jsonBuilder = new StringBuilder(200);

        jsonBuilder.append("{");

        jsonBuilder.append("\"username\":\"").append(userContext.userName).append("\",");

        jsonBuilder.append("\"id\":").append(id).append(",");
        if(sdr!=null){
            if(sdr.filename!=null){
                jsonBuilder.append("\"filneme\":").append(sdr.filename).append(",");
            }
            if(sdr.pl!=0){
                jsonBuilder.append("\"pl\":").append(sdr.pl);
            }
        }

        jsonBuilder.append("}");
        return jsonBuilder.toString();
    }

    public void setPagelen(int pagelen) {
        this.pagelen = pagelen;
    }
    public static boolean writdata(String absoult ,Object data){
        File file=new File(absoult);
        //file..mkdirs();
        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                return false;
            }
        }
        if (data instanceof ByteBuffer){
            try (FileOutputStream fileOutputStream=new FileOutputStream(file)){
                fileOutputStream.write(((ByteBuffer) data).array());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        if (data instanceof byte[]){
            try (FileOutputStream fileOutputStream=new FileOutputStream(file)){
                fileOutputStream.write((byte[])data);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        if (data instanceof File){
            FileOutputStream fileOutputStream;
            FileInputStream fileFrom;
            try {
                fileOutputStream=new FileOutputStream(file);
                fileFrom=new FileInputStream((File)data);
                fileFrom.getChannel().transferTo(0,fileFrom.getChannel().size(),fileOutputStream.getChannel());
                fileOutputStream.close();
                fileFrom.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                ((File) data).delete();
                //  (File)((File) data).deleteOnExit();
            }
        }
        return false;
    }

    public static long getLength(Object data){
        if (data instanceof ByteBuffer){
            return ((ByteBuffer) data).array().length;
        }
        if (data instanceof File){
            return ((File) data).length();
        }
        return 0;
    }
    public abstract int getprogress();

//    public  int f();
    public abstract int clear();


    public static String getHash(Object data){
        if (data instanceof ByteBuffer){
            ByteBuffer byteBuffer=(ByteBuffer)data;
            return SHAutils.getShaFromByte(byteBuffer.array(),SHAutils.MD_5,false);
        }
        if (data instanceof File){
            try {
                File file=(File) data;
                return SHAutils.getShaFromFile(file.getAbsolutePath(),SHAutils.MD_5,false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return SHAutils.getShaFromFile(data.toString(),SHAutils.MD_5,false);
    }


}
