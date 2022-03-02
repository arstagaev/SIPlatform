package com.avtelma.siplatform.aport

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import com.avtelma.backblelogger.AVTSIPlatform_EntryPoint
import com.avtelma.backblelogger.enum.Actions
import com.avtelma.backblelogger.enum.ConnectingStyle
import com.avtelma.backblelogger.logrecorder.service.EndlessService
import com.avtelma.backblelogger.logrecorder.tools.VariablesAndConstants.Companion.ACTION_NOW
import com.avtelma.backblelogger.logrecorder.tools.VariablesAndConstants.Companion.CONNECTING_STYLE
import com.avtelma.backblelogger.logrecorder.tools.VariablesAndConstants.Companion.CURRENT_STATE_OF_SERVICE
import com.avtelma.backblelogger.logrecorder.tools.VariablesAndConstants.Companion.SUPER_BLE_DEVICE
import com.avtelma.backblelogger.logrecorder.tools.log
import com.avtelma.backblelogger.rawparser.service_parsing_events.ParsingActions
import com.avtelma.backblelogger.rawparser.service_parsing_events.ParsingEventService
import com.avtelma.siplatform.BIG_SHARED_STR
import com.avtelma.siplatform.timerOfStatus
import com.avtelma.siplatform.whatCommandsWeHave

class Router {

    @SuppressLint("MissingPermission")
    fun inner(command: String, ctx : Context){
        when(command) {
            "status" ,"0","s"->  { BIG_SHARED_STR.value += "\n>> state:${CURRENT_STATE_OF_SERVICE.name},act:${ACTION_NOW.name},style:${CONNECTING_STYLE.name},aim:${SUPER_BLE_DEVICE?.name ?: "null"}" }
            "info" ,"1"->  { whatCommandsWeHave() }
            "clear" ,"2","c"-> { BIG_SHARED_STR.value = "" }
            "startr" ,"3"-> {
                AVTSIPlatform_EntryPoint.Builder.connStl(ConnectingStyle.AUTO_BY_BOND).build()
                launchCommandInService(Actions.START, ctx)
            }
            "stopr" ,"4"->  {
                //AVTSIPlatform_EntryPoint.Builder.connStl(ConnectingStyle.AUTO_BY_BOND).build()
                launchCommandInService(Actions.STOP, ctx)
            }
            "fstop","fstopr","5" ->  {
                //AVTSIPlatform_EntryPoint.Builder.connStl(ConnectingStyle.AUTO_BY_BOND).build()
                launchCommandInService(Actions.FORCE_STOP, ctx)
            }
            "stl.bnd","6" ->  { AVTSIPlatform_EntryPoint.Builder.connStl(ConnectingStyle.AUTO_BY_BOND).build() }
            "stl.mnl","7" ->  {  AVTSIPlatform_EntryPoint.Builder.connStl(ConnectingStyle.MANUAL).build()      }

            "startpf","8" ->  { launchCommandInService_RAWPARSER(ParsingActions.FULL_PARSING,ctx)  }
            "startpt","9" ->  { launchCommandInService_RAWPARSER(ParsingActions.TARGET_PARSING,ctx)  }
            "stopp","10" ->  {  launchCommandInService_RAWPARSER(ParsingActions.STOP,ctx) }
            "log","11" -> { BIG_SHARED_STR.value = "[${CURRENT_STATE_OF_SERVICE.name}, ${CONNECTING_STYLE.name}]"  }
            "timer","12" -> {
                BIG_SHARED_STR.value += "\nrefresher of status started"
                timerOfStatus.start()

            }
            "13" -> {
                timerOfStatus.cancel()
            }
            "unbond" -> { launchCommandInService(Actions.UNBOND, ctx)  }
            else -> {BIG_SHARED_STR.value += "\ndon`t know command :(" }
        }
    }

    fun launchCommandInService(action : Actions, ctx: Context) {
        Intent(ctx, EndlessService::class.java).also {
            it.action = action.name
            //it.putExtra("CS",6)


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                log("Starting the service in >=26 Mode")
                ContextCompat.startForegroundService(ctx , it)
                return
            }
            log("Starting the service in < 26 Mode")
            ctx.startService(it)
        }
    }



    fun launchCommandInService_RAWPARSER(parsingAction : ParsingActions,ctx: Context) {
        Intent(ctx, ParsingEventService::class.java).also {
            it.action = parsingAction.name
            //it.putExtra("CS",6)


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                log("Starting the service in >=26 Mode")
                ContextCompat.startForegroundService(ctx,it)
                return
            }
            log("Starting the service in < 26 Mode")
            ctx.startService(it)
        }
    }

}