package com.example.myapplication2.client.wifi;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;

import androidx.core.app.ActivityCompat;

public class WifiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private Activity activity; // 假设你有一个名为YourActivity的Activity来处理这些事件

    // 构造函数，需要传入WifiP2pManager和WifiP2pManager.Channel的实例
    public WifiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, Activity activity) {
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Wi-Fi P2P状态已改变
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wi-Fi P2P已启用
            } else {
                // Wi-Fi P2P已禁用
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // 可用设备列表已改变
            // 你可以请求新的设备列表
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(activity, Manifest.permission.NEARBY_WIFI_DEVICES) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            manager.requestPeers(channel, new WifiP2pManager.PeerListListener() {
                @Override
                public void onPeersAvailable(WifiP2pDeviceList peers) {
                    // 在这里处理新的设备列表
                    // 例如，更新UI以显示可用的设备
//                    activity.updatePeers(peers);
                }
            });
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Wi-Fi P2P连接状态已改变
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if (networkInfo.isConnected()) {
                // 已连接到一个设备
                // 你可以获取连接信息并启动数据传输
            } else {
                // 已断开与设备的连接
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // 本地设备的详细信息已改变
            WifiP2pDevice device = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
            // 处理本地设备信息的更新
//        } else if (WifiP2pManager.WIFI_P2P_GROUP_FORMED_ACTION.equals(action)) {
            // 本地设备创建了一个新的组
            // 你可以获取组信息并启动数据传输
//        } else if (WifiP2pManager.WIFI_P2P_GROUP_REMOVED_ACTION.equals(action)) {
            // 本地设备离开了一个组
            // 处理组离开的逻辑
        }

        // 这里可以添加其他广播事件的处理逻辑
    }

    // 可能还需要一些辅助方法来处理连接、数据传输等任务
}