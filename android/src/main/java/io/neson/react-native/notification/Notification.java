package io.neson.react.notification;

import android.os.Build;
import android.os.SystemClock;
import android.app.PendingIntent;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import java.lang.System;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import io.neson.react.notification.NotificationModule;
import io.neson.react.notification.NotificationAttributes;
import io.neson.react.notification.NotificationEventReceiver;
import io.neson.react.notification.NotificationPublisher;

import android.util.Log;

/**
 * An object-oriented Wrapper class around the system notification class.
 *
 * Each instance is an representation of a single, or a set of scheduled
 * notifications. It handles operations like showing, canceling and clearing.
 */
public class Notification {
    private Context context;
    private int id;
    private NotificationAttributes attributes;

    /**
     * Constructor.
     */
    public Notification(Context context, int id, @Nullable NotificationAttributes attributes) {
        this.context = context;
        this.id = id;
        this.attributes = attributes;

        if (attributes == null) loadAttributesFromPreferences();
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
    public NotificationAttributes getAttributes() {
        return attributes;
    }

    /**
     * Create the notification, show it now or set the schedule.
     */
    public Notification create() {
        setAlarmAndSaveOrShow();

        Log.i("ReactSystemNotification", "Notification Created: " + id);

        return this;
    }

    /**
     * Update the notification, resets its schedule.
     */
    public Notification update(NotificationAttributes notificationAttributes) {
        delete();
        attributes = notificationAttributes;
        setAlarmAndSaveOrShow();

        return this;
    }

    /**
     * Clear the notification from the status bar.
     */
    public Notification clear() {
        getNotificationManager().cancel(id);

        Log.i("ReactSystemNotification", "Notification Cleared: " + id);

        return this;
    }

    /**
     * Cancel the notification.
     */
    public Notification delete() {
        getNotificationManager().cancel(id);

        if (attributes.delayed || attributes.scheduled) {
            cancelAlarm();
        }

        Log.i("ReactSystemNotification", "Notification Deleted: " + id);

        return this;
    }

    /**
     * Build the notification.
     */
    public android.app.Notification build() {
        android.app.Notification.Builder notificationBuilder = new android.app.Notification.Builder(context);

        notificationBuilder
            .setContentTitle(attributes.subject)
            .setContentText(attributes.message)
            .setSmallIcon(context.getResources().getIdentifier(attributes.smallIcon, "mipmap", context.getPackageName()))
            .setAutoCancel(attributes.autoCancel)
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

        Log.i("ReactSystemNotification", "Notification Show: " + id);
    }

    /**
     * Setup alarm or show the notification.
     */
    public void setAlarmAndSaveOrShow() {
        if (attributes.delayed) {
            setDelay();
            saveAttributesToPreferences();

        } else if (attributes.scheduled) {
            setSchedule();
            saveAttributesToPreferences();

        } else {
            show();
        }
    }

    /**
     * Schedule the delayed notification.
     */
    public void setDelay() {
        PendingIntent pendingIntent = getScheduleNotificationIntent();

        long futureInMillis = SystemClock.elapsedRealtime() + attributes.delay;
        getAlarmManager().set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);

        Log.i("ReactSystemNotification", "Notification Delay Alarm Set: " + id);
    }

