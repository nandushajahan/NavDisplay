package com.nandu.navdisplay

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.nandu.navdisplay.ble.BleForegroundService
import com.nandu.navdisplay.model.AppDebugState

class MainActivity : ComponentActivity() {

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.POST_NOTIFICATIONS
            )
        )

        startForegroundService(Intent(this, BleForegroundService::class.java))

        setContent {
            NavDisplayScreen()
        }
    }
}

@Composable
fun NavDisplayScreen() {

    val context = LocalContext.current

    var darkMode by remember { mutableStateOf(false) }
    var showTestFields by remember { mutableStateOf(false) }
    var showDetails by remember { mutableStateOf(false) }
    var showLogs by remember { mutableStateOf(false) }

    var direction by remember { mutableStateOf("LEFT") }
    var distance by remember { mutableStateOf("250 m") }
    var road by remember { mutableStateOf("MG Road") }
    var eta by remember { mutableStateOf("8:42 PM") }
    var remaining by remember { mutableStateOf("3.4 km") }

    val bg = if (darkMode) Color(0xFF101010) else Color.White
    val fg = if (darkMode) Color.White else Color.Black

    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bg)
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            Text(
                text = "NavDisplay",
                style = MaterialTheme.typography.headlineLarge,
                color = fg
            )

            Text(
                text = "Connection Status: ${AppDebugState.connectionStatus.value}",
                color = fg,
                style = MaterialTheme.typography.titleMedium
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Dark Mode", color = fg)
                Switch(
                    checked = darkMode,
                    onCheckedChange = { darkMode = it }
                )
            }

            Button(
                onClick = {
                    context.startActivity(
                        Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Notification Access")
            }

            Button(
                onClick = {
                    showTestFields = !showTestFields
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Send Test Message")
            }

            Button(
                onClick = {
                    showLogs = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View Live Logs")
            }

            Button(
                onClick = {
                    showDetails = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("My Details")
            }

            if (showTestFields) {
                OutlinedTextField(
                    value = direction,
                    onValueChange = { direction = it },
                    label = { Text("Direction") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = distance,
                    onValueChange = { distance = it },
                    label = { Text("Distance") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = road,
                    onValueChange = { road = it },
                    label = { Text("Road / Instruction") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = eta,
                    onValueChange = { eta = it },
                    label = { Text("ETA") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = remaining,
                    onValueChange = { remaining = it },
                    label = { Text("Remaining Distance") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        val message = "$direction|$distance|$road|$eta|$remaining"
                        BleForegroundService.bleManager.sendMessage(message)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("SEND")
                }
            }
        }

        if (showDetails) {
            AlertDialog(
                onDismissRequest = { showDetails = false },
                title = { Text("My Details") },
                text = {
                    Text(
                        """
                        Name: Nandu Shajahan

                        LinkedIn:
                        https://www.linkedin.com/in/nandu-shajahan

                        GitHub:
                        https://github.com/nandushajahan
                        """.trimIndent()
                    )
                },
                confirmButton = {
                    Button(onClick = { showDetails = false }) {
                        Text("OK")
                    }
                }
            )
        }

        if (showLogs) {
            AlertDialog(
                onDismissRequest = { showLogs = false },
                title = { Text("Live Logs") },
                text = {
                    Text(
                        """
                        Google Maps:
                        ${AppDebugState.lastMapText.value}

                        Last BLE Message:
                        ${AppDebugState.lastBleMessage.value}

                        BLE Status:
                        ${AppDebugState.lastBleStatus.value}
                        """.trimIndent()
                    )
                },
                confirmButton = {
                    Button(onClick = { showLogs = false }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}