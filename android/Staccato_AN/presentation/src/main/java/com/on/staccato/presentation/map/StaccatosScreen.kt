package com.on.staccato.presentation.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.on.staccato.presentation.map.component.StaccatosDialog
import com.on.staccato.presentation.map.viewmodel.MapsViewModel

@Composable
fun StaccatosScreen(
    onStaccatoClick: (staccatoId: Long) -> Unit,
    mapsViewModel: MapsViewModel = hiltViewModel(),
) {
    val isClusterMode by mapsViewModel.isClusterMode.collectAsStateWithLifecycle()
    val staccatos by mapsViewModel.clusterStaccatoMarkers.collectAsStateWithLifecycle()

    if (isClusterMode) {
        StaccatosDialog(
            staccatos = staccatos,
            onDismissRequest = {
                mapsViewModel.switchClusterMode(
                    isClusterMode = false,
                )
            },
            onStaccatoClick = onStaccatoClick,
        )
    }
}
