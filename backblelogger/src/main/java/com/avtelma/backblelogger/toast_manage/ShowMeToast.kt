package com.avtelma.backblelogger.toast_manage

import android.content.Context
import android.widget.Toast

fun tost(message: String, ctx : Context, isLongLength : Boolean, isPrioritized: Boolean){

    if (isPrioritized) {
        Toast.makeText(ctx,if (message == null){ "null" } else { message },if (isLongLength) { Toast.LENGTH_LONG} else {Toast.LENGTH_SHORT} ).show()
    } else {
        Toast.makeText(ctx,if (message == null){ "null" } else { message },if (isLongLength) { Toast.LENGTH_LONG} else {Toast.LENGTH_SHORT} ).show()
    }

}