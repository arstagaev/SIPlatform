package com.avtelma.backblelogger.logrecorder.nordicble

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.Context
import android.util.Log
import com.avtelma.backblelogger.logrecorder.tools.Converters
import com.avtelma.backblelogger.logrecorder.tools.Converters.Companion.bytesToHex
import com.avtelma.backblelogger.logrecorder.tools.VariablesAndConstants.Companion.CODE_INDICATE_DISABLED_1
import com.avtelma.backblelogger.logrecorder.tools.VariablesAndConstants.Companion.CODE_INDICATE_ENABLED_1
import com.avtelma.backblelogger.logrecorder.tools.VariablesAndConstants.Companion.CODE_NOTIFY_DISABLED_1
import com.avtelma.backblelogger.logrecorder.tools.VariablesAndConstants.Companion.CODE_NOTIFY_ENABLED_1
import no.nordicsemi.android.ble.BleManager
import no.nordicsemi.android.ble.BleManagerCallbacks
import no.nordicsemi.android.log.LogContract
import no.nordicsemi.android.log.LogSession
import no.nordicsemi.android.log.Logger
import java.util.*
import kotlin.collections.HashMap

open class BaseNordicBleManager(context: Context) :
    BleManager<BaseNordicBleManager.ConnectedLECallback>(context) {

    var serviceList: MutableList<BluetoothGattService> = mutableListOf()
    var characteristicMap: HashMap<UUID, BluetoothGattCharacteristic> = hashMapOf()

    var logSession: LogSession? = null

    protected var callback: BleManagerGattCallback = object : BleManagerGattCallback() {
        override fun onDeviceDisconnected() {
        }

        /**
         * BleManagerCallback - onServiceDiscovered 보다 먼저 호출됨
         */
        override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
            serviceList.clear()
            characteristicMap.clear()

            serviceList = gatt.services

            serviceList.forEach { bluetoothGattService ->
                bluetoothGattService.characteristics.forEach { characteristic ->
                    run {
                        characteristicMap[characteristic.uuid] = characteristic
                    }
                }
            }

            mCallbacks.updateServices(serviceList)

            return true
        }
    }

    public fun indicateCharacteristic(requestToMakeEnable: Boolean, uuid: UUID) {
        val characteristic = characteristicMap[uuid]
        var isALREADY_ENABLED = false

        try {
            Log.w("blechk",">>> blechk ${characteristic!!.descriptors.map {
                if (it.value != null) {
                    bytesToHex( it.value)

                }else {
                    it.value
                }
            } } //")
            if (characteristic!!.descriptors.get(0).value == null || bytesToHex(characteristic!!.descriptors.get(0).value) == CODE_INDICATE_DISABLED_1) {
                isALREADY_ENABLED = false
            } else if (bytesToHex(characteristic!!.descriptors.get(0).value) == CODE_INDICATE_ENABLED_1) {
                isALREADY_ENABLED = true
            }

        }catch (e: Exception) {}
        if (requestToMakeEnable == isALREADY_ENABLED) {
            Log.e("blechk","garbage request to enable notify<<<<<<<<<<<<<<<<<")
            return
        }

        Log.w("blechk","blechk 2. ${characteristic!!.writeType} //")
        Log.w("blechk","blechk 3. ${characteristic!!.properties} //")

        //Refreshing of new data: ->
        setIndicationCallback(characteristic).with { device, data ->
            Log.d("cccallback", "notifyCharacteristic -> $uuid")
            mCallbacks.onIndicated(
                uuid,
                data.value
            )
        }

        //callback checker is ON or OFF
        if (requestToMakeEnable) {
            enableIndications(characteristic)
                .done { device ->           mCallbacks.onIndicated(uuid, null, "set up")  }
                .fail { device, status ->  mCallbacks .onIndicated(uuid,  null, "fail")   }
                .enqueue()
        } else {
            disableIndications(characteristic)
                .done { device ->          mCallbacks.onIndicated(uuid,  null, "end")     }
                .fail { device, status ->  mCallbacks.onIndicated(uuid,  null, "fail")    }
                .enqueue()
        }

        try {
            Log.w("blechk","blechk ${characteristic!!.descriptors.map {
                if (it.value != null) {
                    bytesToHex( it.value)
                }else {
                    it.value
                }
            } } <<<//")
        }catch (e: Exception) {}
        Log.w("blechk","blechk 2. ${characteristic!!.writeType} //")
        Log.w("blechk","blechk 3. ${characteristic!!.properties} //")

    }

    override fun setGattCallbacks(callbacks: ConnectedLECallback) {
        super.setGattCallbacks(callbacks)

    }
    public fun notifyCharacteristic(requestToMakeEnable: Boolean, uuid: UUID) {
        val characteristic = characteristicMap[uuid]
        var isALREADY_ENABLED = false

        setNotificationCallback(characteristic).with { device, data ->

            Log.d("cccallback", "notifyCharacteristic -> $uuid")
            mCallbacks.onNotified(
                uuid,
                data.value
            )
        }

        try {
            Log.w("blechk",">>> blechk ${characteristic!!.descriptors.map { 
                if (it.value != null) {
                    bytesToHex( it.value)
                    
                }else {
                    it.value
                }
            } } //")
            if (characteristic!!.descriptors.get(0).value == null || bytesToHex(characteristic!!.descriptors.get(0).value) == CODE_NOTIFY_DISABLED_1) {
                isALREADY_ENABLED = false
            } else if (bytesToHex(characteristic!!.descriptors.get(0).value) == CODE_NOTIFY_ENABLED_1) {
                isALREADY_ENABLED = true
            }

        }catch (e: Exception) {}
        if (requestToMakeEnable == isALREADY_ENABLED) {
            Log.e("blechk","garbage request to enable notify<<<<<<<<<<<<<<<<<")
            return
        }

        Log.w("blechk","blechk 2. ${characteristic!!.writeType} //")
        Log.w("blechk","blechk 3. ${characteristic!!.properties} //")
        //Log.w("blechk","blechk 4. ${characteristic.value.map { it.toString() }} //")
