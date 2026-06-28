package com.example.myapplication2.client.infu;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import superlink.filemanage.classprocess.AutoScan;
import superlink.filemanage.classprocess.Jarloader;
import superlink.filemanage.classprocess.OutJarload;
import superlink.httpserver.servelt.action.buf.scanFile;
import superlink.filemanage.classprocess.property.ReInfuse;

@ReInfuse(name = "scan",grade = "b")
public class openFile implements scanFile {
    @Override
    public void scan(File file) throws MalformedURLException {
        List<Class<?>> list = new ArrayList();
        String name = file.getAbsolutePath();
        name = "jar:file:/" + name + "!/";
        URL url = new URL(name);
        URL[] url1 = new URL[]{url};
        Jarloader urlClassLoader = new Jarloader(url1, AutoScan.classLoader);
        urlClassLoader.addURL(url);
        AutoScan.classLoader = urlClassLoader;
        AutoScan.scanJar(file, list);
        Map<String, AutoScan.ReAssemabling> assemablingMap = OutJarload.autoScanReAssemsble(list);
        OutJarload.reIntoWebMap(assemablingMap, list);
    }
}
