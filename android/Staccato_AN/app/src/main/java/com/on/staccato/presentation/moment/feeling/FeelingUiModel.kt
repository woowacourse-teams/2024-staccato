package com.on.staccato.presentation.moment.feeling

data class FeelingUiModel(
    val feeling: String,
    val colorSrc: Int?,
    val graySrc: Int?,
    val isSelected: Boolean = false,
)
