package com.on.staccato.presentation.bindingadapter

import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.NumberPicker
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.databinding.BindingAdapter
import com.airbnb.lottie.LottieAnimationView
import com.on.staccato.presentation.common.getFormattedLocalDate
import com.on.staccato.presentation.common.getFormattedLocalDateTime
import com.on.staccato.presentation.timeline.model.TimelineUiModel
import java.time.LocalDate
import java.time.LocalDateTime

@BindingAdapter("setDateTimeWithAmPm")
fun TextView.setDateTimeWithAmPm(setNowDateTime: LocalDateTime?) {
    text = setNowDateTime?.let(::getFormattedLocalDateTime) ?: ""
}

@BindingAdapter("visitedAtNumberPickerItems")
fun NumberPicker.setVisitedAtNumberPickerItems(items: List<LocalDateTime>?) {
    items?.map { it.toLocalDate() }
    if (items.isNullOrEmpty()) {
        isGone = true
    } else {
        displayedValues = items.map(::getFormattedLocalDateTime).toTypedArray()
    }
}

@BindingAdapter("localDateNumberPickerItems")
fun NumberPicker.setLocalDateNumberPickerItems(items: List<LocalDate>?) {
    if (items.isNullOrEmpty()) {
        isGone = true
    } else {
        displayedValues = items.map(::getFormattedLocalDate).toTypedArray()
    }
}

@BindingAdapter("setLoadingLottieVisibility")
fun LottieAnimationView.setLoadingLottieVisibility(isLoading: Boolean?) {
    if (isLoading == true) {
        visibility = View.VISIBLE
    } else {
        visibility = View.GONE
    }
}

@BindingAdapter(value = ["thumbnailUri", "thumbnailUrl"])
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

@BindingAdapter("scrollToBottom")
fun ScrollView.scrollToBottom(isPeriodActive: Boolean) {
    if (isPeriodActive) {
        post { fullScroll(ScrollView.FOCUS_DOWN) }
    }
}

@BindingAdapter(
    value = ["visibilityByTimeline", "visibilityByLoading"],
)
fun ViewGroup.setMemoryAddButtonVisible(
    timeLine: List<TimelineUiModel>? = null,
    isTimelineLoading: Boolean,
) {
    visibility =
        if (timeLine.isNullOrEmpty() && isTimelineLoading.not()) {
            View.GONE
        } else {
            View.VISIBLE
        }
}

@BindingAdapter("setAttachedPhotoVisibility")
fun FrameLayout.setAttachedPhotoVisibility(items: Array<Uri>?) {
    isInvisible = !items.isNullOrEmpty()
}

@BindingAdapter(value = ["thumbnailUri", "thumbnailUrl"])
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

@BindingAdapter(
    value = ["visibilityByTimeline", "visibilityByLoading"],
)
fun View.setTimelineEmptyViewVisible(
    timeLine: List<TimelineUiModel>? = null,
    isTimelineLoading: Boolean,
) {
    visibility =
        if (timeLine.isNullOrEmpty() && isTimelineLoading.not()) {
            View.VISIBLE
        } else {
            View.GONE
        }
}
