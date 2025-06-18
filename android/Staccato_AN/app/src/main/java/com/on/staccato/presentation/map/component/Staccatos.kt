package com.on.staccato.presentation.map.component

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.on.staccato.presentation.component.DefaultDivider
import com.on.staccato.presentation.map.model.StaccatoLocationUiModel
import com.on.staccato.presentation.map.model.dummyStaccatoLocationUiModels

@Composable
fun Staccatos(
    staccatos: List<StaccatoLocationUiModel>,
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
fun StaccatoLocationsPreview() {
    Staccatos(
        staccatos = dummyStaccatoLocationUiModels,
        onStaccatoClicked = {},
    )
}
