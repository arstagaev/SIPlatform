package com.avtelma.backblelogger.rawparser.service_parsing_events

import android.content.Context
import android.content.SharedPreferences

enum class ServiceStateRawParser {
    STARTED,
    STOPPED,
}

private const val name = "SPYSERVICE_KEY"
private const val key = "SPYSERVICE_STATE"

fun setServiceState(context: Context, state: ServiceStateRawParser) {
    val sharedPrefs = getPreferences(context)
    sharedPrefs.edit().let {
        it.putString(key, state.name)
        it.apply()
    }
}

fun getServiceState(context: Context): ServiceStateRawParser {
    val sharedPrefs = getPreferences(context)
    val value = sharedPrefs.getString(key, ServiceStateRawParser.STOPPED.name)
    return ServiceStateRawParser.valueOf(value!!)
}

private fun getPreferences(context: Context): SharedPreferences {
    return context.getSharedPreferences(name, 0)
}
