package com.eyesoreinc.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

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
	private final OutputStream mmOutStream;
	private final Handler mHandler;
	private final BluetoothSocket mSocket;
	private final BluetoothConnection mConnection;
	private final Integer mReadSize;

	private Boolean running = true;

	public BluetoothConnectedThread(BluetoothConnection connection, BluetoothSocket socket, Handler handler)
	{
		super();
		mHandler = handler;
		mSocket = socket;
		mConnection = connection;
		mReadSize = mConnection.getReadSize();

		InputStream tmpIn = null;
		OutputStream tmpOut = null;

		try {
			tmpIn = mSocket.getInputStream();
			tmpOut = mSocket.getOutputStream();
		}
		catch(IOException e){
			e.printStackTrace();
			running = false;
			mConnection.relayError(e.getMessage());
		}

		mmInStream = new BufferedInputStream(tmpIn, mConnection.getInputBuffer());
		mmOutStream = new BufferedOutputStream(tmpOut, mConnection.getOutputBuffer());

		start();
	}

	@Override
	public void run()
	{
		Log.d(LCAT, "Starting connection thread");
		byte[] buffer;

		while(running)
		{
			buffer = new byte[mReadSize]; // TODO try creating this only once
			try{
				// check per http://stackoverflow.com/questions/10328852/bluetooth-spp-on-galaxy-note-with-2-3-6
				if(mmInStream.available() > 0)
				{
					int byteCount = mmInStream.read(buffer, 0, buffer.length);

					// get rid of null bytes
					byte[] tempData = new byte[byteCount];
					System.arraycopy(buffer, 0, tempData, 0, byteCount);

					mHandler.obtainMessage(1, tempData).sendToTarget();
				}
			}
			catch(IOException e){
				running = false;
				e.printStackTrace();
				mConnection.relayError(e.getMessage());
				try{
					mSocket.close();
				}
				catch(IOException ee){
					ee.printStackTrace();
					mConnection.relayError(ee.getMessage());
				}
			}
		}
		try{
			mSocket.close();
		}
		catch(IOException eee){
			eee.printStackTrace();
			mConnection.relayError(eee.getMessage());
		}
		// when the thread stops running, stop the message loop!
		mConnection.stopMessageLoop();
	}

	public void write(byte[] bytes)
	{
		Log.d(LCAT, "Writing this many bytes: " + String.valueOf(bytes.length));
		try{
			mmOutStream.write(bytes, 0, bytes.length);
		}
		catch(IOException e){
			e.printStackTrace();
			mConnection.relayError(e.getMessage());
		}
	}

	// use in place of deprecated stop method
	public void stopThread()
	{
		running = false;
	}

}
