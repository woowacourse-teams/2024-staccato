package com.on.staccato.presentation.timeline.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.staccato.presentation.R
import com.on.staccato.presentation.component.DefaultAsyncImage
import com.on.staccato.presentation.timeline.model.CategoryUiModel
import com.on.staccato.presentation.timeline.model.dummyCategoryUiModel
import com.on.staccato.theme.Title3

@Composable
fun CategoryItem(
    modifier: Modifier = Modifier,
    category: CategoryUiModel,
    onCategoryClick: (Long) -> Unit,
    period: @Composable () -> Unit = {},
    participants: @Composable () -> Unit = {},
) {
    Row(
        modifier =
            modifier
                .padding(horizontal = 18.dp, vertical = 13.dp)
                .fillMaxWidth()
                .clickable { onCategoryClick(category.categoryId) },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DefaultAsyncImage(
            modifier =
                Modifier
                    .padding(end = 15.dp)
                    .size(90.dp),
            imageSizeDp = 90.dp,
            url = category.categoryThumbnailUrl,
            placeHolder = R.drawable.default_image,
            errorImageRes = R.drawable.default_image,
            contentDescription = R.string.all_category_thumbnail_photo_description,
            radiusDp = 4.dp,
        )

        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CategoryFolder(
                    modifier = Modifier.padding(top = 2.dp, end = 12.dp),
                    color = category.color,
                )

                Column {
                    Text(
                        modifier = Modifier,
                        text = category.categoryTitle,
                        style = Title3,
                    )
                    period()
                }
            }

            Spacer(modifier = Modifier.size(22.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
            ) {
                participants()
                Spacer(modifier = Modifier.weight(1f))
                StaccatoCount(count = category.staccatoCount)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryItemPreview() {
    CategoryItem(
        category = dummyCategoryUiModel,
        onCategoryClick = {},
    )
}
