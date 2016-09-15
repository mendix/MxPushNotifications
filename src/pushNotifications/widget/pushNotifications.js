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
        DEVICE_ID_ATTRIBUTE: "DeviceID",
        REGISTRATION_ID_ATTRIBUTE: "RegistrationID",
        DEVICE_TYPE_ATTRIBUTE: "DeviceType",
        GCM_SETTINGS_ENTITY: "PushNotifications.GCMSettings",
        SENDER_ID_ATTRIBUTE: "SenderId",
        // Internal variables. Non-primitives created in the prototype are shared between all widget instances.
        INITIALIZATION_INTERVAL_MS: 10000,
        _handle: null,
        _gcmSenderId: null,
        _deviceId: null,
        _registrationId: null,
        _platform: null,
        _initIntervalHandle: null,
        _push: null,

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
            logger.debug(".update");

            if (typeof cordova !== "undefined" && typeof window.PushNotification !== "undefined") {
                if (!this._registrationId) {
                    this.initializePushNotifications(); 
                }
            } else {
                logger.debug("PushNotifications plugin not available; this plugin should be included during the build.");
            }

            mendix.lang.nullExec(callback);
        },

        initializePushNotifications: function() {
            logger.debug(".initializePushNotifications");

            all({
                gcm: this.initGCMSettings()
            })
            .then(dojoLang.hitch(this, this.initializePushPlugin))
            .then(dojoLang.hitch(this, function() {
                // We've registered our device Successfully. We can remove the retry interval, if it's set.
                if (typeof this._initIntervalHandle === "number") {
                    window.clearInterval(this._initIntervalHandle);
                    this._initIntervalHandle = null;
                }
            }))
            .otherwise(dojoLang.hitch(this, function (err) {
                logger.error(err);

                // We were not able to register our device. Let's set up an interval that keeps trying.
                if (typeof this._initIntervalHandle !== "number") {
                    this._initIntervalHandle = window.setInterval(dojoLang.hitch(this, this.initializePushNotifications), this.INITIALIZATION_INTERVAL_MS);
                }
            }));
        },

        initGCMSettings: function() {
            logger.debug(".initGCMSettings");

            var deferred = new Deferred();

            var handleGCMSettings = function(settings, count) {
                if (settings.length > 0) {
                    logger.debug("Found one or more GCM settings objects. Using the first one.");

                    deferred.resolve(settings[0]);
                } else {
                    deferred.reject("Could not find a GCM settings object.")
                }
            };

            /*
            mx.data.getSlice is only available in the offline (client-side) backend.
            Unfortunately, we have no way of knowing if we're running in offline mode.
            Let's try to use getSlice first, and fall back to an xpath retrieve if it fails.
            */
            try {
                mx.data.getSlice(this.GCM_SETTINGS_ENTITY,
                    null,            // No constraints
                    {
                        limit: 0,    // Filter
                        offset: 0,
                        sort: []
                    },
                    handleGCMSettings, // Success handler
                    function (err) { // Error handler
                        deferred.reject("Could not retrieve a GCM settings object: " + err.message);
                    }
                );
            } catch (e) {
                mx.data.get({
                    xpath: "//" + this.GCM_SETTINGS_ENTITY,
                    filter: {
                        amount: 1
                    },
                    callback: handleGCMSettings,
                    error: function (err) {
                        deferred.reject("Could not retrieve a GCM settings object: " + err.message);
                    }
                });
            }

            return deferred.promise;
        },

        initializePushPlugin: function(allSettings) {
            logger.debug(".initializePushPlugin");

            var deferred = new Deferred();

            window.pushWidget = this;

            if (allSettings["gcm"]) {
                var gcm = allSettings.gcm;

                this._gcmSenderId = gcm.get(this.SENDER_ID_ATTRIBUTE);

                this._push = PushNotification.init({
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

                this._push.on('registration', dojoLang.hitch(this, this.onPushRegistration));
                this._push.on('notification', dojoLang.hitch(this, this.onPushNotification));
                this._push.on('error', dojoLang.hitch(this, this.onPushError));

                deferred.resolve(this._push);
            } else {
                deferred.reject("Could not initialize the PushNotifications plugin.")
            }

            return deferred.promise;
        },

        onPushRegistration: function (data) {
            logger.debug(".onPushRegistration");

            this._deviceId = window.device.uuid;
            this._registrationId = data.registrationId;
            this._platform = window.device.platform;

            this.initializeDeviceRegistration()
                .then(dojoLang.hitch(this, this.registerDevice))
                .otherwise(function (err) {
                    logger.error("Failed to register device: " + err);
                })
        },

        initializeDeviceRegistration: function () {
            logger.debug(".initializeDeviceRegistration");

            var deferred = new Deferred();

            var createRegistrationEntity = function() {
                // Nothing there. We'll create a new DeviceRegistration object.
                mx.data.create({
                    entity: this.DEVICE_REGISTRATION_ENTITY,
                    callback: dojoLang.hitch(this, function(deviceRegistration) {
                        deferred.resolve(deviceRegistration);
                    }),
                    error: function(e) {
                        deferred.reject("Failed to initialize device registration: " + e);
                    }
                });
            };

            /*
            In offline mode, it's possible that there is still a DeviceRegistration object in our local database.
            This happens when a device registration took place, but a consecutive 'sync' failed.
            We'll to re-use any existing DeviceRegistration with our Registration ID.

            mx.data.getSlice is only available in the offline (client-side) backend.
            If it fails, we assume that we are in online mode, and just go ahead and create a DeviceRegistration object.
            */
            try {
                mx.data.getSlice(this.DEVICE_REGISTRATION_ENTITY,
                    [{
                        attribute: this.REGISTRATION_ID_ATTRIBUTE,
                        operator: "equals",
                        value: this._registrationId
                    }], {
                        offset: 0,
                        limit: 0,
                        sort: []
                    }, dojoLang.hitch(this, function(mxobjs, count) {
                        if (count === 0) {
                            dojoLang.hitch(this, createRegistrationEntity)();
                        } else {
                            // Found something. We'll re-use it.
                            deferred.resolve(mxobjs[0]);
                        }
                    }), dojoLang.hitch(this, function(e) {
                        deferred.reject("Failed to get deviceRegistration objects: " + e);
                    })
                );
            } catch (e) {
                dojoLang.hitch(this, createRegistrationEntity)();
            }
            
            return deferred.promise;
        },

        registerDevice: function (deviceRegistration) {
            logger.debug(".registerDevice");
            
            deviceRegistration.set(this.DEVICE_ID_ATTRIBUTE, this._deviceId);
            deviceRegistration.set(this.REGISTRATION_ID_ATTRIBUTE, this._registrationId);

            if (this._platform === "Android") {
                deviceRegistration.set("DeviceType", "Android");
            } else if (this._platform === "iOS") {
                deviceRegistration.set("DeviceType", "iOS");
            }

            // We commit the DeviceRegistration object to trigger the backend process.
            // This is the only time we can commit the object, because it will be deleted in the AfterCommit event.
            mx.data.commit({
                mxobj: deviceRegistration,
                callback: dojoLang.hitch(this, function () {
                    logger.debug("Registered device with ID " + deviceRegistration.get(this.REGISTRATION_ID_ATTRIBUTE));
                }),
                error: function(e) {
                    logger.error("Error occurred attempting to register device: " + e);
                }
            });
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

            this._push.finish(function () {
                logger.debug('Successfully processed push notification.');
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
