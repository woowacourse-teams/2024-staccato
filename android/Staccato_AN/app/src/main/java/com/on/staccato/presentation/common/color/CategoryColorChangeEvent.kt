package com.on.staccato.presentation.common.color

data class CategoryColorChangeEvent(
    val categoryId: Long,
    val categoryColor: CategoryColor,
)
