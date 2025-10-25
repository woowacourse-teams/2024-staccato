package com.on.staccato.presentation.bindingadapter

import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import com.on.staccato.presentation.R
import com.on.staccato.presentation.category.model.CategoryStaccatoUiModel
import com.on.staccato.presentation.categorycreation.model.ThumbnailUiModel
import com.on.staccato.presentation.photo.PhotoUploadState
import com.on.staccato.presentation.timeline.model.CategoryUiModel
import com.on.staccato.presentation.timeline.model.FilterType

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
    timeLine: List<CategoryUiModel>? = null,
    filterType: FilterType?,
    isEmptyView: Boolean?,
    isTimelineLoading: Boolean?,
) {
    visibility =
        when (filterType) {
            null -> getVisibilityForAllCategories(timeLine, isEmptyView, isTimelineLoading)
            else -> getVisibilityForFilteredCategories(isEmptyView)
        }
}

@BindingAdapter("visibilityByStaccatos")
fun View.setVisibilityByStaccatos(staccatos: List<CategoryStaccatoUiModel>?) {
    visibility = if (staccatos.isNullOrEmpty()) View.VISIBLE else View.GONE
}

@BindingAdapter("visibilityByIsBottomSheetExpanded")
fun View.setVisibilityBy(isBottomSheetExpanded: Boolean) {
    visibility = if (isBottomSheetExpanded) View.INVISIBLE else View.VISIBLE
}

@BindingAdapter("backgroundByIsBottomSheetExpanded")
fun ViewGroup.setBackgroundBy(isBottomSheetExpanded: Boolean) {
    @DrawableRes val id: Int =
        if (isBottomSheetExpanded) R.drawable.shape_bottom_sheet_square else R.drawable.shape_bottom_sheet_16dp
    setBackgroundResource(
        id,
    )
}

private fun getVisibilityForFilteredCategories(isEmptyView: Boolean?) = if (isEmptyView == true) View.GONE else View.VISIBLE

private fun getVisibilityForAllCategories(
    timeLine: List<CategoryUiModel>?,
    isEmptyView: Boolean?,
    isTimelineLoading: Boolean?,
): Int {
    return if (timeLine.isNullOrEmpty()) {
        getVisibilityForEmptyTimeline(isEmptyView, isTimelineLoading)
    } else {
        getVisibilityForExistingTimeline(isEmptyView)
    }
}

private fun isEmptyTimeline(
    timeLine: List<CategoryUiModel>?,
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

@BindingAdapter("loadingVisibilityByState")
fun View.setLoadingVisibilityByPhotoState(photoState: PhotoUploadState) {
    visibility =
        if (photoState == PhotoUploadState.Loading) {
            View.VISIBLE
        } else {
            View.GONE
        }
}

@BindingAdapter("backgroundVisibilityByState")
fun View.setBackgroundVisibilityByPhotoState(photoState: PhotoUploadState) {
    visibility =
        if (photoState == PhotoUploadState.Success) {
            View.GONE
        } else {
            View.VISIBLE
        }
}
