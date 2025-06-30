package com.on.staccato.presentation.common

import androidx.annotation.StringRes
import com.on.staccato.domain.ExceptionType
import com.on.staccato.toMessageId

sealed interface MessageEvent {
    data class FromResource(
        @StringRes val messageId: Int,
    ) : MessageEvent

    data class Plain(val message: String) : MessageEvent
}

fun <T> convertMessageEvent(message: T?): MessageEvent =
    when (message) {
        is String -> MessageEvent.Plain(message)
        is ExceptionType -> MessageEvent.FromResource(message.toMessageId())
        else -> MessageEvent.FromResource(ExceptionType.UNKNOWN.toMessageId())
    }
