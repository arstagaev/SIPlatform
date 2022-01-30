package com.avtelma.backblelogger.rawparser.service_parsing_events

import android.util.Log
import com.avtelma.backblelogger.rawparser.tools.VariablesAndConstRawParser
import java.io.File
import kotlin.collections.ArrayList

/**
 * Specified of file disposition:
 *
 * Array ( PairFileAndName [ File ; pieceFileName ] )
 *
 */

data class PairFileAndName(val filex : File, val pieceFileName : String)
fun getFilesFromFolderRaw(root : File) : ArrayList<PairFileAndName> {

    var arrayListOfNAMEFiles = arrayListOf<String>()
    var arrayOfFiles_output = arrayListOf<PairFileAndName>()

    val files = root.listFiles()
    Log.d("Files", "Size: " + files.size+ " address: ${root.absolutePath}")

    for (i in files) {

        if (i.name.contains("imu_gps",true)) {
            //arrayListOfNAMEFiles.add(i.name.take(11)) // first 10 letters of world

            arrayOfFiles_output.add(
                PairFileAndName(i,i.name.take(11))
            )
            Log.d("Files", "FileName:"+ " ;; "+i.name.take(10))
        }


    }
    //return arrayListOfNAMEFiles
    return arrayOfFiles_output
}

fun getFilesFromFolderPreProc(root : File) : ArrayList<PairFileAndName> {
    if (!VariablesAndConstRawParser.root2_preproc.exists()) {
        VariablesAndConstRawParser.root2_preproc.mkdirs()
    }
    var arrayListOfFiles = arrayListOf<String>()

    var arrayOfFiles_output = arrayListOf<PairFileAndName>()

    val files = root.listFiles()
    Log.d("Files", "Size: " + files.size)

    for (i in files) {
        // 1412_160244_imu_gps.txt_e.txt
        //arrayListOfFiles.add(i.name.take(11)) // first 10 letters of world
        arrayOfFiles_output.add(
            PairFileAndName(i,i.name.take(11))
        )
        //Log.d("Files", "FileName:"+ " ;; "+files[i].name.take(10))
    }
    return arrayOfFiles_output
}


fun noAlreadyParsedFromRawFile(rawArray : ArrayList<File>, parsedArray : ArrayList<File>): MutableSet<File> {

    var outputArray = mutableSetOf<File>()

    for (i in rawArray) {

        if (!parsedArray.contains(i)) {
            outputArray.add(i)
        }

    }

    println("-> ${outputArray.joinToString()}")

    return outputArray
}

fun noAlreadyParsedFromRawString(rawArray : ArrayList<PairFileAndName>, parsedArray : ArrayList<PairFileAndName>): MutableSet<PairFileAndName> {

    var outputArray = mutableSetOf<PairFileAndName>()

    // if PreProc is empty
    if (parsedArray.size == 0) {
        for (i in rawArray) {
            outputArray.add(i)
        }

        println("parsedArray is EMPTY -> ${outputArray.joinToString()}")
        return outputArray
    }
    var wonderfullArray = mutableSetOf<String>()
//
    for (i in rawArray) {
        for (z in parsedArray) {
            if (i.pieceFileName == z.pieceFileName) {
                wonderfullArray.add(i.pieceFileName)
            }
        }
    }

    for (i in rawArray) {
        if (wonderfullArray.add(i.pieceFileName) == true) {
            outputArray.add(i)
        }
    }
   // outputArray = wonderfullArray

//    //fucking regular expression
//    for (i in rawArray) {
//
//        for (z in  parsedArray) {
////
////            if (
////                !z.pieceFileName.contains(i.pieceFileName,true)
//////                &&
//////                !parsedArray.contains(i)
////            //&& (i.contains("imu_gps",true) == true)
////            ) { //imu_gps
////                outputArray.add(i)
////            }
//
//        }

   // }

    println("parsedArray is Filled-> ${outputArray.joinToString()}")
    //PairFileAndName(filex=/storage/emulated/0/ItelmaBLE_Background/RawData/1412_122914_imu_gps.txt, pieceFileName=1412_122914)?

    return outputArray
}

//fun noAlreadyParsedFromRawString_byFile(rawArray : ArrayList<String>, parsedArray : ArrayList<String>): MutableSet<File> {
//
//    var outputArray = mutableSetOf<File>()
//
//    // if PreProc is empty
//    if (parsedArray.size == 0) {
//        for (i in rawArray) {
//            outputArray.add(i)
//        }
//
//        println("parsedArray is EMPTY -> ${outputArray.joinToString()}")
//        return outputArray
//    }
//
//    // regular expression
//    for (i in rawArray) {
//
//        for (z in  parsedArray) {
//
//            if (
//                !z.contains(i,true)
//                &&
//                !parsedArray.contains(i)
//            //&& (i.contains("imu_gps",true) == true)
//            ) { //imu_gps
//                outputArray.add(i)
//            }
//
//        }
//
//    }
//
//    println("parsedArray is Filled-> ${outputArray.joinToString()}")
//
//    return outputArray
//}