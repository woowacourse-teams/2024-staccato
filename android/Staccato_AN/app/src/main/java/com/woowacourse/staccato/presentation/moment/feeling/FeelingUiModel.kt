package com.woowacourse.staccato.presentation.moment.feeling

data class FeelingUiModel(
    val feeling: String,
    val src: Int?,
    val isSelected: Boolean = false,
)
