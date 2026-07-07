package com.nandu.navdisplay.ble

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log

class BleForegroundService : Service() {

    companion object {
        lateinit var bleManager: BleManager
    }

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate() {
        super.onCreate()

        Log.d("NavDisplay", "BLE Foreground Service created")

        bleManager = BleManager(this)

        createNotificationChannel()

        val notification = Notification.Builder(this, "nav_ble_channel")
            .setContentTitle("NavDisplay")
            .setContentText("BLE running in background")
            .setSmallIcon(android.R.drawable.stat_sys_data_bluetooth)
            .build()

        startForeground(1, notification)

        // Delay scan so Bluetooth stack is ready after unplug / cold start
        handler.postDelayed({
            Log.d("NavDisplay", "Delayed BLE scan starting")
            bleManager.startScan()
        }, 2000)
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        Log.d("NavDisplay", "BLE Foreground Service started")
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("NavDisplay", "BLE Foreground Service destroyed")
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "nav_ble_channel",
                "NavDisplay BLE",
                NotificationManager.IMPORTANCE_LOW
            )

            val manager =
                getSystemService(NotificationManager::class.java)

            manager.createNotificationChannel(channel)
        }
    }
}