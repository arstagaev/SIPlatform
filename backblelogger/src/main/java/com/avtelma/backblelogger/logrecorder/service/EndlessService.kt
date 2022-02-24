package com.avtelma.backblelogger.logrecorder.service

//import com.avtelma.backblelogger.tools.VariablesAndConstants.Companion.FORBACKGRND_BLE_DEVICE

//import com.avtelma.backblelogger.tools.VariablesAndConstants.Companion.SETUP_AIM_BLE_DEVICE_NAME

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.*
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.avtelma.backblelogger.AVTSIPlatform_EntryPoint.Builder.RECORD_ACTIVITY
import com.avtelma.backblelogger.AVTSIPlatform_EntryPoint.Builder.SEND_TO_UNBOND
import com.avtelma.backblelogger.AVTSIPlatform_EntryPoint.Builder.STARTUP_DELAY_OF_LOOPER
import com.avtelma.backblelogger.broadcastreceivers.CloseServiceReceiver_RecorderLogs
import com.avtelma.backblelogger.broadcastreceivers.UnBondingReceiver
import com.avtelma.backblelogger.logrecorder.core.converterToXYZAllArray
import com.avtelma.backblelogger.logrecorder.core.converterToXYZJustFirstElement
import com.avtelma.backblelogger.logrecorder.core.dataParse2
import com.avtelma.backblelogger.enum.Actions
import com.avtelma.backblelogger.enum.ConnectingStyle
import com.avtelma.backblelogger.enum.CurrentStateOfService
import com.avtelma.backblelogger.logrecorder.models.FoundDevice
import com.avtelma.backblelogger.logrecorder.nordicble.BaseNordicBleManager
import com.avtelma.backblelogger.rawparser.service_parsing_events.ParsingActions
import com.avtelma.backblelogger.rawparser.service_parsing_events.ParsingEventService
import com.avtelma.backblelogger.logrecorder.soundplayer.SoundPlay
import com.avtelma.backblelogger.logrecorder.soundplayer.WhatIMustSay
import com.avtelma.backblelogger.logrecorder.tools.*
//import com.avtelma.backblelogger.tools.*
import com.avtelma.backblelogger.logrecorder.tools.Converters.Companion.bytesToHex
import com.avtelma.backblelogger.logrecorder.tools.VariablesAndConstants.Companion.ACTION_NOW
import com.avtelma.backblelogger.logrecorder.tools.VariablesAndConstants.Companion.CHOSEN_BLE_DEVICE
import com.avtelma.backblelogger.logrecorder.tools.VariablesAndConstants.Companion.CLOSEST_BLE_DEVICE
import com.avtelma.backblelogger.logrecorder.tools.VariablesAndConstants.Companion.CONNECTING_STYLE
import com.avtelma.backblelogger.logrecorder.tools.VariablesAndConstants.Companion.CURRENT_SPEED
import com.avtelma.backblelogger.logrecorder.tools.VariablesAndConstants.Companion.CURRENT_STATE_OF_SERVICE
import com.avtelma.backblelogger.logrecorder.tools.VariablesAndConstants.Companion.GPS_LOG
import com.avtelma.backblelogger.logrecorder.tools.VariablesAndConstants.Companion.IS_SUBSCRIBED
import com.avtelma.backblelogger.logrecorder.tools.VariablesAndConstants.Companion.LAST_CAUGHT_NOTIFY_time
import com.avtelma.backblelogger.logrecorder.tools.VariablesAndConstants.Companion.LIST_OF_FOUND_DEVICES
import com.avtelma.backblelogger.logrecorder.tools.VariablesAndConstants.Companion.SESSION_NAME_TIME_raw
import com.avtelma.backblelogger.logrecorder.tools.VariablesAndConstants.Companion.SESSION_NAME_TIME_xyz
import com.avtelma.backblelogger.logrecorder.tools.VariablesAndConstants.Companion.SUPER_BLE_DEVICE
import com.avtelma.backblelogger.logrecorder.tools.VariablesAndConstants.Companion.TYPE_OF_INPUT_LOG
import com.avtelma.backblelogger.toast_manage.tost
import kotlinx.coroutines.*
import no.nordicsemi.android.support.v18.scanner.*
import java.util.*


/**
 * Need check permission before run this Service
 *
 */
@SuppressLint("MissingPermission")
class EndlessService : Service() {

    private var wakeLock: PowerManager.WakeLock? = null
    private var isServiceStarted = false

    var name: String = ""
    var mac : String = ""
    var log : String = ""
    //private lateinit var mp1 : MediaPlayer
    //private var currentStateOfRecording: CurrentStateOfRecording = CurrentStateOfRecording.NO_CONNECTED

    var bleManager : BaseNordicBleManager? = null
    private var bleDevice: BluetoothDevice? = null
    private var scanJob: Job? = null

