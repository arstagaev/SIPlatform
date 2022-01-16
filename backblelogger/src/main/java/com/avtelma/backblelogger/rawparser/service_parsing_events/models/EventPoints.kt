package com.avtelma.backblelogger.rawparser.service_parsing_events.models


//import com.google.android.libraries.maps.model.LatLng
////import com.google.android.libraries.maps.model.StyleSpan

//data class EventPoint(
//    var ltln : LatLng,
//    var colorStart : Int,
//    var colorEnd : Int
//)


// below i commented in jan 2022
//data class EventPoints(
//    var ltln : LatLng,
//    var colors: StyleSpan,
//    var events: Events
//)
//
//data class EventPoint(
//    var ltlns : ArrayList<LatLng>,
//    var events  : ArrayList<Events>,
//    var colors: ArrayList<StyleSpan>
//)

data class Events (
    var stop     : Int,
    var gasBreak : Int,
    var turn     : Int,
    var jump     : Int
)