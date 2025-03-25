package com.rmp.ui.login

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import com.rmp.R
import com.rmp.ui.components.AccentButton
import com.rmp.ui.components.AppScreen
import com.rmp.ui.components.Header
import com.rmp.ui.components.SecondaryButton

@Composable
fun LoginScreen(
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onBackClick: () -> Unit
) {

    val context = LocalContext.current

    AppScreen {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp)
                .imePadding(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxHeight(0.8f)
            ) {
                Header(
                    stringResource(R.string.login_header),
                    stringResource(R.string.login_subheading),
                    Modifier.align(Alignment.Start),
                    textAlign = TextAlign.Start,
                )

                Spacer(modifier = Modifier.height(30.dp))

                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = onEmailChange,
                    label = { Text("Введите email") },
                    modifier = Modifier
                        .fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    isError = uiState.emailError != null,
                    supportingText = {
                        uiState.emailError?.let { Text(stringResource(it), color = MaterialTheme.colorScheme.error) }
                    },
                    shape = MaterialTheme.shapes.medium
                )

                OutlinedTextField(
                    value = uiState.password,
                    onValueChange = onPasswordChange,
                    label = { Text("Введите пароль") },
                    isError = uiState.passwordError != null,
                    supportingText = {
                        uiState.passwordError?.let { Text(stringResource(it), color = MaterialTheme.colorScheme.error) }
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium
                )
            }

//            Spacer(modifier = Modifier.weight(0.5f))

            val notification = if (uiState.loginError != null) {
                stringResource(R.string.login_error)
            } else {
                null
            }


            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AccentButton(
                    stringResource(R.string.login),
                ) {
                    if (notification != null) {
                        Toast.makeText(
                            context,
                            notification,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    onLoginClick()
                }

                SecondaryButton(
                    stringResource(R.string.prev),
                    onBackClick,
                )
            }
        }
    }
}