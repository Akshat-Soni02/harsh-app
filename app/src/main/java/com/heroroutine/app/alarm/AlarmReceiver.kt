package com.heroroutine.app.alarm

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.heroroutine.app.HeroRoutineApp
import com.heroroutine.app.MainActivity

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("EXTRA_TITLE") ?: "Exercise Routine Time! 🌟"
        val message = intent.getStringExtra("EXTRA_MESSAGE") ?: "Time for your daily champion exercises!"
        val notificationId = intent.getIntExtra("EXTRA_ID", 1001)
        val hour = intent.getIntExtra("EXTRA_HOUR", -1)
        val minute = intent.getIntExtra("EXTRA_MINUTE", -1)

        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notification = NotificationCompat.Builder(context, HeroRoutineApp.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setSound(alarmSound)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0, 500, 250, 500, 250, 500))
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)

        // Reschedule for next day if daily recurring alarm
        if (hour >= 0 && minute >= 0) {
            AlarmScheduler.scheduleDailyAlarm(
                context = context,
                requestCode = notificationId,
                hour = hour,
                minute = minute,
                title = title,
                message = message
            )
        }
    }
}
