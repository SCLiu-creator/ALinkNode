package superlink.linkServer.server;

import superlink.linkServer.Links;
import superlink.linkServer.Mod;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.remote.invoking.LinkCallTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;

@Links(name = "testLinkserver")
public class testserver {

    @Mod(def = "test")
    public String test(String a){
        return a+null+a+null;
    }

    @Mod(def = "testrc")
    public String rc(String a){
        LinkCallTemplate linkCallTemplate=new LinkCallTemplate(UDPclient.userlocal.username,a);
        linkCallTemplate.req("testLinkserver.test");
        return a+a;
    }

    @Mod(def = "testrci")
    public byte[] rci(String a) throws IOException {
        File file=new File(a);
        byte[] bytes= Files.readAllBytes(file.toPath());
        return bytes;
    }

}
