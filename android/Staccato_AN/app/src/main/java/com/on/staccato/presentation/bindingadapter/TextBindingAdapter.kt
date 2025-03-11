package com.on.staccato.presentation.bindingadapter

import android.view.View
import android.widget.TextView
import androidx.core.view.isGone
import androidx.databinding.BindingAdapter
import com.on.staccato.R
import com.on.staccato.domain.model.CategoryCandidate
import com.on.staccato.domain.model.CategoryCandidates
import com.on.staccato.presentation.common.getFormattedLocalDateTime
import com.on.staccato.presentation.timeline.model.FilterType
import com.on.staccato.presentation.timeline.model.SortType
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
    text = dateTime?.let(::getFormattedLocalDateTime) ?: EMPTY_TEXT
    setTextColor(resources.getColor(R.color.staccato_black, null))
    isClickable = true
    isFocusable = true
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
        FilterType.TERM -> {
            text = resources.getString(R.string.timeline_filter_term)
            setTextColor(resources.getColor(R.color.staccato_blue, null))
        }

        else -> {
            text = resources.getString(R.string.timeline_filter_all)
            setTextColor(resources.getColor(R.color.gray3, null))
        }
    }
}

private const val DRAGGABLE_PHOTO_NUMBER = 2
private const val EMPTY_TEXT = ""
