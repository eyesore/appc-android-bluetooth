/**
 * This is an example of how one could use this bluetooth module to connect and interact
 * with a device that supports bluetooth on the Android platform.  The module wraps a service that controls
 * all functionality.  Only one instance of the service may run at one time, but it can control up to 10 simultaneous
 * bluetooth connections.
 *
 * Known issues:
 *  In some cases, a remote device must be paired first through the OS bluetooth settings before being able to connect through
 *  this module.  This explained here: http://stackoverflow.com/questions/2268365/android-bluetooth-cross-platform-interoperability
 *
 * For questions, concerns and bug reports, don't hesitate to contact me!
 * Author: Trey Jones <trey@eyesoreinc.com>
 */


// this sets the background color of the master UIView (when there are no windows/tab groups on it)
Titanium.UI.setBackgroundColor('#000');

// create tab group
var tabGroup = Titanium.UI.createTabGroup();

// in this example we will listen for 'destroy' on this activity, to stop the bluetooth service
var mainActivity;

// require the bluetooth module
// the constructor creates and binds a new android service with various bluetooth capabilities.
var bt = require('com.eyesore.bluetooth');
// set buffer sizes for all connections - optional
bt.setInputBuffer(4096);  // defaults to 8192
bt.setOutputBuffer(4096); // defaults to 8192
bt.setReadSize(512); // defaults to 1024

// debugging - includes a logging method
var d = require('tools');
d.log(bt);

// activity indicator
var activityIndicator = Ti.UI.createActivityIndicator({
    font: {fontFamily: 'Helvetica Neue', fontSize: 25, fontWeight: 'bold'},
    zIndex: 15
});
var showSpinner = function(message)
{
    message = message || 'Loading...';
    activityIndicator.setMessage(message);
    activityIndicator.show();
};
tabGroup.add(activityIndicator);


//
// Tab and window for device output.
//
var win2 = Titanium.UI.createWindow({
    title:'Tab 2',
    backgroundColor:'#fff'
});
var tab2 = Titanium.UI.createTab({
    icon:'KS_nav_ui.png',
    title:'Output',
    window:win2
});
var dataView = Ti.UI.createScrollView({
    top: 5,
    width: "100%",
    backgroundColor: 'gray',
    layout: 'vertical',
    color: 'black'
});
win2.add(dataView);

// End Tab 2 /////////////////////////////

//
// Tab and window for bluetooth actions
//
var win1 = Titanium.UI.createWindow({
    title:'Tab 1',
    backgroundColor:'#fff'
});
var tab1 = Titanium.UI.createTab({
    icon:'KS_nav_views.png',
    title:'Actions',
    window: win1
});

// container for bluetooth device and service buttons
var devicesAndServices = Ti.UI.createScrollView({
    width: '85%',
    top: 5,
    left: 5,
    layout: 'vertical'
});
win1.add(devicesAndServices);

// container for the buttons that represent bluetooth devices and services
var deviceView = Ti.UI.createView({
    layout: 'vertical',
    width: '95%',
    height: 400
});
devicesAndServices.add(deviceView);

// and a container for buttons representing services
var servicesView = Ti.UI.createView({
    layout: 'vertical',
    width: '95%'
});
devicesAndServices.add(servicesView);

// another button container!
var buttonView = Ti.UI.createView({
    layout: 'vertical',
    top: 5,
    right: 5,
    width: '15%'
});
win1.add(buttonView);

// button to request that android enable the bluetooth radio
var enableBtButton = Ti.UI.createButton({
    title: 'Enable Bluetooth',
    height: 70
});
enableBtButton.addEventListener('click', function(e)
{
    /**
     * This method prompts the user to enable bluetooth from within your app.
     * If bluetooth is already enabled, it fires a message event stating so.
     * It is safe to call, even if bluetooth is already enabled.
     *
     * In your app you probably don't need a button to do this - but it allows you to request the OS to enable BT
     * programmatically.
     */
    bt.requestEnable();
});
buttonView.add(enableBtButton);

