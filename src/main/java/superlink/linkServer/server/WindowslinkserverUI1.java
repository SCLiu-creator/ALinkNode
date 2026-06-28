package superlink.linkServer.server;

import superlink.filemanage.classprocess.property.ReInfuse;
import superlink.init.InitClass;
import superlink.linkServer.Links;
import superlink.linkServer.Mod;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Links(name = "Linkserver")
@ReInfuse(name = "getUI",grade = "b")
public class WindowslinkserverUI1 {
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
