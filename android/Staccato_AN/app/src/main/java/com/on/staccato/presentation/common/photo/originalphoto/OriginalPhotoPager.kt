package com.on.staccato.presentation.common.photo.originalphoto

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import com.on.staccato.R
import com.on.staccato.presentation.component.DEFAULT_MIN_ZOOM_SCALE
import com.on.staccato.presentation.component.DefaultAsyncImage
import com.on.staccato.presentation.component.PinchToZoomView
import com.on.staccato.theme.Black
import kotlin.math.absoluteValue

private const val ZOOM_SCROLLABLE_TOLERANCE = 0.05f

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
    var scrollable by remember { mutableStateOf(true) }

    HorizontalPager(
        state = pagerState,
        modifier =
        modifier
            .fillMaxSize()
            .background(Black),
        userScrollEnabled = scrollable,
    ) { page ->
        PinchToZoomView(
            onScaleChange = { scale ->
                scrollable = (scale - DEFAULT_MIN_ZOOM_SCALE).absoluteValue < ZOOM_SCROLLABLE_TOLERANCE
            },
            onDrag = { !scrollable },
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
        imageUrls = listOf("", "", ""),
        modifier = Modifier.fillMaxSize(),
        onTap = {},
    )
}
