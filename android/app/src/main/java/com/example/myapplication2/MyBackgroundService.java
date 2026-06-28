package com.example.myapplication2;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.PowerManager;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.myapplication2.client.ServiceCon;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import superlink.udpbind.client.UDPclient;

public class MyBackgroundService extends Service {

    public static void MyBackgroundService(Runnable runnable) {
        MyBackgroundService.runnable = runnable;
    }

    public static Runnable runnable;
    private static final String CHANNEL_ID = "channel_id";
    private static final String CHANNEL_NAME = "channel_name";
    private static final String CHANNEL_DESCRIPTION = "channel_description";

    public static Notification notification = null;

    public static BlockingQueue<ServiceCon> conQeque = new ArrayBlockingQueue<>(4);

    public static PendingIntent pendingIntent;


    private HandlerThread handlerThread;
    private Handler backgroundHandler;
    private PowerManager.WakeLock wakeLock;

    private ServiceCon servieCon = null;

    private int runtimes=0;
    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化 HandlerThread（单例）
        handlerThread = new HandlerThread("MyBackgroundServiceThread");
        handlerThread.start();
        backgroundHandler = new Handler(handlerThread.getLooper());


        try {
            servieCon = conQeque.poll(500, TimeUnit.MICROSECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // 获取 WakeLock
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "MyApp:PollingWakeLock"
        );
        wakeLock.acquire();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(getApplicationContext());
        }

        if (servieCon == null) {
            return START_NOT_STICKY;
        }
        int serviceId = servieCon.serviceId;

        String channelId = servieCon.channelId;// 唯一标识符
        Runnable runnable = servieCon.runnable;
//        String channelId = "my_channel_id";
        CharSequence channelName = "My Channel Name"; // 渠道名称
        String channelDescription = "This is where important notifications will show up."; // 渠道描述
        int importance = NotificationManager.IMPORTANCE_HIGH; // 重要性等级


        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        channel.setDescription(channelDescription);

        // 获取NotificationManager实例
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

// 注册通知渠道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(channel);
        }
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        notification = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setSmallIcon(R.mipmap.mycion)
                .setContentTitle("后台服务通知")
                .setContentText("后台服务正在运行"+"  第"+runtimes+"次")
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis())
                .setOngoing(true)
                .build();
        runtimes=runtimes+1;
//        startForeground(serviceId, notification);
        startForeground(1, notification, android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC);
//        if (!AndriodInitClass.start0){
//        HandlerThread handlerThread = new HandlerThread(this.getClass().getName());
//        handlerThread.start();
//
//        // 获取Handler来在后台线程中执行工作
//        Handler handler = new Handler(handlerThread.getLooper());
//        handler.post(runnable);

        // 提交任务到后台线程
//        backgroundHandler.post( runnable);
        backgroundHandler.post(() -> {
            Thread thread=Thread.currentThread();
            if(UDPclient.mainDataQueue.reciverques!=null) {
                if (UDPclient.mainDataQueue.reciverques.thread!=null&&
                        UDPclient.mainDataQueue.reciverques.thread!=thread){
                    stopSelf();
                    return;
                }
            }
            runnable.run(); // 执行死循环任务
            // 任务完成后，可以停止服务（如果是一次性任务）
            // stopSelf();
        });
        // 这里可以放置你的后台任务代码
        //START_NOT_STICKY  不重启
        // 或者 START_REDELIVER_INTENT  重启
        return START_REDELIVER_INTENT;
    }

    public static NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);

    private void createNotificationChannel(Context context) {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        channel = MyBackgroundService.channel;
        channel.setDescription(CHANNEL_DESCRIPTION);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.createNotificationChannel(channel);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 释放资源
        if (handlerThread != null) {
            handlerThread.quitSafely();
        }
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // 这里不需要提供 IBinder 对象，因为这是一个后台服务
        return null;
    }


//    在 Activity 或 Service 中，你可以使用以下代码来启动后台服务：
//    Intent intent = new Intent(this, MyBackgroundService.class);
//    startService(intent);
}
