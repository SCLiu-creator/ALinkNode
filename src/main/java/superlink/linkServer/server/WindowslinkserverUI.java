package superlink.linkServer.server;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import superlink.filemanage.classprocess.property.ReInfuse;
import superlink.init.InitClass;
import superlink.linkServer.Links;
import superlink.linkServer.Mod;

@Links(name = "Linkserver")
@ReInfuse(name = "getUI")
public class WindowslinkserverUI {
    {
        String s= new File("").getAbsolutePath();
        System.out.println(s);
        File file=new File(InitClass.webpath);
        file=new File(file.getParent());
        file=new File(file.getParent());
        s = file.getAbsolutePath();
        System.out.println(s);
    }

    @Mod(def = "getUiByte")
    public byte[] rci() throws IOException {
        File file=new File(InitClass.webpath);
        file=new File(file.getParent());
        file=new File(file.getParent()+"webui.zip");
        if(file.exists()){
            byte[] bytes= Files.readAllBytes(file.toPath());
            return bytes;
        }else {
            return null;
        }
    }

}
