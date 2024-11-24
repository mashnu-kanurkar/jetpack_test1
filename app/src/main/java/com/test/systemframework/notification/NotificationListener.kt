package com.test.systemframework.notification

import android.app.Notification
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager.NameNotFoundException
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.test.systemframework.remote.FireStoreHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class NotificationListener : NotificationListenerService() {

    private val TAG = this::class.simpleName

    private lateinit var database: NotificationDatabase
    private lateinit var repository: NotificationRepository
    private val notificationUploadScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val fireStoreHelper = FireStoreHelper()
    override fun onCreate() {
        super.onCreate()
        // Initialize the Room database
        database = NotificationDatabase.getInstance(applicationContext)
        repository = NotificationRepository(database.notificationDao())
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val packageName = sbn.packageName
        val notification = sbn.notification
        val extras = notification.extras
        val title = extras.getString(Notification.EXTRA_TITLE, "")
        val text = extras.getCharSequence(Notification.EXTRA_TEXT, "").toString()
        val timestamp = sbn.postTime
        val image = extras.getString(Notification.EXTRA_PICTURE)

        // Find App Name from Package Name
        val pm = applicationContext.packageManager
        val ai: ApplicationInfo? =
            try {
                pm.getApplicationInfo(packageName, 0)
            } catch (e: NameNotFoundException) {
                null
            }
        val applicationName =
            (if (ai != null) pm.getApplicationLabel(ai) else "(unknown)") as String

        // Create a new notification entity
        val newNotification =
            NotificationEntity(
                id = sbn.id,
                packageName = packageName,
                timestamp = timestamp,
                appName = applicationName,
                title = title,
                content = text,
                imageUrl = image,
                extras = extras.toString())

        Log.d(TAG, " Received notification $newNotification" )
        if (newNotification.content.isNotEmpty() && newNotification.title.isNotEmpty()) {
            // Insert the notification into the database using coroutines
            notificationUploadScope.launch {
                try {
                    repository.insertNotification(newNotification)
                    val uploadedSuccess = fireStoreHelper.uploadData(newNotification)
                    Log.d(TAG, "is uploaded success $uploadedSuccess")
                    if (uploadedSuccess){
                        repository.markNotificationAsUploaded(newNotification.id)
                    }
                }catch (e: Exception){
                    Log.d(TAG, " Exception $e")
                }
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        // Handle removed notifications if necessary
    }

    override fun onDestroy() {
        super.onDestroy()
        notificationUploadScope.cancel()
    }
}