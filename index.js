'use strict';

var React = require('react-native');
var { DeviceEventEmitter } = React;
var EventEmitter = require('EventEmitter');

var NotificationModule = require('react-native').NativeModules.NotificationModule;

var Notification = {
  create: function(attributes = {}) {
    return new Promise(function(resolve, reject) {
      NotificationModule.getApplicationName(function(e) {}, function(applicationName) {

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

        NotificationModule.create(attributes.id, attributes, reject, resolve);
      });
    });
  },

  clear: function(id) {
    return new Promise(function(resolve, reject) {
      NotificationModule.clear(id, reject, resolve);
    });
  },

  clearAll: function() {
    return new Promise(function(resolve, reject) {
      NotificationModule.clearAll(reject, resolve);
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
