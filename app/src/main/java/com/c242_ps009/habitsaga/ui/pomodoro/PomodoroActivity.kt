package com.c242_ps009.habitsaga.ui.pomodoro

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.c242_ps009.habitsaga.R
import com.c242_ps009.habitsaga.databinding.ActivityPomodoroBinding
import java.util.Locale

class PomodoroActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPomodoroBinding
    private var sessions: Int = 1

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == "UPDATE_UI") {
                val minutes = intent.getIntExtra("minutes", 0)
                val seconds = intent.getIntExtra("seconds", 0)
                val millisUntilFinished = intent.getIntExtra("millisUntilFinished", 0)
                val currentSession = intent.getIntExtra("currentSession", 0)
                val sessions = intent.getIntExtra("sessions", 0 )
                val isBreak = intent.getBooleanExtra("isBreak", false)
                binding.apply {
                    middleText.text = String.format(Locale.US,"%02d:%02d", minutes, seconds)
                    timerProgressBar.progress = millisUntilFinished
                    if (!isBreak) sessionCounter.text = String.format("Session: $currentSession/$sessions")
                    else sessionCounter.text = String.format("[Intermission]\nSession: $currentSession/$sessions")
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPomodoroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (isServiceRunning(PomodoroService::class.java)) {
            binding.apply {
                middleText.translationY = 0f
                timerProgressBar.alpha = 1f
                sessionCounter.alpha = 1f
                resetButton.alpha = 1f
                sessionAmt.alpha = 0f
                decrementButton.alpha = 0f
                incrementButton.alpha = 0f
                startButton.alpha = 0f
            }
        }

        if (sessions == 1) binding.decrementButton.visibility = View.INVISIBLE

        binding.apply {
            sessionAmt.text = String.format(sessions.toString())

            decrementButton.setOnClickListener {
                sessions--
                binding.sessionAmt.text = String.format(sessions.toString())
                if (sessions == 1) binding.decrementButton.visibility = View.INVISIBLE
                else binding.decrementButton.visibility = View.VISIBLE
            }

            incrementButton.setOnClickListener {
                sessions++
                binding.sessionAmt.text = String.format(sessions.toString())
                 binding.decrementButton.visibility = View.VISIBLE
            }

            startButton.setOnClickListener {
                sessions = binding.sessionAmt.text.toString().toInt()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(this@PomodoroActivity, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this@PomodoroActivity, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 99)
                    } else startPomodoroService()
                } else startPomodoroService()
            }

            resetButton.setOnClickListener {
                stopService(Intent(this@PomodoroActivity, PomodoroService::class.java))
                binding.middleText.text = getString(R.string.sessions)
                val moveMiddleText = ObjectAnimator.ofFloat(binding.middleText, "translationY", 0f, -160f).apply {
                    duration = 1000
                    interpolator = DecelerateInterpolator()
                }
                val hideTimerProgressBar = ObjectAnimator.ofFloat(binding.timerProgressBar, "alpha", 0f).setDuration(1000)
                val hideSessionCounter = ObjectAnimator.ofFloat(binding.sessionCounter, "alpha", 0f).setDuration(500)
                val hideResetBtn = ObjectAnimator.ofFloat(binding.resetButton, "alpha", 0f).setDuration(500)
                val showSessionAmtTxt = ObjectAnimator.ofFloat(binding.sessionAmt, "alpha", 1f).setDuration(500)
                val showDecBtn = ObjectAnimator.ofFloat(binding.decrementButton, "alpha", 1f).setDuration(500)
                val showIncBtn = ObjectAnimator.ofFloat(binding.incrementButton, "alpha", 1f).setDuration(500)
                val showStartBtn = ObjectAnimator.ofFloat(binding.startButton, "alpha", 1f).setDuration(500)
                val together = AnimatorSet().apply { playTogether(moveMiddleText, hideTimerProgressBar, hideSessionCounter, hideResetBtn, showSessionAmtTxt, showDecBtn, showIncBtn, showStartBtn) }
                AnimatorSet().apply {
                    play(together)
                    start()
                }
            }
        }

        registerReceiver(broadcastReceiver, IntentFilter("UPDATE_UI"), RECEIVER_NOT_EXPORTED)
    }

    override fun onResume() {
        super.onResume()
        if (isServiceRunning(PomodoroService::class.java)) {
            binding.apply {
                middleText.translationY = 0f
                timerProgressBar.alpha = 1f
                sessionCounter.alpha = 1f
                resetButton.alpha = 1f
                sessionAmt.alpha = 0f
                decrementButton.alpha = 0f
                incrementButton.alpha = 0f
                startButton.alpha = 0f
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }

    private fun startPomodoroService() {
        val intent = Intent(this, PomodoroService::class.java)
        intent.putExtra("sessions", sessions)
        startService(intent)

        val moveMiddleText = ObjectAnimator.ofFloat(binding.middleText, "translationY", -160f, 0f).apply {
            duration = 1000
            interpolator = DecelerateInterpolator()
        }
        val showTimerProgressBar = ObjectAnimator.ofFloat(binding.timerProgressBar, "alpha", 1f).setDuration(1000)
        val showSessionCounter = ObjectAnimator.ofFloat(binding.sessionCounter, "alpha", 1f).setDuration(500)
        val showResetBtn = ObjectAnimator.ofFloat(binding.resetButton, "alpha", 1f).setDuration(500)
        val hideSessionAmtTxt = ObjectAnimator.ofFloat(binding.sessionAmt, "alpha", 0f).setDuration(500)
        val hideDecBtn = ObjectAnimator.ofFloat(binding.decrementButton, "alpha", 0f).setDuration(500)
        val hideIncBtn = ObjectAnimator.ofFloat(binding.incrementButton, "alpha", 0f).setDuration(500)
        val hideStartBtn = ObjectAnimator.ofFloat(binding.startButton, "alpha", 0f).setDuration(500)
        val together = AnimatorSet().apply { playTogether(moveMiddleText, showTimerProgressBar, showSessionCounter, showResetBtn, hideSessionAmtTxt, hideDecBtn, hideIncBtn, hideStartBtn) }
        AnimatorSet().apply {
            play(together)
            start()
        }
    }

    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
}

