package com.example.myapplication2.client.web;

import com.example.myapplication2.client.AndriodInitClass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import superlink.httpserver.servelt.action.Action;
import superlink.httpserver.servelt.action.Api;
import superlink.httpserver.servelt.WebController;

@WebController(name ="Andriod" )
public class AndriodWeb implements Action {

    @Api(def = "getLogCat")
    public String getLogCat(){
        StringBuilder log=new StringBuilder();

        try {
            ArrayList commandLine = new ArrayList();
            commandLine.add( "logcat");
//            使用该参数可以让logcat获取日志完毕后终止进程
            commandLine.add( "-d");
//            commandLine.add( "-v");
//            commandLine.add( "time");
//            commandLine.add( "-f");
            //如果使用commandLine.add(">");是不会写入文件，必须使用-f的方式
            commandLine.add(AndriodInitClass.initClass.absolute+"logcat.txt");
            Process process = Runtime.getRuntime().exec((String[]) commandLine.toArray( new String[commandLine.size()]));
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(process.getInputStream()), 1024);
            String line = bufferedReader.readLine();
            while ( line != null) {
                log.append(line);
                log.append("\n");
                line = bufferedReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return log.toString();
    }

}
