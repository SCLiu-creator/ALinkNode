package superlink.udpbind.tcpproxy;

import superlink.udpbind.client.recives.data.datastream.DataStreamAB;
import superlink.udpbind.client.recives.data.datastream.DataStreamABb;
import superlink.util.Utils;

import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

public class ProxySocket extends Thread{

    public static Map<String,Map<Byte,ProxySocket>> mapMap=new HashMap<>();
    ServerSocket serverSocket;
    DataStreamAB streamAB;
    boolean aBoolean=true;

    ProxyServer proxyServer;
    ProxyClient proxyClient;
    public int port;
    public int id;
    public ProxySocket(int port){
        this.port=port;
    }
    public void setMode(boolean b){
        aBoolean=b;
    }
    public DataStreamAB createDataStream(String username, short id) throws Exception {
        this.id=id;
        streamAB=new DataStreamAB(username, id);
        streamAB.build();
        return streamAB;
    }
    public DataStreamAB getDataStream(String username, short id,boolean b){
        streamAB=DataStreamAB.dealGetDataStreamAB(username, id);
//        streamAB = new DataStreamAB(username,id,false);
        return streamAB;
    }
    public boolean bulid(int port) {
        byte[] bytes=null;
        streamAB.senders.sendSym(
                Utils.byteMerger("PD".getBytes(),
                Utils.shortToByteArray((short) id),
                Utils.intToByteArray(port)));
        if (bytes!=null){
            return true;
        }else {
            return false;
        }
    }


    @Override
    public void run() {
        if (aBoolean){//client
            proxyClient=new ProxyClient(port,streamAB);
//            new Thread(()->{proxyClient.runrecive(1);}).start();
            new Thread(()->{proxyClient.runread();}).start();
        }else {
            proxyServer=new ProxyServer(port,streamAB);
            new Thread(()->{proxyServer.runrecive();}).start();
            new Thread(()->{proxyServer.runread();}).start();
        }
    }
}
