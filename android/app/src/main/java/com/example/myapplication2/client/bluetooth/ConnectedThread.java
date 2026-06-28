package com.example.myapplication2.client.bluetooth;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ConnectedThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;

    @SuppressLint("RestrictedApi")
    public ConnectedThread(BluetoothSocket socket) {
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "无法获取输入输出流", e);
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    @SuppressLint("RestrictedApi")
    public void run() {
        byte[] buffer = new byte[1024];  // 缓冲区大小
        int bytes; // 读取的字节数

        while (true) {
            try {
                bytes = mmInStream.read(buffer);
                String receivedData = new String(buffer, 0, bytes);
                // 处理接收到的数据
                Log.d(TAG, "Received: " + receivedData);
            } catch (IOException e) {
                Log.e(TAG, "读取错误", e);
                break;
            }
        }
    }

    @SuppressLint("RestrictedApi")
    public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) {
            Log.e(TAG, "写入错误", e);
        }
    }

    @SuppressLint("RestrictedApi")
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "无法关闭套接字", e);
        }
    }
}