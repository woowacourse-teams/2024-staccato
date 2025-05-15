package com.on.staccato.presentation.timeline.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.on.staccato.R
import com.on.staccato.theme.Body4
import com.on.staccato.theme.Gray3
import java.time.LocalDate

@Composable
fun CategoryPeriod(
    modifier: Modifier = Modifier,
    startAt: LocalDate? = null,
    endAt: LocalDate? = null,
) {
    // TODO: 년도에 따라 형식 수정
    val period =
        if (startAt != null && endAt != null) {
            stringResource(
                R.string.category_period_dot,
                startAt.year,
                startAt.monthValue,
                startAt.dayOfMonth,
                endAt.year,
                endAt.monthValue,
                endAt.dayOfMonth,
            )
        } else {
            null
        }

    if (period != null) {
        Text(
            modifier = modifier,
            text = period,
            color = Gray3,
            style = Body4,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CategoryPeriodPreview(
    @PreviewParameter(CategoryPeriodPreviewParameterProvider::class)
    period: Pair<LocalDate?, LocalDate?>,
) {
    CategoryPeriod(
        startAt = period.first,
        endAt = period.second,
    )
}

private class CategoryPeriodPreviewParameterProvider : PreviewParameterProvider<Pair<LocalDate?, LocalDate?>> {
    override val values: Sequence<Pair<LocalDate?, LocalDate?>> =
        sequenceOf(
            LocalDate.of(2025, 4, 12) to LocalDate.of(2025, 10, 20),
            null to null,
        )
}
