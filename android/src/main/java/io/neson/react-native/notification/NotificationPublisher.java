package io.neson.react.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.lang.System;
import java.util.Calendar;

import io.neson.react.notification.Notification;

import android.util.Log;

public class NotificationPublisher extends BroadcastReceiver {
    public static String NOTIFICATION_ID = "notificationId";
    public static String NOTIFICATION = "notification";

    @Override
    public void onReceive(Context context, Intent intent) {
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        long currentTime = System.currentTimeMillis();
        Log.i("ReactSystemNotification", "Prepare To Publish: " + id + ", Now Time: " + currentTime);
        Notification notification = new Notification(context, id, null);

        if (notification.getAttributes() != null) {
            if (notification.getAttributes().endAt != null &&
                notification.getAttributes().endAt < currentTime) {
                notification.cancelAlarm();
                notification.deleteFromPreferences();

            } else if (notification.getAttributes().repeatType.equals("week")) {
                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_WEEK);
                day = day - 1;
                if (notification.getAttributes().sendAtWeekDay == day) notification.show();

            } else if (notification.getAttributes().repeatType.equals("month")) {
                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                if (notification.getAttributes().sendAtDay == day) notification.show();

            } else if (notification.getAttributes().repeatType.equals("year")) {
                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                if (notification.getAttributes().sendAtDay == day && notification.getAttributes().sendAtMonth == month) notification.show();

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
