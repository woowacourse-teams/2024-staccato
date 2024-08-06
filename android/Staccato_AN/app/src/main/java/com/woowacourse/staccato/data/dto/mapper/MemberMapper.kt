package com.woowacourse.staccato.data.dto.mapper

import com.woowacourse.staccato.data.dto.member.MemberDto
import com.woowacourse.staccato.domain.model.Member

fun MemberDto.toDomain() =
    Member(
        memberId = memberId,
        nickname = nickname,
        memberImage = memberImage,
    )
