package com.rmp.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.rmp.R
import com.rmp.data.repository.heart.HeartRateLogDto
import com.rmp.data.repository.heart.HeartRepoImpl
import com.rmp.data.repository.heart.HeartRepository
import com.rmp.data.repository.signup.DateDto
import com.rmp.data.repository.signup.UserRepoImpl
import com.rmp.data.repository.signup.UserRepository
import com.rmp.data.repository.steps.StepsRepoImpl
import com.rmp.data.repository.steps.StepsRepository
import com.rmp.data.repository.steps.UserStepsLogDto
import com.rmp.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


class HealthConnectForegroundService : Service() {
    private lateinit var healthConnectClient: HealthConnectClient
    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())
    private var isRunning = false

    private lateinit var heartRepository: HeartRepository
    private lateinit var stepsRepository: StepsRepository
    private lateinit var userRepository: UserRepository

    private val NOTIFICATION_CHANNEL_ID = "HealthConnectChannel"
    private val NOTIFICATION_ID = 101

    override fun onCreate() {
        super.onCreate()

        heartRepository = HeartRepoImpl()
        stepsRepository = StepsRepoImpl()
        userRepository = UserRepoImpl()

        healthConnectClient = HealthConnectClient.getOrCreate(this)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isRunning) {
            isRunning = true
            startForeground(NOTIFICATION_ID, createNotification())

            startHealthDataCollection()

            Log.d("HealthConnect", "Service started")
        }
        return START_STICKY
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Health Connect Service Channel",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Channel for Health Connect data collection"
        }

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    private fun createNotification(): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Сбор данных о здоровье")
            .setContentText("Сервис собирает данные о шагах и пульсе")
            .setSmallIcon(R.drawable.healthy_food_icon)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun startHealthDataCollection() {
        serviceScope.launch {
            while (isRunning) {
                try {
                    val instant = Instant.now()
                    val ldt = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())

                    val formatter = DateTimeFormatter.ofPattern("HH:mm")
                    val formattedTime = ldt.format(formatter)

                    updateNotification("Последнее обновление: $formattedTime")
                    delay(2 * 60 * 1000)

                    fetchStepCountData()
                    fetchHeartRateData()

                    val now = Instant.now()
                    if (isNearMidnight(now)) {
                        userRepository.checkUserDailyTarget(DateDto(now.toDateAndTime().first))
                        Log.d("HealthConnect", "Daily check target")
                    }
                } catch (e: Exception) {
                    Log.e("HealthConnect", "Error in data collection loop", e)
                    updateNotification("Ошибка: ${e.localizedMessage}")
                    delay(30_000)
                }
            }
        }
    }

    private fun isNearMidnight(instant: Instant): Boolean {
        val zoneId = ZoneId.systemDefault()
        val zonedDateTime = instant.atZone(zoneId)

        val hour = zonedDateTime.hour
        val minute = zonedDateTime.minute
        val seconds = zonedDateTime.second

        return (hour == 23 && minute >= 57 && seconds >= 59)
    }

    private fun updateNotification(text: String) {
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Сбор данных о здоровье")
            .setContentText(text)
            .setSmallIcon(R.drawable.healthy_food_icon)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification)
    }

    private suspend fun fetchStepCountData() {
        try {
            val endTime = Instant.now()
            val startTime = endTime.minus(2, ChronoUnit.MINUTES)

            val response = healthConnectClient.readRecords(
                ReadRecordsRequest(
                    StepsRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )

            for (record in response.records) {
                //добавляем шаги на сервер
                stepsRepository.userStepsLog(UserStepsLogDto(record.count.toInt()))
                Log.d("HealthConnect",
                    "Steps: ${record.count}, Time: ${record.startTime}")
            }
        } catch (e: Exception) {
            Log.e("HealthConnect", "Failed to read steps", e)
        }
    }

    private suspend fun fetchHeartRateData() {
        try {
            val endTime = Instant.now()
            val startTime = endTime.minus(2, ChronoUnit.MINUTES)

            val response = healthConnectClient.readRecords(
                ReadRecordsRequest(
                    HeartRateRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )

            if (response.records.isNotEmpty()) {
                Log.d("HealthConnect",
                    "Found ${response.records.size} heart rate records")

                for (record in response.records) {
                    for (sample in record.samples) {
                        val (date, time) = sample.time.toDateAndTime()

                        heartRepository.userHeartLog(HeartRateLogDto(
                            sample.beatsPerMinute.toInt(),
                            date,
                            time
                        ))
                        Log.d("HealthConnect",
                            "Heart rate: ${sample.beatsPerMinute} bpm, Time: ${sample.time}")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("HealthConnect",
                "Error reading heart rate data", e)
        }
    }

    private fun Instant.toDateAndTime(): Pair<Int, Int> {
        val zonedDateTime = this.atZone(ZoneId.systemDefault())

        val dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val timeFormatter = DateTimeFormatter.ofPattern("HHmm")

        val date = zonedDateTime.format(dateFormatter).toInt()
        val time = zonedDateTime.format(timeFormatter).toInt()

        return Pair(date, time)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        serviceScope.coroutineContext.cancel()
        Log.d("HealthConnect", "Service destroyed")
    }
}