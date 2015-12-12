# react-native-system-notification [![npm version](https://img.shields.io/npm/v/react-native-system-notification.svg?style=flat-square)](https://www.npmjs.com/package/react-native-system-notification)

Android system notifications for React Native.

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
  subject: 'Delayed Notification',
  message: 'This notification will show after 10 seconds.',
  delay: 10000
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

    defaultConfig {
        applicationId "com.reactnativeproject"
        minSdkVersion 16
        targetSdkVersion 22
        versionCode 1
        versionName "1.0"
        multiDexEnabled true                              // <- Add this line
        ndk {
            abiFilters "armeabi-v7a", "x86"
        }
    }

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
    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED"/>               <!-- <- Add this line -->
    <uses-permission android:name="android.permission.VIBRATE"/>                              <!-- <- Add this line -->

    <application
      android:allowBackup="true"
      android:label="@string/app_name"
      android:icon="@mipmap/ic_launcher"
      android:theme="@style/AppTheme">

...

      <activity android:name="com.facebook.react.devsupport.DevSettingsActivity" />
      <service android:name="io.neson.react.notification.NotificationEventHandlerService" />  <!-- <- Add this line -->
      <receiver android:name="io.neson.react.notification.NotificationPublisher" />           <!-- <- Add this line -->
    </application>

</manifest>
```

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

```js
Notification.create({
  id: 1337,
  subject: 'Notification With Payload',
  message: 'This is a notification that contains custom payload.',
  smallIcon: 'ic_launcher',
  autoCancel: true,
  delay: undefined,
  payload: { number: 1, what: true, someAnswer: '42' }
});
```

The function will return a [promise](https://www.promisejs.org/).

```js
Notification.create({ message: 'Testing.' }).then(function(notification) {
  console.log(notification);
  console.log(notification.id);
});
```

### Handle Notification Click Event

```js
DeviceEventEmitter.addListener('sysNotificationClick', function(e) {
  console.log(e);
});
```

```js
Notification.send({ message: 'Message', action: 'ACTION_NAME', payload: { data: 'Anything' } });
```

```js
DeviceEventEmitter.addListener('sysNotificationClick', function(e) {
  switch (e.action) {
    case 'ACTION_NAME':
      console.log('Action Triggered!');
      break;
    case 'ANOTHER_ACTION_NAME':
      console.log('Another Action Triggered!');
      break;
  }
});
```

### Get Created Notifications

```js
Notification.getIDs().then(function(ids) {
  console.log(ids);  // Array of ids
});
```

Only delayed or scheduled notifications will have an record.

```js
Notification.find(notificationID).then(function(notification) {
  console.log(notification);
});
```

### Clearing Notifications

```js
Notification.clear(notificationID);
Notification.clearAll();
```

This will only clear the notification from the system statusbar. To cancel scheduled notifications, please use `Notification.delete(id)`.

### Canceling Notifications

```js
Notification.delete(notificationID);
Notification.deleteAll();
```
