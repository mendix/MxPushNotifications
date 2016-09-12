/*jslint browser: true, devel:true, nomen:true, unparam:true, regexp: true*/
/*global logger, cordova, mx, mxui, device, define, Media, require*/
/*
 pushNotifications
 ========================

 @file      : pushNotifications.js
 @version   : 2.1.0
 @author    : Simon Black
 @date      : Thu, 30 Jun 2016 10:59 CEST
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
    "dojo/Deferred",
    "dojo/promise/all",
    "dojo/_base/lang",
    "dojo/json",
    "dojo/text!pushNotifications/widget/template/pushNotifications.html"
], function (declare, _WidgetBase, _TemplatedMixin, Deferred, all, dojoLang, JSON, widgetTemplate) {
    "use strict";

    // Declare widget"s prototype.
    return declare("pushNotifications.widget.pushNotifications", [_WidgetBase, _TemplatedMixin], {
        // _TemplatedMixin will create our dom node using this HTML template.
        templateString: widgetTemplate,
        // Constants (needed to work around the fact that you cannot use entity paths in offline mode)
        DEVICE_REGISTRATION_ENTITY: "PushNotifications.DeviceRegistration",
        DEVICE_ID_ATTRIBUTE: "PushNotifications.DeviceRegistration.DeviceID",
        REGISTRATION_ID_ATTRIBUTE: "PushNotifications.DeviceRegistration.RegistrationID",
        DEVICE_TYPE_ATTRIBUTE: "PushNotifications.DeviceRegistration.DeviceType",
        GCM_SETTINGS_ENTITY: "PushNotifications.GCMSettings",
        SENDER_ID_ATTRIBUTE: "PushNotifications.GCMSettings.SenderId",
        // Internal variables. Non-primitives created in the prototype are shared between all widget instances.
        _handle: null,
        _gcmSenderId: null,
        _deviceId: null,
        _registrationId: null,

        // dojo.declare.constructor is called to construct the widget instance. Implement to initialize non-primitive properties.
        constructor: function() {
            window.logger.level(window.logger.ALL);
        },

        // dijit._WidgetBase.postCreate is called after constructing the widget. Implement to do extra setup work.
        postCreate: function() {
            logger.debug(".postCreate");

            this.domNode.innerHTML = this.templateString;
        },

        update: function(obj, callback) {
            if (typeof cordova !== "undefined") {
                if (typeof window.PushNotification !== "undefined") {
                    var networkState = navigator.connection.type;
 
                    if (networkState !== Connection.NONE && networkState !== Connection.UNKNOWN) {
                        this.initializePushNotifications();
                    } else {
                        document.addEventListener("online", dojoLang.hitch(this, this.initializePushNotifications), false);
                    }                   
                } else {
                    logger.warning("PushNotifications plugin not available; this plugin should be included during the build.");
                }
            }

            mendix.lang.nullExec(callback);
        },

        initializePushNotifications: function() {
            all({gcm: this.initGCMSettings()})
                        .then(dojoLang.hitch(this, this.initializePushPlugin))
                        .otherwise(function (err) {
                            logger.error(err);
                        });

            document.removeEventListener("online");
        },

        initGCMSettings: function() {
            logger.debug(".initGCMSettings");

            var deferred = new Deferred();

            logger.info(this.GCM_SETTINGS_ENTITY);

            mx.data.getSlice(
                this.GCM_SETTINGS_ENTITY,
                null,                   // No constraints
                {
                    limit: 0,            // Filter
                    offset: 0,
                    sort: []
                },
                function(settings, count) {
                    logger.info(settings);
                    logger.info(count);

                    if (settings.length > 0) {
                        logger.debug("Found a GCM settings object.");

                        logger.info(settings);

                        deferred.resolve(settings[0]);
                    } else {
                        deferred.reject("Could not find a GCM settings object.")
                    }
                },
                function (err) {
                    deferred.reject("Could not retrieve a GCM settings object: " + err.message);
                }
            );

            return deferred.promise;
        },

        initializePushPlugin: function(allSettings) {
            logger.debug(".initializePushPlugin");

            var deferred = new Deferred();

            window.pushWidget = this;

            logger.info(allSettings);

            if (allSettings["gcm"]) {
                var gcm = allSettings.gcm;

                this._gcmSenderId = gcm.get(this.SENDER_ID_ATTRIBUTE);

                var push = PushNotification.init({
                    "android": {
                        "senderID": this._gcmSenderId
                    },
                    "ios": {
                        "alert": "true",
                        "badge": "true",
                        "sound": "true"
                    },
                    "windows": {}
                });

                push.on('registration', dojoLang.hitch(this, this.onPushRegistration));
                push.on('notification', dojoLang.hitch(this.onPushNotification));
                push.on('error', dojoLang.hitch(this.onPushError));

                deferred.resolve(push);
            } else {
                deferred.reject("Could not initialize the PushNotifications plugin.")
            }

            return deferred.promise;
        },

        initializeDeviceRegistration: function () {
            logger.debug(".initializeDeviceRegistration");

            var deferred = new Deferred();

            mx.data.create({
                entity: this.DEVICE_REGISTRATION_ENTITY,
                callback: dojoLang.hitch(this, function(deviceRegistration) {
                    deferred.resolve(deviceRegistration);
                }),
                error: function(e) {
                    deferred.reject("Failed to initialize device registration: " + e);
                }
            });

            return deferred.promise;
        },

        registerDevice: function (deviceRegistration) {
            logger.debug(".registerDevice");
            
            var platform = window.device.platform;

            deviceRegistration.set(this.DEVICE_ID_ATTRIBUTE, this._deviceId);
            deviceRegistration.set(this.REGISTRATION_ID_ATTRIBUTE, this._registrationId);

            if (platform === "Android") {
                deviceRegistration.set("DeviceType", "Android");
            } else if (platform === "iOS") {
                deviceRegistration.set("DeviceType", "iOS");
            } else if (platform === "Windows 8") {
                deviceRegistration.set("DeviceType", "Windows");
            }

            mx.data.commit({
                mxobj: deviceRegistration,
                callback: dojoLang.hitch(this, function () {
                    logger.debug("Register device with ID " + deviceRegistration.get(this.REGISTRATION_ID_ATTRIBUTE));
                }),
                error: dojoLang.hitch(this, function (e) {
                    logger.error("Error occurred attempting to register device: " + e);
                })
            });
        },

        onPushRegistration: function (data) {
            logger.debug(".onPushRegistration");

            this._deviceId = window.device.uuid;
            this._registrationId = data.registrationId;

            this.initializeDeviceRegistration()
                .then(dojoLang.hitch(this, this.registerDevice))
                .otherwise(function (err) {
                    logger.error("Failed to register device: " + err);
                })
        },

        onPushNotification: function (data) {
            logger.debug(".onPushNotification");

            var cards = document.getElementById("cards");
            var card = '' +
                '<div class="alert alert-info alert-dismissible animated fadeInDown" role="alert">' +
                '<button type="button" class="close" data-dismiss="alert" aria-label="Close" onClick="window.pushWidget.removeAlert(this);">' +
                '<span aria-hidden="true">&times;</span>' +
                '</button>' +
                data.message +
                '</div>';

            var cardList = cards.childNodes;
            for(var i = 0; i < cardList.length; i++){
                cardList[i].className = "alert alert-info alert-dismissible";
            }
            cards.innerHTML += card;

            push.finish(function () {
                logger.debug('Successfully process push notification.');
            });
        },

        onPushError: function (e) {
            logger.error("Push error: " + e);
        },

        removeAlert: function (e){
            e.parentNode.parentNode.removeChild(e.parentNode);
        }
    });
});

require(["pushNotifications/widget/pushNotifications"]);
