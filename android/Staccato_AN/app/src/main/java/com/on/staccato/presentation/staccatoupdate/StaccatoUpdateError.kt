package com.on.staccato.presentation.staccatoupdate

sealed interface StaccatoUpdateError {
    data class CategoryCandidates(val messageId: Int) : StaccatoUpdateError

    data class StaccatoInitialize(val messageId: Int) : StaccatoUpdateError

    data class StaccatoUpdate(val messageId: Int) : StaccatoUpdateError
}
