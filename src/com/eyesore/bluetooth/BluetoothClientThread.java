package com.eyesore.bluetooth;

import java.io.IOException;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiConfig;


public class BluetoothClientThread extends Thread
{
	// Debugging vars
	 private static final String LCAT = "BluetoothClientThread";
	 private static final boolean DBG = TiConfig.LOGD;
	// end debugging
	 
	 private BluetoothDevice mDevice;
	 private BluetoothSocket mSocket;
	 private final BluetoothConnection mConnection;
	 
	 public BluetoothClientThread(BluetoothConnection connection)
	 {
		 super();
		 Log.d(LCAT, "Creating new Client thread.");
		 mConnection = connection;
		 mDevice = connection.getDevice();
		 try{		 
//			 // using reflection in an effort to make it work - no different from calling createRfcommSocketToServiceRecord directly
//			 Class<?> cls = Class.forName("android.bluetooth.BluetoothDevice");
//			 Class<?> arg = Class.forName("java.util.UUID");
//			 java.lang.reflect.Method method = cls.getMethod("createRfcommSocketToServiceRecord", new Class[]{arg});
//			 mSocket = (BluetoothSocket) method.invoke(mDevice, mConnection.getServiceId().mUuid);
			 
			 mSocket = mDevice.createRfcommSocketToServiceRecord(mConnection.getServiceId().mUuid);
			 start();
		 }
		 catch(Exception e){
			 mConnection.abortPairing();
			 e.printStackTrace();
			 mConnection.relayError(e.getMessage());
		 }
	 }
	 
	 @Override
	 public void run()
	 {
		// make sure discovery is cancelled before trying to connect
		 BluetoothAdapter adapter = mConnection.getAdapter();
		 adapter.cancelDiscovery();
		 
		 try{
			 mSocket.connect();
		 }
		 catch(IOException e){
			 e.printStackTrace();
			 mConnection.relayError(e.getMessage());
			 try{
				 mSocket.close();
			 }
			 catch(IOException ee){
				 ee.printStackTrace();
				 mConnection.relayError(ee.getMessage());
			 }
			 return;
		 }
		 handleSocket(mSocket);
	 }
	 
	 public void abortConnection()
	 {
		 try{
			 mSocket.close();
		 }
		 catch(IOException e){
			 e.printStackTrace();
			 mConnection.relayError(e.getMessage());
		 }
	 }
	 
	 private void handleSocket(BluetoothSocket socket)
	 {
		 Log.d(LCAT, "Handling socket");
		 mConnection.setClientSocket(socket);
	 }
}