package io.neson.react.notification;

import android.os.Build;
import android.os.SystemClock;
import android.app.PendingIntent;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;

import io.neson.react.notification.NotificationEventHandlerService;
import io.neson.react.notification.NotificationPublisher;

/**
 * An object-oriented Wrapper class around the system notification class.
 *
 * Each instance is an representation of a single, or a set of scheduled
 * notifications. It handles operations like showing, canceling and clearing.
 */
public class Notification {
    private Context context;
    private int id;
    private ReadableMap attributes;

    /**
     * Constructor.
     */
    public Notification(Context context, int id, ReadableMap attributes) {
        this.context = context;
        this.id = id;
        this.attributes = attributes;
    }

    /**
     * Public context getter.
     */
    public Context getContext() {
        return context;
    }

    /**
     * Public attributes getter.
     */
    public ReadableMap getAttributes() {
        return attributes;
    }

    /**
     * Create the notification, show it now or set the schedule.
     */
    public void create() {
        if (attributes.getBoolean("delayed")) {
            setDelay();
        } else {
            show();
        }
    }

    /**
     * Clear the notification from the status bar.
     */
    public void clear() {
        getNotificationManager().cancel(id);
    }

    /**
     * Cancel the notification.
     */
    public void delete() {
        // TODO
    }

    /**
     * Build the notification.
     */
    public android.app.Notification build() {
        android.app.Notification.Builder notificationBuilder = new android.app.Notification.Builder(context);

        notificationBuilder
            .setContentTitle(attributes.getString("subject"))
            .setContentText(attributes.getString("message"))
            .setSmallIcon(context.getResources().getIdentifier(attributes.getString("smallIcon"), "mipmap", context.getPackageName()))
            .setAutoCancel(attributes.getBoolean("autoCancel"))
            .setContentIntent(getContentIntent());

        if (Build.VERSION.SDK_INT <= 15) {
            return notificationBuilder.getNotification();
        } else {
            return notificationBuilder.build();
        }
    }

    /**
     * Show the notification now.
     */
    public void show() {
        getNotificationManager().notify(id, build());
    }

    /**
     * Schedule the delayed notification.
     */
    public void setDelay() {
        PendingIntent pendingIntent = getScheduleNotificationIntent();

        long futureInMillis = SystemClock.elapsedRealtime() + attributes.getInt("delay");
        getAlarmManager().set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private AlarmManager getAlarmManager() {
        return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    private PendingIntent getContentIntent() {
        Intent intent = new Intent(context, NotificationEventHandlerService.class);

        intent.putExtra("action", attributes.getString("action"));
        intent.putExtra("payload", attributes.getString("payload"));

        // TODO: Change this to Brodcast or Activity
        return PendingIntent.getService(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent getScheduleNotificationIntent() {
        Intent notificationIntent = new Intent(context, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, id);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, build());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        return pendingIntent;
    }
}
