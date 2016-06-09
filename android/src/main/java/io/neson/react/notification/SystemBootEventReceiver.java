package io.neson.react.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.ArrayList;

import io.neson.react.notification.NotificationManager;
import io.neson.react.notification.Notification;

import android.util.Log;

/**
 * Set alarms for scheduled notification after system reboot.
 */
public class SystemBootEventReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("ReactSystemNotification", "SystemBootEventReceiver: Setting system alarms");

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            NotificationManager notificationManager = new NotificationManager(context);
            SharedPreferences sharedPreferences = context.getSharedPreferences(NotificationManager.PREFERENCES_KEY, Context.MODE_PRIVATE);

            ArrayList<Integer> ids = notificationManager.getIDs();

            for (Integer id: ids) {
                try {
                    Notification notification = notificationManager.find(id);

                    if (notification.getAttributes() != null) {
                        notification.cancelAlarm();
                        notification.setAlarmAndSaveOrShow();
                        Log.i("ReactSystemNotification", "SystemBootEventReceiver: Alarm set for: " + notification.getAttributes().id);
                    }
                } catch (Exception e) {
                    Log.e("ReactSystemNotification", "SystemBootEventReceiver: onReceive Error: " + e.getMessage());
                }
            }
        }
    }
}
