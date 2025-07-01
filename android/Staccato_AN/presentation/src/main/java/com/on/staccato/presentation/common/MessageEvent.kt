package com.on.staccato.presentation.common

import androidx.annotation.StringRes
import com.on.staccato.domain.ExceptionType
import com.on.staccato.toMessageId

sealed interface MessageEvent {
    data class FromResource(
        @StringRes val messageId: Int,
    ) : MessageEvent

    data class Plain(val message: String) : MessageEvent

    companion object {
        fun from(message: String) = Plain(message)

        fun from(exceptionType: ExceptionType) = FromResource(exceptionType.toMessageId())
    }
}
