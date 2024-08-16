package com.woowacourse.staccato.presentation.moment.model

import java.time.LocalDate

sealed class MomentDetailUiModel {
    data class MomentDefaultUiModel(
        val id: Long,
        val placeName: String,
        val momentImageUrls: List<String>,
        val address: String,
        val visitedAt: LocalDate,
    ) : MomentDetailUiModel()

    data class VisitLogUiModel(
        val id: Long = 0,
        val memberId: Long = 0,
        val nickname: String,
        val memberImageUrl: String,
        val content: String,
    ) : MomentDetailUiModel()
}
