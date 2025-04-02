package com.rmp.ui.signup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rmp.R
import com.rmp.data.ErrorMessage
import com.rmp.data.repository.signup.CreateUserDto
import com.rmp.data.repository.signup.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class SignupState(val n: Int) {
    WELCOME(0), PARAMS(1), TARGET(2), LOGIN_DATA(3), SUCCESS(4);

    fun prev(): SignupState {
        return when (this) {
            LOGIN_DATA -> TARGET
            TARGET -> PARAMS
            PARAMS -> WELCOME
            WELCOME -> WELCOME
            SUCCESS -> SUCCESS
        }
    }

    fun next(): SignupState {
        return when (this) {
            WELCOME -> PARAMS
            PARAMS -> TARGET
            TARGET -> LOGIN_DATA
            LOGIN_DATA -> LOGIN_DATA
            SUCCESS -> SUCCESS
        }
    }
}

data class StateDescription(
    val header: Int,
    val subtitle: Int
) {
    companion object {
        fun buildFrom(
            state: Int
        ): StateDescription {
            return when (state) {
                SignupState.WELCOME.n -> StateDescription(
                    R.string.signup_header,
                    R.string.signup_step_1,
                )

                SignupState.PARAMS.n -> StateDescription(
                    R.string.signup_header,
                    R.string.signup_step_2,
                )

                SignupState.TARGET.n -> StateDescription(
                    R.string.signup_header,
                    R.string.signup_step_3,
                )

                SignupState.LOGIN_DATA.n -> StateDescription(
                    R.string.signup_header,
                    R.string.signup_step_4,
                )

                SignupState.SUCCESS.n -> StateDescription(
                    R.string.signup_header,
                    R.string.signup_step_5,
                )

                else -> StateDescription(
                    R.string.signup_header,
                    R.string.signup_step_1,
                )
            }
        }
    }
}

enum class ActivityLevel(val id: Int, val labelResource: Int) {
    LOW(1, R.string.activity_low),
    MIDDLE(2, R.string.activity_medium),
    HIGH(3, R.string.activity_high);
}

enum class WeightTarget(val id: Int, val imageResource: Int, val labelResource: Int) {
    LOSE(1, R.drawable.weight_lower, R.string.weight_lose),
    STAY(2, R.drawable.weight_same, R.string.weight_same),
    GAIN(3, R.drawable.weight_higher, R.string.weight_gain);

    companion object {
        fun getByName(name: String): WeightTarget =
            entries.first { it.name == name }
    }
}

sealed interface SignupUiState {
    val step: SignupState
    val errors: MutableList<ErrorMessage>

    open class WelcomeState(
        override val step: SignupState,
        override val errors: MutableList<ErrorMessage> = mutableListOf(),
        var name: String = "123",
        var sex: String = "true",
        var age: String = "123"
    ): SignupUiState

    open class ParamsState(
        step: SignupState,
        errors: MutableList<ErrorMessage> = mutableListOf(),
        name: String = "123",
        sex: String = "true",
        age: String = "123",
        var height: String = "123",
        var weight: String = "123",
        var activityLevel: ActivityLevel = ActivityLevel.MIDDLE
    ): WelcomeState(step, errors, name, sex, age)

    open class TargetState(
        step: SignupState,
        errors: MutableList<ErrorMessage> = mutableListOf(),
        name: String = "123",
        sex: String = "true",
        age: String = "123",
        height: String = "123",
        weight: String = "123",
        activityLevel: ActivityLevel = ActivityLevel.MIDDLE,
        var weightTarget: WeightTarget = WeightTarget.GAIN
    ): ParamsState(step, errors, name, sex, age, height, weight, activityLevel)

    open class LoginDataState(
        step: SignupState,
        errors: MutableList<ErrorMessage> = mutableListOf(),
        name: String = "123",
        sex: String = "true",
        age: String = "123",
        height: String = "123",
        weight: String = "123",
        activityLevel: ActivityLevel = ActivityLevel.MIDDLE,
        weightTarget: WeightTarget = WeightTarget.STAY,
        var email: String = "123@test.test",
        var pass: String = "123",
    ): TargetState(step, errors, name, sex, age, height, weight, activityLevel, weightTarget)
}

