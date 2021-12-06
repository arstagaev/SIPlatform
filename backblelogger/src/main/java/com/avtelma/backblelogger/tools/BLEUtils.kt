package com.avtelma.backblelogger.tools

import android.bluetooth.BluetoothDevice

fun BluetoothDevice.removeBond() {
    this.javaClass.getMethod("removeBond").invoke(this)
}
