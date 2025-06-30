package com.on.staccato.domain.model

import com.on.staccato.domain.model.Nickname.Companion.MAX_LENGTH
import com.on.staccato.domain.model.Nickname.Companion.MIN_LENGTH

sealed interface NicknameState {
    data object Empty : NicknameState

    data object BlankFirst : NicknameState

    data object InvalidFormat : NicknameState

    data class InvalidLength(
        val min: Int,
        val max: Int,
    ) : NicknameState

    data class Valid(val value: String) : NicknameState

    companion object {
        fun from(value: String): NicknameState {
            val nickname = Nickname(value)
            return when {
                nickname.isEmpty() -> Empty

                nickname.isBlankFirst() -> BlankFirst

                !nickname.isValidFormat() -> InvalidFormat

                !nickname.isValidLength() -> InvalidLength(MIN_LENGTH, MAX_LENGTH)

                else -> Valid(nickname.value)
            }
        }
    }
}
