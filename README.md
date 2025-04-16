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

#### Setup

```bash
./gradlew.bat updateUserLibs
```

#### Build

```bash
# Do not update Java dependencies
./gradlew.bat exportModule
```

or

```bash
# Update Java dependencies before export
./gradlew.bat buildModule
```

The resulting module package is placed in `module/dist/PushNotifications-<version>.mpk`.

## Notes

The userlibs for the PushNotifications module are not part of the repo.
To fetch them, run the `updateUserLibs` task after checking out the repo (and after switching branches).

Changes to the `userlib` folder are ignored by git.
If you make any changes to the libraries required by other modules, you must commit these explicitly, using e.g. `git add -f <path>`.
