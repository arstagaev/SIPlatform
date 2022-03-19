package com.avtelma.backblelogger

import com.avtelma.backblelogger.enum.ConnectingStyle


/**
 * Entry-Point of module
 */
var INPUT_PREM_DEVICES = arrayListOf<String>()
var AIM_CONNECT_DEVICE_ADDRESS = ""
class AVTSIPlatform_EntryPoint(
    private val config: Builder
) {

    fun getConfig(): String{
        return "is_ENABLE_REALTIME_CHART:${config.is_ENABLE_REALTIME_CHART}\n" +
                "is_SCORING:${config.is_SCORING}"
    }

    companion object Builder {
        var SEPARATE_ADDRESS = "_null" // for connected address? need implement ??
        var CAR_LICENSE_SIGN_TAG_ADDRESS = "LICENSESIG_000000000000"
        var is_ENABLE_REALTIME_CHART      : Boolean?         = null
        var is_SCORING                    : Boolean?         = null
        var is_ENABLE_DELETE_GARBAGE_LOGS : Boolean?         = null
        var CONNECTING_STYLEinp              : ConnectingStyle? = null
        var RECORD_ACTIVITY               : Class<*>?        = null
        var RECORD_ACTIVITY_FOR_RAWPARSER : Class<*>?        = null
        var STARTUP_DELAY_OF_LOOPER       : Long?            = null
        var SEND_TO_UNBOND                : String?          = null
        var CONNECTION_DELAY              : Long             = 8000 // ms

        fun setCarTabletParameters   (carlicense : String)   = apply { CAR_LICENSE_SIGN_TAG_ADDRESS = carlicense }
        fun realtimeChart   (isEnableChart: Boolean)         = apply { is_ENABLE_REALTIME_CHART = isEnableChart }
        fun scoring         (isScoring    : Boolean)         = apply { this.is_SCORING = isScoring }
        fun deleteNoTripLogs(delNoTripLog : Boolean)         = apply { this.is_ENABLE_DELETE_GARBAGE_LOGS = delNoTripLog }
        fun connStl         (connStyle    : ConnectingStyle) = apply { this.CONNECTING_STYLEinp = connStyle }
        fun recAct          (recAct       : Class<*>?)       = apply { this.RECORD_ACTIVITY = recAct }
        fun prsAct    (recActParser : Class<*>?)             = apply { this.RECORD_ACTIVITY_FOR_RAWPARSER = recActParser }
        fun startupDelay    (startupDelay : Long)            = apply { this.STARTUP_DELAY_OF_LOOPER = startupDelay }

        fun build(): AVTSIPlatform_EntryPoint {
            return AVTSIPlatform_EntryPoint(this)
        }
    }

//    data class Builder(
//        var is_ENABLE_REALTIME_CHART      : Boolean? = null,
//        var is_SCORING                    : Boolean? = null,
//        var is_ENABLE_DELETE_GARBAGE_LOGS : Boolean? = null,
//        var CONNECTING_STYLE              : ConnectingStyle? = null,
//        var RECORD_ACTIVITY               : Class<*>? = null,
//        var RECORD_ACTIVITY_FOR_RAWPARSER : Class<*>? = null
//    ) {
//        fun realtimeChart   (isEnableChart: Boolean) = apply { is_ENABLE_REALTIME_CHART = isEnableChart }
//        fun scoring         (isScoring    : Boolean) = apply { this.is_SCORING = isScoring }
//        fun deleteNoTripLogs(delNoTripLog : Boolean) = apply { this.is_ENABLE_DELETE_GARBAGE_LOGS = delNoTripLog }
//        fun connStl         (connStyle    : ConnectingStyle) = apply { this.CONNECTING_STYLE = connStyle }
//        fun recAct          (recAct       : Class<*>?) = apply { this.RECORD_ACTIVITY = recAct }
//        fun recActParser    (recActParser : Class<*>?) = apply { this.RECORD_ACTIVITY_FOR_RAWPARSER = recActParser }
//
//        fun build() = AVTSIPlatform_EntryPoint(
//            is_ENABLE_REALTIME_CHART!!     ,
//            is_SCORING!!               ,
//            is_ENABLE_DELETE_GARBAGE_LOGS!!,
//            CONNECTING_STYLE!!         ,
//            RECORD_ACTIVITY              ,
//            RECORD_ACTIVITY_FOR_RAWPARSER
//        )
//    }



//    companion object {
//        var RECORD_ACTIVITY : Class<*>? = null
//        var RECORD_ACTIVITY_FOR_RAWPARSER : Class<*>? = null
//
//        var is_ENABLE_REALTIME_CHART = true
//            set(value) {
//                Log.w("sss","is_ENABLE_REALTIME_CHART configuration changed to $value")
//                field = value
//            }
//        var is_SCORING = false
//            set(value) {
//                Log.w("sss","is_SCORING configuration changed to $value")
//                field = value
//            }
//        var is_ENABLE_DELETE_GARBAGE_LOGS = false
//            set(value) {
//                Log.w("sss","is_ENABLE_DELETE_GARBAGE_LOGS configuration changed to $value")
//                field = value
//            }
//    }

    fun setup( connectingStyle: ConnectingStyle){

        CONNECTING_STYLEinp = connectingStyle
        //InputSession.RECORD_ACTIVITY = recActivity
    }


}