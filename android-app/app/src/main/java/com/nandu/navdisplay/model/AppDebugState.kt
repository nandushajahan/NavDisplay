package com.nandu.navdisplay.model

import androidx.compose.runtime.mutableStateOf

object AppDebugState {
    val connectionStatus = mutableStateOf("Not connected")
    val lastMapText = mutableStateOf("No Google Maps data yet")
    val lastBleMessage = mutableStateOf("Nothing sent yet")
    val lastBleStatus = mutableStateOf("Waiting")
}