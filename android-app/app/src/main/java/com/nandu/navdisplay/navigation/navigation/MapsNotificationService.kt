package com.nandu.navdisplay.navigation

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.nandu.navdisplay.ble.BleForegroundService
import com.nandu.navdisplay.model.AppDebugState

class MapsNotificationService : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {

        if (sbn?.packageName != "com.google.android.apps.maps") return

        val extras = sbn.notification.extras

        val title =
            extras.getString("android.title") ?: ""

        val text =
            extras.getString("android.text") ?: ""

        val bigText =
            extras.getString("android.bigText") ?: ""

        val allText =
            "$title $text $bigText".trim()

        if (allText.isBlank()) return

        Log.d("NavDisplay", "Maps notification: $allText")

        AppDebugState.lastMapText.value = allText

        val direction = when {
            allText.contains("left", true) -> "LEFT"
            allText.contains("right", true) -> "RIGHT"
            allText.contains("u-turn", true) -> "UTURN"
            allText.contains("first exit", true) -> "FIRSTEXIT"
            else -> "STRAIGHT"
        }

        val bleMessage =
            "$direction|--|$allText||"

        try {
            AppDebugState.lastBleMessage.value = bleMessage
            BleForegroundService.bleManager.sendMessage(bleMessage)
        } catch (e: Exception) {
            Log.d("NavDisplay", "BLE send failed: ${e.message}")
            AppDebugState.lastBleStatus.value =
                "Notification BLE failed"
        }
    }
}