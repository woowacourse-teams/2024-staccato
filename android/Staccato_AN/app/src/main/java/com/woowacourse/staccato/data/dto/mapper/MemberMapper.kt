package com.woowacourse.staccato.data.dto.mapper

import com.woowacourse.staccato.data.dto.member.MemberDto
import com.woowacourse.staccato.data.dto.member.MembersDto
import com.woowacourse.staccato.domain.model.Member

fun MemberDto.toDomain() =
    Member(
        memberId = memberId,
        nickName = nickName,
        memberImage = memberImage,
    )

fun MembersDto.toDomain(): List<Member> = members.map { it.toDomain() }
