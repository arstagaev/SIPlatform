package com.avtelma.backblelogger.rawparser.service_parsing_events

import android.os.Build
import com.avtelma.backblelogger.rawparser.tools.VariablesAndConstRawParser.TARGET_NAME_FILE_IMU_and_GPS
import com.avtelma.backblelogger.rawparser.tools.VariablesAndConstRawParser.TIMESTAMP_FOR_LOG
import java.text.SimpleDateFormat
import java.util.*


fun generateTimestampForFirebase() : String {
    var fullNameFile = ""

    // Create an image file name
    var timeStamp: String = SimpleDateFormat("ddMM_HHmmss").format(Date())
    var logsend = (Build.MANUFACTURER+" "+ Build.MODEL+" : "+timeStamp+"").toString()

    return logsend
}

fun generateJustTimeStamp() : String{
    return SimpleDateFormat("ddMM_HHmmss").format(Date())
}

//////POSTPROCESSING //////////////


fun generateNameOfLogEvents() : String{
    var fullNameFile = ""

    // Create an image file name
    //var timeStamp: String = SimpleDateFormat("ddMM_HHmmss").format(Date())


    return "${TARGET_NAME_FILE_IMU_and_GPS}_e.txt"
}

fun generateNameOfAdditionalLog() : String{
    var fullNameFile = ""

    // Create an image file name
    //var timeStamp: String = SimpleDateFormat("ddMM_HHmmss").format(Date())


    return ""+TIMESTAMP_FOR_LOG+"_additional.txt"
}

fun generateNameOfPreparedLog() : String{
    var fullNameFile = ""

    // Create an image file name
    //var timeStamp: String = SimpleDateFormat("ddMM_HHmmss").format(Date())


    return ""+TIMESTAMP_FOR_LOG+"_just_events_json.txt"
}

fun generateNameOfAllTripGpsLog() : String{
    var fullNameFile = ""

    // Create an image file name
    //var timeStamp: String = SimpleDateFormat("ddMM_HHmmss").format(Date())


    return ""+TIMESTAMP_FOR_LOG+"_all_trip_json.txt"
}

////////////////




//fun generateNameOfAllLogPerSession(){
//    TIMESTAMP_FOR_LOG = generateJustTimeStamp() // for repeatable record per session of whole application
//    SESSION_NAME_TIME_xyz = generateNameOfLogTXTFile()
//    SESSION_NAME_TIME_raw = generateNameOfLogBYTESTXTFile()
//    SESSION_NAME_TIME_gps = generateNameOfLogGPS()
//}


fun generateNameOfLogTXTFile() : String{
    var fullNameFile = ""

    // Create an image file name
    var timeStamp: String = SimpleDateFormat("ddMM_HHmmss").format(Date())


    return ""+timeStamp+"_imu_gps.txt"
}


fun generateNameOfLogBYTESTXTFile() : String{
    var fullNameFile = ""

    // Create an image file name
    var timeStamp: String = SimpleDateFormat("ddMM_HHmmss").format(Date())


    return ""+timeStamp+"_raw_bytes.txt"

}

fun generateNameOfLogGPS() : String{
    var fullNameFile = ""

    // Create an image file name
    var timeStamp: String = SimpleDateFormat("ddMM_HHmmss").format(Date())


    return ""+timeStamp+"_gps.txt"

}

fun generateNameOfLogVIDEOFile() : String{
    var fullNameFile = ""

    // Create an image file name
    var timeStamp: String = SimpleDateFormat("ddMM_HHmmss").format(Date())


    return ""+timeStamp+".mp4"

}