package com.woowacourse.staccato.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MembersDto(
    @SerialName("members") val members: List<MemberDto>,
)
