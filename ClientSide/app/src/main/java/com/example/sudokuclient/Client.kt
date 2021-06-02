package com.example.sudokuclient

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket


class Client(
    val mainActivity: MainActivity,
    val ip: String,
    val port: Int,
    table: Array<Array<MutableState<Int>>>?,
    score: MutableState<String>,
    opponentScore: MutableState<String>,
    turn: MutableState<Boolean>
)
    :Runnable,SetCellsValue{

    var server: Socket? = null

    private var input: BufferedReader? = null
    private var output: PrintWriter? = null

    private var line: String? = null
    
    private var table : Array<Array<MutableState<Int>>>? = table
    private var score = score
    private var opponentScore = opponentScore
    private var turn = turn

    var clientNumber = -1

    private fun connectToServer() {
        var scanning = true
        while (scanning) {
            try {
                Log.i("ip",ip)
                Log.i("port",port.toString())
                server = Socket(ip, port)
                println("Client connected to server successfully")
                scanning = false
            } catch (e: IOException) {
                println("Connect failed, waiting and trying again")
                try {
                    //pipe!!.send("in catch 1")
                    Thread.sleep(2000) //2 seconds
                } catch (ie: InterruptedException) {
                    ie.printStackTrace()
                }
            }
        }

        try {
            input = BufferedReader(
                InputStreamReader(
                    server!!.getInputStream()
                )
            )
            output = PrintWriter(
                server!!.getOutputStream(),
                true
            )
        } catch (e: IOException) {
            println("Read/write failed")
            e.printStackTrace()
        }
    }

    override fun run() {
        connectToServer()

        while (true) {
            try {
                line = input!!.readLine()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            if (line!!.contains("turn")) {
                setTurn(line!!)
            } else if (line!!.contains("nums")) {
                createTable(line!!)
            } else if (line!!.contains("youare")) {
                setClientNumber(line!!)
            } else if (line!!.contains("wrong")) {
                wrongAnswer()
            } else if (line!!.contains("score")) {
                setScores(line!!)
            } else if (line!!.contains("winner")) {
                showWinner(line!!)
            }
        }
    }

    private fun createTable(line: String) {
        var nums = line.split(" ")
        for (i in 0 until com.example.sudokuclient.table!!.size) {
            for (j in 0 until com.example.sudokuclient.table!![0].size) {
                com.example.sudokuclient.table!![i][j].value = nums.get(i * com.example.sudokuclient.table!!.size + j + 1).toInt()
            }
        }
    }

    private fun setTurn(line: String) {
        if (line.contains("turn cl0")){
            turn.value = clientNumber == 0
        }else if (line.contains("changed")){
            turn.value = !turn.value
        }
    }

    private fun setClientNumber(line: String) {
        clientNumber = line.split(":")[1].toInt()
    }

    private fun wrongAnswer() {
        mainActivity.runOnUiThread(Runnable {
            Toast.makeText(mainActivity,"wrong Answer" , Toast.LENGTH_LONG).show()
        })
    }

    private fun setScores(line: String) {
        if (clientNumber == 0){
            myScore.value = line.split(":")[1]
            opponentScore.value =line.split(":")[2]
        }else if(clientNumber == 1){
            myScore.value = line.split(":")[2]
            opponentScore.value =line.split(":")[1]
        }
    }

    private fun showWinner(line: String) {
        mainActivity.runOnUiThread(Runnable {
            var winText = if (line.contains("no")){
                "no one win's"
            }else if(line.contains("cl0")){
                if (clientNumber == 0){
                    "YOU WON..."
                }else{
                    "YOU LOST..."
                }
            }else{
                if (clientNumber == 1){
                    "YOU WON..."
                }else{
                    "YOU LOST..."
                }
            }
            Toast.makeText(mainActivity, winText , Toast.LENGTH_LONG).show()
        })
        turn.value = false
    }

    override fun send(msg: String) {
        Thread(Runnable {
            output!!.println(msg)
        }).start()
    }
}
