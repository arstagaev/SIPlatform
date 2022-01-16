package com.avtelma.backblelogger.logrecorder.service

import android.os.Environment
import android.util.Log
import com.avtelma.backblelogger.logrecorder.tools.VariablesAndConstants.Companion.NAME_OF_FOLDER_LOGS
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter

fun addLogsIMUandGPS(sFileName: String, sBody: String){
    try {
        val root = File(Environment.getExternalStorageDirectory(), NAME_OF_FOLDER_LOGS)
        if (!root.exists()) {
            root.mkdirs()
        }
        val file = File(root, sFileName)

        val fileOutputStream = FileOutputStream(file,true)
        val outputStreamWriter = OutputStreamWriter(fileOutputStream)
        outputStreamWriter.append(""+sBody)

        outputStreamWriter.close()
        fileOutputStream.close()
        //findAndReplacePartOfText(file)

    } catch (e: IOException) {
        Log.e("ccc","ERROR "+ e.message)
        e.printStackTrace()
    }

}
fun addLogsXYZ(sFileName: String, sBody: String){
    try {
        val root = File(Environment.getExternalStorageDirectory(), NAME_OF_FOLDER_LOGS)
        if (!root.exists()) {
            root.mkdirs()
        }
        val file = File(root, sFileName)

        val fileOutputStream = FileOutputStream(file,true)
        val outputStreamWriter = OutputStreamWriter(fileOutputStream)
        outputStreamWriter.append(""+sBody)

        outputStreamWriter.close()
        fileOutputStream.close()
        //findAndReplacePartOfText(file)

    } catch (e: IOException) {
        Log.e("ccc","ERROR "+ e.message)
        e.printStackTrace()
    }

}

fun addLogsRawData(sFileName: String, sBody: String){
    try {
        val root = File(Environment.getExternalStorageDirectory(), NAME_OF_FOLDER_LOGS)
        if (!root.exists()) {
            root.mkdirs()
        }
        val file = File(root, sFileName)

        val fileOutputStream = FileOutputStream(file,true)
        val outputStreamWriter = OutputStreamWriter(fileOutputStream)
        outputStreamWriter.append(""+sBody+"\n")

        outputStreamWriter.close()
        fileOutputStream.close()
        //findAndReplacePartOfText(file)

    } catch (e: IOException) {
        Log.e("ccc","ERROR "+ e.message)
        e.printStackTrace()
    }

}

fun addLogsGPS(sFileName: String, sBody: String){
    try {
        val root = File(Environment.getExternalStorageDirectory(), NAME_OF_FOLDER_LOGS)
        if (!root.exists()) {
            root.mkdirs()
        }
        val file = File(root, sFileName)

        val fileOutputStream = FileOutputStream(file,true)
        val outputStreamWriter = OutputStreamWriter(fileOutputStream)
        outputStreamWriter.append(""+sBody)

        outputStreamWriter.close()
        fileOutputStream.close()
        //findAndReplacePartOfText(file)

    } catch (e: IOException) {
        Log.e("ccc","ERROR "+ e.message)
        e.printStackTrace()
    }

}