    override fun onBind(intent: Intent): IBinder? {
        log("Some component want to bind with the service")
        // We don't provide binding, so return null

        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        log("onStartCommand executed with startId: $startId")

        if (intent != null) {
            val action = intent.action


            log("using an intent with action $action")
            when (action) {
                Actions.START.name -> {
                    startService()
                }
                Actions.STOP.name -> {
                    stopService()
                    if (!isServiceStarted) {
                        stopSelf()
                    }
                    return START_NOT_STICKY
                }
                Actions.FORCE_STOP.name -> {
                    ACTION_NOW = Actions.FORCE_STOP
                    stopService()
                    if (!isServiceStarted) {
                        stopSelf()
                    }
                    return START_NOT_STICKY
                }
                Actions.UNBOND.name ->{
                    ACTION_NOW = Actions.UNBOND
                    startService()

                    //unBondDevice()  // include unbond + stopService
                }
                Actions.SCAN_START.name ->{
                    //CONNECTING_STYLE = ConnectingStyle.AUTO_BY_SEARCH
                    startService()
                    startScan()
                }
                Actions.SCAN_STOP.name ->{
                    stopScan()
                }
                Actions.NEUTRAL_CONNECTED.name ->{
                    ACTION_NOW = Actions.NEUTRAL_CONNECTED
                    //CURRENT_STATE_OF_SERVICE = CurrentStateOfService.WAIT_COMMAND_UNSUBS
                    unSubscribe()

                    when(intent.extras?.getInt("ble_conn")) { // Command to Service
                        1 -> { connectTo(CHOSEN_BLE_DEVICE!!) }
                        0 -> { disconnectOfBleDevice()        }
                    }

                }
                Actions.SUBS_AND_CONNECTED.name ->{
                    // aim is make rec again
                    ACTION_NOW = Actions.SUBS_AND_CONNECTED
                    CURRENT_STATE_OF_SERVICE = CurrentStateOfService.CONNECTED_BUT_NO_RECORDING
                }
                Actions.MISC.name -> {
                    //startService()

                    Log.i("zzz","zzz ${intent.extras?.getInt("CS")}")
                    when(intent.extras?.getInt("CS")) { // Command to Service
                        1 -> { connectTo(CHOSEN_BLE_DEVICE!!)  }
                        6 -> { startScan() }
                        7 -> { stopScan()  }
                        8 -> { unBondDevice()}
                    }
                }
                Actions.TARGET_CONNECT.name -> {
                    ACTION_NOW = Actions.TARGET_CONNECT
                    startService()
                    try {

                        externalConnectTo(intent.extras?.getParcelable("send_ble")!!)

                    } catch (e: Exception) {

                        Toast.makeText(applicationContext,"Sended BLE is null !!! \n${e.message}",Toast.LENGTH_LONG).show()

                    }

                }
                else -> log("This should never happen. No action in the received intent")
            }
        } else {
            log("with a null intent. It has been probably restarted by the system.")
        }
        // by returning this we make sure the service is restarted if the system kills the service
        return START_STICKY
    }


    var isScanning : Boolean = false
    private fun startScan() {
        if (isScanning)
            return

        val setting = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .setReportDelay(500)
            .setUseHardwareBatchingIfSupported(true)
            .build()
        val filter = ScanFilter.Builder()
           // .setServiceUuid(ParcelUuid(MANUFACTURE_UUID)) // here i use manufacture uuid
            .build()
        val scanner = BluetoothLeScannerCompat.getScanner()
        scanner.startScan(mutableListOf(filter), setting, scanCallback)
        isScanning = true

    }


    fun stopScan() {
        if (!isScanning)
            return

        isScanning = false
        BluetoothLeScannerCompat.getScanner().stopScan(scanCallback)
        scanJob?.cancel()

    }


