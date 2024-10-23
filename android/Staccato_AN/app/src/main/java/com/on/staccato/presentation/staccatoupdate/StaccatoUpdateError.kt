package com.on.staccato.presentation.staccatoupdate

sealed interface StaccatoUpdateError {
    data class MemoryCandidates(val message: String) : StaccatoUpdateError

    data class StaccatoInitialize(val message: String) : StaccatoUpdateError

    data class StaccatoUpdate(val message: String) : StaccatoUpdateError
}
