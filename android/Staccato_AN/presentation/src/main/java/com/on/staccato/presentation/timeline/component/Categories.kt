package com.on.staccato.presentation.timeline.component

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import com.on.staccato.presentation.component.DefaultDivider
import com.on.staccato.presentation.timeline.model.CategoryUiModel
import com.on.staccato.presentation.timeline.model.CategoryViewType
import com.on.staccato.presentation.timeline.model.dummyCategoryUiModels
import com.on.staccato.presentation.timeline.model.viewType
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

private const val CATEGORIES_TOP_SCROLL_OFFSET = 0

@Composable
fun Categories(
    modifier: Modifier = Modifier,
    categories: List<CategoryUiModel>,
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
        modifier = modifier.testTag("categories_lazy_column"),
        state = lazyListState,
    ) {
        items(
            items = categories,
            key = { it.categoryId },
            contentType = { it.viewType },
        ) { category ->
            when (category.viewType) {
                CategoryViewType.CommonWithPeriod -> {
                    CommonCategoryWithPeriod(category, onCategoryClicked)
                }
                CategoryViewType.CommonWithoutPeriod -> {
                    CommonCategoryWithoutPeriod(category, onCategoryClicked)
                }
                CategoryViewType.PersonalWithPeriod -> {
                    PersonalCategoryWithPeriod(category, onCategoryClicked)
                }
                CategoryViewType.PersonalWithoutPeriod -> {
                    PersonalCategoryWithoutPeriod(category, onCategoryClicked)
                }
            }
            DefaultDivider()
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun CategoriesPreview() {
    Categories(
        categories = dummyCategoryUiModels,
        onCategoryClicked = {},
        onTopChanged = {},
    )
}
