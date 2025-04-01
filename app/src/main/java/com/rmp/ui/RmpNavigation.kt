package com.rmp.ui

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

/**
 * Destinations used in the [RmpApp].
 */
object RmpDestinations {
    const val HOME_ROUTE = "home"
    const val HELLO_ROUTE = "hello"
    const val SIGN_UP_ROUTE = "signup"
    const val LOGIN_ROUTE = "login"
    const val NUTRITION_ROUTE = "nutrition"
}

/**
 * Models the navigation actions in the app.
 */
class RmpNavigationActions(navController: NavHostController) {
    val navigateToHome: () -> Unit = {
        navController.navigate(RmpDestinations.HOME_ROUTE) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
        }
    }
}
