package com.avtelma.backblelogger.logrecorder.core

import android.util.Log
import com.avtelma.backblelogger.AVTSIPlatform_EntryPoint.Builder.is_ENABLE_REALTIME_CHART
import com.avtelma.backblelogger.enum.TypeOfInputLog
import com.avtelma.backblelogger.logrecorder.tools.VariablesAndConstants.Companion.TRINITY_FOR_CHART
import com.avtelma.backblelogger.logrecorder.tools.VariablesAndConstants.Companion.GPS_LOG
import com.avtelma.backblelogger.logrecorder.tools.VariablesAndConstants.Companion.TYPE_OF_INPUT_LOG
import com.avtelma.backblelogger.logrecorder.tools.log
import com.avtelma.backblelogger.ui.FourthlyDataContainerForChartsXYZ2

var arrayDate :ArrayList<String> = ArrayList()
var arrayValueX :ArrayList<Float> = ArrayList()
var arrayValueY :ArrayList<Float> = ArrayList()
var arrayValueZ :ArrayList<Float> = ArrayList()
var ccount : Int = 0


fun converterToXYZJustFirstElement(rawTrip: RawTrip) : String{
    /**
     * Needed just for visual representation in notification
     */
    var str : String = ""
    if (rawTrip.RawTripParts.size > 0){
        Log.d("ccc test", " bytes raw " + rawTrip.RawTripParts.size + "  // bytes to string " + rawTrip.RawTripParts.get(0).toString())

        try{

            // sizeM by default is ~40
            for (i in 0 until rawTrip.RawTripParts.size){

                var x =(rawTrip.RawTripParts.get(0).x).toFloat().round(3)
                var y =(rawTrip.RawTripParts.get(0).y).toFloat().round(3)
                var z =(rawTrip.RawTripParts.get(0).z).toFloat().round(3)




                str = "\nx: "+x+" y: "+y+" z: "+z+""


            }

        } catch (e: Exception) {
            Log.w("iii","Warning: in Notify################ "+e.message)
            //Job was cancelled
        }


        //a++
    } else {
        Log.e("iii","[Error] Empty ByteArray wait for fill up")
        str = "[Error] Empty ByteArray wait for fill up"
    }
    return str

}


fun converterToXYZAllArray(rawTrip: RawTrip) : String {
    var bigString = ""
    if (rawTrip.RawTripParts.size > 0){
        Log.d("ccc test", " bytes raw " + rawTrip.RawTripParts.size + "  // bytes to string " + rawTrip.RawTripParts.get(0).toString())

        try{
            //var sizeM =

            // sizeM by default is ~40
            for (i in 0 until rawTrip.RawTripParts.size){

                var x =(rawTrip.RawTripParts.get(i).x).toFloat().round(6)
                var y =(rawTrip.RawTripParts.get(i).y).toFloat().round(6)
                var z =(rawTrip.RawTripParts.get(i).z).toFloat().round(6)

                if (is_ENABLE_REALTIME_CHART!!){
                    // for charts observing
                    TRINITY_FOR_CHART = FourthlyDataContainerForChartsXYZ2("",x,y,z)
                }

                var s = ""

                if (TYPE_OF_INPUT_LOG == TypeOfInputLog.HISTORY || TYPE_OF_INPUT_LOG == TypeOfInputLog.HISTORY_RAM || TYPE_OF_INPUT_LOG == TypeOfInputLog.START_REC){
                    GPS_LOG = "NaN"
                }

                if (i == 0) {
                    s  = "${x.toBigDecimal().toPlainString()};${y.toBigDecimal().toPlainString()};${z.toBigDecimal().toPlainString()};${GPS_LOG};${TYPE_OF_INPUT_LOG.name}\n"
                }else {
                    s =  "${x.toBigDecimal().toPlainString()};${y.toBigDecimal().toPlainString()};${z.toBigDecimal().toPlainString()};${GPS_LOG}\n"
                }

                bigString = bigString+s

            }


        } catch (e: Exception) {
            log("Warning: in Notify################ "+e.message)
            //Job was cancelled
        }


        //a++
    } else {
        log("[Error] Empty ByteArray wait for fill up")
        bigString = "---"
    }
    return bigString

}

fun Float.round(decimals: Int): Float {
    var multiplier = 1.0F
    repeat(decimals) { multiplier *= 10F }
    return kotlin.math.round(this * multiplier) / multiplier
}