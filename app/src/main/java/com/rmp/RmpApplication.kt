package com.rmp

import android.app.Application
import com.rmp.data.AppContainer
import com.rmp.data.AppContainerImpl
import com.rmp.data.database.AppDatabase

class RmpApplication : Application() {
    companion object {
        const val RMP_APP_URI = "https://app.rmp.dudosyka.ru"
    }

    // AppContainer instance used by the rest of classes to obtain dependencies
    lateinit var container: AppContainer
    lateinit var database: AppDatabase

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getDatabase(this)
        container = AppContainerImpl(this, database)
    }
}