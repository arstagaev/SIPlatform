package com.avtelma.backblelogger.rawparser.core.algorithm.models

data class ContainerForJSONWholeTrip(
    val timeInSeconds : Int,
    val lat : Float,
    val lon : Float
)

data class ContainerForJSONWholeTrip2(
    val timeInSeconds : Int,
    val lat : Double,
    val lon : Double
)