    private fun initBle() {
        Log.i("init","init BaseNordicBleManager")
        bleManager = BaseNordicBleManager(this@EndlessService)
        bleManager?.setGattCallbacks(bleManagerCallbacks)
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanFailed(errorCode: Int) {
            //stopScan()
            Log.e("ccc","xxx resultx: ERRORORORR")
        }

        /**
         *  Organize list items by yourself
         */
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            //Log.i("iii","found ${result.device.name} ${result.rssi}")
            // not used
//            try {
//                Log.d("ccc","xxx resultx: "+result.device.name.get(0).toString())
//            }catch (e: Exception){
//
//            }

        }
        //need 17-30 sec to find our BLE tag
        override fun onBatchScanResults(results: MutableList<ScanResult>) {
           if (results.isEmpty())
               return


           //LIST_OF_FOUND_DEVICES.clear()

           for (i in ArrayList( results)) {
               if (i.device.name != null && i.device.name.toString().contains("itelma",true) == true) {

                   if (LIST_OF_FOUND_DEVICES.size == 0) {

                       LIST_OF_FOUND_DEVICES.add(FoundDevice(i.device,i.rssi))

                   }else {

                       for (p in 0 until LIST_OF_FOUND_DEVICES.size) {
                            if (LIST_OF_FOUND_DEVICES[p].bluetoothDevice.address != i.device.address) {
                                LIST_OF_FOUND_DEVICES.add(FoundDevice(i.device,i.rssi))
                            }else {
                                LIST_OF_FOUND_DEVICES[p] = (FoundDevice(i.device,i.rssi))
                            }
                       }

                   }



                   LIST_OF_FOUND_DEVICES.distinctBy { it.bluetoothDevice.address }
                   Log.i("iii","I found1!!! ${i.device.name} ${i.rssi} !!!")
                   Toast.makeText(applicationContext,"Found: ${i.device.name}",Toast.LENGTH_SHORT).show()
               }

           }
            if (LIST_OF_FOUND_DEVICES.size > 0) {
                LIST_OF_FOUND_DEVICES.sortedBy { it.rssi }
                CLOSEST_BLE_DEVICE = LIST_OF_FOUND_DEVICES[LIST_OF_FOUND_DEVICES.lastIndex].bluetoothDevice
                CHOSEN_BLE_DEVICE = LIST_OF_FOUND_DEVICES[LIST_OF_FOUND_DEVICES.lastIndex].bluetoothDevice
            }


           //LIST_OF_FOUND_DEVICES = results
           Log.d("ccc","xxx resultx: "+LIST_OF_FOUND_DEVICES.toString())
//           for(result in results) {
//               Log.d("ccc","xxx result: "+result.device.name)
//           }
//            for (i in 0 until INPUT_PREM_DEVICES.size) {
//
//                for (z in 0 until results.size) {
//
//                    if ( INPUT_PREM_DEVICES[i] == results.get(z).device.address ) {
//
//                        //carsTabletsArray.add(MainConstants.PREM_DEVICES[i].nameAuto +"; \nmac address: "+ MainConstants.PREM_DEVICES[i].tabletMac)
//                        AIM_CONNECT_DEVICE_ADDRESS = results.get(z).device.address
//
//                    }
//
//                }
//            }
        }
    }

    @ExperimentalUnsignedTypes
    private val bleManagerCallbacks = object : BaseNordicBleManager.ConnectedLECallback {
        override fun updateServices(serviceList: MutableList<BluetoothGattService>) {
            //foundService.value = serviceList
        }

        override fun onRead(uuid: UUID, bytes: ByteArray?, msg: String?) {
            val readData =
                if (bytes != null) bytesToHex(bytes) else if (!TextUtils.isEmpty(msg)) msg else "data is null"
            log("${uuid.toString()}/ Read) $readData")
        }

        override fun onWrite(uuid: UUID, isSuccess: Boolean) {
            log("${uuid.toString()}/ Write) " + (if (isSuccess) "success" else "failed"))
            sendMessage("********* Write isSuccess= "+isSuccess)
        }

        override fun onNotified(uuid: UUID, bytes: ByteArray?, msg: String?) {
            //Log.d("ccc","logy "+bytes)

            if (msg != null) {
                Log.w("cccnnn"," cccnnn msg from notif:${msg}")

                if(msg == "set up") {
                    IS_SUBSCRIBED = true
                    //SoundPlay().playx(this@EndlessService, WhatIMustSay.SUCCESS_SUBS)
                    SoundPlay().playx(this@EndlessService, WhatIMustSay.DING)
                }else if (msg == "end" || msg == "fail") {
                    IS_SUBSCRIBED = false
                }

                Toast.makeText(applicationContext,">>> Subscribe ${msg}",Toast.LENGTH_SHORT).show()
            }

            if (bytes != null ) {

                LAST_CAUGHT_NOTIFY_time = System.currentTimeMillis() / 1000L

                var bytesX : ByteArray = bytes

                // fixme need change, for econom memory
                refreshNotification(
                    converterToXYZJustFirstElement(
                        dataParse2(bytesX)
                    ),
                    false
                )

                // Write logs to file
                addLogsIMUandGPS(SESSION_NAME_TIME_xyz,
                    converterToXYZAllArray(dataParse2(bytesX))
                )
                addLogsRawData(SESSION_NAME_TIME_raw, bytesToHex(bytesX))

                Log.i("ccc","ccc ${bytesX.size}")

                if (CURRENT_STATE_OF_SERVICE == CurrentStateOfService.CONNECTED_BUT_NO_RECORDING && bytesX.size > 240){

                    SoundPlay().playx(this@EndlessService, WhatIMustSay.START_REC)

                }

                //addLogsGPS(SESSION_NAME_TIME_gps,)
                CURRENT_STATE_OF_SERVICE = CurrentStateOfService.RECORDING

            }else{
                CURRENT_STATE_OF_SERVICE = CurrentStateOfService.CONNECTED_BUT_NO_RECORDING
                log("data notiff is null!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1")
            }

        }

        override fun onDeviceDisconnecting(device: BluetoothDevice) {
            Log.d("ccc", "Disconnecting")
            Toast.makeText(this@EndlessService,"disconnecting ...",Toast.LENGTH_LONG).show()


        }

        override fun onDeviceDisconnected(device: BluetoothDevice) {
            SoundPlay().playx(this@EndlessService, WhatIMustSay.OFF)
            Log.d("ccc", "disconnected")
            CURRENT_STATE_OF_SERVICE = CurrentStateOfService.NO_CONNECTED
            sendMessage("#############disconnected:  "+device.name)

        }

        override fun onDeviceConnected(device: BluetoothDevice) {
            Log.d("ccc", "Connected")
            SoundPlay().playx(this@EndlessService, WhatIMustSay.ON)
            sendMessage("#############connected:  "+device.name)
            CURRENT_STATE_OF_SERVICE = CurrentStateOfService.CONNECTED_BUT_NO_RECORDING

            toastShow("successful connected ${device.name}",Color.GREEN,this@EndlessService)

            generateNameOfAllLogPerSession()

        }

        override fun onDeviceNotSupported(device: BluetoothDevice) {
            // nothing to do
            Toast.makeText(this@EndlessService,"Device not supported",Toast.LENGTH_LONG).show()

        }

        override fun onBondingFailed(device: BluetoothDevice) {
            Toast.makeText(this@EndlessService,"onBondingFailed: ${device.name}",Toast.LENGTH_LONG).show()
            // nothing to do
        }

        override fun onServicesDiscovered(device: BluetoothDevice, optionalServicesFound: Boolean) {
            log("service discovered")
        }

        override fun onBondingRequired(device: BluetoothDevice) {
            // nothing to do
        }

        override fun onLinkLossOccurred(device: BluetoothDevice) {
            // nothing to do
            SoundPlay().playx(this@EndlessService, WhatIMustSay.ERROR)
            CURRENT_STATE_OF_SERVICE= CurrentStateOfService.LOSS_CONNECTION_AND_WAIT_NEW
            toastShow("loss connection of: ${device.name}!!!",Color.RED,this@EndlessService)
        }

        override fun onBonded(device: BluetoothDevice) {
            // nothing to do
        }

        override fun onDeviceReady(device: BluetoothDevice) {
            Log.i("sss"," ondeviceservice:: ${device.name}")
            // nothing to do
            if (device != null) {


            } else {
                //SoundPlay().playx(this@EndlessService,WhatIMustSay.ATTENTION_OVERSPEED_60)
                sendMessage("#######Device NULL")
            }

        }

        override fun onError(device: BluetoothDevice, message: String, errorCode: Int) {
            Log.e("ccc", "ERORR BLE " + message)
            erroredDevices.add(device)
            SoundPlay().playx(this@EndlessService, WhatIMustSay.ERROR)
            CURRENT_STATE_OF_SERVICE = CurrentStateOfService.NO_CONNECTED  //may delete not sure
            toastShow("Error connect ${device.name} code:${message}",Color.RED,this@EndlessService)
        }

        override fun onDeviceConnecting(device: BluetoothDevice) {
            Log.d("ccc", "Connecting")
            CURRENT_STATE_OF_SERVICE = CurrentStateOfService.CONNECTING
            sendMessage("")
        }
    }
    // Send an Intent with an action named "my-event".
    private fun sendMessage(msg : String) {
        val intent = Intent("my-event")
        // add data
        intent.putExtra("message", msg)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    override fun onCreate() {
        super.onCreate()
        log("The recording service has been created".toUpperCase())
        if (ACTION_NOW == Actions.FORCE_STOP) {
            stopService()
        }
        if (!isServiceStarted) {
            val notification = createNotification()
            startForeground(1, notification)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        log("The service has been destroyed".toUpperCase())

        Toast.makeText(this, "Service destroyed", Toast.LENGTH_SHORT).show()
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        val restartServiceIntent = Intent(applicationContext, EndlessService::class.java).also {
            it.setPackage(packageName)
        }
        var restartServicePendingIntent: PendingIntent? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // FLAG_MUTABLE
            restartServicePendingIntent = PendingIntent.getService(this, 1, restartServiceIntent, PendingIntent.FLAG_IMMUTABLE);

        }else {
            restartServicePendingIntent = PendingIntent.getService(this, 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);

        }
        applicationContext.getSystemService(Context.ALARM_SERVICE);
        val alarmService: AlarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager;
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, restartServicePendingIntent)
    }


    private var btAdapter = BluetoothAdapter.getDefaultAdapter()
    private var alreadyBondedDevices = btAdapter.bondedDevices
    private lateinit var manager : BluetoothManager

    val noFilteredDevices : MutableList<String> = ArrayList()
    val filteredDevices :   MutableList<BluetoothDevice> = ArrayList()
    val erroredDevices = mutableSetOf<BluetoothDevice>()

    @SuppressLint("CheckResult")
    private fun startService() {

        if (isServiceStarted) {
            return
        }
        //stopScan()

        log("Starting the foreground service task")

        isServiceStarted = true
        setServiceState(this, ServiceState.STARTED)
//        GlobalScope.launch {
//            startScan()
//        }

        initBle()

        // we need this lock so our service gets not affected by Doze Mode
        wakeLock =
            (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "EndlessService::lock").apply {
                    acquire()
                }
            }



        manager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager

        locationTask()


        if (CONNECTING_STYLE == ConnectingStyle.AUTO_BY_SEARCH) {
            startScan()
        }

        toastShow("Service has been started",Color.YELLOW,this@EndlessService)
        /**
         * MAIN LOOPER
         */
        var INSPECTOR_SWITCHER_SCAN = 0

        GlobalScope.launch(Dispatchers.Main) {
            delay(STARTUP_DELAY_OF_LOOPER ?: 10000)
            while (isServiceStarted) {
                launch(Dispatchers.Main) {
                    /**
                     * Choose something one
                     */
                    //var gattDevices = manager.getConnectedDevices(BluetoothProfile.GATT)
                    try {

                        Log.w("ccc"," current state of rec: ${CURRENT_STATE_OF_SERVICE.name} action: ${ACTION_NOW.name} style: ${CONNECTING_STYLE.name}  SUPERBD: ${SUPER_BLE_DEVICE?.name}" )

                    }catch (e : Exception){}

                    when(ACTION_NOW) {
                        Actions.START, Actions.STOP , Actions.SUBS_AND_CONNECTED, Actions.MISC, Actions.UNBOND -> {

                            if (   CURRENT_STATE_OF_SERVICE == CurrentStateOfService.NO_CONNECTED
                                || CURRENT_STATE_OF_SERVICE == CurrentStateOfService.CONNECTING
                                || CURRENT_STATE_OF_SERVICE == CurrentStateOfService.LOSS_CONNECTION_AND_WAIT_NEW) {
                                time = 0

                                if (SUPER_BLE_DEVICE != null) {

                                    if (CONNECTING_STYLE == ConnectingStyle.MANUAL) {
                                        tost("[Manual mode] Try connect: ${SUPER_BLE_DEVICE?.name?: "[name is null]"}",this@EndlessService,true,true)
                                    }else {
                                        tost("Connect to lost device: ${SUPER_BLE_DEVICE?.name?: "[name is null]"}",this@EndlessService,true,true)
                                    }

                                    connectTo(SUPER_BLE_DEVICE!!)
                                    delay(7000)

                                } else if (CONNECTING_STYLE == ConnectingStyle.AUTO_BY_BOND) {
                                    /**
                                     * AUTOMATIC MODE by bond list
                                     */
                                    Log.w("sss",">>>>>>>>  AUTOMATIC MODE filtered: ${alreadyBondedDevices.joinToString()}")
                                    // auto mode, here we find we
                                    // bonding mode


                                    for (bt in alreadyBondedDevices) {
                                        if (bt.name.toString().contains("itelma",true) == true) {
                                            Log.i("cccc",">>> again connect ")
                                            tost("[AUTO-BOND mode] Try connect: ${SUPER_BLE_DEVICE?.name?: "[error dev is null]"}",this@EndlessService,false,true)

                                            connectTo(bt)
                                            delay(7000)

                                        }
                                    }
                                    delay(5000)

                                    refreshNotification("Can`t connect, state: ${CURRENT_STATE_OF_SERVICE.name}",true)
                                    Log.d("ccc","List of bonded devices "+noFilteredDevices.toString())
                                    //startScan()
                                }
                                else if (CONNECTING_STYLE == ConnectingStyle.AUTO_BY_SEARCH)
                                {
                                    if (INSPECTOR_SWITCHER_SCAN < 7) { // more than 49 sec
                                        Log.d("ccc",">>ccclosest ${CLOSEST_BLE_DEVICE?.name} ")
                                        if (CLOSEST_BLE_DEVICE != null) {
                                            connectTo(CLOSEST_BLE_DEVICE!!)
                                            delay(7000)
                                        } else {
                                            for (btcs in alreadyBondedDevices) {

                                                if (btcs.name.toString().contains("itelma",true) == true){
                                                    Log.i("cccc",">>> again connect ")

                                                    connectTo(btcs)
                                                    delay(7000)

                                                }

                                            }
                                            delay(2000)
                                            INSPECTOR_SWITCHER_SCAN++
                                            refreshNotification("Make sure that ble tag is work, state: ${CURRENT_STATE_OF_SERVICE.name}",true)

                                        }
                                    } else {
                                        CONNECTING_STYLE = ConnectingStyle.AUTO_BY_BOND
                                    }


                                }
                                else
                                {
                                    /**
                                     * MANUAL MODE
                                     */
                                    Log.w("www","ble device is NULL")
                                    Toast.makeText(this@EndlessService,"ble device is null",Toast.LENGTH_SHORT).show()

                                }
                            }else if (CURRENT_STATE_OF_SERVICE == CurrentStateOfService.RECORDING){

                            }else if (CURRENT_STATE_OF_SERVICE == CurrentStateOfService.CONNECTED_BUT_NO_RECORDING) {
                                //delay(12000) // i make this delay coz => phone do not have time to turn notifications in ~2 sec

                                if( ACTION_NOW != Actions.UNBOND ) {
                                    INSPECTOR_SWITCHER_SCAN = 0
                                    if (!IS_SUBSCRIBED) {
                                        delay(700)
                                        subscribeToCharacteristic()
                                    }
                                    stopScan()


                                } else {

                                    unBondDevice()

                                }

                            }
                        }

                        Actions.NEUTRAL_CONNECTED -> {
                            if (   CURRENT_STATE_OF_SERVICE == CurrentStateOfService.NO_CONNECTED
                                || CURRENT_STATE_OF_SERVICE == CurrentStateOfService.CONNECTING
                                || CURRENT_STATE_OF_SERVICE == CurrentStateOfService.LOSS_CONNECTION_AND_WAIT_NEW) {

                                connectTo(SUPER_BLE_DEVICE!!)
                                delay(7000)

                            }else if (CURRENT_STATE_OF_SERVICE == CurrentStateOfService.CONNECTED_BUT_NO_RECORDING) {

                            }
                            Log.i("ccc","ccc NOW is Actions.NEUTRAL_CONNECTED")
                        }
                        Actions.TARGET_CONNECT -> {
                            Log.i("ttt","Actions.TARGET_CONNECT , waiting for connect: ${CHOSEN_BLE_DEVICE?.address} bondState:${CHOSEN_BLE_DEVICE?.bondState}")
                        }

                    }
                }
                ///////////////////////////////////////////////////////////////////
                if (CURRENT_STATE_OF_SERVICE == CurrentStateOfService.RECORDING) {
                    delay(20000)
                }else {
                    delay(4000)
                }


                Log.w("sss","<><><><><><><> LOAD SERVICE AGAIN <><><><><>")
            }
            log(">>>>>>>>>>>>>>>>>>End of the loop for the service<<<<<<<<<<<<<<<<<<<<<<<<<<<")
            log(">>>>>>>>>>>>>>>>>>End of the loop for the service<<<<<<<<<<<<<<<<<<<<<<<<<<<")
            log(">>>>>>>>>>>>>>>>>>End of the loop for the service<<<<<<<<<<<<<<<<<<<<<<<<<<<")
        }
    }

    private var lastLocation: Location? = null
    lateinit var locManager : LocationManager

    fun locationTask() {


        // GPS enabled and have permission - start requesting location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(applicationContext,"GPS ACCESS Error",Toast.LENGTH_LONG).show()
            return
        }
        locManager= getSystemService(LOCATION_SERVICE) as LocationManager
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locListener)


    }

    val locListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(loc: Location) {
            log("location updated !!")
            updateLocation(loc) // add what can see gps
        }
        override fun onProviderEnabled(provider: String) { log("onProviderEnabled !!") }
        override fun onProviderDisabled(provider: String) { log("onProviderDisabled !!") }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
    }



    private fun updateLocation(location: Location) {
        if (locManager == null)
            return

        val locationEnabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val waitingForLocation = locationEnabled && !validLocation(location)
        val haveLocation = locationEnabled && !waitingForLocation


        if (haveLocation) {
            GlobalScope.launch {
                //addLogsGPS(SESSION_NAME_TIME_gps,""+location.latitude+";"+location.longitude+";"+ (location.speed)+";"+azimuthToDirections(location.bearing))
                GPS_LOG = ""+location.latitude+";"+location.longitude+";"+ (location.speed)+";"+ azimuthToDirections(location.bearing)
                // visual representation
                CURRENT_SPEED = speedMeterPerSecondToKmH(location.speed)

            }
        }
    }

    private fun validLocation(location: Location?): Boolean {
        if (location == null) {
            return false
        }

        // Location must be from less than 30 seconds ago to be considered valid
        return if (Build.VERSION.SDK_INT < 17) {
            System.currentTimeMillis() - location.time < 30e3
        } else {
            SystemClock.elapsedRealtimeNanos() - location.elapsedRealtimeNanos < 30e9
        }
    }

    var time = 0
    // 86400 sec = 1 day
