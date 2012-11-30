#!/usr/bin/python3

import uuid
import os

# string uuid_16 eg. '0x1101'
def to128(uuid_16):
    global bluetooth_base
    uuid_as_int = int(uuid_16, 16)

    new_uuid = uuid_as_int * (2 ** 96) + bluetooth_base.int

    return uuid.UUID(int=new_uuid)


def get_head():
    return """
package com.eyesore.bluetooth;

import com.eyesore.bluetooth.BluetoothServiceId;


// constants representing common bluetooth service Ids
class BluetoothCommonServiceIds
{\n"""


def get_foot():
    return "\n}"


def get_constant(uuid128, description):
    return "\t\tpublic static final BluetoothServiceId(UUID.fromString(String '{}'), '{}');\n".format(uuid128, description)


def generate_common_service_class():
    global common_services, outfile

    with open(outfile, 'w', encoding='utf-8') as out:
        out.write(get_head())

        for uuid in common_services:
            out.write(get_constant(to128(uuid), common_services[uuid]))

        out.write(get_foot())


# from bluetooth spec
bluetooth_base = uuid.UUID('00000000-0000-1000-8000-00805F9B34FB')

outfile = os.getcwd() + '/BluetoothCommonServiceIds.java'

# list of common service IDs with descriptions from http://bluetooth-pentest.narod.ru/doc/assigned_numbers_-_service_discovery.html
common_services = {
    '0x1000': 'ServiceDiscoveryServerServiceClassID',
    '0x1001': 'BrowseGroupDescriptorServiceClassID',
    '0x1002': 'PublicBrowseGroup',
    '0x1101': 'SerialPort',
    '0x1102': 'LANAccessUsingPPP',
    '0x1103': 'DialupNetworking',
    '0x1104': 'IrMCSync',
    '0x1105': 'OBEXObjectPush',
    '0x1106': 'OBEXFileTransfer',
    '0x1107': 'IrMCSyncCommand',
    '0x1108': 'Headset',
    '0x1109': 'CordlessTelephony',
    '0x110A': 'AudioSource',
    '0x110B': 'AudioSink',
    '0x110C': 'A/V_RemoteControlTarget',
    '0x110D': 'AdvancedAudioDistribution',
    '0x110E': 'A/V_RemoteControl',
    '0x110F': 'VideoConferencing',
    '0x1110': 'Intercom',
    '0x1111': 'Fax',
    '0x1112': 'HeadsetAudioGateway',
    '0x1113': 'WAP',
    '0x1114': 'WAP_CLIENT',
    '0x1115': 'PANU',
    '0x1116': 'NAP',
    '0x1117': 'GN',
    '0x1118': 'DirectPrinting',
    '0x1119': 'ReferencePrinting',
    '0x111A': 'Imaging',
    '0x111B': 'ImagingResponder',
    '0x111C': 'ImagingAutomaticArchive',
    '0x111D': 'ImagingReferencedObjects',
    '0x111E': 'Handsfree',
    '0x111F': 'HandsfreeAudioGateway',
    '0x1120': 'DirectPrintingReferenceObjectsService',
    '0x1121': 'ReflectedUI',
    '0x1122': 'BasicPrinting',
    '0x1123': 'PrintingStatus',
    '0x1124': 'HumanInterfaceDeviceService',
    '0x1125': 'HardcopyCableReplacement',
    '0x1126': 'HCR_Print',
    '0x1127': 'HCR_Scan',
    '0x1128': 'Common_ISDN_Access',
    '0x1129': 'VideoConferencingGW',
    '0x112A': 'UDI_MT',
    '0x112B': 'UDI_TA',
    '0x112C': 'Audio/Video',
    '0x112D': 'SIM_Access',
    '0x112E': 'Phonebook Access - PCE',
    '0x112F': 'Phonebook Access - PSE',
    '0x1130': 'Phonebook Access',
    '0x1200': 'PnPInformation',
    '0x1201': 'GenericNetworking',
    '0x1202': 'GenericFileTransfer',
    '0x1203': 'GenericAudio',
    '0x1204': 'GenericTelephony',
    '0x1205': 'UPNP_Service',
    '0x1206': 'UPNP_IP_Service',
    '0x1300': 'ESDP_UPNP_IP_PAN',
    '0x1301': 'ESDP_UPNP_IP_LAP',
    '0x1302': 'ESDP_UPNP_L2CAP',
    '0x1303': 'VideoSource',
    '0x1304': 'VideoSink',
    '0x1305': 'VideoDistribution'
}

if __name__ == '__main__':
    generate_common_service_class()
