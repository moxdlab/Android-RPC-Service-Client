package io.moxd.rpc_service

import android.app.*
import android.content.Intent
import android.os.IBinder
import io.moxd.IRemoteService

class RemoteService : Service() {

    private var counter = 0 //not thread-safe!
    private val binder = object : IRemoteService.Stub() {
        override fun getCount(): Int = counter

        override fun incrementCounter() {
            counter++
        }

        override fun resetCounter() {
            counter = 0
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        setupForeground()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun setupForeground() {
        setupNotificationChannel()
        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(
                    this, 0, notificationIntent,
                    PendingIntent.FLAG_IMMUTABLE
                )
            }

        val notification: Notification = Notification.Builder(this, "123")
            .setContentTitle("Service running")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(1, notification)
    }

    private fun setupNotificationChannel() {
        val name = "Notification Channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannel("123", name, importance)
        mChannel.description = name

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }
}