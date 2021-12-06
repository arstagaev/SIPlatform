package com.avtelma.siplatform

import android.Manifest
import android.app.AppOpsManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.app.ActivityCompat
import androidx.core.app.ComponentActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.avtelma.siplatform.ext.isPermanentlyDenied
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.fixedRateTimer


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun callPermissions(): Boolean {
    val permissionsState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        rememberMultiplePermissionsState(
            permissions = listOf(
    //            Manifest.permission.RECORD_AUDIO,
    //            Manifest.permission.CAMERA,
//                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,

            )
        )
    } else {
        rememberMultiplePermissionsState(
            permissions = listOf(
                //            Manifest.permission.RECORD_AUDIO,
                //            Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH_ADMIN
            )
        )
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(

        key1 = lifecycleOwner,
        effect = {
            val observer = LifecycleEventObserver { _, event ->
                if(event == Lifecycle.Event.ON_START) {
                    permissionsState.launchMultiplePermissionRequest()
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    )
    var allPermissionsHave = true

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {

        //permissionsState.permissions
        permissionsState.permissions.forEach { perm ->

            when (perm.permission) {
//                Manifest.permission.CAMERA -> {
//                    when {
//                        perm.hasPermission -> {
//                            Text(text = "Camera permission accepted")
//                            //allPermissionsHave = true
//                        }
//                        perm.shouldShowRationale -> {
//                            allPermissionsHave = false
//                            Text(
//                                text = "Camera permission is needed" +
//                                        "to access the camera"
//                            )
//                        }
//                        perm.isPermanentlyDenied() -> {
//                            allPermissionsHave = false
//                            Text(
//                                text = "Camera permission was permanently" +
//                                        "denied. You can enable it in the app" +
//                                        "settings."
//                            )
//                        }
//                    }
//                }
//                Manifest.permission.RECORD_AUDIO -> {
//                    when {
//                        perm.hasPermission -> {
//                            //allPermissionsHave = true
//                            Text(text = "Record audio permission accepted")
//                        }
//                        perm.shouldShowRationale -> {
//                            allPermissionsHave = false
//                            Text(
//                                text = "Record audio permission is needed" +
//                                        "to access the camera"
//                            )
//                        }
//                        perm.isPermanentlyDenied() -> {
//                            allPermissionsHave = false
//                            Text(
//                                text = "Record audio permission was permanently" +
//                                        "denied. You can enable it in the app" +
//                                        "settings."
//                            )
//                        }
//                    }
//                }
                Manifest.permission.WRITE_EXTERNAL_STORAGE -> {

                    when {
                        perm.hasPermission -> {
                            //allPermissionsHave = true
                            Text(text = "WRITE_EXTERNAL_STORAGE permission accepted")
                        }
                        perm.shouldShowRationale -> {
                            allPermissionsHave = false
                            Text(
                                text = "WRITE_EXTERNAL_STORAGE permission is needed" +
                                        "to access the camera"
                            )
                        }
                        perm.isPermanentlyDenied() -> {
                            allPermissionsHave = false
                            Text(
                                text = "WRITE_EXTERNAL_STORAGE permission was permanently" +
                                        "denied. You can enable it in the app" +
                                        "settings."
                            )
                        }
                    }
                }
                Manifest.permission.ACCESS_FINE_LOCATION -> {

                    when {
                        perm.hasPermission -> {
                            //allPermissionsHave = true
                            Text(text = "WRITE_EXTERNAL_STORAGE permission accepted")
                        }
                        perm.shouldShowRationale -> {
                            allPermissionsHave = false
                            Text(
                                text = "WRITE_EXTERNAL_STORAGE permission is needed" +
                                        "to access the camera"
                            )
                        }
                        perm.isPermanentlyDenied() -> {
                            allPermissionsHave = false
                            Text(
                                text = "WRITE_EXTERNAL_STORAGE permission was permanently" +
                                        "denied. You can enable it in the app" +
                                        "settings."
                            )
                        }
                    }
                }

                Manifest.permission.BLUETOOTH_CONNECT -> {

                    when {
                        perm.hasPermission -> {
                            //allPermissionsHave = true
                            Text(text = "BLUETOOTH_CONNECT permission accepted")
                        }
                        perm.shouldShowRationale -> {
                            allPermissionsHave = false
                            Text(
                                text = "BLUETOOTH_CONNECT permission is needed" +
                                        "to access the camera"
                            )
                        }
                        perm.isPermanentlyDenied() -> {
                            allPermissionsHave = false
                            Text(
                                text = "BLUETOOTH_CONNECT permission was permanently" +
                                        "denied. You can enable it in the app" +
                                        "settings."
                            )
                        }
                    }
                }
                Manifest.permission.BLUETOOTH_SCAN -> {

                    when {
                        perm.hasPermission -> {
                            //allPermissionsHave = true
                            Text(text = "BLUETOOTH_SCAN permission accepted")
                        }
                        perm.shouldShowRationale -> {
                            allPermissionsHave = false
                            Text(
                                text = "BLUETOOTH_SCAN permission is needed" +
                                        "to access the camera"
                            )
                        }
                        perm.isPermanentlyDenied() -> {
                            allPermissionsHave = false
                            Text(
                                text = "BLUETOOTH_SCAN permission was permanently" +
                                        "denied. You can enable it in the app" +
                                        "settings."
                            )
                        }
                    }
                }
                Manifest.permission.BLUETOOTH_ADMIN -> {

                    when {
                        perm.hasPermission -> {
                            //allPermissionsHave = true
                            Text(text = "BLUETOOTH_ADMIN permission accepted")
                        }
                        perm.shouldShowRationale -> {
                            allPermissionsHave = false
                            Text(
                                text = "BLUETOOTH_ADMIN permission is needed" +
                                        "to access the camera"
                            )
                        }
                        perm.isPermanentlyDenied() -> {
                            allPermissionsHave = false
                            Text(
                                text = "BLUETOOTH_ADMIN permission was permanently" +
                                        "denied. You can enable it in the app" +
                                        "settings."
                            )
                        }
                    }
                }

            }
        }
        if (!allPermissionsHave) {
            Text(
                text = "some permissions dont accepted",
                color = Color.White,
                modifier = Modifier.background(Color.Red)
            )
        }else {
            Text(
                text = "All Permissions accepted",
                color = Color.White,
                modifier = Modifier.background(Color.Green)
            )
        }


    }
    LaunchedEffect(true) {


        delay(3000)

    }
    return !allPermissionsHave
}


//////////
const val MANAGE_EXTERNAL_STORAGE_PERMISSION_REQUEST = 1
const val READ_EXTERNAL_STORAGE_PERMISSION_REQUEST = 2
// AppOpsManager.OPSTR_MANAGE_EXTERNAL_STORAGE is a @SystemAPI at the moment
// We should remove the annotation for applications to avoid hardcoded value
const val MANAGE_EXTERNAL_STORAGE_PERMISSION = "android:manage_external_storage"

@RequiresApi(30)
fun checkStoragePermissionApi30(activity: ComponentActivity): Boolean {
    val appOps = activity.getSystemService(AppOpsManager::class.java)
    val mode = appOps.unsafeCheckOpNoThrow(
        MANAGE_EXTERNAL_STORAGE_PERMISSION,
        activity.applicationInfo.uid,
        activity.packageName
    )

    return mode == AppOpsManager.MODE_ALLOWED
}

@RequiresApi(30)
fun requestStoragePermissionApi30(activity: ComponentActivity) {
    val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)

    activity.startActivityForResult(intent, MANAGE_EXTERNAL_STORAGE_PERMISSION_REQUEST)
}

@RequiresApi(19)
fun checkStoragePermissionApi19(activity: MainActivity): Boolean {
    val status =
        ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
    //AppCompatActivity
    return status == PackageManager.PERMISSION_GRANTED
}

@RequiresApi(19)
fun requestStoragePermissionApi19(activity: ComponentActivity) {
    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    ActivityCompat.requestPermissions(
        activity,
        permissions,
        READ_EXTERNAL_STORAGE_PERMISSION_REQUEST
    )
}