//    var timeOfRecordingLog = object : CountDownTimer(86400000,1000){
//        override fun onTick(millisUntilFinished: Long) {
//            time++
//            //refreshNotification()
//
//        }
//
//        override fun onFinish() {
//
//        }
//    }


    fun connectTo(device: BluetoothDevice) {
        if (ACTION_NOW == Actions.TARGET_CONNECT) {
            Log.w("rrr","we return from connect !!! coz: ${ACTION_NOW.name}")
            return
        }
        if (device == null) {
            Log.e("eee","deviceBLE is ${device.name}")
            Toast.makeText(applicationContext,"device ble is null",Toast.LENGTH_SHORT).show()
            return

        }

        SUPER_BLE_DEVICE = device
        // if no connected - connect
        if (CURRENT_STATE_OF_SERVICE == CurrentStateOfService.NO_CONNECTED || CURRENT_STATE_OF_SERVICE == CurrentStateOfService.LOSS_CONNECTION_AND_WAIT_NEW){
            Log.i("ccc","make connectt!!>>${device.name} is connected: ${bleManager?.isConnected} is bonded: ${device.bondState == 12} ")
            bleDevice = device
            //FORBACKGRND_BLE_DEVICE = device

            //toastShow("Connecting to: ${bleDevice?.name}",Color.YELLOW,this@EndlessService)

            if (bleManager?.isConnected == false || ACTION_NOW == Actions.TARGET_CONNECT) {
                GlobalScope.launch {
                    reconnect()
                    // if already connected, make notify
                    //delay(900)
                    //justNotify()
                }

            }

            if (device.bondState == 10){
                GlobalScope.launch {
                    delay(700)
                    device.createBond() // may delete i test why few times suggest make bond connection
                    Log.i("ccc","CREATE BOND....  BONDSTATE:${device.bondState} ")
                    delay(3000)
                }
            }

            //bleManager?.logSession = Logger.newSession(this, null, device.address, device.name)
            // check if not bond -> make bond
            Log.i("ccc","make connectt!! NEW:: is connected: ${bleManager?.isConnected == true} is bonded: ${device.bondState == 12} ")



        }
    }

    fun externalConnectTo(device: BluetoothDevice) {
//        if (ACTION_NOW == Actions.TARGET_CONNECT) {
//            Log.w("rrr","we return from connect !!! coz: ${ACTION_NOW.name}")
//            return
//        }
        if (device == null) {
            Log.e("eee","deviceBLE is ${device.name}")
            return
        }
        SUPER_BLE_DEVICE = device
        // if no connected - connect
        if (CURRENT_STATE_OF_SERVICE == CurrentStateOfService.NO_CONNECTED || CURRENT_STATE_OF_SERVICE == CurrentStateOfService.LOSS_CONNECTION_AND_WAIT_NEW){
            Log.i("ccc","make connectt!!>>${device.name} is connected: ${bleManager?.isConnected} is bonded: ${device.bondState == 12} ")
            bleDevice = device
            if (bleManager?.isConnected == false || ACTION_NOW == Actions.TARGET_CONNECT) {
                GlobalScope.launch {
                    reconnect()
                }
            }

            if (device.bondState == 10) {
                GlobalScope.launch {
                    delay(700)
                    device.createBond() // may delete i test why few times suggest make bond connection
                    Log.i("ccc","CREATE BOND....  BONDSTATE:${device.bondState} ")
                    delay(3000)
                }
            }

            Log.i("ccc","make connectt!! NEW:: is connected: ${bleManager?.isConnected == true} is bonded: ${device.bondState == 12} ")
        }
    }

    fun reconnect() {

        if (bleDevice == null)
            return

        Log.i("mmm","make conn ${bleDevice?.name}")

        bleManager?.connect(bleDevice!!)
            ?.useAutoConnect(true)
            ?.enqueue()

    }

    private fun subscribeToCharacteristic(){
        try {
            if (! bleManager!!.isConnected ){
                CURRENT_STATE_OF_SERVICE = CurrentStateOfService.LOSS_CONNECTION_AND_WAIT_NEW
            }
        }catch (e: Exception){
            CURRENT_STATE_OF_SERVICE = CurrentStateOfService.LOSS_CONNECTION_AND_WAIT_NEW
        }

        Log.i("sss","make NOTIFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF")
        if (CURRENT_STATE_OF_SERVICE == CurrentStateOfService.CONNECTED_BUT_NO_RECORDING){
            Log.i("ccc","try to connect UUID notif")

            bleManager?.notifyCharacteristic(true,UUID.fromString("74ab521e-060d-26df-aa64-cf4df2d0d641"))

        }

    }

    private fun unSubscribe(){
        try {
            bleManager?.notifyCharacteristic(isChecked = false,UUID.fromString("74ab521e-060d-26df-aa64-cf4df2d0d641"))
        }catch (e: Exception){}

    }


    fun disconnectOfBleDevice(){
        bleManager?.notifyCharacteristic(false,UUID.fromString("74ab521e-060d-26df-aa64-cf4df2d0d641"))

        bleManager?.disconnect()?.enqueue()
        //bleDevice = null

    }

    private fun stopService() {
        if (!isServiceStarted){
            return
        }

        stopScan()
        log("Stopping the foreground service")
        Toast.makeText(this, "Service stopping", Toast.LENGTH_SHORT).show()
        CLOSEST_BLE_DEVICE = null
        SUPER_BLE_DEVICE = null
        try {
            disconnectOfBleDevice()
            wakeLock?.let {
                if (it.isHeld) {
                    it.release()
                }
            }

            stopForeground(true)
            stopSelf()


        } catch (e: Exception) {
            log("Service stopped without being started: ${e.message}")
        }
        isServiceStarted = false
        setServiceState(this, ServiceState.STOPPED)

        if (ACTION_NOW != Actions.FORCE_STOP && ACTION_NOW != Actions.UNBOND) {
            launchCommandInService_RAWPARSER(ParsingActions.FULL_PARSING)
        }

    }

    private fun unBondDevice(){
        if (bleDevice == null){
            toastShow("can`t unbond, BLE device not founded [ ${bleDevice?.name} ]",Color.BLUE,this@EndlessService)
            return
        }
        if (CURRENT_STATE_OF_SERVICE == CurrentStateOfService.CONNECTED_BUT_NO_RECORDING ||
                CURRENT_STATE_OF_SERVICE == CurrentStateOfService.RECORDING) {

            toastShow("UNBONDING.. [ ${bleDevice?.name} ]",Color.BLUE,this@EndlessService)
            Log.i("uuu","uuu unBondDevice")             //74ab521e-060d-26df-aa64-cf4df2d0d643
            GlobalScope.launch {

                bleManager?.notifyCharacteristic(false,UUID.fromString("74ab521e-060d-26df-aa64-cf4df2d0d641"))
                delay(3000)
                bleDevice?.removeBond()
                delay(2500)
                bleManager?.writeCharacteristic( UUID.fromString("74ab521e-060d-26df-aa64-cf4df2d0d643"),SEND_TO_UNBOND ?: "01") // been 01
                delay(1500)
                disconnectOfBleDevice() // send unsubs + diconnect


                alreadyBondedDevices = btAdapter.bondedDevices
            }
            toastShow("successful UNBONDED",Color.GREEN,this@EndlessService)
            stopService()
        }else {
            tost("cant unbond \uD83D\uDC2E!! need connection and after that unbond",this@EndlessService,true,true)
        }

    }


    private lateinit var notificationManager : NotificationManager
    private val notificationChannelId = "avtelma1"
    private val notificationChannelId2 = "avtelma2"

    private var builder = NotificationCompat.Builder(this, notificationChannelId)
        .setContentTitle("AVTelma")
        .setContentText("\uD83D\uDD34 Working..")
       // .setContentIntent(pendingIntent)
        .setSmallIcon(com.avtelma.backblelogger.R.drawable.ic_baseline_bluetooth_drive_24)
        //.setTicker("Ticker text")
        .setPriority(NotificationCompat.PRIORITY_MIN) // for under android 26 compatibility
        //.setOnlyAlertOnce(true) // ATTENTION!!!
        //.addAction(actionX)

    private var builder2 = NotificationCompat.Builder(this, notificationChannelId2)
        .setSmallIcon(com.avtelma.backblelogger.R.drawable.ic_baseline_bluetooth_drive_24)
        .setContentTitle("Log:")
        .setOnlyAlertOnce(true) // ATTENTION!!!
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)



    private fun refreshNotification(msg: String, isFirstNotif : Boolean) {
        if (isFirstNotif){

            builder2.setContentTitle("${msg}")
            builder2.setContentText("")
            notificationManager.notify(notificationChannelId2,2,builder2.build())

        }else{

            //CURRENT_LOG_JUST_PRESENTATION = msg
            builder2.setContentTitle("Log:")
            builder2.setContentText("$msg spd: $CURRENT_SPEED stl: ${CONNECTING_STYLE.name.take(3)}|${TYPE_OF_INPUT_LOG.name}")
            notificationManager.notify(notificationChannelId2,2,builder2.build())

        }
    }

    fun launchCommandInService_RAWPARSER(parsingAction : ParsingActions) {
        Intent(this, ParsingEventService::class.java).also {
            it.action = parsingAction.name

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                  log("Starting the service in >=26 Mode")
                  startForegroundService(it)
                  return
            }else {
                log("Starting the service in < 26 Mode")
                startService(it)
            }

        }
        //fixme

        if (isServiceStarted) {
            stopService()
        }
    }
    private fun createNotification(): Notification {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // depending on the Android API that we're dealing with we will have
        // to use a specific method to create the notification

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // coz notif channels added in android 8.0
            val channel = NotificationChannel(
                notificationChannelId,
                "AVTelma_MainIndicatorOfWork",
                NotificationManager.IMPORTANCE_LOW
            ).let {
                it.description = "Main Indicator of Service"
                it.enableLights(true)
                it.lightColor = Color.RED
                it.enableVibration(true)
                it.vibrationPattern = longArrayOf(100, 200, 500, 100, 100, 100, 100, 100, 100, 300, 400, 500)
                it
            }

            val channel2 = NotificationChannel(
                notificationChannelId2,
                "AVTelma_xyz",
                NotificationManager.IMPORTANCE_DEFAULT
            ).let {
                it.description = "no main Indicator of Service"
                it.enableLights(true)
                it.lightColor = Color.RED
                it
            }
            notificationManager.createNotificationChannel(channel)
            notificationManager.createNotificationChannel(channel2)
        }

        if(RECORD_ACTIVITY == null) {
            //RECORD_ACTIVITY = MainActivity::class.java
        }
        Log.i("ccc","ccclass  ${RECORD_ACTIVITY?.name}")
        var pendingIntent: PendingIntent? = null
        var actionIntent : PendingIntent? = null
        var actionIntent2: PendingIntent? = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = Intent(this,RECORD_ACTIVITY).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent,  PendingIntent.FLAG_MUTABLE)
            }
            actionIntent = PendingIntent.getBroadcast(
                this,
                0, Intent(this, CloseServiceReceiver_RecorderLogs::class.java), PendingIntent.FLAG_MUTABLE
            )
            actionIntent2 = PendingIntent.getBroadcast(
                this,
                0, Intent(this, UnBondingReceiver::class.java), PendingIntent.FLAG_MUTABLE
            )

        } else {
            actionIntent = PendingIntent.getBroadcast(
                this,
                0, Intent(this, CloseServiceReceiver_RecorderLogs::class.java), PendingIntent.FLAG_UPDATE_CURRENT
            )
            actionIntent2 = PendingIntent.getBroadcast(
                this,
                0, Intent(this, UnBondingReceiver::class.java), PendingIntent.FLAG_UPDATE_CURRENT
            )

            pendingIntent = Intent(this,RECORD_ACTIVITY).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent,  0)
            }

        }



