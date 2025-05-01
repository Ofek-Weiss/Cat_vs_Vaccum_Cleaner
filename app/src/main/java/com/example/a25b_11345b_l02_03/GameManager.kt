package com.example.a25b_11345b_l02_03

import android.Manifest
import android.content.Context
import android.os.*
import android.widget.Toast
import androidx.annotation.RequiresPermission
import kotlin.random.Random

class GameManager(
    private val context: Context,
    private val handler: Handler,
    private val updateCallback: () -> Unit
) {

    private val ROWS = 6
    private val COLS = 3
    private val UPDATE_INTERVAL = 1000L

    private val logicMatrix = Array(ROWS) { IntArray(COLS) { 0 } }
    private val catLogicArray = IntArray(COLS) { 0 }

    private var catPosition = COLS / 2
    private var lives = 3
    private var iteration = 0
    private var isGameRunning = false
    private val random = Random

    private val updateObstaclePosition = object : Runnable {
        override fun run() {
            if (!isGameRunning) return

            moveObstaclesDown()
            clearTopRow()
            if (iteration % 2 == 0) {
                addNewObstacles()
            }
            iteration++
            checkCollisions()
            updateCallback()
            handler.postDelayed(this, UPDATE_INTERVAL)
        }
    }

    fun startGame() {
        if (!isGameRunning) {
            isGameRunning = true
            handler.post(updateObstaclePosition)
        }
    }

    fun stopGame() {
        if (isGameRunning) {
            isGameRunning = false
            handler.removeCallbacks(updateObstaclePosition)
        }
    }

    fun resetGame() {
        catPosition = COLS / 2
        lives = 3
        iteration = 0
        isGameRunning = false

        for (row in logicMatrix.indices) {
            for (col in logicMatrix[row].indices) {
                logicMatrix[row][col] = 0
            }
        }

        for (col in catLogicArray.indices) {
            catLogicArray[col] = 0
        }

        catLogicArray[catPosition] = 1
    }

    fun moveCat(direction: Int) {
        catLogicArray[catPosition] = 0
        catPosition += direction
        catPosition = catPosition.coerceIn(0, COLS - 1)
        catLogicArray[catPosition] = 1
    }

    fun getLogicMatrix(): Array<IntArray> = logicMatrix
    fun getCatLogicArray(): IntArray = catLogicArray
    fun getLives(): Int = lives

    private fun moveObstaclesDown() {
        for (row in ROWS - 1 downTo 1) {
            for (col in 0 until COLS) {
                logicMatrix[row][col] = logicMatrix[row - 1][col]
            }
        }
    }

    private fun clearTopRow() {
        for (col in 0 until COLS) {
            logicMatrix[0][col] = 0
        }
    }

    private fun addNewObstacles() {
        val randomCol = random.nextInt(COLS)
        logicMatrix[0][randomCol] = 1
    }

    @RequiresPermission(Manifest.permission.VIBRATE)
    private fun checkCollisions() {
        for (col in 0 until COLS) {
            if (logicMatrix[ROWS - 1][col] == 1 && catLogicArray[col] == 1) {
                lives--
                toastAndVibrate()

                if (lives <= 0) {
                    stopGame()
                    updateCallback()
                    Toast.makeText(context, "Game Over!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    @RequiresPermission(Manifest.permission.VIBRATE)
    private fun toastAndVibrate() {
        Toast.makeText(context, "hit", Toast.LENGTH_SHORT).show()
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(300)
        }
    }
}
