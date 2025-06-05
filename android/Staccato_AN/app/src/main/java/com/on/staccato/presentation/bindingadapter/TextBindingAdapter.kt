package com.on.staccato.presentation.bindingadapter

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isGone
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout
import com.on.staccato.R
import com.on.staccato.domain.model.CategoryCandidate
import com.on.staccato.domain.model.CategoryCandidates
import com.on.staccato.domain.model.NicknameState
import com.on.staccato.presentation.common.InputState
import com.on.staccato.presentation.common.getFormattedLocalDateTime
import com.on.staccato.presentation.common.photo.AttachedPhotoState
import com.on.staccato.presentation.mapper.toInputState
import com.on.staccato.presentation.timeline.model.FilterType
import com.on.staccato.presentation.timeline.model.SortType
import com.on.staccato.presentation.timeline.model.TimelineUiModel
import com.on.staccato.presentation.util.dpToPx
import java.time.LocalDate
import java.time.LocalDateTime

@BindingAdapter(value = ["selectedCategory", "categoryCandidates"])
fun TextView.setSelectedCategory(
    selectedCategory: CategoryCandidate?,
    categoryCandidates: CategoryCandidates?,
) {
    when {
        (categoryCandidates?.categoryCandidates?.isEmpty() == true) -> {
            text = resources.getString(R.string.staccato_creation_no_category)
            setTextColor(resources.getColor(R.color.gray3, null))
            isClickable = false
            isFocusable = false
        }

        (selectedCategory == null) -> {
            text = resources.getString(R.string.staccato_creation_no_category_in_this_date)
            setTextColor(resources.getColor(R.color.gray3, null))
            isClickable = false
            isFocusable = false
        }

        else -> {
            text = selectedCategory.categoryTitle
            isClickable = true
            isFocusable = true
            setTextColor(resources.getColor(R.color.staccato_black, null))
        }
    }
}

@BindingAdapter("dateTimeWithAmPm")
fun TextView.setDateTimeWithAmPm(dateTime: LocalDateTime?) {
    if (dateTime == null) {
        text = resources.getString(R.string.staccato_creation_loading_visitedAt)
        setTextColor(resources.getColor(R.color.gray3, null))
        isClickable = false
        isFocusable = false
    } else {
        text = dateTime.let(::getFormattedLocalDateTime)
        setTextColor(resources.getColor(R.color.staccato_black, null))
        isClickable = true
        isFocusable = true
    }
}

@BindingAdapter(
    value = ["startDate", "endDate"],
)
fun TextView.setCategoryPeriod(
    startDate: LocalDate?,
    endDate: LocalDate?,
) {
    if (startDate == null || endDate == null) {
        text = resources.getString(R.string.category_creation_period_hint)
        setTextColor(resources.getColor(R.color.gray3, null))
    } else {
        text =
            resources.getString(R.string.category_creation_selected_period)
                .format(startDate, endDate)
        setTextColor(resources.getColor(R.color.staccato_black, null))
    }
}

@BindingAdapter("isCategoryCandidatesEmpty")
fun TextView.setIsCategoryCandidatesEmptyVisibility(categoryCandidates: CategoryCandidates?) {
    isGone = !categoryCandidates?.categoryCandidates.isNullOrEmpty()
}

@BindingAdapter("timelineNickname")
fun TextView.formatTimelineNickname(nickname: String?) {
    text = nickname?.let {
        resources.getString(R.string.timeline_nickname_memories).format(it)
    } ?: EMPTY_TEXT
}

@BindingAdapter("visitedAtHistory")
fun TextView.formatVisitedAtHistory(visitedAt: LocalDateTime?) {
    text = visitedAt?.let {
        resources.getString(R.string.staccato_at_history).format(getFormattedLocalDateTime(it))
    } ?: EMPTY_TEXT
}

@BindingAdapter(
    value = ["startAt", "endAt"],
)
fun TextView.formatLocalDateToDatePeriod(
    startAt: LocalDate?,
    endAt: LocalDate?,
) {
    val periodFormatString = resources.getString(R.string.category_period_dot)
    text =
        if (startAt != null && endAt != null) {
            visibility = View.VISIBLE
            periodFormatString.format(
                startAt.year,
                startAt.monthValue,
                startAt.dayOfMonth,
                endAt.year,
                endAt.monthValue,
                endAt.dayOfMonth,
            )
        } else {
            visibility = View.INVISIBLE
            null
        }
}

@BindingAdapter(
    value = ["categoryStartAt", "categoryEndAt"],
)
fun TextView.formatLocalDateToDatePeriodInCategory(
    startAt: LocalDate?,
    endAt: LocalDate?,
) {
    val periodFormatString = resources.getString(R.string.category_period_dot)
    text =
        if (startAt != null && endAt != null) {
            visibility = View.VISIBLE
            periodFormatString.format(
                startAt.year,
                startAt.monthValue,
                startAt.dayOfMonth,
                endAt.year,
                endAt.monthValue,
                endAt.dayOfMonth,
            )
        } else {
            visibility = View.GONE
            null
        }
}

