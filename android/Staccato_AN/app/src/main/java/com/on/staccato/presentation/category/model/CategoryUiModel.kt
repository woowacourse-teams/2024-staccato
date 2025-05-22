package com.on.staccato.presentation.category.model

import com.on.staccato.domain.model.Member
import java.time.LocalDate

data class CategoryUiModel(
    val id: Long,
    val title: String,
    val categoryThumbnailUrl: String? = null,
    val startAt: LocalDate? = null,
    val endAt: LocalDate? = null,
    val description: String? = null,
    val color: String,
    val mates: List<Member>,
    val staccatos: List<CategoryStaccatoUiModel>,
) {
    companion object {
        const val DEFAULT_CATEGORY_ID = 0L
    }
}
