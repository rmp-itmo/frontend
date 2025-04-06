package com.rmp.ui

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rmp.R
import com.rmp.data.AppContainer
import com.rmp.ui.heart.HeartRoute
import com.rmp.ui.heart.HeartViewModel
import com.rmp.ui.hello.HelloRoute
import com.rmp.ui.hello.HelloViewModel
import com.rmp.ui.home.HomeRoute
import com.rmp.ui.home.HomeViewModel
import com.rmp.ui.login.LoginRoute
import com.rmp.ui.login.LoginViewModel
import com.rmp.ui.nutrition.NutritionRoute
import com.rmp.ui.nutrition.NutritionViewModel
import com.rmp.ui.signup.ActivityLevel
import com.rmp.ui.signup.SignupRoute
import com.rmp.ui.signup.SignupViewModel
import com.rmp.ui.signup.WeightTarget
import com.rmp.ui.sleep.SleepRoute
import com.rmp.ui.sleep.SleepViewModel
import com.rmp.ui.sleep.history.SleepHistoryRoute
import com.rmp.ui.sleep.history.SleepHistoryViewModel
import com.rmp.ui.water.WaterRoute
import com.rmp.ui.water.WaterViewModel

val LocalNavController = compositionLocalOf<NavHostController> { error("NavController not found") }

var appLogout: () -> Unit = {}

@Composable
fun RmpNavGraph(
    appContainer: AppContainer,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = RmpDestinations.HELLO_ROUTE,
) {
    val ctx = LocalContext.current
    val err = stringResource(R.string.error_session_expired)
    appLogout = {
        navController.navigate(RmpDestinations.HELLO_ROUTE)
        Toast.makeText(ctx, err, Toast.LENGTH_LONG).show()
    }
    CompositionLocalProvider(LocalNavController provides navController) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = modifier
        ) {
            composable(
                route = RmpDestinations.HELLO_ROUTE,
            ) { _ ->
                val helloViewModel: HelloViewModel = viewModel(
                    factory = HelloViewModel.factory()
                )
                HelloRoute(
                    helloViewModel = helloViewModel,
                    goToSignUp = { navController.navigate(RmpDestinations.SIGN_UP_ROUTE) },
                    goToLogin = { navController.navigate(RmpDestinations.LOGIN_ROUTE) }
                )
            }
            composable(
                route = RmpDestinations.SIGN_UP_ROUTE,
            ) { _ ->
                val signupViewModel: SignupViewModel = viewModel(
                    factory = SignupViewModel.factory(appContainer.userRepository)
                )
                SignupRoute(
                    signupViewModel = signupViewModel,
                    prevState = {
                        signupViewModel.prevState()
                    },
                    nextState = {
                        signupViewModel.nextState()
                    },
                    setWelcome = { name: String, sex: String, age: String ->
                        signupViewModel.setWelcome(name, sex, age)
                    },
                    setParams = { height: String, weight: String, activityLevel: ActivityLevel ->
                        signupViewModel.setParamsStep(height, weight, activityLevel)
                    },
                    setTarget = { weightTarget: WeightTarget ->
                        signupViewModel.setTarget(weightTarget)
                    },
                    setLoginStep = { email: String, pass: String ->
                        signupViewModel.setLoginStep(email, pass)
                    }
                )
            }
            composable(
                route = RmpDestinations.LOGIN_ROUTE,
            ) { _ ->
                val loginViewModel: LoginViewModel = viewModel(
                    factory = LoginViewModel.factory(appContainer)
                )
                LoginRoute(
                    loginViewModel = loginViewModel,
                    onLoginSuccess = { navController.navigate(RmpDestinations.HOME_ROUTE) },
                    onBackClick = { navController.navigate(RmpDestinations.HELLO_ROUTE) }
                )
            }
            composable(
                route = RmpDestinations.HOME_ROUTE,
            ) { _ ->
                val homeViewModel: HomeViewModel = viewModel(
                    factory = HomeViewModel.factory(appContainer)
                )
                HomeRoute(
                    homeViewModel = homeViewModel,
                )
            }
            composable(
                route = RmpDestinations.HEART_ROUTE,
            ) { _ ->
                val heartViewModel: HeartViewModel = viewModel(
                    factory = HeartViewModel.factory(appContainer)
                )
                HeartRoute(
                    heartViewModel = heartViewModel
                )
            }
            composable(
                route = RmpDestinations.SLEEP_HISTORY_ROUTE,
            ) { _ ->
                val sleepHistoryViewModel: SleepHistoryViewModel = viewModel(
                    factory = SleepHistoryViewModel.factory(appContainer)
                )
                SleepHistoryRoute(
                    sleepHistoryViewModel = sleepHistoryViewModel
                )
            }
            composable(
                route = RmpDestinations.WATER_ROUTE,
            ) { _ ->
                val waterViewModel: WaterViewModel = viewModel(
                    factory = WaterViewModel.factory(appContainer)
                )
                WaterRoute(
                    waterViewModel = waterViewModel,
                ) 
            }
            composable(
                route = RmpDestinations.NUTRITION_ROUTE,
            ) { _ ->
                val nutritionViewModel: NutritionViewModel = viewModel(
                    factory = NutritionViewModel.factory(appContainer)
                )
                NutritionRoute(
                    nutritionViewModel = nutritionViewModel
                )
            }

            composable(
                route = RmpDestinations.SLEEP_ROUTE
            ) { _ ->
                val sleepViewModel: SleepViewModel = viewModel(
                    factory = SleepViewModel.factory(appContainer.sleepRepository)
                )
                SleepRoute (
                    sleepViewModel = sleepViewModel,
                    { navController.navigate(RmpDestinations.SLEEP_HISTORY_ROUTE) }
                )
            }
        }
    }
}

