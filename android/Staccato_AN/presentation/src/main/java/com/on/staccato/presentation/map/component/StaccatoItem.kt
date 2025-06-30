package com.on.staccato.presentation.map.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.on.staccato.presentation.R
import com.on.staccato.presentation.component.clickableWithoutRipple
import com.on.staccato.presentation.map.model.StaccatoMarkerUiModel
import com.on.staccato.presentation.map.model.dummyStaccatoMarkerUiModels

@Composable
fun StaccatoItem(
    modifier: Modifier = Modifier,
    staccato: StaccatoMarkerUiModel,
    onStaccatoClicked: (Long) -> Unit,
) {
    Row(
        modifier =
            modifier
                .padding(start = 20.dp, end = 20.dp, top = 15.dp, bottom = 18.dp)
                .fillMaxWidth()
                .clickableWithoutRipple { onStaccatoClicked(staccato.staccatoId) },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StaccatoInformation(
            staccato = staccato,
            modifier =
                Modifier.weight(1f)
                    .padding(end = 12.dp),
        )
        Image(
            modifier = Modifier.size(34.dp),
            painter = painterResource(staccato.color.markerRes),
            contentDescription = stringResource(id = R.string.category_creation_color),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
private fun StaccatoItemPreview(
    @PreviewParameter(StaccatoItemPreviewParameterProvider::class)
    staccato: StaccatoMarkerUiModel,
) {
    StaccatoItem(
        onStaccatoClicked = {},
        staccato = staccato,
    )
}

private class StaccatoItemPreviewParameterProvider(
    override val values: Sequence<StaccatoMarkerUiModel> = dummyStaccatoMarkerUiModels.asSequence(),
) : PreviewParameterProvider<StaccatoMarkerUiModel>
