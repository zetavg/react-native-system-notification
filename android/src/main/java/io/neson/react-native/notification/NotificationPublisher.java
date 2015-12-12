package io.neson.react.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import io.neson.react.notification.Notification;

import android.util.Log;

public class NotificationPublisher extends BroadcastReceiver {
    public static String NOTIFICATION_ID = "notificationId";
    public static String NOTIFICATION = "notification";

    @Override
    public void onReceive(Context context, Intent intent) {
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        Notification notification = new Notification(context, id, null);

        if (notification.getAttributes() != null) {
            notification.show();

            if (notification.getAttributes().delayed ||
                !notification.getAttributes().scheduled) {
                notification.deleteFromPreferences();
            }
        }
    }
}
