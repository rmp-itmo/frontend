package com.rmp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.rmp.data.database.auth.AuthToken
import com.rmp.data.database.auth.AuthTokenDao
import com.rmp.data.database.migrations.MIGRATION_1_2

@Database(entities = [AuthToken::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun authTokenDao(): AuthTokenDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(applicationContext: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    applicationContext.applicationContext,
                    AppDatabase::class.java,
                    "auth_database"
                ).addMigrations(MIGRATION_1_2)
                 .build()
                INSTANCE = instance
                instance
            }
        }
    }
}