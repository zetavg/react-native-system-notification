package io.neson.react.notification;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.content.BroadcastReceiver;

import android.util.Log;

/**
 * Handles user's interaction on notifications.
 *
 * Sends broadcast to the application, launches the app if needed.
 */
public class NotificationEventReceiver extends BroadcastReceiver {
    final static String INTENT_ID = "io.neson.react.notification.NotificationEvent";
    final static String NOTIFICATION_ID = "id";
    final static String ACTION = "action";
    final static String PAYLOAD = "payload";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        Log.i("ReactSystemNotification",
                "NotificationEventReceiver: Received: " + extras.getString(ACTION) +
                ", Notification ID: " + extras.getInt(NOTIFICATION_ID) +
                ", payload: " + extras.getString(PAYLOAD));
        sendBroadcast(context, extras);
    }

    private void sendBroadcast(final Context context, final Bundle extras) {
        Intent intent = new Intent(INTENT_ID);

        intent.putExtra("id", extras.getInt(NOTIFICATION_ID));
        intent.putExtra("action", extras.getString(ACTION));
        intent.putExtra("payload", extras.getString(PAYLOAD));

        context.sendOrderedBroadcast(intent, null, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int result = getResultCode();

                if (result != Activity.RESULT_OK) {
                    launchApplication(context, extras);
                }
            }
        }, null, Activity.RESULT_CANCELED, null, null);

        Log.v("ReactSystemNotification",
                "NotificationEventReceiver: Broadcast Sent: NotificationEvent: " +
                        extras.getString(ACTION) +
                        ", Notification ID: " + extras.getInt(NOTIFICATION_ID) +
                        ", payload: " + extras.getString(PAYLOAD));
    }

    private void launchApplication(Context context, Bundle extras) {
        String packageName = context.getApplicationContext().getPackageName();
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);

        launchIntent.putExtra("initialSysNotificationId", extras.getInt(NOTIFICATION_ID));
        launchIntent.putExtra("initialSysNotificationAction", extras.getString(ACTION));
        launchIntent.putExtra("initialSysNotificationPayload", extras.getString(PAYLOAD));
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        context.startActivity(launchIntent);
        Log.i("ReactSystemNotification", "NotificationEventReceiver: Launching: " + packageName);
    }
}
