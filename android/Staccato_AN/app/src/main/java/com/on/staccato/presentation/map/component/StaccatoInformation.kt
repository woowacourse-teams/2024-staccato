package com.on.staccato.presentation.map.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.staccato.presentation.common.toYearMonthDay
import com.on.staccato.presentation.map.model.StaccatoMarkerUiModel
import com.on.staccato.presentation.map.model.dummyStaccatoMarkerUiModel
import com.on.staccato.theme.Title3

@Composable
fun StaccatoInformation(
    modifier: Modifier = Modifier,
    staccato: StaccatoMarkerUiModel,
) {
    Column(modifier = modifier) {
        Text(
            text = staccato.staccatoTitle,
            style = Title3,
        )
        Spacer(modifier = Modifier.size(4.dp))
        StaccatoVisitedAt(dateArgs = staccato.visitedAt.toYearMonthDay())
    }
}

@Preview(showBackground = true)
@Composable
private fun StaccatoTitleAndVisitedAtPreview() {
    StaccatoInformation(staccato = dummyStaccatoMarkerUiModel)
}
