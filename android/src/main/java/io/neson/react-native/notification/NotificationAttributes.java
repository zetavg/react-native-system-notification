package io.neson.react.notification;

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;

public class NotificationAttributes {
    public Integer id;
    public String subject;
    public String message;
    public String action;
    public String smallIcon;
    public Boolean autoCancel;
    public Boolean delayed;
    public Integer delay;
    public Boolean scheduled;
    public String payload;

    public void loadFromReadableMap(ReadableMap readableMap) {
        if (readableMap.hasKey("id")) id = readableMap.getInt("id");
        if (readableMap.hasKey("subject")) subject = readableMap.getString("subject");
        if (readableMap.hasKey("message")) message = readableMap.getString("message");
        if (readableMap.hasKey("action")) action = readableMap.getString("action");
        if (readableMap.hasKey("smallIcon")) smallIcon = readableMap.getString("smallIcon");
        if (readableMap.hasKey("autoCancel")) autoCancel = readableMap.getBoolean("autoCancel");
        if (readableMap.hasKey("delayed")) delayed = readableMap.getBoolean("delayed");
        if (readableMap.hasKey("delay")) delay = readableMap.getInt("delay");
        if (readableMap.hasKey("scheduled")) scheduled = readableMap.getBoolean("scheduled");
        if (readableMap.hasKey("payload")) payload = readableMap.getString("payload");
    }

    public ReadableMap asReadableMap() {
        WritableMap writableMap = new WritableNativeMap();

        if (id != null) writableMap.putInt("id", id);
        if (subject != null) writableMap.putString("subject", subject);
        if (message != null) writableMap.putString("message", message);
        if (action != null) writableMap.putString("action", action);
        if (smallIcon != null) writableMap.putString("smallIcon", smallIcon);
        if (autoCancel != null) writableMap.putBoolean("autoCancel", autoCancel);
        if (delayed != null) writableMap.putBoolean("delayed", delayed);
        if (delay != null) writableMap.putInt("delay", delay);
        if (scheduled != null) writableMap.putBoolean("scheduled", scheduled);
        if (payload != null) writableMap.putString("payload", payload);

        return (ReadableMap) writableMap;
    }
}
