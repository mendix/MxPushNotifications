## Module usage

Please see [Push Notifications Connector](https://docs.mendix.com/appstore/modules/push-notifications) in the Mendix documentation for details.

## Development

### Widget

#### Requirements

- Node.js (v6+)
- nvm (optional)

#### Setup

```bash
cd widget
npm i
```

#### Build

```bash
npm run build
```

### Module

#### Requirements

- JDK

#### Update user libs

```bash
./gradlew.bat updateUserLibs
```

#### Build module (mpk)

```bash
./gradlew.bat exportModule
```

Module package is placed in `module/dist/PushNotifications-<version>.mpk`