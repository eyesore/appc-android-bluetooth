package com.eyesore.bluetooth;

import java.io.IOException;
import java.util.UUID;

import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiConfig;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

class BluetoothConnection extends Object
{
	private final BluetoothService mBluetooth;
	private final BluetoothDevice mDevice;
	private final BluetoothServiceId mServiceId;
	private final BluetoothServerThread mServerThread;
	private final BluetoothClientThread mClientThread;
	private BluetoothSocket mServerSocket;
	private BluetoothSocket mClientSocket;
	private Handler mHandler;
	private BluetoothConnectedThread mConnected;
	private final Integer mOutputBuffer;
	
	// TODO more generic handler - don't convert to string, return data directly
	private Handler.Callback mCallback;
	
	// Debugging vars
	 private static final String LCAT = "Bluetooth Connection";
	 private static final boolean DBG = TiConfig.LOGD;
	// end debugging
	
	public BluetoothConnection(BluetoothService service, BluetoothDevice device, BluetoothServiceId serviceId)
	{
		mBluetooth = service;
		mDevice = device;
		mServiceId = serviceId;
		
		mOutputBuffer = mBluetooth.getOutputBuffer();
		
		mCallback = new Handler.Callback() {		
			@Override
			public boolean handleMessage(Message message) {
				byte[] buffer = (byte[])message.obj;
				mBluetooth.dataReceived(mDevice.getName(), buffer);
				return false;
			}
		};
		
		mServerThread = new BluetoothServerThread(this);
		mClientThread = new BluetoothClientThread(this);
	}
	
	public void setServerSocket(BluetoothSocket socket)
	{
		mServerSocket = socket;
		
		if(mClientSocket != null)
			connect();
		else
			Log.d(LCAT, "Got server socket, but client socket is null.");
	}
	
	public void setClientSocket(BluetoothSocket socket)
	{
		mClientSocket = socket;
		
		if(mClientSocket != null)
			connect();
		else
			Log.d(LCAT, "Client socket is null.");
	}
	
	public BluetoothServiceId getServiceId()
	{
		return mServiceId;
	}
	
	public BluetoothAdapter getAdapter()
	{
		return mBluetooth.getAdapter();
	}
	
	public BluetoothDevice getDevice()
	{
		return mDevice;
	}
	
	public Integer getOutputBuffer()
	{
		return mOutputBuffer;
	}
	
	public void abortPairing()
	{
		mServerThread.abortPairing();
		mClientThread.abortConnection();
	}
	
	public void relayError(String message)
	{
		mBluetooth.relayError(message);
	}
	
	public void relayMessage(String message)
	{
		mBluetooth.relayMessage(message);
	}
	
	public void stopBluetoothThreads()
	{
		mConnected.stopThread();
	}
	
	public void stopMessageLoop()
	{
		// Looper.myLooper().quit();
		try{
			mClientSocket.close();
		}
		catch(IOException e){
			e.printStackTrace();
			relayMessage(e.getMessage());
		}
	}
	
	private void connect()
	{
		Looper.prepare();
		mHandler = new Handler(mCallback);
		mConnected = new BluetoothConnectedThread(this, mClientSocket, mHandler);
		mBluetooth.devicePaired(mDevice.getName());
		Looper.loop();
	}
}