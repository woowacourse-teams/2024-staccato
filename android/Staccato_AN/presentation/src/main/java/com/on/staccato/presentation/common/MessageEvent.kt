package com.on.staccato.presentation.common

import androidx.annotation.StringRes
import com.on.staccato.domain.ExceptionType
import com.on.staccato.toMessageId

sealed interface MessageEvent {
    data class ResId(
        @StringRes val value: Int,
    ) : MessageEvent

    data class Text(val value: String) : MessageEvent

    companion object {
        fun from(message: String) = Text(message)

        fun from(exceptionType: ExceptionType) = ResId(exceptionType.toMessageId())
    }
}
