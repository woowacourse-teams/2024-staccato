package com.woowacourse.staccato.presentation.visitcreation.model

data class VisitCreationUiModel(
    val pinId: Long,
    val placeName: String,
    val address: String,
    val travels: List<VisitTravelUiModel>,
)
