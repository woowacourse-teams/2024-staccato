package com.on.staccato.presentation.timeline

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.on.staccato.presentation.timeline.compose.Timeline
import com.on.staccato.presentation.timeline.viewmodel.TimelineViewModel

@Composable
fun TimelineScreen(
    viewModel: TimelineViewModel = hiltViewModel(),
    onCategoryClicked: (Long) -> Unit,
) {
    val timeline by viewModel.timeline.collectAsState()
    Timeline(
        timeline = timeline,
        onCategoryClicked = onCategoryClicked,
    )
}
