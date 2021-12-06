package com.avtelma.backblelogger.enum

enum class TypeOfInputLog(s : String) {
    NO_LOG("no_logs"),
    START_REC  ("start_rec"),
    HISTORY    ("history"),      // from flash
    HISTORY_RAM("history_ram"),  // from ram
    REAL_TIME  ("real_time"),
}