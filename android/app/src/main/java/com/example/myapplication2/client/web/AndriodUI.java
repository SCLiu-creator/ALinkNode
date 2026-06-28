package com.example.myapplication2.client.web;

import static com.example.myapplication2.MainActivity.context;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import superlink.filemanage.classprocess.property.ReInfuse;
import superlink.httpserver.servelt.WebController;
import superlink.httpserver.servelt.action.Action;
import superlink.httpserver.servelt.action.Api;
import superlink.httpserver.servelt.action.GetParm;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.remote.invoking.LinkCallTemplate;
import superlink.util.Utils;

@WebController(name ="Index" )
@ReInfuse(name = "Index")
public class AndriodUI implements Action {


    @Api(def = "getUI")
    public String getUI(@GetParm String username){
        try {
            String p1= context.getFilesDir().getAbsolutePath();
            LinkCallTemplate linkCallTemplate=new LinkCallTemplate(UDPclient.userlocal.username,username);
            byte[] bytes = linkCallTemplate.req("Linkserver.getUI");
//            File file=new File(InitClass.webpath);
            InputStream inputStream = new ByteArrayInputStream(bytes);
            Utils.unZip(p1+"/superlink/web/",inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "ok";
    }

}
