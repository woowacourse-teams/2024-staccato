package com.on.staccato.presentation.staccatocreation

import androidx.annotation.StringRes

sealed interface StaccatoCreationError {
    data class CategoryCandidates(
        @StringRes val messageId: Int,
    ) : StaccatoCreationError

    data class StaccatoCreation(
        @StringRes val messageId: Int,
    ) : StaccatoCreationError
}
