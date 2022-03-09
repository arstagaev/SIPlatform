package com.avtelma.backblelogger.rawparser.core.algorithm

import AngleBetweenQ_P
import Axang2Q_P
import Deviation_P
import EventLine
import EventPreFinal
import Find_max_index_P
import Mean_P
import NormVector_P
import Quatrotate_P
import RotVecCalc_P
import VectorNorm_P
import XYZ
import android.util.Log
import com.avtelma.backblelogger.AVTSIPlatform_EntryPoint.Builder.is_SCORING
import com.avtelma.backblelogger.globaltools.roundTo2decimals
import com.avtelma.backblelogger.rawparser.service_parsing_events.models.LtLn
import com.avtelma.backblelogger.rawparser.service_parsing_events.checkZeroOrNot
import com.avtelma.backblelogger.rawparser.service_parsing_events.generateNameOfLogEvents
import com.avtelma.backblelogger.rawparser.tools.VariablesAndConstRawParser
import com.avtelma.backblelogger.rawparser.tools.VariablesAndConstRawParser.GENERATE_SPECIAL_ID_FOR_EVENTS_2
import com.avtelma.backblelogger.rawparser.tools.VariablesAndConstRawParser.THRESHOLD_GAS_BREAK_DURATION
import com.avtelma.backblelogger.rawparser.tools.VariablesAndConstRawParser.THRESHOLD_JUMP_DURATION
import com.avtelma.backblelogger.rawparser.tools.VariablesAndConstRawParser.THRESHOLD_STOP_DURATION
import com.avtelma.backblelogger.rawparser.tools.VariablesAndConstRawParser.THRESHOLD_TURN_DURATION
import com.avtelma.backblelogger.rawparser.tools.VariablesAndConstRawParser.root2_preproc
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import kotlin.math.abs
import kotlin.math.sign


var SIZE_OF_EVENTS = 0
var stop_duration      : Int = 0
var gas_break_duration : Int = 0
var turn_duration      : Int = 0
var jump_duration      : Int = 0


/**
 *  Below - main code base of Algorithm
 */
//var Events = arrayListOf<EventLine>()



var isHorisonted = 0
var isAzimuted = 0

//var STOP_THRESHOLD=0.0002; //Do adaptive
var GAS_THRESHHOLD = 0.06;   //Do adaptive

//var GAS_BREAKS_THRESHHOLD = 0 // really?
val Chunk_size = 25       // Constant?

var first_chunk_completed = -1;
var pos = 0;
var isLocatedVert = false;
var isLocatedForw = false;



//var Forw: IntArray = intArrayOf(0, 0, 0);
var Forw = 0
var Hor_counter = 0;
var Forw_counter = 0;
var Q_Hor =     arrayListOf<Double>(1.0, 0.0, 0.0, 0.0)
var Q_Hor_old = arrayListOf<Double>(1.0, 0.0, 0.0, 0.0)
var Q_Forw =    arrayListOf<Double>(1.0, 0.0, 0.0, 0.0)
var GB_s = 0;
var GB_f = 0;
var GB_event = false;
var isReverse = 1;
var Side = -1
var test=0.0;

//Preallocating arrays
//    condition=zeros(size(T,1),1);
//    MeanA=zeros(size(T,1),3);
//    Chunk_A=zeros(Chunk_size,3);
//    Chunk_F=zeros(Chunk_size,3);
//    Events_M=zeros(size(T,1),5);
var MeanA : ArrayList<Double> = ArrayList()
var MeanA_temp     = arrayListOf<Double>()
var MeanA_mod :Double = 0.0
var RMS_amplitude = doubleArrayOf()
var RMS_Mean = doubleArrayOf()
var DevA  :ArrayList<Double> = ArrayList()
var DevA_mod: Double = 0.0

var NorMean  :ArrayList<Double> = ArrayList()
var Vert: Int = 0
var Axang_Hor :ArrayList<Double> = ArrayList()
var ANGLE_CRITERIA = 5.0
var IS_HORISONTED = 0
//var TURN_THRESHHOLD = 0

