package com.avtelma.backblelogger

import com.avtelma.backblelogger.enum.ConnectingStyle
import com.avtelma.backblelogger.logrecorder.tools.VariablesAndConstants.Companion.CONNECTING_STYLE


/**
 * Entry-Point of module
 */
class AVTSIPlatform_EntryPoint {
    var INPUT_PREM_DEVICES = arrayListOf<String>()
    var AIM_CONNECT_DEVICE_ADDRESS = ""
    companion object {
        var RECORD_ACTIVITY : Class<*>? = null
        var RECORD_ACTIVITY_FOR_RAWPARSER : Class<*>? = null

    }

    fun setup( connectingStyle: ConnectingStyle){

        CONNECTING_STYLE = connectingStyle
        //InputSession.RECORD_ACTIVITY = recActivity
    }


}