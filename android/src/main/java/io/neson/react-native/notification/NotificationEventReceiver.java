package io.neson.react.notification;

import android.content.ComponentName;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Intent;
import android.content.Context;
import android.content.BroadcastReceiver;

import java.util.List;

import android.util.Log;

/**
 * Handles user's interaction on notifications.
 *
 * Sends broadcast to the application, launches the app if needed.
 */
public class NotificationEventReceiver extends BroadcastReceiver {
    final static String NOTIFICATION_ID = "id";
    final static String ACTION = "action";
    final static String PAYLOAD = "payload";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();

        Log.i("ReactSystemNotification", "NotificationEventReceiver: Recived: " + extras.getString(ACTION) + ", Notification ID: " + extras.getInt(NOTIFICATION_ID) + ", payload: " + extras.getString(PAYLOAD));

        if (!applicationIsRunning(context)) {
            String packageName = context.getApplicationContext().getPackageName();
            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            context.startActivity(launchIntent);

            Log.i("ReactSystemNotification", "NotificationEventReceiver: Launching: " + packageName);

            while (!applicationIsRunning(context)) {
                SystemClock.sleep(1000);
                Log.v("ReactSystemNotification", "NotificationEventReceiver: waiting for " + packageName + "to launch");
            }
        }

        // TODO: Ensure the app is ready in a more reasonable way
        SystemClock.sleep(3000);

        Intent i = new Intent("NotificationEvent");
        i.putExtra("id", extras.getInt(NOTIFICATION_ID));
        i.putExtra("action", extras.getString(ACTION));
        i.putExtra("payload", extras.getString(PAYLOAD));
        context.sendBroadcast(i);
        Log.v("ReactSystemNotification", "NotificationEventReceiver: Broadcast Sent: NotificationEvent: " + extras.getString(ACTION) + ", Notification ID: " + extras.getInt(NOTIFICATION_ID) + ", payload: " + extras.getString(PAYLOAD));
    }

    private boolean applicationIsRunning(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<RunningAppProcessInfo> processInfos = activityManager.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
                if (processInfo.processName.equals(context.getApplicationContext().getPackageName())) {
                    if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        for (String d: processInfo.pkgList) {
                            Log.v("ReactSystemNotification", "NotificationEventReceiver: ok: " + d);
                            return true;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = activityManager.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }

        return false;
    }
}
