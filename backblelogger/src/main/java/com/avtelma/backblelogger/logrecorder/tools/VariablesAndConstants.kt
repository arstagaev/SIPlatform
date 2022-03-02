package com.avtelma.backblelogger.logrecorder.tools

import android.bluetooth.BluetoothDevice
import com.avtelma.backblelogger.enum.Actions
import com.avtelma.backblelogger.enum.ConnectingStyle
import com.avtelma.backblelogger.enum.CurrentStateOfService
import com.avtelma.backblelogger.enum.TypeOfInputLog
import com.avtelma.backblelogger.logrecorder.models.FoundDevice
import com.avtelma.backblelogger.ui.FourthlyDataContainerForChartsXYZ2
//import no.nordicsemi.android.support.v18.scanner.ScanResult

class VariablesAndConstants {
    companion object {

        const val NAME_OF_TABLET = "itelma"

        var CURRENT_SPEED = 0
        var CURRENT_LOG_JUST_PRESENTATION = ""

        // we connect in service by this bluetooth devices:
        var SUPER_BLE_DEVICE      : BluetoothDevice? = null
        var CLOSEST_BLE_DEVICE    : BluetoothDevice? = null
        var LIST_OF_FOUND_DEVICES : ArrayList<FoundDevice> = arrayListOf()
        //var FORBACKGRND_BLE_DEVICE : BluetoothDevice? = null
        var CHOSEN_BLE_DEVICE : BluetoothDevice? = null

        // Can share by AVTelma entry point
        var ACTION_NOW = Actions.START                                    // external manage of service
        var CURRENT_STATE_OF_SERVICE = CurrentStateOfService.NO_CONNECTED // state of work service
        var CONNECTING_STYLE = ConnectingStyle.AUTO_BY_BOND               // connect strategy to tablet
        var TYPE_OF_INPUT_LOG = TypeOfInputLog.NO_LOG                     // is history log or realtime or another
        // if activity is closed -> need make notification if we have connect,
        // and that`s it
        var SILENT_MODE = false
        var IS_MANUAL_LOG_RECORD = false

        ///////////

        val BOND_FEATURE_IS = false // from preference maestro mb

        //////// Need for separating logs,for e.x. if we in parking now
        var LAST_CAUGHT_NOTIFY_time = 0L        //[sec]
        const val DELAY_BEFORE_NEW_TRIP = 1200L //[sec] is 20 min
        //////// For writing logs in file
        var FIRST_BYTE = 2
        var SESSION_NAME_TIME_xyz =   "-"
        var SESSION_NAME_TIME_raw =   "-"

        //var SESSION_NAME_TIME_gps = "-"
        var NAME_OF_FOLDER_LOGS = "ItelmaBLE_Background/RawData"

        var TIMESTAMP_FOR_LOG = generateJustTimeStamp()

        val SESSION_NAME = generateTimestampForFirebase()
        var MAINLOGS = ""
        var GPS_LOG = "NaN"

        var IS_SUBSCRIBED = false

        // PostProcessing
//        val GENERATE_SPECIAL_ID_FOR_EVENTS_2 = generateNameOfLogEvents()
//        val GENERATE_SPECIAL_ID_FOR_PREPARED_LOG = generateNameOfPreparedLog()
//        val GENERATE_ALL_TRIP = generateNameOfAllTripGpsLog()


        // Storage of autos
        //var STORAGE_OF_AUTOS = ArrayList<AutoCardWithTablet>()


        /**
         *  DEBUG & MANUAL under hood manipulations
         */
        const val DELAY_FOR_GET_HISTORY = 70000
        var MAKE_NOTIFY_DEBUG = false
        var IS_NOTIFY_TYPE_OF_CHARACTERISTIC = true
        //var SETUP_AIM_BLE_DEVICE_NAME : String? = PreferenceMaestro.aimBLEDevice
        var WITH_CREATE_BOND = false


        /**
         *  CHARTS
         */
        var TRINITY_FOR_CHART = FourthlyDataContainerForChartsXYZ2("~",0f,0f,0f)

    }
}