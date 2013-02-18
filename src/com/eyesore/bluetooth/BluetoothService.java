package com.eyesore.bluetooth;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;

import android.app.Service;

import android.os.IBinder;
import android.os.Binder;
import android.os.ParcelUuid;

import android.content.Context;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiConfig;

import com.eyesore.bluetooth.BluetoothModule;
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
	 private KrollDict mConnections = new KrollDict();
	 private String[] mConnectedDevices = new String[10];
	 private int deviceCounter = 0;
	 private Integer mOutputBuffer = 8192;
	 private Integer mInputBuffer = 8192;
	 private Integer mReadSize = 1024;
	 
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
	
	private final BroadcastReceiver uuidReceiver = new BroadcastReceiver(){
		public void onReceive(Context context, Intent intenxt){
			
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
     
     public Integer getOutputBuffer()
     {
    	 return mOutputBuffer;
     }
     
     public Integer getInputBuffer()
     {
    	 return mInputBuffer;
     }
     
     public Integer getReadSize()
     {
    	 return mReadSize;
     }
     
     public void setOutputBuffer(Integer bytes)
     {
    	 mOutputBuffer = bytes;
     }
     
     public void setInputBuffer(Integer bytes)
     {
    	 mInputBuffer = bytes;
     }
     
     public void setReadSize(Integer bytes)
     {
    	 mReadSize = bytes;
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
	 
	public Boolean isEnabled()
	{
		return mBluetoothAdapter.isEnabled();
	}
	
	public Boolean isSupported()
	{
		return mBluetoothAdapter != null;
	}
	 
	public void registerReceivers(){
		IntentFilter foundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		mContext.registerReceiver(foundReceiver, foundFilter);
		
		IntentFilter startedFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		mContext.registerReceiver(startedReceiver, startedFilter);
		
		IntentFilter finishedFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		mContext.registerReceiver(finishedReceiver, finishedFilter);
		
		IntentFilter uuidFoundFilter = new IntentFilter("android.bluetooth.device.action.UUID");  // BluetoothDevice.ACTION_UUID in API level 15+
		mContext.registerReceiver(uuidReceiver, uuidFoundFilter);
	}
	
	public void unregisterReceivers(){
		mContext.unregisterReceiver(foundReceiver);
		mContext.unregisterReceiver(startedReceiver);
		mContext.unregisterReceiver(finishedReceiver);
		mContext.unregisterReceiver(uuidReceiver);
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
		
		// Store reference to connection keyed on remote Address - TODO key off device name?
		mConnections.put(remoteAddress, new BluetoothConnection(this, remoteDevice, serviceId));
		addConnectedDevice(remoteAddress);
	}
	
	public void write(String deviceAddress, byte[] data)
	{
		BluetoothConnection connection = getConnection(deviceAddress);
		if(connection != null)
			connection.write(data);
	}
	
	public void stopBluetoothThreads(String deviceAddress)
	{
		BluetoothConnection connection = getConnection(deviceAddress);
		if(connection != null)
			connection.stopBluetoothThreads();
	}
	
	public void closeAllConnections()
	{
		for(int i = 0; i < mConnectedDevices.length; i++)
		{
			stopBluetoothThreads(mConnectedDevices[i]);
		}
		
		mConnectedDevices = new String[10];
	}
	
	public void abortPairing(String deviceAddress)
	{
		getConnection(deviceAddress).abortPairing();
	}
	
	public void abortConnection(String deviceAddress)
	{
		stopBluetoothThreads(mConnections.getString(deviceAddress));
		mConnections.remove(deviceAddress);
	}
	
	public void relayError(String message)
	{
		mModule.sendError(message);
	}
	
	public void relayMessage(String message)
	{
		mModule.sendMessage(message);
	}
	
	public void dataReceived(String source, byte[] data)
	{
		mModule.dataReceived(source, data);
	}
	
	// fires event with String[]
	public void getServiceList(String deviceAddress)
	{
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(deviceAddress);
		Log.d(LCAT, device.getName());
		try{
			String[] services = getServiceDescriptions(servicesFromDevice(device));
			mModule.servicesFound(services);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			mModule.sendError(e.getMessage());
		}
	}
	
	// the point of this is to cache the list of services so that they can be retrieved later with getUuids
	public void startSdp(BluetoothDevice device) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException, ClassNotFoundException
	{
		//BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(deviceAddress);
		
		Class<?> cls = Class.forName("android.bluetooth.BluetoothDevice");
		java.lang.reflect.Method method = cls.getMethod("fetchUuidsWithSdp", new Class[0]);
		method.invoke(device);
	}
	
	public void devicePaired(String deviceName)
	{
		mModule.devicePaired(deviceName);
	}
	
	//In SDK15 (4.0.3) this method is now public as
	//Bluetooth.fetchUuidsWithSdp() and BluetoothDevice.getUuids()
	// from http://stackoverflow.com/questions/11003280/finding-uuids-in-android-2-0
	private ParcelUuid[] servicesFromDevice(BluetoothDevice device) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException
	{
		Class<?> cls = Class.forName("android.bluetooth.BluetoothDevice");
		java.lang.reflect.Method method = cls.getMethod("getUuids", new Class[0]);
		ParcelUuid[] uuidList = (ParcelUuid[]) method.invoke(device);
		return uuidList;
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
	
	private BluetoothConnection getConnection(String deviceAddress)
	{
		BluetoothConnection connection = (BluetoothConnection)mConnections.get(deviceAddress);
		return connection;
	}
	
	private void addConnectedDevice(String deviceAddress)
	{
		mConnectedDevices[deviceCounter] = deviceAddress;
		deviceCounter++;
	}
}