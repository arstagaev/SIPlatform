package com.avtelma.backblelogger.tools

import android.bluetooth.BluetoothDevice
import com.avtelma.backblelogger.enum.ConnectingStyle
import com.avtelma.backblelogger.storage.PreferenceMaestro
import com.avtelma.backblelogger.enum.CurrentStateOfRecording
import com.avtelma.backblelogger.enum.TypeOfInputLog
import com.avtelma.backblelogger.ui.FourthlyDataContainerForChartsXYZ2

class VariablesAndConstants {
    companion object {

        const val NAME_OF_TABLET = "itelma"

        var CURRENT_SPEED = 0
        var CURRENT_LOG_JUST_PRESENTATION = ""

        // we connect in service by this bluetooth devices:
        var SUPER_BLE_DEVICE       : BluetoothDevice? = null
        //var FORBACKGRND_BLE_DEVICE : BluetoothDevice? = null
        //
        var CURRENT_STATE_OF_RECORDING = CurrentStateOfRecording.NO_CONNECTED
        var CONNECTING_STYLE = ConnectingStyle.AUTO_BY_BOND  // need migrate to entry point FIXME

        var IS_MANUAL_LOG_RECORD = false

        ///////////

        val BOND_FEATURE_IS = false // from preference maestro mb

        //////////

        var FIRST_BYTE = 2
        var SESSION_NAME_TIME_xyz = "-"
        var SESSION_NAME_TIME_raw = "-"
        var SESSION_NAME_TIME_gps = "-"
        var NAME_OF_FOLDER_LOGS = "ItelmaBLE_Background/RawData"

        var TIMESTAMP_FOR_LOG = generateJustTimeStamp()

        val SESSION_NAME = generateTimestampForFirebase()
        var MAINLOGS = ""
        var GPS_LOG = "NaN"

        // PostProcessing
        val GENERATE_SPECIAL_ID_FOR_EVENTS_2 = generateNameOfLogEvents()
        val GENERATE_SPECIAL_ID_FOR_PREPARED_LOG = generateNameOfPreparedLog()
        val GENERATE_ALL_TRIP = generateNameOfAllTripGpsLog()


        // Storage of autos
        //var STORAGE_OF_AUTOS = ArrayList<AutoCardWithTablet>()


        /**
         *  DEBUG & MANUAL under hood manipulations
         */
        const val DELAY_FOR_GET_HISTORY = 70000
        var MAKE_NOTIFY_DEBUG = false
        //var SETUP_AIM_BLE_DEVICE_NAME : String? = PreferenceMaestro.aimBLEDevice

        var typeOfInputLog = TypeOfInputLog.NO_LOG

        /**
         *  CHARTS
         */
        var ARRAY_MAIN_CHARTS = ArrayList<FourthlyDataContainerForChartsXYZ2>()

    }
}