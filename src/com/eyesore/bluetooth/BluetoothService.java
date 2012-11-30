package com.eyesore.bluetooth;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;

import android.app.Service;

import android.os.Handler;
import android.os.IBinder;
import android.os.Binder;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelUuid;

import android.content.Context;

import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiConfig;

import com.eyesore.bluetooth.BluetoothModule;
import com.eyesore.bluetooth.BluetoothConnectedThread;
import com.eyesore.bluetooth.BluetoothServerThread;
import com.eyesore.bluetooth.BluetoothClientThread;
import com.eyesore.bluetooth.BluetoothCommonServiceIds;

public class BluetoothService extends Service{
	
	// Debugging vars
	 private static final String LCAT = "BluetoothService";
	 private static final boolean DBG = TiConfig.LOGD;
	// end debugging
	 
	 private Context mContext;
	 private BluetoothModule mModule;
	 private final IBinder mBinder = new BluetoothBinder();
	 private static BluetoothDevice[] mArray = new BluetoothDevice[100]; 
	 private static int counter = 0;
	 
	 private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	 private BluetoothDevice mRemoteDevice;
	 
	 private UUID mRemoteServiceUuid;
	 
	 private BluetoothSocket mServerSocket;
	 private BluetoothSocket mClientSocket;	 
	 private BluetoothServerThread mServerThread;
	 private BluetoothClientThread mClientThread;
	 private BluetoothConnectedThread mConnectedThread;
	 private Handler mHandler;
	 
