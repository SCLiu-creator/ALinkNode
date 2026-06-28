package superlink.httpserver.servelt.action.buf;

import superlink.filemanage.classprocess.property.ReInfuse;
import superlink.init.InitClass;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.remote.invoking.LinkCallTemplate;

import java.io.File;

@ReInfuse(name = "uz")
public class saveZipS implements SaveZip {
    @Override
    public void op(String username)  {
        LinkCallTemplate linkCallTemplate=new LinkCallTemplate(UDPclient.userlocal.username,username);
        linkCallTemplate.req("Linkserver.getUI");
        File file=new File(InitClass.webpath);
        file=new File(file.getParent());
        file=new File(file.getParent());
    }
}
