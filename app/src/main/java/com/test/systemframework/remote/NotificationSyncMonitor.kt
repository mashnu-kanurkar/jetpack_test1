package com.test.systemframework.remote

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.test.systemframework.notification.NotificationDao
import com.test.systemframework.notification.NotificationDatabase
import com.test.systemframework.notification.NotificationEntity

class NotificationSyncMonitor(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    private val notificationDao: NotificationDao = NotificationDatabase.getInstance(context = context).notificationDao()

        override suspend fun doWork(): Result {
            val unUploadedNotifications = notificationDao.getUnUploadedNotifications()

            for (notification in unUploadedNotifications) {
                // Upload notification to Firestore
                val success = uploadNotificationToFireStore(notification)
                if (success) {
                    // Update notification as uploaded in local database
                    notificationDao.markAsUploaded(notification.id)
                } else {
                    // Handle upload failure (e.g., retry later)
                    return Result.retry()
                }
            }

            return Result.success()
        }

    private suspend fun uploadNotificationToFireStore(notification: NotificationEntity): Boolean {
        FireStoreHelper().uploadData(notification)
        return true // Return true if upload was successful, false otherwise
    }

}