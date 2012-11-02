// This is a test harness for your module
// You should do something interesting in this harness 
// to test out the module and to provide instructions 
// to users on how to use it by example.


// this sets the background color of the master UIView (when there are no windows/tab groups on it)
Titanium.UI.setBackgroundColor('#000');

// create tab group
var tabGroup = Titanium.UI.createTabGroup();
var mainActivity;
var bt = require('com.eyesore.bluetooth');
var NmeaStream = require('NmeaStream');

var parserOptions = {
    acceptTypes: ['RMC']
};
var parser = new NmeaStream(parserOptions);

//
// create base UI tab and root window
//
var win1 = Titanium.UI.createWindow({  
    title:'Tab 1',
    backgroundColor:'#fff'
});
var tab1 = Titanium.UI.createTab({  
    icon:'KS_nav_views.png',
    title:'Tab 1',
    window:win1
});


//
// create controls tab and root window
//
d = require('tools');
d.log('Loading bluetooth module.');


var win2 = Titanium.UI.createWindow({  
    title:'Tab 2',
    backgroundColor:'#fff'
});
var tab2 = Titanium.UI.createTab({  
    icon:'KS_nav_ui.png',
    title:'Tab 2',
    window:win2
});

var buttonView = Ti.UI.createView({
    layout: 'vertical',
    top: 5,
    right: 5,
    width: 100
});
win2.add(buttonView);

var devicesView = Ti.UI.createView({
    layout: 'vertical',
    top: 5,
    left: 5,
    width: 100
});
win2.add(devicesView);

//
tab2.addEventListener("click", function(e){
});

var devices;
var enableBtButton = Ti.UI.createButton({
    title: 'Enable Bluetooth',
    height: 70
});
enableBtButton.addEventListener('click', function(e)
{
    bt.requestEnable();
});
buttonView.add(enableBtButton);

var findDevicesButton = Ti.UI.createButton({
    title: 'Find Devices',
    height: 70  
});
findDevicesButton.addEventListener('click', function(e)
{
    bt.findDevices();
});
buttonView.add(findDevicesButton);

var deviceView = Ti.UI.createView({
    layout: 'vertical',
    bottom: 5,
    left: 5,
    width: 200
});
win2.add(deviceView);

bt.addEventListener('discovery:finished', function(e)
{
    d.log(e.devices); 
    d.log(typeof e.devices);   
    var device, deviceButton;
    for(device in e.devices)
    {
        deviceButton = Ti.UI.createButton({
            title: device,
            height: 50
        });
        deviceButton.addEventListener('click', function(e)
        {
            bt.pairDevice(e.source.getTitle());
        });
        deviceView.add(deviceButton);
    }    
});

var cancelPairingButton = Ti.UI.createButton({
    title: 'Abort Pairing',
    height: 70
});
cancelPairingButton.addEventListener('click', function(e)
{
    parser.getDataQuality();
});
buttonView.add(cancelPairingButton);

var dataView = Ti.UI.createScrollView({
    top: 5,
    width: "100%",
    backgroundColor: 'grey',
    layout: 'vertical',
    color: 'black'
});
win1.add(dataView);

var receivedData = [];
var processGpsData = function()
{
    if(receivedData.length > 0)
        parser.addData(receivedData.shift());
};

bt.addEventListener('data:received', function(e)
{
    receivedData.push(e.data);
});

var updateLocation = function()
{
    var lastLine = parser.getLast();
    if(lastLine)
    {
        dataView.add(Ti.UI.createLabel({
            text: lastLine.raw
        }));
    }
};

var gpsloop = setInterval(updateLocation, 2000);
var processLoop = setInterval(processGpsData, 250);

win2.addEventListener('open', function(e)
{
    mainActivity = e.source.activity;
    mainActivity.addEventListener('destroy', function(e)
    {
        d.log('Destroyed activity.');
        d.log(e);
        clearInterval(gpsloop);
        clearInterval(processLoop);
        bt.stopService();
    });
});

tabGroup.addTab(tab1);  
tabGroup.addTab(tab2);  
tabGroup.open();
