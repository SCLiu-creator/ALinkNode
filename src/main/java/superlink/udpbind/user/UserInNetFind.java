package superlink.udpbind.user;

import com.alibaba.fastjson2.JSON;
import superlink.udpbind.client.UDPclient;
import superlink.util.Utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class UserInNetFind {

    public void scanIp(){
        String ip=UDPclient.userlocal.address.getHostAddress();
        String[] strings=ip.split("\\.");
        String ips;
        String data= "TF"+UDPclient.userlocal.toString();
        switch (strings[0]){
            case "10":{
                ips=strings[0]+".";
                InetAddress inetAddress=null;
                byte[] bytes=new byte[6];
                bytes= Utils.byteMerger(bytes,data.getBytes());
                DatagramPacket packet=new DatagramPacket(bytes,bytes.length);
                for (int z = 1000; z <1255 ; z++) {
                    for (int i = 1000; i < 1255; i++) {
                        for (int j = 1000; j <1255 ; j++) {
                            try {
                                inetAddress=InetAddress.getByName(ips+new Integer(z).toString().substring(1)+"."
                                        +new Integer(i).toString().substring(1)+"."
                                        +new Integer(j).toString().substring(1));
                                packet.setAddress(inetAddress);
                                allSend(packet);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                break;
            }
            case "172":{
                ips=strings[0]+"."+strings[1]+".";
                InetAddress inetAddress=null;
                byte[] bytes=new byte[6];
                bytes= Utils.byteMerger(bytes,data.getBytes());
                DatagramPacket packet=new DatagramPacket(bytes,bytes.length);
                for (int i = 1000; i < 1255; i++) {
                    for (int j = 1000; j <1255 ; j++) {
                        try {
                            inetAddress=InetAddress.getByName(ips+new Integer(i).toString().substring(1)+"."+new Integer(j).toString().substring(1));
                            packet.setAddress(inetAddress);
                            allSend(packet);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            }
            case "192":{
                ips=strings[0]+"."+strings[1]+"."+strings[2]+".";
                Integer i=0;
                InetAddress inetAddress=null;
                byte[] bytes=new byte[6];
                bytes= Utils.byteMerger(bytes,data.getBytes());
                DatagramPacket packet=new DatagramPacket(bytes,bytes.length);
                for (int j = 0; j <255 ; j++) {
                    try {
                        inetAddress=InetAddress.getByName(ips+new Integer(j).toString());
                        packet.setAddress(inetAddress);
                        BroadcastSend(packet);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            default:{
                return;
            }
        }
    }

    public static void allSend(DatagramPacket packet){
        for (int i = 1; i < 256*256; i++) {
            packet.setPort(i);
            try {
                UDPclient.socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void BroadcastSend(DatagramPacket packet){
        for (int p:Utils.RandomPort.ports){
            packet.setPort(p);
            try {
                UDPclient.socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
