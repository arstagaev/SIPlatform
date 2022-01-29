package com.avtelma.backblelogger.logrecorder.models

import android.bluetooth.BluetoothDevice

data class FoundDevice(
    val bluetoothDevice: BluetoothDevice,
    val rssi : Int
)
