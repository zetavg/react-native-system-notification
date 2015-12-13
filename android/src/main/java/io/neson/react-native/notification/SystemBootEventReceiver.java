package io.neson.react.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Set;

import io.neson.react.notification.NotificationModule;
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
            SharedPreferences sharedPreferences = context.getSharedPreferences(NotificationModule.PREFERENCES_KEY, Context.MODE_PRIVATE);
            Set<String> keys = sharedPreferences.getAll().keySet();

            for (String key : keys) {
                try {
                    Notification notification = new Notification(context, Integer.parseInt(key), null);

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
