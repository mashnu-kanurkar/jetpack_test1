package com.test.systemframework.notification

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class NotificationViewModel(private val application: Application, repository: NotificationRepository) :
    AndroidViewModel(application) {

        private val TAG = this::class.simpleName
    private val packageManager = application.packageManager

    private val notificationsFlow: Flow<List<NotificationEntity>> = repository.getAllNotificationsFlow()

    val appInfoFromFlow: Flow<List<AppInfo>> =
        notificationsFlow.map { notifications ->
            notifications
                .groupBy { it.packageName }
                .map { entry ->
                    AppInfo(
                        appName = loadAppNameFromPackageName(packageManager, entry.key),
                        icon = loadIconFromPackageName(packageManager, entry.key),
                        notificationCount = entry.value.size,
                        packageName = entry.key)
                }
        }

    val notificationsGroupedByAppFlow: Flow<Map<String, List<NotificationEntity>>> =
        notificationsFlow.map { notificationsFLow -> notificationsFLow.groupBy { it.appName } }

    private val _isNotificationPermissionGranted = MutableStateFlow(false)
    val isNotificationPermissionGranted = _isNotificationPermissionGranted.asStateFlow()

    fun refreshNotificationPermission() {
        val enabledListeners = NotificationManagerCompat.getEnabledListenerPackages(application)

        _isNotificationPermissionGranted.update {
            val enabled = enabledListeners.contains(application.packageName)
            Log.d(TAG, "enabled notifications: $enabled")
            enabled
        }
    }

    init {
        refreshNotificationPermission()
    }
}

fun loadAppNameFromPackageName(packageManager: PackageManager, packageName: String): String {
    val ai: ApplicationInfo? =
        try {
            packageManager.getApplicationInfo(packageName, 0)
        } catch (e: NameNotFoundException) {
            null
        }
    val applicationName =
        (if (ai != null) packageManager.getApplicationLabel(ai) else "(unknown)") as String
    return applicationName
}

fun loadIconFromPackageName(packageManager: PackageManager, packageName: String): ImageBitmap? {

    val ai: ApplicationInfo? =
        try {
            packageManager.getApplicationInfo(packageName, 0)
        } catch (e: NameNotFoundException) {
            null
        }

    return (ai?.loadIcon(packageManager)?.toBitmap()?.asImageBitmap())
}

class NotificationViewModelFactory(
    private val application: Application,
    private val repository: NotificationRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotificationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotificationViewModel(application = application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}