//1
var Suspect :ArrayList<Double> = arrayListOf(0.0, 0.0, 0.0)

var Axang_forw = arrayListOf<Double>()
var Q_Forw_old = arrayListOf<Double>(1.0, 0.0, 0.0, 0.0)
var IS_AZIMUTED = 0

var Grav = arrayListOf<Double>(0.0, 0.0, 0.0) //
var A1 = arrayListOf<Double>(0.0, 0.0, 0.0)

val STOP_THRESHOLD = 0.05
val GAS_BREAKS_THRESHHOLD = 0.06

val TURN_THRESHHOLD = 0.08
val BUMP_THRESHHOLD = 0.2
var testt = 0.0
var CURRENT_POS = 1
var CONDITION = 0

//var event = EventLine(0,0,0,0,CONDITION)

var EvsumForw = 0.0
var EvsumSide = 0.0
var EvsumVert = 0.0

var event = EventLine(0,0,0,0, CONDITION,0,0,0,0,0.0,0.0,0.0)
//var CONDITION = -1
var BIG_BUFFER : String = ""
fun rawToEventLine(arrayOfXYZinner: ArrayList<XYZ>) : EventLine {
    var chunk_A = arrayOfXYZinner // vvariables 25
    if (chunk_A != null) {
        logMB("start of cycle >>>>>>>>>>>>>>>> ${(chunk_A).joinToString()}")
    }

    CONDITION = isHorisonted + isAzimuted
    event.stop_1     = 0
    event.gas_break_2= 0
    event.turn_3     = 0
    event.jump_4     = 0
    event.condition_debug = CONDITION
    event.Forw       = 0.0
    event.Side       = 0.0
    event.Vert       = 0.0
    //event = EventLine(0,0,0,0,CONDITION)

    //Log.i("ccc","uuu isHoris $isHorisonted isAzim $isAzimuted")
    var GB_int = 0
    var GB_fin = 0
    logMB("${arrayOfXYZinner[0].x}  ${arrayOfXYZinner[0].y}  ${arrayOfXYZinner[0].z}  ${arrayOfXYZinner.size}")
    // here we starting define conditions
    when (CONDITION) {
        -1 -> {

            logMB("###################### CONDITION = -1 ##############################")
        }
        0 -> {
            MeanA     = Mean_P(chunk_A)
            MeanA_mod = VectorNorm_P(MeanA)

            DevA     = Deviation_P(chunk_A, MeanA)
            DevA_mod = VectorNorm_P(DevA)

            if (DevA_mod < STOP_THRESHOLD) {
                //Events
                event.stop_1 = 1  // EVENT
                //isLocatedVert
                NorMean = NormVector_P(MeanA) // ?


                if (isLocatedVert == false) {

                    Vert = Find_max_index_P(NorMean)
                    Grav[Vert] = 1.0
                    isLocatedVert = true
                }

                Axang_Hor = RotVecCalc_P(Grav, NorMean)
                Q_Hor_old = Q_Hor
                Q_Hor = Axang2Q_P(Axang_Hor)

                if (AngleBetweenQ_P(Q_Hor, Q_Hor_old) < ANGLE_CRITERIA) {
                    Hor_counter++
                }
                if (Hor_counter > 10) { // для фильтрации, если до 12 копить - то на две строчки больше генерирует, а если до 10 то норм
                    isHorisonted = 1
                }
            }

            logMB("devA mod:$DevA_mod Axang_Hor:$Axang_Hor Q_Hor:$Q_Hor Q_Hor_old:$Q_Hor_old")
        }
        1 -> {
            logMB("###################### CONDITION = 1 ############################## ")
            logMB("Q_Hor:$Q_Hor ")

            MeanA     = Mean_P(chunk_A)
            DevA      = Deviation_P(chunk_A, MeanA)

            DevA_mod  = VectorNorm_P(DevA)
            MeanA_mod = VectorNorm_P(MeanA)

            MeanA     =  Quatrotate_P(Q_Hor, MeanA)
            DevA      =  Quatrotate_P(Q_Hor, DevA)

            MeanA[Vert] = 0.0

            //test        = VectorNorm(MeanA);

            if (VectorNorm_P(MeanA) > GAS_BREAKS_THRESHHOLD) { // change vector norm - to meanmod

                Suspect = MeanA//NormVector(MeanA)

                if (isLocatedForw == false){
                    Forw = Find_max_index_P(Suspect)

                    A1[Forw] = 1.0              //
                    Side = 3 - (Vert + Forw)    //
                    isLocatedForw = true
                }
            }

            if (DevA_mod < STOP_THRESHOLD)   {
                event.stop_1 = 1      // EVENT

                if(isLocatedForw){
                    Axang_forw = RotVecCalc_P(A1, NormVector_P(Suspect)) // Nan
                    Q_Forw_old = Q_Forw
                    Q_Forw = Axang2Q_P(Axang_forw)

                    if (AngleBetweenQ_P(Q_Forw, Q_Forw_old) < ANGLE_CRITERIA) { // ANGLE_CRITERIA = 5
                        Forw_counter++

                        if (Forw_counter > Chunk_size / 5) {
                            isAzimuted = 1

                            isReverse = -1
                        }


                    }else{
                        Forw_counter = 0
                    }
                }
            }else{
            }
        }
        2 -> {
            logMB("###################### CONDITION = 2 ############################## $SIZE_OF_EVENTS")
            MeanA    = Mean_P(chunk_A)
            //MeanA_mod = VectorNorm(MeanA) //

            DevA     = Deviation_P(chunk_A, MeanA)
            DevA_mod = VectorNorm_P(DevA)

            MeanA =  Quatrotate_P( Q_Hor,  MeanA )
            DevA =  Quatrotate_P( Q_Hor,  DevA  )

            MeanA =  Quatrotate_P( Q_Forw, MeanA )
            DevA =  Quatrotate_P( Q_Forw, DevA  )





            if (abs(MeanA[Forw]) > GAS_BREAKS_THRESHHOLD) {

                event.gas_break_2 = ((MeanA[Forw]).sign * isReverse).toInt()
                // need catch from array
                // Score part:
                event.Forw = abs(MeanA[Forw])
                EvsumForw += abs(MeanA[Forw])
            }

            if (DevA_mod < STOP_THRESHOLD) {

                event.stop_1      = 1

            }
            //!!! i force reinit Side below coz he is equal = -1
            //Side = 2
            if (abs(MeanA[Side]) > TURN_THRESHHOLD) {

                event.turn_3  = ((MeanA[Side]).sign * isReverse).toInt()
                // Score part:
                event.Side = abs(MeanA[Side])
                EvsumSide += abs(MeanA[Side])
            }

            if (DevA[Vert] > BUMP_THRESHHOLD) {

                event.jump_4 = 1
                // Score part:
                event.Vert = DevA[Vert]
                EvsumVert += DevA[Vert]
            }

//            event.Forw = abs(MeanA[Forw])
//            EvsumForw += abs(MeanA[Forw])
//
//            event.Side = abs(MeanA[Side])
//            EvsumSide += abs(MeanA[Side])
//
//            event.Vert = DevA[Vert]
//            EvsumVert += DevA[Vert]
            Log.i("mmm","mmark:>>Forw ${MeanA[Forw]} side ${MeanA[Side]} vert ${DevA[Vert]} |<< // ${MeanA.joinToString()} ")

            // for (g in 0 until Events.size ){
            //     Log.i("event","size:${Events.size} events: ${Events[g].stop} ${Events[g].gas_break} ${Events[g].turn} ${Events[g].jump}")
            // }
        }
    }

    logMB("|||${event.stop_1},${event.gas_break_2},${event.turn_3},${event.jump_4},${event.condition_debug}||| Cond: $CONDITION : $CURRENT_POS  Q_Hor:${Q_Hor[0]} ${Q_Hor[1]} ${Q_Hor[2]} ${Q_Hor[3]}; Q_Forw:${Q_Forw[0]} ${Q_Forw[1]} ${Q_Forw[2]} ${Q_Forw[3]}; MeanA:[${MeanA[0]} ${MeanA[1]} ${MeanA[2]}] MeanA_mod:$MeanA_mod* DevA:[${DevA[0]} ${DevA[1]} ${DevA[2]}]; *${DevA_mod.toString()}* ")
    return event
}

