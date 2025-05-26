package com.on.staccato.presentation.timeline

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.hilt.navigation.compose.hiltViewModel
import com.on.staccato.presentation.main.viewmodel.SharedViewModel
import com.on.staccato.presentation.timeline.component.Timeline
import com.on.staccato.presentation.timeline.viewmodel.TimelineViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

private const val TIMELINE_TOP_INDEX = 0

@Composable
fun TimelineScreen(
    timelineViewModel: TimelineViewModel = hiltViewModel(),
    sharedViewModel: SharedViewModel,
    onCategoryClicked: (Long) -> Unit,
) {
    val timeline by timelineViewModel.timeline.collectAsState()
    val lazyListState = rememberLazyListState()

    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.firstVisibleItemIndex }
            .map { it == TIMELINE_TOP_INDEX }
            .distinctUntilChanged()
            .collect {
                sharedViewModel.updateIsDraggable(it)
            }
    }

    Timeline(
        timeline = timeline,
        onCategoryClicked = onCategoryClicked,
        lazyListState = lazyListState,
    )
}
