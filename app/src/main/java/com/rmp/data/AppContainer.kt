package com.rmp.data

import android.content.Context
import com.rmp.data.repository.signup.UserRepoImpl
import com.rmp.data.repository.signup.UserRepository

/**
 * Dependency Injection container at the application level.
 */
interface AppContainer {
    val userRepository: UserRepository
}

/**
 * Implementation for the Dependency Injection container at the application level.
 *
 * Variables are initialized lazily and the same instance is shared across the whole app.
 */
class AppContainerImpl(private val applicationContext: Context) : AppContainer {

    override val userRepository: UserRepository by lazy {
        UserRepoImpl()
    }
}