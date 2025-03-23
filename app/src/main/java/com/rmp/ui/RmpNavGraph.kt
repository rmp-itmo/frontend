package com.rmp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rmp.data.AppContainer
import com.rmp.ui.hello.HelloRoute
import com.rmp.ui.hello.HelloViewModel
import com.rmp.ui.login.LoginRoute
import com.rmp.ui.login.LoginViewModel
import com.rmp.ui.signup.ActivityLevel
import com.rmp.ui.signup.SignupRoute
import com.rmp.ui.signup.SignupViewModel
import com.rmp.ui.signup.WeightTarget

val LocalNavController = compositionLocalOf<NavHostController> { error("NavController not found") }

@Composable
fun RmpNavGraph(
    appContainer: AppContainer,
    isExpandedScreen: Boolean,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    openDrawer: () -> Unit = {},
    startDestination: String = RmpDestinations.HOME_ROUTE,
) {

    CompositionLocalProvider(LocalNavController provides navController) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = modifier
        ) {
            composable(
                route = RmpDestinations.HOME_ROUTE,
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
                    factory = LoginViewModel.factory(appContainer.userRepository)
                )
                LoginRoute(
                    loginViewModel = loginViewModel,
                    onLoginSuccess = { navController.navigate(RmpDestinations.HOME_ROUTE) }, // Указать реальное местоположение (сейчас такового нет)
                    onBackClick = { navController.navigate(RmpDestinations.HOME_ROUTE) }
                )
            }
        }
    }
}

