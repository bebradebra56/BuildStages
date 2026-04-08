package com.buidlsta.stagebuisla.xcv.presentation.notificiation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import com.buidlsta.stagebuisla.BuildStagesActivity
import com.buidlsta.stagebuisla.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.buidlsta.stagebuisla.xcv.presentation.app.BuildStagesApplication

private const val BUILD_STAGES_CHANNEL_ID = "build_stages_notifications"
private const val BUILD_STAGES_CHANNEL_NAME = "BuildStages Notifications"
private const val BUILD_STAGES_NOT_TAG = "BuildStages"

class BuildStagesPushService : FirebaseMessagingService(){
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Обработка notification payload
        remoteMessage.notification?.let {
            if (remoteMessage.data.contains("url")) {
                buildStagesShowNotification(it.title ?: BUILD_STAGES_NOT_TAG, it.body ?: "", data = remoteMessage.data["url"])
            } else {
                buildStagesShowNotification(it.title ?: BUILD_STAGES_NOT_TAG, it.body ?: "", data = null)
            }
        }

        // Обработка data payload
        if (remoteMessage.data.isNotEmpty()) {
            buildStagesHandleDataPayload(remoteMessage.data)
        }
    }

    private fun buildStagesShowNotification(title: String, message: String, data: String?) {
        val buildStagesNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Создаем канал уведомлений для Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                BUILD_STAGES_CHANNEL_ID,
                BUILD_STAGES_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            buildStagesNotificationManager.createNotificationChannel(channel)
        }

        val buildStagesIntent = Intent(this, BuildStagesActivity::class.java).apply {
            putExtras(bundleOf(
                "url" to data
            ))
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val buildStagesPendingIntent = PendingIntent.getActivity(
            this,
            0,
            buildStagesIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val buildStagesNotification = NotificationCompat.Builder(this, BUILD_STAGES_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.build_stages_noti_ic)
            .setAutoCancel(true)
            .setContentIntent(buildStagesPendingIntent)
            .build()

        buildStagesNotificationManager.notify(System.currentTimeMillis().toInt(), buildStagesNotification)
    }

    private fun buildStagesHandleDataPayload(data: Map<String, String>) {
        data.forEach { (key, value) ->
            Log.d(BuildStagesApplication.BUILD_STAGES_MAIN_TAG, "Data key=$key value=$value")
        }
    }
}