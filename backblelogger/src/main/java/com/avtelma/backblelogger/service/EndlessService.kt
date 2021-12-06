package com.avtelma.backblelogger.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.bluetooth.*
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
import com.avtelma.backblelogger.AVTSIPlatform_EntryPoint
import com.avtelma.backblelogger.enum.CurrentStateOfRecording
import com.avtelma.backblelogger.nordicble.BaseNordicBleManager

import com.avtelma.backblelogger.soundplayer.WhatIMustSay

import com.avtelma.backblelogger.broadcastreceivers.CloseServiceReceiver
import com.avtelma.backblelogger.core.converterToXYZAllArray
import com.avtelma.backblelogger.core.converterToXYZJustFirstElement
import com.avtelma.backblelogger.enum.Actions
import com.avtelma.backblelogger.soundplayer.SoundPlay
import com.avtelma.backblelogger.tools.Converters.Companion.bytesToHex
import com.avtelma.backblelogger.tools.InputSession.Companion.RECORD_ACTIVITY
import com.avtelma.backblelogger.tools.VariablesAndConstants.Companion.CURRENT_SPEED
//import com.avtelma.backblelogger.tools.VariablesAndConstants.Companion.FORBACKGRND_BLE_DEVICE
import com.avtelma.backblelogger.tools.VariablesAndConstants.Companion.GPS_LOG
import com.avtelma.backblelogger.tools.VariablesAndConstants.Companion.NAME_OF_TABLET
import com.avtelma.backblelogger.tools.VariablesAndConstants.Companion.SESSION_NAME_TIME_raw
import com.avtelma.backblelogger.tools.VariablesAndConstants.Companion.SESSION_NAME_TIME_xyz
import com.avtelma.backblelogger.tools.VariablesAndConstants.Companion.CURRENT_STATE_OF_RECORDING

import kotlinx.coroutines.*

import no.nordicsemi.android.support.v18.scanner.*
import java.util.*

import com.avtelma.backblelogger.core.dataParse2
//import com.avtelma.backblelogger.tools.VariablesAndConstants.Companion.SETUP_AIM_BLE_DEVICE_NAME

