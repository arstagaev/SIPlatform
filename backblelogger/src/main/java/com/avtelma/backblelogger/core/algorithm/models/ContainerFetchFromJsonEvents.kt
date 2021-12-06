package com.avtelma.backblelogger.core.algorithm.models

import com.google.android.libraries.maps.model.LatLng

data class ContainerFetchFromJsonEvents(
   val Time    : Float,
   val Type    : String,
   val Power   : String,
   val Duration: Int
)
data class ContainerOfMarksEvent(
   var latLng: LatLng,
   var type  : String
)
