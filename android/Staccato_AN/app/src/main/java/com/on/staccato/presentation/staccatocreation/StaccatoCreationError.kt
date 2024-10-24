package com.on.staccato.presentation.staccatocreation

sealed interface StaccatoCreationError {
    data class MemoryCandidates(val message: String) : StaccatoCreationError

    data class StaccatoCreation(val message: String) : StaccatoCreationError
}
