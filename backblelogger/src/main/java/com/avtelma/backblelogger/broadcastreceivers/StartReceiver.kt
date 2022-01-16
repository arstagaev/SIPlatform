package com.avtelma.backblelogger.broadcastreceivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.avtelma.backblelogger.logrecorder.service.ServiceState
import com.avtelma.backblelogger.logrecorder.service.getServiceState
import com.avtelma.backblelogger.enum.Actions
import com.avtelma.backblelogger.logrecorder.tools.log
import com.avtelma.backblelogger.logrecorder.service.EndlessService

class StartReceiver : BroadcastReceiver() {
    // for notifications
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED && getServiceState(context) == ServiceState.STARTED) {
            Intent(context, EndlessService::class.java).also {
                it.action = Actions.START.name
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    log("Starting the service in >=26 Mode from a BroadcastReceiver")
                    context.startForegroundService(it)
                    return
                }
                log("Starting the service in < 26 Mode from a BroadcastReceiver")
                context.startService(it)
            }
        }
    }
}
