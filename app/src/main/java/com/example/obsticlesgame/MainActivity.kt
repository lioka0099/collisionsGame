package com.example.obsticlesgame

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.GridLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.lifecycleScope
import com.example.obsticlesgame.utilities.Constants.GameGrid
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private var rabbitRow : Int = GameGrid.RABBIT_START_ROW
    private var rabbitCol : Int = GameGrid.RABBIT_START_COL
    private var gameOver : Boolean = false
    private var collisionCounter : Int = 0
    private var gamePaused : Boolean = false

    private lateinit var main_IMG_hearts: Array<AppCompatImageView>
    private lateinit var main_GAME_GRID: Array<Array<AppCompatImageView>>
    private lateinit var main_BTN_left: FloatingActionButton
    private lateinit var main_BTN_right: FloatingActionButton

    private lateinit var timerJob: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViews()
        initViews()
    }

    private fun findViews() {
        main_IMG_hearts = arrayOf(
            findViewById(R.id.main_IMG_heart1),
            findViewById(R.id.main_IMG_heart2),
            findViewById(R.id.main_IMG_heart3)
        )
        main_BTN_right = findViewById(R.id.main_BTN_right)
        main_BTN_left = findViewById(R.id.main_BTN_left)
        val gameLayout = findViewById<GridLayout>(R.id.game_grid)
        val rows = gameLayout.rowCount
        val cols = gameLayout.columnCount
        main_GAME_GRID = Array(rows) { r ->
            Array(cols) { c ->
                val index = r * cols + c
                gameLayout.getChildAt(index) as AppCompatImageView
            }
        }
    }

    private fun initViews() {
        main_GAME_GRID[rabbitRow][rabbitCol].setImageResource(R.drawable.rabbit)
        main_GAME_GRID[rabbitRow][rabbitCol].visibility = View.VISIBLE
        main_BTN_left.setOnClickListener { View -> moveRabbit(true) }
        main_BTN_right.setOnClickListener { View -> moveRabbit(false) }
        spawnWolfs()
    }

    private fun moveRabbit(toLeft: Boolean) {
        val newCol = if (toLeft) rabbitCol - 1 else rabbitCol + 1
        if (newCol in 0 until GameGrid.COLS) {
            main_GAME_GRID[rabbitRow][rabbitCol].visibility = View.INVISIBLE
            rabbitCol = newCol
            main_GAME_GRID[rabbitRow][rabbitCol].setImageResource(R.drawable.rabbit)
            main_GAME_GRID[rabbitRow][rabbitCol].visibility = View.VISIBLE
        }
    }

    private fun spawnWolfs() {
        if (::timerJob.isInitialized && timerJob.isActive) {
            timerJob.cancel() // Cancel any existing coroutine to avoid duplication
        }
        timerJob = lifecycleScope.launch {
            while (!gameOver) {
                val currentCol = (0..2).random()
                main_GAME_GRID[0][currentCol].setImageResource(R.drawable.wolf)
                //top wolf
                main_GAME_GRID[0][currentCol].visibility = View.VISIBLE
                moveWolfs(currentCol)
                delay(2000)
            }
        }
    }

    private fun moveWolfs(currentCol: Int) {
        lifecycleScope.launch {
            for (row in 1 until GameGrid.ROWS) {
                main_GAME_GRID[row - 1][currentCol].visibility = View.INVISIBLE
                main_GAME_GRID[row][currentCol].setImageResource(R.drawable.wolf)
                main_GAME_GRID[row][currentCol].visibility = View.VISIBLE

                delay(500)

                if (row == rabbitRow && currentCol == rabbitCol) {
                    main_GAME_GRID[row][currentCol].visibility = View.INVISIBLE
                    handleCollision()
                }
            }
            main_GAME_GRID[main_GAME_GRID.size - 1][currentCol].visibility = View.INVISIBLE
            main_GAME_GRID[rabbitRow][rabbitCol].setImageResource(R.drawable.rabbit)
            main_GAME_GRID[rabbitRow][rabbitCol].visibility = View.VISIBLE
        }
    }


    private fun handleCollision() {
        if (gamePaused) return
        main_IMG_hearts[collisionCounter].visibility = View.INVISIBLE
        collisionCounter++

        SignalManager.getInstance().toast("The wolf ate you!")
        SignalManager.getInstance().vibration()

        if (collisionCounter == GameGrid.HEART_COUNT)
            handleGameOver()
    }

    private fun handleGameOver() {
        gameOver = true
        val intent = Intent(this, GameOverActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onPause() {
        super.onPause()
        timerJob.cancel()
        gamePaused = true
    }

    override fun onResume() {
        super.onResume()
        gamePaused = false
        if (!gameOver)
            spawnWolfs()
    }
}