var TIME = 0
var last_time        = 0
var last_stop_1      = 0
var last_gas_break_2 = 0
var last_turn_3      = 0
var last_jump_4      = 0
var last_LtLn : LtLn = LtLn(0.0,0.0)


var needPublishLog = false

var SAVER_Event_Container : EventPreFinal? = null

var EvrawForw = 0.0
var EvrawSide = 0.0
var EvrawVert = 0.0

fun findDurationAndScoreOfEvents (eventLine: EventLine, lat: Double, lon : Double) {
    TIME++ // time scale ~[1/25s]
    needPublishLog = false
    var notEnoughLong = false


    //stop_1
    //gas_break_2
    //turn_3
    //jump_4
    if (last_stop_1 == eventLine.stop_1) { // if event is resume and not ended
        event.stop_duration++ // and we continue duration
    } else if (event.stop_duration < THRESHOLD_STOP_DURATION) {  // if duration of event not enough long
        notEnoughLong = true
    } else { // if duration is normal, and new event is appear, we can publish event (with duration & score) to file
        needPublishLog = true
    }

    if (last_gas_break_2 == eventLine.gas_break_2 && !needPublishLog) {
        event.gas_break_duration++
    } else if (event.gas_break_duration < THRESHOLD_GAS_BREAK_DURATION) {
        notEnoughLong = true
    } else {
        needPublishLog = true
    }

    if (last_turn_3 == eventLine.turn_3 && !needPublishLog) {
        event.turn_duration++
    } else if (event.turn_duration < THRESHOLD_TURN_DURATION) {
        notEnoughLong = true
    }  else {
        needPublishLog = true
    }

    if (last_jump_4 == eventLine.jump_4 && !needPublishLog) {
        event.jump_duration++
    } else if (event.jump_duration < THRESHOLD_JUMP_DURATION) {
        notEnoughLong = true
    } else {
        needPublishLog = true
    }
    // for somewhat refresh position ?
    if (event.stop_duration in 6..9) {
        last_LtLn = LtLn(lat, lon)
    }

    Log.i("ccc","ccc needPublishLog:$needPublishLog|| event:st${eventLine.stop_duration}gs${eventLine.gas_break_duration}tr${eventLine.turn_duration}jm${eventLine.jump_duration}")
    ////
    if (needPublishLog) {
        if (notEnoughLong) // return if not enough return
            return

        if (last_stop_1 != eventLine.stop_1 || last_gas_break_2 != eventLine.gas_break_2 || last_turn_3 != eventLine.turn_3 || last_jump_4 != eventLine.jump_4) {
            Log.w("algo","algo I see difference")
        }else {
            Log.w("algo","algo I don`t see difference")
            return
        }
        /**
         * Catch max duration of single event from 4 types of event
         */
        var maxDuration = arrayListOf<Int>(event.stop_duration, event.gas_break_duration, event.turn_duration, event.jump_duration).maxOrNull()
        if (maxDuration == null) { maxDuration = 1000000 } // just for test \\\

        /**
         * Scoring Part
         */
        if (event.gas_break_duration > 0) {
            EvrawForw = EvsumForw / (event.gas_break_duration / 25 ).toDouble() // need in ranges: 8-35 but i have 9
        }
        if (event.turn_duration > 0) {
            EvrawSide = EvsumSide / (event.turn_duration / 25 ).toDouble()
        }
        if (event.jump_duration > 0) {
            EvrawVert = EvsumVert / (event.jump_duration / 25 ).toDouble()
        }


        Log.i("mmm","mmark: EvsumForw:${EvsumForw} EvsumSide:${EvsumSide} EvsumVert:${EvsumVert} |>|> ${EvrawForw} ${EvrawSide} ${EvrawVert} // ")


//        var asd = EventPreFinal(
//            last_stop_1, last_gas_break_2, last_turn_3, last_jump_4,eventLine.condition_debug,
//            TIME -maxDuration,
//            event.stop_duration, event.gas_break_duration, event.turn_duration, event.jump_duration,
//            last_LtLn!!,
//            PowerCalc(EvrawForw,8,28),
//            PowerCalc(EvrawForw,8,35),
//            PowerCalc(EvrawSide,7,40)
//        )
        /** write to file */
        writePreProcLog(EventPreFinal(
            last_stop_1, last_gas_break_2, last_turn_3, last_jump_4, // events
            eventLine.condition_debug, // condition stage
            maxDuration, // now is just duration, previously has been like start time of event
            event.stop_duration, event.gas_break_duration, event.turn_duration, event.jump_duration, // events duration
            last_LtLn!!, // event coordinate
            PowerCalc(EvrawForw,2.0,12.0), // score by forward acceleration vector
            PowerCalc(EvrawForw,2.0,12.0), // score by forward acceleration vector
            PowerCalc(EvrawSide,3.0,12.0)  // score by side acceleration vector
        ))
        // Clear duration`s, reduce to 1, preparing to new event
        event.stop_duration      = 1
        event.gas_break_duration = 1
        event.turn_duration      = 1
        event.jump_duration      = 1
        // Clear sum  [its for Scoring]
        EvsumForw = 0.0
        EvsumSide = 0.0
        EvsumVert = 0.0
        // Clear acceleration axis's [its for Scoring]
        EvrawForw = 0.0
        EvrawSide = 0.0
        EvrawVert = 0.0
    }

    /*
    Set last events to future compare
     */
    last_stop_1      =  eventLine.stop_1
    last_gas_break_2 =  eventLine.gas_break_2
    last_turn_3      =  eventLine.turn_3
    last_jump_4      =  eventLine.jump_4

    ///////////////
    var maxDurationSAVER = arrayListOf<Int>(event.stop_duration, event.gas_break_duration, event.turn_duration, event.jump_duration).maxOrNull()

//    SAVER_Event_Container =  EventPreFinal(
//        last_stop_1, last_gas_break_2, last_turn_3, last_jump_4,eventLine.condition_debug,
//        TIME -maxDurationSAVER!!,
//        event.stop_duration, event.gas_break_duration, event.turn_duration, event.jump_duration,
//        last_LtLn!!,
//        PowerCalc(EvrawForw,8,28),
//        PowerCalc(EvrawForw,8,35),
//        PowerCalc(EvrawSide,7,40)
//    )
}

