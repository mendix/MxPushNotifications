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

```		  <script>
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

## Step 6: Set up project security for your module

## Step 7 - Deploy your app

## Step 8 - Set up access to APNs and GCM

## Step 10 - Build the hybrid mobile application
