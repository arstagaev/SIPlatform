package com.avtelma.siplatform

import android.app.Application
import com.avtelma.backblelogger.tools.InputSession.Companion.RECORD_ACTIVITY

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        RECORD_ACTIVITY = MainActivity::class.java
    }
}