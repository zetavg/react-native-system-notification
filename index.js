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
        if (!attributes.smallIcon) attributes.smallIcon = 'ic_launcher';
        if (!attributes.id) attributes.id = parseInt(Math.random() * 100000);
        if (!attributes.action) attributes.action = 'DEFAULT';
        if (!attributes.payload) attributes.payload = {};
        if (attributes.autoCancel === undefined) attributes.autoCancel = true;
        attributes.delayed = (attributes.delay !== undefined);

        // Stringify the payload
        attributes.payload = JSON.stringify(attributes.payload);

        NotificationModule.rCreate(attributes.id, attributes, reject, resolve);
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
        if (notification.payload) notification.payload = JSON.parse(notification.payload);
        resolve(notification);
      });
    });
  },

  delete: function(id) {
    return new Promise(function(resolve, reject) {
      NotificationModule.rDelete(id, reject, resolve);
    });
  },

  deleteAll: function() {
    return new Promise(function(resolve, reject) {
      NotificationModule.rDeleteAll(reject, resolve);
    });
  },

  clear: function(id) {
    return new Promise(function(resolve, reject) {
      NotificationModule.rClear(id, reject, resolve);
    });
  },

  clearAll: function() {
    return new Promise(function(resolve, reject) {
      NotificationModule.rClearAll(reject, resolve);
    });
  },

  module: NotificationModule
}

DeviceEventEmitter.addListener('sysModuleNotificationClick', function(e) {
  var event = {
    action: e.action,
    payload: JSON.parse(e.payload)
  }

  DeviceEventEmitter.emit('sysNotificationClick', event);
});

module.exports = Notification;
