package com.nandu.navdisplay.navigation

import android.accessibilityservice.AccessibilityService
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.nandu.navdisplay.ble.BleForegroundService
import com.nandu.navdisplay.model.AppDebugState
import java.util.Calendar

class MapsAccessibilityService : AccessibilityService() {

    private var lastBleMessage = ""
    private var lastSentTime = 0L

    private val handler = Handler(Looper.getMainLooper())

    private val noNavRunnable = Runnable {
        sendNoNavigation()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

        if (event?.packageName != "com.google.android.apps.maps") return

        val rootNode = rootInActiveWindow ?: return

        val nodes = mutableListOf<String>()
        collectNodeText(rootNode, nodes)

        val distanceRegex =
            Regex("""^\d+(\.\d+)?\s?(m|km)$""", RegexOption.IGNORE_CASE)

        val distanceToTurn = nodes.firstOrNull {
            distanceRegex.matches(it.replace('\u00A0', ' ').trim())
        } ?: "--"

        val instruction = nodes.firstOrNull {
            it.contains("Head", true) ||
                    it.contains("Turn", true) ||
                    it.contains("Continue", true) ||
                    it.contains("Take", true) ||
                    it.contains("Keep", true) ||
                    it.contains("U-turn", true) ||
                    it.contains("Exit", true)
        } ?: return

        val remainingLine = nodes.firstOrNull {
            it.contains("km", true) && it.contains("•")
        }

        val direction = when {
            instruction.contains("left", true) -> "LEFT"
            instruction.contains("right", true) -> "RIGHT"
            instruction.contains("u-turn", true) -> "UTURN"
            instruction.contains("first exit", true) -> "FIRSTEXIT"
            else -> "STRAIGHT"
        }

        val remainingParts = remainingLine?.split("•")

        val remainingDistance =
            remainingParts?.getOrNull(0)?.trim() ?: ""

        val arrivalTime =
            remainingParts?.getOrNull(1)?.trim() ?: ""

        val bleMessage =
            "$direction|$distanceToTurn|$instruction|$arrivalTime|$remainingDistance"

        val now = System.currentTimeMillis()

        // Reset 15 sec no-navigation timer on every valid Maps update
        handler.removeCallbacks(noNavRunnable)
        handler.postDelayed(noNavRunnable, 15000)

        AppDebugState.lastMapText.value = bleMessage

        if (bleMessage != lastBleMessage || now - lastSentTime > 2000) {
            lastBleMessage = bleMessage
            lastSentTime = now

            Log.d("NavDisplay", "AUTO MAP BLE OUT: $bleMessage")

            try {
                AppDebugState.lastBleMessage.value = bleMessage
                BleForegroundService.bleManager.sendMessage(bleMessage)
            } catch (e: UninitializedPropertyAccessException) {
                Log.d("NavDisplay", "BLE service not initialized yet")
                AppDebugState.lastBleStatus.value = "BLE not initialized"
            }
        }
    }

    private fun sendNoNavigation() {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        val greeting = when {
            hour < 12 -> "Good Morning"
            hour < 17 -> "Good Afternoon"
            else -> "Good Evening"
        }

        val message = "NONAV|--|$greeting||"

        Log.d("NavDisplay", "NO NAV AFTER 15 SEC: $message")

        try {
            AppDebugState.lastBleMessage.value = message
            BleForegroundService.bleManager.sendMessage(message)
        } catch (e: Exception) {
            Log.d("NavDisplay", "No nav send failed: ${e.message}")
        }
    }

    private fun collectNodeText(
        node: AccessibilityNodeInfo?,
        list: MutableList<String>
    ) {
        if (node == null) return

        val text = node.text?.toString()?.trim()

        if (!text.isNullOrEmpty()) {
            list.add(text)
        }

        for (i in 0 until node.childCount) {
            collectNodeText(node.getChild(i), list)
        }
    }

    override fun onInterrupt() {
        Log.d("NavDisplay", "Service interrupted")
    }
}