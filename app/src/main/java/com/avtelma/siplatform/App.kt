package com.avtelma.siplatform

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import com.avtelma.backblelogger.AVTSIPlatform_EntryPoint
import com.avtelma.backblelogger.AVTSIPlatform_EntryPoint.Companion.RECORD_ACTIVITY
import com.avtelma.backblelogger.AVTSIPlatform_EntryPoint.Companion.RECORD_ACTIVITY_FOR_RAWPARSER
import com.avtelma.backblelogger.enum.ConnectingStyle

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        RECORD_ACTIVITY               = MainActivity::class.java
        RECORD_ACTIVITY_FOR_RAWPARSER = MainActivity::class.java

        AVTSIPlatform_EntryPoint().setup(ConnectingStyle.MANUAL)
    }
}