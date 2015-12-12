package io.neson.react.notification;

import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.NotificationManager;

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

import io.neson.react.notification.Notification;

import android.util.Log;

public class NotificationModule extends ReactContextBaseJavaModule {
    public Activity mActivity = null;
    public Context mContext = null;
    public NotificationManager mNotificationManager = null;

    public NotificationModule(ReactApplicationContext reactContext, Activity activity) {
        super(reactContext);
        mContext = reactContext;
        mActivity = activity;
        mNotificationManager = (NotificationManager) reactContext.getSystemService(Context.NOTIFICATION_SERVICE);
        listenNotificationEvent();
    }

    @Override
    public String getName() {
        return "NotificationModule";
    }

    @ReactMethod
    public void create(
        Integer notificationID,
        ReadableMap notificationAttributes,
        Callback errorCallback,
        Callback successCallback
    ) {
        try {
            Notification notification = new Notification(getReactApplicationContext(), notificationID, notificationAttributes);

            notification.create();

            successCallback.invoke(notificationID);

        } catch (Exception e) {
            errorCallback.invoke(e.getMessage());
        }
    }

    @ReactMethod
    public void clear(
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
    public void clearAll(
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

    @ReactMethod
    public void getApplicationName(
        Callback errorCallback,
        Callback successCallback
    ) {
        try {
            int stringId = getReactApplicationContext().getApplicationInfo().labelRes;
            successCallback.invoke(getReactApplicationContext().getString(stringId));

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
                params.putString("payload", extras.getString("payload"));

                sendEvent("sysModuleNotificationClick", params);
            }
        }, intentFilter);
    }
}
