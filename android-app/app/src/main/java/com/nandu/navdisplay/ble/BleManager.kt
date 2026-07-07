package com.nandu.navdisplay.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.nandu.navdisplay.model.AppDebugState
import java.util.UUID

class BleManager(private val context: Context) {

    private val deviceName = "NavDisplay"

    private val serviceUUID =
        UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E")

    private val rxUUID =
        UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E")

    private val txUUID =
        UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E")

    private val bluetoothAdapter: BluetoothAdapter? =
        BluetoothAdapter.getDefaultAdapter()

    private var bluetoothGatt: BluetoothGatt? = null
    private var rxCharacteristic: BluetoothGattCharacteristic? = null
    private var txCharacteristic: BluetoothGattCharacteristic? = null

    private var isConnected = false
    private var isScanning = false
    private var isConnecting = false

    private val handler = Handler(Looper.getMainLooper())

    @SuppressLint("MissingPermission")
    fun startScan() {
        Log.d("NavDisplay", "Starting BLE scan")
        AppDebugState.lastBleStatus.value = "Scanning..."

        if (isConnected) {
            Log.d("NavDisplay", "Already connected")
            AppDebugState.lastBleStatus.value = "Already connected"
            return
        }

        if (isConnecting) {
            Log.d("NavDisplay", "Already connecting")
            AppDebugState.lastBleStatus.value = "Already connecting"
            return
        }

        if (isScanning) {
            Log.d("NavDisplay", "Already scanning")
            AppDebugState.lastBleStatus.value = "Already scanning"
            return
        }

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            Log.d("NavDisplay", "Bluetooth unavailable")
            AppDebugState.connectionStatus.value = "Bluetooth unavailable"
            AppDebugState.lastBleStatus.value = "Bluetooth unavailable"
            retryScan()
            return
        }

        val scanner = bluetoothAdapter.bluetoothLeScanner

        if (scanner == null) {
            Log.d("NavDisplay", "BLE scanner null")
            AppDebugState.lastBleStatus.value = "BLE scanner unavailable"
            retryScan()
            return
        }

        cleanupGatt()

        isScanning = true

        scanner.startScan(scanCallback)

        handler.postDelayed({
            if (isScanning && !isConnected && !isConnecting) {
                Log.d("NavDisplay", "Scan timeout")
                stopScan()
                retryScan()
            }
        }, 8000)
    }

    @SuppressLint("MissingPermission")
    private fun stopScan() {
        try {
            bluetoothAdapter?.bluetoothLeScanner?.stopScan(scanCallback)
        } catch (e: Exception) {
            Log.d("NavDisplay", "Stop scan failed: ${e.message}")
        }

        isScanning = false
    }

    private fun retryScan() {
        handler.postDelayed({
            if (!isConnected && !isConnecting) {
                Log.d("NavDisplay", "Retrying BLE scan...")
                AppDebugState.lastBleStatus.value = "Retrying scan..."
                startScan()
            }
        }, 3000)
    }

    @SuppressLint("MissingPermission")
    private fun cleanupGatt() {
        try {
            bluetoothGatt?.disconnect()
            bluetoothGatt?.close()
        } catch (e: Exception) {
            Log.d("NavDisplay", "GATT cleanup failed: ${e.message}")
        }

        bluetoothGatt = null
        rxCharacteristic = null
        txCharacteristic = null
        isConnected = false
        isConnecting = false
    }

    @SuppressLint("MissingPermission")
    fun sendMessage(message: String) {
        AppDebugState.lastBleMessage.value = message

        Log.d("NavDisplay", "SEND REQUEST: $message")
        Log.d("NavDisplay", "isConnected = $isConnected")
        Log.d("NavDisplay", "bluetoothGatt = ${bluetoothGatt != null}")
        Log.d("NavDisplay", "rxCharacteristic = ${rxCharacteristic != null}")

        if (!isConnected || bluetoothGatt == null || rxCharacteristic == null) {
            Log.d("NavDisplay", "BLE not ready")
            AppDebugState.lastBleStatus.value = "BLE not ready"
            return
        }

        rxCharacteristic?.value = message.toByteArray()

        val result =
            bluetoothGatt?.writeCharacteristic(rxCharacteristic)

        Log.d("NavDisplay", "Sent: $message")
        Log.d("NavDisplay", "Write result: $result")

        AppDebugState.lastBleStatus.value = "Write result: $result"
    }

    private val scanCallback = object : ScanCallback() {

        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {

            val name = result.device.name ?: return

            if (name == deviceName) {
                Log.d("NavDisplay", "Found ESP32")
                AppDebugState.lastBleStatus.value = "Found ESP32"

                stopScan()

                cleanupGatt()

                isConnecting = true
                AppDebugState.lastBleStatus.value = "Connecting..."

                bluetoothGatt =
                    result.device.connectGatt(
                        context,
                        false,
                        gattCallback
                    )
            }
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {

        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(
            gatt: BluetoothGatt,
            status: Int,
            newState: Int
        ) {
            Log.d(
                "NavDisplay",
                "Connection state changed. status=$status newState=$newState"
            )

            if (status != BluetoothGatt.GATT_SUCCESS) {
                Log.d("NavDisplay", "Connection failed with status: $status")

                isConnected = false
                isConnecting = false

                AppDebugState.connectionStatus.value = "Connection failed"
                AppDebugState.lastBleStatus.value = "Connection failed: $status"

                cleanupGatt()
                retryScan()
                return
            }

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d("NavDisplay", "Connected")

                bluetoothGatt = gatt
                isConnected = true
                isConnecting = false

                AppDebugState.connectionStatus.value = "NavDisplay connected"
                AppDebugState.lastBleStatus.value = "Connected"

                handler.postDelayed({
                    gatt.discoverServices()
                }, 500)
            }

            if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d("NavDisplay", "Disconnected")

                isConnected = false
                isConnecting = false
                rxCharacteristic = null
                txCharacteristic = null

                AppDebugState.connectionStatus.value = "Disconnected"
                AppDebugState.lastBleStatus.value = "Disconnected"

                cleanupGatt()
                retryScan()
            }
        }

        override fun onServicesDiscovered(
            gatt: BluetoothGatt,
            status: Int
        ) {
            Log.d("NavDisplay", "Services discovered status=$status")

            if (status != BluetoothGatt.GATT_SUCCESS) {
                Log.d("NavDisplay", "Service discovery failed")
                AppDebugState.lastBleStatus.value = "Service discovery failed"
                cleanupGatt()
                retryScan()
                return
            }

            val service = gatt.getService(serviceUUID)

            if (service == null) {
                Log.d("NavDisplay", "NavDisplay service not found")
                AppDebugState.lastBleStatus.value = "Service not found"
                cleanupGatt()
                retryScan()
                return
            }

            rxCharacteristic =
                service.getCharacteristic(rxUUID)

            txCharacteristic =
                service.getCharacteristic(txUUID)

            Log.d("NavDisplay", "RX ready: ${rxCharacteristic != null}")
            Log.d("NavDisplay", "TX ready: ${txCharacteristic != null}")

            AppDebugState.lastBleStatus.value =
                "RX: ${rxCharacteristic != null}, TX: ${txCharacteristic != null}"

            if (rxCharacteristic != null) {
                sendMessage("STATUS|--|NavDisplay: Connected||")
            }
        }
    }
}