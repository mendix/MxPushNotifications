# Mendix Push notifications

This module and widget should be used to implement push notifications for Android, iOS and Windows mobile devices. They have been tested to work on Mendix 5.14.1 and up.

The project contains:

- Directory structure.
- Readme.md file.
- Javascript source.
- XSD for package.xml, to configure properties of the widget, visible inside the Mendix Business Modeler.
- Example project
- Module for sending push notifications
- Custom CSS file to style the push notification html

## Contributing

For more information on contributing to this repository visit [Contributing to a GitHub repository](https://world.mendix.com/display/howto50/Contributing+to+a+GitHub+repository)!

## Description

This project provides all the necessary widgets, Javascript, Java and modules necessary to both send and receive push notifications in a Mendix application. For information on how to build your Mendix apps into PhoneGap applications please refer to this documentation: [Mendix mobile] (https://world.mendix.com/display/refguide5/Mobile)

## Implementation Guide

### Step 1 - Create an mpk of PushNotifications module

We need to extract the module from this project before starting with the implementation. Walk through these following steps:

1. Clone this project or download it as ZIP and extract it.
2. Open the `PushNotifications.mpr` which is located in the `test` directory in the root of the project with a Mendix Modeler.
3. Right-click on the `PushNotifications` module (inside Project Explorer pane), select `Export module package...` and save the mpk file.

<img src="assets/images/implementation guide/Preparation export Push Notification module.JPG"/>

### Step 2 - Install module dependencies

First, open your existing Mendix project (or create a new one). The Push Notification module has two dependencies: `CommunityCommons` and `Encryption` module. Include these two dependencies by downloading it from the AppStore.

> Note: importing Encryption module will trigger errors because it contains reference to a non-existant layout, fix it by assigning a master layout of `Encryption.ResponsiveLayout_Certificate` page to some other layout (in this specific use case it is not really important which layout is used).
> <img src="assets/images/implementation guide/Fix Encryption module.JPG"/>

### Step 3 - Import the PushNotification module

Import the created mpk file from the Step 1 into your Mendix project. To do this, right-click on an empty space on the Project Explorer pane, select `Import module package...`, choose the mpk file, and add it as a new module.

<img src="assets/images/implementation guide/Import Push Notification module.JPG"/>

<img src="assets/images/implementation guide/Import Push Notification module as a new module.JPG"/>

### Step 4 - Update component.json file

Make an update to `theme\components.json`. This file contains (among others) the dependencies of the to-be-created mobile hybrid application. Update `theme\components.json` by adding `"widgets/pushNotifications/lib/PushNotification.js"` as an element of `js` array so it would look like this:

```
{  
   "files":{  
      "css":[  
         "lib/bootstrap/css/bootstrap.min.css",
         "mxclientsystem/mxui/ui/mxui.css",
         "styles/css/lib/lib.css",
         "styles/css/custom/custom.css"
      ],
      "js":[  
         "mxclientsystem/mxui/mxui.js",
         "widgets/pushNotifications/lib/PushNotification.js"
      ]
   },
   "cachebust":"635689412670032000"
}
```

### Step 5 - Update index.html file

Update `theme/index.html`to include the following reference to this javascript library:

```
<script type="text/javascript" src="widgets/pushNotifications/lib/PushNotification.js"></script>
```

<img src="assets/images/implementation guide/Update index html file.JPG"/>

### Step 6 - Include Push Notification Snippet in the layouts

Include push notification snippet on mobile and tablet layouts.

<img src="assets/images/implementation guide/Include push notification snippet on layouts.JPG"/>

### Step 7 - Set up the administration pages

Add `Apple Administration`, `GoogleAdministration`, and `Device_Overview` pages to the project navigation. The `Apple Administration` and `GoogleAdministration` pages are used to configure your application to be able to reach the respective services (APNs and GCM) later on. The `Device_Overview` page is useful for testing purpose.

> Note: don't forget to set the `Project security` -> `User roles` to include `PushNotifications.Administrator` role as part as the main `Administrator` role and `PushNotifications.User` role as part of the main `User` role.
> <img src="assets/images/implementation guide/Project security.JPG"/>

At this moment you can deploy your application to the cloud. If you are using Free App, simply click the `Run` button.

### Step 8 - Set up access to APNs and GCM

Set up access to APNs and GCM and configure them in your application.

<TODO: link to separate documentation>

### Step 9 - Build the hybrid mobile application

You will need to build the hybrid mobile application. Refer to [this documentation] (https://world.mendix.com/display/howto50/Publishing+a+Mendix+Hybrid+Mobile+App+in+Mobile+App+Stores) to get the explanation on how to do it. Note that you should opt to download the app instead of directly publishing it. It is necessary because we need to include a phonegap plugin which is used by this module into the mobile hybrid application.

Once you have the mobile hybrid project file downloaded, extract it and include the required phonegap plugin by adding this line to the `config.xml` file:

```
<gap:plugin name="com.phonegap.plugins.pushplugin" version="2.4.0" />
```

You can proceed by repackaging the project into a zip file and use PhoneGap Build to generate the files for Android and iOS.

## Sending push notifications

In the module there are two operations to send messages. One which these queue the notifications whereas the other will send them immediately. If you are planning to send several push notifications to different devices at once, then it would be most suitable to use the queued method. If you however simply want to test the sending of messages then just use the send immediate microflow.

In the module there is a device overview page. This will list all of the devices that are currently registered to the application. If you want to send a message to one of these devices you can simply select a device and send a message.

To send a message without using the devices page, simply create a microflow that retrieves the device from a user account and create a message object with the attributes filled. Then simply pass this message to one of the microflows in the use me folder that sends either a message or a list of messages.  

## Setting up Apple Push Notification Server
In order to send push notifications for Apple devices from this module you will need to correctly set up and acquire a certificate from Apple, then add this to the Mendix settings pages.

### Step 1 - Login to members center
Login to the [members center] (https://developer.apple.com/) on developer.apple.com. If you do not have an Apple developers license you will need to purchase it from Apple. When logged in to members center click on "Certificates, Identifiers & Profiles".

This will take you to a screen like this:
<img src="assets/images/apn-step1.PNG"/>

### Step 2 - Create APP ID

Click on Identifiers and then click on App IDs.
On the top right there will be a plus button, press this and a dialog like this should appear:
<img src="assets/images/apn-step2.PNG"/>

Enter your app ID name and select Explicit App ID. You must select Explicit App ID in order to be able to perform push notifications.
Enter a Bundle ID, this bundle ID must match the bundle ID that you entered during the PhoneGap build phase.
<img src="assets/images/apn-step2-1.PNG"/>
Tick the options push notifications and then click continue.

### Step 3 - Confirm
Click submit
<img src="assets/images/apn-step3.PNG"/>

### Step 4 - Creating APS Certificate
Locate your created App ID and click on it. This will expand it out.
<img src="assets/images/apn-step4.PNG"/>

Click on Edit and scroll down to the push notifications section. You will see that there are two options one to generate a certificate for development purposes and one for production. For the purpose of this documentation we will generate a development certificate, so we will click on the development create certificate button.
<img src="assets/images/apn-step4-1.PNG"/>

### Step 5 - Generating Certificate
Click continue
<img src="assets/images/apn-step5.PNG"/>

You will then be asked to upload a certificate signing request file. In order to create this file please read the following documentation:

[Creating CSR](https://world.mendix.com/display/refguide5/Managing+App+Signing+Keys)

Upload the CSR and then click generate. You will be presented by a screen saying your certificate is ready.
Click done and then click on your certificate from the list and click Download.

### Step 6 - Converting Certificate
Now that we have the certificate from Apple we now need to convert this into p12 format so that we can get it to work with our Mendix application.
This documentation should be helpful for getting your certificate converted:

[Converting Cer to p12](http://docs.build.phonegap.com/en_US/signing_signing-ios.md.html)

### Step 7 - Setting up Mendix APNS
Once you have the p12 certificate you can set up the Apple push notification system in Mendix. Login as an admin to the application and open up the apple admin. In the configuration you will need to upload the p12 file to the Apple administration section. You will also need to include the passcode that you entered when you converted the file to a p12 format.

Click on the enabled checkbox and then click Save. Once saved click the restart button. The apple push notification system will start up and inform you that it has restarted. After you have done this you will be ready to send Apple push notifications. Now that it is enabled the application will always start up when Mendix is started up.

<img src="assets/images/apn-step7.PNG"/>


## Setting up Google Cloud Messaging Server
In order to send google push notifications from this module you need to have set up a google account with google cloud messaging enabled.
To do so follow these steps to get registered for Google cloud messaging and enter the details into the Mendix screens.

### Step 1 - Login to developers console
Open up the Google [developers console] (https://console.developers.google.com) and login with your Google id.
<img src="assets/images/gcm-step1.PNG"/>

### Step 2 - Create project
Click new project and fill in the project name and project ID for your application. Then click create. Once created you will see a project number at the top of the screen next to the project ID. Take note of this ID because you will need it later on for our sender ID.

<img src="assets/images/gcm-step2.png"/>

### Step 3 - Enable Google Cloud Messaging
Once created, click the link to the Google Cloud Messaging API and click the Enable button.
<img src="assets/images/gcm-step3.png"/>

### Step 4 - Adding credentials
Click on the menu option credentials, located on the left hand side under the API Manager section.
<img src="assets/images/gcm-step4.png"/>

For the question **Which API are you using?**, select "Google Cloud Messaging".
The next question, **Where will you be calling the API from?**, answer "Web server".

<img src="assets/images/gcm-step4b.png"/>

### Step 5 - Create API key
Choose a name for your key and, optionally, restrict the IP addresses that can connect to the API.
Then, press the "Create API key" button.
<img src="assets/images/gcm-step5.png"/>

### Step 6 - Setup Mendix app
Open up your application in Mendix and login as an Admin, so that you can see the menu option "Google admin".
Enter the **project number** into the sender ID field and the API key into the API field. You can find it in your Google project's Settings pane.
<img src="assets/images/gcm-step6.PNG"/>
<img src="assets/images/gcm-step6b.png"/>

Once entered tick the checkbox "Enabled" and press the Restart button. From now on your application will always start the GCM push notification system for you.

For more information on setting up your Google API please refer to this article: (Google API Setup) [http://developer.android.com/google/gcm/gs.html]


## Setting up Windows
Windows requires no additional configuration for push notifications to work. Simply load the application onto a Windows 8 Phone and login to the application. The user's Windows Phone credentials should appear within the device menu.
The notifications for Windows work using a web service, the widget within your application registers the user's unique URL. With the URL the Mendix application can pass a message to this URL.  


## Installing a Windows App
In order to test and publish your Windows 8 applications you will need a Windows developer account that can be obtained from:

[Windows Dev Center](https://dev.windows.com/en-us)

Upon doing so you will receive a Publisher GUID (this can be located under Dashboard > Windows Phone Store > Account). This GUID is needed by PhoneGap in order to sign your applications.

You can download the SDK as well as development tools from [here](https://dev.windows.com/en-us/develop/download-phone-sdk)

Using the "Windows Phone Application Deployment" program you can deploy the XAP which PhoneGap creates onto your Windows device

<img src="assets/images/apploader.PNG"/>

## Creating PhoneGap app
In order to build a Mendix PhoneGap app that utilises the push notification application there are number of steps that you need to complete before being able to utilise the functionality.

### Step 1 - Login to Mendix home
Open up home.mendix.com and navigate to the project that you wish to build the app for. Once on the project wall for your application click on the publish section.

<img src="assets/images/step1.png"/>

### Step 2 - App Identifier
Once in the publish section you must enter an app identifier for your application. This is important when setting up an IOS app. Refer to the section about creating an app identifier to find out more information about creating an app identifier and IOS certificate.

<img src="assets/images/step2.png"/>

### Step 3 - Select Devices
Select the devices that you want to deploy your app to and upload splash screen images for the devices you have selected.

Press the button publish to appstore and you will be asked whether you want to build in the cloud or do it yourself. Choose do it yourself and press the Download Phonegap Build Package.


<img src="assets/images/step3.png"/>

### Step 4 - Download phonegap.zip

Once the phonegap.zip is downloaded, unzip it into a folder and open up the config.xml. You will need to edit the config.xml so that you can include additional phonegap plugins. The plugin we will need to include is the phonegap [push plugin](https://github.com/phonegap-build/PushPlugin).

The code you will need to include is:
`<gap:plugin name="com.phonegap.plugins.pushplugin" version="2.4.0" />`

<img src="assets/images/step4.png"/>

### Step 5 - Configure xml
Once you have edited the config.xml you should have everything necessary for your application to work. You will now need to zip up your files and upload the zipped file to [phonegap build](https://build.phonegap.com).

<img src="assets/images/step5.png"/>
