# appc-android-bluetooth
Bluetooth module for appcelerator titanium, circa 2013.


#V1.3 Update
Build for Titanium SDK 5.5.1 for use with Android 6 and 7

#Permissions
The location permission is now required in order to serach for nearby devices.

The module includes the permission android.permission.ACCESS_COARSE_LOCATION however it can not automatically prompt the user for permission and so that must be done withing your Titanium App, as per the following JIRA

https://jira.appcelerator.org/browse/TIMOB-20144

https://jira.appcelerator.org/browse/TIMOB-20320

#Request Permission

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
