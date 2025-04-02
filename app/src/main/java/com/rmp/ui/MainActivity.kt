package com.rmp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.HealthConnectClient.Companion.getOrCreate
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.lifecycle.lifecycleScope
import com.rmp.RmpApplication
import com.rmp.services.HealthConnectForegroundService
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import androidx.core.content.edit

class MainActivity : ComponentActivity() {
    private val healthConnectPermissions = setOf(
        HealthPermission.getReadPermission(HeartRateRecord::class),
        HealthPermission.getWritePermission(HeartRateRecord::class),
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getWritePermission(StepsRecord::class)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val appContainer = (application as RmpApplication).container

        checkHealthConnectPermissions()

        val intent = Intent(this, HealthConnectForegroundService::class.java)
        this.startForegroundService(intent)

        setContent {
            RmpApp(appContainer)
        }
    }

    private fun checkHealthConnectPermissions() {
        val healthConnectClient = getOrCreate(this)

        lifecycleScope.launch {
            try {
                kotlinx.coroutines.delay(500)

                val granted = healthConnectClient.permissionController.getGrantedPermissions()
                Log.d("HealthPermissions", "Granted permissions after delay: $granted")

                if (granted.containsAll(healthConnectPermissions)) {
                    Log.d("HealthPermissions", "All permissions granted, starting service")
                    startHealthService()
                } else {
                    Log.d("HealthPermissions", "Not all permissions granted, requesting")
                    val sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE)
                    val isFirstRun = sharedPref.getBoolean("is_first_run", true)

                    if (isFirstRun) {
                        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.data = "package:$packageName".toUri()
                        startActivity(intent)

                        sharedPref.edit { putBoolean("is_first_run", false) }
                    } else {
                        requestPermissions.launch(healthConnectPermissions.toTypedArray())
                    }
                }
            } catch (e: Exception) {
                Log.e("HealthPermissions", "Error checking permissions", e)
                showError("Ошибка проверки разрешений")
            }
        }
    }

    private val requestPermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        Log.d("HealthPermissions", "Permission results: $results")

        lifecycleScope.launch {
            kotlinx.coroutines.delay(500)

            val granted = HealthConnectClient.getOrCreate(this@MainActivity)
                .permissionController.getGrantedPermissions()

            Log.d("HealthPermissions", "Permissions after request: $granted")

            if (granted.containsAll(listOf(
                    "android.permission.health.READ_HEART_RATE",
                    "android.permission.health.READ_STEPS")
                )) {
                Log.d("HealthPermissions", "All permissions granted, starting service")
                startHealthService()
            } else {
                showError("Не все разрешения были предоставлены. Проверьте настройки.")
            }
        }
    }

    private fun startHealthService() {
        val intent = Intent(this, HealthConnectForegroundService::class.java)
        startForegroundService(intent)
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
