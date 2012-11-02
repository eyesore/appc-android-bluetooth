package com.eyesore.bluetooth;

import java.io.IOException;
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
	
	// TODO more generic handler - don't convert to string, return data directly
	private final Handler.Callback relayData = new Handler.Callback() {		
		@Override
		public boolean handleMessage(Message message) {
			byte[] buffer = (byte[])message.obj;
			// String dataString = new String(buffer).replace(Character.toString('\0'), "");
			String dataString = new String(buffer);
			//Log.d(LCAT, dataString);
			mModule.dataReceived(dataString);
			return false;
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
		}
		else if(mBluetoothAdapter.isEnabled())
		{
		    Log.d(LCAT, "********** Bluetooth is enabled");
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
		
		IntentFilter connectedFilter = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
		mContext.registerReceiver(connectionStateReceiver, connectedFilter);
		
		IntentFilter disconnectedFilter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		mContext.registerReceiver(connectionStateReceiver, disconnectedFilter);
	}
	 
	public void findBluetoothDevices(){			
		Log.d(LCAT, "Starting discovery.");
		mBluetoothAdapter.startDiscovery();
	}
	
	public Set<BluetoothDevice> findBondedDevices()
	{
		return mBluetoothAdapter.getBondedDevices();
	}
	
	public void attemptConnection(String remoteAddress)
	{
		mRemoteDevice = mBluetoothAdapter.getRemoteDevice(remoteAddress);
		setServiceUuid();
		Log.d(LCAT, "Attempting to pair with device: " + mRemoteDevice.toString());
		
		mServerThread = new BluetoothServerThread(this);
		mClientThread = new BluetoothClientThread(this);
	}
	
	public void setServerSocket(BluetoothSocket socket)
	{
		mServerSocket = socket;
		
		if(mClientSocket != null)
			establishConnection();
		else
			Log.d(LCAT, "Got server socket, but client socket is null.");
	}
	
	public void setClientSocket(BluetoothSocket socket)
	{
		mClientSocket = socket;
		
		if(mClientSocket != null)
			establishConnection();
		else
			Log.d(LCAT, "Got client socket, but server socket is null.");
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
	
	private void establishConnection()
	{
		Looper.prepare();
		mHandler = new Handler(relayData);
		mConnectedThread = new BluetoothConnectedThread(mClientSocket, mHandler);
		Looper.loop();
	}
	
	private void setServiceUuid()
	{
		ParcelUuid[] parcels = mRemoteDevice.getUuids();  // API 15 or higher only
		if(parcels.length > 0)
			mRemoteServiceUuid = parcels[0].getUuid();
		
		Log.d(LCAT, mRemoteServiceUuid.toString());
		// TODO error handling
		// TODO figure out what the different UUIDs are for
	}
}