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
    "pushNotifications/lib/jquery-1.11.2",
    "dojo/text!pushNotifications/widget/template/pushNotifications.html"
], function (declare, _WidgetBase, _TemplatedMixin, dojoLang, _jQuery, widgetTemplate) {
    "use strict";

    var $ = _jQuery.noConflict(true);

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
            logger.debug( ".alertDismissed");
        },
        // mxui.widget._WidgetBase.update is called when context is changed or initialized. Implement to re-render and / or fetch data.
        update: function (obj, callback) {
            logger.debug(".update");
            if (typeof cordova  !== undefined) {
                if (typeof window.plugins.pushNotification !== undefined) {
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
            this._storeMf();
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
            if (settings.length === 1) {
                this._gcmSettings = settings[0];
                this._gcmSenderID = this._gcmSettings.get(this.senderId);
                window.mxPush = this;
                window.mObject = this._contextObj;
                var pushNotification = window.plugins.pushNotification;
                if (device.platform === "android" || device.platform === "Android") {
                    logger.debug("[PUSHNOTIFY] - Android - Registering Android device.");
                    window.mObject.set("DeviceType", "Android");
                    pushNotification.register(
                        this._androidSuccessHandler,
                        this._androidErrorHandler,
                        {
                            "senderID": this._gcmSenderID,
                            "ecb": "mxPush._androidCallBack"
                        }
                    );
                } else if (device.platform === "Win32NT") {
                    // if (typeof console !== "undefined") {
                    logger.debug("[PUSHNOTIFY] - WP8 - Registering Windows device.");
                    logger.debug(JSON.stringify(window.plugins));
                    logger.debug("[PUSHNOTIFY] - WP8 - Device UUID - " + device.uuid);
                    //}
                    window.mObject.set("DeviceType", "Windows");
                    window.mObject.set("WindowsUUID", device.uuid);
                    pushNotification.register(
                        this._wp8ChannelHandler,
                        this._wp8ErrorHandler,
                        {
                            "channelName": "toast",
                            "ecb": "window.mxPush._wp8SuccessHandler",
                            "uccb": "window.mxPush._wp8ChannelHandler",
                            "errcb": "window.mxPush._wp8ErrorHandler"
                        }
                    );
                } else {
                    logger.debug("[PUSHNOTIFY] - iOS - Registering iOS device.");
                    window.mObject.set("DeviceType", "iOS");
                    pushNotification.register(
                        this._iosTokenHandler,
                        this._iosErrorHandler,
                        {
                            "badge": "true",
                            "sound": "true",
                            "alert": "true",
                            "ecb": "mxPush._iosCallBack"
                        }
                    );
                }
            } else {
                console.warn("unable to retrieve settings");
            }
        },
        _setupEvents: function () {
            logger.debug("_setupEvents");
        },
        _storeMf: function () {
            logger.debug("._storeMf");
            window.androidID = this._androidId;
        },
        _closeNot: function () {
            logger.debug("._closeNot");
            $(".notification").fadeOut();
        },
        _iosTokenHandler: function (result) {
            logger.debug("._iosTokenHandler");
            // Your iOS push server needs to know the token before it can push to this device
            // here is where you might want to send it the token for later use.
            window.mObject.set("RegistrationID", result);
            mx.data.commit({
                mxobj: window.mObject,
                callback: function () {
                    console.log("[PUSHNOTIFY] - iOS - Object committed");
                },
                error: function (e) {
                    console.error("[PUSHNOTIFY] - iOS - Error occurred attempting to commit: " + e);
                }
            });
        },
        _iosErrorHandler: function () {
            console.warn("[PUSHNOTIFY] - iOS - could not register device");
        },
        // Windows Phone 8
        _wp8ChannelHandler: function (event) {
            logger.debug("[PUSHNOTIFY] ChannelHandler - " + event.uri);
            if (event.uri) {
                window.mObject.set("RegistrationID", event.uri);
                mx.data.commit({
                    mxobj: window.mObject,
                    callback: function () {
                        console.log("[PUSHNOTIFY] - wp8 - Object committed");
                    },
                    error: function (e) {
                        console.error("[PUSHNOTIFY] - wp8 - Error occurred attempting to commit: " + JSON.stringify(e));
                    }
                });
            }
        },
        _wp8SuccessHandler: function (e) {
            logger.debug("[PUSHNOTIFY] - wp8 - Result Notification - " + JSON.stringify(e));
            if (e.type === "toast" && e.jsonContent) {
                window.plugins.pushNotification.showToastNotification(
                    this._wp8ToastSuccessHandler,
                    this._wp8ErrorHandler,
                    {
                        "Title": e.jsonContent["wp:Title"],
                        "Subtitle": e.jsonContent["wp:Content"],
                        "NavigationUri": e.jsonContent["wp:Param"]
                    }
                );
            }
            if (e.type === "raw" && e.jsonContent) {
                alert(e.jsonContent.Body);
            }
        },
        _wp8ToastSuccessHandler: function (event) {
            logger.debug("_wp8ToastSuccessHandler called");
        },
        _wp8ErrorHandler: function (event) {
            logger.error("[PUSHNOTIFY] - wp8 - Error - " + JSON.stringify(event));
        },
        // Android
        _androidCallBack: function (e) {
            switch (e.event) {
            case "registered":
                if (e.regid.length > 0) {
                    window.mObject.set("RegistrationID", e.regid);
                    mx.data.commit({
                        mxobj: window.mObject,
                        callback: function () {
                            logger.debug("[PUSHNOTIFY] Object committed");
                        },
                        error: function (e) {
                            console.error("[PUSHNOTIFY] Error occurred attempting to commit: " + e);
                        }
                    });
                }
                break;

            case "message":
                // if this flag is set, this notification happened while we were in the foreground.
                // you might want to play a sound to get the user"s attention, throw up a dialog, etc.
                if (e.foreground) {
                    // on Android soundname is outside the payload.
                    // On Amazon FireOS all custom attributes are contained within payload
                     // if the notification contains a soundname, play it.
                    var soundfile = e.soundname || e.payload.sound,
                        my_media = new Media("/android_asset/www/" + soundfile);
                    my_media.play();
                }
                $(".notification p").text(e.payload.message);
                $(".notification").fadeIn();
                break;

            case "error":
                $(".notification p").text(e.msg);
                $(".notification").fadeIn();
                break;

            default:
                logger.debug("An unknown GCM event has occurred");
                break;
            }
        },
        _androidSuccessHandler: function (result) {
            logger.debug("[PUSHNOTIFY] - Android - Result - ");
            logger.debug(result);
        },
        _androidErrorHandler: function () {
            console.error("[PUSHNOTIFY] - Android - could not register device");
        },
        // iOS
        _iosCallBack: function (event) {
            /*ios event*/
            logger.debug("._iosCallBack", event);
            if (event.alert) {
                $(".notification p").text(event.alert);
                $(".notification").fadeIn();
            }

            if (event.sound) {
                var snd = new Media("not.caf");
                snd.play();
            }

            if (event.badge) {
                window.plugins.pushNotification.setApplicationIconBadgeNumber(this._iosSuccessHandler, this._iosErrorHandler, event.badge);
            }
        },
        _iosSuccessHandler: function (result) {
            logger.debug("[PUSHNOTIFY] - iOS - Result - ");
            logger.debug(result);
        }
    });
});

require(["pushNotifications/widget/pushNotifications"]);