# react-native-system-notification [![npm version](https://img.shields.io/npm/v/react-native-system-notification.svg?style=flat-square)](https://www.npmjs.com/package/react-native-system-notification)

Send or schedule Android system notifications for React Native.

<img width="35%" align="right" hspace="1" vspace="1" src="http://i.imgur.com/cY2Z9GH.png"></img>


```js
import React, { DeviceEventEmitter } from 'react-native';
import Notification from 'react-native-system-notification';

// Send a simple notification
Notification.create({ subject: 'Hey', message: 'Yo! Hello world.' });

// Listen to notification-clicking events
DeviceEventEmitter.addListener('sysNotificationClick', function(e) {
  console.log(e);
});

// Custom payload for notifications
Notification.create({
  subject: 'Notification With Payload',
  message: 'This is a notification that contains custom payload.',
  payload: { number: 1, what: true, someAnswer: '42' }
});

// Receive the payload on notification events
DeviceEventEmitter.addListener('sysNotificationClick', function(e) {
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
  repeat: 'week',
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

**Basic**

<dl>
  <dt>id (`number`)</dt>
  <dd>The unique ID of this notification. It will be randomly chosen if not specified.</dd>

  <dt>subject (`string`)</dt>
  <dd>The notification subject. Defaults to the application name on Android.</dd>

  <dt>message (`string`)</dt>
  <dd>The message showen in the notification.</dd>

  <dt>action (`string`)</dt>
  <dd>An action name that can be used to determine what to do when this notification is clicked. Defaults to `DEFAULT`.</dd>

  <dt>payload (`object`)</dt>
  <dd>A custom payload object. It can be retrieved on events of this notification. Defaults to `{}`.</dd>
</dl>

**Scheduling**

<dl>
  <dt>delay (`number`)</dt>
  <dd>Milliseconds to delay before showing this notification after it is created. Useful when creating countdown alarms, reminders, etc. Note that it cannot be used with `sendAt`.</dd>

  <dt>sendAt (`Date`)</dt>
  <dd>Schedule this notification to show on a specified time. Note that it cannot be used with `delay`.</dd>

  <dt>repeatEvery (`string` or `number`)</dt>
  <dd>Must use with `sendAt`. Schedule this notification to repeat. Can be `minute`, `hour`, `halfDay`, `day`, `week`, `month`, `year` or a number of time in milliseconds.</dd>

  <dt>repeatCount (`number`)</dt>
  <dd>Must use with `sendAt` and `repeatEvery`. End repeating this notification after n times. Note that it cannot be used with `endAt`.</dd>

  <dt>endAt (`Date`)</dt>
  <dd>Must use with `sendAt` and `repeatEvery`. End repeating this notification after a specified time. Note that it cannot be used with `repeatCount`.</dd>
</dl>

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

**Customization**

<dl>
  <dt>smallIcon (`string`)</dt>
  <dd>The icon (file name) to show. This icon must be placed in the project's `android/app/src/main/res/mipmap-*` folder. Defaults to `ic_launcher`.</dd>

  <dt>autoClear (`boolean`)</dt>
  <dd>Clear this notification automatically after the user clicks on it. Defaults to `true`.</dd>
</dl>

### Handle Notification Click Event

Register a listener on `sysNotificationClick` events to handle notification clicking:

```js
DeviceEventEmitter.addListener('sysNotificationClick', function(e) {
  console.log(e);
});
```

The action and payload of the notification can be retrieved on these events:

```js
Notification.send({ message: 'Message', action: 'ACTION_NAME', payload: { data: 'Anything' } });
```

```js
DeviceEventEmitter.addListener('sysNotificationClick', function(e) {
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
