package com.example.a25b_11345b_l02_03

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.example.a25b_11345b_l02_03.GameManager

class MainActivity : AppCompatActivity() {

    private lateinit var gameArea: LinearLayoutCompat
    private lateinit var leftButton: ExtendedFloatingActionButton
    private lateinit var rightButton: ExtendedFloatingActionButton
    private lateinit var handler: Handler
    private lateinit var gameManager: GameManager

    private val grid = Array(6) { arrayOfNulls<ImageView>(3) }
    private val catImages = arrayOfNulls<ImageView>(3)
    private lateinit var hearts: Array<AppCompatImageView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViews()
        initGridAndCleaners()

        handler = Handler(mainLooper)
        gameManager = GameManager(this, handler) {
            updateUI()
        }

        leftButton.setOnClickListener {
            gameManager.moveCat(-1)
            updateUI()
        }

        rightButton.setOnClickListener {
            gameManager.moveCat(1)
            updateUI()
        }
    }

    override fun onStart() {
        super.onStart()
        gameManager.resetGame()
        gameManager.startGame()
    }

    override fun onPause() {
        super.onPause()
        gameManager.stopGame()
    }

    private fun findViews() {
        gameArea = findViewById(R.id.game_area)
        leftButton = findViewById(R.id.main_LBL_left)
        rightButton = findViewById(R.id.main_LBL_right)

        hearts = arrayOf(
            findViewById(R.id.main_IMG_heart1),
            findViewById(R.id.main_IMG_heart2),
            findViewById(R.id.main_IMG_heart3)
        )
    }

    private fun initGridAndCleaners() {
        for (row in 0 until 6) {
            for (col in 0 until 3) {
                val resId = resources.getIdentifier("obstacle_${row}${col}", "id", packageName)
                grid[row][col] = findViewById(resId)
            }
        }

        for (col in 0 until 3) {
            val resId = resources.getIdentifier("cat_$col", "id", packageName)
            catImages[col] = findViewById(resId)
        }
    }

    private fun updateUI() {
        val logicMatrix = gameManager.getLogicMatrix()
        val catLogicArray = gameManager.getCatLogicArray()

        for (row in 0 until 6) {
            for (col in 0 until 3) {
                val cell = grid[row][col]
                if (cell != null) {
                    if (logicMatrix[row][col] == 1) {
                        cell.setImageDrawable(null)
                        cell.setBackgroundResource(R.drawable.vacuum_cleaner)
                        cell.visibility = View.VISIBLE
                    } else {
                        cell.setImageDrawable(null)
                        cell.setBackgroundResource(0)
                        cell.visibility = View.INVISIBLE
                    }
                }
            }
        }

        for (col in 0 until 3) {
            catImages[col]?.visibility =
                if (catLogicArray[col] == 1) View.VISIBLE else View.INVISIBLE
        }

        val lives = gameManager.getLives()
        for (i in hearts.indices) {
            hearts[i].visibility = if (i < lives) View.VISIBLE else View.INVISIBLE
        }
    }
}
