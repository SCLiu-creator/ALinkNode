package superlink.util.datastack;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import superlink.filemanage.xmltool.XmlCreate;
import superlink.filemanage.xmltool.XmlParser;
import superlink.udpbind.usedata.User;
import superlink.util.Utils;

import javax.xml.bind.Element;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static superlink.util.Utils.*;

public class DataLinkRW {
    //head:32b
    //pos:long
    //datalen:int
    long pos;
    long timeStart;
    long timeEnd;

    long systemTime = System.currentTimeMillis();

    RandomAccessFile randomAccessFile;
    public HashMap<String,DataByte> hashMap=new HashMap<>();
    public int datastar;

    public static LinkedList<DataByte> emptyList=new LinkedList();

    // XmlParser.chat+"list"
    //pos在time后
    public DataLinkRW(File file) throws Exception {
        String path= file.getAbsolutePath();
        this.systemTime  = System.currentTimeMillis();
        if (new File(path).exists()){
            this.randomAccessFile=new RandomAccessFile(path,"rw");
            randomAccessFile.seek(0);
            this.pos = randomAccessFile.readLong();
            randomAccessFile.seek(24);
            this.timeStart = randomAccessFile.readLong();
            randomAccessFile.seek(pos-8);
            this.timeEnd = randomAccessFile.readLong();
        }else {
            file.createNewFile();
            this.randomAccessFile=new RandomAccessFile(path,"rw");
            this.timeStart = System.currentTimeMillis();
            this.timeEnd = System.currentTimeMillis();
            byte[] bytes = Utils.byteMerger(
                    Utils.longToByteArray(24),
                    new byte[16],Utils.longToByteArray(timeStart),
                    Utils.longToByteArray(timeEnd));
            randomAccessFile.seek(0);
            randomAccessFile.write( bytes);
            this.pos = 8+16+8;

        }
    }
    public byte[] select(String target,findFun findFun) throws Exception {
        boolean sy=true;
        long pos = 32;
        while (sy){
            randomAccessFile.seek(pos+8);
            int len = randomAccessFile.readInt();
            byte[] bytes=new byte[len];
            randomAccessFile.read(bytes);
            byte[] data = findFun.find( bytes,target);
            if (data!=null){
                return data;
            }else {
                pos = pos+len+8+4+4;
                if (pos+8>=randomAccessFile.length()){
                    sy=false;
                }
            }
        }
        return null;
    }

