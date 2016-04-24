/*jslint browser: true, devel:true, nomen:true, unparam:true, regexp: true*/
/*global logger, cordova, mx, mxui, device, define, Media, require*/
/*
 pushNotifications
 ========================
 
 @file      : pushNotifications.js
 @version   : 2.0.1
 @author    : Simon Black
 @date      : Tue, 21 Apr 2016 15:27:32 GMT
 @copyright : 
 @license   : 
 
 Documentation
 ========================
 Describe your widget here.
 */

// Required module list. Remove unnecessary modules, you can always get them back from the boilerplate.
define([
    "dojo/_base/declare",
    "mxui/widget/_WidgetBase",
    "dijit/_TemplatedMixin",
    "dojo/_base/lang",
    "dojo/text!pushNotifications/widget/template/pushNotifications.html"
], function (declare, _WidgetBase, _TemplatedMixin, dojoLang, widgetTemplate) {
    "use strict";

    // Declare widget"s prototype.
    return declare("pushNotifications.widget.pushNotifications", [_WidgetBase, _TemplatedMixin], {
        // _TemplatedMixin will create our dom node using this HTML template.
        templateString: widgetTemplate,
        // Parameters configured in the Modeler.
        settingsEntity: "",
        settingsXpathConstraint: "",
        senderId: "",
        // Internal variables. Non-primitives created in the prototype are shared between all widget instances.
        _handle: null,
        _contextObj: null,
        _objProperty: null,
        _pushNotification: null,
        _gcmSettings: null,
        _gcmSenderID: null,
        // dojo.declare.constructor is called to construct the widget instance. Implement to initialize non-primitive properties.
        constructor: function () {
            window.logger.level(window.logger.ALL);
            this._objProperty = {};
        },
        // dijit._WidgetBase.postCreate is called after constructing the widget. Implement to do extra setup work.
        postCreate: function () {
            logger.debug(".postCreate");
            this.domNode.innerHTML = this.templateString;
        },
        alertDismissed: function () {
            logger.debug(".alertDismissed");
        },
        // mxui.widget._WidgetBase.update is called when context is changed or initialized. Implement to re-render and / or fetch data.
        update: function (obj, callback) {
            logger.debug(".update");
            if (typeof cordova != 'undefined') {
                if (typeof window.PushNotification != 'undefined') {
                    if (typeof obj === "string") {
                        this._contextGuid = obj;
                        mx.data.get({
                            guids: [obj],
                            callback: dojoLang.hitch(this, function (objArr) {
                                if (objArr.length === 1) {
                                    this._loadData(objArr[0]);
                                } else {
                                    console.warn("Could not find the object corresponding to the received object ID.");
                                }
                            })
                        });
                    } else if (obj === null) {
                        // Sorry no data no show!
                        console.warn("Could not find the object corresponding to the received object ID.");
                    } else {
                        // Load data
                        this._loadData(obj);
                    }
                } else {
                    console.warn("plugins pushNotification not availble, should be included during the build.");
                }
            }
            if (callback) {
                callback();
            }
        },
        // Loading data
        _loadData: function (obj) {
            logger.debug("._loadData");
            this._contextObj = obj;
            this._initGCMSettings();
        },
        _initGCMSettings: function () {
            logger.debug("._initGCMSettings");
            var xpathString = "//" + this.settingsEntity + this.settingsXpathConstraint;
            mx.data.get({
                xpath: xpathString,
                callback: this._registerDevice
            }, this);
        },
        _registerDevice: function (settings) {
            logger.debug("._registerDevice");
            window.mObject = this._contextObj;
            if (settings.length === 1) {
                this._gcmSettings = settings[0];
                this._gcmSenderID = this._gcmSettings.get(this.senderId);
                var push = PushNotification.init({
                    "android": {
                        "senderID": this._gcmSenderID
                    },
                    "ios": { "alert": "true", "badge": "true", "sound": "true" },
                    "windows": {}
                });

                push.on('registration', function (data) {
                    console.log("registration event");
                    var platform = window.device.platform;
                    if (platform === "Android") {
                        window.mObject.set("DeviceType", "Android");
                    } else if (platform === "iOS") {
                        window.mObject.set("DeviceType", "IOS");
                    } else if (platform === "Windows 8") {
                        window.mObject.set("DeviceType", "Windows");
                    }

                    window.mObject.set("RegistrationID", data.registrationId);
                    mx.data.commit({
                        mxobj: window.mObject,
                        callback: function () {
                            console.log("[PUSHNOTIFY] - " + platform + " - Object committed");
                        },
                        error: function (e) {
                            console.error("[PUSHNOTIFY] - " + platform + " - Error occurred attempting to commit: " + e);
                        }
                    });
                });

                push.on('notification', function (data) {
                    console.log("notification event");
                    var cards = document.getElementById("cards");
                    var card = '<div class="alert alert-info alert-dismissible animated fadeInDown" role="alert">' +
                        '<button type="button" class="close" data-dismiss="alert" onclick="function(btn){var child = btn.parentNode; var parent = btn.parentNode.parentNode; parent.removeChild(child);}" aria-label="Close"><span aria-hidden="true">&times;</span></button>' +
                        data.message +
                        '</div>'
                    cards.innerHTML += card;
                    push.finish(function () {
                        console.log('finish successfully called');
                    });
                });

                push.on('error', function (e) {
                    console.log("push error");
                });
            } else {
                console.warn("unable to retrieve settings");
            }
        }
    });
});

require(["pushNotifications/widget/pushNotifications"]);