package io.neson.react.notification;

import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.app.Activity;

import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableNativeArray;

import io.neson.react.notification.NotificationManager;
import io.neson.react.notification.Notification;
import io.neson.react.notification.NotificationAttributes;
import io.neson.react.notification.NotificationEventReceiver;

import java.util.ArrayList;
import java.util.Set;

import android.util.Log;

/**
 * The main React native module.
 *
 * Provides JS accessible API, bridge Java and JavaScript.
 */
public class NotificationModule extends ReactContextBaseJavaModule {
    final static String PREFERENCES_KEY = "ReactNativeSystemNotification";
    public Activity mActivity = null;
    public Context mContext = null;
    public NotificationManager mNotificationManager = null;

    @Override
    public String getName() {
        return "NotificationModule";
    }

    /**
     * Constructor.
     */
    public NotificationModule(ReactApplicationContext reactContext, Activity activity) {
        super(reactContext);

        this.mContext = reactContext;
        this.mActivity = activity;
        this.mNotificationManager = (NotificationManager) new NotificationManager(reactContext);

        listenNotificationEvent();
    }

    /**
     * React method to create or update a notification.
     */
    @ReactMethod
    public void rCreate(
        Integer notificationID,
        ReadableMap notificationAttributes,
        Callback errorCallback,
        Callback successCallback
    ) {
        try {
            NotificationAttributes a = getNotificationAttributesFromReadableMap(notificationAttributes);
            Notification n = mNotificationManager.createOrUpdate(notificationID, a);

            successCallback.invoke(n.getAttributes().asReadableMap());

        } catch (Exception e) {
            errorCallback.invoke(e.getMessage());
            Log.e("ReactSystemNotification", "NotificationModule: rCreate Error: " + e.getMessage());
        }
    }

    /**
     * React method to get all notification ids.
     */
    @ReactMethod
    public void rGetIDs(
        Callback errorCallback,
        Callback successCallback
    ) {
        try {
            ArrayList<Integer> ids = mNotificationManager.getIDs();
            WritableArray rids = new WritableNativeArray();

            for (Integer id: ids) {
                rids.pushInt(id);
            }

            successCallback.invoke((ReadableArray) rids);

        } catch (Exception e) {
            errorCallback.invoke(e.getMessage());
            Log.e("ReactSystemNotification", "NotificationModule: rGetIDs Error: " + e.getMessage());
        }
    }

    /**
     * React method to get data of a notification.
     */
    @ReactMethod
    public void rFind(
        Integer notificationID,
        Callback errorCallback,
        Callback successCallback
    ) {
        try {
            Notification n = mNotificationManager.find(notificationID);
            successCallback.invoke(n.getAttributes().asReadableMap());

        } catch (Exception e) {
            errorCallback.invoke(e.getMessage());
            Log.e("ReactSystemNotification", "NotificationModule: rFind Error: " + e.getMessage());
        }
    }

    /**
     * React method to delete (i.e. cancel a scheduled) notification.
     */
    @ReactMethod
    public void rDelete(
        int notificationID,
        Callback errorCallback,
        Callback successCallback
    ) {
        try {
            Notification n = mNotificationManager.delete(notificationID);

            successCallback.invoke(n.getAttributes().asReadableMap());

        } catch (Exception e) {
            errorCallback.invoke(e.getMessage());
            Log.e("ReactSystemNotification", "NotificationModule: rDelete Error: " + e.getMessage());
        }
    }

    /**
     * React method to delete (i.e. cancel a scheduled) notification.
     */
    @ReactMethod
    public void rDeleteAll(
        Callback errorCallback,
        Callback successCallback
    ) {
        try {
            ArrayList<Integer> ids = mNotificationManager.getIDs();

            for (Integer id: ids) {
                try {
                    mNotificationManager.delete(id);
                } catch (Exception e) {
                    Log.e("ReactSystemNotification", "NotificationModule: rDeleteAll Error: " + e.getMessage());
                }
            }

            successCallback.invoke();

        } catch (Exception e) {
            errorCallback.invoke(e.getMessage());
            Log.e("ReactSystemNotification", "NotificationModule: rDeleteAll Error: " + e.getMessage());
        }
    }

    /**
     * React method to clear a notification.
     */
    @ReactMethod
    public void rClear(
        int notificationID,
        Callback errorCallback,
        Callback successCallback
    ) {
        try {
            Notification n = mNotificationManager.clear(notificationID);

            successCallback.invoke(n.getAttributes().asReadableMap());

        } catch (Exception e) {
            errorCallback.invoke(e.getMessage());
            Log.e("ReactSystemNotification", "NotificationModule: rClear Error: " + e.getMessage());
        }
    }

    /**
     * React method to clear all notifications of this app.
     */
    @ReactMethod
    public void rClearAll(
        Callback errorCallback,
        Callback successCallback
    ) {
        try {
            mNotificationManager.clearAll();
            successCallback.invoke();

        } catch (Exception e) {
            errorCallback.invoke(e.getMessage());
            Log.e("ReactSystemNotification", "NotificationModule: rClearAll Error: " + e.getMessage());
        }
    }

    @ReactMethod
    public void rGetApplicationName(
        Callback errorCallback,
        Callback successCallback
    ) {
        try {
            int stringId = getReactApplicationContext().getApplicationInfo().labelRes;
            successCallback.invoke(getReactApplicationContext().getString(stringId));

        } catch (Exception e) {
            errorCallback.invoke(e.getMessage());
            Log.e("ReactSystemNotification", "NotificationModule: rGetApplicationName Error: " + e.getMessage());
        }
    }

    /**
     * Emit JavaScript events.
     */
    private void sendEvent(
        String eventName,
        Object params
    ) {
        getReactApplicationContext()
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit(eventName, params);

        Log.i("ReactSystemNotification", "NotificationModule: sendEvent (to JS): " + eventName);
    }

    private NotificationAttributes getNotificationAttributesFromReadableMap(
        ReadableMap readableMap
    ) {
        NotificationAttributes notificationAttributes = new NotificationAttributes();

        notificationAttributes.loadFromReadableMap(readableMap);

        return notificationAttributes;
    }

    private void listenNotificationEvent() {
        IntentFilter intentFilter = new IntentFilter("NotificationEvent");

        getReactApplicationContext().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Bundle extras = intent.getExtras();

                WritableMap params = Arguments.createMap();
                params.putInt("notificationID", extras.getInt(NotificationEventReceiver.NOTIFICATION_ID));
                params.putString("action", extras.getString(NotificationEventReceiver.ACTION));
                params.putString("payload", extras.getString(NotificationEventReceiver.PAYLOAD));

                sendEvent("sysModuleNotificationClick", params);
            }
        }, intentFilter);
    }
}
