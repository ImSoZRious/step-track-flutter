# my_app

## Flutter
The app communicates with host platform with [pigeon package](https://pub.dev/packages/pigeon) ([type definition](pigeons/type.dart)), but it doesn't support `EventChannel` [(source)](https://github.com/flutter/flutter/issues/66711). So the stream communication must be handled seperately. Check [Event Channel](#event-channel) for more information.

---
## Android
In android, [foreground service](https://developer.android.com/guide/components/foreground-services) is used in order to achieve. Other type of service is killed when the app is closed. `android:stopWithTask` doesn't work with normal service.

Don't forget to add permisions and register service. [example](android/app/src/main/AndroidManifest.xml)

---
## Issue
- Sometimes, flutter app just don't start (probably device's problem). [issue](https://github.com/flutter/flutter/issues/93668)

---
## Event Channel
Continuous data stream uses Flutter's `EventChannel` to communicate with host platform. Our `EventChannel`'s name is `PedometerService/{EventName}`. Note that current `EventChannel` implementation doesn't hold message when there's no listener, so event handlers must be register to channel before registering service.

---
## Reference
### Event
#### Activate
when service is ready. \
eventName: ActivateEvent \
payload: int (latest number of steps)

#### Sensor
when host platform send update to step counter. \
eventName: SensorEvent \
payload: int (latest number of steps)