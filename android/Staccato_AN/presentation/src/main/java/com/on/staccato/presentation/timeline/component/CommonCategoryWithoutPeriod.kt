package com.on.staccato.presentation.timeline.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.on.staccato.presentation.timeline.model.CategoryUiModel
import com.on.staccato.presentation.timeline.model.dummyCommonCategoryWithoutPeriodUiModel

@Composable
fun CommonCategoryWithoutPeriod(
    category: CategoryUiModel,
    onCategoryClick: (Long) -> Unit,
) {
    CategoryItem(
        category = category,
        onCategoryClick = onCategoryClick,
        participants = {
            Participants(
                participants = category.participants,
                color = category.color,
            )
        },
    )
}

@Preview(showBackground = true)
@Composable
fun CommonCategoryWithoutPeriodPreview() {
    CommonCategoryWithoutPeriod(
        dummyCommonCategoryWithoutPeriodUiModel,
        onCategoryClick = {},
    )
}
