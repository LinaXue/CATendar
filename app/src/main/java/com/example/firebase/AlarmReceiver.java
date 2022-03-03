package com.example.firebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {

    // 當 alarm 被觸發時，需要一個 receiver 來接收它，然後指示要做什麼事
    // 所以要繼承一個 BroadcastReceiver 並實作它的 onReceive 方法
    // 就是在此實作 notification !
    @Override
    public void onReceive(Context context, Intent intent) {
        String event = intent.getStringExtra("event");
        String time = intent.getStringExtra("time");
        int notifyID = intent.getIntExtra("id",0);
        //要執行 notification 的地方
        Intent activityIntent = new Intent(context, HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,activityIntent,PendingIntent.FLAG_ONE_SHOT);

        String channelID = "channel_id";
        CharSequence name = "channel_name";
        String descruption = "description";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(channelID,name, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(descruption);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        //Alarm 結合 Notification : 在時間到達時發出通知來提醒使用者
        Notification notification = new NotificationCompat.Builder(context,channelID)
                .setSmallIcon(R.mipmap.icon)
                .setContentTitle("鏟屎官注意！")
                .setContentText("別忘了 "+time+" 有 "+event+" 呦！喵嗚~")
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setDeleteIntent(pendingIntent)
                .setGroup("Group_calendar_view")
                .setAutoCancel(true)
                .build();
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(notifyID,notification);
    }
}
