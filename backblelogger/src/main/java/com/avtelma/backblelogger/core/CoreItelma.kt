package com.avtelma.backblelogger.core

import android.util.Log
import com.avtelma.backblelogger.enum.TypeOfInputLog
import com.avtelma.backblelogger.tools.VariablesAndConstants.Companion.typeOfInputLog



fun dataParse2(data : ByteArray): RawTrip {
    val count = data.size
    // from 7th
    // from 3th
    var startOfTrip : Byte = 0x01
    var history :     Byte = 0x02
    var historyRAM :  Byte = 0x09
    var realtime :    Byte = 0x05

    var i = 2 // number of entry-point byte
    Log.w("fff","fff ${data.joinToString()}  ${count}")
    Log.w("fff","fff ${data[0]} ${startOfTrip} ${data[0]==startOfTrip} ${data[0]==history} ${data[0]==historyRAM} ${data[0]==realtime} ${count}")

    if (data[0] == startOfTrip) { // start trip 01
        i = 6
        typeOfInputLog = TypeOfInputLog.START_REC

    }else if ( data[0] == history) { // history 02 & 09 or realtime 05
        i = 2
        typeOfInputLog = TypeOfInputLog.HISTORY

    }else if (data[0] == historyRAM){
        i = 2
        typeOfInputLog = TypeOfInputLog.HISTORY_RAM

    } else if (data[0] == realtime) {
        i = 2
        typeOfInputLog = TypeOfInputLog.REAL_TIME

    } else {
        Log.e("eee","ERROR: NOT DEFINE HEADER IN BYTEARRAY !!!")
        Log.e("eee","ERROR: NOT DEFINE HEADER IN BYTEARRAY !!!")
        Log.e("eee","ERROR: NOT DEFINE HEADER IN BYTEARRAY !!!")
    }

    var rawTrip : ArrayList<RawTripParts> = arrayListOf<RawTripParts>()

    while(i < (count-1)){

        var x: UInt = 0u
        var y: UInt = 0u
        var z: UInt = 0u

        var xStr = ""
        var yStr = ""
        var zStr = ""

        if (i+1 <= count) {
            x = (data[i+1]).toUInt() * 256u + (data[i]).toUInt()
        } else {
            xStr = "-.--"
        }
        if (i+3 <= count) {
            y = (data[i+3]).toUInt() * 256u + (data[i+2]).toUInt()
        } else {
            yStr = "-.--"
        }
        if (i+5 <= count) {
            z = (data[i+5]).toUInt() * 256u + (data[i+4]).toUInt()
        } else {
            zStr = "-.--"
        }

        var resX: Float = 0F
        var resY: Float = 0F
        var resZ: Float = 0F

        val COOF: Float = 4F

        // 32797 is range of Int
        if (x > 32767u) {
            x = x.inv()
            resX = -(x + 1u).toFloat() * COOF / 65535
        } else {
            resX = (x).toFloat() * COOF / 65535
        }

        if (y > 32767u) {
            y = y.inv()
            resY = -(y + 1u).toFloat() * COOF / 65535
        } else {
            resY = (y).toFloat() * COOF / 65535
        }

        if (z > 32767u) {
            z = z.inv()
            resZ = -(z + 1u).toFloat() * COOF / 65535
        } else {
            resZ = (z).toFloat() * COOF / 65535
        }

        if (xStr == ""){
            xStr = resX.toString()
        }
        if (yStr == ""){
            yStr = resY.toString()
        }
        if (zStr == ""){
            zStr = resZ.toString()
        }

        rawTrip.add(RawTripParts(xStr,yStr,zStr)) // add new line in array

        //Log.d("ccc 1 output","vvvvv value $i")
        //Log.d("ccc 2 output","vvvvv x: ${xStr}, y: $yStr, z: $zStr")

        i += 6
    }
    return RawTrip(rawTrip)
}

@ExperimentalUnsignedTypes
fun dataParse(data : ByteArray, firstByte : Int) : RawTrip {

    var i = firstByte
    val count = data.size
    Log.i("vvv","vvvvvv count : ${count}")

    var rawTrip : ArrayList<RawTripParts> = arrayListOf<RawTripParts>()

    while(i < (count-1)){

        var x: UInt = 0u
        var y: UInt = 0u
        var z: UInt = 0u

        var xStr = ""
        var yStr = ""
        var zStr = ""

        if (i+1 <= count) {
            x = (data[i+1]).toUInt() * 256u + (data[i]).toUInt()
        } else {
            xStr = "-.--"
        }
        if (i+3 <= count) {
            y = (data[i+3]).toUInt() * 256u + (data[i+2]).toUInt()
        } else {
            yStr = "-.--"
        }
        if (i+5 <= count) {
            z = (data[i+5]).toUInt() * 256u + (data[i+4]).toUInt()
        } else {
            zStr = "-.--"
        }

        var resX: Float = 0F
        var resY: Float = 0F
        var resZ: Float = 0F

        val COOF: Float = 4F

        // 32797 is range of Int
        if (x > 32767u) {
            x = x.inv()
            resX = -(x + 1u).toFloat() * COOF / 65535
        } else {
            resX = (x).toFloat() * COOF / 65535
        }

        if (y > 32767u) {
            y = y.inv()
            resY = -(y + 1u).toFloat() * COOF / 65535
        } else {
            resY = (y).toFloat() * COOF / 65535
        }

        if (z > 32767u) {
            z = z.inv()
            resZ = -(z + 1u).toFloat() * COOF / 65535
        } else {
            resZ = (z).toFloat() * COOF / 65535
        }
//        Below is Swift
//        xStr = xStr == "-.--" ? "-.--" : String(resX)
//        yStr = yStr == "-.--" ? "-.--" : String(resY)
//        zStr = zStr == "-.--" ? "-.--" : String(resZ)

        if (xStr == ""){
            xStr = resX.toString()
        }
        if (yStr == ""){
            yStr = resY.toString()
        }
        if (zStr == ""){
            zStr = resZ.toString()
        }

        //resultRawTrip.rideParts.append(RawTripParts(x: xStr, y: yStr, z: zStr))
        rawTrip.add(RawTripParts(xStr,yStr,zStr))

        //Log.d("ccc 1 output","vvvvv value $i")
        //Log.d("ccc 2 output","vvvvv x: ${xStr}, y: $yStr, z: $zStr")


        i += 6

    }

    return RawTrip(rawTrip)

}

data class RawTripParts(val x: String, val y: String, val z: String)

data class RawTrip(val RawTripParts : ArrayList<RawTripParts>)