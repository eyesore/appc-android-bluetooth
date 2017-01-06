# Appcelerator Module For Bluetooth Connectivity on Android

## Requirements for Building the Module
Create the file android/build.properties with following content (substituting your local values):

* titanium.platform=${TITANIUM_SDK_PATH_FOR_DESIRED_VERSION}
* android.platform=${PATH_TO_ANDROID_SDK}/platforms/android-${DESIRED_VERSION}
* google.apis=${PATH_TO_ANDROID_SDK}/add-ons/addon-google_apis-google-${VERSION}
* android.ndk=${PATH_TO_ANDROID_NDK}

For example:

```
titanium.platform=/Users/me/Library/Application Support/Titanium/mobilesdk/osx/5.5.1.GA/android
android.platform=/Users/me/sdks/androidsdk2/platforms/android-23
google.apis=/Users/me/sdks/androidsdk2/add-ons/addon-google_apis-google-23
android.ndk=/Users/me/sdks/android-ndk-r10e
```

#V1.3 Update
Build for Titanium SDK 5.5.1 for use with Android 6 and 7

##Permissions
The location permission is now required in order to search for nearby devices.

The module includes the permission **android.permission.ACCESS_COARSE_LOCATION** however it can not automatically prompt the user for permission and so that must be done withing your Titanium App, as per the following JIRA

https://jira.appcelerator.org/browse/TIMOB-20144

https://jira.appcelerator.org/browse/TIMOB-20320

##Request Permission

```javascript

    var locationPermission = "android.permission.ACCESS_COARSE_LOCATION";
    var hasLocationPermission = Ti.Android.hasPermission(locationPermission);
    var permissionsToRequest = [];

    if (!hasLocationPermission) {
        permissionsToRequest.push(locationPermission);
    } else {
        bluetoothModule.findDevices(callback);
    }
    if (permissionsToRequest.length > 0) {
        Ti.Android.requestPermissions(permissionsToRequest, function(e) {

            if (e.success) {
                bluetoothModule.findDevices(callback);
            } else {
                alert("Your Android Device must grant location permission in order to search for devices");
            }
        });
    }
```

## [Contributors](https://github.com/eyesore/appc-android-bluetooth/graphs/contributors)
