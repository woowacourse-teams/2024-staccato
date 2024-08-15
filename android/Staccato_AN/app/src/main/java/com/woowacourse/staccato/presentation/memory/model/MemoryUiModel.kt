package com.woowacourse.staccato.presentation.memory.model

import com.woowacourse.staccato.presentation.common.MemberUiModel
import com.woowacourse.staccato.presentation.common.dummyMates
import java.time.LocalDate

data class MemoryUiModel(
    val id: Long,
    val title: String,
    val memoryThumbnailUrl: String? = null,
    val startAt: LocalDate,
    val endAt: LocalDate,
    val description: String? = null,
    val mates: List<MemberUiModel>,
    val visits: List<MemoryVisitUiModel>,
)

val dummyTravel: MemoryUiModel =
    MemoryUiModel(
        id = 1L,
        title = "제주도 여행",
        memoryThumbnailUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSA8EwTvV8MvmnT5SHmZVbqaPVflGBSRsj-uA&s",
        startAt = LocalDate.of(2024, 6, 15),
        endAt = LocalDate.of(2024, 6, 17),
        description = "우테코 친구들과 제주도 여행!",
        mates = dummyMates,
        visits = dummyVisits,
    )
