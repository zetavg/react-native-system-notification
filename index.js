'use strict';

var React = require('react-native');
var { DeviceEventEmitter } = React;
var EventEmitter = require('EventEmitter');

var NotificationModule = require('react-native').NativeModules.NotificationModule;

// Warp the native module so we can do some pre/post processing to have a cleaner API.
var Notification = {
  create: function(attributes = {}) {
    return new Promise(function(resolve, reject) {
      NotificationModule.rGetApplicationName(function(e) {}, function(applicationName) {

        // Set defaults
        if (!attributes.subject) attributes.subject = applicationName;
        attributes = encodeNativeNotification(attributes);

        NotificationModule.rCreate(attributes.id, attributes, reject, function(notification) {
          resolve(decodeNativeNotification(notification));
        });
      });
    });
  },

  getIDs: function() {
    return new Promise(function(resolve, reject) {
      NotificationModule.rGetIDs(reject, resolve);
    });
  },

  find: function(id) {
    return new Promise(function(resolve, reject) {
      NotificationModule.rFind(id, reject, function(notification) {
        resolve(decodeNativeNotification(notification));
      });
    });
  },

  delete: function(id) {
    return new Promise(function(resolve, reject) {
      NotificationModule.rDelete(id, reject, function(notification) {
        resolve(decodeNativeNotification(notification));
      });
    });
  },

  deleteAll: function() {
    return new Promise(function(resolve, reject) {
      NotificationModule.rDeleteAll(reject, resolve);
    });
  },

  clear: function(id) {
    return new Promise(function(resolve, reject) {
      NotificationModule.rClear(id, reject, function(notification) {
        resolve(decodeNativeNotification(notification));
      });
    });
  },

  clearAll: function() {
    return new Promise(function(resolve, reject) {
      NotificationModule.rClearAll(reject, function(notification) {
        resolve(decodeNativeNotification(notification));
      });
    });
  },

  module: NotificationModule
}

module.exports = Notification;

// Encode the JS notification to pass into the native model
function encodeNativeNotification(attributes) {
  // Set defaults
  if (!attributes.smallIcon) attributes.smallIcon = 'ic_launcher';
  if (!attributes.id) attributes.id = parseInt(Math.random() * 100000);
  if (!attributes.action) attributes.action = 'DEFAULT';
  if (!attributes.payload) attributes.payload = {};
  if (attributes.autoCancel === undefined) attributes.autoCancel = true;
  attributes.delayed = (attributes.delay !== undefined);
  attributes.scheduled = (attributes.sendAt !== undefined);

  if (attributes.sendAt !== undefined) {
    if (attributes.repeatCount === undefined) attributes.repeatCount = 1;
    attributes.sendAtYear = attributes.sendAt.getFullYear();
    attributes.sendAtMonth = attributes.sendAt.getMonth() + 1;
    attributes.sendAtDay = attributes.sendAt.getDate();
    attributes.sendAtWeekDay = attributes.sendAt.getDay();
    attributes.sendAtHour = attributes.sendAt.getHours();
    attributes.sendAtMinute = attributes.sendAt.getMinutes();

    attributes.sendAt = attributes.sendAt.getTime();
    if (attributes.endAt) attributes.endAt = attributes.endAt.getTime();

    if (typeof attributes.repeatEvery === 'number') {
      attributes.repeatType = 'time';
      attributes.repeatTime = attributes.repeatEvery;

      if (attributes.repeatCount) {
        attributes.endAt = parseInt(attributes.sendAt + attributes.repeatEvery * attributes.repeatCount + (attributes.repeatEvery / 2));
      }

    } else if (typeof attributes.repeatEvery === 'string') {
      attributes.repeatType = attributes.repeatEvery;

      if (attributes.repeatCount) {
        switch (attributes.repeatEvery) {
          case 'minute':
            attributes.endAt = attributes.sendAt + 60000 * attributes.repeatCount + 1000 * 30;
            break;
          case 'hour':
            attributes.endAt = attributes.sendAt + 60000 * 60 * attributes.repeatCount + 60000 * 30;
            break;
          case 'halfDay':
            attributes.endAt = attributes.sendAt + 60000 * 60 * 12 * attributes.repeatCount + 60000 * 60 * 6;
            break;
          case 'day':
            attributes.endAt = attributes.sendAt + 60000 * 60 * 24 * attributes.repeatCount + 60000 * 60 * 12;
            break;
          case 'week':
            attributes.endAt = attributes.sendAt + 60000 * 60 * 24 * 7 * attributes.repeatCount + 60000 * 60 * 24 * 3;
            break;
          case 'month':
            attributes.endAt = attributes.sendAt + 60000 * 60 * 24 * 30 * attributes.repeatCount + 60000 * 60 * 24 * 15;
            break;
          case 'year':
            attributes.endAt = attributes.sendAt + 60000 * 60 * 24 * 365 * attributes.repeatCount + 60000 * 60 * 24 * 100;
            break;
        }
      }
    }

    attributes.sendAt = attributes.sendAt.toString();
    if (attributes.endAt) attributes.endAt = attributes.endAt.toString();
  }

  // Stringify the payload
  attributes.payload = JSON.stringify(attributes.payload);

  return attributes;
}

// Decode the notification data from the native module to pass into JS
function decodeNativeNotification(attributes) {
  if (attributes.payload) attributes.payload = JSON.parse(attributes.payload);
  if (attributes.sendAt) attributes.sendAt = new Date(parseInt(attributes.sendAt));
  if (attributes.endAt) attributes.endAt = new Date(parseInt(attributes.endAt));
  return attributes;
}

DeviceEventEmitter.addListener('sysModuleNotificationClick', function(e) {
  var event = {
    action: e.action,
    payload: JSON.parse(e.payload)
  }

  DeviceEventEmitter.emit('sysNotificationClick', event);
});