// button to start the bluetooth discovery process - it takes a few seconds
var findDevicesButton = Ti.UI.createButton({
    title: 'Find Devices',
    height: 70
});
findDevicesButton.addEventListener('click', function(e)
{
    showSpinner('Searching for available devices.');
    /**
     * This is the method that kicks off discovery. Any devices found will be returned as strings
     * in the event 'bluetooth:discovery'.  The same strings can be passed to bt.getDeviceServices(device),
     * and as the first argument to bt.pairDevice(deviceName, serviceName).
     */
    bt.findDevices();
});
buttonView.add(findDevicesButton);

// failsafe button to cancel pairing
var abortPairingButton = Ti.UI.createButton({
    title: 'Abort Pairing',
    height: 70,
    visible: false  // show when trying to pair
});
abortPairingButton.addEventListener('click', function(e)
{
    /**
     * This method simply closes the sockets on the devices that are trying to pair.
     * In a production application, you probably don't want to expose this to the user, just
     * set a timer, as pairing shouldn't take very long if everything is OK.
     */
    bt.abortPairing(activeDevice);
});
buttonView.add(abortPairingButton);

// start service test
var startServiceButton = Ti.UI.createButton({
    title: 'Start Service',
    height: 70
});
startServiceButton.addEventListener('click', function(e)
{
    bt.startService();
});
buttonView.add(startServiceButton);

// stop service test
var stopServiceButton = Ti.UI.createButton({
    title: 'Stop Service',
    height: 70
});
stopServiceButton.addEventListener('click', function(e)
{
    bt.stopService();
});
buttonView.add(stopServiceButton);


// write test
var writeButton = Ti.UI.createButton({
    title: 'Test Send Data',
    height: 70
});
writeButton.addEventListener('click', function(e)
{
    showSpinner('Writing data...');
    // from Filesystem API example
    //var filePath = Ti.Filesystem.resourcesDirectory + '/assets/heroes.jpg',
    var filePath = Ti.Filesystem.resourcesDirectory + '/assets/test.txt',
        file = Ti.Filesystem.getFile(filePath),
        data = file.read();
    
    streamData(data);
    //bt.write(activeDevice, data);
});
buttonView.add(writeButton);

// global to hold the name of the device that is currently being interacted with
// not recommended for your app!  Do something better!
var activeDevice;

/**
 * This function is just a simple implementation of streaming file data incrementally to allow buffering.
 * @param {Ti.Filesystem.File object} file
 */
var streamData = function(data)
{
    var blobStream = Ti.Stream.createStream({
        mode: Ti.Stream.MODE_READ,
        source: data
        }),
        buffer = Ti.createBuffer({ length: 2048});
        
    while( blobStream.read(buffer) > -1)
    {
        bt.write(activeDevice, buffer.toBlob());
        buffer.clear();
    }
    blobStream.close();
    activityIndicator.hide();
};

// remove child views
var removeChildren = function(view)
{
    var children = view.getChildren(), i;
    for(i = 0; i < children.length; i++)
    {
        view.remove(children[i]);
    }
};

// just keep dataview from reaching critical mass
var manageWinSize = function()
{
    var children = dataView.getChildren();
    if(children.length > 30)
    {
        dataView.remove(children[0]);
    }
};

/**
 * @param {String} deviceName
 * @return {Ti.UI.button}  Button with a 'click' listener to get the supported services from the selected device.
 */
var newDeviceButton = function(deviceName)
{
    var button = Ti.UI.createButton({
        title: deviceName,
        height: 50,
        width: '80%'
    });


    // Listen for 'click' on this button and request the supported services from the device.
    button.addEventListener('click', function(e)
    {
        /**
         * This method requests all of the bluetooth services supported by this device.
         * The services are strings that are mapped to UUIDs.
         *
         * THE NAME OF THE SERVICE RETURNED MAY NOT BE RELIABLE, IT DEPENDS ON THE HARDWARE IMPLEMENTATION
         * The strings returned are mapped based on the list found here: http://bluetooth-pentest.narod.ru/doc/assigned_numbers_-_service_discovery.html
         *
         * If present, the service called Serial Port is a good generic channel to connect on if in doubt.
         *
         * The list of services is returned asynchronously on the event 'bluetooth:services'.  This process should be very fast.
         */
        activeDevice = e.source.getTitle();
        bt.getDeviceServices(activeDevice);
    });

    return button;
};

