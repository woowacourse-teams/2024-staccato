package com.on.staccato.presentation.photo.originalphoto

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.on.staccato.presentation.staccato.viewmodel.StaccatoViewModel

@Composable
fun OriginalPhotoScreen(viewModel: StaccatoViewModel = hiltViewModel()) {
    val originalPhotoIndex by viewModel.originalPhotoIndex.collectAsStateWithLifecycle()
    val staccatoDetail = viewModel.staccatoDetail.value
    val imageUrls = staccatoDetail?.staccatoImageUrls ?: emptyList()
    val staccatoTitle = staccatoDetail?.staccatoTitle ?: ""
    var topBarVisibility by remember { mutableStateOf(true) }

    if (originalPhotoIndex.isAvailable) {
        OriginalPhotoDialog(
            imageUrls = imageUrls,
            originalPhotoIndex = originalPhotoIndex,
            topBarTitle = staccatoTitle,
            topBarVisibility = topBarVisibility,
            onDismiss = { viewModel.changeOriginalPhotoIndex(OriginalPhotoIndex.unavailable) },
            onTab = { topBarVisibility = !topBarVisibility },
        )
    }
}
