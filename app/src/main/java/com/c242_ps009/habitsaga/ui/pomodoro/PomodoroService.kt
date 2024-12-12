package com.c242_ps009.habitsaga.ui.pomodoro

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.c242_ps009.habitsaga.R
import java.util.Locale
import java.util.Locale.*

class PomodoroService : Service() {

    private var sessions = 0
    private var currentSession = 1
    private var isBreak = false
    private var timer: CountDownTimer? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        sessions = intent.getIntExtra("sessions", 0)
        startTimer()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startTimer() {
        val time = if (isBreak) 300 else 1500
        timer = object : CountDownTimer((time.toLong() * 1000), 50) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60

                displayTimeOnNotificationBar(isBreak, minutes.toInt(), seconds.toInt())

                val intent = Intent("UPDATE_UI")
                intent.putExtra("minutes", minutes.toInt())
                intent.putExtra("seconds", seconds.toInt())
                intent.putExtra("millisUntilFinished", (millisUntilFinished * 10000000 / (time * 1000)).toInt())
                intent.putExtra("currentSession", currentSession)
                intent.putExtra("sessions", sessions)
                intent.putExtra("isBreak", isBreak)
                sendBroadcast(intent)
            }

            override fun onFinish() {
                if (isBreak) sendNotification("Break has concluded, let's get back to work!")
                else {
                    if (currentSession == sessions) {
                        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                        sendNotification("All sessions have concluded, work again some other time!")
                        notificationManager.cancel(2) // Cancel the ongoing notification
                    } else sendNotification("Session $currentSession has concluded, let's take a break!")
                }

                if (!isBreak) {
                    currentSession++
                    isBreak = true
                } else isBreak = false

                if ((currentSession - 1) != sessions) startTimer()
            }
        }.start()
    }
    private fun displayTimeOnNotificationBar(isBreak: Boolean, minutes: Int, seconds: Int) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannel = NotificationChannel("timer", "Pomodoro Timer", NotificationManager.IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(notificationChannel)

        var title = "Pomodoro Ongoing: Session $currentSession"
        if (isBreak) title = "Pomodoro Upcoming: Session $currentSession"

        var text = "${String.format(US, "%02d", minutes)}:${String.format(US, "%02d", seconds)}"
        if (isBreak) text = "Intermission: $text"

        val notification = NotificationCompat.Builder(this, "timer")
            .setContentTitle(title )
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_timer)
            .setOngoing(true)
            .build()

        notificationManager.notify(2, notification)
    }

    private fun sendNotification(text: String) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannel = NotificationChannel("notifier", "Pomodoro Notifier", NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(notificationChannel)

        val notification = NotificationCompat.Builder(this, "notifier")
            .setContentTitle("Pomodoro Notifier")
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_timer)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(1, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}