package com.on.staccato.data.dto.mapper

import com.on.staccato.data.dto.member.MemberDto
import com.on.staccato.domain.model.Member

fun MemberDto.toDomain() =
    Member(
        memberId = memberId,
        nickname = nickname,
        memberImage = memberImage,
    )
