package com.avtelma.backblelogger.enum

enum class CurrentStateOfRecording(s: String) {
    NO_CONNECTED("no connected"),
    CONNECTING("connecting"),
    CONNECTED_BUT_NO_RECORDING("no recording"),
    RECORDING("recording.."),
    LOSS_CONNECTION_AND_WAIT_NEW("loss connection"),SAVING_PLEASE_WAIT("saving.."),
    UNBONDING("unbonding")
}