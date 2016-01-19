package io.neson.react.notification;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.google.gson.Gson;
import org.mozilla.javascript.*;
import java.util.Map;

import com.google.android.gms.gcm.GcmListenerService;

public class GCMNotificationListenerService extends GcmListenerService {

    private static final String TAG = "GCMNotificationListenerService";

    // This is compressed manually from the encodeNativeNotification function
    // in index.js
    // https://skalman.github.io/UglifyJS-online/
    private static final String NOTIFICATION_ATTRIBUTES_JS_PARSING_CODE = "function encodeNativeNotification(e){if(\"string\"==typeof e&&(e=JSON.parse(e)),e.smallIcon||(e.smallIcon=\"ic_launcher\"),e.id||(e.id=parseInt(1e5*Math.random())),e.action||(e.action=\"DEFAULT\"),e.payload||(e.payload={}),void 0===e.autoClear&&(e.autoClear=!0),void 0===e.tickerText&&(e.tickerText=e.subject?e.subject+\": \"+e.message:e.message),void 0===e.priority&&(e.priority=1),void 0===e.sound&&(e.sound=\"default\"),void 0===e.vibrate&&(e.vibrate=\"default\"),void 0===e.lights&&(e.lights=\"default\"),e.delayed=void 0!==e.delay,e.scheduled=void 0!==e.sendAt,e.sendAt&&\"object\"!=typeof e.sendAt&&(e.sendAt=new Date(e.sendAt)),e.endAt&&\"object\"!=typeof e.endAt&&(e.endAt=new Date(e.endAt)),e.when&&\"object\"!=typeof e.when&&(e.when=new Date(e.when)),void 0!==e.sendAt&&(e.sendAtYear=e.sendAt.getFullYear(),e.sendAtMonth=e.sendAt.getMonth()+1,e.sendAtDay=e.sendAt.getDate(),e.sendAtWeekDay=e.sendAt.getDay(),e.sendAtHour=e.sendAt.getHours(),e.sendAtMinute=e.sendAt.getMinutes()),e.sendAt&&(e.sendAt=e.sendAt.getTime()),e.endAt&&(e.endAt=e.endAt.getTime()),e.when&&(e.when=e.when.getTime()),void 0!==e.sendAt&&(\"number\"==typeof e.repeatEvery?(e.repeatType=\"time\",e.repeatTime=e.repeatEvery):\"string\"==typeof e.repeatEvery&&(e.repeatType=e.repeatEvery),e.repeatCount))if(\"number\"==typeof e.repeatEvery)e.endAt=parseInt(e.sendAt+e.repeatEvery*e.repeatCount+e.repeatEvery/2);else if(\"string\"==typeof e.repeatEvery)switch(e.repeatEvery){case\"minute\":e.endAt=e.sendAt+6e4*e.repeatCount+3e4;break;case\"hour\":e.endAt=e.sendAt+36e5*e.repeatCount+18e5;break;case\"halfDay\":e.endAt=e.sendAt+432e5*e.repeatCount+216e5;break;case\"day\":e.endAt=e.sendAt+864e5*e.repeatCount+432e5;break;case\"week\":e.endAt=e.sendAt+6048e5*e.repeatCount+2592e5;break;case\"month\":e.endAt=e.sendAt+2592e6*e.repeatCount+1296e6;break;case\"year\":e.endAt=e.sendAt+31536e6*e.repeatCount+864e7}return e.sendAt&&(e.sendAt=\"\"+e.sendAt),e.endAt&&(e.endAt=\"\"+e.endAt),e.when&&(e.when=\"\"+e.when),e.repeatEvery&&(e.repeatEvery=\"\"+e.repeatEvery),e.progress&&(e.progress=1e3*e.progress),e.payload=JSON.stringify(e.payload),e}";

    @Override
    public void onMessageReceived(String from, Bundle bundle) {
        sendNotification(bundle);

        String notificationString = bundle.getString("notification");

        if (notificationString != null) {
            sendSysNotification(notificationString);
        }
    }

    private void sendNotification(Bundle bundle) {
        Log.d(TAG, "sendNotification");

        Intent i = new Intent("com.oney.gcm.GCMReceiveNotification");
        i.putExtra("bundle", bundle);
        sendOrderedBroadcast(i, null);
    }

    private void sendSysNotification(String notificationString) {
        Object[] functionParams = new Object[] { notificationString };

        Context rhino = Context.enter();
        rhino.setOptimizationLevel(-1);

        Scriptable scope = rhino.initStandardObjects();

        rhino.evaluateString(scope, NOTIFICATION_ATTRIBUTES_JS_PARSING_CODE, "script", 1, null);

        Function function = (Function) scope.get("encodeNativeNotification", scope);

        Object parsedParams = function.call(rhino, scope, scope, functionParams);

        Gson gson = new Gson();
        String parsedParamsJson = gson.toJson(parsedParams);

        Log.d(TAG, "Notification parsedParams: " + parsedParamsJson);

        NotificationAttributes notificationAttributes = new NotificationAttributes();
        notificationAttributes.loadFromMap((Map) parsedParams);

        NotificationManager nm = new NotificationManager(this);
        nm.create(notificationAttributes.id, notificationAttributes);
    }
}
