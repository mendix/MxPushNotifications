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
        // Parameters configured in the Modeler.
        deviceEntity: "",
        registrationIdAttribute: "",
        settingsEntity: "",
        settingsXpathConstraint: "",
        senderIdAttribute: "",
        // Internal variables. Non-primitives created in the prototype are shared between all widget instances.
        _handle: null,
        _gcmSenderId: null,
        _registrationId: null,

        // dojo.declare.constructor is called to construct the widget instance. Implement to initialize non-primitive properties.
        constructor: function() {
            window.logger.level(window.logger.ALL);
        },

        // dijit._WidgetBase.postCreate is called after constructing the widget. Implement to do extra setup work.
        postCreate: function() {
            logger.debug(".postCreate");

            this.domNode.innerHTML = this.templateString;

            if (typeof cordova !== "undefined") {
                if (typeof window.PushNotification !== "undefined") {
                    all({gcm: this.initGCMSettings()})
                        .then(dojoLang.hitch(this, this.registerDevice))
                        .otherwise(function (err) {
                            console.error(err);
                        })
                } else {
                    logger.warning("PushNotifications plugin not available; this plugin should be included during the build.");
                }
            }
        },

        initGCMSettings: function() {
            logger.debug(".initGCMSettings");

            var deferred = new Deferred();

            var xpathString = "//" + this.settingsEntity + this.settingsXpathConstraint;
            mx.data.get({
                xpath: xpathString,
                callback: function(settings) {
                    if (settings.length > 0) {
                        logger.debug("Found a GCM settings object.");

                        deferred.resolve(settings[0]);
                    } else {
                        deferred.reject("Could not find a GCM settings object.")
                    }
                },
                error: function (err) {
                    deferred.reject("Could not retrieve a GCM settings object.");
                }
            }, this);

            return deferred.promise;
        },

        registerDevice: function(allSettings) {
            logger.debug(".registerDevice");

            var deferred = new Deferred();

            window.pushWidget = this;

            if (allSettings["gcm"]) {
                var gcm = allSettings.gcm;

                this._gcmSenderId = gcm.get(this.senderIdAttribute);

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

        retrieveDevice: function (registrationId) {
            logger.debug(".retrieveDevice");

            var xpathString = "//" + this.deviceEntity + "[" + this.registrationIdAttribute + "='" + registrationId + "']"

            var deferred = new Deferred();

            mx.data.get({
                xpath: xpathString,
                filter: {
                    amount: 1
                },
                callback: dojoLang.hitch(this, function(device) {
                    if (device.length > 0) {
                        logger.debug("Retrieved device object with ID " + device[0].get(this.registrationIdAttribute));

                        deferred.resolve(device[0]);
                    } else {
                        mx.data.create({
                            entity: this.deviceEntity,
                            callback: dojoLang.hitch(this, function(obj) {
                                obj.set(this.registrationIdAttribute, registrationId);

                                logger.debug("Created device object created with ID " + obj.get(this.registrationIdAttribute));

                                deferred.resolve(obj);
                            }),
                            error: dojoLang.hitch(this, function(e) {
                                deferred.reject("Failed to create device object: " + e);
                            })
                        });
                    }
                }),
                error: function(e) {
                    deferred.reject("Failed to retrieve device object: " + e);
                }
            });

            return deferred.promise;
        },

        updateDevice: function (device) {
            var platform = window.device.platform;

            if (platform === "Android") {
                device.set("DeviceType", "Android");
            } else if (platform === "iOS") {
                device.set("DeviceType", "iOS");
            } else if (platform === "Windows 8") {
            }

            mx.data.commit({
                mxobj: device,
                callback: dojoLang.hitch(this, function () {
                    console.log("Committed device object with ID " + device.get(this.registrationIdAttribute));
                }),
                error: dojoLang.hitch(this, function (e) {
                    console.error("Error occurred attempting to commit device object: " + e);
                })
            });
        },

        onPushRegistration: function (data) {
            logger.debug(".onPushRegistration");

            this._registrationId = data.registrationId;

            this.retrieveDevice(data.registrationId)
                .then(dojoLang.hitch(this, this.updateDevice))
                .otherwise(function (err) {
                    console.error("Failed to register device: " + err);
                })
        },

        onPushNotification: function (data) {
            console.log("notification event");

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
                console.log('finish successfully called');
            });
        },

        onPushError: function (e) {
            console.error("Push error: " + e);
        },

        removeAlert: function (e){
            e.parentNode.parentNode.removeChild(e.parentNode);
        }
    });
});

require(["pushNotifications/widget/pushNotifications"]);
