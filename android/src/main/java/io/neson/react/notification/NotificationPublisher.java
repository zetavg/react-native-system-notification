package io.neson.react.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.lang.System;
import java.util.Calendar;

import io.neson.react.notification.Notification;
import io.neson.react.notification.NotificationManager;

import android.util.Log;

/**
 * Publisher for scheduled notifications.
 */
public class NotificationPublisher extends BroadcastReceiver {
    final static String NOTIFICATION_ID = "notificationId";
    final static String NOTIFICATION = "notification";

    @Override
    public void onReceive(Context context, Intent intent) {
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        long currentTime = System.currentTimeMillis();
        Log.i("ReactSystemNotification", "NotificationPublisher: Prepare To Publish: " + id + ", Now Time: " + currentTime);

        NotificationManager notificationManager = new NotificationManager(context);
        Notification notification = notificationManager.find(id);

        if (notification.getAttributes() != null) {

            // Delete notifications that are out-dated
            if (notification.getAttributes().endAt != null &&
                notification.getAttributes().endAt < currentTime) {
                notification.cancelAlarm();
                notification.deleteFromPreferences();

            // Show and delete one-time notifications
            } else if (notification.getAttributes().repeatType == null) {
                notification.show();
                notification.cancelAlarm();
                notification.deleteFromPreferences();

            // Special conditions for weekly based notifications
            } else if (notification.getAttributes().repeatType.equals("week")) {
                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_WEEK);
                day = day - 1;
                if (notification.getAttributes().sendAtWeekDay == day) notification.show();

            // Special conditions for monthly based notifications
            } else if (notification.getAttributes().repeatType.equals("month")) {
                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                if (notification.getAttributes().sendAtDay == day) notification.show();

            // Special conditions for yearly based notifications
            } else if (notification.getAttributes().repeatType.equals("year")) {
                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                if (notification.getAttributes().sendAtDay == day && notification.getAttributes().sendAtMonth == month) notification.show();

            // Other repeating notifications - just show them
            } else {
                notification.show();
            }

            if (notification.getAttributes().delayed ||
                !notification.getAttributes().scheduled) {
                notification.deleteFromPreferences();
            }

        } else {
            notification.cancelAlarm();
            notification.deleteFromPreferences();
        }
    }
}
