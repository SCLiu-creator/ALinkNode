package superlink.httpserver.servelt.action.url;

import superlink.filemanage.classprocess.property.ReInfuse;

@ReInfuse(name = "rj")
public class ServiceTest {
    String string="bnm";


    public String a(){
        System.out.println(string);
        return string;
    }
}
