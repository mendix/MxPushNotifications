# Mendix Push notifications

This module and widget should be used to implement push notifications for android and ios tablets/mobiles. This module/widget has been tested to work on 5.13.1 and up. 

The project contains:

- Directory structure.
- Readme.md file.
- License.
- Javascript source.
- XSD for package.xml, to configure properties of the widget, visible inside the Mendix business modeler.
- Example project
- Module for sending push notifications

## Contributing

For more information on contributing to this repository visit [Contributing to a GitHub repository](https://world.mendix.com/display/howto50/Contributing+to+a+GitHub+repository)!
 
## Description

The javascript inside the widget has examples of:

- Using CSS within a Widget.
- Using templating.
- Loading external library's.
- DOM manipulation.
- Event attaching.
- Loading data.
- Executing microflow and sending data.
- Working with the context object (The object that is send by a contextview , for instance a dataview).

## Setting up Apple Push Notification Server


## Setting up Google Cloud Messaging Server

## Creating PhoneGap app
In order to build a Mendix phonegap app that utilises the push notification application there are number of steps that you need to complete before being able to utilise the functionality.

### Step 1
Open up home.mendix.com and navigate to the project that you wish to build the app for. Once on the project wall for your application click on the publish section.

<img src="assets/images/step1.png"/>

Once in the publish section you must enter an app identifier for your application. This is important when setting up an IOS app. Refer to the section about creating an app identifier to find out more information about creating an app identifier and IOS certificate.

<img src="assets/images/step2.png"/>

Select the devices that you want to deploy your app to and upload splash screen images for the devices you have selected. 

Press the button publish to appstore and you will be asked whether you want to build in the cloud or do it yourself. Choose do it yourself and press the Download Phonegap Build Package.

<img src="assets/images/step3.png"/>


One the phonegap.zip is downloaded, unzip it into a folder and open up the config.xml. You will need to edit the config.xml so that you can include additional phonegap plugins. The plugin we will need to include is the phonegap [push plugin](https://github.com/phonegap-build/PushPlugin).

The code you will need to include is:
`<gap:plugin name="com.phonegap.plugins.pushplugin" version="2.4.0" />`

<img src="assets/images/step4.png"/>

Once you have edited the config.xml you should have everything necessary for your application to work. You will now need to zip up your files and upload the zipped file to [phonegap build](https://build.phonegap.com).

<img src="assets/images/step5.png"/>







## Testing push notifications
