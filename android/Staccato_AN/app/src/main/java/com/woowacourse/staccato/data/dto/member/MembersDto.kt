package com.woowacourse.staccato.data.dto.member

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MembersDto(
    @SerialName("members") val members: List<MemberDto>,
)
