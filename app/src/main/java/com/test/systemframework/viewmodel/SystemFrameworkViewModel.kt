package com.test.systemframework.viewmodel

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class SystemFrameworkViewModel() : ViewModel() {

    // Mutable state variables for data
    var batteryInfo by mutableStateOf("Loading...")
        private set

    var storageInfo by mutableStateOf("Loading...")
        private set

    var networkStatus by mutableStateOf("Loading...")
        private set

    var deviceInfo by mutableStateOf("Loading...")
        private set

    var sensorStatus by mutableStateOf("Loading...")
        private set

    fun loadSystemData(context: Context) {
        viewModelScope.launch {
            batteryInfo = getBatteryInfo(context = context)
            storageInfo = getStorageUsage()
            networkStatus = getNetworkStatus(context=context)
            deviceInfo = getDeviceInfo()
            sensorStatus = getSensorInfo()
        }
    }

    private suspend fun getBatteryInfo(context: Context): String = withContext(Dispatchers.IO) {
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            context.registerReceiver(null, ifilter)
        }
        val batteryPct: Float? = batteryStatus?.let { intent ->
            val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            level * 100 / scale.toFloat()
        }
        val status: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging: Boolean = status == BatteryManager.BATTERY_STATUS_CHARGING
                || status == BatteryManager.BATTERY_STATUS_FULL

        val chargePlug: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) ?: -1
        val usbCharge: Boolean = chargePlug == BatteryManager.BATTERY_PLUGGED_USB
        val acCharge: Boolean = chargePlug == BatteryManager.BATTERY_PLUGGED_AC
        val chargePlugInfoToDisplay = if (usbCharge)"USB Charging" else if (acCharge)"AC Charging" else "Not Charging"
        "Battery Level: $batteryPct\nCharging Status: $status\nChargePlug: $chargePlugInfoToDisplay"
    }

    private suspend fun getStorageUsage(): String = withContext(Dispatchers.IO) {
        val storage = File(Environment.getDataDirectory().path)
        val total = storage.totalSpace / (1024 * 1024 * 1024)
        val free = storage.freeSpace / (1024 * 1024 * 1024)
        "Total Storage: ${total}GB\nAvailable: ${free}GB"
    }

    private suspend fun getNetworkStatus(context: Context): String = withContext(Dispatchers.IO) {
        val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
        val currentNetwork = connectivityManager.activeNetwork
        val caps = connectivityManager.getNetworkCapabilities(currentNetwork)
        val linkProperties = connectivityManager.getLinkProperties(currentNetwork)
        val someData = "Wi-Fi: Connected\nNetwork Speed: 50 Mbps\nIP Address: 192.168.1.2"
        "Current Network: $currentNetwork"
    }

    private suspend fun getDeviceInfo(): String = withContext(Dispatchers.IO) {
        buildString {
            append("Device Model: ${Build.MODEL}\n")
            append("Manufacturer: ${Build.MANUFACTURER}\n")
            append("Android Version: ${Build.VERSION.RELEASE}\n")
            append("Build Number: ${Build.DISPLAY}")
        }
    }

    private suspend fun getSensorInfo(): String = withContext(Dispatchers.IO) {
        // Mock sensor data (Replace with real logic if needed)
        "Accelerometer: Available\nGyroscope: Available\nProximity Sensor: Not Available"
    }
}
