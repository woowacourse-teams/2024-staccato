package com.on.staccato.presentation.common.photo.originalphoto

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.on.staccato.presentation.staccato.viewmodel.StaccatoViewModel

@Composable
fun OriginalPhotoDialog(viewModel: StaccatoViewModel = hiltViewModel<StaccatoViewModel>()) {
    val originalPhotoIndex = viewModel.originalPhotoIndex.collectAsState()
    val imageUrls = viewModel.staccatoDetail.value?.staccatoImageUrls ?: emptyList()

    if (originalPhotoIndex.value.isAvailable) {
        Dialog(
            onDismissRequest = {
                viewModel.changeOriginalPhotoIndex(OriginalPhotoIndex.unavailable)
            },
            properties =
                DialogProperties(
                    usePlatformDefaultWidth = false,
                    dismissOnClickOutside = false,
                ),
        ) {
            Scaffold { paddingValues ->
                OriginalPhotoPager(
                    modifier = Modifier.padding(paddingValues),
                    imageUrls = imageUrls,
                    initialPage = originalPhotoIndex.value.initialPage,
                )
            }
        }
    }
}
