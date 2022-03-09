package com.avtelma.backblelogger

import com.avtelma.backblelogger.enum.CurrentStateOfService
import com.avtelma.backblelogger.logrecorder.tools.VariablesAndConstants.Companion.CURRENT_STATE_OF_SERVICE
import com.avtelma.backblelogger.logrecorder.tools.VariablesAndConstants.Companion.SUPER_BLE_DEVICE
import com.avtelma.backblelogger.rawparser.tools.currentTimeDefiner
import org.junit.Assert
import org.junit.Test

class FirstTest {

    @Test
    fun addition_isCorrect() {
        Assert.assertEquals(null, SUPER_BLE_DEVICE)
        Assert.assertEquals(CurrentStateOfService.NO_CONNECTED, CURRENT_STATE_OF_SERVICE)
    }

    @Test
    fun exTime(){
       println(">>>"+ currentTimeDefiner(1646752800,10L))
    }

}
