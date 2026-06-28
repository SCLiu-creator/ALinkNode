package superlink.test.andriod;

import android.net.wifi.p2p.*;

public class WifiDirectSender {
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private WifiP2pManager.ActionListener listener;
    private WifiP2pDevice device;
    private WifiP2pGroup group;
    private WifiP2pInfo info;

    public WifiDirectSender(Context context) {
        manager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(context, Looper.getMainLooper(), null);
        listener = new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                // 处理操作成功的情况
            }

            @Override
            public void onFailure(int reason) {
                // 处理操作失败的情况
            }
        };
    }

    public void discoverPeers() {
        manager.discoverPeers(channel, listener);
    }

    public void connect(WifiP2pDevice device) {
        this.device = device;
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        manager.connect(channel, config, listener);
    }

    public void createGroup() {
        manager.createGroup(channel, listener);
    }

    public void removeGroup() {
        manager.removeGroup(channel, listener);
    }

    public void send(String message) {
        if (info != null && info.groupOwnerAddress != null) {
            try {
                Socket socket = new Socket(info.groupOwnerAddress, 12345); // 连接组所有者的 IP 地址和端口号
                OutputStream out = socket.getOutputStream();
                out.write(message.getBytes());
                out.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        this.info = info;
    }

    public void disconnect() {
        manager.removeGroup(channel, listener);
    }

    public void onPeersAvailable(WifiP2pDeviceList peers) {
        // 处理可用的设备列表
    }

    public void onConnectionStateChanged(int state) {
        // 处理连接状态变化事件
    }
}
