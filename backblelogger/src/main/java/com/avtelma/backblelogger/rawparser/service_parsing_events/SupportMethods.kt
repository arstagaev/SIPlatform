package com.avtelma.backblelogger.rawparser.service_parsing_events

import android.util.Log
import com.avtelma.backblelogger.rawparser.service_parsing_events.models.LtLn
import com.avtelma.backblelogger.rawparser.tools.VariablesAndConstRawParser
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import kotlin.math.abs

// LOCATION:

fun getNormalCoordinates(strLt : String, strLn : String): LtLn {
    if (strLt == null || strLt == "NaN") {
        return LtLn(0.0,0.0)
    }else {
        return LtLn(strLt.toDouble(),strLn.toDouble())
    }
}

fun checkZeroOrNot(coordination : Double) :String {
    if (coordination == 0.0) {
        return "~"
    } else {
        return coordination.toString()
    }
}

//////
/**
 * I Remove logs by position of tablet (by coefficients of accelerometer)
 */
fun clearJustOldLogs(file: File,tailRaw : String) {
    var isExist = file.exists()
    Log.i("go for delete . is Exist: ${isExist}", " " + file.name +"<<< ${tailRaw}")
    if (!isExist) {
        return
    }
    //Read text from file
    //VariablesAndConst.TARGET_NAME_FILE_IMU_and_GPS = file.name
    VariablesAndConstRawParser.PROGRESS_MAX = 0
    var NUMBER_OF_LINE = 0
    //val text = StringBuilder()
    Log.i("go for delete", " " + file.name)
    val reader = BufferedReader(FileReader(file))

    //var arrayXYZ = arrayListOf<XYZ>()
    var arrayX = arrayListOf<Float>()
    var arrayY = arrayListOf<Float>()
    var arrayZ = arrayListOf<Float>()

    // read how many lines in file:
//    while (reader.readLine() != null) VariablesAndConstRawParser.PROGRESS_MAX++
//    reader.close()

    try {
        // read lines in txt by Bufferreader

        val br = BufferedReader(FileReader(file))
        var line: String?

        while (br.readLine().also { line = it } != null) {
            //Log.i("vvv","l "+line)
            if (line != "" || line != " ") {
                val items = line?.split(";"," ")?.toTypedArray()
                // we create pool of xyz array in 500 elements  // 2907_212603_imu_gps.txt has been error
                if (items != null) {

                    //if (NUMBER_OF_LINE % 10 == 0) {
                        arrayX.add( items[0].toFloat())
                        arrayY.add( items[1].toFloat())
                        arrayZ.add( items[2].toFloat())
                        if (arrayX.size > 20)
                        {
                            if (arrayX.average() > 0.30 && abs(arrayY.average()) <= 0.1 && arrayZ.average() <= -0.78){ // SPECIAL CONDITION FOR POSITION OF TABLET
                                br.close()
                                Log.d("ccc", ">>>>> ITS REAL LOG! ${file.name}")

                            }else {
                                br.close()
                                Log.e("ccc", "xxxxxx ITS NOTREAL LOG! ${file.name}")
                                file.delete()

                                var fileRaw = File("${VariablesAndConstRawParser.root1_raw}/${tailRaw}_raw_bytes.txt")
                                if (fileRaw.exists()) {
                                    fileRaw.delete()
                                }else {
                                    Log.e("ccc", ">>>> error dont have ~${fileRaw.name}")
                                }

                            }

                            return
                        }
                    //}


                    NUMBER_OF_LINE++
                }
            }
        }
        br.close()
    } catch (e: IOException) {
        Log.e("ccc", ">> error +${e.message} // ${file.name} Supp methods")
        Log.e("ccc", ">> error +${e.message} // ${file.name}")
        Log.e("ccc", ">> error +${e.message} // ${file.name}")
        //arrayListOfSpeed.clear()
        //You'll need to add proper error handling here
    }
}

fun findFileEvenHeChanged(pieceOfName : String, ) {


}