package com.avtelma.backblelogger

import android.util.Log
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

        var is_ENABLE_REALTIME_CHART = true
            set(value) {
                Log.w("sss","is_ENABLE_REALTIME_CHART configuration changed to $value")
                field = value
            }
        var is_SCORING = false
            set(value) {
                Log.w("sss","is_SCORING configuration changed to $value")
                field = value
            }
        var is_ENABLE_DELETE_GARBAGE_LOGS = false
            set(value) {
                Log.w("sss","is_ENABLE_DELETE_GARBAGE_LOGS configuration changed to $value")
                field = value
            }
    }

    fun setup( connectingStyle: ConnectingStyle){

        CONNECTING_STYLE = connectingStyle
        //InputSession.RECORD_ACTIVITY = recActivity
    }


}