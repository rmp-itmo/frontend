package com.rmp

import android.app.Application
import com.rmp.data.AppContainer
import com.rmp.data.AppContainerImpl

class RmpApplication : Application() {
    companion object {
        const val RMP_APP_URI = "https://app.rmp.dudosyka.ru"
    }

    // AppContainer instance used by the rest of classes to obtain dependencies
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl(this)
    }
}