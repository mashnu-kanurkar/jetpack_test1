package com.test.systemframework.notification

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [NotificationEntity::class], version = 2, exportSchema = false)
abstract class NotificationDatabase : RoomDatabase() {
    abstract fun notificationDao(): NotificationDao

    // Singleton instance of the DB
    companion object {

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE ${Constants.dbName} ADD COLUMN uploaded BOOLEAN NOT NULL DEFAULT 0")
            }
        }


        @Volatile private var INSTANCE: NotificationDatabase? = null

        fun getInstance(context: Context): NotificationDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance =
                        Room.databaseBuilder(
                            context.applicationContext,
                            NotificationDatabase::class.java,
                            Constants.dbName)
                            .addMigrations(MIGRATION_1_2)
                            .fallbackToDestructiveMigration()
                            .build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}