package com.on.staccato.presentation.category.model

import com.on.staccato.presentation.common.MemberUiModel
import java.time.LocalDate

data class MemoryUiModel(
    val id: Long,
    val title: String,
    val categoryThumbnailUrl: String? = null,
    val startAt: LocalDate? = null,
    val endAt: LocalDate? = null,
    val description: String? = null,
    val mates: List<MemberUiModel>,
    val staccatos: List<CategoryStaccatoUiModel>,
) {
    companion object {
        fun buildDatesInRange(
            startAt: LocalDate?,
            endAt: LocalDate?,
        ): List<LocalDate> {
            return generateSequence(startAt) { it.plusDays(1) }
                .takeWhile { !it.isAfter(endAt) }
                .toList()
        }
    }
}
