package com.on.staccato.presentation.bindingadapter

import android.view.View
import android.widget.ScrollView
import androidx.databinding.BindingAdapter
import com.on.staccato.presentation.categorycreation.ThumbnailUiModel
import com.on.staccato.presentation.timeline.model.FilterType
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
    value = ["visibilityByTimeline", "visibilityByFilterType", "isEmptyView", "isTimelineLoading"],
)
fun View.setVisibilityByTimelineAndFilter(
    timeLine: List<TimelineUiModel>? = null,
    filterType: FilterType?,
    isEmptyView: Boolean?,
    isTimelineLoading: Boolean?,
) {
    visibility =
        when (filterType) {
            FilterType.TERM -> getVisibilityForTermFilter(isEmptyView)
            else -> getVisibilityForAllCategories(timeLine, filterType, isEmptyView, isTimelineLoading)
        }
}

private fun getVisibilityForTermFilter(isEmptyView: Boolean?) = if (isEmptyView == true) View.GONE else View.VISIBLE

private fun getVisibilityForAllCategories(
    timeLine: List<TimelineUiModel>?,
    filterType: FilterType?,
    isEmptyView: Boolean?,
    isTimelineLoading: Boolean?,
): Int {
    return when (isEmptyTimeline(timeLine, filterType)) {
        true -> getVisibilityForEmptyTimeline(isEmptyView, isTimelineLoading)
        else -> getVisibilityForExistingTimeline(isEmptyView)
    }
}

private fun isEmptyTimeline(
    timeLine: List<TimelineUiModel>?,
    filterType: FilterType?,
) = timeLine.isNullOrEmpty() && filterType == null

private fun getVisibilityForEmptyTimeline(
    isEmptyView: Boolean?,
    isTimelineLoading: Boolean?,
) = when {
    isEmptyView == true && isTimelineLoading != true -> View.VISIBLE
    isEmptyView != true && isTimelineLoading == true -> View.VISIBLE
    else -> View.INVISIBLE
}

private fun getVisibilityForExistingTimeline(isEmptyView: Boolean?) =
    when (isEmptyView) {
        true -> View.INVISIBLE
        else -> View.VISIBLE
    }
