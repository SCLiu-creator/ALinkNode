package com.example.myapplication2;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class NotificationUtil {

    private static final String CHANNEL_ID = "channel_id";
    private static final String CHANNEL_NAME = "channel_name";
    private static final String CHANNEL_DESCRIPTION = "channel_description";

    public static void showNotification(Context context, String title, String message) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(context);
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setWhen(System.currentTimeMillis())
                .build();

        notificationManager.notify(1, notification);
    }

    private static void createNotificationChannel(Context context) {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(CHANNEL_DESCRIPTION);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }

//    NotificationUtil.showNotification(this, "通知标题", "通知内容");
}
//    // 设置高频定时任务（例如每100ms）
//    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//    Intent intent = new Intent(this, PollingReceiver.class);
//    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
//
//// 使用setExactAndAllowWhileIdle()对抗省电限制（最低间隔约100ms）
//if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//        alarmManager.setExactAndAllowWhileIdle(
//        AlarmManager.RTC_WAKEUP,
//        System.currentTimeMillis() + 100, // 100ms后触发
//        pendingIntent
//        );
//        } else {
//        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 100, pendingIntent);
//        }
//
//// 在BroadcastReceiver中执行任务并重新设置Alarm
//public class PollingReceiver extends BroadcastReceiver {
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        // 执行轮询任务
//        Log.d("Polling", "Task executed via AlarmManager");
//
//        // 重新设置下一次Alarm（实现循环）
//        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(
//                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
//        );
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            alarmManager.setExactAndAllowWhileIdle(
//                    AlarmManager.RTC_WAKEUP,
//                    System.currentTimeMillis() + 100,
//                    pendingIntent
//            );
//        }
//    }
//}
