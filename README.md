# Mendix Push Notifications

> Documentation has been moved to [Mendix 6 How-To's](https://world.mendix.com/display/howto6/Push+Notifications)

Push Notifications let your application notify a user of events even when the user is not actively using the application. This is a native capability provided by both Android and iOS devices and made available via Google Cloud Messaging (GCM) and Apple Push Notifications service (APNs). This project is meant to make it easy for Mendix developers who want to include Push Notifications capability into their Mendix hybrid mobile application.

## Step 1 - Import the PushNotifications module from the App Store

Same

## Step 2 - Install module dependencies

Same as [Mendix 6 How-To](https://world.mendix.com/display/public/howto6/Implementation+Guide)

## Step 3 - Include the push notifications snippet in the application's layouts

Same as [Mendix 6 How-To](https://world.mendix.com/display/public/howto6/Implementation+Guide)

## Step 4 - Start connectors from your After Startup microflow

Add Microflow Services to your After Startup for each callback defined in your index.html. These callbacks should be defined like such:

```		  
<script>
firstCallback = function(data) {
    // For test purposes
    // console.log(JSON.stringify(data));
    $.ajax({
        url:"YOUR URL HERE",
        type:"POST",
        data:JSON.stringify(data),
        contentType:"application/json; charset=utf-8",
        dataType:"json"
    });
},

secondCallback = function(data) {
    // For test purposes
    // console.log(JSON.stringify(data));                
    $.ajax({
        url:"YOUR URL HERE",
        type:"POST",
        data:JSON.stringify(data),
        contentType:"application/json; charset=utf-8",
        dataType:"json"
    });               
},

thirdCallback = function(data) {
    // For test purposes
    // console.log(JSON.stringify(data));                
    $.ajax({
        url:"YOUR URL HERE",
        type:"POST",
        data:JSON.stringify(data),
        contentType:"application/json; charset=utf-8",
        dataType:"json"
    });        
}
</script> 
  ```
Make sure you don't change the callback function names, otherwise the module will not work. You can also use XMLHTTPRequests to send data, but I prefer this way. If you are implementing it with the above, you also need to include jquery in your Phonegap zip. I have added jquery.min.js to the /js/ folder (downloaded from [Google](https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js)).

## Step 5 - Set up the administration pages

Same as [Mendix 6 How-To](https://world.mendix.com/display/public/howto6/Implementation+Guide)

## Step 6: Set up project security for your module

Same as [Mendix 6 How-To](https://world.mendix.com/display/public/howto6/Implementation+Guide)

## Step 7 - Deploy your app

Same as [Mendix 6 How-To](https://world.mendix.com/display/public/howto6/Implementation+Guide)

## Step 8 - Set up access to APNs and GCM

Same as [Mendix 6 How-To](https://world.mendix.com/display/public/howto6/Implementation+Guide)

## Step 10 - Build the hybrid mobile application

Add the following lines of code to your config.xml:

```
<preference name="android-build-tool" value="gradle" />
<gap:plugin name="phonegap-plugin-push" source="npm" version="1.6.0">
            <param name="SENDER_ID" value="XXXXXXXXXXXX" />
</gap:plugin>
```

## Extending functionality to make callbacks more useful

The Action Buttons that are shown in your notification menu are outside of Mendix context. Therefore you need to add the callbacks to your index.html (so it's always accessible). To extend the callback functionality you can edit the GCMConnection.java file and send extra data along. An example:

```
payload.put("awesomeness", message.getawesomeness());
//the notId attribute is added so each notification is shown instead of replacing the previous one sent from your application.
payload.put("notId", message.getnotId());

//New bit of code that addes ActionButtons to your notification. The constructor has the following parameters: Title, callback function title, foreground (this defines if clicking the Action Button should open the app or handle things in the background).
ActionButton firstAction = new ActionButton("YEAH!", "firstCallback", false);
ActionButton secondAction = new ActionButton("NO :-(", "secondCallback", false);
ActionButton thirdAction = new ActionButton("Not sure", "thirdCallback", false);

actions.add(firstAction);
actions.add(secondAction);
actions.add(thirdAction);
payload.put("actions", actions);
```

