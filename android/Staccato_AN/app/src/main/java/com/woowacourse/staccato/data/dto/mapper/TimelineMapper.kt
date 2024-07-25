package com.woowacourse.staccato.data.dto.mapper

import com.woowacourse.staccato.data.dto.MemberDto
import com.woowacourse.staccato.data.dto.MembersDto
import com.woowacourse.staccato.data.dto.timeline.TimelineResponse
import com.woowacourse.staccato.data.dto.timeline.TimelineTravelDto
import com.woowacourse.staccato.domain.model.Member
import com.woowacourse.staccato.domain.model.Timeline
import com.woowacourse.staccato.domain.model.Travel
import com.woowacourse.staccato.toLocalDate

fun TimelineResponse.toDomain(): Timeline {
    val travels =
        travels.map { timelineTravelDto ->
            timelineTravelDto.toDomain()
        }
    return Timeline(travels)
}

fun TimelineTravelDto.toDomain(): Travel {
    return Travel(
        travelId = travelId,
        travelThumbnail = travelThumbnail,
        travelTitle = travelTitle,
        startAt = startAt.toLocalDate(),
        endAt = endAt.toLocalDate(),
        description = description,
        mates = mates.toDomain(),
        visits = emptyList(),
    )
}

fun MembersDto.toDomain(): List<Member> {
    return members.map { it.toDomain() }
}

fun MemberDto.toDomain(): Member {
    return Member(
        memberId = memberId,
        nickName = nickName,
        memberImage = memberImage,
    )
}
