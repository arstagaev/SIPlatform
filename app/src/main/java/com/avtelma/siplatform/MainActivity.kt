package com.avtelma.siplatform

// for a 'val' variable

// for a `var` variable also add
//import com.avtelma.backgroundparser.InputSessionParser.Companion.PIZDEC
import android.Manifest
import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.provider.DocumentsContract
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.avtelma.backblelogger.AVTSIPlatform_EntryPoint
import com.avtelma.backblelogger.enum.Actions
import com.avtelma.backblelogger.enum.ConnectingStyle
import com.avtelma.backblelogger.enum.CurrentStateOfService
import com.avtelma.backblelogger.logrecorder.service.EndlessService
import com.avtelma.backblelogger.logrecorder.tools.VariablesAndConstants
import com.avtelma.backblelogger.logrecorder.tools.VariablesAndConstants.Companion.ACTION_NOW
import com.avtelma.backblelogger.logrecorder.tools.VariablesAndConstants.Companion.CONNECTING_STYLE
import com.avtelma.backblelogger.logrecorder.tools.VariablesAndConstants.Companion.CURRENT_STATE_OF_SERVICE
import com.avtelma.backblelogger.logrecorder.tools.VariablesAndConstants.Companion.LIST_OF_FOUND_DEVICES
import com.avtelma.backblelogger.logrecorder.tools.VariablesAndConstants.Companion.SUPER_BLE_DEVICE
import com.avtelma.backblelogger.logrecorder.tools.log
import com.avtelma.backblelogger.rawparser.service_parsing_events.ParsingActions
import com.avtelma.backblelogger.rawparser.service_parsing_events.ParsingEventService
import com.avtelma.siplatform.ui.theme.SIPlatformTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter


// or just
private const val ENABLE_BLUETOOTH_REQUEST_CODE = 1
private const val LOCATION_PERMISSION_REQUEST_CODE = 2




@SuppressLint("MissingPermission")
class MainActivity : ComponentActivity() {


    private var readPermissionGranted = false
    private var writePermissionGranted = false
    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>
    val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    var REQUEST_ENABLE_BT = 1

    private val isLocationPermissionGranted
        get() = this.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)

    private val isPermissionBleGranted
        get() = this.hasPermission(Manifest.permission.BLUETOOTH_SCAN)

    private fun Context.hasPermission(permissionType: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permissionType) ==
                PackageManager.PERMISSION_GRANTED
    }

//    private val scanResults = mutableListOf<ScanResult>()
    var permiss = mutableStateOf(true)
    var BD : BluetoothDevice? = null

    var timer : CountDownTimer = object : CountDownTimer(1000000,1000){
        override fun onTick(p0: Long) {
            //Timber.i("zzz !!!")
            //Timber.i("zzz ${LIST_OF_FOUND_DEVICES?.joinToString()}")
            if (VariablesAndConstants.LIST_OF_FOUND_DEVICES != null ){

                Timber.i("zzz ${VariablesAndConstants.LIST_OF_FOUND_DEVICES?.toString()}  name super: ${SUPER_BLE_DEVICE?.name}")
                for (i in LIST_OF_FOUND_DEVICES) {
                    if (i.bluetoothDevice.name != null && i.bluetoothDevice.name.toString().contains("itelma",true) == true) {
                        SUPER_BLE_DEVICE = i.bluetoothDevice
                        BD = i.bluetoothDevice
                        Timber.i("I found2!!! ${i.bluetoothDevice.name} ${i.rssi} !!!")
                    }
                }
            }
        }
        override fun onFinish() {}
    }

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        val intent = Intent(baseContext, TestSecondActivity::class.java)
        //var foo : BluetoothDevice? = null


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            requestPermission(ACCESS_BACKGROUND_LOCATION,3)
//        }

        Timber.i("zzz","zzz ${getFilesDir()} or ${getCacheDir()}   // ${getExternalCacheDir()}")

        Log.i("zzz","zzz222 ${getExternalFilesDir("")} or ${getExternalCacheDir()}")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Log.i("zzz","checkStoragePermissionApi30 ${checkStoragePermissionApi30(this)}")
            requestStoragePermissionApi30(this)
        }else {
            Log.i("zzz","checkStoragePermissionApi19 ${checkStoragePermissionApi19(this)}")
        }
        timer.start()
