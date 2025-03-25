package com.rmp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.rmp.data.database.auth.AuthToken
import com.rmp.data.database.auth.AuthTokenDao

@Database(entities = [AuthToken::class], version = 1, exportSchema = false)
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
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        db.execSQL("INSERT INTO auth_token (id, accessToken, refreshToken) VALUES (1, '', '')")
                    }
                })
                 .build()
                INSTANCE = instance
                instance
            }
        }
    }
}