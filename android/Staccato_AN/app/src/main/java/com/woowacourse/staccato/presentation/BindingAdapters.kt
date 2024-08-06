package com.woowacourse.staccato.presentation

import android.graphics.drawable.Drawable
import android.widget.Button
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.TextView
import androidx.core.view.isGone
import androidx.databinding.BindingAdapter
import coil.load
import coil.transform.RoundedCornersTransformation
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.woowacourse.staccato.R
import com.woowacourse.staccato.presentation.visitcreation.model.VisitTravelUiModel
import okhttp3.internal.format
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@BindingAdapter(
    value = ["coilImageUrl", "coilPlaceHolder"],
)
fun ImageView.loadImageWithCoil(
    url: String?,
    placeHolder: Drawable? = null,
) {
    load(url) {
        placeholder(placeHolder)
        error(placeHolder)
    }
}

@BindingAdapter(
    value = ["coilCircleImageUrl", "coilPlaceHolder"],
)
fun ImageView.setCircleImageWithCoil(
    url: String?,
    placeHolder: Drawable? = null,
) {
    load(url) {
        placeholder(placeHolder)
        transformations(RoundedCornersTransformation(1000f))
        error(placeHolder)
    }
}

@BindingAdapter(
    value = ["coilRoundedCornerImageUrl", "coilPlaceHolder", "coilRoundingRadius"],
)
fun ImageView.setRoundedCornerImageWithCoil(
    url: String?,
    placeHolder: Drawable? = null,
    roundingRadius: Float,
) {
    load(url) {
        placeholder(placeHolder)
        transformations(RoundedCornersTransformation(roundingRadius))
        error(placeHolder)
    }
}

@BindingAdapter(
    value = ["glideImageUrl", "glidePlaceHolder"],
)
fun ImageView.loadImageWithGlide(
    url: String?,
    placeHolder: Drawable? = null,
) {
    Glide.with(context)
        .load(url)
        .placeholder(placeHolder)
        .centerCrop()
        .error(placeHolder)
        .into(this)
}

@BindingAdapter(
    value = ["glideCircleImageUrl", "glidePlaceHolder"],
)
fun ImageView.setCircleImageWithGlide(
    url: String?,
    placeHolder: Drawable? = null,
) {
    Glide.with(context)
        .load(url)
        .placeholder(placeHolder)
        .circleCrop()
        .error(placeHolder)
        .into(this)
}

@BindingAdapter(
    value = ["glideRoundedCornerImageUrl", "glidePlaceHolder", "glideRoundingRadius"],
)
fun ImageView.setRoundedCornerImageWithGlide(
    url: String?,
    placeHolder: Drawable? = null,
    roundingRadius: Int,
) {
    Glide.with(context)
        .load(url)
        .placeholder(placeHolder)
        .centerCrop()
        .apply(RequestOptions.bitmapTransform(RoundedCorners(roundingRadius)))
        .error(placeHolder)
        .into(this)
}

@BindingAdapter(
    value = ["travelTitle", "startDate", "endDate"],
)
fun Button.setTravelSaveButtonActive(
    title: String?,
    startDate: LocalDate?,
    endDate: LocalDate?,
) {
    isEnabled =
        if (title.isNullOrEmpty() || startDate == null || endDate == null) {
            setTextColor(resources.getColor(R.color.gray4, null))
            false
        } else {
            setTextColor(resources.getColor(R.color.white, null))
            true
        }
}

@BindingAdapter("selectedTravel")
fun TextView.setSelectedTravel(selectedTravel: VisitTravelUiModel?) {
    if (selectedTravel == null) {
        text = resources.getString(R.string.visit_creation_travel_selection_hint)
        setTextColor(resources.getColor(R.color.gray3, null))
    } else {
        text = selectedTravel.title
        setTextColor(resources.getColor(R.color.staccato_black, null))
    }
}

