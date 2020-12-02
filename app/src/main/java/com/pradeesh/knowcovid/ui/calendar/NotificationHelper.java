package com.pradeesh.knowcovid.ui.calendar;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import com.pradeesh.knowcovid.R;

public class NotificationHelper extends ContextWrapper
{
    public static final String NOTIFICATION_CHANNEL_PRIMARY = "notification_channel_primary";
    public static final int NOTIFICATION_ID_PRIMARY = 1100;

    private NotificationManager manager;


    public NotificationHelper(Context ctx)
    {
        super(ctx);

        // For API 26+ create notification channels
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_PRIMARY,
                    "Event Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setLightColor(Color.BLUE);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            channel.setDescription("Using for Events Notifications");
            getManager().createNotificationChannel(channel);
        }
    }
    /**
     * Cancel a previously shown notification.  If it's transient, the view
     * will be hidden.  If it's persistent, it will be removed from the status
     * bar.
     *
     * @param id    The ID of the notification
     */
    public void remove(int id){
        manager.cancel(id);
    }

    /**
     * Get a notification of type 1
     * <p>
     * Provide the builder rather than the notification it's self as useful for making notification
     * changes.
     *
     * @return the builder as it keeps a reference to the notification (since API 24)
     */
//    public Notification getNotification()
//    {
//        return getNotification(getTitle(), getBody() ).build();
//    }

    /**
     * Get a notification of type 1
     * <p>
     * Provide the builder rather than the notification it's self as useful for making notification
     * changes.
     *
     * @param title the title of the notification
     * @param body  the body text for the notification
     * @return the builder as it keeps a reference to the notification (since API 24)
     */
    public Notification.Builder getNotification(String title, String body)
    {
        Log.d("uri" , "Inside Get Notification method : "+title);
        Notification.Builder builder = new Notification.Builder(getApplicationContext())
                .setOngoing(true)  // Persistent notification!
                .setAutoCancel(true)
                .setTicker(title)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(getSmallIcon());

        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(NOTIFICATION_CHANNEL_PRIMARY); // Channel ID
        }

        return builder;
    }

    /**
     * Send a notification.
     *
     * @param id           The ID of the notification
     * @param notification The notification object
     */
    public void notify(int id, Notification.Builder notification)
    {
        getManager().notify(id, notification.build());
    }

    /**
     * Get the notification manager.
     * <p>
     * Utility method as this helper works with it a lot.
     *
     * @return The system service NotificationManager
     */
    private NotificationManager getManager()
    {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }

        return manager;
    }

    /**
     * Get the small icon for this app
     *
     * @return The small icon resource id
     */
    private int getSmallIcon()
    {
        return R.drawable.notifications_icon;
    }

    /**
     * Get the notification title for this app
     *
     * @return The notification title as string
     */
    private String getTitle()
    {
        return "getString(R.string.notification_title)";
    }

    /**
     * Get the notification content for this app
     *
     * @return The notification content as string
     */
    private String getBody()
    {
        return "getString(R.string.notification_content)";
    }
}