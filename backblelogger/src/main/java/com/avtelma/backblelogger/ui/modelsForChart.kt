package com.avtelma.backblelogger.ui

data class FourthlyDataContainerForChartsXYZ(
    val date : ArrayList<String>,
    val valuesX : ArrayList<Float>,
    val valuesY : ArrayList<Float>,
    val valuesZ: ArrayList<Float>
)

data class FourthlyDataContainerForChartsXYZ2(
    val date :    String,
    val valuesX : Float,
    val valuesY : Float,
    val valuesZ:  Float
)

data class FirstChartDataTransitor (
    val dates : ArrayList<String>,
    val values : ArrayList<Float>
)