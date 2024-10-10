package com.on.staccato.presentation.bindingadapter

import android.view.View
import android.widget.TextView
import androidx.core.view.isGone
import androidx.databinding.BindingAdapter
import com.on.staccato.R
import com.on.staccato.domain.model.MemoryCandidate
import com.on.staccato.domain.model.MemoryCandidates
import com.on.staccato.presentation.common.getFormattedLocalDateTime
import okhttp3.internal.format
import java.time.LocalDate
import java.time.LocalDateTime

@BindingAdapter(value = ["selectedMemory", "memoryCandidates"])
fun TextView.setSelectedMemory(
    selectedMemory: MemoryCandidate?,
    memoryCandidates: MemoryCandidates?,
) {
    if (memoryCandidates?.memoryCandidate?.isEmpty() == true) {
        text = resources.getString(R.string.visit_creation_no_memory_hint)
        setTextColor(resources.getColor(R.color.gray3, null))
        isClickable = false
        isFocusable = false
    } else if (selectedMemory == null) {
        text = resources.getString(R.string.visit_creation_memory_selection_hint)
        setTextColor(resources.getColor(R.color.gray3, null))
        isClickable = true
        isFocusable = true
    } else {
        text = selectedMemory.memoryTitle
        setTextColor(resources.getColor(R.color.staccato_black, null))
    }
}

@BindingAdapter(value = ["dateTimeWithAmPm", "memoryCandidates"])
fun TextView.setDateTimeWithAmPm(
    dateTime: LocalDateTime?,
    memoryCandidates: MemoryCandidates?,
) {
    if (memoryCandidates?.memoryCandidate?.isEmpty() == true) {
        text = resources.getString(R.string.visit_creation_memory_selection_hint)
        setTextColor(resources.getColor(R.color.gray3, null))
        isClickable = false
        isFocusable = false
    } else {
        text = dateTime?.let(::getFormattedLocalDateTime) ?: ""
        setTextColor(resources.getColor(R.color.staccato_black, null))
        isClickable = true
        isFocusable = true
    }
}

@BindingAdapter(
    value = ["startDate", "endDate"],
)
fun TextView.setMemoryPeriod(
    startDate: LocalDate?,
    endDate: LocalDate?,
) {
    if (startDate == null || endDate == null) {
        text = resources.getString(R.string.memory_creation_period_hint)
        setTextColor(resources.getColor(R.color.gray3, null))
    } else {
        text =
            resources.getString(R.string.memory_creation_selected_period)
                .format(startDate, endDate)
        setTextColor(resources.getColor(R.color.staccato_black, null))
    }
}

@BindingAdapter("isMemoryCandidatesEmpty")
fun TextView.setIsMemoryCandidatesEmptyVisibility(memoryCandidates: MemoryCandidates?) {
    isGone = !memoryCandidates?.memoryCandidate.isNullOrEmpty()
}

@BindingAdapter("isMemoryEmpty")
fun TextView.setIsMemoryEmptyVisibility(items: List<Int>?) {
    isGone = !items.isNullOrEmpty()
}

@BindingAdapter("visitedAtHistory")
fun TextView.formatVisitedAtHistory(visitedAt: LocalDateTime?) {
    text = visitedAt?.let {
        getFormattedLocalDateTime(it) + resources.getString(R.string.visit_at_history)
    } ?: ""
}

@BindingAdapter(
    value = ["startAt", "endAt"],
)
fun TextView.formatLocalDateToDatePeriod(
    startAt: LocalDate?,
    endAt: LocalDate?,
) {
    val periodFormatString = resources.getString(R.string.memory_period_dot)
    text =
        if (startAt != null && endAt != null) {
            visibility = View.VISIBLE
            format(
                periodFormatString,
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
    value = ["memoryStartAt", "memoryEndAt"],
)
fun TextView.formatLocalDateToDatePeriodInMemory(
    startAt: LocalDate?,
    endAt: LocalDate?,
) {
    val periodFormatString = resources.getString(R.string.memory_period_dot)
    text =
        if (startAt != null && endAt != null) {
            visibility = View.VISIBLE
            format(
                periodFormatString,
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
    isGone = currentPhotoNumbers < 2
}

@BindingAdapter("selectedAddress")
fun TextView.setSelectedAddress(address: String?) {
    text = address ?: context.getString(R.string.visit_creation_empty_address)
}
