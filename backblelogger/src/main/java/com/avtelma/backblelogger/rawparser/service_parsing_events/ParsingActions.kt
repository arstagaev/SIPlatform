package com.avtelma.backblelogger.rawparser.service_parsing_events

enum class ParsingActions {
    START,
    STOP,
    FULL_PARSING, // every file parse
    TARGET_PARSING, // target file parse
    CLEAR_DESKTOP_LOG
}