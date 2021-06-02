package com.example.sudokuclient


import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.Toast

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.sudokuclient.ui.theme.SudokuClientTheme

@SuppressLint("StaticFieldLeak")
var context : Context? = null

var ip = "192.168.1.9"
var port = 4321
private var client: Client? = null

var myScore = mutableStateOf("0")
var opponentScore = mutableStateOf("0")

var turn = mutableStateOf(false)

var table : Array<Array<MutableState<Int>>>? = Array(9) {
    Array(9){
        mutableStateOf(0)
    }
}

private var selectedI = mutableStateOf(-1)
private var selectedJ = mutableStateOf(-1)
private var selectedVal = mutableStateOf(-1)


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        context = this

        ip = intent.getStringExtra("ip").toString()
        port = intent.getIntExtra("port", 4321)

        client = Client(this , ip, port , table , myScore , opponentScore , turn)
        Thread(client).start()

        setContent {
            SudokuClientTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Sudoku()
                }
            }
        }
    }

}




@Composable
private fun Sudoku(){
    Column(
        modifier = Modifier
            .background(Color(188, 181, 181, 0xFF))
            .fillMaxSize(1f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ScoreBoard()
        table()
        numsAndSubmit()
    }
}

@Composable
fun ScoreBoard() {
    Row(
        modifier = Modifier
            .fillMaxWidth(1f)
            .padding(top = 16.dp, bottom = 24.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(5.dp)
                .background(
                    color = Color(241, 99, 99, 0xFF),
                    shape = RoundedCornerShape(8.dp)
                )
                .weight(1f),
            contentAlignment = Alignment.CenterStart
        ){
            Text(
                text = "opponent score\n${opponentScore.value}",
                modifier = Modifier
                    .padding(0.dp, 10.dp)
                    .align(Alignment.Center),
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .padding(5.dp)
                .background(
                    color = Color(147, 241, 99, 0xFF),
                    shape = RoundedCornerShape(8.dp)
                )
                .weight(1f),
            contentAlignment = Alignment.CenterEnd

        ){
            Text(
                text = "your score\n ${myScore.value}",
                modifier = Modifier
                    .padding(0.dp, 10.dp)
                    .align(Alignment.Center),
                textAlign = TextAlign.Center,
                maxLines = 2
            )

        }

    }
}

@Composable
private fun table(){

    Box(
        modifier = Modifier
            .fillMaxWidth(1f)
            .padding(5.dp, 5.dp)
            .background(
                color = Color(0, 0, 0, 0xFF),
                shape = RoundedCornerShape(8.dp)
            )

    ){
        Column(
            modifier = Modifier
                .padding(10.dp) ,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for(i in 0..8){

                var x = 1
                if (i==2 || i==5)
                    x=5

                Row(
                    modifier = Modifier
                        .padding(1.dp, 0.dp, 1.dp, x.dp)
                ) {
                    for (j in 0..8){

                        var x = 1
                        if (j==2 || j==5)
                            x=5

                        Button(
                            modifier = Modifier
                                .weight(1f)
                                .padding(0.dp, 0.dp, x.dp, 0.dp)
                                .border(
                                    if (i == selectedI.value && j == selectedJ.value) {
                                        2.dp
                                    } else {
                                        0.dp
                                    },
                                    Color(0, 0, 0, 0XFF),
                                ),
                            enabled = table!![i][j].value == 0,
                            colors = ButtonDefaults
                                .buttonColors(
                                    backgroundColor =
                                    if (table!![i][j].value == 0) {
                                        Color(130, 142, 255, 0XFF)
                                    } else {
                                        Color(160, 161, 169, 0XFF)
                                    }
                                ),
                            onClick = {
                                selectedI.value = i
                                selectedJ.value = j
                                //Toast.makeText(context, "btnij ${selectedI.value} ${selectedJ.value} ${selectedVal.value}" , Toast.LENGTH_SHORT).show()

                            }
                        ) {

                            var text = if(table!![i][j].value == 0){
                                " "
                            }else{
                                table!![i][j].value.toString()
                            }

                            Text(text = text)
                        }
                    }
                }

            }
        }

    }
}

@Composable
fun numsAndSubmit() {

    Column(Modifier.fillMaxWidth(1f)) {
        Row(Modifier.padding(16.dp)) {
            for (i in 0..8){

                Button(
                    modifier = Modifier
                        .weight(1f)
                        .padding(2.dp),
                    colors = ButtonDefaults
                        .buttonColors(
                            backgroundColor =
                                if (i == selectedVal.value-1){
                                    Color(130,142,255,0XFF)
                                }else{
                                    Color(160,161,169,0XFF)
                                }
                        ),
                    onClick = {
                        selectedVal.value = i+1
                        //Toast.makeText(context, "btnVal ${selectedI.value} ${selectedJ.value} ${selectedVal.value}" , Toast.LENGTH_SHORT).show()

                    },
                    enabled = turn.value
                ) {
                    Text(text = "${i+1}")
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
            //.padding(bottom = 24.dp)
        ) {
            Button(
                modifier = Modifier
                    .fillMaxWidth(0.3f),
                onClick = {
                    if (selectedI.value== -1 || selectedVal.value == -1){
                        Toast.makeText(context, "select a cell or value" , Toast.LENGTH_SHORT).show()
                    }else {
                        var msg = "${selectedI.value} ${selectedJ.value} ${selectedVal.value}"
                        selectedI.value = -1
                        selectedVal.value = -1
                        selectedJ.value = -1

                        client?.send(msg)
                    }
                },
                enabled = turn.value
            ) {

                var text = if(turn.value){
                    "submit"
                }else{
                    "opponent turn"
                }
                Text(
                    text = text,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