////        builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Notification.Builder(
////            this,
////            notificationChannelId
////        ) else Notification.Builder(this)
//
//        //////////
//        val broadcastIntent = Intent(this, CloseServiceReceiver_RecorderLogs::class.java)
//        //broadcastIntent.putExtra("toastMessage", android.R.id.message)
//
//
//        var actionX: NotificationCompat.Action =
//            NotificationCompat.Action.Builder(com.avtelma.backblelogger.R.drawable.ic_stop_rec, "Stop", actionIntent).build()
////        val remoteViews = RemoteViews(packageName, android.R.layout.activity_list_item)
////        remoteViews.setTextViewText(R.id.text,"d")


        return builder
            .setContentTitle("AVTelma")
            .setContentText("\uD83D\uDD34 Working..")
            .setContentIntent(pendingIntent)
            .setSmallIcon(com.avtelma.backblelogger.R.drawable.ic_baseline_bluetooth_drive_24)
            //.setTicker("Ticker text")
            .setPriority(NotificationCompat.PRIORITY_LOW) // for under android 26 compatibility
            .setOnlyAlertOnce(true) // ATTENTION!!!
            .addAction(com.avtelma.backblelogger.R.drawable.ic_stop_rec,"stop",actionIntent)
            .addAction(com.avtelma.backblelogger.R.drawable.ic_stop_rec,"unbonding",actionIntent2)

            .build()
    }




}
