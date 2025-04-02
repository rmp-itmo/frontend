package com.rmp.services.schedulers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DailyHealthWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Log.d("HealthWorker", "Performing daily health sync...")

            Result.success()
        } catch (e: Exception) {
            Log.e("HealthWorker", "Daily task failed", e)
            Result.retry()
        }
    }
}