@BindingAdapter(value = ["attachedPhotoNumbers", "maxPhotoNumbers"])
fun TextView.setPhotoNumbers(
    attachedPhotoNumbers: Int,
    maxPhotoNumbers: Int,
) {
    text =
        resources.getString(R.string.all_photo_number).format(attachedPhotoNumbers, maxPhotoNumbers)
}

@BindingAdapter("photoDragHintVisibility")
fun TextView.setPhotoDragHintVisibility(currentPhotoNumbers: Int) {
    isGone = currentPhotoNumbers < DRAGGABLE_PHOTO_NUMBER
}

@BindingAdapter("selectedAddress")
fun TextView.setSelectedAddress(address: String?) {
    text = address ?: context.getString(R.string.staccato_creation_empty_address)
}

@BindingAdapter("categorySort")
fun TextView.setCategoryFilter(sortType: SortType?) {
    text =
        when (sortType) {
            SortType.UPDATED -> resources.getString(R.string.sort_updated_order)
            SortType.NEWEST -> resources.getString(R.string.sort_newest_order)
            SortType.OLDEST -> resources.getString(R.string.sort_oldest_order)
            else -> resources.getString(R.string.timeline_sort)
        }
}

@BindingAdapter("categoryFilter")
fun TextView.setCategoryFilter(filterType: FilterType?) {
    when (filterType) {
        FilterType.WITH_TERM -> {
            text = resources.getString(R.string.timeline_filter_with_term)
            setTextColor(resources.getColor(R.color.staccato_blue, null))
        }

        FilterType.WITHOUT_TERM -> {
            text = resources.getString(R.string.timeline_filter_without_term)
            setTextColor(resources.getColor(R.color.staccato_blue, null))
        }

        else -> {
            text = resources.getString(R.string.timeline_filter_all)
            setTextColor(resources.getColor(R.color.gray3, null))
        }
    }
}

@BindingAdapter(
    value = ["visibilityByTimeline", "visibilityByFilterType", "isTimelineLoading"],
)
fun TextView.setTimelineEmptyText(
    timeLine: List<TimelineUiModel>? = null,
    filterType: FilterType?,
    isTimelineLoading: Boolean?,
) {
    visibility =
        if (timeLine.isNullOrEmpty() && isTimelineLoading == false) View.VISIBLE else View.GONE
    updateTopMargin(if (filterType == null) 10f else 100f)
}

@BindingAdapter(
    value = ["visibilityAndTextByTimeline", "visibilityAndTextByFilterType", "isTimelineLoading"],
)
fun TextView.setMakeCategoryText(
    timeLine: List<TimelineUiModel>? = null,
    filterType: FilterType?,
    isTimelineLoading: Boolean?,
) {
    visibility =
        if (timeLine.isNullOrEmpty() && isTimelineLoading == false) {
            View.VISIBLE
        } else {
            View.GONE
        }
    text =
        context.getString(
            when (filterType) {
                null -> R.string.timeline_make_category
                else -> R.string.timeline_no_filtered_category
            },
        )
}

private fun View.updateTopMargin(dp: Float) {
    val params = layoutParams as? ViewGroup.MarginLayoutParams ?: return
    val px = dp.dpToPx(context).toInt()
    params.topMargin = px
    layoutParams = params
}

@BindingAdapter("nicknameState")
fun TextInputLayout.setNicknameInputState(nicknameState: NicknameState) {
    val inputState = nicknameState.toInputState(context)
    changeLayoutBy(inputState)
}

fun TextInputLayout.changeLayoutBy(inputState: InputState) {
    val strokeWidth = DEFAULT_DP_SIZE.dpToPx(context).toInt()
    when (inputState) {
        is InputState.Empty -> {
            boxStrokeColor = resources.getColor(R.color.gray1, context.theme)
            boxStrokeWidth = 0
            boxStrokeWidthFocused = 0
            error = null
            helperText = null
        }

        is InputState.Valid -> {
            boxStrokeColor = resources.getColor(R.color.staccato_blue, context.theme)
            boxStrokeWidth = 0
            boxStrokeWidthFocused = strokeWidth
            error = null
            helperText = inputState.message
        }

        is InputState.Invalid -> {
            boxStrokeColor = resources.getColor(R.color.accents4, context.theme)
            boxStrokeWidth = strokeWidth
            boxStrokeWidthFocused = strokeWidth
            error = inputState.errorMessage
            helperText = null
        }
    }
}

@BindingAdapter("visibilityByDescription")
fun View.visibilityByDescription(description: String?) {
    visibility = if (description.isNullOrEmpty()) View.GONE else View.VISIBLE
}

@BindingAdapter("failVisibilityByState")
fun TextView.setFailVisibilityByPhotoState(photoState: AttachedPhotoState) {
    visibility =
        if (photoState == AttachedPhotoState.Failure) {
            View.VISIBLE
        } else {
            View.GONE
        }
}

private const val DRAGGABLE_PHOTO_NUMBER = 2
private const val EMPTY_TEXT = ""
private const val DEFAULT_DP_SIZE = 1F
