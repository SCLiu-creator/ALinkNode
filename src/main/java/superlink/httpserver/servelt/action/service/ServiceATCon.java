package superlink.httpserver.servelt.action.service;

import superlink.filemanage.classprocess.property.ReInfuse;
import superlink.udpbind.client.recives.datalen.DataLength;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@ReInfuse()
public class ServiceATCon {

    Map<Integer, DataLength> map=new HashMap();

    public void add(String user, String filename, DataLength dataAuto){
        map.put(Objects.hash(user,filename),dataAuto);
    }

    public DataLength get(String user, String filename){
        return map.get(Objects.hash(user,filename));
    }
    public void remove(String user,String filename){
        map.remove(Objects.hash(user,filename));
    }
}
