package com.woowacourse.staccato.presentation.visit.model

import java.time.LocalDate

sealed class VisitDetailUiModel {
    data class VisitDefaultUiModel(
        val id: Long,
        val placeName: String,
        val visitImageUrls: String,
        val address: String,
        val visitedAt: LocalDate,
    ) : VisitDetailUiModel()

    data class VisitLogUiModel(
        val id: Long = 0,
        val memberId: Long = 0,
        val nickname: String,
        val memberImageUrl: String,
        val content: String,
    ) : VisitDetailUiModel()
}
