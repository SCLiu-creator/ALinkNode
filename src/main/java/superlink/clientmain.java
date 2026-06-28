package superlink;


import superlink.init.InitClass;
import superlink.util.Tool;
import superlink.util.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class clientmain {
    {
        String string="D:\\uniapp\\up1\\pages\\img\\00524-3466405767-{{masterpiece}}, {best quality}, {{light}}, highly detailed, {{a plant of petals like opal are flying}}, long black and blue hai (3).png";
        try {
            byte[] bytes=Files.readAllBytes(new File(string).toPath());
            bytes= Utils.subByte(bytes,0,bytes.length-1000);
            Files.write(new File("D:\\uniapp\\up1\\pages\\img\\0.png").toPath(),bytes);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        String s="\\\\ad:s你ad/\\c:d.c\\\\:d/?//cd\\编:稿d/";
        String s1="///ad:s你ad/\\c:d.c\\?\\:d///cd编:稿d/";
        String ss= Tool.normalize1(s);
        String sss= Tool.normalize1(s1);
        String ss2= Tool.normalize2(s);
        String sss2= Tool.normalize2(s1);
        File file=new File(s);
        String sp="D:\\uniapp\\up11\\pagess\\imgg\\0.png";
        System.out.println(file.getName());
        System.out.println(new File(file.getParent()).getName());
        System.nanoTime();
//        byte[] b=Utils.shortToByteArray((short) 221);
//        short bi=Utils.byteArrayToshort(b);
//        SimpleDateFormat simpleDateFormat=new SimpleDateFormat();
//        Long l=(Long.valueOf("1723796086")-4)*1000;
//        Long l1=Long.valueOf("1723796082000");
//        Long lm=Long.MAX_VALUE;
//        Timestamp dm=new Timestamp(lm);
//        Timestamp d1=new Timestamp(l1);
//        long d0=System.currentTimeMillis();
//        Timestamp d=new Timestamp(l);
//        String s=d.toString();
//        s=simpleDateFormat.format(d);
        new InitClass().lowInit();
    }
}
