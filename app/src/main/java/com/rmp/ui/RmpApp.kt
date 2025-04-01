package com.rmp.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.rmp.data.AppContainer
import com.rmp.ui.theme.RmpTheme
import kotlinx.coroutines.runBlocking


@Composable
fun RmpApp(
    appContainer: AppContainer,
) {
    RmpTheme {
        val navController = rememberNavController()
        val start = runBlocking {

            if (appContainer.database.authTokenDao().getTokens().accessToken == "") {
                RmpDestinations.HELLO_ROUTE
            } else {
                RmpDestinations.NUTRITION_ROUTE // Заменить на нужный экран. См LoginViewModel.
            }
        }

        RmpNavGraph(
            startDestination = start,
            appContainer = appContainer,
            navController = navController,
        )
    }
}