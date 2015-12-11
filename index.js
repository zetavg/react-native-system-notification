'use strict';

var React = require('react-native');
var { DeviceEventEmitter } = React;
var EventEmitter = require('EventEmitter');

var NotificationModule = require('react-native').NativeModules.NotificationModule;

var Notification = {
  send: function(subject, message, action = 'DEFAULT', payload = {}) {
    if (!payload.icon) payload.icon = 'ic_launcher';
    if (!payload.id) payload.id = parseInt(Math.random() * 100000);
    if (payload.autoCancel === undefined) payload.autoCancel = true;
    if (payload.delay === undefined) payload.delay = 0;
    payload.action = action;

    return new Promise(function(resolve, reject) {
      NotificationModule.send(subject, message, payload.id, payload.action, payload.icon, payload.autoCancel, JSON.stringify(payload), payload.delay, reject, resolve);
    });
  },

  cancel: function(id) {
    return new Promise(function(resolve, reject) {
      NotificationModule.cancel(id, reject, resolve);
    });
  },

  cancelAll: function() {
    return new Promise(function(resolve, reject) {
      NotificationModule.cancelAll(reject, resolve);
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