/**
 * Calculate score by whole trip, Alexander`s algorithm [its for Scoring]
 */
//var MIN_SPECIAL = 100.0
//var MINUS = 5.0
fun PowerCalc(ind : Double,old_min : Double,old_max : Double): Double {
    var new_min=1.0;
    var new_max=10.0;
    var res = 0.0

    //var new= new_max -((ind-old_min) * ((new_max-new_min) / (old_max-old_min)));
    var new= new_max -(ind-old_min) * ((new_max-new_min) / (old_max-old_min));
    Log.i("mmm","mark power calc new: ${new}")
    if(new < 1.0) {
        res=1.0;
    } else {
        if(new>10.0 ){
            res=10.0;
        } else {
            res=new;
        }
    }


    return res
}
//fun compareLogs3 (eventLine: EventLine, lat: Float, lon : Float) { // without any compress ,like raw may be present
//    TIME++
//    needPublishLog = false
//
//    //stop_1
//    //gas_break_2
//    //turn_3
//    //jump_4
//    if (last_stop_1 == eventLine.stop_1) {
//        event.stop_duration++
//    } else {
//        needPublishLog = true
//    }
//
//    if (last_gas_break_2 == eventLine.gas_break_2 && !needPublishLog) {
//        event.gas_break_duration++
//    }  else {
//        needPublishLog = true
//    }
//
//    if (last_turn_3 == eventLine.turn_3 && !needPublishLog) {
//        event.turn_duration++
//    }  else {
//        needPublishLog = true
//    }
//
//    if (last_jump_4 == eventLine.jump_4 && !needPublishLog) {
//        event.jump_duration++
//    } else {
//        needPublishLog = true
//    }
//
//
//    ////
//    if (needPublishLog) {
//
//
//        var maxDuration = arrayListOf<Int>(event.stop_duration, event.gas_break_duration, event.turn_duration, event.jump_duration).maxOrNull()
//        if (maxDuration == null) { maxDuration = 1000000 } // just for test\\\
//
//
//        var asd = EventPreFinal(
//            last_stop_1,
//            last_gas_break_2,
//            last_turn_3,
//            last_jump_4,eventLine.condition_debug,
//
//            if ( TIME == 0) 0 else TIME -maxDuration,
//
//            event.stop_duration,
//            event.gas_break_duration,
//            event.turn_duration,
//            event.jump_duration,
//            last_LtLn
//        )
//
//
//        writePreProcLog(asd)
//
//        event.stop_duration      = 1
//        event.gas_break_duration = 1
//        event.turn_duration      = 1
//        event.jump_duration      = 1
//    }
//    last_stop_1      =  eventLine.stop_1
//    last_gas_break_2 =  eventLine.gas_break_2
//    last_turn_3      =  eventLine.turn_3
//    last_jump_4      =  eventLine.jump_4
//}

