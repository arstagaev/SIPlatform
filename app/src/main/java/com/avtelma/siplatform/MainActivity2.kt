package com.avtelma.siplatform

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avtelma.siplatform.aport.Router
import com.avtelma.siplatform.ui.theme.SIPlatformTheme

var BIG_SHARED_STR = mutableStateOf("")


class MainActivity2 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //var BIG_STR = ""
        setContent {
            SIPlatformTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    var textState = remember { mutableStateOf("") }
                    var BIG_STR = remember { BIG_SHARED_STR }
                    val scroll = rememberScrollState(0)

                    Column(modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Green)){
//                        Box(
//                            modifier =  Modifier.weight(1f).background(Blue)){
//                            Text(text = "Weight = 1", color = Color.White)
//                        }

                        Text(
                            text = BIG_STR.value,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(10f)
                                .background(Color.Black).horizontalScroll(scroll), color = Color.Green
                        )
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(90.dp)
                                .weight(1f)) {


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
                                },
                                Modifier
                                    .fillMaxSize()
                                    .weight(1f)) {  }

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


