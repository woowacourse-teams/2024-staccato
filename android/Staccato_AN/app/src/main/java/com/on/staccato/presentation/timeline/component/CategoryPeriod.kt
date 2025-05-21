package com.on.staccato.presentation.timeline.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.on.staccato.theme.Body4
import com.on.staccato.theme.Gray3
import java.time.LocalDate

@Composable
fun CategoryPeriod(
    modifier: Modifier = Modifier,
    startAt: LocalDate? = null,
    endAt: LocalDate? = null,
) {
    val period = formatPeriod(startAt, endAt)

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
            LocalDate.of(2025, 4, 12) to LocalDate.of(2026, 10, 20),
            null to null,
        )
}
