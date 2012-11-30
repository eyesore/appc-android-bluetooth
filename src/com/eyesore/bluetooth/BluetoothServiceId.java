package com.eyesore.bluetooth;

import java.util.UUID;

public class BluetoothServiceId extends Object
{
	public final UUID mUuid;
	public final String mDescription;
	
	public BluetoothServiceId(UUID uuid, String description)
	{
		mUuid = uuid;
		mDescription = description;
	}
	
	public BluetoothServiceId()
	{
		mUuid = null;
		mDescription = null;
	}
}