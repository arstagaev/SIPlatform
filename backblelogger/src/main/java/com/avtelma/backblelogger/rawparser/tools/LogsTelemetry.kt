package com.avtelma.backblelogger.rawparser.tools


import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.Log
import android.widget.TextView
import android.widget.Toast

fun log(msg : String){
    Log.i("ccc","$msg")
}

fun toastShow(msg: String, colorText : Int , ctx : Context){
    try{
        val toast = Toast.makeText(ctx, "${msg}", Toast.LENGTH_LONG)
        val view = toast.view
        view!!.background.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN)
        val text = view!!.findViewById<TextView>(android.R.id.message)
        text.setTextColor(colorText)
        toast.show()
    }catch (exc :Exception) {
        Log.e("eee","eee fun toastShow( ${exc.message}")
        Toast.makeText(ctx,msg,Toast.LENGTH_SHORT).show()
    }


}
