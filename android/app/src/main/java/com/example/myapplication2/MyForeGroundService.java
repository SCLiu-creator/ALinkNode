package com.example.myapplication2;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.PowerManager;

public class MyForeGroundService extends Service {

    public static Runnable runnable=null;
    private static final String NOTIFICATION_CHANNEL_ID = "my_service_channel";
    private static final String NOTIFICATION_ID = "my_service";
    private static final String NOTIFICATION_TITLE = "My Foreground Service";
    private static final String NOTIFICATION_CONTENT = "Service is running...";

    private HandlerThread handlerThread;
    private Handler handler;
    private PowerManager.WakeLock wakeLock;
    @Override
    public void onCreate() {
        super.onCreate();
        // 获取 WakeLock
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "MyApp:PollingWakeLock"
        );
        wakeLock.acquire();

        // 创建一个新的线程来处理服务中的工作
        handlerThread = new HandlerThread(MyForeGroundService.class.getSimpleName());
        handlerThread.start();

        // 获取Handler来在后台线程中执行工作
        handler = new Handler(handlerThread.getLooper());
        handler.post(runnable);

        // 显示前台通知
        startForeground(NOTIFICATION_ID.hashCode(), createNotification());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 启动你的Runnable任务
        handler.post(MyForeGroundService.runnable); // 假设我们传递值42给a

        // 返回START_STICKY以在服务被杀死后重新创建
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // 停止HandlerThread
        if (handlerThread != null && handlerThread.isAlive()) {
            handlerThread.quitSafely();
        }
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
        // 移除前台通知
        stopForeground(true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // 不支持绑定
        return null;
    }

    private Notification createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "My Service Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
//                manager.createNotificationChannel(MyBackgroundService.channel);
            }
        }

        return new Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher) // 使用你的图标资源
                .setContentTitle(NOTIFICATION_TITLE)
                .setContentText(NOTIFICATION_CONTENT)
                .setOngoing(true)
                .build();
//        return MyBackgroundService.notification;
    }



}