    /**
     * Schedule the notification.
     */
    public void setSchedule() {
        PendingIntent pendingIntent = getScheduleNotificationIntent();

        if (attributes.repeatType.equals("time")) {
            getAlarmManager().setRepeating(AlarmManager.RTC_WAKEUP, attributes.sendAt, attributes.repeatTime, pendingIntent);
            Log.i("ReactSystemNotification", "Set " + attributes.repeatTime + "ms Alarm: " + id);

        } else if (attributes.repeatType.equals("minute")) {
            getAlarmManager().setRepeating(AlarmManager.RTC_WAKEUP, attributes.sendAt, 60000, pendingIntent);
            Log.i("ReactSystemNotification", "Set Minute Alarm: " + id);

        } else if (attributes.repeatType.equals("hour")) {
            getAlarmManager().setRepeating(AlarmManager.RTC_WAKEUP, attributes.sendAt, AlarmManager.INTERVAL_HOUR, pendingIntent);
            Log.i("ReactSystemNotification", "Set Hour Alarm: " + id);

        } else if (attributes.repeatType.equals("halfDay")) {
            getAlarmManager().setRepeating(AlarmManager.RTC_WAKEUP, attributes.sendAt, AlarmManager.INTERVAL_HALF_DAY, pendingIntent);
            Log.i("ReactSystemNotification", "Set Half-Day Alarm: " + id);

        } else if (attributes.repeatType.equals("day")) {
            getAlarmManager().setRepeating(AlarmManager.RTC_WAKEUP, attributes.sendAt, AlarmManager.INTERVAL_DAY, pendingIntent);
            Log.i("ReactSystemNotification", "Set Day Alarm: " + id);

        } else if (attributes.repeatType.equals("week")) {
            getAlarmManager().setRepeating(AlarmManager.RTC_WAKEUP, attributes.sendAt, AlarmManager.INTERVAL_DAY, pendingIntent);
            Log.i("ReactSystemNotification", "Set Week Alarm: " + id);

        } else if (attributes.repeatType.equals("month")) {
            getAlarmManager().setRepeating(AlarmManager.RTC_WAKEUP, attributes.sendAt, AlarmManager.INTERVAL_DAY, pendingIntent);
        Log.i("ReactSystemNotification", "Set Month Alarm: " + id);

        } else if (attributes.repeatType.equals("year")) {
            getAlarmManager().setRepeating(AlarmManager.RTC_WAKEUP, attributes.sendAt, AlarmManager.INTERVAL_DAY, pendingIntent);
            Log.i("ReactSystemNotification", "Set Year Alarm: " + id);

        } else {
            getAlarmManager().set(AlarmManager.RTC_WAKEUP, attributes.sendAt, pendingIntent);
            Log.i("ReactSystemNotification", "Set One-Time Alarm: " + id);
        }

        Log.i("ReactSystemNotification", "Notification Schedule Alarm Set: " + id + ", Type: " + attributes.repeatType + ", Current Time: " + System.currentTimeMillis() + ", First Send At: " + attributes.sendAt);
    }

    /**
     * Cancel the delayed notification.
     */
    public void cancelAlarm() {
        PendingIntent pendingIntent = getScheduleNotificationIntent();
        getAlarmManager().cancel(pendingIntent);

        Log.i("ReactSystemNotification", "Notification Alarm Canceled: " + id);
    }

    public void saveAttributesToPreferences() {
        SharedPreferences.Editor editor = getSharedPreferences().edit();

        String attributesJSONString = new Gson().toJson(attributes);

        editor.putString(Integer.toString(id), attributesJSONString);

        if (Build.VERSION.SDK_INT < 9) {
            editor.commit();
        } else {
            editor.apply();
        }

        Log.i("ReactSystemNotification", "Notification Saved To Pref: " + id + ": " + attributesJSONString);
    }

    public void loadAttributesFromPreferences() {
        String attributesJSONString = getSharedPreferences().getString(Integer.toString(id), null);
        this.attributes = (NotificationAttributes) new Gson().fromJson(attributesJSONString, NotificationAttributes.class);

        Log.i("ReactSystemNotification", "Notification Loaded From Pref: " + id + ": " + attributesJSONString);
    }

    public void deleteFromPreferences() {
        SharedPreferences.Editor editor = getSharedPreferences().edit();

        editor.remove(Integer.toString(id));

        if (Build.VERSION.SDK_INT < 9) {
            editor.commit();
        } else {
            editor.apply();
        }

        Log.i("ReactSystemNotification", "Notification Deleted From Pref: " + id);
    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private AlarmManager getAlarmManager() {
        return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    private SharedPreferences getSharedPreferences () {
        return (SharedPreferences) context.getSharedPreferences(NotificationModule.PREFERENCES_KEY, Context.MODE_PRIVATE);
    }

    private PendingIntent getContentIntent() {
        Intent intent = new Intent(context, NotificationEventReceiver.class);

        intent.putExtra(NotificationEventReceiver.NOTIFICATION_ID, id);
        intent.putExtra(NotificationEventReceiver.ACTION, attributes.action);
        intent.putExtra(NotificationEventReceiver.PAYLOAD, attributes.payload);

        return PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent getScheduleNotificationIntent() {
        Intent notificationIntent = new Intent(context, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, id);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        return pendingIntent;
    }
}
