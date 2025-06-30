package com.on.staccato.presentation.common

import androidx.annotation.StringRes

sealed interface MessageEvent {
    data class FromResource(
        @StringRes val messageId: Int,
    ) : MessageEvent

    data class Plain(val message: String) : MessageEvent
}