    public synchronized byte[] read(int poss) throws IOException {
        try {
            if (poss>=0){
                long pos = 32;
                byte[] bytes=null;
                int len=0;
                for (int i=0;i<=poss;i++){
                    if (pos+8>=randomAccessFile.length()){
                        return null;
                    }
                    randomAccessFile.seek(pos+8);
                    len = randomAccessFile.readInt();
                    pos = pos+len+8+4+4;
                }
                bytes=new byte[len];
                int len1=randomAccessFile.read(bytes);
                return bytes;
            }else {
                long pos = 0;
                byte[] bytes = null;
                int len=0;
                for (int i=0;i>poss;poss++){
                    if (pos>=this.pos-32-8){
                        return null;
                    }
                    randomAccessFile.seek(this.pos-pos-8-4);
                    len = randomAccessFile.readInt();
                    pos = pos+(len+8+4+4);

                }
                bytes=new byte[len];
                randomAccessFile.seek(this.pos-pos+4);
                randomAccessFile.read(bytes);
                return bytes;
            }
        }catch (IOException  e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    public synchronized List<byte[]> read(long poss, int length) throws IOException {
        ArrayList<byte[]> arrayList = new ArrayList<byte[]>();
        if (poss>=0){
            long pos = 32;
            byte[] bytes=null;
            int len=0;
            for (int i=0;i<=poss;i++){
                if (pos+8>=randomAccessFile.length()){
                    return arrayList;
                }
                randomAccessFile.seek(pos+8);
                len = randomAccessFile.readInt();
                pos = pos+8+4+len+4;
            }
            try {
                for (int i=0;i<length;i++){
                    randomAccessFile.seek(pos+8);
                    len = randomAccessFile.readInt();
                    bytes=new byte[len];
                    int len1=randomAccessFile.read(bytes);
                    arrayList.add(bytes);
                    pos = pos+len+8+4+4;
                    if (pos>=randomAccessFile.length()){
                        return arrayList;
                    }
                }
            }catch (IOException e){
                System.out.println(e.getMessage());
            }catch (Exception e){
                e.printStackTrace();
            }
        }else {
            long pos = 0;
            byte[] bytes = null;
            int len=0;
            for (int i=0;i>poss;poss++){
                if (pos>=this.pos-32-8){
                    pos = this.pos-32-8;
                    break;
                }
                randomAccessFile.seek(this.pos-pos-8-4);
                len = randomAccessFile.readInt();
                pos = pos+(len+8+4+4);
            }
            try {
                for (int i=0;i<length;i++){
                     if (pos<=0){
                        return arrayList;
                    }
                    randomAccessFile.seek(this.pos-pos);
                    len = randomAccessFile.readInt();
                    bytes=new byte[len];
                    randomAccessFile.seek(this.pos-pos+4);
                    int len1=randomAccessFile.read(bytes);
                    arrayList.add(bytes);
                    pos = pos-(len+8+4+4);
//                    if (pos>=this.pos-32-8){
//                        return arrayList;
//                    }
                }
            }catch (IOException e){
                System.out.println(e.getMessage());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return arrayList;
    }

    public synchronized void write(byte[] data) throws IOException {
        DataByte db=new DataByte(data);
        randomAccessFile.seek(randomAccessFile.length());
        randomAccessFile.write(db.toBytes());
        pos = randomAccessFile.length();
        randomAccessFile.seek(0);
        randomAccessFile.write(longToByteArray(pos));
    }
    public synchronized void del(byte[] user,findFun findFun) throws IOException {
        boolean sy=true;
        long pos = 32;
        byte[] data=null;
        while (sy){
            randomAccessFile.seek(pos+8);
            int len = randomAccessFile.readInt();
            if (len==user.length || true){
                byte[] bytes=new byte[len];
                randomAccessFile.read(bytes);
                bytes = findFun.find(bytes,new String(user));
                if(bytes!=null){
                    pos=pos+8;
                    randomAccessFile.seek(pos);
                    int bufferSize = 40960;
                    // 计算删除区域之后的数据长度
                    long remainingLength = randomAccessFile.length() - (pos+8+4+len+4);
                    randomAccessFile.seek(0);
                    byte[] buffer = new byte[bufferSize];
                    int bytesRead;
                    long totalBytesRead = 0;

                    while (totalBytesRead < remainingLength) {
                        // 计算本次读取的字节数
                        int bytesToRead = (int) Math.min(bufferSize, remainingLength - totalBytesRead);
                        randomAccessFile.seek(pos+4+len+4+8 + totalBytesRead);
                        bytesRead = randomAccessFile.read(buffer, 0, bytesToRead);

                        if (bytesRead == -1) {
                            break; // 文件结束
                        }
                        randomAccessFile.seek(totalBytesRead+pos);
                        randomAccessFile.write(buffer, 0, bytesRead);

                        totalBytesRead += bytesRead;
                    }
                    randomAccessFile.setLength(pos + remainingLength);
                    randomAccessFile.seek(0);
                    randomAccessFile.write(longToByteArray(pos+remainingLength));
                    this.pos=randomAccessFile.length();
                    return;
                }
            }

            if (data==null){
                pos = pos+len+8+8;
                if (pos+8>=randomAccessFile.length()){
                    sy=false;
                }
            }
        }
    }
    //20

    public class DataByte {
        byte[] len;
        long ctime;
        String string;
        byte[] data;

        private DataByte(byte[] bytes) {
            this.string=new String(bytes, StandardCharsets.UTF_8);
            data=bytes;
            int len = bytes.length;
            this.len=intToByteArray(len);
            ctime = System.currentTimeMillis()- systemTime +timeEnd;
        }

        public byte[] toBytes() {
            byte[] bytes = Utils.byteMerger(len,data,len,longToByteArray(ctime));
            return bytes;
        }

    }

    @FunctionalInterface
    public static interface findFun {
        public byte[] find(byte[] bytes,String target);
    }


    public static void main(String[] args) throws Exception {
        File file = new File(XmlParser.cachepath+"data");
        DataLinkRW dataLinkRW = new DataLinkRW(file);
        String s = "{\"nickName\":\"我的电脑\",\"username\":\"81ZO0d7Bjj9StquD\",\"inaddress\":\"192.168.135.147\"," +
                "\"inport\":\"50016\",\"udpstate\":\"0\"}";
        s = "{\"nickName\":\"我的电脑\",\"username\":\"81ZO0d7Bjj9StquD\",\"inaddress\":\"192.168.135.147\",\"inport\":50016}";
        s = "{\"nickName\":\"我的电脑\",\"username\":\"81ZO0d7Bjj9StquD\",\"inaddress\":\"192.168.135.147\"}";
//        dataLinkRW.write(s.getBytes());
        byte[] bytes = dataLinkRW.read(1);
        Object user = JSON.parse(bytes);
        bytes = dataLinkRW.read(-1);
        user = JSON.parse(bytes);
        bytes = dataLinkRW.read(-2);
        user = JSON.parse(bytes);
        bytes = dataLinkRW.read(4);
        user = JSON.parse(bytes);
        List<byte[]> arrayList = dataLinkRW.read( 3,2);
        for (byte[] bytes1:arrayList){
            user = JSON.parse(bytes1);
        }
        arrayList = dataLinkRW.read( -2,2);
        for (byte[] bytes1:arrayList){
            user = JSON.parse(bytes1);
        }
        bytes = dataLinkRW.select("b",(a,b)->{
            JSONObject jsonObject = (JSONObject) JSON.parse(a);
            Integer integer=null;
            try {
                integer = (Integer) jsonObject.get("inport");
                if(integer.equals(50016)){
                    return a;
                }else {
                    return null;
                }
            } catch (Exception e){

            }

            return null;
        });
        user = JSON.parse(bytes);
        bytes = dataLinkRW.select("b",(a,b)->{
            JSONObject jsonObject = (JSONObject) JSON.parse(a);
            Integer integer=null;
            try {
                integer = (Integer) jsonObject.get("inport");
                if(integer==null||integer.equals(0)){
                    return a;
                }else {
                    return null;
                }
            } catch (Exception e){

            }
            return null;

        });
        user = JSON.parse(bytes);
        long len=file.length();
        dataLinkRW.del("b".getBytes(),(a,b)->{
            JSONObject jsonObject = (JSONObject) JSON.parse(a);
            Integer integer=null;
            String integer1 = null;
            try {
                integer = (Integer) jsonObject.get("inport");
                integer1 = (String) jsonObject.get("udpstate");
                if(integer!=null && integer1==null&&integer.equals(50016)){
                    return a;
                }else {
                    return null;
                }
            } catch (Exception e){

            }
            return null;
        });
        len=file.length();
    }
}
