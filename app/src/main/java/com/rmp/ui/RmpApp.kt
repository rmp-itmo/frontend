package com.rmp.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.rmp.data.AppContainer
import com.rmp.ui.theme.RmpTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking


@Composable
fun RmpApp(
    appContainer: AppContainer
) {
    RmpTheme {
        val navController = rememberNavController()
        val start = runBlocking {

            if (appContainer.database.authTokenDao().getTokens().accessToken == "") {
                RmpDestinations.HELLO_ROUTE
            } else {
                RmpDestinations.HOME_ROUTE
            }
        }

        RmpNavGraph(
            startDestination = start,
            appContainer = appContainer,
            navController = navController,
        )
    }
}