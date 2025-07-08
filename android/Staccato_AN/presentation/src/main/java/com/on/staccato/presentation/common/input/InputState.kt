package com.on.staccato.presentation.common.input

sealed interface InputState {
    data object Empty : InputState

    data class Valid(val message: String = "") : InputState

    data class Invalid(val errorMessage: String) : InputState
}
