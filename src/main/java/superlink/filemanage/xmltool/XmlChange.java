package superlink.filemanage.xmltool;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import superlink.util.Utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class XmlChange {
    private Document userpage;
    private Document cloude;
    private Document user;
    public static int USERPAGE=0;
    public static int CLOUDE=1;
    public static int USERS=2;
    private static final XmlChange t=new XmlChange();
    public static XmlChange getInstance(){
        return t;
    }
    public XmlChange read(int type){
        SAXReader reader=new SAXReader();
        switch (type){
            case 0:{
                try {
                    this.userpage= reader.read(new File(XmlCreate.userShow +".xml"));
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
                break;
            }
            case 1:{
                try {
                    this.userpage= reader.read(new File(XmlCreate.userCloudefile +".xml"));
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
                break;
            }
            case 2:{
                try {
                    this.userpage= reader.read(new File(XmlParser.dir +"user.xml"));
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        return this;
    }
//    public Document addCloudeXml(String user,Document document){
//
//    }
    public static byte[] getGZip(byte[] bytes) throws IOException {
        ByteArrayInputStream bis = null;
        GZIPInputStream gzip = null;
        try {
            bis= new ByteArrayInputStream(bytes);
            gzip=new GZIPInputStream(bis);
            int len;
            byte[] buf = new byte[1024];
            byte[] bufz=new byte[0];
            do {
                len=gzip.read(buf);
                bufz=Utils.byteMerger(bufz,buf);
            }while (len!=-1);
            return bufz;
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            bis.close();
            gzip.close();
        }
        return null;
    }

    public static byte[] unGZip(byte[] data) {
        byte[] b = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            GZIPInputStream gzip = new GZIPInputStream(bis);
            byte[] buf = new byte[1024];
            int num = -1;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((num = gzip.read(buf, 0, buf.length)) != -1) {
                baos.write(buf, 0, num);
            }
            b = baos.toByteArray();
            baos.flush();
            baos.close();
            gzip.close();
            bis.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return b;
    }

        public static byte[] zip(byte[] data) {
        byte[] b = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ZipOutputStream zip = new ZipOutputStream(bos);
            ZipEntry entry = new ZipEntry("zip");
            entry.setSize(data.length);
            zip.putNextEntry(entry);
            zip.write(data);
            zip.closeEntry();
            zip.close();
            b = bos.toByteArray();
            bos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return b;
    }
    public static byte[] zipByte(byte[] bytes) throws IOException {
        byte[] b = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ZipInputStream zip = new ZipInputStream(bis);
            while (zip.getNextEntry() != null) {
                byte[] buf = new byte[1024];
                int num = -1;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                while ((num = zip.read(buf, 0, buf.length)) != -1) {
                    baos.write(buf, 0, num);
                }
                b = baos.toByteArray();
                baos.flush();
                baos.close();
            }
            zip.close();
            bis.close();
        } catch (Exception ex) {
            ex.printStackTrace();

        }
        return b;
    }
}
