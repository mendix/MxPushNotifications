# Mendix Push Notifications

> Documentation has been moved to [Mendix How-To's](https://docs.mendix.com/howto/mobile/push-notifications)

Push Notifications let your application notify a user of events even when the user is not actively using the application. This is a native capability provided by both Android and iOS devices and made available via Google Cloud Messaging (GCM) and Apple Push Notifications service (APNs). This project is meant to make it easy for Mendix developers who want to include Push Notifications capability into their Mendix hybrid mobile application.

## Development
When using the module in a project, all dependencies are bundled when downloaded from the App Store. For developers that like to update and publish a newer version, there is some help scripts.

Prerequisite:
 - Install Gradle Build Tool https://gradle.org/install/
 - On MacOS install Mono https://www.mono-project.com/download/stable/

Install dependencies JAR files
```bash
$ gradle prepareDeps
```

Please note that all project `test/userlib` content is cleared, including the jar files of the other modules, use the App Store to re-download to add the required modules:

- [Community Commons](https://appstore.home.mendix.com/link/app/170/)
- [Encryption](https://appstore.home.mendix.com/link/app/1011/)

*Check versions* up to date JAR dependencies:
```bash
$ gradle dependencyUpdate
```

*Check security* on JAR dependencies:
```bash
$ gradle dependencyCheckAnalyze
```

Before release update the build.gradle, the target version of the module and the "Push Notification Connector" version.

``` groofy
PNC_VERSION = '4.0.6'
MXBUILD_VERSION = '7.23.8.58888'
```

*Export module* for App Store, will export module including the depended userlib content. The module will be exported to `dist/{version}/module/PushNotifications.mpk`

```bash
$ gradle extractModule
```
