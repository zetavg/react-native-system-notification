# react-native-system-notification [![npm version](https://img.shields.io/npm/v/react-native-system-notification.svg?style=flat-square)](https://www.npmjs.com/package/react-native-system-notification)

Send or schedule Android system notifications for React Native.

<img width="35%" align="right" hspace="1" vspace="1" src="http://i.imgur.com/cY2Z9GH.png"></img>

## Table of Contents

  * [Installation](#installation)
  * [Usage](#usage)
    * [Creating Notifications](#creating-notifications)
      * [Basic](#basic)
      * [Scheduling](#scheduling)
      * [Customization](#customization)
    * [Handle Notification Click Event](#handle-notification-click-event)
    * [Manage Scheduled Notifications](#manage-scheduled-notifications)
    * [Clearing Notifications](#clearing-notifications)
  * [Push Notifications On Android](#push-notifications-on-android)

---

```js
import React, { DeviceEventEmitter } from 'react-native';
import Notification from 'react-native-system-notification';

// Send a simple notification
Notification.create({ subject: 'Hey', message: 'Yo! Hello world.' });

// Listen to notification-clicking events
Notification.addListener('press', function(e) {
  console.log(e);
});

// Custom payload for notifications
Notification.create({
  subject: 'Notification With Payload',
  message: 'This is a notification that contains custom payload.',
  payload: { number: 1, what: true, someAnswer: '42' }
});

// Receive the payload on notification events
Notification.addListener('press', function(e) {
  console.log(e.payload);  // => { number: 1, what: true, someAnswer: '42' }
});

// Customize notification
Notification.create({
  subject: 'Notification With Custom Icon',
  message: 'This is a notification with a specified icon.',
  smallIcon: 'ic_alert'
});

// Scheduled notifications
Notification.create({
  subject: 'Scheduled Notification',
  message: 'This notification will show on every Friday morning at 8:30 AM, starts at 2015/9/9 and end after 10 times.',
  sendAt: new Date(2015, 9, 9, 8, 30),
  repeatEvery: 'week',
  count: 10
});
```

## Installation

- Run `npm install react-native-system-notification --save` to install using npm.

- Add the following two lines to `android/settings.gradle`:

```gradle
include ':react-native-system-notification'
project(':react-native-system-notification').projectDir = new File(settingsDir, '../node_modules/react-native-system-notification/android')
```

- Edit `android/app/build.gradle` and add the annoated lines as below:

```gradle
...

dependencies {
    compile fileTree(dir: "libs", include: ["*.jar"])
    compile "com.android.support:appcompat-v7:23.0.1"
    compile "com.facebook.react:react-native:0.16.+"
    compile project(':react-native-system-notification')  // <- Add this line
}
```

- Edit `android/app/src/main/AndroidManifest.xml` and add the annoated lines as below:

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.reactnativeproject">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.GET_TASKS" />                       <!-- <- Add this line -->
    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED"/>           <!-- <- Add this line -->
    <uses-permission android:name="android.permission.VIBRATE"/>                          <!-- <- Add this line -->

    <application
      android:allowBackup="true"
      android:label="@string/app_name"
      android:icon="@mipmap/ic_launcher"
      android:theme="@style/AppTheme">

...

      <activity android:name="com.facebook.react.devsupport.DevSettingsActivity" />
      <receiver android:name="io.neson.react.notification.NotificationEventReceiver" />   <!-- <- Add this line -->
      <receiver android:name="io.neson.react.notification.NotificationPublisher" />       <!-- <- Add this line -->
      <receiver android:name="io.neson.react.notification.SystemBootEventReceiver">       <!-- <- Add this line -->
        <intent-filter>                                                                   <!-- <- Add this line -->
          <action android:name="android.intent.action.BOOT_COMPLETED"></action>           <!-- <- Add this line -->
        </intent-filter>                                                                  <!-- <- Add this line -->
      </receiver>                                                                         <!-- <- Add this line -->
    </application>

</manifest>
```

> The `RECEIVE_BOOT_COMPLETED` permission is used to re-register all scheduled notifications after reboot.  
> Requesting `VIBRATE` permission is required if you want to make the device vibrate while sending notifications.

- Edit `MainActivity.java` (usually at `android/app/src/main/java/com/<project-name>/MainActivity.java`) and add the annoated lines as below:

```java
import com.facebook.react.LifecycleState;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactRootView;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.shell.MainReactPackage;
import com.facebook.soloader.SoLoader;

import io.neson.react.notification.NotificationPackage;                // <- Add this line

public class MainActivity extends Activity implements DefaultHardwareBackBtnHandler {

...

        mReactRootView = new ReactRootView(this);

        mReactInstanceManager = ReactInstanceManager.builder()
                .setApplication(getApplication())
                .setBundleAssetName("index.android.bundle")
                .setJSMainModuleName("index.android")
                .addPackage(new MainReactPackage())
                .addPackage(new NotificationPackage(this))             // <- Add this line
                .setUseDeveloperSupport(BuildConfig.DEBUG)
                .setInitialLifecycleState(LifecycleState.RESUMED)
                .build();

...
```

## Usage

### Creating Notifications

Just do:

```js
Notification.create({
  id: 1337,
  subject: 'Notification With Payload',
  message: 'This is a notification that contains custom payload.',
  smallIcon: 'ic_launcher',
  autoClear: true,
  payload: { number: 1, what: true, someAnswer: '42' }
});
```

> All functions of this module will return [promise](https://www.promisejs.org/)s with the notification object handing in. So you can get the data of the notification and do anything that is needed, like this:
>
> ```js
> Notification.create({ message: 'Testing.' }).then(function(notification) {
>   console.log(notification);
>   console.log(notification.id);
> });
> ```

All available options on a notification are listed below:

#### Basic

**id (`number`)**  
The unique ID of this notification. It will be randomly chosen if not specified.

**subject (`string`)**  
The notification subject. Defaults to the application name on Android.

**message (`string`)**  
The message showen in the notification.

**action (`string`)**  
An action name that can be used to determine what to do when this notification is clicked. Defaults to `DEFAULT`.

**payload (`object`)**  
A custom payload object. It can be retrieved on events of this notification. Defaults to `{}`.


#### Scheduling

**delay (`number`)**  
Milliseconds to delay before showing this notification after it is created. Useful when creating countdown alarms, reminders, etc. Note that it cannot be used with `sendAt`.

**sendAt (`Date`)**  
Schedule this notification to show on a specified time. Note that it cannot be used with `delay`.

**repeatEvery (`string` or `number`)**  
Must use with `sendAt`. Schedule this notification to repeat. Can be `minute`, `hour`, `halfDay`, `day`, `week`, `month`, `year` or a number of time in milliseconds.

**repeatCount (`number`)**  
Must use with `sendAt` and `repeatEvery`. End repeating this notification after n times. Note that it cannot be used with `endAt`.

**endAt (`Date`)**  
Must use with `sendAt` and `repeatEvery`. End repeating this notification after a specified time. Note that it cannot be used with `repeatCount`.


> Some Samples of Scheduled Notifications
>
> ```js
> Notification.create({
>   subject: 'Scheduled Notification',
>   message: 'This notification will show on every Friday morning at 8:30 AM, starts at 2015/9/9 and end after 10 times.',
>   sendAt: new Date(2015, 9, 9, 8, 30),
>   repeatEvery: 'week',
>   repeatCount: 10
> });
> ```
>
> ```js
> Notification.create({
>   subject: 'Scheduled Notification',
>   message: 'This notification will show on 2015/9/9 morning at 8:30 AM, and repeat for 10 times every minute.',
>   sendAt: new Date(2015, 9, 9, 8, 30),
>   repeatEvery: 60000,
>   repeatCount: 10
> });
> ```
>
> ```js
> Notification.create({
>   subject: 'Delayed Notification',
>   message: 'This notification will show after 10 seconds, even the app has been stoped.',
>   delay: 10000
> });
> ```

#### Customization

**priority (`number`)**  
Priority of this notification, can be `-2`, `-1`, `0`, `1`, `2`. When this is set to `1` or `2`, heads-up notification will be more likely to show on Android 5+. Defaults to `1`.

**smallIcon (`string`)**  
The icon (file name) to show. This icon must be placed in the project's `android/app/src/main/res/mipmap-*` folder. Defaults to `ic_launcher`.

**largeIcon (`string`)**  
Not yet implemented.

**sound (`string`)**  
Set the sound to play. Defaults to `default` as using the default notification sound, or set this to `null` to disable the sound. Other options are not yet implemented.

**vibrate (`string`)**  
Set the vibration pattern to use. Defaults to `default` as using the default notification vibrate, or set this to `null` to disable the vibrate. Other options are not yet implemented.

**lights (`string`)**  
Set the desired color for the indicator LED on the device. Defaults to `default` as using the default notification lights, or set this to `null` to disable the lights. Other options are not yet implemented.

**autoClear (`boolean`)**  
Clear this notification automatically after the user clicks on it. Defaults to `true`.

**onlyAlertOnce (`boolean`)**  
Do not let the sound, vibrate and ticker to be played if the notification is already showing.

**tickerText (`string`)**  
Set the text to show on ticker. Defaults to `<subject>: <message>`. Set this to `null` to disable ticker.

**when (`Date`)**  
Add a timestamp pertaining to the notification (usually the time the event occurred).

**bigText (`string`)**  
Set the text to be shown when the user expand the notification.

**subText (`string`)**  
Set the third line of text in the platform notification template. Note that it cannot be used with `progress`.

**progress (`number`)**  
Set the progress this notification represents, range: `0.0` ~ `1.0`. Set this to a number lower then zero to get an indeterminate progress. Note that it cannot be used with `subText`.

**color (`string`)**  
Color to be applied by the standard Style templates when presenting this notification.

**number (`number`)**  
Set a number on the notification.

**private (`boolean`)**  
Not yet implemented.

**ongoing (`boolean`)**  
Not yet implemented.

**category (`string`)**  
Set the notification category, e.g.: `alarm`, `call`, `email`, `event`, `progress`, `reminder`, `social`. It may be used by the Android system for ranking and filtering.

**localOnly (`boolean`)**  
Set whether or not this notification should not bridge to other devices.

### Handle Notification Click Event

Register a listener on `sysNotificationClick` events to handle notification clicking:

```js
Notification.addListener('press', function(e) {
  console.log(e);
});
```

The action and payload of the notification can be retrieved on these events:

```js
Notification.send({ message: 'Message', action: 'ACTION_NAME', payload: { data: 'Anything' } });
```

```js
Notification.addListener('press', function(e) {
  switch (e.action) {
    case 'ACTION_NAME':
      console.log(`Action Triggered! Data: ${e.payload.data}`);
      break;

    case 'ANOTHER_ACTION_NAME':
      console.log(`Another Action Triggered! Data: ${e.payload.data}`);
      break;
  }
});
```

### Manage Scheduled Notifications

Sometimes you'll need to get the scheduled notifications (which has `delay` or `sendAt` set up) that you had created before. You can use `Notification.getIDs()` to retrieve an array of IDs of available (i.e. will be send in the future) scheduled notifications.

```js
Notification.getIDs().then(function(ids) {
  console.log(ids);  // Array of ids
});
```

and use `Notification.find(notificationID)` to get data of an notification.

```js
Notification.find(notificationID).then(function(notification) {
  console.log(notification);
});
```

or just cancel it with `Notification.delete(notificationID)`:

```js
Notification.delete(notificationID);
```

Want to cancel all scheduled notifications set by your app? Sure:

```js
Notification.deleteAll();
```

> To update a scheduled notification, just use `Notification.create()` with the same id.

### Clearing Notifications

When you want to clear a notification from the system statusbar, just use:

```js
Notification.clearAll();
```

or:

```js
Notification.clear(notificationID);
```


## Push Notifications On Android

Sending push notification via web servers to Android is also easy! With [react-native-gcm-android](https://github.com/oney/react-native-gcm-android) intergrated, you can just pass notification arguments through GCM (with the same format as JavaScript), your app will show it directly or put it into schedule. To set this up, follow these directions:

1. Run `npm install react-native-gcm-android --save` to add react-native-gcm-android to your app

2. Setup GCM, follow [react-native-gcm-android](https://github.com/oney/react-native-gcm-android/tree/17b1e54)'s [README](https://github.com/oney/react-native-gcm-android/blob/17b1e54/README.md) to get GCM working.

3. Open `android/app/src/main/AndroidManifest.xml`, change `com.oney.gcm.RNGcmListenerService` to `io.neson.react.notification.GCMNotificationListenerService`.

Then you can send push notifications like this, putting an `notification` object (just as the same thing that you use in JavaScript Notification.create()) into the GCM `data` (curl example):

```bash
curl -X POST -H "Authorization: key=<your_google_api_key>" -H "Content-Type: application/json" -d '
{
  "data": {
    "notification": {
      "subject": "Hello GCM",
      "message": "Hello from the server side!"
    }
  },
  "to" : "<device_token>"
}' 'https://gcm-http.googleapis.com/gcm/send'
```

The notification will be created natively (so this works even if the app is closed) when the device received the GCM message.
