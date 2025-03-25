package com.on.staccato.domain.model

sealed interface NicknameState {
    data object Empty : NicknameState

    data object BlankFirst : NicknameState

    data object InvalidFormat : NicknameState

    data class InvalidLength(
        val min: Int,
        val max: Int,
    ) : NicknameState

    data class Valid(val nickname: String) : NicknameState
}
