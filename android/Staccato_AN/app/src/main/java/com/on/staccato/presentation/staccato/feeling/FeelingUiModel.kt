package com.on.staccato.presentation.staccato.feeling

import androidx.annotation.ColorRes

data class FeelingUiModel(
    val feeling: String,
    @ColorRes val colorSrc: Int?,
    @ColorRes val graySrc: Int?,
    val isSelected: Boolean = false,
)
