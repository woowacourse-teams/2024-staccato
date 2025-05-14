package com.on.staccato.presentation.timeline.compose

import androidx.annotation.ColorRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.on.staccato.R
import com.on.staccato.presentation.component.DefaultAsyncImage
import com.on.staccato.presentation.component.TextComponent
import com.on.staccato.presentation.timeline.model.TimelineUiModel
import com.on.staccato.theme.Body4
import com.on.staccato.theme.Gray1
import com.on.staccato.theme.Gray3
import com.on.staccato.theme.Title3
import java.time.LocalDate

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
        // TODO: size 90으로 수정
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

                CategoryPeriodAndTitle(
                    timeline.startAt,
                    timeline.endAt,
                    timeline.categoryTitle,
                )
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

// TODO: 파라미터 Enum Class 타입으로 변경 필요
@Composable
private fun CategoryColor(
    @ColorRes color: Int = R.color.gray3,
) {
    // TODO: Box 백그라운드 컬러 번경 필요
    // TODO: size 36.dp로 변경
    Box(
        modifier =
            Modifier
                .size(32.dp)
                .background(color = Gray1, shape = CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.icon_folder),
            contentDescription = stringResource(id = R.string.category_creation_color),
            tint = colorResource(id = color),
        )
    }
}

@Composable
private fun CategoryPeriodAndTitle(
    startAt: LocalDate? = null,
    endAt: LocalDate? = null,
    categoryTitle: String,
) {
    // TODO: 년도에 따라 형식 수정
    val period =
        if (startAt != null && endAt != null) {
            stringResource(
                R.string.category_period_dot,
                startAt.year,
                startAt.monthValue,
                startAt.dayOfMonth,
                endAt.year,
                endAt.monthValue,
                endAt.dayOfMonth,
            )
        } else {
            null
        }

    Column {
        if (period != null) {
            TextComponent(
                color = Gray3,
                description = period,
                style = Body4, // TODO: style Body5로 수정
            )
        }
        // TODO: Spacer 추가 size 2.dp
        TextComponent(
            description = categoryTitle,
            style = Title3,
        )
    }
}

@Composable
private fun StaccatosCount(count: Int = 0) {
    Row(
        modifier = Modifier.padding(bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.icon_marker),
            contentDescription = "Marker Icon",
            tint = Color.Unspecified,
        )
        Spacer(modifier = Modifier.width(3.dp))
        TextComponent(color = Gray3, description = count.toString(), style = Body4)
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
        sequenceOf(*timeline.toTypedArray())
}
