package com.avtelma.backblelogger.logrecorder.tools

import android.os.Build
import com.avtelma.backblelogger.AVTSIPlatform_EntryPoint.Builder.CAR_LICENSE_SIGN_TAG_ADDRESS
//import com.avtelma.backblelogger.tools.VariablesAndConstants.Companion.SESSION_NAME_TIME_gps
import com.avtelma.backblelogger.logrecorder.tools.VariablesAndConstants.Companion.SESSION_NAME_TIME_raw
import com.avtelma.backblelogger.logrecorder.tools.VariablesAndConstants.Companion.SESSION_NAME_TIME_xyz
import com.avtelma.backblelogger.logrecorder.tools.VariablesAndConstants.Companion.TIMESTAMP_FOR_LOG
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


    return ""+TIMESTAMP_FOR_LOG+"_e.txt"
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




fun generateNameOfAllLogPerSession(){
    TIMESTAMP_FOR_LOG = generateJustTimeStamp() // for repeatable record per session of whole application
    SESSION_NAME_TIME_xyz = generateNameOfLogTXTFile()
    SESSION_NAME_TIME_raw = generateNameOfLogBYTESTXTFile()
    //SESSION_NAME_TIME_gps = generateNameOfLogGPS()
}


fun generateNameOfLogTXTFile() : String{
    var fullNameFile = ""

    // Create an image file name
    var timeStamp: String = SimpleDateFormat("ddMM_HHmmss").format(Date())


    return ""+timeStamp+"_imu_gps_${CAR_LICENSE_SIGN_TAG_ADDRESS}.txt"
}


fun generateNameOfLogBYTESTXTFile() : String{
    var fullNameFile = ""

    // Create an image file name
    var timeStamp: String = SimpleDateFormat("ddMM_HHmmss").format(Date())


    return ""+timeStamp+"_raw_bytes_${CAR_LICENSE_SIGN_TAG_ADDRESS}.txt"

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