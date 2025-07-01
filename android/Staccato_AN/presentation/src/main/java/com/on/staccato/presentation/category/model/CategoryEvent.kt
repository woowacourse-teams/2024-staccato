package com.on.staccato.presentation.category.model

sealed interface CategoryEvent {
    data class Deleted(val success: Boolean) : CategoryEvent

    data object Exited : CategoryEvent

    data object Error : CategoryEvent

    data class InviteSuccess(val count: Int) : CategoryEvent
}
