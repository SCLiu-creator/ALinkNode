package superlink.udpbind.remote.remoteImp;

import superlink.udpbind.client.UDPclient;
import superlink.udpbind.usedata.DataRequest;

public class DataRemote implements Remote {
    public String[] inetAddress = new String[0];
    public String data = null;
    public DataRemote(String user,int i){
        if (i>15){
            i=15;
        }
        inetAddress=new String[i];
        inetAddress[0]= UDPclient.userlocal.address.toString()+":"+UDPclient.userlocal.port;
        data=user;
    }
}
