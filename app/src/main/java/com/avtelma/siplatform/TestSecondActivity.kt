package com.avtelma.siplatform

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.avtelma.siplatform.ui.theme.SIPlatformTheme
import timber.log.Timber

class TestSecondActivity : ComponentActivity() {
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val foo2: BluetoothDevice? = intent.extras?.getParcelable("foxy")
            val foo3: BluetoothDevice? = intent.extras?.getParcelable("foo3")

            Timber.w("ffffff   | ${foo2.toString()}")
            Timber.w("ffffff   | ${foo2?.name.toString()}")
            Timber.w("ffffff 3 | ${foo3.toString()}")

            SIPlatformTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SIPlatformTheme {
        Greeting("Android")
    }
}