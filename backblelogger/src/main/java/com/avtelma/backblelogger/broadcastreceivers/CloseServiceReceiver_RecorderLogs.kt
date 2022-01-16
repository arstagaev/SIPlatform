package com.avtelma.backblelogger.broadcastreceivers

import android.content.Intent

import android.content.BroadcastReceiver
import android.content.Context
import android.os.Build
import android.util.Log
import com.avtelma.backblelogger.enum.Actions
import com.avtelma.backblelogger.logrecorder.service.EndlessService


class CloseServiceReceiver_RecorderLogs : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        //if (getServiceState(this) == ServiceState.STOPPED && action == Actions.STOP) return
        Log.i("ccc","ccc  START!!!!!")
        //log("###############Start !!!!!!!!!!!!!!!!")

        //context.sendBroadcast(Intent("call"));
//        when(intent.action) {
//            is Actions -> {
//                print("")
//            }
//            is String -> {
//                print("")
//            }
//        }
        Intent(context, EndlessService::class.java).also {
            it.action = Actions.STOP.name

           // //Unbonding
           // EndlessService().bleManager?.notifyCharacteristic(true, UUID.fromString("74ab521e-060d-26df-aa64-cf4df2d0d643"))

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //log("Starting the service in >=26 Mode")
                context.startForegroundService(it)
                return
            }

            //log("Starting the service in < 26 Mode")
            context.startService(it)
        }



        //Toast.makeText(context,"recieved",Toast.LENGTH_SHORT).show();
//        val action = intent.getStringExtra("action")
//        if (action == "action1") {
//            performAction1()
//        } else if (action == "action2") {
//            performAction2()
//        }
//        //This is used to close the notification tray
//        val it = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
//        context.sendBroadcast(it)
    }


}