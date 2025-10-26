package com.on.staccato.presentation.timeline.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.on.staccato.presentation.timeline.model.CategoryUiModel
import com.on.staccato.presentation.timeline.model.dummyPersonalCategoryWithoutPeriodUiModel

@Composable
fun PersonalCategoryWithoutPeriod(
    category: CategoryUiModel,
    onCategoryClick: (Long) -> Unit,
) {
    CategoryItem(
        category = category,
        onCategoryClick = onCategoryClick,
    )
}

@Preview(showBackground = true)
@Composable
fun PersonalCategoryWithoutPeriodPreview() {
    PersonalCategoryWithoutPeriod(
        dummyPersonalCategoryWithoutPeriodUiModel,
        onCategoryClick = {},
    )
}
