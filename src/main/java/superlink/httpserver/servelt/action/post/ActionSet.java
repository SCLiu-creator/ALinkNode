package superlink.httpserver.servelt.action.post;

import org.dom4j.io.XMLWriter;
import superlink.filemanage.classprocess.property.ReInfuse;
import superlink.filemanage.xmltool.UserGet;
import superlink.filemanage.xmltool.XmlParser;
import superlink.httpserver.servelt.WebController;
import superlink.httpserver.servelt.action.Api;
import superlink.httpserver.servelt.action.GetParm;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.recives.DataLenMange;

import java.io.FileOutputStream;
import java.io.IOException;

@WebController(name = "setPage")
@ReInfuse()
public class ActionSet {

    @Api(def = "getPageLen")
    public Integer getFpLen(@GetParm Integer sort) throws IOException {
        return DataLenMange.getLen(sort);
    }

    @Api(def = "setPageLen")
    public Integer setFpLen(@GetParm Integer length) throws IOException {
        DataLenMange.setLen(length,0);
        return DataLenMange.getLen(0);
    }

    @Api(def = "setPageLen1")
    public Integer setFpLen1(@GetParm Integer length) throws IOException {
        DataLenMange.setLen(length,1);
        return DataLenMange.getLen(1);
    }
    @Api(def = "setPageLen2")
    public Integer setFpLen2(@GetParm Integer length) throws IOException {
        DataLenMange.setLen(length,2);
        return DataLenMange.getLen(2);
    }
    @Api(def = "setPageLen3")
    public Integer setFpLen3(@GetParm Integer length) throws IOException {
        DataLenMange.setLen(length,3);
        return DataLenMange.getLen(3);
    }
}