fun logMB (msg: String) {
    //Log.i("algorithm","algorithm: ${msg}")
}




fun writePreProcLog(s: String) {
    Log.i("ccc","ccc already writed")
    try {
        //val root2 = File(Environment.getExternalStorageDirectory(), "PreProcessing") // and folder
        if (!root2_preproc.exists()) {
            root2_preproc.mkdirs()
        }
        val file = File(root2_preproc, GENERATE_SPECIAL_ID_FOR_EVENTS_2)

        val fileOutputStream = FileOutputStream(file,true)
        val outputStreamWriter = OutputStreamWriter(fileOutputStream)
        outputStreamWriter.appendLine(s)

        outputStreamWriter.close()
        fileOutputStream.close()
        //findAndReplacePartOfText(file)

    } catch (e: IOException) {
        Log.e("ccc","ERROR "+ e.message)
        e.printStackTrace()
    }
}
// write to File
fun writePreProcLog(s: EventPreFinal) {
    Log.i("ccc","ccc already writed:= ${s}")
    try {
        if (!root2_preproc.exists()) {
            root2_preproc.mkdirs()
        }
        GENERATE_SPECIAL_ID_FOR_EVENTS_2 = generateNameOfLogEvents()

        val file = File(root2_preproc, GENERATE_SPECIAL_ID_FOR_EVENTS_2)

        val fileOutputStream = FileOutputStream(file,true)
        val outputStreamWriter = OutputStreamWriter(fileOutputStream)
        // check we have logs with score or not
        if (is_SCORING!!){
            outputStreamWriter.appendLine("${s.stop_1},${s.gas_break_2},${s.turn_3},${s.jump_4} ${s.condition_debug} ${s.time} ${s.stop_duration},${s.gas_break_duration},${s.turn_duration},${s.jump_duration} ${checkZeroOrNot(s.ltln.lat)},${checkZeroOrNot(s.ltln.lon)} ${roundTo2decimals(s.Gas.toFloat())},${roundTo2decimals(s.Break.toFloat())},${roundTo2decimals(s.Turn.toFloat())} ${VariablesAndConstRawParser.TIME_OF_HAPPENED_EVENT}")
        } else {
            outputStreamWriter.appendLine("${s.stop_1},${s.gas_break_2},${s.turn_3},${s.jump_4} ${s.condition_debug} ${s.time} ${s.stop_duration},${s.gas_break_duration},${s.turn_duration},${s.jump_duration} ${checkZeroOrNot(s.ltln.lat)},${checkZeroOrNot(s.ltln.lon)} ${VariablesAndConstRawParser.TIME_OF_HAPPENED_EVENT}")
        }

        outputStreamWriter.close()
        fileOutputStream.close()
        //findAndReplacePartOfText(file)
    } catch (e: IOException) {
        Log.e("ccc","ERROR "+ e.message)
        e.printStackTrace()
    }
}