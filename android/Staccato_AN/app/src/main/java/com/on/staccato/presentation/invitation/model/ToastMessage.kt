package com.on.staccato.presentation.invitation.model

import androidx.annotation.StringRes

sealed interface ToastMessage {
    data class FromResource(
        @StringRes val messageId: Int,
    ) : ToastMessage

    data class Plain(val errorMessage: String) : ToastMessage
}
