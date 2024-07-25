package com.woowacourse.staccato.presentation.visitcreation.model

import java.time.LocalDate

data class VisitCreationUiModel(
    val pinId: Long,
    val placeName: String,
    val address: String,
    val visitedImages: List<String>,
    val travels: List<TravelUiModel>,
)

data class TravelUiModel(
    val travelId: Long,
    val travelTitle: String,
    val startAt: String,
    val endAt: String,
) {
    fun buildLocalDatesInRange(): List<String> {
        val startDateNumbers = startAt.split("-").map { it.toInt() }
        val endDateNumbers = endAt.split("-").map { it.toInt() }
        val startDate = LocalDate.of(startDateNumbers[0], startDateNumbers[1], startDateNumbers[2])
        val endDate = LocalDate.of(endDateNumbers[0], endDateNumbers[1], endDateNumbers[2])
        return createDateList(startDate, endDate).map { it.toString() }
    }

    private fun createDateList(
        startDate: LocalDate,
        endDate: LocalDate,
    ): List<LocalDate> {
        return generateSequence(startDate) { it.plusDays(1) }
            .takeWhile { !it.isAfter(endDate) }
            .toList()
    }
}
