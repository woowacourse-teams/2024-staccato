package com.woowacourse.staccato.presentation.visitcreation.model

import java.time.LocalDate

data class VisitTravelUiModel(
    val id: Long,
    val title: String,
    val startAt: LocalDate,
    val endAt: LocalDate,
) {
    fun buildDatesInRange(): List<LocalDate> {
        return generateSequence(startAt) { it.plusDays(1) }
            .takeWhile { !it.isAfter(endAt) }
            .toList()
    }
}
