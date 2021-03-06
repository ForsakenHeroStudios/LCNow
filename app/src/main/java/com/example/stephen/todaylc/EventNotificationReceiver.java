package com.example.stephen.todaylc;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import static com.example.stephen.todaylc.EventAdapter.CHANNEL_ID;

public class EventNotificationReceiver extends BroadcastReceiver {
    public EventNotificationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int requestCode = 01234;
        String title = intent.getStringExtra("title");
        String location = intent.getStringExtra("location");
        String description = "";
        if (!location.equals("")) {
            description = "It's located at "+location;
        }
        Intent tent = new Intent(App.getContext(), MainActivity.class);
        // TODO: take the user to this event when clicked on
        PendingIntent pendingIntent = PendingIntent.getActivity(App.getContext(),requestCode,tent,0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(App.getContext(), CHANNEL_ID)
                .setCategory(Notification.CATEGORY_EVENT)
                .setContentTitle(title+" is happening soon!")
                .setContentText(description)
                .setSmallIcon(R.drawable.lcnowicon)
                .setAutoCancel(true)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setFullScreenIntent(pendingIntent,true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID)
                    .setColorized(true);
        }
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(App.getContext());
        notificationManager.notify(requestCode,builder.build());
    }
}