	 private final BroadcastReceiver foundReceiver = new BroadcastReceiver() {
	    public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
	        Log.d(LCAT,  action);
	        // When discovery finds a device
	        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
	            // Get the BluetoothDevice object from the Intent
	            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	            if(device != null)
	            {
	            	Log.d(LCAT,  device.getName());
	            	Log.d(LCAT,  "Adding device to array.");
		            // Add the name and address to an array adapter to show in a ListView
		            mArray[counter] = device;
		            counter++;	
	            }
	        }
	    }
	};
	
	private final BroadcastReceiver startedReceiver = new BroadcastReceiver(){
		public void onReceive(Context context, Intent intent){
			String action = intent.getAction();
			
			if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
				// empty device array and restart counter
				mArray = new BluetoothDevice[100];
				counter = 0;
			}
		}
	};
	
	private final BroadcastReceiver finishedReceiver = new BroadcastReceiver(){
		public void onReceive(Context context, Intent intent){
			String action = intent.getAction();
			
			if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
				mModule.discoveryFinished(mArray);
			}
		}
	};
	
	private final BroadcastReceiver connectionStateReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent){
			String action = intent.getAction();
			
			if(BluetoothDevice.ACTION_ACL_CONNECTED.equals(action))
				Log.d(LCAT, "Bluetooth device connected.");
			
			if(BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action))
				Log.d(LCAT, "Bluetooth device disconnected.");
		}
	};
     
     public BluetoothService() {
    	 super();
     }
     
     public BluetoothService(Context context){
    	 super();
    	 mContext = context;
     }
     
     // associate the service with the titanium module that called it.
     public void setBluetoothModule(BluetoothModule module)
     {
    	 Log.d(LCAT, "setting module");
    	 mModule = module;
     }
     
     public class BluetoothBinder extends Binder {
    	 BluetoothService getService(){
    		 return BluetoothService.this;
    	 }
     }
     
     @Override
     public IBinder onBind(Intent intent){
    	 return mBinder;
     }
     
     public BluetoothAdapter getAdapter()
     {
    	 return mBluetoothAdapter;
     }
     
     public BluetoothDevice getRemoteDevice()
     {
    	 return mRemoteDevice;
     }
     
     public UUID getServiceUuid()
     {
    	 return mRemoteServiceUuid;
     }
	 
	 public void connectBluetooth()
	 {		
		if (mBluetoothAdapter == null) 
		{
		    Log.d(LCAT, "********** Device does not support Bluetooth");
		    mModule.sendMessage("This device does not support bluetooth.");
		}
		else if(mBluetoothAdapter.isEnabled())
		{
		    Log.d(LCAT, "********** Bluetooth is enabled");
		    mModule.sendMessage("Bluetooth is already enabled!");
		}
		else
		{
			Log.d(LCAT, "********** Bluetooth is disabled");
			Log.d(LCAT, "********** Attempting to enable Bluetooth");			
			Intent intentBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			intentBluetooth.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			
			mContext.startActivity(intentBluetooth);
		}	
	}
	 
	public void registerReceivers(){
		IntentFilter foundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		mContext.registerReceiver(foundReceiver, foundFilter);
		
		IntentFilter startedFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		mContext.registerReceiver(startedReceiver, startedFilter);
		
		IntentFilter finishedFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		mContext.registerReceiver(finishedReceiver, finishedFilter);
		
//		IntentFilter connectedFilter = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
//		mContext.registerReceiver(connectionStateReceiver, connectedFilter);
//		
//		IntentFilter disconnectedFilter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
//		mContext.registerReceiver(connectionStateReceiver, disconnectedFilter);
	}
	
	public void unregisterReceivers(){
		mContext.unregisterReceiver(foundReceiver);
		mContext.unregisterReceiver(startedReceiver);
		mContext.unregisterReceiver(finishedReceiver);
//		mContext.unregisterReceiver(connectionStateReceiver);
//		mContext.unregisterReceiver(connectionStateReceiver);
	}
	 
	public void findBluetoothDevices(){			
		Log.d(LCAT, "Starting discovery.");
		mBluetoothAdapter.startDiscovery();
	}
	
	public Set<BluetoothDevice> findBondedDevices()
	{
		return mBluetoothAdapter.getBondedDevices();
	}
	
	public void attemptConnection(String remoteAddress, String serviceName)
	{
		BluetoothDevice remoteDevice = mBluetoothAdapter.getRemoteDevice(remoteAddress);
		BluetoothServiceId serviceId = BluetoothCommonServiceIds.getByDescription(serviceName);
		Log.d(LCAT, "UUID of selected service");
		Log.d(LCAT, serviceId.toString());
		Log.d(LCAT, "Attempting to pair with device: " + remoteDevice.toString());
		
		// TODO create some type of data struction to hold connections
		BluetoothConnection connection = new BluetoothConnection(this, remoteDevice, serviceId);
	}
	
	public void stopBluetoothThreads()
	{
		try{
			mClientSocket.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void stopMessageLoop()
	{
		Looper.myLooper().quit();
		mConnectedThread.stopThread();		
	}
	
	public void abortPairing()
	{
		mServerThread.abortPairing();
	}
	
	public void relayError(String message)
	{
		mModule.sendError(message);
	}
	
	public void dataReceived(String source, byte[] data)
	{
		mModule.dataReceived(source, data);
	}
	
	// fires event with String[]
	public void getServiceList(String deviceAddress)
	{
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(deviceAddress);
		
		String[] services = getServiceDescriptions(servicesFromDevice(device));
		mModule.servicesFound(services);
	}
	
	public void devicePaired(String deviceName)
	{
		mModule.devicePaired(deviceName);
	}
	
	//In SDK15 (4.0.3) this method is now public as
	//Bluetooth.fetchUuidsWithSdp() and BluetoothDevice.getUuids()
	// from http://stackoverflow.com/questions/11003280/finding-uuids-in-android-2-0
	private ParcelUuid[] servicesFromDevice(BluetoothDevice device) 
	{
	    try {
	        Class cl = Class.forName("android.bluetooth.BluetoothDevice");
	        Class[] par = {};
	        Method method = cl.getMethod("getUuids", par);
	        Object[] args = {};
	        ParcelUuid[] retval = (ParcelUuid[]) method.invoke(device, args);
	        return retval;
	    } 
	    catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}
	
	private String[] getServiceDescriptions(ParcelUuid[] parcels)
	{
		String[] retval = new String[parcels.length];
		int counter = 0;
		
		for(ParcelUuid p: parcels)
		{
			String nextService = getServiceDescription(p.getUuid());
			
			if(nextService != null)
			{
				retval[counter] = nextService;
				counter++;
			}
		}
		
		return retval;
	}
	
	private String getServiceDescription(UUID serviceId)
	{
		return BluetoothCommonServiceIds.getDescription(serviceId);
	}
}