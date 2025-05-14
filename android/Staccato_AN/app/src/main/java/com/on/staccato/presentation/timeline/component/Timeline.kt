package com.on.staccato.presentation.timeline.component

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.staccato.presentation.timeline.model.TimelineUiModel
import com.on.staccato.presentation.timeline.model.dummyTimelineUiModel
import com.on.staccato.theme.Gray1

@Composable
fun Timeline(
    timeline: List<TimelineUiModel>,
    onCategoryClicked: (Long) -> Unit,
) {
    LazyColumn {
        items(timeline) { timelineCategory ->
            TimelineItem(
                timeline = timelineCategory,
                onCategoryClicked = onCategoryClicked,
            )
            HorizontalDivider(thickness = 1.dp, color = Gray1)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun TimelinePreview() {
    Timeline(
        timeline = dummyTimelineUiModel,
        onCategoryClicked = {},
    )
}
