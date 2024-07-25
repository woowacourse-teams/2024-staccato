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
import com.woowacourse.staccato.R
import com.woowacourse.staccato.presentation.visitcreation.model.TravelUiModel

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
    value = ["glideImageUrl", "glidePlaceHolder"],
)
fun ImageView.loadImageWithGlide(
    url: String?,
    placeHolder: Drawable? = null,
) {
    Glide.with(context)
        .load(url)
        .placeholder(placeHolder)
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

@BindingAdapter("bindSetSelectedTravel")
fun TextView.setSelectedTravel(selectedTravel: TravelUiModel?) {
    if (selectedTravel == null) {
        text = resources.getString(R.string.visit_creation_travel_selection_hint)
        setTextColor(resources.getColor(R.color.gray3, null))
    } else {
        text = selectedTravel.travelTitle
        setTextColor(resources.getColor(R.color.staccato_black, null))
    }
}

@BindingAdapter("bindSetSelectedVisitedAt")
fun TextView.setSelectedVisitedAt(selectedVisitedAt: String?) {
    if (selectedVisitedAt == null) {
        text = resources.getString(R.string.visit_creation_visited_at_hint)
        setTextColor(resources.getColor(R.color.gray3, null))
    } else {
        text = selectedVisitedAt
        setTextColor(resources.getColor(R.color.staccato_black, null))
    }
}

@BindingAdapter(
    value = ["selectedTravel", "visitedAt"],
)
fun Button.setVisitUpdateButtonActive(
    travel: TravelUiModel?,
    visitedAt: String?,
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

@BindingAdapter("bindSetVisitedAtConfirmButtonActive")
fun Button.setVisitedAtConfirmButtonActive(items: List<String>?) {
    isEnabled =
        if (items.isNullOrEmpty()) {
            setTextColor(resources.getColor(R.color.gray4, null))
            false
        } else {
            setTextColor(resources.getColor(R.color.white, null))
            true
        }
}

@BindingAdapter("bindSetVisitedAtNumberPickerItems")
fun NumberPicker.setVisitedAtNumberPickerItems(items: List<String>?) {
    if (items.isNullOrEmpty()) {
        isGone = true
    } else {
        displayedValues = items.toTypedArray()
    }
}

@BindingAdapter("bindSetVisitedAtIsEmptyVisibility")
fun TextView.setVisitedAtIsEmptyVisibility(items: List<String>?) {
    isGone = !items.isNullOrEmpty()
}
