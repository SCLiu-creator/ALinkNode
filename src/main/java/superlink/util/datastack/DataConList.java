package superlink.util.datastack;

import com.alibaba.fastjson2.JSON;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;

public class DataConList extends DataCon{
    public DataConList(File file) throws IOException {
        super(file);
    }

    public <T> T getObject(Class ca) throws IOException {
        int i;
        randomAccessFile.seek(pr);
        try {
            i=randomAccessFile.readInt();
            pr=pr+4;
        }catch (Exception e){
            randomAccessFile.seek(pr);
            throw e;
        }
        byte[] bytes=new byte[i];
        randomAccessFile.read(bytes);
        pr=pr+i+4;
        T t= JSON.parseObject(bytes, (Type) ca);
        return t;
    }
    public int writeObject(Object o) throws IOException {
        byte[] bytes=JSON.toJSONBytes(o);
        int len=bytes.length;
        long l=file.length()+len+8;
        long sk=pw;
        randomAccessFile.seek(0);
        randomAccessFile.writeLong(l);

        randomAccessFile.seek(sk);
        randomAccessFile.write(len);
        randomAccessFile.write(bytes);
        randomAccessFile.write(len);
        fileLong=l;
        pw=sk+bytes.length+8;
        return len+4;
    }

    public <T> T queReadObject(Class ca) throws Exception {
        randomAccessFile.seek(file.length()-4);
        int i=randomAccessFile.readInt();
        byte[] bytes=new byte[i];
        randomAccessFile.seek(file.length()-4-i);
        randomAccessFile.read(bytes);
        randomAccessFile.setLength(file.length()-8-i);
        T t= (T) JSON.parseObject(bytes,ca);
        return t;
    }
}
