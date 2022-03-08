package com.avtelma.backblelogger.rawparser.tools

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun currentTimeDefiner(lines : Long, startTimestamp : Long) : String {
    val sdf = java.text.SimpleDateFormat("HH:mm:ss_dd/MM/yyyy")
    sdf.timeZone = TimeZone.getDefault()  //.getTimeZone("GMT+3:00")
    var str : String = sdf.format(java.util.Date(startTimestamp+lines))
    return str
}

fun timeStrToLong(fileName: String) : Long {
    var dateFormat: DateFormat = SimpleDateFormat("HH:mm:ss dd/MM/yyyy")
    dateFormat.timeZone = TimeZone.getDefault() //.getTimeZone("GMT+3:00")
    //                                            0303_152900
    var date: Date = dateFormat.parse("${fileName.substring(5,7)}:${fileName.substring(7,9)}:${fileName.substring(9,11)} ${fileName.take(2)}/${fileName.substring(2,4)}/2021")
    var time_L: Long = date.getTime()
    return time_L
}

