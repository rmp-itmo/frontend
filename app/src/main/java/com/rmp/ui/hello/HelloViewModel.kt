package com.rmp.ui.hello

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rmp.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class HelloScreenStep(val n: Int) {
    FIRST(1), SECOND(2), THIRD(3)
}

data class HelloStepDescription(
    val image: Int,
    val text: Int
)

fun getStepDescription(step: HelloScreenStep): HelloStepDescription = when (step) {
    HelloScreenStep.FIRST -> HelloStepDescription(
        R.drawable.step_1,
        R.string.step_1
    )
    HelloScreenStep.SECOND -> HelloStepDescription(
        R.drawable.step_2,
        R.string.step_2
    )
    HelloScreenStep.THIRD -> HelloStepDescription(
        R.drawable.step_3,
        R.string.step_3
    )
}

interface HelloUiState

private class HelloViewModelState {
    fun toUiState(): HelloUiState = object: HelloUiState {}
}

class HelloViewModel: ViewModel() {
    private val viewModelState = MutableStateFlow(
        HelloViewModelState()
    )

    val uiState = viewModelState
                    .map(HelloViewModelState::toUiState)
                    .stateIn(
                        viewModelScope,
                        SharingStarted.Eagerly,
                        viewModelState.value.toUiState()
                    )

    init {
        viewModelScope.launch {  }
    }

    companion object {
        fun factory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HelloViewModel() as T
            }
        }
    }
}