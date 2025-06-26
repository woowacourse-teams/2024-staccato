package com.on.staccato.presentation.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.on.staccato.presentation.map.component.StaccatosDialog
import com.on.staccato.presentation.map.viewmodel.MapsViewModel

@Composable
fun StaccatosScreen(
    onStaccatoClicked: (staccatoId: Long) -> Unit,
    mapsViewModel: MapsViewModel = hiltViewModel(),
) {
    val isClusterMode by mapsViewModel.isClusterMode.collectAsState()
    val staccatos by mapsViewModel.clusterStaccatoMarkers.collectAsState()

    if (isClusterMode) {
        StaccatosDialog(
            staccatos = staccatos,
            onDismissRequest = {
                mapsViewModel.switchClusterMode(
                    isClusterMode = false,
                )
            },
            onStaccatoClicked = onStaccatoClicked,
        )
    }
}
