package superlink.init;

import org.dom4j.Element;
import superlink.filemanage.xmltool.UserGet;
import superlink.filemanage.xmltool.XmlCreate;
import superlink.testjava.testBase64;
import superlink.udpbind.controller.UdpBindSend;
import superlink.udpbind.usedata.User;
import superlink.udpbind.usedata.UserRequest;
import superlink.util.Tool;


import java.util.ArrayList;
import java.util.List;

import static superlink.udpbind.controller.Controller.findUser;

public class UserLinkCon {

    public static List<UserLinkCon> linkConList= new ArrayList<>();

    public String user;

    public long time1;

    public long time2;

    public int times=0;

    public static void init(){
        Element link = UserGet.user.element("link");
        if (link==null){
            return;
        }
        List<Element> elements = link.elements("u");
        for (Element ele:elements){
            String u=ele.attribute("user").getValue();
            UserLinkCon userLinkCon=new UserLinkCon(u);
            linkConList.add(userLinkCon);
        }
    }

    public static void add(String user){
        UserLinkCon userLinkCon=new UserLinkCon(user);
        Element link;
        link = UserGet.user.element("link");
        if (link==null){
            link = UserGet.user.addElement("link");
        }
        Element ele = link.addElement("u");
        ele.addAttribute("user",user);
        UserGet.save();
        linkConList.add(userLinkCon);
    }
    public static void remove(String user){
        Element link;
        link = UserGet.user.element("link");
        if (link==null){
            link = UserGet.user.addElement("link");
        }
        List<Element> links= link.elements("u");
        for (Element ele:links){
            String u=ele.attribute("user").getValue();
            if(u.equals(user)){
                link.remove(ele);
                UserGet.save();
            }
        }
        UserLinkCon userLinkCon = null;
        for (UserLinkCon userlc:UserLinkCon.linkConList){
            if(userlc.user.equals(user)){
                userLinkCon=userlc;
            }
        }
        if(userLinkCon!=null){
            linkConList.remove(userLinkCon);
        }
    }

    private UserLinkCon(String user){
        this.user=user;
        time1=System.currentTimeMillis();
        time2=time1;
    }

    public int st=0;
    public void link(User user){
        if(user==null){
            findUser(this.user);
            return;
        }
        if(st==0){
            if(times==0){
                UserRequest userRequest= Tool.UsertoUserRequestbind(user);
                UdpBindSend.udpBindSend(userRequest);
                time1=System.currentTimeMillis();
                time2=System.currentTimeMillis();
            }
            if(times>3){
                st=1;
            }
        }
        if (st==1){
            if(times%4==0){
                UdpBindSend.UdpBindSendinlocal(user);
            }
            if(times>10){
                st=2;
            }
        }
        if (st==2){
            st=3;
            if(times%4==0 && times>10){
                UdpBindSend.udpReturnBind(user);
            }
        }

        times++;
        if(times>100){
            times=0;
        }
    }
    public void inital(){
        times=0;
        st=0;
        time2=time1=0;
    }
}
