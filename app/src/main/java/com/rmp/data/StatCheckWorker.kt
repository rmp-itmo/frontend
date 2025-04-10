package com.rmp.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.random.Random


class StatsWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    private val notificationHelper by lazy { NotificationHelper(context) }


    override suspend fun doWork(): Result {
        return try {
            val response = fetchStats()
            if (response != null) {
                checkActivity(response)
            } else {
                Log.d("respond", "some exception")
            }
            Result.success()
        } catch (e: Exception) {
            handleUnauthorized(e)
        }
    }

    private suspend fun fetchStats(): StatsResponse? {
        val requestDate = getCurrentDate()

        return ApiClient.authorizedRequest<StatsResponse>(
            ApiClient.Method.POST,
            url = "users/stat/summary",
            data = StatsRequest(requestDate)).successOr(null)
    }

    private fun getCurrentDate(): Int {
        return SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date()).toInt()
    }

    private fun checkActivity(response: StatsResponse) {
        showStatsNotification(response)
    }

    private fun showStatsNotification(stats: StatsResponse) {
        Log.d("respond", "NOTIFICATION")
        notificationHelper.show(
            title = "Сводка активности",
            message = createNotificationMessage(stats)
        )
    }

    private fun createNotificationMessage(stats: StatsResponse): String {
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val issues = listOfNotNull(
            getWaterIssue(stats, currentHour),
            getStepsIssue(stats, currentHour),
            getCaloriesIssue(stats)
        )

        return when {
            issues.isEmpty() -> getPositiveMessage()
            else -> selectMainIssue(issues).message
        }
    }

    private data class HealthIssue(
        val priority: Int,
        val message: String,
        val progressPercent: Double
    )

    private fun getWaterIssue(stats: StatsResponse, currentHour: Int): HealthIssue? {
        val target = stats.waterTarget
        val current = stats.glassesOfWater
        if (target == 0.0) return null

        val progress = current / target
        val timeBasedThreshold = when {
            currentHour < 12 -> 0.3
            currentHour < 18 -> 0.6
            else -> 0.8
        }

        return if (progress < timeBasedThreshold) {
            val remaining = (target * timeBasedThreshold - current).toInt()
            val timePeriod = when {
                currentHour < 12 -> "утром"
                currentHour < 18 -> "днём"
                else -> "вечером"
            }
            HealthIssue(
                priority = 1,
                message = "💧 Воды выпито меньше нормы ($timePeriod)! " +
                        "Осталось до цели: $remaining стаканов\n" +
                        getWaterTip(currentHour),
                progressPercent = progress
            )
        } else null
    }

    private fun getStepsIssue(stats: StatsResponse, currentHour: Int): HealthIssue? {
        val target = stats.stepsTarget
        val current = stats.stepsCurrent
        if (target == 0) return null

        val progress = current.toDouble() / target
        val timeLeft = 24 - currentHour

        return if (progress < 0.5 && timeLeft < 6) {
            HealthIssue(
                priority = 2,
                message = "👟 Нужно пройти ещё ${target - current} шагов! " +
                        "Попробуйте вечернюю прогулку\n" +
                        getStepsTip(timeLeft, stats),
                progressPercent = progress
            )
        } else null
    }

    private fun getCaloriesIssue(stats: StatsResponse): HealthIssue? {
        val remaining = stats.caloriesTarget - stats.caloriesCurrent
        return when {
            remaining > 500 -> HealthIssue(
                priority = 3,
                message = "🍎 Не хватает ${remaining.toInt()} ккал. " +
                        "Попробуйте полезные перекусы",
                progressPercent = stats.caloriesCurrent / stats.caloriesTarget
            )
            remaining < -300 -> HealthIssue(
                priority = 3,
                message = "🔥 Превышение на ${-remaining.toInt()} ккал.",
                progressPercent = 1.0
            )
            else -> null
        }
    }

    private fun selectMainIssue(issues: List<HealthIssue>): HealthIssue {
        val mostCritical = issues.minByOrNull { it.progressPercent }

        return if (Random.nextBoolean() && issues.size > 1) {
            issues.filter { it.progressPercent < 0.5 }.random()
        } else {
            mostCritical ?: issues.random()
        }
    }

    private fun getPositiveMessage(): String {
        return listOf(
            "🎉 Отличный прогресс! Вы молодец!",
            "🌟 Все цели достигнуты! Так держать!",
            "💪 Вы на правильном пути! Продолжайте в том же духе!"
        ).random()
    }

    private fun getStepsTip(hoursLeft: Int, stats: StatsResponse): String {
        val remainingSteps = stats.stepsTarget - stats.stepsCurrent
        val stepsPerHour = remainingSteps / hoursLeft
        return "Попробуйте пройти $stepsPerHour шагов каждый час"
    }
    private fun getWaterTip(currentHour: Int): String {
        return when {
            currentHour < 12 -> "Начните день со стакана воды с лимоном"
            currentHour < 18 -> "Поставьте бутылку с водой на видное место"
            else -> "Пейте небольшими порциями перед сном"
        }
    }

    private suspend fun handleUnauthorized(e: Exception): Result = withContext(Dispatchers.IO) {
        Log.d("exception", "${e.message}")
        Result.failure()
    }
}


@Serializable
data class StatsRequest(
    val date: Int
)

@Serializable
data class StatsResponse(
    val caloriesTarget: Double,
    val caloriesCurrent: Double,
    val waterTarget: Double,
    val waterCurrent: Double,
    val stepsTarget: Int,
    val stepsCurrent: Int,
    val sleepHours: Int,
    val sleepMinutes: Int,
    val heartRate: Double?,
    val glassesOfWater: Double
)

class NotificationHelper(private val context: Context) {
    private val channelId = "activity_channel"
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            channelId,
            "Activity Notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Shows activity progress notifications"
        }
        notificationManager.createNotificationChannel(channel)
    }

    fun show(title: String, message: String) {
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Замените на свою иконку
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        private const val NOTIFICATION_ID = 1001
    }
}