package superlink.httpserver.webserver;

import superlink.udpbind.client.recives.data.stream1.QSContrain;
import superlink.udpbind.controller.Controller;

import java.util.HashMap;
import java.util.Map;

public class TcpProxyFactory {

    public static Map<String,Map> tcpProxyClientMap=new HashMap();

    public static Map<String,Map> tcpProxyServerMap=new HashMap();



    public static TcpProxyClient getTcpProxyClient(String username,int port){
        Map<Integer,TcpProxyClient> map=tcpProxyClientMap.get(username);
        if (map==null){
            map=new HashMap();
            tcpProxyClientMap.put(username,map);
        }
        TcpProxyClient tcpProxyClient=map.get(port);
        if (tcpProxyClient==null){
            QSContrain qsContrain=QSContrain.getInstance(username);

            tcpProxyClient=new TcpProxyClient(port,qsContrain);
            map.put(port,tcpProxyClient);
        }
        return tcpProxyClient;
    }

    public static TcpProxyServer getTcpProxyServer(String username,int port){
        Map<Integer,TcpProxyServer> map=tcpProxyServerMap.get(username);
        if (map==null){
            map=new HashMap();
            tcpProxyServerMap.put(username,map);
        }
        TcpProxyServer tcpProxyServer=map.get(port);
        if (tcpProxyServer==null){
            QSContrain qsContrain=QSContrain.getInstance(username);

            tcpProxyServer=new TcpProxyServer(port,qsContrain);
            map.put(port,tcpProxyServer);
        }
        return tcpProxyServer;
    }
}
