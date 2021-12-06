package com.avtelma.backblelogger

import android.bluetooth.BluetoothDevice
import com.avtelma.backblelogger.enum.ConnectingStyle
import com.avtelma.backblelogger.tools.InputSession
import com.avtelma.backblelogger.tools.VariablesAndConstants.Companion.CONNECTING_STYLE
import com.avtelma.backblelogger.tools.VariablesAndConstants.Companion.SUPER_BLE_DEVICE


/**
 * Entry-Point of module
 */
class AVTSIPlatform_EntryPoint {
    var INPUT_PREM_DEVICES = arrayListOf<String>()
    var AIM_CONNECT_DEVICE_ADDRESS = ""

    fun setup( connectingStyle: ConnectingStyle){

        CONNECTING_STYLE = connectingStyle
        //InputSession.RECORD_ACTIVITY = recActivity
    }
    fun setupSec(inpDevice : BluetoothDevice ,){
        SUPER_BLE_DEVICE = inpDevice
    }


}