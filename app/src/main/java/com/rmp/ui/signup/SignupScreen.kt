package com.rmp.ui.signup

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.ColorFilter
import com.rmp.R
import com.rmp.ui.LocalNavController
import com.rmp.ui.RmpDestinations
import com.rmp.ui.components.AccentButton
import com.rmp.ui.components.AppScreen
import com.rmp.ui.components.DropDown
import com.rmp.ui.components.Header
import com.rmp.ui.components.ScreenStepVisualizer
import com.rmp.ui.components.SecondaryButton
import kotlinx.coroutines.launch
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.async

@Composable
fun WeightSelection(
    current: WeightTarget,
    onTargetSelect: (WeightTarget) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
//        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        for (it in WeightTarget.entries) {
            val selected = it == current

            val animatedColor by animateColorAsState(
                targetValue = if (selected) ButtonDefaults.buttonColors().contentColor else ButtonDefaults.buttonColors().containerColor,
                label = "color",
                animationSpec = tween(100)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(160.dp)
                    .clickable { onTargetSelect(it) }
                    .padding(10.dp)
            ) {
                NavigationRailItem(
                    selected = selected,
                    onClick = { onTargetSelect(it) },
                    icon = {
                        Column {
                            Image(
                                painter = painterResource(it.imageResource),
                                contentDescription = it.name,
                                contentScale = ContentScale.Inside,
                                colorFilter = ColorFilter.tint(animatedColor),
                                modifier = Modifier
                                    .width(120.dp)
                                    .height(51.dp)
                            )
                        }
                    },
                    label = {
                        Text(text = stringResource(it.labelResource))
                    },
                    colors = NavigationRailItemColors(
                        selectedIconColor = ButtonDefaults.buttonColors().contentColor,
                        selectedTextColor = ButtonDefaults.buttonColors().containerColor,
                        selectedIndicatorColor = ButtonDefaults.buttonColors().containerColor,
                        unselectedIconColor = ButtonDefaults.buttonColors().disabledContentColor,
                        unselectedTextColor = ButtonDefaults.buttonColors().containerColor,
                        disabledIconColor = ButtonDefaults.buttonColors().disabledContentColor,
                        disabledTextColor = ButtonDefaults.buttonColors().disabledContentColor
                    )
                )
            }
        }
    }
}

@Composable
fun SexSelection(
    current: Boolean,
    onSexSelect: (Boolean) -> Unit,
) {
    Row {
        listOf(true, false).forEach {
            val selected = current == it

            val bg by animateColorAsState(
                targetValue = if (selected) ButtonDefaults.buttonColors().containerColor else Color.Transparent,
                animationSpec = tween(300)
            )

            val color by animateColorAsState(
                targetValue = if (selected) ButtonDefaults.buttonColors().contentColor else ButtonDefaults.buttonColors().containerColor,
                animationSpec = tween(300)
            )

            Button(
                colors = ButtonColors(
                    containerColor = bg,
                    contentColor = color,
                    disabledContainerColor = ButtonDefaults.buttonColors().disabledContainerColor,
                    disabledContentColor = ButtonDefaults.buttonColors().disabledContentColor
                ),
                onClick = { onSexSelect(it) }
            ) {
                Text(color = color, text = stringResource(
                    if (it) R.string.male else R.string.female
                ))
            }
        }
    }
}



