package com.rmp.data

import android.content.Context
import com.rmp.data.database.AppDatabase
import com.rmp.data.repository.achievements.AchievementsRepoImpl
import com.rmp.data.repository.achievements.AchievementsRepository
import com.rmp.data.repository.forum.ForumRepoImpl
import com.rmp.data.repository.forum.ForumRepository
import com.rmp.data.repository.heart.HeartRepoImpl
import com.rmp.data.repository.heart.HeartRepository
import com.rmp.data.repository.nutrition.NutritionRepoImpl
import com.rmp.data.repository.nutrition.NutritionRepository
import com.rmp.data.repository.signup.UserRepoImpl
import com.rmp.data.repository.signup.UserRepository
import com.rmp.data.repository.sleep.SleepRepoImpl
import com.rmp.data.repository.sleep.SleepRepository
import com.rmp.data.repository.training.TrainingRepoImpl
import com.rmp.data.repository.training.TrainingRepository
import com.rmp.data.repository.water.WaterRepoImpl
import com.rmp.data.repository.water.WaterRepository

/**
 * Dependency Injection container at the application level.
 */
interface AppContainer {
    val userRepository: UserRepository
    val database: AppDatabase
    val heartRepository: HeartRepository
    val waterRepository: WaterRepository
    val sleepRepository: SleepRepository
    val nutritionRepository: NutritionRepository
    val forumRepository: ForumRepository
    val achievementsRepository: AchievementsRepository
    val trainingsRepository: TrainingRepository
}

public var ApplicationDatabase: AppDatabase? = null

/**
 * Implementation for the Dependency Injection container at the application level.
 *
 * Variables are initialized lazily and the same instance is shared across the whole app.
 */
class AppContainerImpl(private val applicationContext: Context, database: AppDatabase) : AppContainer {

    override val userRepository: UserRepository by lazy {
        UserRepoImpl()
    }

    override val database: AppDatabase by lazy {
        database
    }

    override val heartRepository: HeartRepository by lazy {
        HeartRepoImpl()
    }

    override val waterRepository: WaterRepository by lazy {
        WaterRepoImpl()
    }

    override val sleepRepository: SleepRepository by lazy {
        SleepRepoImpl()
    }

    override val nutritionRepository: NutritionRepository by lazy {
        NutritionRepoImpl()
    }
    override val forumRepository: ForumRepository by lazy {
        ForumRepoImpl()
    }
    override val achievementsRepository: AchievementsRepository by lazy {
        AchievementsRepoImpl()
    }
    override val trainingsRepository: TrainingRepository by lazy {
        TrainingRepoImpl()
    }
}