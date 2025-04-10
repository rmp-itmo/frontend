package com.rmp

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.rmp.data.AppContainer
import com.rmp.data.AppContainerImpl
import com.rmp.data.database.AppDatabase
import com.rmp.data.ApplicationDatabase
import com.rmp.data.StatsWorker
import java.util.concurrent.TimeUnit

class RmpApplication : Application() {
    companion object {
        const val RMP_APP_URI = "https://app.rmp.dudosyka.ru"
    }

    // AppContainer instance used by the rest of classes to obtain dependencies
    lateinit var container: AppContainer
    lateinit var database: AppDatabase


    private fun setupWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<StatsWorker>(
            15,
            TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "statsCheck",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
    }

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getDatabase(this)
        ApplicationDatabase = database
        container = AppContainerImpl(this, database)
        setupWorker()
    }
}