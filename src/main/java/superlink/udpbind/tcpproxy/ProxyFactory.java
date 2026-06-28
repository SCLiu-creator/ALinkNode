package superlink.udpbind.tcpproxy;

import superlink.udpbind.client.UDPclient;
import superlink.udpbind.client.UserContext;

public class ProxyFactory {

    public TcpProxy getProxy(String name) throws Exception {
        UserContext userContext=UDPclient.mainDataQueue.getUserContext(name);
  return new TcpProxy();
    }


}