//        try {
//            appendText("wow.txt","777")
//        }catch (e :Exception){
//            Log.e("zzz","zzz  ${e.message}")
//            appendText2("www.txt","888")
//        }






//        runOnUiThread {
//            requestPermission(
//                Manifest.permission.BLUETOOTH_SCAN,
//                1
//            )
//            requestPermission(
//                Manifest.permission.BLUETOOTH_CONNECT,
//                1
//            )
//        }


        setContent {
            var visibleOfPermissions by remember { permiss }
            var MASTER_PADDING = 3.dp
            visibleOfPermissions = callPermissions(applicationContext)
            AnimatedVisibility(
                visible = visibleOfPermissions,
                enter = fadeIn(
                    // Overwrites the initial value of alpha to 0.4f for fade in, 0 by default
                    initialAlpha = 0.4f
                ),
                exit = fadeOut(
                    // Overwrites the default animation with tween
                    animationSpec = tween(durationMillis = 250)
                )
            ) {

                callPermissions(applicationContext)

            }


            SIPlatformTheme {
                // A surface container using the 'background' color from the theme
                Log.i("zzz","BLE: ${isPermissionBleGranted} ")
                if (!visibleOfPermissions){

                }
                AnimatedVisibility(visible = !visibleOfPermissions) {

                    Column(modifier = Modifier.fillMaxSize()) {
                        Button(onClick = {
                            val intentX = Intent(this@MainActivity,MainActivity2::class.java)
                            startActivity(intentX)
                            finish()
                            //commonDocumentDirPath("Rock")

                        }, modifier = Modifier.fillMaxWidth().height(400.dp) .padding(MASTER_PADDING)) {
                            Text(text = "Go to TERMINAL",color = Color.Blue)
                        }

                        Button(onClick = {

                            appendText("Recur32:34:24.txt".replace(Regex(":"),""),"++++++")

                        }, modifier = Modifier.padding(MASTER_PADDING)) {
                            Text(text = "Create file ",color = Color.Blue)
                        }

                        Button(onClick = {
                            AVTSIPlatform_EntryPoint.Builder.connStl(ConnectingStyle.AUTO_BY_BOND).build()
                            launchCommandInService(Actions.UNBOND)

                        }, modifier = Modifier.padding(MASTER_PADDING)) {
                            Text(text = "Start service, UNBOND",color = Color.Blue)
                        }

                        Button(onClick = {
                                AVTSIPlatform_EntryPoint.Builder.connStl(ConnectingStyle.AUTO_BY_BOND).build()
                            //AVTSIPlatform_EntryPoint().setup(ConnectingStyle.AUTO_BY_BOND)
                            launchCommandInService(Actions.START)

                        }, modifier = Modifier.padding(MASTER_PADDING)) {
                            Text(text = "Start service, AUTO_BY_BOND",color = Color.Blue)
                        }

                        Button(onClick = {
                            requestLocationPermission()
                            launchCommandInService(Actions.SCAN_START)

                        }, modifier = Modifier.padding(MASTER_PADDING)) {
                            Text(text = "Start Scanning, and finding SUPER_BLE_DEVICE with Force isLocationPermissionGranted: ${isLocationPermissionGranted}",color = Color.Blue)
                        }

                        Button(onClick = {
                            requestLocationPermission()
                            launchCommandInService(Actions.SCAN_STOP)

                        }, modifier = Modifier.padding(MASTER_PADDING)) {
                            Text(text = "Stop Scanning, and finding SUPER_BLE_DEVICE with Force isLocationPermissionGranted: ${isLocationPermissionGranted}",color = Color.Blue)
                        }

                        Divider(color = Color.Blue, thickness = 1.dp,modifier = Modifier.padding(5.dp))

                        Button(onClick = {

                            //AVTSIPlatform_EntryPoint().setup(ConnectingStyle.AUTO_BY_BOND)
                            launchCommandInService(Actions.NEUTRAL_CONNECTED)

                        }, modifier = Modifier.padding(MASTER_PADDING)) {
                            Text(text = "make, NEUTRAL_CONNECTED",color = Color.Blue)
                        }

                        Button(onClick = {

                            AVTSIPlatform_EntryPoint.connStl(ConnectingStyle.MANUAL).build()
                            CONNECTING_STYLE = ConnectingStyle.MANUAL
                            launchCommandInService(Actions.START)

                        }, modifier = Modifier.padding(MASTER_PADDING)) {
                            Text(text = "make, MANUAL CONNECT",color = Color.Blue)
                        }
                        Row {
                            Button(onClick = {
                                launchCommandInService(Actions.NEUTRAL_CONNECTED,this@MainActivity,1)
                                //AVTSIPlatform_EntryPoint().setup(ConnectingStyle.AUTO_BY_BOND) Actions.TARGET_CONNECT
                                Timber.w("bbb conn${VariablesAndConstants.CHOSEN_BLE_DEVICE?.name}")
                                //launchCommandInService(Actions.TARGET_CONNECT)

                            }, modifier = Modifier.padding(MASTER_PADDING),
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)) {
                                Text(text = "A",color = Color.Magenta)
                            }
                            Button(onClick = {
                                launchCommandInService(Actions.NEUTRAL_CONNECTED,this@MainActivity,0)
                                //AVTSIPlatform_EntryPoint().setup(ConnectingStyle.AUTO_BY_BOND) Actions.TARGET_CONNECT
                                Timber.w("bbb diss${VariablesAndConstants.CHOSEN_BLE_DEVICE?.name}")


                            }, modifier = Modifier.padding(MASTER_PADDING),
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)) {
                                Text(text = "B",color = Color.Magenta)
                            }

                            Button(onClick = {
                                launchCommandInService(Actions.SCAN_START,this@MainActivity)
                                VariablesAndConstants.ACTION_NOW = Actions.TARGET_CONNECT
                            }, modifier = Modifier.padding(MASTER_PADDING),
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)) {
                                Text(text = "just scan tart",color = Color.Magenta)
                            }

                            Button(onClick = {
                                if (VariablesAndConstants.CURRENT_STATE_OF_SERVICE != CurrentStateOfService.RECORDING) {
                                    launchCommandInService(Actions.SCAN_STOP,this@MainActivity)
                                    launchCommandInService(Actions.STOP,this@MainActivity)



                                }
                            }, modifier = Modifier.padding(MASTER_PADDING),
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)) {
                                Text(text = "stop serv scan",color = Color.Magenta)
                            }
                        }
                        /////
                        Row {
                            Button(onClick = {
                                CONNECTING_STYLE = ConnectingStyle.AUTO_BY_SEARCH
                                ACTION_NOW = Actions.START
                                launchCommandInService(Actions.START)
                                //launchCommandInService(Actions.TARGET_CONNECT)

                            }, modifier = Modifier.padding(MASTER_PADDING),
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)) {
                                Text(text = "F",color = Color.Red)
                            }
                            Button(onClick = {
                                launchCommandInService(Actions.NEUTRAL_CONNECTED,this@MainActivity,0)
                                //AVTSIPlatform_EntryPoint().setup(ConnectingStyle.AUTO_BY_BOND) Actions.TARGET_CONNECT
                                Timber.w("bbb diss${VariablesAndConstants.CHOSEN_BLE_DEVICE?.name}")


                            }, modifier = Modifier.padding(MASTER_PADDING),
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)) {
                                Text(text = "G",color = Color.Red)
                            }
                        }

                        Divider(color = Color.Blue, thickness = 1.dp,modifier = Modifier.padding(5.dp))
                        Divider(color = Color.Blue, thickness = 1.dp,modifier = Modifier.padding(5.dp))

                        Button(onClick = {

                            //AVTSIPlatform_EntryPoint().setup(ConnectingStyle.AUTO_BY_BOND)
                            launchCommandInService_RAWPARSER(ParsingActions.FULL_PARSING)

                        }, modifier = Modifier.padding(MASTER_PADDING),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)) {
                            Text(text = "start RawParser FULL_PARSING",color = Color.Blue)
                        }
                        Button(onClick = {
                            //AVTSIPlatform_EntryPoint().setup(ConnectingStyle.AUTO_BY_BOND)
                            //launchCommandInService_RAWPARSER(ParsingActions.START)
                            launchCommandInService_RAWPARSER(ParsingActions.STOP)

                        }, modifier = Modifier
                            .padding(MASTER_PADDING)
                            .background(Color.White),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
                        ) {
                            Text(text = "start RawParser STOP",color = Color.Blue)
                        }

                        Button(onClick = {
                            //AVTSIPlatform_EntryPoint().setup(ConnectingStyle.AUTO_BY_BOND)
                            //launchCommandInService_RAWPARSER(ParsingActions.START)

                            intent.putExtra("foxy", SUPER_BLE_DEVICE)
                            startActivity(intent)

                        }, modifier = Modifier
                            .padding(MASTER_PADDING)
                            .background(Color.Cyan),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Cyan)
                        ) {
                            Text(text = "To new Activity!",color = Color.Blue)
                        }
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(90.dp)
                                .background(Color.Red)
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onPress = { },
                                        onDoubleTap = { /* Called on Double Tap */ },
                                        onLongPress = {
                                            Timber.i("qqq onLongPressonLongPressonLongPressonLongPressonLongPress")
                                        },
                                        onTap = { Timber.i("qqq onPressonPressonPress") }
                                    )
                                }
                        ) {

                        }
                        Button(onClick = {
                            CONNECTING_STYLE = ConnectingStyle.MANUAL
                            launchCommandInService(Actions.SCAN_START,this@MainActivity)

                        }, modifier = Modifier.padding(MASTER_PADDING),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)) {
                            Text(text = "manual connect",color = Color.Yellow)
                        }

                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        //checkPermissionBLE()
        var timer = object : CountDownTimer(100000,1000){
            override fun onTick(p0: Long) {
                Timber.i("zzz>>>>>> ${VariablesAndConstants.TRINITY_FOR_CHART.valuesX} ${VariablesAndConstants.TRINITY_FOR_CHART.valuesZ}")

            }
            override fun onFinish() {}
        }.start()

    }

    override fun onPause() {
        super.onPause()


    }

    override fun onDestroy() {
        if (VariablesAndConstants.CURRENT_STATE_OF_SERVICE != CurrentStateOfService.RECORDING) {

            ACTION_NOW = Actions.FORCE_STOP
            //launchCommandInService(Actions.SCAN_STOP,this)
            launchCommandInService(Actions.FORCE_STOP,this)
        }

        super.onDestroy()

    }


    /**
     * BLE
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ENABLE_BLUETOOTH_REQUEST_CODE -> {
                if (resultCode != Activity.RESULT_OK) {
                    promptEnableBluetooth()
                }
            }
        }
    }

    fun checkPermissionBLE(): Boolean {
        if (bluetoothAdapter == null) {

            // Device doesn't support Bluetooth
            Toast.makeText(applicationContext,"Device doesn't support Bluetooth",Toast.LENGTH_LONG).show()
            return false
        } else {
            if (bluetoothAdapter?.isEnabled == false) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent!!, REQUEST_ENABLE_BT)
            }
            if (bluetoothAdapter?.isEnabled == false) {
                return false
            }else {
                return true
            }
        }
    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        when (requestCode) {
//            LOCATION_PERMISSION_REQUEST_CODE -> {
//                if (grantResults.firstOrNull() == PackageManager.PERMISSION_DENIED) {
//                    requestLocationPermission()
//                } else {
//                    startBleScan()
//                }
//            }
//        }
//    }

    /*******************************************
     * Private functions
     *******************************************/

    private fun promptEnableBluetooth() {
        if (!bluetoothAdapter!!.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH_REQUEST_CODE)
        }
    }



    private fun requestLocationPermission() {
        if (isLocationPermissionGranted) {
            return
        }
        runOnUiThread {
            requestPermission(
                Manifest.permission.ACCESS_FINE_LOCATION,
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }
    private fun Activity.requestPermission(permission: String, requestCode: Int) {
        ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
    }







    fun commonDocumentDirPath(FolderName: String): File? {
        var dir: File? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                    .toString() + "/" + FolderName
            )
        } else {
            dir = File(Environment.getExternalStorageDirectory().toString() + "/" + FolderName)
        }

        // Make sure the path directory exists.
        if (!dir.exists()) {
            // Make it, if it doesn't exit
            val success = dir.mkdirs()
            if (!success) {
                dir = null
            }
        }

        val file = File(dir, "LOLIC2.txt")

        val fileOutputStream = FileOutputStream(file,true)
        val outputStreamWriter = OutputStreamWriter(fileOutputStream)
        outputStreamWriter.append("\n "+"sBody")

        outputStreamWriter.close()
        fileOutputStream.close()


        return dir
    }

    // Request code for creating a PDF document.
    val CREATE_FILE = 1

    private fun createFile(pickerInitialUri: Uri) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
            putExtra(Intent.EXTRA_TITLE, "invoice.pdf")

            // Optionally, specify a URI for the directory that should be opened in
            // the system file picker before your app creates the document.
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }
        startActivityForResult(intent, CREATE_FILE)
    }

    private fun updateOrRequestPermissions() {
        val hasReadPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val hasWritePermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

        readPermissionGranted = hasReadPermission
        writePermissionGranted = hasWritePermission || minSdk29

        val permissionsToRequest = mutableListOf<String>()
        if(!writePermissionGranted) {
            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if(!readPermissionGranted) {
            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if(permissionsToRequest.isNotEmpty()) {
            permissionsLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }


    fun appendText(sFileName: String, sBody: String){
        try {
            val root = File(Environment.getExternalStorageDirectory(), "Arsen")
            if (!root.exists()) {
                root.mkdirs()
            }
            val file = File(root, sFileName)

            val fileOutputStream = FileOutputStream(file,true)
            val outputStreamWriter = OutputStreamWriter(fileOutputStream)
            outputStreamWriter.append("\n "+sBody)

            outputStreamWriter.close()
            fileOutputStream.close()
            //findAndReplacePartOfText(file)

        } catch (e: IOException) {
            Log.e("ccc","ERROR "+ e.message)
            e.printStackTrace()
        }

    }

    fun appendText2(sFileName: String, sBody: String){
        try {
            val root = File(Environment.getExternalStorageDirectory(), "Arsen2")
            if (!root.exists()) {
                root.mkdirs()
            }
            val file = File(root, sFileName)

            val fileOutputStream = FileOutputStream(file,true)
            val outputStreamWriter = OutputStreamWriter(fileOutputStream)
            outputStreamWriter.append("\n "+sBody)

            outputStreamWriter.close()
            fileOutputStream.close()
            //findAndReplacePartOfText(file)

        } catch (e: IOException) {
            Log.e("ccc","ERROR "+ e.message)
            e.printStackTrace()
        }
    }

    fun launchCommandInService(action : Actions) {
        Intent(this, EndlessService::class.java).also {
            it.action = action.name
            //it.putExtra("CS",6)


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                log("Starting the service in >=26 Mode")
                startForegroundService(it)
                return
            }
            log("Starting the service in < 26 Mode")
            startService(it)
        }
    }



    fun launchCommandInService_RAWPARSER(parsingAction : ParsingActions) {
        Intent(this, ParsingEventService::class.java).also {
            it.action = parsingAction.name
            //it.putExtra("CS",6)


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                log("Starting the service in >=26 Mode")
                startForegroundService(it)
                return
            }
            log("Starting the service in < 26 Mode")
            startService(it)
        }
    }

//    fun autoConnectService(code:  Int) {
//        Intent(this, EndlessService::class.java).also {
//            it.action = Actions.MISC.name
//            it.putExtra("CS",code)
//
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                log("Starting the service in >=26 Mode")
//                startForegroundService(it)
//                return
//            }
//            log("Starting the service in < 26 Mode")
//            startService(it)
//        }
//    }
}

fun launchCommandInService(action : Actions, ctx : Context) {
    Intent(ctx, EndlessService::class.java).also {
        it.action = action.name
        //it.putExtra("CS",6)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            log("Starting the service in >=26 Mode")
            ContextCompat.startForegroundService(ctx, it)
            return
        }
        log("Starting the service in < 26 Mode")

        ctx.startService(it)
        //TODO: NEED IMPLEMENT CHANGE INVOCATION
        //AppCompatActivity.startService(it)
    }
}



fun launchCommandInService(action : Actions, ctx : Context, commandCode : Int) {

    Intent(ctx, EndlessService::class.java).also {
        it.action = action.name
        it.putExtra("ble_conn",commandCode)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            log("Starting the service in >=26 Mode")
            ContextCompat.startForegroundService(ctx,it)
            return
        }
        log("Starting the service in < 26 Mode")
        ctx.startService(it)
    }
}