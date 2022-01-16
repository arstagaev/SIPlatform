package com.avtelma.backblelogger.logrecorder.tools

import android.bluetooth.BluetoothDevice

fun BluetoothDevice.removeBond() {
    this.javaClass.getMethod("removeBond").invoke(this)
}
