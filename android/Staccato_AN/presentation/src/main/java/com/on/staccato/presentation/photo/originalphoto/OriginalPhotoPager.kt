package com.on.staccato.presentation.photo.originalphoto

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import com.on.staccato.presentation.R
import com.on.staccato.presentation.component.DefaultAsyncImage
import com.on.staccato.presentation.component.PinchZoom
import com.on.staccato.presentation.component.rememberPinchZoomState
import com.on.staccato.theme.Black

@Composable
fun OriginalPhotoPager(
    imageUrls: List<String>,
    modifier: Modifier = Modifier,
    onTap: () -> Unit,
    initialPage: Int = 0,
) {
    val pagerState =
        rememberPagerState(
            initialPage = initialPage,
            pageCount = { imageUrls.size },
        )
    val zoomState = rememberPinchZoomState()

    LaunchedEffect(pagerState.currentPage) { zoomState.reset() }

    HorizontalPager(
        state = pagerState,
        modifier =
            modifier
                .fillMaxSize()
                .background(Black),
        userScrollEnabled = !zoomState.isZoomedIn,
    ) { page ->
        PinchZoom(
            state = zoomState,
            onTap = { onTap() },
        ) {
            DefaultAsyncImage(
                modifier = Modifier.fillMaxSize(),
                url = imageUrls[page],
                contentDescription = R.string.all_original_photo,
                contentScale = ContentScale.Fit,
            )
        }
    }
}

@Composable
@Preview
fun OriginalPhotoPagerPreview() {
    OriginalPhotoPager(
        imageUrls = dummyImageUrls,
        modifier = Modifier.fillMaxSize(),
        onTap = {},
    )
}
