package com.on.staccato.data.dto.member

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemberSearchResponse(
    @SerialName("members") val members: List<MemberDto>,
)
