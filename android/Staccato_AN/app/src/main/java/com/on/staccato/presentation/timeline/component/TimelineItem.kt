package com.on.staccato.presentation.timeline.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.on.staccato.R
import com.on.staccato.presentation.component.DefaultAsyncImage
import com.on.staccato.presentation.component.clickableWithoutRipple
import com.on.staccato.presentation.timeline.model.TimelineUiModel
import com.on.staccato.presentation.timeline.model.dummyTimelineUiModels
import com.on.staccato.presentation.util.dpToPx
import com.on.staccato.theme.Title3

@Composable
fun TimelineItem(
    modifier: Modifier = Modifier,
    category: TimelineUiModel,
    onCategoryClicked: (Long) -> Unit,
) {
    val hasPeriod = category.startAt != null && category.endAt != null

    ConstraintLayout(
        modifier =
            modifier
                .padding(horizontal = 18.dp, vertical = 13.dp)
                .fillMaxWidth()
                .clickableWithoutRipple { onCategoryClicked(category.categoryId) },
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
            staccatoCount,
        ) = createRefs()

        DefaultAsyncImage(
            modifier =
                Modifier
                    .size(90.dp)
                    .constrainAs(thumbnail) {
                        top.linkTo(parent.top)
                    },
            bitmapPixelSize = 500,
            url = category.categoryThumbnailUrl,
            placeHolder = R.drawable.default_image,
            contentDescription = R.string.all_category_thumbnail_photo_description,
            radius = 4f.dpToPx(LocalContext.current),
        )

        Spacer(modifier = Modifier.constrainAs(firstSpacer) { start.linkTo(thumbnail.end) }.size(15.dp))

        CategoryColor(
            modifier =
                Modifier.constrainAs(color) {
                    start.linkTo(firstSpacer.end)
                    top.linkTo(thumbnail.top)
                },
            color = category.color.label,
        )

        Spacer(modifier = Modifier.constrainAs(secondSpacer) { start.linkTo(color.end) }.size(10.dp))

        Text(
            modifier =
                Modifier.constrainAs(title) {
                    start.linkTo(secondSpacer.end)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                    height = Dimension.preferredWrapContent

                    if (hasPeriod) {
                        top.linkTo(color.top)
                        bottom.linkTo(period.top)
                    } else {
                        top.linkTo(color.top)
                        bottom.linkTo(color.bottom)
                    }
                },
            text = category.categoryTitle,
            style = Title3,
        )

        CategoryPeriod(
            modifier =
                Modifier.constrainAs(period) {
                    start.linkTo(secondSpacer.end)
                    top.linkTo(title.bottom, margin = 3.dp)
                    bottom.linkTo(color.bottom)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                },
            startAt = category.startAt,
            endAt = category.endAt,
        )

        Spacer(
            modifier =
                Modifier.size(22.dp)
                    .constrainAs(thirdSpacer) {
                        if (hasPeriod) {
                            top.linkTo(period.bottom)
                        } else {
                            top.linkTo(title.bottom)
                        }
                    },
        )

        if (category.isShared) {
            Participants(
                modifier =
                    Modifier.constrainAs(participants) {
                        start.linkTo(firstSpacer.end)
                        top.linkTo(thirdSpacer.bottom)
                        bottom.linkTo(parent.bottom)
                    },
                participants = category.participants,
                colorLabel = category.color.label,
            )
        }

        StaccatoCount(
            modifier =
                Modifier.constrainAs(staccatoCount) {
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                },
            count = category.staccatoCount,
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
        category = category,
        onCategoryClicked = {},
    )
}

class TimelineItemPreviewParameterProvider(
    override val values: Sequence<TimelineUiModel> =
        sequenceOf(*dummyTimelineUiModels.toTypedArray()),
) : PreviewParameterProvider<TimelineUiModel>
