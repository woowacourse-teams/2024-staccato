package com.woowacourse.staccato.presentation.travel.model

import com.woowacourse.staccato.presentation.MemberUiModel
import com.woowacourse.staccato.presentation.dummyMates
import java.time.LocalDate

data class TravelUiModel(
    val id: Long,
    val title: String,
    val thumbnail: String? = null,
    val startAt: LocalDate,
    val endAt: LocalDate,
    val description: String? = null,
    val mates: List<MemberUiModel>,
    val visits: List<TravelVisitUiModel>,
)

val dummyTravel: TravelUiModel =
    TravelUiModel(
        id = 1L,
        title = "제주도 여행",
        thumbnail = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSA8EwTvV8MvmnT5SHmZVbqaPVflGBSRsj-uA&s",
        startAt = LocalDate.of(2024, 6, 15),
        endAt = LocalDate.of(2024, 6, 17),
        description = "우테코 친구들과 제주도 여행!",
        mates = dummyMates,
        visits = dummyVisits,
    )
