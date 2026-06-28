package superlink.udpbind.handle;

import superlink.udpbind.client.UDPclient;
import superlink.udpbind.dataLink.UdpData;
import superlink.udpbind.dataqueue.ReciveQueueFactory;
import superlink.util.Tool;
import com.alibaba.fastjson2.JSON;
import superlink.udpbind.usedata.UserRequest;
import superlink.udpbind.usedata.baseMassage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.concurrent.ConcurrentHashMap;

public class Handler {
    public static ConcurrentHashMap<String, Object> DispectMap;
    public static ConcurrentHashMap<String, UdpData> UdpMap;
    public static ConcurrentHashMap<String, java.lang.Object> liveMap;
    public static ConcurrentHashMap<String, java.lang.Object> TcpMap;

    public static DatagramPacket dataPacket;

    public Handler(){
        DispectMap=new ConcurrentHashMap<>();
        UdpMap=new ConcurrentHashMap<>();
        liveMap=new ConcurrentHashMap<>();
        TcpMap=new ConcurrentHashMap<>();
    }

    public static UserRequest chooseHandler(UserRequest ur){
        UserRequest rs= Tool.toUserRequest(ur);
        switch (ur.choose){
            case 1: {
                File file = new File("filePath");
                long fileSLength = file.length();

                if (!file.exists()) {
//                    Log.i("message", "文件不存在");
//                    sendMessage(SendFileEntity.STATE_FAILED, 0, "文件不存在");

                }

                FileInputStream in = null;
                try {
                    in = new FileInputStream("filePath");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                byte[] buf = new byte[1024];
                int len;
                int sum = 0;
                try {
                    len = in.read(buf);

                while (len != -1) {

                    sum += len;
//                                Log.e("message", "文件传输的大小==" + sum);
                    int progress = (int) (sum * 100 / fileSLength);
//                    dataPacket = new DatagramPacket(buf, len);
//                    UDPclient.socket.send(dataPacket);

                    Thread.sleep(10);  //延时一段时间，防止传输太快。丢包
                }

            }catch (Exception e) {

                    //Log.i("message", "发送文件异常：" + e.toString());
                }


            return ur;
            } case 2:{

            } default:{break;}



        }
        return ur;

    }
    public static void send(String name, baseMassage massage,String prefix){
        String s=JSON.toJSONString(massage);
        s=prefix+s;
        DatagramPacket datagramPacket=new DatagramPacket(s.getBytes(),s.getBytes().length);
        UdpData udpData=(UdpData) UdpMap.get(name);
        try {
            udpData.dataSocket.send(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static void removeUdp(String username){
        try {
            UdpData udpData=Handler.UdpMap.get(username);
            udpData.over();
            ReciveQueueFactory.deltask(username);
            Handler.UdpMap.remove(username);
            Handler.liveMap.remove(username);
        }catch (Exception e){

        }

    }
}
