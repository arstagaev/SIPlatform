package com.avtelma.siplatform

import android.app.Application
import com.avtelma.backblelogger.AVTSIPlatform_EntryPoint
import com.avtelma.backblelogger.enum.ConnectingStyle

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        //RECORD_ACTIVITY               = MainActivity::class.java
        //RECORD_ACTIVITY_FOR_RAWPARSER = MainActivity::class.java
        AVTSIPlatform_EntryPoint.Builder
            .realtimeChart(true)
            .deleteNoTripLogs(false)
            .scoring(isScoring = true)
            .connStl(ConnectingStyle.AUTO_BY_BOND)
            .recAct(MainActivity::class.java)
            .prsAct(MainActivity::class.java)
            .startupDelay(10000L)
            .build()
        //AVTSIPlatform_EntryPoint().setup(ConnectingStyle.MANUAL)
    }
}