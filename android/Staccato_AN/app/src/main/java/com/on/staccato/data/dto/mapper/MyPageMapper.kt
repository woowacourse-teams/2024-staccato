package com.on.staccato.data.dto.mapper

import com.on.staccato.data.dto.mypage.MemberProfileResponse
import com.on.staccato.domain.model.MemberProfile

// TODO: 사용자 프로필 조회 API V2 머지 되면 수정
fun MemberProfileResponse.toDomain() =
    MemberProfile(
        memberId = 54L,
        profileImageUrl = profileImageUrl,
        nickname = nickname,
        uuidCode = code,
    )
