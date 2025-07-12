package com.on.staccato.presentation.timeline.component

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.tooling.preview.Preview
import com.on.staccato.presentation.component.DefaultDivider
import com.on.staccato.presentation.timeline.model.TimelineUiModel
import com.on.staccato.presentation.timeline.model.dummyTimelineUiModels
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

private const val CATEGORIES_TOP_SCROLL_OFFSET = 0

@Composable
fun Categories(
    categories: List<TimelineUiModel>,
    onCategoryClicked: (Long) -> Unit,
    onTopChanged: (Boolean) -> Unit,
) {
    val lazyListState = rememberLazyListState()
    val previousHashCode = rememberSaveable { mutableIntStateOf(categories.hashCode()) }

    LaunchedEffect(categories.hashCode()) {
        if (categories.hashCode() != previousHashCode.intValue) {
            lazyListState.animateScrollToItem(0)
            previousHashCode.intValue = categories.hashCode()
        }
    }

    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.firstVisibleItemScrollOffset }
            .map { it == CATEGORIES_TOP_SCROLL_OFFSET }
            .distinctUntilChanged()
            .collect {
                onTopChanged(it)
            }
    }

    LazyColumn(
        state = lazyListState,
    ) {
        items(
            items = categories,
            key = { it.categoryId },
        ) { category ->
            CategoryItem(
                category = category,
                onCategoryClicked = onCategoryClicked,
            )
            DefaultDivider()
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun CategoriesPreview() {
    Categories(
        categories = dummyTimelineUiModels,
        onCategoryClicked = {},
        onTopChanged = {},
    )
}
