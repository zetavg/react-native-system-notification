'use strict';

var NotificationModule = require('react-native').NativeModules.NotificationModule;

var Notification = {
  send: function(subject, message, action, payload = {}) {
    if (!payload.icon) payload.icon = 'ic_launcher';
    if (!payload.id) payload.id = parseInt(Math.random() * 100000);
    if (payload.autoCancel === undefined) payload.autoCancel = true;
    payload.action = action;

    return new Promise(function(resolve, reject) {
      NotificationModule.send(subject, message, payload.id, payload.action, payload.icon, payload.autoCancel, reject, resolve);
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

module.exports = Notification;
