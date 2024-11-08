package com.on.staccato.presentation.common

data class MemberUiModel(
    val id: Long,
    val nickname: String,
    val memberImage: String? = null,
)
