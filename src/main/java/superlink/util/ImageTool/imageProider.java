package superlink.util.ImageTool;

import net.coobird.thumbnailator.Thumbnails;
import superlink.filemanage.xmltool.XmlCreate;
import superlink.util.SHAutils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class imageProider {

    public  void generateFixedSizeImage(String filename){
        try {
            File file = new File(filename);
            BufferedImage bufferedImage = ImageIO.read(new FileInputStream(file));
            int height = bufferedImage.getHeight();
            int width = bufferedImage.getWidth();
            if (height>width){
                Thumbnails.of(filename).height(300).toFile(XmlCreate.userCloudecache+ SHAutils.getSHA1(filename,false));
            }else {
                Thumbnails.of(filename).width(300).toFile(XmlCreate.userCloudecache+SHAutils.getSHA1(filename,false));
            }
        } catch (IOException e) {
            System.out.println("原因: " + e.getMessage());
        }
    }

    public void generateFixedSizeImage(String filename,String newFileName){
        try {
            File file = new File(filename);
            BufferedImage bufferedImage = ImageIO.read(new FileInputStream(file));
            int height = bufferedImage.getHeight();
            int width = bufferedImage.getWidth();
            if (height>width){
                Thumbnails.of(filename).outputQuality(0.85f).height(180).toFile(newFileName);
            }else {
                Thumbnails.of(filename).outputQuality(0.85f).width(180).toFile(newFileName);
            }
        } catch (IOException e) {
            System.out.println("原因: " + e.getMessage());
        }
    }
}
