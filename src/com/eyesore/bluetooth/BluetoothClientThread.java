package com.eyesore.bluetooth;

import java.io.IOException;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.eyesore.bluetooth.BluetoothService;

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
	 private final BluetoothService mService;
	 
	 public BluetoothClientThread(BluetoothService service)
	 {
		 super();
		 Log.d(LCAT, "Creating new Client thread.");
		 mService = service;
		 mDevice = service.getRemoteDevice();
		 try{
			 mSocket = mDevice.createRfcommSocketToServiceRecord(mService.getServiceUuid());
		 }
		 catch(IOException e){
			 e.printStackTrace();
		 }
		 
		 start();
	 }
	 
	 @Override
	 public void run()
	 {
		// make sure discovery is cancelled before trying to connect
		 BluetoothAdapter adapter = mService.getAdapter();
		 adapter.cancelDiscovery();
		 
		 try{
			 mSocket.connect();
		 }
		 catch(IOException e){
			 e.printStackTrace();
			 try{
				 mSocket.close();
			 }
			 catch(IOException ee){
				 ee.printStackTrace();
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
		 }
	 }
	 
	 private void handleSocket(BluetoothSocket socket)
	 {
		 Log.d(LCAT, "Handling socket");
		 mService.setClientSocket(socket);
	 }
}