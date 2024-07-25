package com.woowacourse.staccato.presentation.converter

import java.time.LocalDate
import java.time.format.DateTimeFormatter

const val FULL_DATE_FORMAT = "yyyy.MM.dd"
const val MONTH_DATE_FORMAT = "MM.dd"
const val DAY_DATE_FORMAT = "dd"

fun convertLocalDateToDatePeriodString(
    startAt: LocalDate,
    endAt: LocalDate,
): String {
    val fullFormatter = DateTimeFormatter.ofPattern(FULL_DATE_FORMAT)
    val monthFormatter = DateTimeFormatter.ofPattern(MONTH_DATE_FORMAT)
    val dayFormatter = DateTimeFormatter.ofPattern(DAY_DATE_FORMAT)

    return if (startAt.year != endAt.year) {
        "${startAt.format(fullFormatter)} - ${endAt.format(fullFormatter)}"
    } else if (startAt.monthValue != endAt.monthValue) {
        "${startAt.format(fullFormatter)} - ${endAt.format(monthFormatter)}"
    } else if (startAt.dayOfMonth != endAt.dayOfMonth) {
        "${startAt.format(fullFormatter)} - ${endAt.format(dayFormatter)}"
    } else {
        startAt.format(fullFormatter)
    }
}
