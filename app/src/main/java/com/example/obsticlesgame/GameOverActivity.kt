package com.example.obsticlesgame

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class GameOverActivity: AppCompatActivity() {
    private lateinit var START_AGAIN_BTN : MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_over)
        findViews()
        initViews()
    }

    private fun initViews() {
        START_AGAIN_BTN.setOnClickListener{View -> startAgain()}
    }

    private fun findViews() {
        START_AGAIN_BTN = findViewById(R.id.START_AGAIN_BTN)
    }

    private fun startAgain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
