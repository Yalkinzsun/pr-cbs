package com.example.pr_cbs.Reminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.pr_cbs.R;

import java.util.Date;

public class ReminderBroadcast extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        String title = intent.getStringExtra("test");
        String channelId = intent.getStringExtra("id");

        if (channelId != null) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.ic_start_logo)
                    .setContentTitle(title)
                    .setContentText("Text")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);


            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

            int id = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

            notificationManager.notify(id, builder.build());
        }

    }
}
