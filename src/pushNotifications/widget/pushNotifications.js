/*jslint white:true, nomen: true, plusplus: true */
/*global mx, define, require, browser, devel, console */
/*mendix */
/*
    pushNotifications
    ========================

    @file      : pushNotifications.js
    @version   : 1.0
    @author    : Simon Black
    @date      : Tue, 17 Feb 2015 12:53:46 GMT
    @copyright : 
    @license   : 

    Documentation
    ========================
    Describe your widget here.
*/

// Required module list. Remove unnecessary modules, you can always get them back from the boilerplate.
require({
    packages: []
}, [
    'dojo/_base/declare', 'mxui/widget/_WidgetBase', 'dijit/_TemplatedMixin',
    'mxui/dom', 'dojo/dom', 'dojo/query', 'dojo/dom-prop', 'dojo/dom-geometry', 'dojo/dom-class', 'dojo/dom-style', 'dojo/dom-construct', 'dojo/_base/array', 'dojo/_base/lang', 'dojo/text'
], function (declare, _WidgetBase, dom, dojoDom, domQuery, domProp, domGeom, domClass, domStyle, domConstruct, dojoArray, lang, text) {
    'use strict';
    
    // Declare widget's prototype.
    return declare('pushNotifications.widget.pushNotifications', [_WidgetBase], {
        // _TemplatedMixin will create our dom node using this HTML template.
        // Parameters configured in the Modeler.
        androidId: "",
        settingsEntity: "",
        settingsXpathConstraint: "",
        senderId : "",

        // Internal variables. Non-primitives created in the prototype are shared between all widget instances.
        _handle: null,
        _contextObj: null,
        _objProperty: null,
        _pushNotification: null,
        _gcmSettings: null,
        _gcmSenderID: null,

        // dojo.declare.constructor is called to construct the widget instance. Implement to initialize non-primitive properties.
        constructor: function () {
            this._objProperty = {};
        },

        // dijit._WidgetBase.postCreate is called after constructing the widget. Implement to do extra setup work.
        postCreate: function () {
        },
        alertDismissed : function(){
        },

        // mxui.widget._WidgetBase.update is called when context is changed or initialized. Implement to re-render and / or fetch data.
    update : function (obj, callback) {
        
        if(typeof obj === 'string'){
            this._contextGuid = obj;
            mx.data.get({
                guids    : [obj],
                callback : lang.hitch(this, function (objArr) {
                    if (objArr.length === 1){
                        this._loadData(objArr[0]);
                    }
                    else{
                        console.log('Could not find the object corresponding to the received object ID.');
                    }
                })
            });
        } else if(obj === null){
            // Sorry no data no show!
            console.log('Could not find the object corresponding to the received object ID.');
        } else {
            // Load data
            this._loadData(obj);
        }

        if(typeof callback != 'undefined') {
            callback();
        }
    },

    // Loading data
    _loadData : function(obj){
        
        if(cordova !== undefined){
            if(window.plugins.pushNotification !== undefined){
                    this._contextObj = obj;
                    this._initGCMSettings();
                    this._storeMf();
            }
            else{
                    console.log('window.plugins.pushNotification is undefined');
            }
                
        }
        
    },
    _initGCMSettings: function(){
        var xpathString = '//' + this.settingsEntity + this.settingsXpathConstraint;
        
        mx.data.get({
                xpath    : xpathString,
                callback : lang.hitch(this, this._registerDevice)
            });
        
    },
        
    _registerDevice: function(settings) {
        if(settings.length===1){
            this._gcmSettings = settings[0];
            this._gcmSenderID = this._gcmSettings.getAttribute(this.senderId);
            window.mxPush = this;
            window.mObject = this._contextObj;
            var pushNotification = window.plugins.pushNotification;
            if ( device.platform == 'android' || device.platform == 'Android') {
                console.log('[PUSHNOTIFY] - Android - Registering Android device.');
                window.mObject.set('DeviceType', 'Android');        
                pushNotification.register(
                    this._androidSuccessHandler,
                    this._androidErrorHandler,
                    {
                        "senderID": this._gcmSenderID,
                        "ecb": "mxPush._androidCallBack"
                    }
                );
            } 
            else if (device.platform === 'Win32NT') {
                if(typeof console !== 'undefined') {
                    console.log('[PUSHNOTIFY] - WP8 - Registering Windows device.');
                    console.log(JSON.stringify(window.plugins));
                    console.log('[PUSHNOTIFY] - WP8 - Device UUID - ' + device.uuid);
                }
                window.mObject.set('DeviceType', 'Windows');
                window.mObject.set('WindowsUUID', device.uuid);

                pushNotification.register(
                    this._wp8ChannelHandler,
                    this._wp8ErrorHandler,
                    {
                        "channelName": 'toast',
                        "ecb": "window.mxPush._wp8SuccessHandler",
                        "uccb": "window.mxPush._wp8ChannelHandler",
                        "errcb": "window.mxPush._wp8ErrorHandler"
                    });
            } 
            else {
                 
                console.log('[PUSHNOTIFY] - iOS - Registering iOS device.');
                window.mObject.set('DeviceType', 'iOS');
                pushNotification.register(
                        this._iosTokenHandler,
                        this._iosErrorHandler,
                        {
                            "badge":"true",
                            "sound":"true",
                            "alert":"true",
                            "ecb":"mxPush._iosCallBack"
                        }
                    );
                }
            
       
        }
        else{
            console.log('unable to retrieve settings');
        }

        },

        _setupEvents: function () {
            console.log('_setupEvents');
            
        },
            
        _storeMf : function() {
            window.androidID = this._androidId;
        }, 
            
        _closeNot  : function (){
            $('.notification').fadeOut();
        },
            
        _iosTokenHandler : function (result) {
            // Your iOS push server needs to know the token before it can push to this device
            // here is where you might want to send it the token for later use.
            console.log('device token = ' + result);
            window.mObject.set('RegistrationID', result);
            mx.data.commit({
                mxobj    : window.mObject,
                callback : function() {
                    console.log('[PUSHNOTIFY] - iOS - Object committed');
                },
                error : function(e) {
                    console.log('[PUSHNOTIFY] - iOS - Error occurred attempting to commit: ' + e);
                }
            });
        },
        
        _iosErrorHandler : function (){
            console.log('[PUSHNOTIFY] - iOS - could not register device');
        },
            
        // Windows Phone 8
        _wp8ChannelHandler : function (event) {
            console.log('[PUSHNOTIFY] ChannelHandler - ' + event.uri);
            if (event.uri) {
                window.mObject.set('RegistrationID', event.uri);
                mx.data.commit({
                    mxobj    : window.mObject,
                    callback : function() {
                            console.log('[PUSHNOTIFY] - wp8 - Object committed');
                    },
                    error : function(e) {
                            console.log('[PUSHNOTIFY] - wp8 - Error occurred attempting to commit: ' + JSON.stringify(e));
                    }
                });
            }
        },

        _wp8SuccessHandler : function (e) {
            console.log('[PUSHNOTIFY] - wp8 - Result Notification - ' + JSON.stringify(e));
            if (e.type === "toast" && e.jsonContent) {
                window.plugins.pushNotification.showToastNotification(this._wp8ToastSuccessHandler, this._wp8ErrorHandler,
                    {
                        "Title": e.jsonContent["wp:Title"], "Subtitle": e.jsonContent["wp:Content"], "NavigationUri": e.jsonContent["wp:Param"]
                    });
            }

            if (e.type === "raw" && e.jsonContent) {
                alert(e.jsonContent.Body);
            }

        },

        _wp8ToastSuccessHandler : function(event) {
            console.log("_wp8ToastSuccessHandler called");
        },

        _wp8ErrorHandler : function(event) {
            console.log('[PUSHNOTIFY] - wp8 - Error - ' + JSON.stringify(event));
        },

        // Android
        _androidCallBack : function (event){

        if( event.message){
            $('.notification p').text(event.message);
            $('.notification').fadeIn();
        }
        if ( event.alert ){
            $('.notification p').text(event.message);
            $('.notification').fadeIn();
            navigator.notification.alert(event.alert);
        }

        if ( event.sound ){
            var snd = new Media(event.sound);
            snd.play();
        }

        if ( event.badge ){
            window.plugins.pushNotification.setApplicationIconBadgeNumber(this.androidSuccessHandler, this.androidErrorHandler, event.badge);
        }
    },

    _androidSuccessHandler : function (result){
            if ( result.regid){
                window.mObject.set('RegistrationID', result.regid);
                mx.data.commit({
                    mxobj    : window.mObject,
                    callback : function() {
                    console.log('[PUSHNOTIFY] Object committed');
                },
                error : function(e) {
                    console.log('[PUSHNOTIFY] Error occurred attempting to commit: ' + result);
                }
            });
        }
        console.log('[PUSHNOTIFY] - Android - Result - ');
        console.log(result);
    },

    _androidErrorHandler : function (){
        console.log('[PUSHNOTIFY] - Android - could not register device');
    },

    // iOS
    _iosCallBack : function (event){
        /*ios event*/
        console.log(event);
        if ( event.alert ){
            $('.notification p').text(event.alert);
            $('.notification').fadeIn();
        }

        if ( event.sound ){
            var snd = new Media('not.caf');
            snd.play();
        }

        if ( event.badge ){
            window.plugins.pushNotification.setApplicationIconBadgeNumber(this._iosSuccessHandler, this._iosErrorHandler, event.badge);
        }
    },

    _iosSuccessHandler : function (result){
        console.log('[PUSHNOTIFY] - iOS - Result - ');
        console.log(result);
    }
            
    
    
    });
});