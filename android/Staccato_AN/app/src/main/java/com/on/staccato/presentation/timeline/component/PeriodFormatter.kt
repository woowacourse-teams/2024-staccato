package com.on.staccato.presentation.timeline.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.on.staccato.R
import com.on.staccato.presentation.common.toMonthDay
import com.on.staccato.presentation.common.toYearMonthDay
import java.time.LocalDate

@Composable
fun formatPeriod(
    startAt: LocalDate?,
    endAt: LocalDate?,
): String? {
    if (startAt == null || endAt == null) return null

    return if (startAt.year == endAt.year) {
        stringResource(
            R.string.all_period_dot_same_year,
            *startAt.toYearMonthDay(),
            *endAt.toMonthDay(),
        )
    } else {
        stringResource(
            R.string.all_period_dot_different_year,
            *startAt.toYearMonthDay(),
            *endAt.toYearMonthDay(),
        )
    }
}
