package com.avtelma.backblelogger.tools

import android.util.Log

class Configurator {
    var is_ENABLE_REALTIME_CHART = true
        set(value) {
            Log.w("sss","is_ENABLE_REALTIME_CHART configuration changed to $value")
            field = value
        }


}