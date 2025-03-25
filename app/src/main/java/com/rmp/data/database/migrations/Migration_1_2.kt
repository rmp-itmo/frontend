package com.rmp.data.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        val cursor = database.query("SELECT * FROM auth_token")
        if (!cursor.moveToFirst()) {
            database.execSQL("INSERT INTO auth_token (id, accessToken, refreshToken) VALUES (1, '', '')")
        }
        cursor.close()
    }
}