package io.neson.react.notification;

import android.os.Bundle;
import android.os.SystemClock;
import android.app.IntentService;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Intent;
import android.content.Context;

import java.util.List;

public class NotificationEventHandlerService extends IntentService {
    private static final String TAG = "NotificationEventHandlerService";

    public NotificationEventHandlerService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        if (!applicationIsRunning()) {
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage(getApplication().getPackageName());
            startActivity(launchIntent);
        }

        while (!applicationIsRunning()) {
            SystemClock.sleep(1000);
        }

        // TODO: Ensure the app is ready in a more reasonable way
        SystemClock.sleep(500);

        Intent i = new Intent("NotificationEvent");
        i.putExtra("action", extras.getString("action"));
        i.putExtra("payload", extras.getString("payload"));
        sendBroadcast(i);
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
}
