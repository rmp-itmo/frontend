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
import androidx.compose.ui.unit.dp

import com.rmp.R
import com.rmp.ui.components.AccentButton
import com.rmp.ui.components.AppScreen
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
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),  // Уменьшаем расстояние между элементами
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Вход",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .align(Alignment.Start)
            )

            Text(
                text = "Рады видеть тебя снова! Введи свои данные для входа.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(bottom = 16.dp),  // Уменьшаем отступ снизу
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(60.dp))

            OutlinedTextField(
                value = uiState.email,
                onValueChange = onEmailChange,
                label = { Text("Введите email") },
                isError = uiState.emailError != null,
                modifier = Modifier
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )
            uiState.emailError?.let { Text(stringResource(it), color = MaterialTheme.colorScheme.error) }

            OutlinedTextField(
                value = uiState.password,
                onValueChange = onPasswordChange,
                label = { Text("Введите пароль") },
                isError = uiState.passwordError != null,
                modifier = Modifier
                    .fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )
            uiState.passwordError?.let { Text(stringResource(it), color = MaterialTheme.colorScheme.error) }


            Spacer(modifier = Modifier.weight(1f))

            val notification = if (uiState.loginError != null) {
                    stringResource(R.string.login_error)
            } else {
                null
            }

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