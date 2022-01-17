package com.avtelma.backblelogger.enum

enum class CurrentStateOfService(s: String) {
    //OFF("off"),
    NO_CONNECTED("no connected"),
    CONNECTING("connecting"),
    CONNECTED_BUT_NO_RECORDING("no recording"),
    RECORDING("recording.."),
    LOSS_CONNECTION_AND_WAIT_NEW("loss connection"),SAVING_PLEASE_WAIT("saving.."),
    UNBONDING("unbonding"),
    //WAIT_COMMAND_UNSUBS("wait_command")
}