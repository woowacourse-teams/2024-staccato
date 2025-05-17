package com.on.staccato.presentation.timeline.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester.Companion.createRefs
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.on.staccato.R
import com.on.staccato.presentation.component.DefaultAsyncImage
import com.on.staccato.presentation.timeline.model.TimelineUiModel
import com.on.staccato.presentation.timeline.model.dummyTimelineUiModel
import com.on.staccato.presentation.util.dpToPx
import com.on.staccato.theme.Title3

@Composable
fun TimelineItem(
    modifier: Modifier = Modifier,
    timeline: TimelineUiModel,
    onCategoryClicked: (Long) -> Unit,
) {
    ConstraintLayout(
        modifier =
            modifier
                .padding(horizontal = 18.dp, vertical = 13.dp)
                .fillMaxWidth()
                .clickable { onCategoryClicked(timeline.categoryId) },
    ) {
        val (
            thumbnail,
            firstSpacer,
            color,
            secondSpacer,
            period,
            title,
            thirdSpacer,
            participants,
            staccatosCount,
        ) = createRefs()

        DefaultAsyncImage(
            modifier =
                Modifier
                    .size(90.dp)
                    .constrainAs(thumbnail) {
                        top.linkTo(parent.top)
                    },
            bitmapPixelSize = 500,
            url = timeline.categoryThumbnailUrl,
            placeHolder = R.drawable.default_image,
            contentDescription = R.string.all_category_thumbnail_photo_description,
            radius = 4f.dpToPx(LocalContext.current),
        )

        Spacer(modifier = Modifier.constrainAs(firstSpacer) { start.linkTo(thumbnail.end) }.size(15.dp))

        CategoryColor(
            modifier =
                Modifier.constrainAs(color) {
                    start.linkTo(firstSpacer.end)
                    top.linkTo(period.top)
                },
            color = timeline.color,
        )

        Spacer(modifier = Modifier.constrainAs(secondSpacer) { start.linkTo(color.end) }.size(10.dp))

        CategoryPeriod(
            modifier =
                Modifier.constrainAs(period) {
                    start.linkTo(secondSpacer.end)
                    top.linkTo(thumbnail.top, margin = 4.dp)
                    bottom.linkTo(title.top)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                },
            startAt = timeline.startAt,
            endAt = timeline.endAt,
        )

        Text(
            modifier =
                Modifier.constrainAs(title) {
                    start.linkTo(secondSpacer.end)
                    end.linkTo(parent.end)
                    bottom.linkTo(color.bottom)
                    width = Dimension.fillToConstraints
                    height = Dimension.preferredWrapContent

                    if (timeline.startAt == null && timeline.endAt == null) {
                        top.linkTo(parent.top)
                    } else {
                        top.linkTo(period.bottom)
                    }
                },
            text = timeline.categoryTitle,
            style = Title3,
        )

        Spacer(modifier = Modifier.size(22.dp).constrainAs(thirdSpacer) { top.linkTo(title.bottom) })

        // TODO: members 서버에서 주는 값으로 변경
        if (members.isNotEmpty()) {
            Members(
                modifier =
                    Modifier.constrainAs(participants) {
                        start.linkTo(firstSpacer.end)
                        top.linkTo(thirdSpacer.bottom)
                        bottom.linkTo(parent.bottom)
                    },
                members = members,
                hiddenMembersCount = 3,
                color = timeline.color,
            )
        }

        // TODO: 서버에서 주는 값으로 변경
        StaccatosCount(
            modifier =
                Modifier.constrainAs(staccatosCount) {
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                },
            count = 21,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun TimelineItemPreview(
    @PreviewParameter(TimelineItemPreviewParameterProvider::class)
    category: TimelineUiModel,
) {
    TimelineItem(
        timeline = category,
        onCategoryClicked = {},
    )
}

class TimelineItemPreviewParameterProvider : PreviewParameterProvider<TimelineUiModel> {
    override val values: Sequence<TimelineUiModel> =
        sequenceOf(*dummyTimelineUiModel.toTypedArray())
}
