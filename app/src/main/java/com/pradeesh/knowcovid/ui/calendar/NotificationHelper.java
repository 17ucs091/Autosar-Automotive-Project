package com.pradeesh.knowcovid.ui.calendar;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.pradeesh.knowcovid.R;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationHelper
{
    public static final String NOTIFICATION_CHANNEL_PRIMARY = "notification_channel_primary";
    public static final int NOTIFICATION_ID_PRIMARY = 1100;

    public NotificationHelper(Context context)
    {
//        super(ctx);
//        String ChannelID="1234";
        Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ context.getPackageName() + "/" + R.raw.mysoundone);
        // For API 26+ create notification channels
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_PRIMARY,
                    "Event Notification",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setLightColor(Color.BLUE);
            channel.setImportance(NotificationManager.IMPORTANCE_HIGH);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            channel.setDescription("Using for Events Notifications");

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();
            channel.setSound(soundUri, audioAttributes);


            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public Notification.Builder getNotification(Context context,String title, String body)
    {
        Notification.Builder builder = new Notification.Builder(context)
                .setOngoing(true)  // Persistent notification!
                .setAutoCancel(true)
                .setContentTitle(title)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setContentText(body)
                .setSmallIcon(getSmallIcon());
        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(NOTIFICATION_CHANNEL_PRIMARY); // Channel ID
        }
        return builder;
    }

    private int getSmallIcon()
    {
        return R.drawable.notifications_icon;
    }

}