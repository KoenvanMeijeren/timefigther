package com.koenvanmeijeren.timefighter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    internal lateinit var tapMeButton: Button
    internal lateinit var timeLeft: TextView
    internal lateinit var gameScore: TextView
    internal lateinit var countDownTimer: CountDownTimer

    internal var score: Int = 0
    internal var gameStarted: Boolean = false
    internal var timeLeftOnTimer: Long = 60000

    internal val initialCountDown: Long = 60000
    internal val countDownInterval: Long = 1000

    companion object {
        private const val SCORE_KEY = "SCORE_KEY"
        private const val TIME_LEFT_KEY = "TIME_LEFT_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tapMeButton = findViewById(R.id.tapMeButton)
        timeLeft = findViewById(R.id.timeLeft)
        gameScore = findViewById(R.id.gameScore)

        tapMeButton.setOnClickListener {view ->
            startGame()
            incrementScore()
        }

        determineGameMustBeRestarted(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(SCORE_KEY, score)
        outState.putLong(TIME_LEFT_KEY, timeLeftOnTimer)
        countDownTimer.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun determineGameMustBeRestarted(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            score = savedInstanceState.getInt(SCORE_KEY)
            timeLeftOnTimer = savedInstanceState.getLong(TIME_LEFT_KEY)

            return restoreGame()
        }

        return resetGame()
    }

    private fun resetGame() {
        score = 0

        changeIntPlaceholder(R.string.your_score, gameScore, score)
        changeLongPlaceholder(R.string.time_left, timeLeft, initialCountDown / 1000)

        countDownTimer = object : CountDownTimer(initialCountDown, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftOnTimer = millisUntilFinished
                changeLongPlaceholder(R.string.time_left, timeLeft, millisUntilFinished / 1000)
            }

            override fun onFinish() {
                endGame()
            }
        }

        gameStarted = false
    }

    private fun restoreGame() {
        changeIntPlaceholder(R.string.your_score, gameScore, score)
        changeLongPlaceholder(R.string.time_left, timeLeft, timeLeftOnTimer / 1000)

        countDownTimer = object : CountDownTimer(timeLeftOnTimer, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftOnTimer = millisUntilFinished
                changeLongPlaceholder(R.string.time_left, timeLeft, millisUntilFinished / 1000)
            }

            override fun onFinish() {
                endGame()
            }
        }

        countDownTimer.start()
        gameStarted = true
    }

    private fun changeIntPlaceholder(IntText: Int, Text: TextView, newValue: Int) {
        Text.text = getString(IntText, newValue)
    }

    private fun changeLongPlaceholder(IntText: Int, Text: TextView, newValue: Long) {
        Text.text = getString(IntText, newValue)
    }

    private fun incrementScore() {
        score += 1
        gameScore.text = getString(R.string.your_score, score)
    }

    private fun startGame() {
        if (!gameStarted) {
            countDownTimer.start()
            gameStarted = true
        }
    }

    private fun endGame() {
        Toast.makeText(this, getString(R.string.game_over, score), Toast.LENGTH_LONG).show()
        resetGame()
    }
}
