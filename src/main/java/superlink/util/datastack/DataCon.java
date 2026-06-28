package superlink.util.datastack;

import superlink.util.Utils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class DataCon {

    RandomAccessFile randomAccessFile;
    File file;
    long fileLong;
    long pr=8;
    long pw=8;

    public DataCon(File file) throws IOException {
        this.file=file;
        byte[] bytes=new byte[8];
        int l = 0;
        try {
            randomAccessFile=new RandomAccessFile(file,"rw");
            l=randomAccessFile.read(bytes);
            fileLong=Utils.byteArrayToLong(bytes);
        }catch (IOException io){
            if (l<8){
                randomAccessFile.write(Utils.longToByteArray(0));
            }
            fileLong=0;
            io.getMessage();
        }catch (NullPointerException nu){
            throw nu;
        }
    }




    public String finalCon() throws Throwable {
        randomAccessFile.close();
        super.finalize();
        return file.getAbsolutePath();
    }
}
