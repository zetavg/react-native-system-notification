package io.neson.react.notification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.res.Resources;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import java.util.List;

import com.google.android.gms.gcm.GcmListenerService;

import io.neson.react.notification.NotificationManager;
import io.neson.react.notification.Notification;
import io.neson.react.notification.NotificationAttributes;

public class GCMNotificationListenerService extends GcmListenerService {

    private static final String TAG = "GCMNotificationListenerService";

    @Override
    public void onMessageReceived(String from, Bundle bundle) {
        sendNotification(bundle);
    }

    public Class getMainActivityClass() {
        try {
            String packageName = getApplication().getPackageName();
            return Class.forName(packageName + ".MainActivity");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean applicationIsRunning() {
        ActivityManager activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> processInfos = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
            if (processInfo.processName.equals(getApplication().getPackageName())) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String d: processInfo.pkgList) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void sendNotification(Bundle bundle) {
        Resources resources = getApplication().getResources();

        String packageName = getApplication().getPackageName();

        Class intentClass = getMainActivityClass();
        if (intentClass == null) {
            return;
        }

        if (applicationIsRunning()) {
            Intent i = new Intent("RNGCMReceiveNotification");
            i.putExtra("bundle", bundle);
            sendBroadcast(i);
            return;
        }

        if (bundle.keySet().contains("notification")) {
            try {
                Bundle notificationBundle = bundle.getBundle("notification")

            } catch (Exception e) {}
        }

        int resourceId = resources.getIdentifier(bundle.getString("largeIcon"), "mipmap", packageName);

        Intent intent = new Intent(this, intentClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Bitmap largeIcon = BitmapFactory.decodeResource(resources, resourceId);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(largeIcon)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(bundle.getString("contentTitle"))
                .setContentText(bundle.getString("message"))
                .setAutoCancel(false)
                .setSound(defaultSoundUri)
                .setTicker(bundle.getString("ticker"))
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notif = notificationBuilder.build();
        notif.defaults |= Notification.DEFAULT_VIBRATE;
        notif.defaults |= Notification.DEFAULT_SOUND;
        notif.defaults |= Notification.DEFAULT_LIGHTS;

        notificationManager.notify(0, notif);
    }
}
