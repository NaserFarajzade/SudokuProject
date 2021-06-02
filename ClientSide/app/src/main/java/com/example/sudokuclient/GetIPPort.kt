package com.example.sudokuclient


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sudokuclient.ui.theme.SudokuClientTheme
import java.lang.Exception


class GetIPPort : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent = Intent(this , MainActivity ::class.java)

        setContent {
            SudokuClientTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    GetIPAndPort()
                }
            }
        }
    }

    @Preview
    @Composable
    private fun GetIPAndPort() {
        val ip = remember { mutableStateOf("") }
        val port = remember { mutableStateOf("") }

        Column(
            modifier = Modifier.fillMaxSize(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            TextField(
                value = ip.value ,
                onValueChange = {
                    ip.value = it
                },
                Modifier.padding(0.dp , 10.dp),
                label = { Text(text = "ip")}
            )

            TextField(
                value = port.value ,
                onValueChange = {
                    port.value = it
                },
                Modifier.padding(0.dp , 10.dp),
                label = { Text(text = "port")}
            )

            Button(
                onClick = {
                    if (isValidIP(ip.value) && isValidPort(port.value)){
                        intent.putExtra("ip", ip.value)
                        intent.putExtra("port", port.value)
                        startActivity(intent)
                    }
                },
                Modifier.padding(0.dp , 10.dp),

            ) {
                Text(text = "CONNECT")
            }
        }
    }

    private fun isValidPort(value: String): Boolean {
        if (value ==null ||value.isEmpty()){
            Toast.makeText(
                this@GetIPPort,
                "enter port",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        if (value.length > 5 || value.toInt()<0 || value.toInt()>65535){
            Toast.makeText(
                this@GetIPPort,
                "port is invalid",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        return true
    }

    private fun isValidIP(value: String): Boolean {
        try {
            if (value ==null ||value.isEmpty()){
                Toast.makeText(
                    this@GetIPPort,
                    "enter ip",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
            var parts = value.split(".")
            if (parts.size != 4){
                Toast.makeText(
                    this@GetIPPort,
                    "ip should be like xxx.xxx.xxx.xxx",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
            for (i in 0..3){
                var part = parts[i].toInt()
                if (part<0 || part>255){
                    Toast.makeText(
                        this@GetIPPort,
                        "each part of ip is between 0 and 255",
                        Toast.LENGTH_LONG
                    ).show()
                    return false
                }
            }
            return true
        }catch (e : Exception){
            Toast.makeText(
                this@GetIPPort,
                "ip should be number",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
    }

}


