package com.avtelma.backblelogger.rawparser.tools

import android.os.Environment
import com.avtelma.backblelogger.rawparser.service_parsing_events.enum.STATE_OF_PARSING
import com.avtelma.backblelogger.rawparser.service_parsing_events.enum.STYLE_OF_PARSING
import com.avtelma.backblelogger.rawparser.service_parsing_events.generateJustTimeStamp
import com.avtelma.backblelogger.rawparser.service_parsing_events.generateNameOfAllTripGpsLog
import com.avtelma.backblelogger.rawparser.service_parsing_events.generateNameOfLogEvents
import com.avtelma.backblelogger.rawparser.service_parsing_events.generateNameOfPreparedLog
import java.io.File

object VariablesAndConstRawParser {
    var currentStateOfParsing = STATE_OF_PARSING.START
    var styleOfParsing        = STYLE_OF_PARSING.WATER_FALL_PARSING
    var TIMESTAMP_FOR_LOG = generateJustTimeStamp()

    ////////////
    val root1_raw     = File(Environment.getExternalStorageDirectory(), "ItelmaBLE_Background/RawData")
    val root2_preproc = File(Environment.getExternalStorageDirectory(), "ItelmaBLE_Background/PreProcessing")
    val root3_json    = File(Environment.getExternalStorageDirectory(), "ItelmaBLE_Background/Jsons")

    //for define needed duration fixme i doubt when is second
    const val THRESHOLD_STOP_DURATION      = 75 // [seconds]
    const val THRESHOLD_GAS_BREAK_DURATION = 25 // [seconds]
    const val THRESHOLD_TURN_DURATION      = 25 // [seconds]
    const val THRESHOLD_JUMP_DURATION      = 12 // [seconds] fixme why so long?

    //private var NAME_OF_FILE_IMU_and_GPS = "1412_123022_imu_gpsleftpovorot.txt"//1412_123022_imu_gpsleftpovorot.txt"//1412_160244_imu_gps.txt" // 2907_212603_imu_gps.txt

    //private var NAME_OF_FILE_IMU_and_GPS ="1412_120124_imu_gps_gas_tormoz.txt"
    //private var NAME_OF_FILE_IMU_and_GPS = "1412_124133_imu_gps_serpantin.txt"
    //private var NAME_OF_FILE_IMU_and_GPS = "1412_160244_imu_gps.txt"1412_095227_imu_gps.txt
    var TARGET_NAME_FILE_IMU_and_GPS ="1412_123022_imu_gpsleftpovorot.txt" //<<<<
    //var NAME_OF_FILE_IMU_and_GPS = "1412_160244_imu_gps.txt" // very big

    var NAME_OF_FILE_IMU = "0705_091900_xyz.txt"
    var NAME_OF_FILE_GPS = "0705_091900_gps.txt"
    var CURSOR_MAP_SHOW = "${TARGET_NAME_FILE_IMU_and_GPS}_e.txt"
    // PostProcessing
    var GENERATE_SPECIAL_ID_FOR_EVENTS_2 = generateNameOfLogEvents()
    var GENERATE_SPECIAL_ID_FOR_PREPARED_LOG = generateNameOfPreparedLog()
    var GENERATE_ALL_TRIP = generateNameOfAllTripGpsLog()
    var TIME_OF_HAPPENED_EVENT = ""

    // for notifications
    var PROGRESS_NOTIF = 0
    var PROGRESS_MAX = 100
    //var CURRENT_FILE_IN_PROGRESS = "~"


}
