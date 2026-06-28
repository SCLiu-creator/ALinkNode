package superlink.udpbind.servlet;

import superlink.udpbind.client.UDPclient;

public class UpServer implements Runnable{

    @Override
    public void run(){
        while (true){
            if (UDPclient.bindUser.size()>=30){//向服务器发送，标记为次级服务器

            }
            if (UDPclient.bindUser.size()>=100){//向服务器发送，标记为次级服务器

            }
            if (UDPclient.bindUser.size()>=300){//向服务器发送，标记为服务器

            }
        }


    }

}
