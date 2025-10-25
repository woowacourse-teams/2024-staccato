package com.on.staccato.presentation.timeline.component

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.staccato.presentation.timeline.model.CategoryUiModel
import com.on.staccato.presentation.timeline.model.dummyCommonCategoryWithPeriodUiModel

@Composable
fun CommonCategoryWithPeriod(
    category: CategoryUiModel,
    onCategoryClick: (Long) -> Unit,
) {
    CategoryItem(
        category = category,
        onCategoryClick = onCategoryClick,
        period = {
            CategoryPeriod(
                modifier = Modifier.padding(top = 3.dp),
                startAt = category.startAt,
                endAt = category.endAt,
            )
        },
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
fun CommonCategoryWithPeriodPreview() {
    CommonCategoryWithPeriod(
        dummyCommonCategoryWithPeriodUiModel,
        onCategoryClick = {},
    )
}
