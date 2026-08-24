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

    // 확대된 상태에서는 스와이프가 막혀 한 번에 한 페이지만 다뤄지므로, 페이지 간 확대 상태를 공유해도 안전하다.
    // 페이지를 넘기면 이전 사진의 확대를 초기화해, 각 사진을 원본 크기에서 시작한다.
    LaunchedEffect(pagerState.currentPage) { zoomState.reset() }

    HorizontalPager(
        state = pagerState,
        modifier =
            modifier
                .fillMaxSize()
                .background(Black),
        // 확대 상태에서는 페이지 스와이프를 막아, 드래그가 이미지 이동(팬)으로만 쓰이게 한다.
        userScrollEnabled = !zoomState.isZoomedIn,
    ) { page ->
        PinchZoom(
            // 확대 상태의 드래그는 PinchZoom이 기본으로 소비하므로(부모 Pager로 미전파) 별도 지정이 필요 없다.
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
