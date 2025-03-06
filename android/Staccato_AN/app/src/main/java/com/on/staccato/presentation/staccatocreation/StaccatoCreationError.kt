package com.on.staccato.presentation.staccatocreation

sealed interface StaccatoCreationError {
    data class CategoryCandidates(val message: String) : StaccatoCreationError

    data class StaccatoCreation(val message: String) : StaccatoCreationError
}
