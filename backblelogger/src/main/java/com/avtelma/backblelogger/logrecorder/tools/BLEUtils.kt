package com.avtelma.backblelogger.logrecorder.tools

import android.bluetooth.BluetoothDevice
import android.util.Log

fun BluetoothDevice.removeBond() {
    try {
        this.javaClass.getMethod("removeBond").invoke(this)
    }catch ( e: Exception) {
        Log.e("unbond","unbond NOT SUCCESS >> ${e.message} <<")
        Log.e("unbond","unbond NOT SUCCESS >> ${e.message} <<")
        Log.e("unbond","unbond NOT SUCCESS >> ${e.message} <<")
    }

}
