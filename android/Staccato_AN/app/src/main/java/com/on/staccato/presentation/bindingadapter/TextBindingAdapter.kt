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

@BindingAdapter(value = ["setSelectedMemory", "setMemoryCandidates"])
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

@BindingAdapter(value = ["setDateTimeWithAmPm", "setMemoryCandidates"])
fun TextView.setDateTimeWithAmPm(
    setNowDateTime: LocalDateTime?,
    memoryCandidates: MemoryCandidates?,
) {
    if (memoryCandidates?.memoryCandidate?.isEmpty() == true) {
        text = resources.getString(R.string.visit_creation_memory_selection_hint)
        setTextColor(resources.getColor(R.color.gray3, null))
        isClickable = false
        isFocusable = false
    } else {
        text = setNowDateTime?.let(::getFormattedLocalDateTime) ?: ""
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

@BindingAdapter("memoryIsEmptyVisibility")
fun TextView.setMemoryIsEmptyVisibility(memoryCandidates: MemoryCandidates?) {
    isGone = !memoryCandidates?.memoryCandidate.isNullOrEmpty()
}

@BindingAdapter("visitedAtIsEmptyVisibility")
fun TextView.setVisitedAtIsEmptyVisibility(items: List<Int>?) {
    isGone = !items.isNullOrEmpty()
}

@BindingAdapter("visitedAt")
fun TextView.combineVisitedAt(visitedAt: LocalDateTime?) {
    text =
        if (visitedAt != null) {
            val hour = if (visitedAt.hour % 12 == 0) 12 else visitedAt.hour % 12
            val noonText = if (visitedAt.hour < 12) "오전" else "오후"
            format(
                resources.getString(R.string.visit_history),
                visitedAt.year,
                visitedAt.monthValue,
                visitedAt.dayOfMonth,
                noonText,
                hour,
            )
        } else {
            ""
        }
}

@BindingAdapter(
    value = ["startAt", "endAt"],
)
fun TextView.convertLocalDateToDatePeriodString(
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
fun TextView.convertLocalDateToDatePeriodStringInMemory(
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

@BindingAdapter(value = ["currentPhotoNumbers", "maxPhotoNumbers"])
fun TextView.setPhotoNumbers(
    currentPhotoNumbers: Int,
    maxPhotoNumbers: Int,
) {
    text =
        resources.getString(R.string.all_photo_number).format(currentPhotoNumbers, maxPhotoNumbers)
}

@BindingAdapter("photoDragHintVisibility")
fun TextView.setPhotoDragHintVisibility(currentPhotoNumbers: Int) {
    isGone = currentPhotoNumbers < 2
}

@BindingAdapter("setAddress")
fun TextView.setAddress(address: String?) {
    text = address ?: context.getString(R.string.visit_creation_empty_address)
}

@BindingAdapter("periodSelectionVisibility")
fun TextView.setPeriodSelectionVisibility(isChecked: Boolean) {
    isGone = !isChecked
}
