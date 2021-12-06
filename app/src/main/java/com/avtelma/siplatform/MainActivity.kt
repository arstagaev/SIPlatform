package com.avtelma.siplatform

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
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
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.avtelma.siplatform.ui.theme.SIPlatformTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter

// for a 'val' variable
import androidx.compose.runtime.getValue

// for a `var` variable also add
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.avtelma.backblelogger.AVTSIPlatform_EntryPoint
import com.avtelma.backblelogger.enum.Actions
import com.avtelma.backblelogger.enum.ConnectingStyle
import com.avtelma.backblelogger.service.EndlessService
import com.avtelma.backblelogger.tools.log
import com.avtelma.siplatform.MainConstants.Companion.SCAN_DEVICES
import com.avtelma.siplatform.ble.ConnectionEventListener
import com.avtelma.siplatform.ble.ConnectionManager
import timber.log.Timber

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

    private val bleScanner by lazy {
        bluetoothAdapter?.bluetoothLeScanner
    }
    private val isLocationPermissionGranted
        get() = this.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)

    private val isPermissionBleGranted
        get() = this.hasPermission(Manifest.permission.BLUETOOTH_SCAN)

    private fun Context.hasPermission(permissionType: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permissionType) ==
                PackageManager.PERMISSION_GRANTED
    }

    private val scanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .build()

    private var isScanning = false
        set(value) { field = value }

    private val scanResults = mutableListOf<ScanResult>()
    var permiss = mutableStateOf(true)

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        Log.i("zzz","zzz ${getFilesDir()} or ${getCacheDir()}   // ${getExternalCacheDir()}")

        Log.i("zzz","zzz222 ${getExternalFilesDir("")} or ${getExternalCacheDir()}")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Log.i("zzz","checkStoragePermissionApi30 ${checkStoragePermissionApi30(this)}")
            requestStoragePermissionApi30(this)
        }else {
            Log.i("zzz","checkStoragePermissionApi19 ${checkStoragePermissionApi19(this)}")
        }
//        try {
//
//            appendText("wow.txt","777")
//
//        }catch (e :Exception){
//            Log.e("zzz","zzz  ${e.message}")
//
//            appendText2("www.txt","888")
//        }


        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Toast.makeText(applicationContext,"Device doesn't support Bluetooth",Toast.LENGTH_LONG).show()
        } else {
            if (bluetoothAdapter?.isEnabled == false) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent!!, REQUEST_ENABLE_BT)
            }
        }
        ConnectionManager.registerListener(connectionEventListener)

        AVTSIPlatform_EntryPoint().setup(ConnectingStyle.MANUAL,)
        runOnUiThread {
            requestPermission(
                Manifest.permission.BLUETOOTH_SCAN,
                1
            )
        }

        setContent {
            var visibleOfPermissions by remember { permiss }

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

                visibleOfPermissions= callPermissions()

            }


            SIPlatformTheme {
                // A surface container using the 'background' color from the theme
                Log.i("zzz","BLE: ${isPermissionBleGranted} ")
                if (!visibleOfPermissions){
                    if (!isScanning) {
                        Log.i("zzz","BLE: ${isPermissionBleGranted}")
                        startBleScan()
                    }
                }
                AnimatedVisibility(visible = !visibleOfPermissions) {

                    Column(modifier = Modifier.fillMaxSize(),) {
                        Button(onClick = {

                            commonDocumentDirPath("Rock")

                        }) {
                            Text(text = "ROOOCKK",color = androidx.compose.ui.graphics.Color.Red)
                        }

                        Button(onClick = {

                            appendText("Recur.txt","++++++")

                        }) {
                            Text(text = "Create file",color = androidx.compose.ui.graphics.Color.Blue)
                        }

                        Button(onClick = {
                            for (i in SCAN_DEVICES) {

                                if (i.device.name != null && i.device.name.toString().contains("itelma",true) == true){
                                    AVTSIPlatform_EntryPoint().setupSec(i.device)
                                    autoConnectService()
                                }
                            }


                        }) {
                            Text(text = "Start service",color = androidx.compose.ui.graphics.Color.Magenta)
                        }
                    }
                }

            }
        }
    }

    override fun onResume() {
        super.onResume()


    }

    override fun onPause() {
        super.onPause()

        stopBleScan()
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

    private fun startBleScan() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !isLocationPermissionGranted) {
            requestLocationPermission()
        } else {
            scanResults.clear()

            bleScanner?.startScan(null, scanSettings, scanCallback)
            isScanning = true
        }
    }

    private fun stopBleScan() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
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
        bleScanner?.stopScan(scanCallback)
        isScanning = false

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



    /*******************************************
     * Callback bodies
     *******************************************/

    private val scanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val indexQuery = scanResults.indexOfFirst { it.device.address == result.device.address }
            if (indexQuery != -1) { // A scan result already exists with the same address
                scanResults[indexQuery] = result
                //scanResultAdapter.notifyItemChanged(indexQuery)
                scanResults[0].scanRecord?.manufacturerSpecificData
            } else {
                with(result.device) {
                    Timber.i("Found BLE device! Name: ${result.device.name ?: "Unnamed"}, address: $address")
                }
                scanResults.add(result)

                SCAN_DEVICES = scanResults

                //scanResultAdapter.notifyItemInserted(scanResults.size - 1)
            }
        }

        override fun onScanFailed(errorCode: Int) {
            stopBleScan()
            startBleScan()
            Timber.e("onScanFailed: code $errorCode")
        }
    }

    private val connectionEventListener by lazy {
        ConnectionEventListener().apply {
            onConnectionSetupComplete = { gatt ->
                Toast.makeText(applicationContext,"onConnectionSetupComplete",Toast.LENGTH_LONG).show()
//                Intent(this@MainActivity, BleOperationsActivity::class.java).also {
//                    it.putExtra(BluetoothDevice.EXTRA_DEVICE, gatt.device)
//                    startActivity(it)
//                }
//                ConnectionManager.unregisterListener(this)
            }
            onDisconnect = {
                runOnUiThread {
                    Toast.makeText(applicationContext,"onDisconnect",Toast.LENGTH_LONG).show()
//                    alert {
//                        title = "Disconnected"
//                        message = "Disconnected or unable to connect to device."
//                        positiveButton("OK") {}
//                    }.show()
                }
            }
        }
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

    fun autoConnectService() {
        Intent(this, EndlessService::class.java).also {
            it.action = Actions.START.name

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                log("Starting the service in >=26 Mode")
                startForegroundService(it)
                return
            }
            log("Starting the service in < 26 Mode")
            startService(it)
        }
    }
}