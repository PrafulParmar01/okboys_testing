package com.ok.boys.delivery.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.content.res.Resources
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.ok.boys.delivery.R
import com.ok.boys.delivery.ui.dashboard.HomeActivity


class NotificationUtils internal constructor(private val mContext: Context) {
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private var notification: Notification? = null

    private val mResources: Resources
    private var locationsService: LocationsService? = null
    private var channelId = ""
    private var channelMessageId = ""


    init {
        notificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mResources = mContext.resources
        channelId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createNotificationChannel(
            notificationManager
        ) else ""

        channelMessageId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createMessageNotificationChannel(
                notificationManager
            ) else ""

        if (mContext is LocationsService) {
            locationsService = mContext
        }
    }

    fun startForegroundNotification() {
        val homeIntent = Intent(mContext, HomeActivity::class.java)
        notificationBuilder = NotificationCompat.Builder(mContext, channelId)
        notification = notificationBuilder
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_circle_notifications)
            .setContentTitle("Location service")
            .setContentText("Service is running...")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(launchIntent(homeIntent))
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            locationsService?.startForeground(MAIN_NOTIFICATION_ID, notification)
        } else {
            locationsService?.startForeground(MAIN_NOTIFICATION_ID, notification!!,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
        }
    }

    fun startChatNotification(response: String) {
        val uniqueID = System.currentTimeMillis().toInt()
        val notificationIntent = Intent(mContext, HomeActivity::class.java)
        notificationBuilder = NotificationCompat.Builder(mContext, channelMessageId)
            .setAutoCancel(true)
            .setOngoing(false)
            .setSmallIcon(R.drawable.ic_circle_notifications)
            .setContentTitle("OkBoys Delivery")
            .setContentText(response)
            .setContentIntent(launchIntent(notificationIntent))

        notification = notificationBuilder.build()
        notificationManager.notify(uniqueID, notification)
    }
     fun startChatNotificationEditOrder(response: String) {
            val uniqueID = System.currentTimeMillis().toInt()
          //  val notificationIntent = Intent(mContext, HomeActivity::class.java)
            val notificationIntent = Intent(mContext, HomeActivity::class.java).putExtra("is_from_notification", true)
            notificationBuilder = NotificationCompat.Builder(mContext, channelMessageId)
                .setAutoCancel(true)
                .setOngoing(false)
                .setSmallIcon(R.drawable.ic_circle_notifications)
                .setContentTitle("OkBoys Delivery")
                .setContentText(response)
                .setContentIntent(launchIntent(notificationIntent))

            notification = notificationBuilder.build()
            notificationManager.notify(uniqueID, notification)
        }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager?): String {
        val channelId = "okboys_delivery_service_channel_id"
        val channelName = "OkBoys Delivery Location"
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.importance = NotificationManager.IMPORTANCE_DEFAULT
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        notificationManager!!.createNotificationChannel(channel)
        return channelId
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createMessageNotificationChannel(notificationManager: NotificationManager?): String {
        val channelId = "okboys_default_channel_id"
        val channelName = "OkBoys Delivery Default"
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.importance = NotificationManager.IMPORTANCE_HIGH
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        notificationManager!!.createNotificationChannel(channel)
        return channelId
    }

    fun cancelNotification() {
        notificationManager.cancel(MAIN_NOTIFICATION_ID)
    }

    companion object {
        private const val MAIN_NOTIFICATION_ID = 309
    }


    @SuppressLint("UnspecifiedImmutableFlag")
    private fun launchIntent(resultIntent: Intent): PendingIntent {
        val contentPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getActivity(
                mContext,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
        } else {
            PendingIntent.getActivity(
                mContext,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        return contentPendingIntent
    }
}