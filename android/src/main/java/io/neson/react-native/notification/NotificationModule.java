package io.neson.react.notification;

import android.app.Activity;

import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Notification;
import android.app.NotificationManager;
import android.support.annotation.Nullable;

import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;

import io.neson.react.notification.NotificationEventHandlerService;
import io.neson.react.notification.NotificationPublisher;

import android.util.Log;

public class NotificationModule extends ReactContextBaseJavaModule {
    public Activity mActivity = null;
    public Context mContext = null;
    public AlarmManager mAlarmManager = null;

    NotificationManager mNotificationManager = (NotificationManager) getReactApplicationContext().getSystemService(getReactApplicationContext().NOTIFICATION_SERVICE);

    public NotificationModule(ReactApplicationContext reactContext, Activity activity) {
        super(reactContext);
        mContext = reactContext;
        mActivity = activity;
        mAlarmManager = (AlarmManager) getReactApplicationContext().getSystemService(getReactApplicationContext().ALARM_SERVICE);
        listenNotificationEvent();
    }

    @Override
    public String getName() {
        return "NotificationModule";
    }

    @ReactMethod
    public void send(
        String subject,
        String message,
        Integer notificationID,
        String actionName,
        String iconName,
        Boolean autoCancel,
        String payloadString,
        int delay,
        Callback errorCallback,
        Callback successCallback
    ) {
        try {
            Intent intent = new Intent(getReactApplicationContext(), NotificationEventHandlerService.class);

            intent.putExtra("event", "sysModuleNotificationClick");
            intent.putExtra("action", actionName);
            intent.putExtra("payloadString", payloadString);

            PendingIntent pIntent = PendingIntent.getService(getReactApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification notification = new Notification.Builder(getReactApplicationContext())
                .setSmallIcon(getReactApplicationContext().getResources().getIdentifier(iconName, "mipmap", getReactApplicationContext().getPackageName()))
                .setContentTitle(subject)
                .setContentText(message)
                .setContentIntent(pIntent)
                .setAutoCancel(autoCancel)
                .build();

            if (delay > 0) {
                scheduleNotificationByDelay(notificationID, notification, delay);
            } else {
                mNotificationManager.notify(notificationID, notification);
            }

            successCallback.invoke(notificationID);

        } catch (Exception e) {
            errorCallback.invoke(e.getMessage());
        }
    }

    @ReactMethod
    public void cancel(
        int notificationID,
        Callback errorCallback,
        Callback successCallback
    ) {
        try {
            mNotificationManager.cancel(notificationID);
            successCallback.invoke(notificationID);

        } catch (Exception e) {
            errorCallback.invoke(e.getMessage());
        }
    }

    @ReactMethod
    public void cancelAll(
        Callback errorCallback,
        Callback successCallback
    ) {
        try {
            mNotificationManager.cancelAll();
            successCallback.invoke();

        } catch (Exception e) {
            errorCallback.invoke(e.getMessage());
        }
    }

    private void sendEvent(
        String eventName,
        Object params
    ) {
        getReactApplicationContext()
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit(eventName, params);
    }

    private void listenNotificationEvent() {
        IntentFilter intentFilter = new IntentFilter("NotificationEvent");

        getReactApplicationContext().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Bundle extras = intent.getExtras();

                WritableMap params = Arguments.createMap();
                params.putString("action", extras.getString("action"));
                params.putString("payload", extras.getString("payloadString"));

                sendEvent(extras.getString("event"), params);
            }
        }, intentFilter);
    }

    private PendingIntent getScheduleNotificationIntent(int notificationID, Notification notification) {
        Intent notificationIntent = new Intent(getReactApplicationContext(), NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, notificationID);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getReactApplicationContext(), notificationID, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        return pendingIntent;
    }

    private void scheduleNotificationByDelay(int notificationID, Notification notification, int delay) {
        PendingIntent pendingIntent = getScheduleNotificationIntent(notificationID, notification);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        mAlarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }
}
