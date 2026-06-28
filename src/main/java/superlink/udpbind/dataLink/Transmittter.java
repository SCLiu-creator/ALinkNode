package superlink.udpbind.dataLink;

import superlink.udpbind.usedata.UserRequest;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;

import static superlink.util.Utils.byteMerger;

public class Transmittter {

    public static volatile Socket Tcpsocket;
    public static volatile DatagramSocket Udpsocket;
    public static volatile UserRequest UserRequest;

    public void SetTcp(Socket socket){
        Tcpsocket=socket;
    }
    public void SetUdp(DatagramSocket socket){
        Udpsocket=socket;
    }
    public void SetUser(UserRequest userRequest){
        UserRequest=userRequest;
    }

    public boolean Udpsend(String filename) throws IOException, InterruptedException {
        File file=new File(filename);
        FileInputStream fileInputStream =new FileInputStream(file);
        BufferedInputStream bufferedInputStream=new BufferedInputStream(fileInputStream);
        byte[] bytes=new byte[65497];
        int l=10000000;
        int ll=10000000;

        long length=file.length();
        l=l+(int)(length/65497);
        if(length%65497>0){
            l=l+1;
        }
        byte[] b=new byte[10];
        b=("SD"+l).getBytes();
        DatagramPacket sd=new DatagramPacket(b,10,UserRequest.toaddress,UserRequest.toport);
        Udpsocket.send(sd);
        Thread.sleep(20);
        while (bufferedInputStream.read(bytes) != -1){
            ll=ll+1;
            DatagramPacket datagramPacket=new DatagramPacket(setdataed("DD",ll,bytes),65507,UserRequest.toaddress,UserRequest.toport);
            Udpsocket.send(datagramPacket);
            Thread.sleep(1);
        }

        return true;
    }


    public byte[] setdataed(String string,int page,byte[] after){
        String datehead=string+page;
        return byteMerger(datehead.getBytes(),after);
    }

}
