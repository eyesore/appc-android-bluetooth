package com.eyesore.bluetooth;

import java.io.IOException;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiConfig;

import com.eyesore.bluetooth.BluetoothService;


public class BluetoothServerThread extends Thread 
{
	// Debugging vars
	 private static final String LCAT = "BluetoothServerThread";
	 private static final boolean DBG = TiConfig.LOGD;
	// end debugging
		 
	// service that opened the thread
	private final BluetoothService mService;
	// adapter acting as a bt server
	private final BluetoothAdapter mBluetoothAdapter;
	
	private BluetoothServerSocket mServerSocket;
	private BluetoothSocket mSocket;
	
	public BluetoothServerThread(BluetoothService service)
	{
		super();
		mService = service;
		BluetoothAdapter tmpAdapter = mService.getAdapter();
		mBluetoothAdapter = tmpAdapter;
		
		try {
	   		 mServerSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("com.eyesore.bluetooth.BluetoothService", mService.getServiceUuid());
	   	 }
	   	 catch(IOException e){
	   		 e.printStackTrace();
	   	 }
		start();
	}
	
	@Override
	public void run()
	{
		acceptConnections();
	}

	public void abortPairing()
	{
		Log.d(LCAT, "Aborting pair process");
		try {
			mServerSocket.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		Log.d(LCAT, "Pairing aborted.");
	}
	
	private void acceptConnections()
	{
		Log.d(LCAT, "Waiting for device to pair");
		try{
			mSocket = mServerSocket.accept();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		Log.d(LCAT, "Connection accepted");
		
		if(mSocket != null)
		{
			handleSocket(mSocket);
			Log.d(LCAT, "Device found...");
			try{
				mServerSocket.close();
			}
			catch(IOException e){
				e.printStackTrace();
			}	
		}
		Log.d(LCAT, "Finished pairing");
	}
	
	private void handleSocket(BluetoothSocket socket)
	{
		// always close the serversocket after socket is established
		try{
			mServerSocket.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
		mService.setServerSocket(socket);
	}	
}