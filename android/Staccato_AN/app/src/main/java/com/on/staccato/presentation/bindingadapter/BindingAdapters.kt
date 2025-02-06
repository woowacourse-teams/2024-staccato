package com.on.staccato.presentation.bindingadapter

import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.databinding.BindingAdapter
import com.on.staccato.presentation.categorycreation.ThumbnailUiModel
import com.on.staccato.presentation.timeline.model.TimelineUiModel

@BindingAdapter("visibleOrGone")
fun View.setVisibility(isVisible: Boolean?) {
    visibility =
        if (isVisible == true) {
            View.VISIBLE
        } else {
            View.GONE
        }
}

@BindingAdapter("scrollToBottom")
fun ScrollView.setScrollToBottom(isScrollable: Boolean) {
    if (isScrollable) {
        post { fullScroll(ScrollView.FOCUS_DOWN) }
    }
}

@BindingAdapter(value = ["visibilityByEmptyThumbnail"])
fun View.setThumbnailVisibility(thumbnail: ThumbnailUiModel) {
    visibility =
        if (thumbnail.uri == null && thumbnail.url == null) {
            View.VISIBLE
        } else {
            View.GONE
        }
}

@BindingAdapter(value = ["loadingVisibilityByThumbnail"])
fun View.setThumbnailLoadingVisibility(thumbnail: ThumbnailUiModel) {
    visibility =
        if (thumbnail.uri != null && thumbnail.url == null) {
            View.VISIBLE
        } else {
            View.GONE
        }
}

@BindingAdapter(
    value = ["visibilityByTimeline", "visibilityByLoading"],
)
fun View.setTimelineEmptyViewVisible(
    timeLine: List<TimelineUiModel>? = null,
    isTimelineLoading: Boolean,
) {
    visibility = if (isTimeLineEmpty(timeLine, isTimelineLoading)) View.VISIBLE else View.GONE
}

@BindingAdapter(
    value = ["visibilityByTimeline", "visibilityByLoading"],
)
fun ViewGroup.setCategoryAddButtonVisible(
    timeLine: List<TimelineUiModel>? = null,
    isTimelineLoading: Boolean,
) {
    visibility = if (isTimeLineEmpty(timeLine, isTimelineLoading)) View.GONE else View.VISIBLE
}

private fun isTimeLineEmpty(
    timeLine: List<TimelineUiModel>?,
    isTimelineLoading: Boolean,
) = timeLine.isNullOrEmpty() && isTimelineLoading.not()
