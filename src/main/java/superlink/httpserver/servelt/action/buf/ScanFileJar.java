package superlink.httpserver.servelt.action.buf;


import superlink.filemanage.classprocess.AutoScan;
import superlink.filemanage.classprocess.Jarloader;
import superlink.filemanage.classprocess.OutJarload;
import superlink.filemanage.classprocess.property.ReInfuse;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@ReInfuse(name = "scan")
public class ScanFileJar implements scanFile{
    @Override
    public void scan(File file) throws MalformedURLException {
        List<Class<?>> list=new ArrayList<>();
        String name=file.getAbsolutePath();
        name="jar:file:/"+name+"!/";
        URL url=new URL(name);
        AutoScan.classLoader=AutoScan.class.getClassLoader();
        URL[] url1=new URL[1];
        url1[0]=url;
        Jarloader urlClassLoader=new Jarloader(url1,AutoScan.classLoader);
        urlClassLoader.addURL(url);
        AutoScan.classLoader=urlClassLoader;

        AutoScan.scanJar(file,list);
        Map<String, AutoScan.ReAssemabling> assemablingMap= OutJarload.autoScanReAssemsble(list);
        OutJarload.reIntoWebMap(assemablingMap,list);
    }



    public static void main1(String zipFile,String destDir) {
         zipFile= "path/to/your/file.zip";
         destDir= "path/to/destination/directory/";

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                String filePath = destDir + zipEntry.getName();
                if (!zipEntry.isDirectory()) {
                    // 如果是文件，则解压
                    extractFile(zis, filePath);
                } else {
                    // 如果是目录，则创建目录
                    new File(filePath).mkdirs();
                }
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void extractFile(ZipInputStream zis, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[4096];
        int read = 0;
        while ((read = zis.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }
    public static void mains(String sourceFile,String zipFile) {
         sourceFile= "path/to/your/file.txt";
         zipFile= "path/to/your/file.zip";

        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            ZipEntry zipEntry = new ZipEntry(sourceFile.substring(sourceFile.lastIndexOf("/") + 1));
            zos.putNextEntry(zipEntry);

            byte[] bytes = new byte[1024];
            int length;
            try (FileInputStream fis = new FileInputStream(sourceFile)) {
                while ((length = fis.read(bytes)) >= 0) {
                    zos.write(bytes, 0, length);
                }
            }
            zos.closeEntry();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
