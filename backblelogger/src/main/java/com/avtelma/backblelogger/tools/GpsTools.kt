package com.avtelma.backblelogger.tools

fun azimuthToDirections(azimuth : Float) : String {

    when(azimuth){
        0.0F -> {return "--"}
        in 0F..11.25F, in 348.75F..360F ->   { return "N"   }
        in 11.25F..33.75F   ->  { return "NNE" }
        in 33.75F..56.25F   ->  { return "NE"  }
        in 56.25F..78.75F   ->  { return "ENE" }

        in 78.75F..101.25F  ->  { return "E"   }
        in 101.25F..123.75F ->  { return "ESE" }
        in 123.75F..146.25F ->  { return "SE"  }
        in 146.25F..168.75F ->  { return "SSE" }

        in 168.75F..191.25F ->  { return "S"   }
        in 191.25F..213.75F ->  { return "SSW" }
        in 213.75F..236.25F ->  { return "SW"  }
        in 236.25F..258.75F ->  { return "WSW" }

        in 258.75F..281.25F ->  { return "W"   }
        in 281.25F..303.75F ->  { return "WNW" }
        in 303.75F..326.25F ->  { return "NW"  }
        in 326.25F..348.75F ->  { return "NNW" }

        else -> {
            return "NON"
        }
    }
}

fun speedMeterPerSecondToKmH(speed: Float): Int{
    return (speed*3.6F).toInt()
}