fun validateLoginData(uiState: SignupUiState.LoginDataState): Boolean =
    uiState.email != "" && uiState.pass != "" && android.util.Patterns.EMAIL_ADDRESS.matcher(uiState.email).matches()

fun validateTarget(uiState: SignupUiState.TargetState): Boolean = true

fun validateParams(uiState: SignupUiState.ParamsState): Boolean =
    uiState.height != "" && uiState.weight != ""

fun validateWelcome(uiState: SignupUiState.WelcomeState): Boolean {
    Log.d("tag", "Validated: ${uiState.name != "" && uiState.age != "" && uiState.sex != ""}")
    Log.d("tag", "Name: ${uiState.name} != '' -> ${uiState.name != ""}")
    Log.d("tag", "Age: ${uiState.age} != '' -> ${uiState.age != ""}")
    Log.d("tag", "Sex: ${uiState.sex} != '' -> ${uiState.sex != ""}")
    return uiState.name != "" && uiState.age != "" && uiState.sex != ""
}

private data class SignupViewModelState(
    val step: SignupState = SignupState.WELCOME,
    val name: String = "123",
    val sex: String = "true",
    val age: String = "123",
    val height: String = "123",
    val weight: String = "123",
    val activityLevel: ActivityLevel? = null,
    val weightTarget: WeightTarget? = null,
    val email: String = "123@test.test",
    val pass: String = "123",
    val errors: List<ErrorMessage> = emptyList()
) {
    fun toUiState(): SignupUiState =
        if (weightTarget != null && activityLevel != null)
            SignupUiState.LoginDataState(
                step, errors.toMutableList(), name, sex, age, height, weight, activityLevel, weightTarget, email, pass
            )
        else if (height != "" && weight != "" && activityLevel != null)
            SignupUiState.TargetState(
                step, errors.toMutableList(), name, sex, age, height, weight, activityLevel, WeightTarget.STAY
            )
        else if (name != "")
            SignupUiState.ParamsState(
                step, errors.toMutableList(), name, sex, age, height, weight, activityLevel ?: ActivityLevel.MIDDLE
            )
        else
            SignupUiState.WelcomeState(
                step, errors.toMutableList(), name, sex, age
            )

}

class SignupViewModel(private val userRepository: UserRepository): ViewModel() {
    private val viewModelState = MutableStateFlow(
        SignupViewModelState()
    )

    // UI state exposed to the UI
    val uiState = viewModelState
        .map(SignupViewModelState::toUiState)
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        viewModelScope.launch {}
    }

    companion object {
        fun factory(userRepository: UserRepository): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SignupViewModel(userRepository) as T
            }
        }
    }

    fun prevState() {
        viewModelState.update {
            it.copy(step = it.step.prev(), errors = emptyList())
        }
    }

    suspend fun nextState(): Boolean {
        var anyErrors = false
        viewModelState.update {
            val next = it.step.next()
            if (next == it.step) {
                val result = userRepository.createUser(CreateUserDto(
                    it.name,
                    it.email,
                    it.pass,
                    it.sex == "true",
                    it.age.toInt(),
                    it.height.toDouble(),
                    it.weight.toDouble(),
                    it.activityLevel!!.id,
                    it.weightTarget!!.id
                ))
                Log.d("tag", "TRY TO CREATE USER, RESULT = ${result}")
                if (result) {
                    Log.d("tag", "Пользователь успешно создан")
                    it.copy(step = SignupState.SUCCESS, errors = emptyList())
                } else {
                    anyErrors = true
                    it.copy(errors = listOf(ErrorMessage(null, message = R.string.error_user_creation)))
                }
            } else {
                it.copy(step = next)
            }
        }
        return !anyErrors
    }

    fun setWelcome(name: String, sex: String, age: String) {
        viewModelState.update {
            it.copy(name = name, sex = sex, age = age)
        }
    }

    fun setParamsStep(height: String, weight: String, activityLevel: ActivityLevel) {
        viewModelState.update {
            it.copy(height = height, weight = weight, activityLevel = activityLevel)
        }
    }

    fun setTarget(weightTarget: WeightTarget) {
        viewModelState.update {
            it.copy(weightTarget = weightTarget)
        }
    }

    fun setLoginStep(email: String, pass: String) {
        viewModelState.update {
            it.copy(email = email, pass = pass)
        }
    }
}