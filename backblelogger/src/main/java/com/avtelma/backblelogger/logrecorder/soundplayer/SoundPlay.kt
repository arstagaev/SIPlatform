package com.avtelma.backblelogger.logrecorder.soundplayer

import android.content.Context
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.avtelma.backblelogger.R


class SoundPlay {



    @RequiresApi(Build.VERSION_CODES.M)
    fun playx(context: Context, whatIMustSay: WhatIMustSay){
        try {
            when(whatIMustSay){
                WhatIMustSay.DING -> {
                    var disconectSuccess = MediaPlayer.create(context, R.raw.air_ding)
                    val newPlaybackParams = PlaybackParams()
                    newPlaybackParams.speed = 1.0F
                    disconectSuccess.playbackParams = newPlaybackParams
                }
                WhatIMustSay.ON -> {
                    var connectSuccess =   MediaPlayer.create(context, R.raw.autopilot_on)

                    val newPlaybackParams = PlaybackParams()
                    newPlaybackParams.speed = 1.0F
                    connectSuccess.playbackParams = newPlaybackParams
                }
                WhatIMustSay.OFF -> {
                    var disconectSuccess = MediaPlayer.create(context, R.raw.autopilot_off)
                    val newPlaybackParams = PlaybackParams()
                    newPlaybackParams.speed = 1.0F
                    disconectSuccess.playbackParams = newPlaybackParams
                }

                WhatIMustSay.ERROR -> {
                    var errorAudio = MediaPlayer.create(context, R.raw.error)
                    val newPlaybackParams = PlaybackParams()
                    newPlaybackParams.speed = 1.0F
                    errorAudio.playbackParams = newPlaybackParams
                }

                WhatIMustSay.START_REC -> {
                    var startRec = MediaPlayer.create(context, R.raw.started_logs_may_driving)
                    val newPlaybackParams = PlaybackParams()
                    newPlaybackParams.speed = 1.0F
                    startRec.playbackParams = newPlaybackParams
                }

                WhatIMustSay.ATTENTION_OVERSPEED_60 -> {
                    var startRec = MediaPlayer.create(context, R.raw.overspeed_60mph)
                    val newPlaybackParams = PlaybackParams()
                    newPlaybackParams.speed = 1.0F
                    startRec.playbackParams = newPlaybackParams
                }

            }
        }catch ( e: Exception){
            Log.e("ERROR","ERROR, sound play: ${e.message}")
        }



    }
}


