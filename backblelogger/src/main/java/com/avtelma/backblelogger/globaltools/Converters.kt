package com.avtelma.backblelogger.globaltools

import java.math.RoundingMode
import java.text.DecimalFormat

fun roundTo2decimals(num : Float) : Float{
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.CEILING

    return (df.format(num)).replace(",",".").toFloat()

}