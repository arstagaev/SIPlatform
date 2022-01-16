package com.avtelma.backblelogger.broadcastreceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.avtelma.backblelogger.enum.Actions
import com.avtelma.backblelogger.rawparser.service_parsing_events.ParsingEventService
import com.avtelma.backblelogger.logrecorder.service.EndlessService

class CloseServiceReceiver_RawParser : BroadcastReceiver(){

    override fun onReceive(context: Context, intent: Intent) {
        //if (getServiceState(this) == ServiceState.STOPPED && action == Actions.STOP) return
        Log.i("ccc","ccc  START!!!!!")
        //log("###############Start !!!!!!!!!!!!!!!!")

        //context.sendBroadcast(Intent("call"));

        Intent(context, ParsingEventService::class.java).also {
            it.action = Actions.STOP.name

            // EndlessService().bleManager?.notifyCharacteristic(true, UUID.fromString("74ab521e-060d-26df-aa64-cf4df2d0d643"))

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //log("Starting the service in >=26 Mode")
                context.startForegroundService(it)
                return
            }

            //log("Starting the service in < 26 Mode")
            context.startService(it)
        }
    }

}