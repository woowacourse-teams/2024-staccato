package com.on.staccato.presentation.common.photo.originalphoto

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun OriginalPhotoDialog(
    staccatoTitle: String,
    imageUrls: List<String>,
    originalPhotoIndex: OriginalPhotoIndex,
    topBarVisibility: Boolean,
    onDismiss: () -> Unit,
    onTab: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
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
                initialPage = originalPhotoIndex.initialPage,
                onTap = onTab,
            )
            OriginalPhotoTopBar(
                isVisible = topBarVisibility,
                title = staccatoTitle,
                onNavigationClick = onDismiss,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun OriginalPhotoDialogPreview() {
    var topBarVisibility by remember { mutableStateOf(true) }

    OriginalPhotoDialog(
        staccatoTitle = "스타카토 원본 사진 프리뷰",
        imageUrls = dummyImageUrls,
        originalPhotoIndex = OriginalPhotoIndex(0),
        topBarVisibility = topBarVisibility,
        onDismiss = {},
        onTab = { topBarVisibility = !topBarVisibility },
    )
}

val dummyImageUrls =
    listOf(
        "https://avatars.githubusercontent.com/u/46596035?v=4",
        "https://avatars.githubusercontent.com/u/103019852?v=4",
        "https://avatars.githubusercontent.com/u/92203597?v=4",
    )
