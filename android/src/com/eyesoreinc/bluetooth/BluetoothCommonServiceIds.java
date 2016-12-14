
package com.eyesoreinc.bluetooth;

import java.util.UUID;
import java.lang.reflect.Field;

import java.lang.IllegalAccessException;

import org.appcelerator.kroll.common.TiConfig;
import org.appcelerator.kroll.common.Log;

import com.eyesoreinc.bluetooth.BluetoothServiceId;


// constants representing common bluetooth service Ids
public class BluetoothCommonServiceIds
{
	public static final BluetoothServiceId COMMONISDNACCESS = new BluetoothServiceId(UUID.fromString("00001128-0000-1000-8000-00805f9b34fb"), "Common_ISDN_Access");
	public static final BluetoothServiceId GENERICNETWORKING = new BluetoothServiceId(UUID.fromString("00001201-0000-1000-8000-00805f9b34fb"), "GenericNetworking");
	public static final BluetoothServiceId PNPINFORMATION = new BluetoothServiceId(UUID.fromString("00001200-0000-1000-8000-00805f9b34fb"), "PnPInformation");
	public static final BluetoothServiceId VIDEOSOURCE = new BluetoothServiceId(UUID.fromString("00001303-0000-1000-8000-00805f9b34fb"), "VideoSource");
	public static final BluetoothServiceId BASICPRINTING = new BluetoothServiceId(UUID.fromString("00001122-0000-1000-8000-00805f9b34fb"), "BasicPrinting");
	public static final BluetoothServiceId PRINTINGSTATUS = new BluetoothServiceId(UUID.fromString("00001123-0000-1000-8000-00805f9b34fb"), "PrintingStatus");
	public static final BluetoothServiceId DIRECTPRINTINGREFERENCEOBJECTSSERVICE = new BluetoothServiceId(UUID.fromString("00001120-0000-1000-8000-00805f9b34fb"), "DirectPrintingReferenceObjectsService");
	public static final BluetoothServiceId REFLECTEDUI = new BluetoothServiceId(UUID.fromString("00001121-0000-1000-8000-00805f9b34fb"), "ReflectedUI");
	public static final BluetoothServiceId HEADSET = new BluetoothServiceId(UUID.fromString("00001108-0000-1000-8000-00805f9b34fb"), "Headset");
	public static final BluetoothServiceId CORDLESSTELEPHONY = new BluetoothServiceId(UUID.fromString("00001109-0000-1000-8000-00805f9b34fb"), "CordlessTelephony");
	public static final BluetoothServiceId HUMANINTERFACEDEVICESERVICE = new BluetoothServiceId(UUID.fromString("00001124-0000-1000-8000-00805f9b34fb"), "HumanInterfaceDeviceService");
	public static final BluetoothServiceId HARDCOPYCABLEREPLACEMENT = new BluetoothServiceId(UUID.fromString("00001125-0000-1000-8000-00805f9b34fb"), "HardcopyCableReplacement");
	public static final BluetoothServiceId IRMCSYNC = new BluetoothServiceId(UUID.fromString("00001104-0000-1000-8000-00805f9b34fb"), "IrMCSync");
	public static final BluetoothServiceId OBEXOBJECTPUSH = new BluetoothServiceId(UUID.fromString("00001105-0000-1000-8000-00805f9b34fb"), "OBEXObjectPush");
	public static final BluetoothServiceId OBEXFILETRANSFER = new BluetoothServiceId(UUID.fromString("00001106-0000-1000-8000-00805f9b34fb"), "OBEXFileTransfer");
	public static final BluetoothServiceId IRMCSYNCCOMMAND = new BluetoothServiceId(UUID.fromString("00001107-0000-1000-8000-00805f9b34fb"), "IrMCSyncCommand");
	public static final BluetoothServiceId SERIALPORT = new BluetoothServiceId(UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"), "SerialPort");
	public static final BluetoothServiceId LANACCESSUSINGPPP = new BluetoothServiceId(UUID.fromString("00001102-0000-1000-8000-00805f9b34fb"), "LANAccessUsingPPP");
	public static final BluetoothServiceId DIALUPNETWORKING = new BluetoothServiceId(UUID.fromString("00001103-0000-1000-8000-00805f9b34fb"), "DialupNetworking");
	public static final BluetoothServiceId VIDEOCONFERENCINGGW = new BluetoothServiceId(UUID.fromString("00001129-0000-1000-8000-00805f9b34fb"), "VideoConferencingGW");
	public static final BluetoothServiceId IMAGINGAUTOMATICARCHIVE = new BluetoothServiceId(UUID.fromString("0000111c-0000-1000-8000-00805f9b34fb"), "ImagingAutomaticArchive");
	public static final BluetoothServiceId IMAGINGRESPONDER = new BluetoothServiceId(UUID.fromString("0000111b-0000-1000-8000-00805f9b34fb"), "ImagingResponder");
	public static final BluetoothServiceId IMAGING = new BluetoothServiceId(UUID.fromString("0000111a-0000-1000-8000-00805f9b34fb"), "Imaging");
	public static final BluetoothServiceId VIDEODISTRIBUTION = new BluetoothServiceId(UUID.fromString("00001305-0000-1000-8000-00805f9b34fb"), "VideoDistribution");
	public static final BluetoothServiceId ESDPUPNPL2CAP = new BluetoothServiceId(UUID.fromString("00001302-0000-1000-8000-00805f9b34fb"), "ESDP_UPNP_L2CAP");
	public static final BluetoothServiceId HANDSFREEAUDIOGATEWAY = new BluetoothServiceId(UUID.fromString("0000111f-0000-1000-8000-00805f9b34fb"), "HandsfreeAudioGateway");
	public static final BluetoothServiceId HANDSFREE = new BluetoothServiceId(UUID.fromString("0000111e-0000-1000-8000-00805f9b34fb"), "Handsfree");
	public static final BluetoothServiceId IMAGINGREFERENCEDOBJECTS = new BluetoothServiceId(UUID.fromString("0000111d-0000-1000-8000-00805f9b34fb"), "ImagingReferencedObjects");
	public static final BluetoothServiceId PHONEBOOKACCESSPSE = new BluetoothServiceId(UUID.fromString("0000112f-0000-1000-8000-00805f9b34fb"), "Phonebook Access - PSE");
	public static final BluetoothServiceId ESDPUPNPIPLAP = new BluetoothServiceId(UUID.fromString("00001301-0000-1000-8000-00805f9b34fb"), "ESDP_UPNP_IP_LAP");
	public static final BluetoothServiceId GENERICAUDIO = new BluetoothServiceId(UUID.fromString("00001203-0000-1000-8000-00805f9b34fb"), "GenericAudio");
	public static final BluetoothServiceId GENERICFILETRANSFER = new BluetoothServiceId(UUID.fromString("00001202-0000-1000-8000-00805f9b34fb"), "GenericFileTransfer");
	public static final BluetoothServiceId ESDPUPNPIPPAN = new BluetoothServiceId(UUID.fromString("00001300-0000-1000-8000-00805f9b34fb"), "ESDP_UPNP_IP_PAN");
	public static final BluetoothServiceId PHONEBOOKACCESS = new BluetoothServiceId(UUID.fromString("00001130-0000-1000-8000-00805f9b34fb"), "Phonebook Access");
	public static final BluetoothServiceId REFERENCEPRINTING = new BluetoothServiceId(UUID.fromString("00001119-0000-1000-8000-00805f9b34fb"), "ReferencePrinting");
	public static final BluetoothServiceId DIRECTPRINTING = new BluetoothServiceId(UUID.fromString("00001118-0000-1000-8000-00805f9b34fb"), "DirectPrinting");
	public static final BluetoothServiceId UPNPIPSERVICE = new BluetoothServiceId(UUID.fromString("00001206-0000-1000-8000-00805f9b34fb"), "UPNP_IP_Service");
	public static final BluetoothServiceId WAP = new BluetoothServiceId(UUID.fromString("00001113-0000-1000-8000-00805f9b34fb"), "WAP");
	public static final BluetoothServiceId HEADSETAUDIOGATEWAY = new BluetoothServiceId(UUID.fromString("00001112-0000-1000-8000-00805f9b34fb"), "HeadsetAudioGateway");
	public static final BluetoothServiceId FAX = new BluetoothServiceId(UUID.fromString("00001111-0000-1000-8000-00805f9b34fb"), "Fax");
	public static final BluetoothServiceId INTERCOM = new BluetoothServiceId(UUID.fromString("00001110-0000-1000-8000-00805f9b34fb"), "Intercom");
	public static final BluetoothServiceId GN = new BluetoothServiceId(UUID.fromString("00001117-0000-1000-8000-00805f9b34fb"), "GN");
	public static final BluetoothServiceId NAP = new BluetoothServiceId(UUID.fromString("00001116-0000-1000-8000-00805f9b34fb"), "NAP");
	public static final BluetoothServiceId PANU = new BluetoothServiceId(UUID.fromString("00001115-0000-1000-8000-00805f9b34fb"), "PANU");
	public static final BluetoothServiceId WAPCLIENT = new BluetoothServiceId(UUID.fromString("00001114-0000-1000-8000-00805f9b34fb"), "WAP_CLIENT");
	public static final BluetoothServiceId UDITA = new BluetoothServiceId(UUID.fromString("0000112b-0000-1000-8000-00805f9b34fb"), "UDI_TA");
	public static final BluetoothServiceId AUDIOVIDEO = new BluetoothServiceId(UUID.fromString("0000112c-0000-1000-8000-00805f9b34fb"), "Audio/Video");
	public static final BluetoothServiceId UDIMT = new BluetoothServiceId(UUID.fromString("0000112a-0000-1000-8000-00805f9b34fb"), "UDI_MT");
	public static final BluetoothServiceId BROWSEGROUPDESCRIPTORSERVICECLASSID = new BluetoothServiceId(UUID.fromString("00001001-0000-1000-8000-00805f9b34fb"), "BrowseGroupDescriptorServiceClassID");
	public static final BluetoothServiceId SERVICEDISCOVERYSERVERSERVICECLASSID = new BluetoothServiceId(UUID.fromString("00001000-0000-1000-8000-00805f9b34fb"), "ServiceDiscoveryServerServiceClassID");
	public static final BluetoothServiceId SIMACCESS = new BluetoothServiceId(UUID.fromString("0000112d-0000-1000-8000-00805f9b34fb"), "SIM_Access");
	public static final BluetoothServiceId PUBLICBROWSEGROUP = new BluetoothServiceId(UUID.fromString("00001002-0000-1000-8000-00805f9b34fb"), "PublicBrowseGroup");
	public static final BluetoothServiceId ADVANCEDAUDIODISTRIBUTION = new BluetoothServiceId(UUID.fromString("0000110d-0000-1000-8000-00805f9b34fb"), "AdvancedAudioDistribution");
	public static final BluetoothServiceId AVREMOTECONTROL = new BluetoothServiceId(UUID.fromString("0000110e-0000-1000-8000-00805f9b34fb"), "A/V_RemoteControl");
	public static final BluetoothServiceId VIDEOCONFERENCING = new BluetoothServiceId(UUID.fromString("0000110f-0000-1000-8000-00805f9b34fb"), "VideoConferencing");
	public static final BluetoothServiceId PHONEBOOKACCESSPCE = new BluetoothServiceId(UUID.fromString("0000112e-0000-1000-8000-00805f9b34fb"), "Phonebook Access - PCE");
	public static final BluetoothServiceId AUDIOSOURCE = new BluetoothServiceId(UUID.fromString("0000110a-0000-1000-8000-00805f9b34fb"), "AudioSource");
	public static final BluetoothServiceId AUDIOSINK = new BluetoothServiceId(UUID.fromString("0000110b-0000-1000-8000-00805f9b34fb"), "AudioSink");
	public static final BluetoothServiceId AVREMOTECONTROLTARGET = new BluetoothServiceId(UUID.fromString("0000110c-0000-1000-8000-00805f9b34fb"), "A/V_RemoteControlTarget");
	public static final BluetoothServiceId UPNPSERVICE = new BluetoothServiceId(UUID.fromString("00001205-0000-1000-8000-00805f9b34fb"), "UPNP_Service");
	public static final BluetoothServiceId GENERICTELEPHONY = new BluetoothServiceId(UUID.fromString("00001204-0000-1000-8000-00805f9b34fb"), "GenericTelephony");
	public static final BluetoothServiceId HCRPRINT = new BluetoothServiceId(UUID.fromString("00001126-0000-1000-8000-00805f9b34fb"), "HCR_Print");
	public static final BluetoothServiceId VIDEOSINK = new BluetoothServiceId(UUID.fromString("00001304-0000-1000-8000-00805f9b34fb"), "VideoSink");
	public static final BluetoothServiceId HCRSCAN = new BluetoothServiceId(UUID.fromString("00001127-0000-1000-8000-00805f9b34fb"), "HCR_Scan");

	public static String getDescription(UUID serviceId)
	{
		Field[] fields = BluetoothCommonServiceIds.class.getDeclaredFields();

		for(Field f: fields)
		{
			try {
				BluetoothServiceId id = (BluetoothServiceId)f.get(null);

				if(id.mUuid.equals(serviceId))
					return id.mDescription;
			}
			catch (IllegalAccessException e){
				e.printStackTrace();
			}
		}

		return null;
	}

	public static BluetoothServiceId getByDescription(String description)
	{
		Field[] fields = BluetoothCommonServiceIds.class.getDeclaredFields();

		for(Field f: fields)
		{
			try {
				BluetoothServiceId id = (BluetoothServiceId)f.get(null);

				if(id.mDescription.equals(description))
					return id;
			}
			catch(IllegalAccessException e){
				e.printStackTrace();
			}
		}

		return null;
	}

	public static UUID getUuidByDescription(String description)
	{
		Field[] fields = BluetoothCommonServiceIds.class.getDeclaredFields();

		for(Field f: fields)
		{
			try {
				BluetoothServiceId id = (BluetoothServiceId)f.get(null);

				if(id.mDescription.equals(description))
					return id.mUuid;
			}
			catch(IllegalAccessException e){
				e.printStackTrace();
			}
		}

		return null;
	}
}
