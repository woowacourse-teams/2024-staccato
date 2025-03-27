package com.on.staccato.data.dto.mapper

import com.on.staccato.data.dto.mypage.MemberProfileResponse
import com.on.staccato.domain.model.MemberProfile

fun MemberProfileResponse.toDomain() =
    MemberProfile(
        profileImageUrl = profileImageUrl,
        nickname = nickname,
        uuidCode = code,
    )
