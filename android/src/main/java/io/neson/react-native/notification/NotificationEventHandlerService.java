package io.neson.react.notification;

import android.os.Bundle;
import android.app.IntentService;
import android.content.Intent;

public class NotificationEventHandlerService extends IntentService {
    private static final String TAG = "NotificationEventHandlerService";

    public NotificationEventHandlerService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        Intent i = new Intent("NotificationEvent");
        i.putExtra("event", extras.getString("event"));
        i.putExtra("action", extras.getString("action"));
        i.putExtra("payloadString", extras.getString("payloadString"));
        sendBroadcast(i);
    }
}
