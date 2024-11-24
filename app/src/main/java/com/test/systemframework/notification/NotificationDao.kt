package com.test.systemframework.notification

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("SELECT * FROM notification ORDER BY timestamp DESC")
    fun getAllNotificationsFlow(): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notification WHERE uploaded = 0")
    fun getUnUploadedNotifications(): List<NotificationEntity>

    @Query("UPDATE notification SET uploaded = 1 WHERE id = :id")
    fun markAsUploaded(id: Int)
}