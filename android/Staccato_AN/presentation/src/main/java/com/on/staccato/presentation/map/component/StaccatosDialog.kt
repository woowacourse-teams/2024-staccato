package com.on.staccato.presentation.map.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.on.staccato.presentation.map.model.StaccatoMarkerUiModel
import com.on.staccato.presentation.map.model.dummyStaccatoMarkerUiModels
import com.on.staccato.theme.White

@Composable
fun StaccatosDialog(
    staccatos: List<StaccatoMarkerUiModel>,
    onDismissRequest: () -> Unit,
    onStaccatoClick: (Long) -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties =
            DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnClickOutside = false,
            ),
    ) {
        Surface(
            modifier =
                Modifier
                    .fillMaxWidth(0.9f)
                    .height(500.dp),
            shape = RoundedCornerShape(10.dp),
            color = White,
            tonalElevation = 10.dp,
        ) {
            Column {
                StaccatosTopBar(onDismissRequest = onDismissRequest)
                Staccatos(staccatos = staccatos, onStaccatoClicked = onStaccatoClick)
            }
        }
    }
}

@Preview
@Composable
private fun StaccatosDialogPreview() {
    StaccatosDialog(
        staccatos = dummyStaccatoMarkerUiModels,
        onDismissRequest = {},
        onStaccatoClick = {},
    )
}
