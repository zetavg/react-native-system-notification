'use strict';

class Notification {

  static addEventListener(type, handler) {
    warning(false, 'Cannot listen to Notification events on IOS.');
  }

  static removeEventListener(type, handler) {
    warning(false, 'Cannot remove Notification listener on IOS.');
  }

}

Notification.currentState = null;

module.exports = Notification;
