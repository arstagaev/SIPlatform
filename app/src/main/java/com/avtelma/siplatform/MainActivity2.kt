package com.avtelma.siplatform

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avtelma.backblelogger.logrecorder.tools.VariablesAndConstants
import com.avtelma.siplatform.aport.Router
import com.avtelma.siplatform.ui.theme.SIPlatformTheme

var BIG_SHARED_STR = mutableStateOf("")


class MainActivity2 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //var BIG_STR = ""
        whatCommandsWeHave()
        setContent {
            SIPlatformTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    var textState = remember { mutableStateOf("") }
                    var BIG_STR = remember   { BIG_SHARED_STR           }
                    var scroll = rememberScrollState()

                    Column(modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)) {
//                        Box(
//                            modifier =  Modifier.weight(1f).background(Blue)){
//                            Text(text = "Weight = 1", color = Color.White)
//                        }

                        Text(
                            text = BIG_STR.value,
                            fontSize = 10.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(2.dp)
                                .weight(10f)
                                .background(Color.Black)
                                .verticalScroll(ScrollState(scroll.maxValue)),
                            color = Color.Green,
                            overflow = TextOverflow.Clip,

                        )
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(90.dp)
                                .weight(1f)
                                .background(Color.Blue)) {


                            TextField(
                                value = textState.value,
                                onValueChange = { textState.value = it
                                                },
                                //label = { Text("Enter text") },
                                //maxLines = 2,
                                textStyle = TextStyle(color = Color.Black, fontWeight = FontWeight.Bold, //fontSize = TextUnit(30f, TextUnitType.Sp)
                                ),
                                modifier = Modifier.weight(5f)
                            )

                            Button(onClick = {
                                  BIG_STR.value += "\n${textState.value}"
                                  Log.i("ccc","log : ${BIG_STR.value}")
                                  Router().inner(textState.value,this@MainActivity2)
                                  if (BIG_STR.value.length > 10000) {
                                      BIG_STR.value = "cleared"
                                  }
                                },
                                Modifier
                                    .fillMaxSize()
                                    .weight(1f)) {

                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "Localized description",
                                    Modifier.padding(end = 8.dp)
                                )
                            }

                        }
//                        Text(
//                            text = "Very important text",
//                            fontSize = 20.sp,
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .weight(1f)
//                                .background(Color.Red)
//                        )
                    }
                }
            }
        }
    }
}
var TMR = 0
@SuppressLint("MissingPermission")
var timerOfStatus = object : CountDownTimer(1000000,1000) {
    override fun onTick(p0: Long) {
        BIG_SHARED_STR.value += "\n>> state:${VariablesAndConstants.CURRENT_STATE_OF_SERVICE.name},act:${VariablesAndConstants.ACTION_NOW.name},style:${VariablesAndConstants.CONNECTING_STYLE.name}," +
                "aim:${VariablesAndConstants.SUPER_BLE_DEVICE?.name ?: "null"}[${VariablesAndConstants.SUPER_BLE_DEVICE?.address ?: ""}]," +
                "isNotify:${VariablesAndConstants.IS_NOTIFY_TYPE_OF_CHARACTERISTIC}"+
                ",t:${TMR++}"

    }

    override fun onFinish() {
        BIG_SHARED_STR.value += "\nrefresher of status stopped"
        TMR = 0
    }
}

fun whatCommandsWeHave() {
    BIG_SHARED_STR.value +=
        "Common codes and commands:" +
        "\n code|     command    " +
        "\n------------------------------" +
        "\n   12| start realtime status" +
        "\n   13| stop realtime status" +
        "\n     3| start ble connect" +
        "\n     4| stop ble connect"

}