var newServicesButton = function(serviceName)
{
    var button = Ti.UI.createButton({
        title: serviceName,
        height: 50,
        width: '80%'
    });

    /**
     * This method will attempt to pair with the currently 'active' device on the channel represented by the
     * selected service.
     *
     * In production, you should implement a timer of some sort to cancel the pairing with 'bt.abortPairing()' after some amount of time.
     * If the connection is successful, the device name will be returned on the event 'bluetooth:paired'.
     *
     * You should also listen for the event 'bluetooth:error', which will fire if there is a problem connecting.
     */
    // the title is the service name
    button.addEventListener('click', function(e)
    {
        abortPairingButton.show();
        bt.pairDevice(activeDevice, e.source.getTitle());
    });

    return button;
};


// Add bluetooth Event Listeners
////////////////////////////////////////////

// setup the data connection after pairing is complete
var listenForData = function(deviceName)
{
    /**
     * The event fired when data is transmitted from the connected device looks like this:
     *      'bluetooth:data:deviceName'
     *
     * In order to connect to multiple devices simultaneously, you can listen to these events from all of them.
     *
     * e.data is device of type Titanium.Blob
     */
    d.log('Listener:');
    d.log('bluetooth:data:' + deviceName);
    bt.addEventListener('bluetooth:data:' + deviceName, function(e)
    {
        d.log('Data received:');
        d.log(e.data);
        // just dump the data into the container as an example
        // chances are you want to do something a little bit more sophisticated!
        dataView.add(Ti.UI.createLabel({
            text: deviceName + ' says: ' + e.data.getText()
        }));
        manageWinSize();
    });
};

/**
 * TODO add specifics about when messages are sent
 *
 * You should listen for messages from the bluetooth service.
 */
bt.addEventListener('bluetooth:message', function(e)
{
    d.log(e.message);
});

/**
 * You should definitely listen for errors from the bluetooth service.
 * They are also transmitted in e.message
 */
var errorHandler = function(e)
{
    Ti.API.error('Error in bluetooth module: ');
    d.log(e.message);
    alert(e.message);
    
    // prevent alert spam
    bt.removeEventListener('bluetooth:error', errorHandler);
    setTimeout(function()
    {
        bt.addEventListener('bluetooth:error', errorHandler);
    }, 3000);
};
bt.addEventListener('bluetooth:error', errorHandler);

/**
 * This event carries an object at e.devices.  Keys are the names of the devices found
 * during discovery.  The values are the hardware addresses of those devices.
 * You should only need the names; hardware addresses are not needed to interact with the bluetooth module.
 */
bt.addEventListener('bluetooth:discovery', function(e)
{
    activityIndicator.hide();
    // device names are returned in this variable - see log output
    d.log(e.devices);

    // clear old device data
    removeChildren(deviceView);
    removeChildren(servicesView);
    activeDevice = null;

    deviceView.add(Ti.UI.createLabel({
        text: 'Devices Found:'
    }));

    var device;
    for(device in e.devices)
        deviceView.add(newDeviceButton(device));
});

/**
 * This event carries an array of strings at e.services.  The strings are the names of services as
 * best determined based on the UUIDs given by the device.
 * Pass this string, along with the string device name to bt.pairDevice(deviceName, serviceName)
 * to initiate a connection.
 */
bt.addEventListener('bluetooth:services', function(e)
{
    d.log(e.services);

    removeChildren(servicesView);
    servicesView.add(Ti.UI.createLabel({
        text: activeDevice
    }));

    var i;
    for(i = 0; i < e.services.length; i++)
        servicesView.add(newServicesButton(e.services[i]));
});

/**
 * This event contains the name of the device that has been connected at e.device.
 * It occurs when pairing is completed, which is intiated with bt.pairDevice(deviceName, serviceName)
 */
bt.addEventListener('bluetooth:paired', function(e)
{
    d.log('Device is paired!');
    alert('Pairing finished.  Check Output tab...');
    abortPairingButton.hide();
    listenForData(e.device);
});

// dirty way of making sure the service stops when the app closes
win1.addEventListener('open', function(e)
{
    mainActivity = e.source.activity;
    mainActivity.addEventListener('destroy', function(e)
    {
        d.log('Destroyed activity.');
        d.log(e);
        bt.stopService();
    });
});

tabGroup.addTab(tab1);
tabGroup.addTab(tab2);
tabGroup.open();
