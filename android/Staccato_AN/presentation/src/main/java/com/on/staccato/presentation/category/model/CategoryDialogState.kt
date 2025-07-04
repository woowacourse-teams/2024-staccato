package com.on.staccato.presentation.category.model

sealed interface CategoryDialogState {
    data object None : CategoryDialogState

    data class Exit(val onConfirm: () -> Unit) : CategoryDialogState
}
