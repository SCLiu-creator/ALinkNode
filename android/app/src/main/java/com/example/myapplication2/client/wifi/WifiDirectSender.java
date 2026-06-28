package com.example.myapplication2.client.wifi;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Looper;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class WifiDirectSender {
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private WifiP2pManager.ActionListener listener;
    private WifiP2pDevice device;
    private WifiP2pGroup group;
    private WifiP2pInfo info;

    Context context;

    public WifiDirectSender(Context context) {
        manager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(context, Looper.getMainLooper(), null);
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        context.registerReceiver(new WifiDirectBroadcastReceiver(manager,channel, (Activity) context),intentFilter);

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
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.NEARBY_WIFI_DEVICES)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        manager.discoverPeers(channel, listener);
    }

    public void connect(WifiP2pDevice device) {
        this.device = device;
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.NEARBY_WIFI_DEVICES)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        manager.connect(channel, config, listener);
    }

    public void createGroup() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.NEARBY_WIFI_DEVICES)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
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
