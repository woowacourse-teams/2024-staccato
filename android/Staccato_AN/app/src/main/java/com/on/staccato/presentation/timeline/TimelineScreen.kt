package com.on.staccato.presentation.timeline

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.on.staccato.presentation.main.viewmodel.SharedViewModel
import com.on.staccato.presentation.timeline.component.Timeline
import com.on.staccato.presentation.timeline.viewmodel.TimelineViewModel

@Composable
fun TimelineScreen(
    timelineViewModel: TimelineViewModel = hiltViewModel(),
    sharedViewModel: SharedViewModel,
    onCategoryClicked: (Long) -> Unit,
) {
    val timeline by timelineViewModel.timeline.collectAsState()

    Timeline(
        timeline = timeline,
        onCategoryClicked = onCategoryClicked,
        onDraggableChanged = { sharedViewModel.updateIsDraggable(it) },
    )
}