@BindingAdapter("selectedVisitedAt")
fun TextView.setSelectedVisitedAt(selectedVisitedAt: LocalDate?) {
    if (selectedVisitedAt == null) {
        text = resources.getString(R.string.visit_creation_visited_at_hint)
        setTextColor(resources.getColor(R.color.gray3, null))
    } else {
        text = selectedVisitedAt.toString()
        setTextColor(resources.getColor(R.color.staccato_black, null))
    }
}

@BindingAdapter(
    value = ["selectedTravel", "visitedAt"],
)
fun Button.setVisitUpdateButtonActive(
    travel: VisitTravelUiModel?,
    visitedAt: LocalDate?,
) {
    isEnabled =
        if (travel == null || visitedAt == null) {
            setTextColor(resources.getColor(R.color.gray4, null))
            false
        } else {
            setTextColor(resources.getColor(R.color.white, null))
            true
        }
}

@BindingAdapter(
    value = ["startDate", "endDate"],
)
fun TextView.setTravelPeriod(
    startDate: LocalDate?,
    endDate: LocalDate?,
) {
    if (startDate == null || endDate == null) {
        text = resources.getString(R.string.travel_creation_period_hint)
    } else {
        text =
            resources.getString(R.string.travel_creation_period_description)
                .format(startDate, endDate)
        setTextColor(resources.getColor(R.color.staccato_black, null))
    }
}

@BindingAdapter("visitedAtConfirmButtonActive")
fun Button.setVisitedAtConfirmButtonActive(items: List<LocalDate>?) {
    isEnabled =
        if (items.isNullOrEmpty()) {
            setTextColor(resources.getColor(R.color.gray4, null))
            false
        } else {
            setTextColor(resources.getColor(R.color.white, null))
            true
        }
}

@BindingAdapter("visitedAtNumberPickerItems")
fun NumberPicker.setVisitedAtNumberPickerItems(items: List<LocalDate>?) {
    if (items.isNullOrEmpty()) {
        isGone = true
    } else {
        displayedValues = items.map { it.toString() }.toTypedArray()
    }
}

@BindingAdapter("visitedAtIsEmptyVisibility")
fun TextView.setVisitedAtIsEmptyVisibility(items: List<LocalDate>?) {
    isGone = !items.isNullOrEmpty()
}

@BindingAdapter(
    value = ["visitedAt", "visitedCount"],
)
fun TextView.combineVisitedAtWithVisitCount(
    visitedAt: LocalDate,
    visitedCount: Long,
) {
    val resultText =
        if (visitedCount == 1L) {
            format(
                resources.getString(R.string.visit_history_first_time),
                visitedAt.year,
                visitedAt.monthValue,
                visitedAt.dayOfMonth,
            )
        } else {
            format(
                resources.getString(R.string.visit_history_many_times),
                visitedAt.year,
                visitedAt.monthValue,
                visitedAt.dayOfMonth,
                visitedCount,
            )
        }
    text = resultText
}

@BindingAdapter(
    value = ["startAt", "endAt"],
)
fun TextView.convertLocalDateToDatePeriodString(
    startAt: LocalDate,
    endAt: LocalDate,
) {
    val fullFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
    val monthFormatter = DateTimeFormatter.ofPattern("MM.dd")
    val dayFormatter = DateTimeFormatter.ofPattern("dd")

    val datePeriod =
        if (startAt.year != endAt.year) {
            "${startAt.format(fullFormatter)} - ${endAt.format(fullFormatter)}"
        } else if (startAt.monthValue != endAt.monthValue) {
            "${startAt.format(fullFormatter)} - ${endAt.format(monthFormatter)}"
        } else if (startAt.dayOfMonth != endAt.dayOfMonth) {
            "${startAt.format(fullFormatter)} - ${endAt.format(dayFormatter)}"
        } else {
            startAt.format(fullFormatter)
        }
    text = datePeriod
}
