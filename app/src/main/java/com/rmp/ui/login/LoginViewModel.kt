package com.rmp.ui.login


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rmp.R
import com.rmp.data.AppContainer
import com.rmp.data.repository.signup.UserLoginDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

interface LoginUiState {
    val email: String
    val password: String
    val emailError: Int?
    val passwordError: Int?
    val loginError: Int?
    val isLoginEnabled: Boolean
}

private class LoginViewModelState(
    override val email: String = "",
    override val password: String = "",
    override val emailError: Int? = null,
    override val passwordError: Int? = null,
    override val loginError: Int? = null,
    override val isLoginEnabled: Boolean = false
) : LoginUiState {
    fun toUiState(): LoginUiState = this

    fun copy(
        email: String = this.email,
        password: String = this.password,
        emailError: Int? = this.emailError,
        passwordError: Int? = this.passwordError,
        loginError: Int? = this.loginError,
        isLoginEnabled: Boolean = this.isLoginEnabled
    ) = LoginViewModelState(email, password, emailError, passwordError,  loginError, isLoginEnabled)
}
class LoginViewModel(private val container: AppContainer) : ViewModel() {
    private val viewModelState = MutableStateFlow(
        LoginViewModelState()
    )

    val uiState = viewModelState
        .map(LoginViewModelState::toUiState)
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    fun onEmailChange(newEmail: String) {
        val emailError = if (newEmail.isValidEmail()) null else R.string.invalid_email
        updateState(viewModelState.value.copy(email = newEmail, emailError = emailError))
    }

    fun onPasswordChange(newPassword: String) {
        val passwordError = if (newPassword.length >= 6) null else R.string.short_password
        updateState(viewModelState.value.copy(password = newPassword, passwordError = passwordError))
    }

    fun onLoginClick() {
        viewModelScope.launch {
            val (tokens, result) = container.userRepository.loginUser(
                UserLoginDto(
                    viewModelState.value.email,
                    viewModelState.value.password
                )
            )
            if (result) {
                container.database.authTokenDao().saveTokens(
                    accessToken = tokens.accessToken!!,
                    refreshToken = tokens.refreshToken!!
                )
                onLoginSuccess.invoke()
            } else {
                val errorMessage = R.string.login_error
                updateState(viewModelState.value.copy(loginError = errorMessage))
            }
        }
    }

    var onLoginSuccess: () -> Unit = {}

    private fun updateState(newState: LoginViewModelState) {
        viewModelState.value = newState.copy(
            isLoginEnabled = newState.emailError == null &&
                    newState.passwordError == null &&
                    newState.email.isNotBlank() &&
                    newState.password.isNotBlank()
        )
    }

    companion object {
        fun factory(appContainer: AppContainer): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return LoginViewModel(appContainer) as T
            }
        }
    }
}

fun String.isValidEmail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}
