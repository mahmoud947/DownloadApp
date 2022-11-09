package com.udacity

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat

const val NOTIFICATION_ID = 1

@SuppressLint("UnspecifiedImmutableFlag")
fun NotificationManager.sendNotification(
    title: String,
    description: String,
    applicationContext: Context,
    fileName: String,
    status: String
) {

    val contentIntent = Intent(applicationContext, DetailActivity::class.java)
        .apply {
            putExtra(FILE_NAME_EXTRA, fileName)
            putExtra(STATUS_EXTRA, status)
        }

    val pendingIntent = PendingIntent.getActivity(
        applicationContext,
        1,
        contentIntent,
        PendingIntent.FLAG_CANCEL_CURRENT
    )

    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.notification_id)
    )
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(title)
        .setContentText(description)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)

    notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.createChannel(channelId: String, channelName: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel: NotificationChannel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
        notificationChannel.apply {
            enableVibration(true)
            lightColor = Color.RED
            enableLights(true)
        }
        this.createNotificationChannel(notificationChannel)

    }
}