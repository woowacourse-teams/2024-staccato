package com.on.staccato.presentation.map.component

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.on.staccato.presentation.component.DefaultDivider
import com.on.staccato.presentation.map.model.StaccatoMarkerUiModel
import com.on.staccato.presentation.map.model.dummyStaccatoMarkerUiModels

@Composable
fun Staccatos(
    staccatos: List<StaccatoMarkerUiModel>,
    onStaccatoClicked: (Long) -> Unit,
) {
    LazyColumn {
        items(
            items = staccatos,
            key = { it.staccatoId },
        ) { staccato ->
            StaccatoItem(
                staccato = staccato,
                onStaccatoClicked = onStaccatoClicked,
            )
            DefaultDivider()
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
fun StaccatosPreview() {
    Staccatos(
        staccatos = dummyStaccatoMarkerUiModels,
        onStaccatoClicked = {},
    )
}
