package com.woowacourse.staccato.presentation.visit.model

import java.time.LocalDate

sealed class VisitDetailUiModel {
    data class VisitDefaultUiModel(
        val id: Long,
        val placeName: String,
        val visitImage: String,
        val address: String,
        val visitedAt: LocalDate,
        val visitedCount: Long,
    ) : VisitDetailUiModel()

    data class VisitLogUiModel(
        val id: Long = 0,
        val memberId: Long = 0,
        val nickName: String,
        val memberImage: String,
        val content: String,
    ) : VisitDetailUiModel()
}
