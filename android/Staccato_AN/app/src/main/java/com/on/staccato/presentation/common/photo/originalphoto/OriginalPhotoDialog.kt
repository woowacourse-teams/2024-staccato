package com.on.staccato.presentation.common.photo.originalphoto

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.on.staccato.presentation.staccato.viewmodel.StaccatoViewModel

@Composable
fun OriginalPhotoDialog(viewModel: StaccatoViewModel = hiltViewModel<StaccatoViewModel>()) {
    val originalPhotoIndex = viewModel.originalPhotoIndex.collectAsState()
    val imageUrls = viewModel.staccatoDetail.value?.staccatoImageUrls ?: emptyList()
    var topBarVisibility by remember { mutableStateOf(true) }

    fun dismiss() {
        viewModel.changeOriginalPhotoIndex(OriginalPhotoIndex.unavailable)
    }

    if (originalPhotoIndex.value.isAvailable) {
        Dialog(
            onDismissRequest = { dismiss() },
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
                    onTap = { topBarVisibility = !topBarVisibility },
                )
                OriginalPhotoTopBar(
                    isVisible = topBarVisibility,
                    title = viewModel.staccatoDetail.value?.staccatoTitle,
                    onNavigationClick = { dismiss() },
                )
            }
        }
    }
}
