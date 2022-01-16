import com.avtelma.backblelogger.rawparser.service_parsing_events.models.LtLn

// container for array of xyz
data class XYZ(
    var x : Double,
    var y : Double,
    var z : Double
)

// container for array of Events
data class EventLine(
    var stop_1      : Int,
    var gas_break_2 : Int,
    var turn_3      : Int,
    var jump_4      : Int,
    var condition_debug : Int,

    var stop_duration : Int,
    var gas_break_duration : Int,
    var turn_duration : Int,
    var jump_duration : Int,
//
//    var time : Int
)


data class EventPreFinal(
    var stop_1      : Int,
    var gas_break_2 : Int,
    var turn_3      : Int,
    var jump_4      : Int,
    var condition_debug : Int,

    var time : Int,

    var stop_duration : Int,
    var gas_break_duration : Int,
    var turn_duration : Int,
    var jump_duration : Int,

    var ltln : LtLn
)


//data class Data_Chank(
//    var vector : ArrayList<XYZ>
//)