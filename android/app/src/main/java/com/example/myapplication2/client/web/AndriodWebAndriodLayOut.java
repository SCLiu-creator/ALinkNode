package com.example.myapplication2.client.web;

import superlink.filemanage.classprocess.property.ReInfuse;
import superlink.httpserver.servelt.WebController;
import superlink.httpserver.servelt.action.Action;
import superlink.httpserver.servelt.action.Api;
import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.recives.MainDataQueue;
import superlink.udpbind.client.recives.recor.BindFactory;


@WebController(name = "over")
@ReInfuse(name = "Index",grade = "b")
public class AndriodWebAndriodLayOut implements Action {

    @Api(def = "Logout")
    public void getLogCat(){
        MainDataQueue.mainReciverques.setMode(false);
        MainDataQueue.mainReciverques.run=false;
        BindFactory.mode=false;
        try {
            UDPclient.over();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