//        isALREADY_ENABLED = characteristic!!.descriptors.map {
//            if (it.value != null) {
//
//                bytesToHex( it.value)
//            }else {
//                false
//            }
//        }


        if (requestToMakeEnable) {
            enableNotifications(characteristic)
                .done { device ->           mCallbacks.onNotified(uuid, null, "set up")  }
                .fail { device, status ->  mCallbacks.onNotified(uuid,  null, "fail")  }
                .enqueue()
        } else {
            disableNotifications(characteristic)
                .done { device ->  mCallbacks.onNotified(uuid,  null, "end")}
                .fail { device, status ->  mCallbacks.onNotified(uuid,  null, "fail")}
                .enqueue()
        }
        try {
            Log.w("blechk","blechk ${characteristic!!.descriptors.map {
                if (it.value != null) {
                    bytesToHex( it.value)
                }else {
                    it.value
                }
            } } <<<//")
        }catch (e: Exception) {}
        Log.w("blechk","blechk 2. ${characteristic!!.writeType} //")
        Log.w("blechk","blechk 3. ${characteristic!!.properties} //")


    }

    fun readCharacteristic(uuid: UUID) {
        readCharacteristic(characteristicMap[uuid])
            .fail { device, status -> mCallbacks.onRead(uuid, null, "failed") }
            .with { device, data -> mCallbacks.onRead(uuid, data.value) }
            .enqueue()
    }

    fun writeCharacteristic(uuid: UUID, data: String) {
        val characteristic = characteristicMap[uuid]

        writeCharacteristic(characteristic, Converters.hexToBytes(data))
            .done { device ->
                mCallbacks.onWrite(uuid, true) }
            .fail { device, status -> mCallbacks.onWrite(uuid, false) }
            .enqueue()
    }

    override fun log(priority: Int, message: String) {
        Logger.log(logSession, LogContract.Log.Level.fromPriority(priority), message)
    }

    override fun getGattCallback(): BleManagerGattCallback = callback

    interface ConnectedLECallback : BleManagerCallbacks {
        fun updateServices(serviceList: MutableList<BluetoothGattService>)
        fun onRead(uuid: UUID, bytes: ByteArray?, msg: String? = null)
        fun onWrite(uuid: UUID, isSuccess: Boolean)
        fun onIndicated(uuid: UUID, bytes: ByteArray?, msg:String? = null) // acknowledgment notifycations
        fun onNotified(uuid: UUID, bytes: ByteArray?, msg:String? = null)
    }
}