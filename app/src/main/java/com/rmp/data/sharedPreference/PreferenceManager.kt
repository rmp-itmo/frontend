package com.rmp.data.sharedPreference

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit

class PreferenceManager(context: Context) {
    private val sharedPref = context.getSharedPreferences("app_prefs", MODE_PRIVATE)

    fun getIsFirstRun(): Boolean {
        return sharedPref.getBoolean("is_first_run", true)
    }

    fun setIsFirstRunToFalse() {
        sharedPref.edit { putBoolean("is_first_run", false) }
    }
}