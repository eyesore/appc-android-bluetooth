package com.eyesore.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.appcelerator.kroll.common.TiConfig;
import org.appcelerator.kroll.common.Log;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;

public class BluetoothConnectedThread extends Thread
{
	// Debugging vars
	 private static final String LCAT = "BluetoothServerThread";
	 private static final boolean DBG = TiConfig.LOGD;
	// end debugging
		 
	private final InputStream mmInStream;
	// private final OutputStream mmOutStream; TODO
	private final Handler mHandler;
	private final BluetoothSocket mSocket;
	
	private Boolean running = true;
	
	public BluetoothConnectedThread(BluetoothSocket socket, Handler handler)
	{
		super();
		mHandler = handler;
		mSocket = socket;
		InputStream tmpIn = null;
		//OutputStream tmpOut = null;  TODO
		
		try {
			tmpIn = socket.getInputStream();
			//tmpOut = socket.getOutputStream(); TODO
		}
		catch(IOException e){
			e.printStackTrace();
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
			buffer = new byte[512];
			try{
				mmInStream.read(buffer);
				//Log.d(LCAT, new String(buffer));
				//Log.d(LCAT, "value:" + bytes);
				mHandler.obtainMessage(1, buffer).sendToTarget();
			}
			catch(IOException e){
				e.printStackTrace();
				try{
					mSocket.close();
				}
				catch(IOException ee){
					ee.printStackTrace();
				}
			}
		}
	}
	
	// use in place of deprecated stop method
	public void stopThread()
	{
		running = false;
	}
	
}