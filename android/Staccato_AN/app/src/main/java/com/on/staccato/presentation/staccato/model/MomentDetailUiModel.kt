package com.on.staccato.presentation.staccato.model

import java.time.LocalDateTime

sealed class MomentDetailUiModel {
    data class MomentDefaultUiModel(
        val id: Long,
        val placeName: String,
        val momentImageUrls: List<String>,
        val address: String,
        val visitedAt: LocalDateTime,
    ) : MomentDetailUiModel()

    data class CommentsUiModel(
        val id: Long = 0,
        val memberId: Long = 0,
        val nickname: String,
        val memberImageUrl: String,
        val content: String,
    ) : MomentDetailUiModel()
}
