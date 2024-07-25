package com.woowacourse.staccato.presentation.visit.model

sealed class VisitDetailUiModel {
    data class VisitDefaultUiModel(
        val visitId: Long,
        val placeName: String,
        val visitImage: String,
        val address: String,
        val visitedAt: String,
        val visitedCount: Long,
    ) : VisitDetailUiModel()

    data class VisitLogUiModel(
        val visitLogId: Long = 0,
        val memberId: Long = 0,
        val nickName: String,
        val memberImage: String,
        val content: String,
    ) : VisitDetailUiModel()
}
