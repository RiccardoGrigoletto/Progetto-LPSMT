package com.example.marco.progettolpsmt;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.support.v4.app.NotificationCompat;

/**
 * Helper class for showing and canceling pause timer
 * notifications.
 * <p>
 * This class makes heavy use of the {@link NotificationCompat.Builder} helper
 * class to create notifications in a backward-compatible way.
 */
public class TimerNotification {
    /**
     * The unique identifier for this type of notification.
     */
    private static final String NOTIFICATION_TAG = "Study tracker timer";
    static NotificationCompat.Builder builder;
    private static String channelTAG = "not_channel_01";

    public TimerNotification(Object systemService) {
        // The user-visible name of the channel.
        CharSequence name = "Study tracker";
        // Th  e user-visible description of the channel.
        String description = "Timer";
        NotificationChannel mChannel;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_LOW;
            mChannel = new NotificationChannel(channelTAG, name, importance);
            // Configure the notification channel.
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            // Sets the notification light color for notifications posted to this
            // channel, if the device supports this feature.
            mChannel.setLightColor(R.color.colorPrimary);
            mChannel.enableVibration(false);
            NotificationManager mNotificationManager = (NotificationManager) systemService;
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(mChannel);
            }
        }
    }

    /**
     * Shows the notification, or updates a previously shown notification of
     * this type, with the given parameters.
     * <p>
     * the notification.
     * <p>
     * presentation of timer notifications. Make
     * sure to follow the
     * <a href="https://developer.android.com/design/patterns/notifications.html">
     * Notification design guidelines</a> when doing so.
     *
     * @see #cancel(Context)
     */
    public static void notify(final Context context,
                              final String description, final int number) {
        final Resources res = context.getResources();

        final String title = res.getString(
                R.string.pause_timer_notification_title_template, description);


        builder = new NotificationCompat.Builder(context, channelTAG)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(title)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setTicker(description)
                .setNumber(number)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(
                        PendingIntent.getActivity(
                                context,
                                0,
                                new Intent(context, TimerActivity.class).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT),
                                PendingIntent.FLAG_UPDATE_CURRENT))
                .setAutoCancel(false);

        notify(context, builder.build());
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private static void notify(final Context context, final Notification notification) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.notify(NOTIFICATION_TAG, 0, notification);
        } else {
            nm.notify(NOTIFICATION_TAG.hashCode(), notification);
        }
    }

    /**
     * Cancels any notifications of this type previously shown using
     * {@link #notify(Context, String, int)}.
     */
    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public static void cancel(final Context context) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.cancel(NOTIFICATION_TAG, 0);
        } else {
            nm.cancel(NOTIFICATION_TAG.hashCode());
        }
    }
    public static void modifyText(String text) {
        NotificationListenerService nls = new NotificationListenerService() {
            @Override
            protected void attachBaseContext(Context base) {
                super.attachBaseContext(base);
            }
        };
    }


    public NotificationCompat.Builder getBuilder() {
        return builder;
    }
}