@Composable
fun SignupScreen(
    uiState: SignupUiState,
    prevState: () -> Unit,
    nextState: suspend () -> Boolean,
    setWelcome: (String, String, String) -> Unit,
    setParams: (String, String, ActivityLevel) -> Unit,
    setTarget: (WeightTarget) -> Unit,
    setLoginStep: (String, String) -> Unit
) {
    val navigator = LocalNavController.current
    val context = LocalContext.current
    val pagerState = rememberPagerState { SignupState.entries.size }
    val animationScope = rememberCoroutineScope()
    val apiScope = rememberCoroutineScope()
    var emailError by remember { mutableStateOf(false) }

    val pageData = StateDescription.buildFrom(pagerState.currentPage)

    val nextPage: (state: SignupState, end: Boolean) -> Unit = { it, end ->
        if (!end && it.n > uiState.step.n) {
            apiScope.launch {
                nextState()
            }
            animationScope.launch {
                pagerState.animateScrollToPage(pagerState.currentPage.inc())
            }
        }

        if (end) {
            val apiCall = apiScope.async {
                nextState()
            }
            animationScope.launch {
                val success = apiCall.await()
                Log.d("tag", "$success ${uiState.errors}")
                if (success)
                    pagerState.animateScrollToPage(pagerState.pageCount)
            }
        }
    }

    AppScreen {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = false,
                modifier = Modifier
                    .align(alignment = Alignment.CenterHorizontally)
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
            ) { page ->
                Column(
                    verticalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(30.dp)
                ) {
                    Header(
                        stringResource(pageData.header),
                        stringResource(pageData.subtitle),
                        Modifier.align(Alignment.Start),
                        textAlign = TextAlign.Start,
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        if (uiState.errors.isNotEmpty()) {
                            Column {
                                for (error in uiState.errors) {
                                    Text(stringResource(error.message), color = MaterialTheme.colorScheme.error)
                                    Spacer(modifier = Modifier.height(10.dp))
                                }
                            }
                        }
                        when (uiState.step) {
                            SignupState.WELCOME -> {
                                uiState as SignupUiState.WelcomeState

                                // Set defaults
                                setWelcome(uiState.name, uiState.sex, uiState.age)

                                OutlinedTextField(
                                    label = { Text("Имя") },
                                    value = uiState.name,
                                    onValueChange = { setWelcome(it, uiState.sex, uiState.age) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(15.dp))
                                Text(
                                    stringResource(R.string.sex),
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp)
                                )
                                SexSelection(uiState.sex == "true") {
                                    setWelcome(uiState.name, it.toString(), uiState.age)
                                }
                                Spacer(modifier = Modifier.height(15.dp))
                                OutlinedTextField(
                                    label = { Text("Возраст") },
                                    value = uiState.age,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    onValueChange = {
                                        if (it.all { c -> c.isDigit() }) {
                                            setWelcome(uiState.name, uiState.sex, it)
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            SignupState.PARAMS -> {
                                uiState as SignupUiState.ParamsState

                                setParams(uiState.height, uiState.weight, uiState.activityLevel)

                                OutlinedTextField(
                                    label = { Text(stringResource(R.string.height)) },
                                    value = uiState.height,
                                    trailingIcon = {
                                        Text(stringResource(R.string.sm))
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    onValueChange = {
                                        if (it.all { c -> c.isDigit() }) {
                                            setParams(it, uiState.weight, uiState.activityLevel)
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(15.dp))

                                OutlinedTextField(
                                    label = { Text(stringResource(R.string.weight)) },
                                    trailingIcon = {
                                        Text(stringResource(R.string.kg))
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    value = uiState.weight,
                                    onValueChange = {
                                        if (it.all { c -> c.isDigit() }) {
                                            setParams(uiState.height, it, uiState.activityLevel)
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )


                                val entryByLabel = ActivityLevel.entries.associateBy {
                                    stringResource(it.labelResource)
                                }

                                Spacer(modifier = Modifier.height(15.dp))

                                DropDown(
                                    options = ActivityLevel.entries.map { stringResource(it.labelResource) },
                                    label = stringResource(R.string.activity_level),
                                    value = stringResource(uiState.activityLevel.labelResource),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    setParams(uiState.height, uiState.weight, entryByLabel[it]!!)
                                }
                            }
                            SignupState.TARGET -> {
                                uiState as SignupUiState.TargetState

                                // Set default value
                                setTarget(uiState.weightTarget)

                                WeightSelection(uiState.weightTarget) {
                                    setTarget(it)
                                }
                            }
                            SignupState.LOGIN_DATA -> {
                                uiState as SignupUiState.LoginDataState

                                var passwordHidden by remember { mutableStateOf(true) }

                                OutlinedTextField(
                                    label = { Text("Email") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                    value = uiState.email,
                                    isError = emailError,
                                    supportingText = {
                                        if (emailError)
                                            Text(stringResource(R.string.invalid_email), color = MaterialTheme.colorScheme.error)
                                    },
                                    onValueChange = { emailError = false; setLoginStep(it, uiState.pass) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(15.dp))
                                OutlinedTextField(
                                    label = { Text(stringResource(R.string.password)) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                    visualTransformation = if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
                                    value = uiState.pass,
                                    onValueChange = { setLoginStep(uiState.email, it) },
                                    trailingIcon = {
                                        IconButton(onClick = { passwordHidden = !passwordHidden }) {
                                            val visibilityIcon =
                                                if (passwordHidden) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                                            Icon(imageVector = visibilityIcon, contentDescription = "")
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            SignupState.SUCCESS -> {}
                        }
                    }
                }
            }

            Column(
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .requiredHeight(150.dp)
            ) {
                ScreenStepVisualizer(
                    SignupState.entries.size, pagerState.currentPage,
                    Modifier.padding(bottom = 30.dp)
                )

                val text = if (uiState.step != SignupState.SUCCESS) R.string.next else R.string.signin

                val errorString = stringResource(R.string.error_check_fields)

                AccentButton(stringResource(text)) {
                    Log.d("tag", "next triggered")
                    when (uiState.step) {
                        SignupState.WELCOME -> {
                            val state = try {
                                uiState as SignupUiState.ParamsState
                            } catch (_: Exception) { null } ?: return@AccentButton

                            if (validateWelcome(state)) {
                                nextPage(SignupState.PARAMS, false)
                            } else {
                                Toast.makeText(
                                    context,
                                    errorString,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            return@AccentButton
                        }
                        SignupState.PARAMS -> {
                            val state = try {
                                uiState as SignupUiState.TargetState
                            } catch (_: Exception) { null } ?: return@AccentButton

                            if (validateParams(state)) {
                                nextPage(SignupState.TARGET, false)
                            } else {
                                Toast.makeText(
                                    context,
                                    errorString,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            return@AccentButton
                        }
                        SignupState.TARGET -> {
                            val state = try {
                                uiState as SignupUiState.LoginDataState
                            } catch (_: Exception) { null } ?: return@AccentButton

                            if (validateTarget(state)) {
                                nextPage(SignupState.LOGIN_DATA, false)
                            } else {
                                Toast.makeText(
                                    context,
                                    errorString,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            return@AccentButton
                        }
                        SignupState.LOGIN_DATA -> {
                            val state = try {
                                uiState as SignupUiState.LoginDataState
                            } catch (_: Exception) { null } ?: return@AccentButton

                            if (validateLoginData(state)) {
                                nextPage(SignupState.LOGIN_DATA, true)
                            } else {
                                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
                                    emailError = true
                                }
                                Toast.makeText(
                                    context,
                                    errorString,
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                            return@AccentButton
                        }

                        SignupState.SUCCESS -> {
                            navigator.navigate(RmpDestinations.LOGIN_ROUTE)
                        }
                    }
                }
                if (uiState.step.n <= SignupState.LOGIN_DATA.n)
                    SecondaryButton(stringResource(R.string.prev)) {
                        if (uiState.step == SignupState.WELCOME)
                            navigator.navigate(RmpDestinations.HOME_ROUTE)
                        else {
                            prevState()
                            animationScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage.dec())
                            }
                        }
                    }
            }
        }
    }
}