import com.avtelma.backblelogger.broadcastreceivers.UnBondingReceiver
import com.avtelma.backblelogger.enum.ConnectingStyle
import com.avtelma.backblelogger.tools.VariablesAndConstants.Companion.CONNECTING_STYLE
import com.avtelma.backblelogger.tools.VariablesAndConstants.Companion.SUPER_BLE_DEVICE
import com.avtelma.backblelogger.tools.*


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
                Actions.START.name ->  startService()
                Actions.STOP.name ->   stopService()
                Actions.UNBOND.name -> unBondDevice()  // include unbond + stopService
                else -> log("This should never happen. No action in the received intent")
            }
        } else {
            log(
                "with a null intent. It has been probably restarted by the system."
            )
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

        isScanning = false
        BluetoothLeScannerCompat.getScanner().stopScan(scanCallback)
        scanJob?.cancel()

    }


    private fun initBle() {
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
            // not used
//            try {
//                Log.d("ccc","xxx resultx: "+result.device.name.get(0).toString())
//            }catch (e: Exception){
//
//            }

        }

        override fun onBatchScanResults(results: MutableList<ScanResult>) {
           if (results.isEmpty())
               return

           Log.d("ccc","xxx resultx: "+results.toString())
           for(result in results) {
               Log.d("ccc","xxx result: "+result.device.name)
           }
            for (i in 0 until AVTSIPlatform_EntryPoint().INPUT_PREM_DEVICES.size) {

                for (z in 0 until results.size) {

                    if ( AVTSIPlatform_EntryPoint().INPUT_PREM_DEVICES[i] == results.get(z).device.address ) {

                        //carsTabletsArray.add(MainConstants.PREM_DEVICES[i].nameAuto +"; \nmac address: "+ MainConstants.PREM_DEVICES[i].tabletMac)
                        AVTSIPlatform_EntryPoint().AIM_CONNECT_DEVICE_ADDRESS = results.get(z).device.address

                    }

                }


            }

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
            Log.d("ccc","logy "+bytes)

            if (bytes != null ){


                var bytesX : ByteArray = bytes

                refreshNotification(
                    converterToXYZJustFirstElement(
                        dataParse2(bytesX)
                    ),
                    false
                )

                addLogsIMUandGPS(SESSION_NAME_TIME_xyz,
                    converterToXYZAllArray(dataParse2(bytesX))
                )
                addLogsRawData(SESSION_NAME_TIME_raw, bytesToHex(bytesX))

                Log.i("ccc","ccc ${bytesX.size}")

                if (CURRENT_STATE_OF_RECORDING == CurrentStateOfRecording.CONNECTED_BUT_NO_RECORDING && bytesX.size > 240){

                    SoundPlay().playx(this@EndlessService, WhatIMustSay.START_REC)

                }

                //addLogsGPS(SESSION_NAME_TIME_gps,)
                CURRENT_STATE_OF_RECORDING = CurrentStateOfRecording.RECORDING

            }else{
                CURRENT_STATE_OF_RECORDING = CurrentStateOfRecording.CONNECTED_BUT_NO_RECORDING
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
            CURRENT_STATE_OF_RECORDING = CurrentStateOfRecording.NO_CONNECTED
            sendMessage("#############disconnected:  "+device.name)

        }

        override fun onDeviceConnected(device: BluetoothDevice) {
            Log.d("ccc", "Connected")
            SoundPlay().playx(this@EndlessService, WhatIMustSay.ON)
            sendMessage("#############connected:  "+device.name)
            CURRENT_STATE_OF_RECORDING = CurrentStateOfRecording.CONNECTED_BUT_NO_RECORDING

            toastShow("successful connected ${device.name}",Color.GREEN,this@EndlessService)

            generateNameOfAllLogPerSession()

        }

        override fun onDeviceNotSupported(device: BluetoothDevice) {
            // nothing to do
            Toast.makeText(this@EndlessService,"Device not supported",Toast.LENGTH_LONG).show()

        }

        override fun onBondingFailed(device: BluetoothDevice) {
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
            CURRENT_STATE_OF_RECORDING= CurrentStateOfRecording.LOSS_CONNECTION_AND_WAIT_NEW
            toastShow("loss connection of: ${device.name}!!!",Color.RED,this@EndlessService)
        }

        override fun onBonded(device: BluetoothDevice) {
            // nothing to do
        }

        override fun onDeviceReady(device: BluetoothDevice) {
            Log.i("sss"," ondeviceservice:: ${device.name}")
            // nothing to do
            if (device != null){


            }else{
                //SoundPlay().playx(this@EndlessService,WhatIMustSay.ATTENTION_OVERSPEED_60)
                sendMessage("#######Device NULL")
            }

        }

        override fun onError(device: BluetoothDevice, message: String, errorCode: Int) {
            Log.e("ccc", "ERORR BLE " + message)
            erroredDevices.add(device)
            SoundPlay().playx(this@EndlessService, WhatIMustSay.ERROR)
            CURRENT_STATE_OF_RECORDING = CurrentStateOfRecording.NO_CONNECTED  //may delete not sure
            toastShow("Error connect ${device.name} code:${message}",Color.RED,this@EndlessService)
        }

        override fun onDeviceConnecting(device: BluetoothDevice) {
            Log.d("ccc", "Connecting")
            CURRENT_STATE_OF_RECORDING = CurrentStateOfRecording.CONNECTING
//            if (device.name != "SL004002"){
//
//            }
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
        log("The service has been created".toUpperCase())
        val notification = createNotification()
        startForeground(1, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        log("The service has been destroyed".toUpperCase())

        Toast.makeText(this, "Service destroyed", Toast.LENGTH_SHORT).show()
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        val restartServiceIntent = Intent(applicationContext, EndlessService::class.java).also {
            it.setPackage(packageName)
        };
        val restartServicePendingIntent: PendingIntent = PendingIntent.getService(this, 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        applicationContext.getSystemService(Context.ALARM_SERVICE);
        val alarmService: AlarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager;
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, restartServicePendingIntent)
    }


    private var btAdapter = BluetoothAdapter.getDefaultAdapter()
    private var alreadyBondedDevices = btAdapter.bondedDevices
    private lateinit var manager : BluetoothManager
//    private val ENABLE_BLUETOOTH_REQUEST_CODE = 1
//    private fun promptEnableBluetooth() {
//        if (!btAdapter.isEnabled) {
//            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//            startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH_REQUEST_CODE)
//        }
//    }
    val noFilteredDevices : MutableList<String> = ArrayList()
    val filteredDevices :   MutableList<BluetoothDevice> = ArrayList()
    val erroredDevices = mutableSetOf<BluetoothDevice>()

    @SuppressLint("CheckResult")
    private fun startService() {


        if (isServiceStarted) return

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


        var totalItems = 0
        var times = 0
        manager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager

        locationTask()
        try {
            //refreshListOfBondedDevices()
        }catch (e: Exception){}


        if (CONNECTING_STYLE == ConnectingStyle.AUTO_BY_SEARCH) {
            startScan()
        }

        toastShow("Service has been started",Color.YELLOW,this@EndlessService)
        /**
         * MAIN LOOPER
         */
        GlobalScope.launch(Dispatchers.Main) {
            while (isServiceStarted) {
                launch(Dispatchers.Main) {
                    /**
                     * Choose something one
                     */
                    //var gattDevices = manager.getConnectedDevices(BluetoothProfile.GATT)
                    Log.w("ccc"," current state of rec: ${CURRENT_STATE_OF_RECORDING.name}  style: ${CONNECTING_STYLE.name}" )

                    if (   CURRENT_STATE_OF_RECORDING == CurrentStateOfRecording.NO_CONNECTED
                        || CURRENT_STATE_OF_RECORDING == CurrentStateOfRecording.CONNECTING
                        || CURRENT_STATE_OF_RECORDING == CurrentStateOfRecording.LOSS_CONNECTION_AND_WAIT_NEW){
                        time = 0



                        if (CONNECTING_STYLE == ConnectingStyle.AUTO_BY_BOND) {
                            /**
                             * AUTOMATIC MODE by bond list
                             */
                            Log.w("sss",">>>>>>>>  AUTOMATIC MODE filtered: ${alreadyBondedDevices.joinToString()}")
                            // auto mode, here we find we
                            // bonding mode

                            // remove ble devices which we can`t make connect
                            //toastShow("bonded devices: ${bondedDevices.joinToString()}",Color.YELLOW,this@EndlessService)

                            for (bt in alreadyBondedDevices) {
                                if (bt.name.toString().contains("itelma",true) == true){
                                    Log.i("cccc",">>> again connect ")
                                    connectAndPlayNotify(bt)
                                    delay(7000)
                                }
                            }
//                            try{
//
//                                for (bt in erroredDevices){
//
//                                    for (i in 0 until filteredDevices.size) {
//
//                                        if (bt == filteredDevices[i]){
//                                            filteredDevices.removeAt(i)
//                                        }
//
//                                    }
//
//                                }
//                            }catch (e: Exception){}
//                            Log.i("fff","filteredDevices.size ${filteredDevices.size}")
//                            if (filteredDevices.size > 0){
//
//                                connectAndPlayNotify(filteredDevices[0])
//
//                            }else {
//                                refreshListOfBondedDevices()
//                            }

                            delay(5000)



//                            loop1@for (btFilt in 0 until filteredDevices.size){
//
//                                Toast.makeText(this@EndlessService,"Try connect: ${SUPER_BLE_DEVICE?.name}",Toast.LENGTH_SHORT).show()
//
//
//                                if (currentStateOfRecording == CurrentStateOfRecording.CONNECTED_BUT_NO_RECORDING ||bleManager?.isConnected == true){
//                                    Log.i("sss",">>iiiiiiiiiiiiiiiii found available bonded device: ${filteredDevices[btFilt].name} |${filteredDevices[btFilt].address}")
//
//                                    break@loop1
//                                    //cancel()
//                                }
//                            }
                            refreshNotification("Need bond tablet Itelma ",true)
                            Log.d("ccc","List of bonded devices "+noFilteredDevices.toString())
                            //startScan()
                        }
                        else if (CONNECTING_STYLE == ConnectingStyle.AUTO_BY_SEARCH)
                        {

                        }
                        else
                        {
                            /**
                             * MANUAL MODE
                             */
                            Log.w("sss",">>>>>>>>  MANUAL MODE")
                            // debug and just record mode
                            if (SUPER_BLE_DEVICE != null){
                                Toast.makeText(this@EndlessService,"[Manual mode] Try connect: ${SUPER_BLE_DEVICE?.name}",Toast.LENGTH_SHORT).show()
                                connectAndPlayNotify(SUPER_BLE_DEVICE!!)

                            }else {
                                Log.w("www","ble device is NULL")
                                Toast.makeText(this@EndlessService,"ble device is null",Toast.LENGTH_SHORT).show()
                            }
                        }
                    }else if (CURRENT_STATE_OF_RECORDING == CurrentStateOfRecording.RECORDING){

//                        time++
//                        if (time < 2){
//                            SoundPlay().playx(this@EndlessService,WhatIMustSay.START_REC)
//                        }

                    }else if(CURRENT_STATE_OF_RECORDING == CurrentStateOfRecording.CONNECTED_BUT_NO_RECORDING) {
                        //bleManager?.logSession = Logger.newSession(getApplication(), null, FORBACKGRND_BLE_DEVICE.address, FORBACKGRND_BLE_DEVICE.name) // in logsession we have been error, and i remove this

                        //delay(12000) // i make this delay coz => phone do not have time to turn notifications in ~2 sec
                        justNotify()
//                        Handler(Looper.getMainLooper()).postDelayed({
//
//                        }, 3000)

                    }
                }
                if (CURRENT_STATE_OF_RECORDING == CurrentStateOfRecording.CONNECTED_BUT_NO_RECORDING){
                    justNotify()
                    delay(15000)
                }else if (CURRENT_STATE_OF_RECORDING == CurrentStateOfRecording.RECORDING){
                    delay(20000)
                } else {
                    delay(9000)
                    times++
                }
                Log.w("sss","<><><><><><><> LOAD SERVICE AGAIN <><><><><>")
            }
            log("End of the loop for the service")
        }
    }

//    private fun refreshListOfBondedDevices(){
//        alreadyBondedDevices = btAdapter.bondedDevices
//        filteredDevices.clear()
//        noFilteredDevices.clear()
//
//        for (bt in alreadyBondedDevices) {
//            noFilteredDevices.add(bt.name)
//
//            if(SETUP_AIM_BLE_DEVICE_NAME != null && SETUP_AIM_BLE_DEVICE_NAME != "")
//            {
//                if (bt.name.toString().contains(SETUP_AIM_BLE_DEVICE_NAME.toString(),true)) {
//
//                    filteredDevices.add(bt)
//
//                }
//            }else{
//                if (bt.name.toString().contains(NAME_OF_TABLET.toString(),true)) {
//
//                    filteredDevices.add(bt)
//
//                }
//            }
//
//        }
//    }



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
            updateLocation(loc)
            // add what can see gps
        }

        override fun onProviderEnabled(provider: String) {

        }

        override fun onProviderDisabled(provider: String) {

        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {

        }
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


    fun connectAndPlayNotify(device: BluetoothDevice){
        SUPER_BLE_DEVICE = device
        // if no connected - connect
        if (CURRENT_STATE_OF_RECORDING == CurrentStateOfRecording.NO_CONNECTED || CURRENT_STATE_OF_RECORDING == CurrentStateOfRecording.LOSS_CONNECTION_AND_WAIT_NEW){
            Log.i("ccc","make connectt!!>>${device.name} is connected: ${bleManager?.isConnected == true} is bonded: ${device.bondState != 12} ")
            bleDevice = device
            //FORBACKGRND_BLE_DEVICE = device

            toastShow("Connecting to: ${bleDevice?.name}",Color.YELLOW,this@EndlessService)

            if (bleManager?.isConnected == false) {
                reconnect()
                // if already connected, make notify
                justNotify()
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
            Log.i("ccc","make connectt!! NEW:: is connected: ${bleManager?.isConnected == true} is bonded: ${device.bondState != 12} ")



        }
    }

    fun reconnect() {
        if (bleDevice == null)
            return

        bleManager?.connect(bleDevice!!)
            ?.useAutoConnect(true)
            ?.enqueue()

    }

    fun justNotify(){
        try {
            if (! bleManager!!.isConnected ){
                CURRENT_STATE_OF_RECORDING = CurrentStateOfRecording.LOSS_CONNECTION_AND_WAIT_NEW
            }
        }catch (e: Exception){
            CURRENT_STATE_OF_RECORDING = CurrentStateOfRecording.LOSS_CONNECTION_AND_WAIT_NEW
        }

        Log.i("sss","make NOTIFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF")
        if (CURRENT_STATE_OF_RECORDING == CurrentStateOfRecording.CONNECTED_BUT_NO_RECORDING){
            Log.i("ccc","try to connect UUID notif")
            bleManager?.notifyCharacteristic(true,UUID.fromString("74ab521e-060d-26df-aa64-cf4df2d0d641"))

        }

    }


    fun disconnectOfBleDevice(){
        bleManager?.notifyCharacteristic(false,UUID.fromString("74ab521e-060d-26df-aa64-cf4df2d0d641"))

        bleManager?.disconnect()?.enqueue()
        //bleDevice = null

    }

    private fun stopService() {

        log("Stopping the foreground service")
        Toast.makeText(this, "Service stopping", Toast.LENGTH_SHORT).show()
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

    }

    private fun unBondDevice(){
        toastShow("UNBONDING.. [ ${bleDevice?.name} ]",Color.BLUE,this@EndlessService)
        Log.i("uuu","uuu unBondDevice")             //74ab521e-060d-26df-aa64-cf4df2d0d643
        GlobalScope.launch {

            bleManager?.notifyCharacteristic(false,UUID.fromString("74ab521e-060d-26df-aa64-cf4df2d0d641"))
            delay(1000)
            bleManager?.writeCharacteristic( UUID.fromString("74ab521e-060d-26df-aa64-cf4df2d0d643"),"01")
            delay(1000)
            bleDevice?.removeBond()
            delay(1500)
            disconnectOfBleDevice()
            alreadyBondedDevices = btAdapter.bondedDevices
        }
        toastShow("successful UNBONDED",Color.GREEN,this@EndlessService)

        //bleManager?.notifyCharacteristic(true,)
        //stopService()
        //disconnectOfBleDevice()
    }

    private var a = 0
    private var b = 1

    private lateinit var notificationManager : NotificationManager
    private val notificationChannelId = "avtelma1"
    private val notificationChannelId2 = "avtelma2"

    private var builder = NotificationCompat.Builder(this, notificationChannelId)
        .setContentTitle("AVTelma")
        .setContentText("\uD83D\uDD34 Working..")
       // .setContentIntent(pendingIntent)
        .setSmallIcon(com.avtelma.backblelogger.R.drawable.ic_baseline_bluetooth_drive_24)
        //.setTicker("Ticker text")
        .setPriority(Notification.PRIORITY_HIGH) // for under android 26 compatibility
        .setOnlyAlertOnce(true) // ATTENTION!!!
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
            builder2.setContentText("$msg spd: $CURRENT_SPEED")
            notificationManager.notify(notificationChannelId2,2,builder2.build())

        }
    }

//    private fun refreshMainNotification(msg : String){
//
//        builder.setContentText("\uD83D\uDD34 Working.. | $msg")
//        notificationManager.notify(notificationChannelId,0, builder.build());
//    }

    private fun createNotification(): Notification {

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // depending on the Android API that we're dealing with we will have
        // to use a specific method to create the notification

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // coz notif channels added in android 8.0
            val channel = NotificationChannel(
                notificationChannelId,
                "AVTelma_MainIndicatorOfWork",
                NotificationManager.IMPORTANCE_HIGH
            ).let {
                it.description = "Main Indicator of Service"
                it.enableLights(true)
                it.lightColor = Color.RED
                it.enableVibration(true)
                it.vibrationPattern = longArrayOf(100, 200, 500, 100, 100, 100, 100, 100, 100, 300, 400, 500)
                it
            }
            //notificationManager.createNotificationChannel(channel)

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
        val pendingIntent: PendingIntent = Intent(this,RECORD_ACTIVITY).let { notificationIntent ->
            PendingIntent.getActivity(this, 0, notificationIntent, 0)
        }

//        builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Notification.Builder(
//            this,
//            notificationChannelId
//        ) else Notification.Builder(this)

        //////////
        val broadcastIntent = Intent(this, CloseServiceReceiver::class.java)
        //broadcastIntent.putExtra("toastMessage", android.R.id.message)

        val actionIntent = PendingIntent.getBroadcast(
            this,
            0, Intent(this, CloseServiceReceiver::class.java), PendingIntent.FLAG_UPDATE_CURRENT
        )
        val actionIntent2 = PendingIntent.getBroadcast(
            this,
            0, Intent(this, UnBondingReceiver::class.java), PendingIntent.FLAG_UPDATE_CURRENT
        )
        var actionX: NotificationCompat.Action =
            NotificationCompat.Action.Builder(com.avtelma.backblelogger.R.drawable.ic_stop_rec, "Stop", actionIntent).build()
//        val remoteViews = RemoteViews(packageName, android.R.layout.activity_list_item)
//        remoteViews.setTextViewText(R.id.text,"d")


        return builder
            .setContentTitle("AVTelma")
            .setContentText("\uD83D\uDD34 Working..")
            .setContentIntent(pendingIntent)
            .setSmallIcon(com.avtelma.backblelogger.R.drawable.ic_baseline_bluetooth_drive_24)
            //.setTicker("Ticker text")
            .setPriority(Notification.PRIORITY_HIGH) // for under android 26 compatibility
            .setOnlyAlertOnce(true) // ATTENTION!!!
            .addAction(com.avtelma.backblelogger.R.drawable.ic_stop_rec,"stop",actionIntent)
            .addAction(com.avtelma.backblelogger.R.drawable.ic_stop_rec,"unbonding",actionIntent2)

            .build()
    }




}
