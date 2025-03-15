package com.rmp.data

import android.content.Context

/**
 * Dependency Injection container at the application level.
 */
interface AppContainer {}

/**
 * Implementation for the Dependency Injection container at the application level.
 *
 * Variables are initialized lazily and the same instance is shared across the whole app.
 */
class AppContainerImpl(private val applicationContext: Context) : AppContainer {}