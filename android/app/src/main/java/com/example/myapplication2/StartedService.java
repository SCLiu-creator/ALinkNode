package com.example.myapplication2;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class StartedService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化操作
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 处理启动命令
        // 返回START_STICKY或START_REDELIVER_INTENT以在服务被杀后重启
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 清理操作
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
