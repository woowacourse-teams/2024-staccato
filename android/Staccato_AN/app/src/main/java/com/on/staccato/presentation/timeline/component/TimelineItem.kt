package com.on.staccato.presentation.timeline.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.on.staccato.R
import com.on.staccato.presentation.component.DefaultAsyncImage
import com.on.staccato.presentation.timeline.model.TimelineUiModel
import com.on.staccato.presentation.timeline.model.dummyTimelineUiModel
import com.on.staccato.theme.Title3

@Composable
fun TimelineItem(
    timeline: TimelineUiModel,
    onCategoryClicked: (Long) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .padding(horizontal = 18.dp, vertical = 13.dp)
                .fillMaxWidth()
                .clickable { onCategoryClicked(timeline.categoryId) },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DefaultAsyncImage(
            modifier = Modifier.size(90.dp),
            bitmapPixelSize = 150,
            url = timeline.categoryThumbnailUrl,
            placeHolder = R.drawable.default_image,
            contentDescription = R.string.all_category_thumbnail_photo_description,
            radius = 4f,
        )

        Spacer(modifier = Modifier.width(15.dp))

        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CategoryColor(color = timeline.color)

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    CategoryPeriod()
                    Spacer(modifier = Modifier.size(2.dp))
                    Text(
                        text = timeline.categoryTitle,
                        style = Title3,
                    )
                }
            }

            Spacer(modifier = Modifier.size(22.dp))

            Row(
                verticalAlignment = Alignment.Bottom,
            ) {
                // TODO: members, hiddenMembersCount 서버에서 주는 값으로 변경
                if (members.isNotEmpty()) {
                    Members(
                        members = members,
                        hiddenMembersCount = 3,
                        color = timeline.color,
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                StaccatosCount(count = 21) // TODO: 서버에서 주는 값으로 변경
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun TimelineItemPreview(
    @PreviewParameter(TimelineItemPreviewParameterProvider::class)
    category: TimelineUiModel,
) {
    TimelineItem(
        category,
        onCategoryClicked = {},
    )
}

class TimelineItemPreviewParameterProvider : PreviewParameterProvider<TimelineUiModel> {
    override val values: Sequence<TimelineUiModel> =
        sequenceOf(*dummyTimelineUiModel.toTypedArray())
}
