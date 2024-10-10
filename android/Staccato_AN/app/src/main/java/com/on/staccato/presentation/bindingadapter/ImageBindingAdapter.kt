package com.on.staccato.presentation.bindingadapter

import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load
import coil.transform.RoundedCornersTransformation

@BindingAdapter("selected")
fun ImageView.setSelected(selected: Boolean) {
    isSelected = selected
}

@BindingAdapter(
    value = ["coilImageUrl", "coilPlaceHolder"],
)
fun ImageView.loadCoilImage(
    url: String?,
    placeHolder: Drawable? = null,
) {
    load(url) {
        placeholder(placeHolder)
        error(placeHolder)
    }
}

@BindingAdapter(
    value = ["coilImageUri", "coilPlaceHolder"],
)
fun ImageView.loadCoilImageByUri(
    uri: Uri?,
    placeHolder: Drawable? = null,
) {
    load(uri) {
        placeholder(placeHolder)
        error(placeHolder)
    }
}

@BindingAdapter(
    value = ["coilCircleImageUrl", "coilPlaceHolder"],
)
fun ImageView.loadCoilCircleImage(
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
    value = ["coilRoundedCornerImageUrl", "coilRoundedCornerPlaceHolder", "coilRoundingRadius"],
)
fun ImageView.setCoilRoundedCornerImage(
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
    value = ["coilRoundedCornerImageUrl", "coilRoundedCornerImageUri", "coilRoundedCornerPlaceHolder", "coilRoundingRadius"],
)
fun ImageView.setCoilRoundedCornerImageWithUri(
    url: String?,
    uri: Uri?,
    placeHolder: Drawable? = null,
    roundingRadius: Float,
) {
    val image = uri ?: url
    load(image) {
        placeholder(placeHolder)
        transformations(RoundedCornersTransformation(roundingRadius))
        error(placeHolder)
    }
}

@BindingAdapter(
    value = [
        "colorImageResource",
        "grayImageResource",
    ],
)
fun ImageView.setImageResourceWithId(
    colorResId: Int,
    grayResId: Int,
) {
    setImageResource(
        if (isSelected) {
            colorResId
        } else {
            grayResId
        },
    )
}
