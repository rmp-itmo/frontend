package com.rmp.ui.signup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rmp.R
import com.rmp.data.repository.signup.CreateUserDto
import com.rmp.data.repository.signup.SignupRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.sign

enum class SignupState(val n: Int) {
    WELCOME(0), PARAMS(1), TARGET(2), LOGIN_DATA(3);

    fun prev(): SignupState {
        return when (this) {
            LOGIN_DATA -> TARGET
            TARGET -> PARAMS
            PARAMS -> WELCOME
            WELCOME -> WELCOME
        }
    }

    fun next(): SignupState {
        return when (this) {
            WELCOME -> PARAMS
            PARAMS -> TARGET
            TARGET -> LOGIN_DATA
            LOGIN_DATA -> LOGIN_DATA
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

    open class WelcomeState(
        override val step: SignupState,
        var name: String = "",
        var sex: String = "true",
        var age: String = ""
    ): SignupUiState

    open class ParamsState(
        step: SignupState,
        name: String = "",
        sex: String = "true",
        age: String = "",
        var height: String = "",
        var weight: String = "",
        var activityLevel: ActivityLevel = ActivityLevel.MIDDLE
    ): WelcomeState(step, name, sex, age)

    open class TargetState(
        step: SignupState,
        name: String = "",
        sex: String = "true",
        age: String = "",
        height: String = "",
        weight: String = "",
        activityLevel: ActivityLevel = ActivityLevel.MIDDLE,
        var weightTarget: WeightTarget = WeightTarget.GAIN
    ): ParamsState(step, name, sex, age, height, weight, activityLevel)

    open class LoginDataState(
        step: SignupState,
        name: String = "",
        sex: String = "true",
        age: String = "",
        height: String = "",
        weight: String = "",
        activityLevel: ActivityLevel = ActivityLevel.MIDDLE,
        weightTarget: WeightTarget = WeightTarget.STAY,
        var email: String = "",
        var pass: String = "",
    ): TargetState(step, name, sex, age, height, weight, activityLevel, weightTarget)
}

fun validateLoginData(uiState: SignupUiState.LoginDataState): Boolean =
    uiState.email != "" && uiState.pass != ""

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
    val name: String = "",
    val sex: String = "true",
    val age: String = "",
    val height: String = "",
    val weight: String = "",
    val activityLevel: ActivityLevel? = null,
    val weightTarget: WeightTarget? = null,
    val email: String = "",
    val pass: String = "",
) {
    fun toUiState(): SignupUiState =
        if (weightTarget != null && activityLevel != null)
            SignupUiState.LoginDataState(
                step, name, sex, age, height, weight, activityLevel, weightTarget, email, pass
            )
        else if (height != "" && weight != "" && activityLevel != null)
            SignupUiState.TargetState(
                step, name, sex, age, height, weight, activityLevel, WeightTarget.STAY
            )
        else if (name != "")
            SignupUiState.ParamsState(
                step, name, sex, age, height, weight, activityLevel ?: ActivityLevel.MIDDLE
            )
        else
            SignupUiState.WelcomeState(
                step, name, sex, age
            )

}

class SignupViewModel(private val signupRepo: SignupRepo): ViewModel() {
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
        fun factory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SignupViewModel() as T
            }
        }
    }

    fun prevState() {
        viewModelState.update {
            it.copy(step = it.step.prev())
        }
    }

    suspend fun nextState() {
        viewModelState.update {
            val next = it.step.next()
            if (next == it.step) {
                signupRepo.createUser(CreateUserDto(
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
                return
            }
            it.copy(step = next)
        }
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