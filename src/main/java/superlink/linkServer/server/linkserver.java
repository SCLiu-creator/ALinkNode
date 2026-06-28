package superlink.linkServer.server;

import superlink.init.InitClass;
import superlink.linkServer.Links;
import superlink.linkServer.Mod;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.remote.invoking.LinkCallTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Links(name = "Linkserver")
public class linkserver {

    @Mod(def = "getFile")
    public byte[] rci(String path) throws IOException {
        File file=new File("");
        byte[] bytes= Files.readAllBytes(file.toPath());
        return bytes;
    }

    @Mod(def = "getUI")
    public byte[] rci() throws IOException {
        File file=new File(InitClass.webpath);
        file=new File(file.getParent());
        file=new File(file.getParent()+"\\webui.zip");
        byte[] bytes= Files.readAllBytes(file.toPath());
        return bytes;
    }

}
