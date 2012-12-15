package com.eyesore.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;  // TODO

import org.appcelerator.kroll.common.TiConfig;
import org.appcelerator.kroll.common.Log;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;

public class BluetoothConnectedThread extends Thread
{
	// Debugging vars
	 private static final String LCAT = "BluetoothConnectedThread";
	 private static final boolean DBG = TiConfig.LOGD;
	// end debugging
		 
	private final InputStream mmInStream;
	// private final OutputStream mmOutStream; TODO
	private final Handler mHandler;
	private final BluetoothSocket mSocket;
	private final BluetoothConnection mConnection;
	
	private Boolean running = true;
	
	public BluetoothConnectedThread(BluetoothConnection connection, BluetoothSocket socket, Handler handler)
	{
		super();
		mHandler = handler;
		mSocket = socket;
		mConnection = connection;
		
		InputStream tmpIn = null;
		//OutputStream tmpOut = null;  TODO
		
		try {
			tmpIn = socket.getInputStream();
			//tmpOut = socket.getOutputStream(); TODO
		}
		catch(IOException e){
			e.printStackTrace();
			mConnection.relayError(e.getMessage());
		}
		
		mmInStream = tmpIn;
		//mmOutStream = tmpOut; TODO
		
		start();
	}
	
	@Override
	public void run()
	{
		Log.d(LCAT, "Starting connection thread");
		byte[] buffer;
		
		while(running)
		{
			buffer = new byte[1024];
			try{
				mmInStream.read(buffer);
				mHandler.obtainMessage(1, buffer).sendToTarget();
			}
			catch(IOException e){
				e.printStackTrace();
				mConnection.relayError(e.getMessage());
				try{
					mSocket.close();
				}
				catch(IOException ee){
					ee.printStackTrace();
					mConnection.relayError(e.getMessage());
				}
			}
		}
		// when the thread stops running, stop the message loop!
		mConnection.stopMessageLoop();
	}
	
	// use in place of deprecated stop method
	public void stopThread()
	{
		running = false;
	}
	
}