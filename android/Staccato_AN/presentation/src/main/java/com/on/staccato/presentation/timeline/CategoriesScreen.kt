package com.on.staccato.presentation.timeline

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.on.staccato.presentation.main.viewmodel.SharedViewModel
import com.on.staccato.presentation.timeline.component.Categories
import com.on.staccato.presentation.timeline.viewmodel.TimelineViewModel

@Composable
fun CategoriesScreen(
    timelineViewModel: TimelineViewModel = hiltViewModel(),
    sharedViewModel: SharedViewModel,
    onCategoryClicked: (Long) -> Unit,
) {
    val categories by timelineViewModel.timeline.collectAsState()

    Categories(
        categories = categories,
        onCategoryClicked = onCategoryClicked,
        onTopChanged = { sharedViewModel.updateIsAtTop(it) },
    )
}
