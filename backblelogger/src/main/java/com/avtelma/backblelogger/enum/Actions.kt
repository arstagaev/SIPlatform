package com.avtelma.backblelogger.enum

enum class Actions {
    START,              // global goal: start service
    STOP,               // global goal: stop  service
    UNBOND,             // aim is unbond current device
    NEUTRAL_CONNECTED,  // aim is stop rec
    SUBS_AND_CONNECTED, // aim is make rec again
    SCAN_START,         // aim is start scan
    SCAN_STOP,          // aim is stop scan
    MISC
}
