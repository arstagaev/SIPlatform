package com.avtelma.siplatform

import android.app.Application
import com.avtelma.backblelogger.AVTSIPlatform_EntryPoint
import com.avtelma.backblelogger.enum.ConnectingStyle
import com.avtelma.backblelogger.tools.InputSession.Companion.RECORD_ACTIVITY

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        RECORD_ACTIVITY = MainActivity::class.java
        AVTSIPlatform_EntryPoint().setup(ConnectingStyle.MANUAL)
    }
}