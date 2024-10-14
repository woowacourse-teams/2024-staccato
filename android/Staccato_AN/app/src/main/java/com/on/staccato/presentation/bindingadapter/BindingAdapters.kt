package com.on.staccato.presentation.bindingadapter

import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.ScrollView
import androidx.databinding.BindingAdapter
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

@BindingAdapter(value = ["visibilityByThumbnailUri", "visibilityByThumbnailUrl"])
fun View.setThumbnail(
    thumbnailUri: Uri?,
    thumbnailUrl: String?,
) {
    visibility =
        if (thumbnailUri == null && thumbnailUrl == null) {
            View.VISIBLE
        } else {
            View.GONE
        }
}

@BindingAdapter(value = ["visibilityByThumbnailUri", "visibilityByThumbnailUrl"])
fun ProgressBar.setThumbnailLoadingProgressBar(
    thumbnailUri: Uri?,
    thumbnailUrl: String?,
) {
    visibility =
        if (thumbnailUri != null && thumbnailUrl == null) {
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
fun ViewGroup.setMemoryAddButtonVisible(
    timeLine: List<TimelineUiModel>? = null,
    isTimelineLoading: Boolean,
) {
    visibility = if (isTimeLineEmpty(timeLine, isTimelineLoading)) View.GONE else View.VISIBLE
}

private fun isTimeLineEmpty(
    timeLine: List<TimelineUiModel>?,
    isTimelineLoading: Boolean,
) = timeLine.isNullOrEmpty() && isTimelineLoading.not()
