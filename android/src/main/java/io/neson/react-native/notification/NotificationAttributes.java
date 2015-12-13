package io.neson.react.notification;

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;

public class NotificationAttributes {
    public Integer id;
    public String subject;
    public String message;
    public String action;
    public String payload;

    public String smallIcon;
    public Boolean autoCancel;

    public Boolean delayed;
    public Integer delay;

    public Boolean scheduled;
    public Long sendAt;
    public Long endAt;
    public Integer sendAtYear;
    public Integer sendAtMonth;
    public Integer sendAtDay;
    public Integer sendAtWeekDay;
    public Integer sendAtHour;
    public Integer sendAtMinute;

    public String repeatType;
    public Integer repeatTime;
    public Integer repeatCount;


    public void loadFromReadableMap(ReadableMap readableMap) {
        if (readableMap.hasKey("id")) id = readableMap.getInt("id");
        if (readableMap.hasKey("subject")) subject = readableMap.getString("subject");
        if (readableMap.hasKey("message")) message = readableMap.getString("message");
        if (readableMap.hasKey("action")) action = readableMap.getString("action");
        if (readableMap.hasKey("payload")) payload = readableMap.getString("payload");

        if (readableMap.hasKey("smallIcon")) smallIcon = readableMap.getString("smallIcon");
        if (readableMap.hasKey("autoCancel")) autoCancel = readableMap.getBoolean("autoCancel");

        if (readableMap.hasKey("delayed")) delayed = readableMap.getBoolean("delayed");
        if (readableMap.hasKey("delay")) delay = readableMap.getInt("delay");

        if (readableMap.hasKey("scheduled")) scheduled = readableMap.getBoolean("scheduled");
        if (readableMap.hasKey("sendAt")) sendAt = Long.parseLong(readableMap.getString("sendAt"));
        if (readableMap.hasKey("endAt")) endAt = Long.parseLong(readableMap.getString("endAt"));
        if (readableMap.hasKey("sendAtYear")) sendAtYear = readableMap.getInt("sendAtYear");
        if (readableMap.hasKey("sendAtMonth")) sendAtMonth = readableMap.getInt("sendAtMonth");
        if (readableMap.hasKey("sendAtDay")) sendAtDay = readableMap.getInt("sendAtDay");
        if (readableMap.hasKey("sendAtWeekDay")) sendAtWeekDay = readableMap.getInt("sendAtWeekDay");
        if (readableMap.hasKey("sendAtHour")) sendAtHour = readableMap.getInt("sendAtHour");
        if (readableMap.hasKey("sendAtMinute")) sendAtMinute = readableMap.getInt("sendAtMinute");

        if (readableMap.hasKey("repeatType")) repeatType = readableMap.getString("repeatType");
        if (readableMap.hasKey("repeatTime")) repeatTime = readableMap.getInt("repeatTime");
        if (readableMap.hasKey("repeatCount")) repeatCount = readableMap.getInt("repeatCount");
    }

    public ReadableMap asReadableMap() {
        WritableMap writableMap = new WritableNativeMap();

        if (id != null) writableMap.putInt("id", id);
        if (subject != null) writableMap.putString("subject", subject);
        if (message != null) writableMap.putString("message", message);
        if (action != null) writableMap.putString("action", action);
        if (payload != null) writableMap.putString("payload", payload);

        if (smallIcon != null) writableMap.putString("smallIcon", smallIcon);
        if (autoCancel != null) writableMap.putBoolean("autoCancel", autoCancel);

        if (delayed != null) writableMap.putBoolean("delayed", delayed);
        if (delay != null) writableMap.putInt("delay", delay);

        if (scheduled != null) writableMap.putBoolean("scheduled", scheduled);
        if (sendAt != null) writableMap.putString("sendAt", Long.toString(sendAt));
        if (endAt != null) writableMap.putString("endAt", Long.toString(endAt));
        if (sendAtYear != null) writableMap.putInt("sendAtYear", sendAtYear);
        if (sendAtMonth != null) writableMap.putInt("sendAtMonth", sendAtMonth);
        if (sendAtDay != null) writableMap.putInt("sendAtDay", sendAtDay);
        if (sendAtWeekDay != null) writableMap.putInt("sendAtWeekDay", sendAtWeekDay);
        if (sendAtHour != null) writableMap.putInt("sendAtHour", sendAtHour);
        if (sendAtMinute != null) writableMap.putInt("sendAtMinute", sendAtMinute);

        if (repeatType != null) writableMap.putString("repeatType", repeatType);
        if (repeatTime != null) writableMap.putInt("repeatTime", repeatTime);
        if (repeatCount != null) writableMap.putInt("repeatCount", repeatCount);

        return (ReadableMap) writableMap;
    }
}
