package superlink.httpserver.servelt.action.service;

import superlink.filemanage.classprocess.property.reInject;
import superlink.httpserver.servelt.action.url.ActionTest;
import superlink.filemanage.classprocess.property.ReInfuse;
import superlink.httpserver.servelt.action.url.ActionTest1;

@ReInfuse(name = "rj",grade = "b")
public class ServiceTest {
    String string="abcd";


    @ReInfuse(name = "rj",grade = "c")
    public ServiceTest getServiceTest(ActionTest1 actionTest) {
        System.out.println("runing   getServiceTest()");
        return this;
    }
    @ReInfuse(name = "rj",grade = "d")
    public ServiceTest getServiceTest1(@reInject(name = "ActionTest") Object actionTest) {
        System.out.println("runing   actionTest()");
        return this;
    }


    public String a(){
        System.out.println(string);
        return string;
    }
}
