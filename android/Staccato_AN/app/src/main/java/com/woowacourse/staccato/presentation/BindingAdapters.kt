package com.woowacourse.staccato.presentation

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load
import coil.transform.RoundedCornersTransformation
import com.bumptech.glide.Glide

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
