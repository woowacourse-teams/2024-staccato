package com.on.staccato.presentation.timeline.component

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.on.staccato.presentation.component.DefaultDivider
import com.on.staccato.presentation.timeline.model.TimelineUiModel
import com.on.staccato.presentation.timeline.model.dummyTimelineUiModels
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@Composable
fun Timeline(
    timeline: List<TimelineUiModel>,
    onCategoryClicked: (Long) -> Unit,
    updateIsDraggable: (Boolean) -> Unit,
) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .map { it == 0 }
            .distinctUntilChanged()
            .collect {
                updateIsDraggable(it)
            }
    }

    LazyColumn(state = listState) {
        items(timeline, key = { it.categoryId }) { timelineCategory ->
            TimelineItem(
                modifier = Modifier.animateItem(),
                category = timelineCategory,
                onCategoryClicked = onCategoryClicked,
            )
            DefaultDivider()
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun TimelinePreview() {
    Timeline(
        timeline = dummyTimelineUiModels,
        onCategoryClicked = {},
        updateIsDraggable = {},
    )
}
