/*jslint browser: true, devel:true, nomen:true, unparam:true, regexp: true*/
/*global cordova, mx, mxui, device, define, Media, require*/

define([
    "dojo/_base/declare",
    "mxui/widget/_WidgetBase",
    "dijit/_TemplatedMixin",
    "dojo/Deferred",
    "dojo/promise/all",
    "dojo/_base/lang",
    "dojo/json",
    "dojo/text!pushNotifications/widget/template/pushNotifications.html"
], function(declare, _WidgetBase, _TemplatedMixin, Deferred, all, dojoLang, JSON, widgetTemplate) {
    "use strict";

    return declare("pushNotifications.widget.pushNotifications", [_WidgetBase, _TemplatedMixin], {
        // _TemplatedMixin will create our dom node using this HTML template.
        templateString: widgetTemplate,
        notificationActions: [{ actionName: "", actionType: "", contextEntity: "", page: "", microflow:""}],

        // Constants (needed to work around the fact that you cannot use entity paths in offline mode)
        DEVICE_REGISTRATION_ENTITY: "PushNotifications.DeviceRegistration",
        DEVICE_ID_ATTRIBUTE: "DeviceID",
        REGISTRATION_ID_ATTRIBUTE: "RegistrationID",
        DEVICE_TYPE_ATTRIBUTE: "DeviceType",
        SENDER_ID_ATTRIBUTE: "SenderId",
        IS_HYBRID_ATTRIBUTE: "IsHybrid",

        // Internal variables. Non-primitives created in the prototype are shared between all widget instances.
        INITIALIZATION_INTERVAL_MS: 10000,

        _handle: null,
        _deviceId: null,
        _registrationId: null,
        _platform: null,
        _initIntervalHandle: null,
        _push: null,

        version: "",
        progressId: null,

        // dijit._WidgetBase.postCreate is called after constructing the widget. Implement to do extra setup work.
        postCreate: function() {
            mx.logger.debug(".postCreate");

            this.version = this._parseVersionString(mx.version);

            this.domNode.innerHTML = this.templateString;
        },

        update: function(obj, callback) {
            mx.logger.debug(".update");

            if (typeof cordova !== "undefined" && typeof window.PushNotification !== "undefined" && !this._registrationId) {
                this.initializePushNotifications();
            } else {
                mx.logger.debug("PushNotifications plugin not available; this plugin should be included during the build.");
            }

            if (callback) callback();
        },

        removeRetryInterval: function() {
            // We've registered our device Successfully. We can remove the retry interval, if it's set.
            if (typeof this._initIntervalHandle === "number") {
                window.clearInterval(this._initIntervalHandle);
                this._initIntervalHandle = null;
            }
        },

        initializePushNotifications: function() {
            mx.logger.debug(".initializePushNotifications");

            try {
                this.initializePushPlugin();
                this.removeRetryInterval();
            } catch (err) {
                // We were not able to register our device. Let's set up an interval that keeps trying.
                if (typeof this._initIntervalHandle !== "number") {
                    this._initIntervalHandle = window.setInterval(
                        dojoLang.hitch(this, this.initializePushNotifications),
                        this.INITIALIZATION_INTERVAL_MS
                    );
                }
                mx.logger.error(err);
            }
        },

        initializePushPlugin: function() {
            mx.logger.debug(".initializePushPlugin");

            window.pushWidget = this;

            this._push = PushNotification.init({
                "android": {},
                "ios": {
                    "alert": "true",
                    "badge": "true",
                    "sound": "true"
                }
            });

            this._push.on('registration', dojoLang.hitch(this, this.onPushRegistration));
            this._push.on('notification', dojoLang.hitch(this, this.onPushNotification));
            this._push.on('error', dojoLang.hitch(this, this.onPushError));
        },

        onPushRegistration: function(data) {
            mx.logger.debug(".onPushRegistration");

            this._deviceId = window.device.uuid;
            this._registrationId = data.registrationId;
            this._platform = window.device.platform;

            this.getDeviceRegistrationEntity()
                .otherwise(dojoLang.hitch(this, this.createRegistrationEntity))
                .then(dojoLang.hitch(this, this.registerDevice))
                .otherwise(function(err) {
                    mx.logger.error("Failed to register device: " + err);
                });
        },

        getDeviceRegistrationEntity: function() {
            mx.logger.debug(".getDeviceRegistrationEntity");

            var deferred = new Deferred();

            var handleRegistrationEntity = function(deviceregistrations) {
                if (deviceregistrations.length > 0) {
                    mx.logger.debug("Found one or more device registration objects. Using the first one.");

                    deferred.resolve(deviceregistrations[0]);
                } else {
                    deferred.reject("Could not find a device registration object.");
                }
            };

            var getRegistrationEntityOfflineFn = dojoLang.hitch(this, this.getRegistrationEntityOffline,
                handleRegistrationEntity,
                function(e) {
                    deferred.reject("Failed to get deviceRegistration objects: " + e);
                }
            );

            var getRegistrationEntityOnlineFn = function() {
                window.setTimeout(function() {
                    deferred.reject("Cannot retrieve local deviceRegistration object when in online mode.");
                }, 0);
            };

            // In offline mode, it's possible that there is still a DeviceRegistration object in our local database.
            // This happens when a device registration took place, but a consecutive 'sync' failed.
            // We'll to re-use any existing DeviceRegistration with our Registration ID.
            //
            // mx.data.getSlice is only available in the offline (client-side) backend.
            // If it fails, we assume that we are in online mode, and just go ahead and create a DeviceRegistration object.
            this._executeOfflineOnline(getRegistrationEntityOfflineFn, getRegistrationEntityOnlineFn);

            return deferred.promise;
        },

        getRegistrationEntityOffline: function(success, error) {
            this._getSliceCompat(this.DEVICE_REGISTRATION_ENTITY, [{
                    attribute: this.REGISTRATION_ID_ATTRIBUTE,
                    operator: "equals",
                    value: this._registrationId
                }], {
                    offset: 0,
                    limit: 0,
                    sort: []
                },
                success,
                error
            );
        },

        createRegistrationEntity: function() {
            mx.logger.debug(".createRegistrationEntity");

            var deferred = new Deferred();

            // Nothing there. We'll create a new DeviceRegistration object.
            mx.data.create({
                entity: this.DEVICE_REGISTRATION_ENTITY,
                callback: function(deviceRegistration) {
                    deferred.resolve(deviceRegistration);
                },
                error: function(e) {
                    deferred.reject("Failed to create device registration: " + e);
                }
            });

            return deferred.promise;
        },

        registerDevice: function(deviceRegistration) {
            mx.logger.debug(".registerDevice");

            deviceRegistration.set(this.DEVICE_ID_ATTRIBUTE, this._deviceId);
            deviceRegistration.set(this.REGISTRATION_ID_ATTRIBUTE, this._registrationId);
            deviceRegistration.set(this.IS_HYBRID_ATTRIBUTE, true);

            if (this._platform === "Android") {
                deviceRegistration.set("DeviceType", "Android");
            } else if (this._platform === "iOS") {
                deviceRegistration.set("DeviceType", "iOS");
            }

            // We commit the DeviceRegistration object to trigger the backend process.
            // This is the only time we can commit the object, because it will be deleted in the AfterCommit event.
            mx.data.commit({
                mxobj: deviceRegistration,
                callback: dojoLang.hitch(this, function() {
                    try {
                        mx.logger.debug("Registered device with ID " + deviceRegistration.get(this.REGISTRATION_ID_ATTRIBUTE));
                    } catch (e) {
                        mx.logger.debug("Registered unknown device");
                    }
                }),
                error: function(e) {
                    mx.logger.error("Error occurred attempting to register device: " + e);
                }
            });
        },

        onPushNotification: function(data) {
            mx.logger.debug(".onPushNotification");

            if (data.additionalData.foreground) {
                var cards = document.getElementById("cards");
                var alertWrapper = ""
                
                // If it is IE11 we cant use backticks
                if (window.document.documentMode) {
                    alertWrapper = '<div class="alert alert-info alert-dismissible animated fadeInDown" role="alert" onClick="window.pushWidget.onClickAlert(' + JSON.stringify(data.additionalData) +', this)">'
                }
                else {
                    alertWrapper = `<div class="alert alert-info alert-dismissible animated fadeInDown" role="alert" onClick='window.pushWidget.onClickAlert(${JSON.stringify(data.additionalData)},this)'>`
                }
                var card = alertWrapper +
                    '<button type="button" class="close" data-dismiss="alert" aria-label="Close" onClick="window.pushWidget.removeAlert(this);">' +
                    '<span aria-hidden="true">&times;</span>' +
                    '</button>' +
                    data.message +
                    '</div>';

                var cardList = cards.childNodes;
                for (var i = 0; i < cardList.length; i++) {
                    cardList[i].className = "alert alert-info alert-dismissible";
                }
                cards.innerHTML += card;
            } else {
                this.onClickAlert(data.additionalData);
            }

            this._push.finish(function() {
                mx.logger.debug('Successfully processed push notification.');
            });
        },

        onPushError: function(e) {
            mx.logger.error("Push error: " + e);
        },

        removeAlert: function(e) {
            e.parentNode.parentNode.removeChild(e.parentNode);
        },

        onClickAlert: function (data, e) {
            var callback = dojoLang.hitch(this, function () {
                if (e) {
                    this.removeAlert(e.childNodes[0]);
                }
            });

            var action = null;
            for (var index = 0; index < this.notificationActions.length; index++) {
                if (this.notificationActions[index].actionName === data.actionName) {
                    action = this.notificationActions[index];
                    break;
                }
            }

            if (!action) {
                callback();
                return;
            }

            var contextEntity = action.contextEntity;
            var actionType = action.actionType;
            var microflow = action.microflow;
            var page = action.page;

            var guid = data.guid;

            var params = {};

            if (actionType === "openPage") {
                params = {
                    callback: callback,
                    error: function (error) {
                        window.mx.ui.error("Error while opening page " + page + ": " + error.message);
                    }
                };

                if (contextEntity) {
                    if (!guid) {
                        callback();
                        return;
                    }

                    mx.data.get({guid: guid, callback: dojoLang.hitch(this, function(obj) {
                        if (obj) {
                            var context = new mendix.lib.MxContext(contextEntity, guid);
                            context.setTrackObject(obj);

                            params.context = context;

                            window.mx.ui.openForm(page, params);
                        } else {
                            if (this._isOffline) {
                                var actionCallback = this.onClickAlert.bind(this, data, e);

                                mx.ui.confirmation({
                                    content: "Synchronize this application with the server?",
                                    proceed: "Yes",
                                    cancel: "No",
                                    handler: this.offlineSync.bind(this, actionCallback)
                                });
                            }
                        }
                    })});
                } else {
                    window.mx.ui.openForm(page, params);
                }

            } else if (actionType === "callMicroflow") {
                if (contextEntity && guid) {
                    params = {
                        applyto: "selection",
                        guids: [guid],
                        mxform: this.mxform
                    };
                }

                window.mx.ui.action(microflow, {
                    callback: callback,
                    error: function (error) {
                        window.mx.ui.error("Error while calling microflow " + microflow + ": " + error.message);
                    },
                    params: params
                });
            } else {
                callback();
            }
        },

        offlineSync: function (callback) {
            var progressId = window.mx.ui.showProgress(null, true);
            var onSyncSuccess = function () {
                if (progressId) {
                    window.mx.ui.hideProgress(progressId);
                }
                if (callback) {
                    callback();
                }
            };
            var onSyncFailure = function () {
                window.mx.ui.hideProgress(progressId);

                window.mx.ui.info(window.mx.ui.translate("mxui.sys.UI", "sync_error"), true);
            };

            if (window.mx.data.synchronizeOffline) {
                window.mx.data.synchronizeOffline({fast: false}, onSyncSuccess, onSyncFailure);
            } else if (window.mx.data.synchronizeDataWithFiles) {
                window.mx.data.synchronizeDataWithFiles(onSyncSuccess, onSyncFailure);
            }
        },

        _executeOfflineOnline: function(offlineFn, onlineFn) {
            if ((mx.isOffline && mx.isOffline()) || !!mx.session.getConfig("sync_config")) {
                offlineFn();
            } else {
                onlineFn();
            }
        },

        _isOffline: function() {
            return (mx.isOffline && mx.isOffline()) || !!mx.session.getConfig("sync_config");
        },

        _getSliceCompat: function(entity, constraints, filter, success, error) {
            if (this.version.major > 7 || (this.version.major === 7 && this.version.minor >= 3)) {
                mx.data.getSlice(entity, constraints, filter, true, success, error); // caching, introduced in 7.3
            } else {
                mx.data.getSlice(entity, constraints, filter, success, error);
            }
        },

        _parseVersionString: function(str) {
            if (typeof(str) !== 'string') {
                return false;
            }
            var x = str.split('.');
            // parse from string or default to 0 if can't parse
            var maj = parseInt(x[0]) || 0;
            var min = parseInt(x[1]) || 0;
            var pat = parseInt(x[2]) || 0;
            return {
                major: maj,
                minor: min,
                patch: pat
            };
        }
    });
});

require(["pushNotifications/widget/pushNotifications"]);
