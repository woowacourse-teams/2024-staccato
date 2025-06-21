package com.on.staccato.presentation.category.model

sealed interface CategoryState {
    data class Deleted(val success: Boolean) : CategoryState

    data object Exited : CategoryState

    data object Error : CategoryState
}
