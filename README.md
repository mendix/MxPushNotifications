# Mendix Push Notifications

> Documentation has been moved to [Mendix How-To's](https://docs.mendix.com/howto/mobile/push-notifications)

Push Notifications let your application notify a user of events even when the user is not actively using the application. This is a native capability provided by both Android and iOS devices and made available via Google Cloud Messaging (GCM) and Apple Push Notifications service (APNs). This project is meant to make it easy for Mendix developers who want to include Push Notifications capability into their Mendix hybrid mobile application.

## Development
For normal project all dependencies are found in the App Store module. For developers that like to update and publish a newer version, there is some help.

Prerequisite:
 - Install Gradle Build Tool https://gradle.org/install/
 - On MaxOS install Mono https://www.mono-project.com/download/stable/

Install dependencies JAR files
```bash
$ gradle prepareDeps
```

Please note that all project `userlib` content is cleared, use the App Store to re-download the required modules:

- [Community Commons](https://appstore.home.mendix.com/link/app/170/)
- [Encryption](https://appstore.home.mendix.com/link/app/1011/)

Check security on JAR dependencies:
```bash
$ gradle dependencyCheckAnalyze --info
```

Export module for App Store:
```bash
$ gradle extractModule
```
