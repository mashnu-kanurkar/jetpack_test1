package com.test.systemframework.notification

import com.test.systemframework.notification.NotificationDao
import com.test.systemframework.notification.NotificationEntity
import kotlinx.coroutines.flow.Flow

class NotificationRepository(private val notificationDao: NotificationDao) {

    suspend fun insertNotification(notificationEntity: NotificationEntity) {
        notificationDao.insertNotification(notificationEntity)
    }

    fun getAllNotificationsFlow(): Flow<List<NotificationEntity>> =
        notificationDao.getAllNotificationsFlow()

    suspend fun markNotificationAsUploaded(id: Int) {
        notificationDao.markAsUploaded(id)
    }

}