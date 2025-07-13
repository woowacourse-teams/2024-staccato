package com.on.staccato.presentation.timeline.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.on.staccato.benchmark.trace
import com.on.staccato.presentation.R
import com.on.staccato.presentation.component.DefaultAsyncImage
import com.on.staccato.presentation.component.clickableWithoutRipple
import com.on.staccato.presentation.timeline.model.CategoryUiModel
import com.on.staccato.presentation.timeline.model.dummyCategoryUiModels
import com.on.staccato.theme.Title3

private const val THUMBNAIL_IMAGE_SIZE = 90

@Composable
fun CategoryItem(
    modifier: Modifier = Modifier,
    category: CategoryUiModel,
    onCategoryClicked: (Long) -> Unit,
) {
    val hasPeriod = category.startAt != null && category.endAt != null

    trace("CategoryItem") {
        ConstraintLayout(
            modifier =
                modifier
                    .padding(horizontal = 18.dp, vertical = 13.dp)
                    .fillMaxWidth()
                    .clickableWithoutRipple { onCategoryClicked(category.categoryId) },
        ) {
            val (
                thumbnail,
                color,
                period,
                title,
                participants,
                staccatoCount,
            ) = createRefs()

            trace("ThumbnailImage") {
                DefaultAsyncImage(
                    modifier =
                        Modifier
                            .padding(end = 15.dp)
                            .size(THUMBNAIL_IMAGE_SIZE.dp)
                            .constrainAs(thumbnail) {
                                top.linkTo(parent.top)
                            },
                    imageSizeDp = THUMBNAIL_IMAGE_SIZE.dp,
                    url = category.categoryThumbnailUrl,
                    placeHolder = R.drawable.default_image,
                    errorImageRes = R.drawable.default_image,
                    contentDescription = R.string.all_category_thumbnail_photo_description,
                    radiusDp = 4.dp,
                )
            }

            trace("CategoryFolder") {
                CategoryFolder(
                    modifier =
                        Modifier
                            .padding(end = 10.dp)
                            .constrainAs(color) {
                                start.linkTo(thumbnail.end)
                                top.linkTo(thumbnail.top)
                            }.padding(2.dp),
                    color = category.color,
                )
            }

            trace("Title") {
                Text(
                    modifier =
                        Modifier.constrainAs(title) {
                            start.linkTo(color.end)
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
            }

            trace("CategoryPeriod") {
                CategoryPeriod(
                    modifier =
                        Modifier.constrainAs(period) {
                            start.linkTo(color.end)
                            top.linkTo(title.bottom, margin = 3.dp)
                            bottom.linkTo(color.bottom)
                            end.linkTo(parent.end)
                            width = Dimension.fillToConstraints
                        },
                    startAt = category.startAt,
                    endAt = category.endAt,
                )
            }

            trace("Participants") {
                if (category.isShared) {
                    Participants(
                        modifier =
                            Modifier
                                .padding(top = 22.dp)
                                .constrainAs(participants) {
                                    if (hasPeriod) {
                                        top.linkTo(period.bottom)
                                    } else {
                                        top.linkTo(title.bottom)
                                    }
                                    start.linkTo(thumbnail.end)
                                    bottom.linkTo(parent.bottom)
                                },
                        participants = category.participants,
                        color = category.color,
                    )
                }
            }

            trace("StaccatoCount") {
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
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun CategoryItemPreview(
    @PreviewParameter(CategoryItemPreviewParameterProvider::class)
    category: CategoryUiModel,
) {
    CategoryItem(
        category = category,
        onCategoryClicked = {},
    )
}

private class CategoryItemPreviewParameterProvider(
    override val values: Sequence<CategoryUiModel> =
        sequenceOf(*dummyCategoryUiModels.toTypedArray()),
) : PreviewParameterProvider<CategoryUiModel>
