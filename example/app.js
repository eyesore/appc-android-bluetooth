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
var d = require('tools');



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
win1.add(buttonView);

var devicesView = Ti.UI.createView({
    layout: 'vertical',
    top: 5,
    left: 5,
    width: 100
});
win1.add(devicesView);

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

var abortPairingButton = Ti.UI.createButton({
    title: 'Abort Pairing',
    height: 70
});
buttonView.add(abortPairingButton);

var deviceView = Ti.UI.createView({
    layout: 'vertical',
    bottom: 5,
    left: 5,
    width: 200
});
win1.add(deviceView);

var pairingDevice;
bt.addEventListener('bluetooth:discovery', function(e)
{
    d.log(e.devices);   
    var device, deviceButton;
    for(device in e.devices)
    {
        deviceButton = Ti.UI.createButton({
            title: device,
            height: 50
        });
        deviceButton.addEventListener('click', function(e)
        {
            var newServicesView;
            bt.addEventListener('bluetooth:services', function(e)
            {
                newServicesView = Ti.UI.createView({
                    layout: 'vertical',
                    bottom: 5, 
                    left: 5,
                    width: 200
                });                
                win1.add(newServicesView);
                
                var i, serviceButton;
                for(i = 0; i < e.services.length; i++)
                {
                    serviceButton = Ti.UI.createButton({
                        title: e.services[i],
                        height: 50
                    });
                    
                    serviceButton.addEventListener('click', function(e)
                    {
                        var abortPairing = function(e)
                        {
                            bt.abortPairing(pairingDevice);
                        };
                        abortPairingButton.addEventListener('click', abortParing);

                        bt.addEventListener('bluetooth:paired', function(e)
                        {
                            handleConnection(e.device);
                        });
                        bt.pairDevice(pairingDevice, e.source.title);
                    });
                    newServicesView.add(serviceButton);
                }
            });
            
            pairingDevice = e.source.getTitle();
            bt.getDeviceServices(pairingDevice);
            deviceView.hide();           
        });
        deviceView.add(deviceButton);
    }    
});

var dataView = Ti.UI.createScrollView({
    top: 5,
    width: "100%",
    backgroundColor: 'grey',
    layout: 'vertical',
    color: 'black'
});
win2.add(dataView);

var manageWinSize = function()
{
    var children = dataView.getChildren();
    if(children.length > 100)
    {
        dataView.remove(children[0]);
    }
};

bt.addEventListener('bluetooth:data', function(e)
{
    dataView.add(Ti.UI.createLabel({
        text: e.data.toString()
    }));
    manageWinSize();
});

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
